package nancal.iam.mvc.tenant

import com.nancal.cipher.SHA256Util
import io.swagger.annotations.Api
import nbcp.comm.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.SecurityPolicy
import nancal.iam.db.mongo.entity.TenantSecretSet
import nancal.iam.db.redis.rer
import nancal.iam.util.PwdVerifyStrategy
import nbcp.utils.Md5Util
import nancal.iam.util.ValidateUtils
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.*
import javax.servlet.http.HttpServletResponse
import kotlin.streams.toList

/**
 * Created by CodeGenerator at 2021-11-17 15:48:36
 */
@Api(description = "租户用户安全相关", tags = arrayOf("TenantAdmin"))
@RestController
@RequestMapping("/tenant/tenant-user-security")
@OpenAction
class TenantAdminSecurityController {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    // 由于前端需要失败时有多种页面需要展示，而一旦抛异常就会统一被拦截，因此此处返回值临时加一种状态
    data class tempVO(
        var code: Int,
        var msg: String
    )

    /**
     * 找回密码
     */
    @PostMapping("/findPwd")
    fun findPwd(
        @Require type: SendPasswordType,
        @Require contact: String,
        @Require code: String,
        @Require newPwd: String,
        response: HttpServletResponse
    ): ApiResult<tempVO> {

        val redisUuid = rer.sys.smsCode(contact).get().toString()
        if(redisUuid.equals("")){
            return ApiResult.error("验证已过期，请重新发送")
        }

//        // 校验code是否正确
//        var errorMsg = verificationCode(type, code, redisUuid)
//
//        if (errorMsg.HasValue) {
//            return ApiResult.error(errorMsg)
//        }


        // 判断用户状态是否正常
        val tenantAdminUsers = mor.tenant.tenantLoginUser.query()
            .apply {
            when (type) {
                SendPasswordType.Mobile -> {
                    this.where { it.mobile match redisUuid }
                }
                SendPasswordType.Email -> {
                    this.where { it.email match redisUuid }
                }
                SendPasswordType.Defined -> {
                    // Do nothing
                }
                else -> return ApiResult.error("枚举类型有误")
            }
        }.orderByDesc { it.createAt }.toList()

        if (tenantAdminUsers.isEmpty()) {
            return ApiResult.error("您输入的手机号或邮箱不存在")
        }
        // 获取用户去重后的 userIds
        val tenantAdminUserIds = tenantAdminUsers.stream().map { it.id }.toList().distinct()
        // 查找用户所有的租户ids
        val tenantIds = tenantAdminUsers.stream().map { it.tenant.id }.toList().distinct()

        // 校验密码格式是否正确
        var errorMsg = verificationPwd(tenantIds, newPwd)

        if (errorMsg.HasValue) {
            return ApiResult.error(errorMsg)
        }

        tenantAdminUsers.forEach { user ->
            mor.tenant.tenantLoginUser.update()
                .where { it.id match user.id }
                .set { it.password to SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(newPwd) + user.passwordSalt) }
                .set { it.lastUpdatePwdAt to LocalDateTime.now() }
                .set { it.manualRemindPwdTimes to 0 }
                .set { it.manualExpirePwdTimes to 0 }
                .set { it.autoExpirePwdTimes to 0 }
                .set { it.autoRemindPwdTimes to 0 }
                .exec()
        }

        rer.sys.smsCode(contact).deleteKey()
        return ApiResult()
    }

    /**
     *  根据发送密码类型，校验验证码
     */
    @PostMapping("/verificationCode")
    fun verificationCode(
        @Require type: SendPasswordType,
        @Require contact: String,
        @Require code: String,
    ): ApiResult<String> {
        val vCode = when (type) {
            // 缺失邮件枚举类型，目前就用同一个也没问题
            SendPasswordType.Mobile -> rer.sys.smsCode("${MobileCodeModuleEnum.ForgetPassword.name}-$contact").get()
            SendPasswordType.Email -> rer.sys.smsCode("${MobileCodeModuleEnum.ForgetPassword.name}-$contact").get()
            SendPasswordType.Defined -> rer.sys.smsCode("${MobileCodeModuleEnum.ForgetPassword.name}-$contact").get()
        }

        if (!vCode.HasValue) {
            return ApiResult.error("验证码已过期")
        }else if (vCode != code) {
            return ApiResult.error(  "验证码错误")
        }else{
            return ApiResult()
        }
    }

    private fun verificationPwd(tenantIds: List<String>, newPwd: String): String {
        if (ValidateUtils.containerSpace(newPwd) || ValidateUtils.isContainChinese(newPwd)) {
            return "密码包含非法字符"
        }

        // 根据租户ids查找所有的租户系统设置
        var tenantSecretSets=mor.tenant.tenantSecretSet.query().apply {
            this.where { it.tenant.id match_in tenantIds }
        }.toList()

        if (tenantSecretSets.isEmpty()) {
            tenantSecretSets = mutableListOf(TenantSecretSet())
        }

        var leastLenght = 10000
        var lowInput = false
        var upInput = false
        var specialInput = false
        var numberInput = false

        leastLenght = tenantSecretSets.get(0).setting.selfSetting.securityPolicy.leastLenght
        lowInput = tenantSecretSets.get(0).setting.selfSetting.securityPolicy.lowInput
        upInput = tenantSecretSets.get(0).setting.selfSetting.securityPolicy.upInput
        specialInput = tenantSecretSets.get(0).setting.selfSetting.securityPolicy.specialInput
        numberInput = tenantSecretSets.get(0).setting.selfSetting.securityPolicy.numberInput

//        // 查找租户里密码长度最短的、复杂度最低的
//        val minLength = tenantSecretSets.stream().map { it.setting.selfSetting?.securityPolicy ?: SecurityPolicy() }.toList()
//            .minByOrNull { it!!.leastLenght }!!.leastLenght
//        val minChar = tenantSecretSets.stream().map { it.setting.selfSetting?.securityPolicy ?: SecurityPolicy() }.toList()
//            .minByOrNull { it!!.leastCharacters }!!.leastCharacters

        if (!PwdVerifyStrategy.pwdVerification(newPwd ,leastLenght, lowInput,upInput,specialInput,numberInput)) {
            return PwdVerifyStrategy.getPwdVerificationPrompt(newPwd ,leastLenght, lowInput,upInput,specialInput,numberInput)
        }

        return ""
    }


}

