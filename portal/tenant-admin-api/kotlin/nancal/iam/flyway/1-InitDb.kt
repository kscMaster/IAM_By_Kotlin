//package nancal.iam.flyway
//
//import nbcp.db.FlywayVersionBaseService
//import nbcp.db.IdName
//import nancal.iam.db.mongo.entity.*
//import nbcp.db.mongo.match
//import mor
//import nbcp.db.mongo.query
//import nbcp.utils.Md5Util
//import org.springframework.stereotype.Component
//
//
//@Component
//class `1-InitDb` : FlywayVersionBaseService(1) {
//    override fun initData() {
//        var admin = AdminUser();
//        if (mor.admin.adminUser.query()
//                .where { it.loginName match "admin" }
//                .exists() == false
//        ) {
//            admin.loginName = "admin";
//            admin.name = "管理员"
//            admin.isAdmin = true;
//
//            mor.admin.adminUser.doInsert(admin);
//        }
//
//        if (mor.admin.adminLoginUser.query()
//                .where { it.loginName match "admin" }
//                .exists() == false
//        ) {
//            var admin_login = AdminLoginUser();
//            admin_login.userId = admin.id;
//            admin_login.loginName = "admin"
//            admin_login.password = Md5Util.getBase64Md5("1234");
//
//            mor.admin.adminLoginUser.doInsert(admin_login);
//        }
//
//        var corp = Corporation();
//        if (mor.corp.corporation.query()
//                .where { it.name match "admin" }
//                .exists() == false
//        ) {
//            corp.name = "admin"
//
//            mor.corp.corporation.doInsert(corp);
//        }
//
//        var user = CorpUser();
//        if (mor.corp.corpUser.query()
//                .where { it.loginName match "admin" }
//                .exists() == false
//        ) {
//            user.loginName = "admin"
//            user.isAdmin = true
//            user.corp = IdName(corp.id, corp.name)
//
//            mor.corp.corpUser.doInsert(user);
//        }
//
//
//        if (mor.corp.corpLoginUser.query()
//                .where { it.loginName match "admin" }
//                .exists() == false
//        ) {
//            var loginUser = CorpLoginUser();
//            loginUser.userId = user.id
//            loginUser.loginName = "admin"
//            loginUser.password = Md5Util.getBase64Md5("1234")
//
//            mor.corp.corpLoginUser.doInsert(loginUser);
//        }
//    }
//}