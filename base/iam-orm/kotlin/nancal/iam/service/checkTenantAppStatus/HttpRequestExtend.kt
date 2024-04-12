//package nancal.iam.service.checkTenantAppStatus
//
//import nancal.iam.db.mongo.mor
//import nancal.iam.db.redis.rer
//import nbcp.base.mvc.*
//import nbcp.comm.HasValue
//import nbcp.db.mongo.match
//import nbcp.db.mongo.query
//import javax.servlet.http.HttpServletRequest
//
//val HttpServletRequest.availableAppCodes: List<String>
//    get() {
//        if (this.getAttribute("[AvailableAppCodes]") != null) {
//            return this.getAttribute("[AvailableAppCodes]") as List<String>
//        }
//
//        val request = HttpContext.request
//        var tenantId = request.findParameterStringValue("tenant-id")
//        if (tenantId.isEmpty()) {
//            val apiToken = request.getHeader("api-token")
//            if (apiToken.HasValue) {
//                tenantId = apiToken.split(".")[1].split("!")[0]
//            }
//            if (tenantId.isEmpty()) {
//                val token = request.getHeader("token")
//                if (token.HasValue) {
//                    val loginUserData = rer.sys.oauthToken(token).get()
//                    if (loginUserData != null) {
//                        tenantId = loginUserData.organization.id
//                    }
//                }
//            }
//        }
//
//        if (tenantId.isEmpty()) {
//            return listOf<String>()
//        }
//
//        val availableAppCodes = mor.tenant.tenantApplication.query()
//            .where { it.tenant.id match tenantId }
//            .where { it.enabled match true }
//            .select { it.appCode }
//            .toList(String::class.java)
//
//        this.setAttribute("[AvailableAppCodes]", availableAppCodes)
//        return availableAppCodes
//    }