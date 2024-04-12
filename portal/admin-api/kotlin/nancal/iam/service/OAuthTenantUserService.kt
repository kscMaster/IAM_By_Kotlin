//package nancal.iam.service
//
//import nbcp.comm.ApiResult
//import UserTypeEnum
//import SysApplication
//import nbcp.db.mongo.match
//import mor
//import nbcp.db.mongo.query
//import rer
//import nbcp.utils.CodeUtil
//import nbcp.utils.Md5Util
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Service
//
//@Service
//class OAuthTenantUserService {
//    companion object {
//        private val admin_tenant by lazy {
//            return@lazy mor.tenant.tenant.query().where { it.isAdmin match true }.toEntity()!!
//        }
//    }
//
//    @Autowired
//    lateinit var tenantUserService: TenantUserService;
//
//    fun loginOpen(loginName: String, password: String, appInfo: SysApplication): ApiResult<String> {
//        return loginTenant(loginName, password, appInfo)
//    }
//
//    fun loginTenant(loginName: String, password: String, appInfo: SysApplication): ApiResult<String> {
//        var loginUser = mor.tenant.tenantLoginUser.queryByLoginName(loginName).toEntity()
//        if (loginUser == null) {
//            return ApiResult.error("找不到用户")
//        }
//
//
//        var myApps = tenantUserService.getMyApps(loginUser.userId);
//        if (myApps.data.map { it.id }.contains(appInfo.id) == false) {
//            return ApiResult.error("您没有登录该系统的权限")
//        }
//
//        if (Md5Util.getBase64Md5(password) != loginUser.password) {
//            return ApiResult.error("密码不正确")
//        }
//
//        var code = CodeUtil.getCode();
//        var token = CodeUtil.getCode();
//
//        rer.sys.oauthCode.set(code, UserTypeEnum.TenantAdmin.toString() + "-" + token + "-" + loginUser.userId)
//
//        return ApiResult.of(code)
//    }
//
//
//}