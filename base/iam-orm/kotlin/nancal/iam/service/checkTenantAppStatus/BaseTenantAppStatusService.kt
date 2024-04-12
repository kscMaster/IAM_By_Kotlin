//package nancal.iam.service.checkTenantAppStatus
//
//import nbcp.base.mvc.*
//import nbcp.web.*
//import nbcp.db.cache.FromRedisCache
//import nancal.iam.db.mongo.entity.TenantApplication
//import nbcp.db.mongo.match
//import nancal.iam.db.mongo.mor
//import nbcp.db.mongo.query
//
//abstract class BaseTenantAppStatusService {
//
//    @FromRedisCache(tableClass = TenantApplication::class, groupKey = "tenant.id", groupValue = "#tenantId")
//    fun getAvailableAppCodes(tenantId: String): List<String> {
//        if (tenantId.isEmpty()) return listOf<String>()
//
//        var availableAppCodes = mor.tenant.tenantApplication.query()
//            .where { it.tenant.id match tenantId }
//            .where { it.enabled match true }
//            .select { it.appCode }
//            .toList(String::class.java)
//
//        return availableAppCodes;
//    }
//
//}