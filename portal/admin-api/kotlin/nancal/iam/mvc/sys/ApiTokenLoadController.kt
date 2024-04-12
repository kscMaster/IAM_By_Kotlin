package nancal.iam.mvc.sys

import nancal.iam.ApiTokenService
import nancal.iam.db.mongo.TenantAdminTypeEnum
import nancal.iam.db.mongo.mor
import nbcp.comm.JsonResult
import nbcp.comm.OpenAction
import nbcp.comm.Require
import nbcp.db.mongo.match
import nbcp.db.mongo.query
import nbcp.db.mongo.queryById
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@OpenAction
@RestController
@RequestMapping("/sys/api-token")
class ApiTokenLoadController {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    @Autowired
    lateinit var apiTokenService: ApiTokenService;

    /**
     *
     */
    @PostMapping("/load-app")
    fun loadApp(
        @Require appCode: String
    ): JsonResult {
        var app = mor.iam.sysApplication.queryByAppCode(appCode).toEntity();
        if (app == null) {
            return JsonResult.error("找不到appCode")
        }
        apiTokenService.loadAppToRedis(app)
        return JsonResult()
    }


    @PostMapping("/load-tenant")
    fun loadTenant(
        @Require tenantId: String
    ): JsonResult {
        var tenant = mor.tenant.tenant.queryById(tenantId).toEntity();
        if (tenant == null) {
            return JsonResult.error("找不到租户")
        }
        apiTokenService.loadTenantToRedis(tenant)
        return JsonResult()
    }
}
