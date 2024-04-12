package nancal.iam.mvc

import com.nancal.cipher.SHA256Util
import nbcp.comm.ApiResult
import nbcp.comm.HasValue
import nbcp.comm.OpenAction
import nbcp.comm.Require
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.redis.rer
import nancal.iam.util.*
import nbcp.utils.Md5Util
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.Serializable
import java.time.LocalDateTime
import javax.servlet.http.HttpServletResponse
import kotlin.streams.toList

@OpenAction
@RestController
@RequestMapping("tenant/user")
class TenantUserOperationController {
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

        val redisUuid = rer.sys.smsCode(contact).get()
        if(redisUuid.equals("")){
            return ApiResult.error("验证已过期，请重新发送")
        }



        // 判断用户状态是否正常
        val tenantLoginUsers = mor.tenant.tenantLoginUser.query().apply {
            when (type) {
                SendPasswordType.Mobile -> {
                    this.where { it.mobile match redisUuid }
                }
                SendPasswordType.Email -> {
                    this.where { it.email match redisUuid }
                }
                else -> return ApiResult.error("枚举类型有误")
            }
        }.toList()

        if (tenantLoginUsers.isEmpty()) {
            return ApiResult.error("您输入的手机号或邮箱不存在")
        }
        // 获取用户去重后的 userIds
        val tenantLoginUserIds = tenantLoginUsers.stream().map { it.id }.toList().distinct()
        // 查找用户所有的租户ids
        val tenantIds = tenantLoginUsers.stream().map { it.tenant.id }.toList().distinct()

        // 校验密码格式是否正确
        var errorMsg = verificationPwd(tenantIds, newPwd)

        if (errorMsg.HasValue) {
            return ApiResult.error(errorMsg)
        }

        tenantLoginUserIds.forEach { loginId ->

            val toEntity = mor.tenant.tenantLoginUser.query()
                .where { it.id match loginId }
                .toEntity()
            if(toEntity !=null){
                mor.tenant.tenantLoginUser.update()
                    .apply {
                        this.where { it.id match loginId }
                        this.set { it.password to SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(newPwd) + toEntity.passwordSalt) }
                        this.set { it.lastUpdatePwdAt to LocalDateTime.now() }
                            .set { it.manualRemindPwdTimes to 0 }
                            .set { it.manualExpirePwdTimes to 0 }
                            .set { it.autoExpirePwdTimes to 0 }
                            .set { it.autoRemindPwdTimes to 0 }
                            .exec()
                    }
            }

        }


        rer.sys.smsCode(contact).deleteKey()
        return ApiResult()
    }

    fun verificationPwd(tenantIds: List<String>, newPwd: String): String {
        if (ValidateUtils.containerSpace(newPwd) || ValidateUtils.isContainChinese(newPwd)) {
            return "密码包含非法字符"
        }

        var tenantIdDesc = mor.tenant.tenant.query()
            .where { it.id match_in tenantIds }
            .orderByDesc { it.createAt }
            .toList()


        val sp = getTenantPwdPolicy(tenantIdDesc.map { it.id }).get(0).setting.selfSetting.securityPolicy

        if (!PwdVerifyStrategy.pwdVerification(newPwd ,sp.leastLenght, sp.lowInput,sp.upInput,sp.specialInput,sp.numberInput)) {
            return PwdVerifyStrategy.getPwdVerificationPrompt(newPwd ,sp.leastLenght, sp.lowInput,sp.upInput,sp.specialInput,sp.numberInput)

        }
        return ""
    }

    /**
     *  根据租户ID 获取租户设置的密码种类及长度
     *  0：密码种类
     *  1：密码长度
     */
    fun getTenantPwdPolicy(tenantIds: List<String>): MutableList<TenantSecretSet> {
        // 根据租户ids查找所有的租户系统设置
        var tenantSecretSets=mor.tenant.tenantSecretSet.query().apply {
            this.where { it.tenant.id match_in tenantIds }
        }.toList()

        if (tenantSecretSets.isEmpty()) {
            tenantSecretSets = mutableListOf(TenantSecretSet())
        }

        return tenantSecretSets

        // 查找租户里密码长度最短的、复杂度最低的
//        val minLength = tenantSecretSets.stream().map { it.setting.selfSetting?.securityPolicy ?: SecurityPolicy() }.toList()
//            .minByOrNull { it!!.leastLenght }!!.leastLenght
//        val minChar = tenantSecretSets.stream().map { it.setting.selfSetting?.securityPolicy ?: SecurityPolicy() }.toList()
//            .minByOrNull { it!!.leastCharacters }!!.leastCharacters

//        return arrayListOf( minLength)
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

}
