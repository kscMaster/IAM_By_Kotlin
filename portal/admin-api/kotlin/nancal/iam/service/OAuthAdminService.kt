//package nancal.iam.service
//
//import nbcp.comm.ApiResult
//import UserTypeEnum
//import nbcp.db.mongo.match
//import mor
//import nbcp.db.mongo.query
//import rer
//import nbcp.utils.CodeUtil
//import nbcp.utils.Md5Util
//import org.springframework.stereotype.Service
//
//@Service
//class OAuthTenantAdminUserService {
//    companion object {
//        private val admin_tenant by lazy {
//            return@lazy mor.tenant.tenant.query().where { it.isAdmin match true }.toEntity()!!
//        }
//    }
//
//
//    fun loginBoss(loginName: String, password: String): ApiResult<String> {
//        var loginUser = mor.tenant.tenantLoginUser.queryByLoginName(loginName).toEntity()
//        if (loginUser == null) {
//            return ApiResult.error("找不到用户")
//        }
//
//        if (Md5Util.getBase64Md5(password) != loginUser.password) {
//            return ApiResult.error("密码不正确")
//        }
//
//        var code = CodeUtil.getCode();
//        var token = CodeUtil.getCode();
//
//        rer.sys.oauthCode.set(code, UserTypeEnum.Boss.toString() + "-" + token + "-" + loginUser.userId)
//
//        return ApiResult.of(code)
//    }
//}