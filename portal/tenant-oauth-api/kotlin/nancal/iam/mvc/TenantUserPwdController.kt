package nancal.iam.mvc

import com.nancal.cipher.SHA256Util
import nancal.iam.db.mongo.entity.SecurityPolicy
import nbcp.comm.*
import nbcp.db.mongo.match
import nancal.iam.db.mongo.mor
import nbcp.db.mongo.query
import nbcp.db.mongo.update
import nancal.iam.util.*
import nbcp.utils.Md5Util
import nbcp.web.LoginUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 密码相关
 */
@OpenAction
@RestController
class TenantUserPwdController {

    @Autowired
    private lateinit var tenantUserOpController: TenantUserOperationController


    // 根据旧密码修改新密码
    @PostMapping("/tenant/user/reset")
    fun resetByOldPwd(
        @Require userId: String,
        @Require oldPassword: String,
        @Require newPassword: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): JsonResult {
        if(oldPassword == newPassword){
            return JsonResult.error("新密码不能与旧密码一致")
        }
        val user = mor.tenant.tenantLoginUser.query().where { it.id match userId }.toEntity() ?: return JsonResult.error("用户未找到")

        //鉴权
        mor.tenant.tenantLoginUser.query()
            .where { it.userId match userId }
            .where { it.password match SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(oldPassword) + user.passwordSalt) }
            .toEntity()
            .apply {
                if (this == null) {
                    return JsonResult.error("原密码不正确")
                }
            }
        // 校验密码格式是否正确
        val errorMsg = tenantUserOpController.verificationPwd(listOf(user.tenant.id), newPassword)

        if (errorMsg.HasValue) {
            return JsonResult.error(errorMsg)
        }
        // 判断租户是否正常
        val tenant = mor.tenant.tenant.query()
            .where { it.id match user.tenant.id }
            .toEntity().must().elseThrow { "找不到该用户" }
            .apply {
                if (this.isLocked) {
                    return JsonResult.error("您的租户已被冻结")
                }
            }

        mor.tenant.tenantLoginUser.update()
            .where { it.userId match userId }
            .set { it.password to SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(newPassword) + user.passwordSalt) }
            .set { it.lastUpdatePwdAt to LocalDateTime.now() }
            .set { it.manualRemindPwdTimes to 0 }
            .set { it.manualExpirePwdTimes to 0 }
            .set { it.autoExpirePwdTimes to 0 }
            .set { it.autoRemindPwdTimes to 0 }
            .exec()
            .apply {
                if (this < 0) {
                    return JsonResult.error("修改失败")
                }


            }
        return JsonResult()
    }


    /**
     *  获取租户密码策略
     */
    @PostMapping("/pwd/getPwdPolicy")
    fun getPwdPolicy(
        request: HttpServletRequest,
        @Require  tenantId : String
    ): ApiResult<SecurityPolicy> {
//        val tenantId = request.LoginUser.organization.id ?: ApiResult.error<String>("未找到改租户",500)

        mor.tenant.tenant.query().where { it.id match tenantId }.toEntity().must().elseThrow { "找不到租户" }

//        val pwdPolicy = tenantUserOpController.getTenantPwdPolicy(listOf(tenantId) as List<String>)

//        return ApiResult.of(PwdVerifyStrategy.getPwdVerificationPrompt(pwdPolicy[0], pwdPolicy[1]))


        return ApiResult.of(tenantUserOpController.getTenantPwdPolicy(listOf(tenantId) as List<String>).get(0).setting.selfSetting.securityPolicy)

    }

}