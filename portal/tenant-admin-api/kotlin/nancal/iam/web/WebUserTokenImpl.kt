//package nancal.iam.web
//
//import nbcp.comm.HasValue
//import nbcp.db.*
//import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
//import AdminUser
//import nbcp.base.service.UserAuthenticationService
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.stereotype.Component
//
//
//@Component
//class WebUserTokenImpl : WebUserTokenBean {
//    @Autowired
//    lateinit var userSystem: userAuthenticationService
//
//    @Value("\${token:}")
//    var adminTokenValue: String = "";
//
//    private val adminUserCacheData by lazy {
//        return@lazy mor.admin.adminUser.queryByLoginName("admin").toEntity()
//    }
//
//
//    override fun changeToken(token: String, newToken: String) {
//        var userInfo = userSystem.getLoginInfoFromToken(token);
//
////        mor.admin.adminLoginUser.update()
////            .where { it.token match token }
////            .set { it.token to newToken }
////            .exec()
//
//        if (userInfo != null && newToken.HasValue) {
//            userInfo.token = newToken;
//            userSystem.saveLoginUserInfo(userInfo);
//        }
//        userSystem.deleteToken(token);
//    }
//
//
//    override fun getUserInfo(token: String): LoginUserModel? {
//        if (token.isEmpty()) return LoginUserModel()
//        var adminUser: AdminUser? = null;
//        if (adminTokenValue == token) {
//            adminUser = adminUserCacheData
//        }
//
//        if (adminUser != null) {
//            return LoginUserModel(
//                adminUser.id,
//                adminUser.loginName,
//                adminUser.name,
//                token,
//                true,
//                IdName(),
//                adminUser.roles.map I{ it.id });
//        }
//
//        var ret = userSystem.getLoginInfoFromToken(token);
//        if (ret != null) {
//            return ret;
//        }
//
//        return return LoginUserModel()
//
////        var adminLoginUser = mor.admin.adminLoginUser.query()
////                .where { it.token match token }
////                .toEntity()
////
////        if (adminLoginUser == null) {
////            return null;
////        }
////
////        adminUser = mor.admin.adminUser.queryById(adminLoginUser.userId).toEntity()
////
////        if (adminUser == null) {
////            return null;
////        }
////
////        var model = LoginUserModel(adminUser.id, adminUser.loginName, adminUser.name, adminLoginUser.token,IdName(),adminUser.roles.map { it.id });
////        userSystem.saveLoginUserInfo(model);
////        return model;
//    }
//}