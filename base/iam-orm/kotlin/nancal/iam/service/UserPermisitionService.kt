//package nancal.iam.service
//
//import nbcp.db.cache.*
//import nancal.iam.db.mongo.mor
//import nbcp.db.mongo.query
//import nbcp.db.redis.proxy.RedisListProxy
//import org.springframework.stereotype.Service
//import org.springframework.util.AntPathMatcher
//
//@Service
//class UserPermisitionService {
//    private var userDenyApi = RedisListProxy("user-deny-api")
//
//    //判断用户是否可以访问Api
//
//    fun allowAdminUserApi(token: String, roles: List<String>, requestURI: String): Boolean {
//        var denyApis = userDenyApi.getListString(token)
//
//        if (denyApis == null) {
//            var urls = mutableSetOf<String>();
//
//
//            mor.admin.adminRole.query().where { it.id match_in roles }
//                .select { it.permissionApis }
//                .toList()
//                .forEach {
//                    urls.addAll(it.permissionApis.map { it.path })
//                }
//
//
//            var all = mor.admin.adminPermissionApi.query()
//                .select { it.url }
//                .toList()
//                .map { it.url }
//
//            denyApis = all.minus(urls);
//
//            userDenyApi.push(token, *denyApis.toTypedArray())
//        }
//
//        var ant = AntPathMatcher();
//        if (denyApis.any { ant.match(it, requestURI) }) {
//            return false;
//        }
//
//        return true;
//    }
//
//}