package nancal.iam

import nbcp.comm.AsString
import nbcp.comm.BatchReader
import nbcp.comm.Important
import nbcp.db.LoginUserModel
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.redis.rer
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDateTime


@Configuration
class ApiTokenService {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)

        val adminUser: AdminUser by lazy {
            return@lazy mor.admin.adminUser.query()
                .where { it.isAdmin match true }
                .orderByAsc { it.id }
                .toEntity()!!
        }
    }

    @EventListener
    fun started(event: ApplicationReadyEvent) {
        loadTenantApiToken()
    }

    private fun loadTenantApiToken() {
        var now = LocalDateTime.now();

        var count = 0;
        BatchReader.init(50, { skip, take ->
            return@init mor.tenant.tenant.query()
                .orderByAsc { it.id }
                .limit(skip, take)
                .toList()
        })
            .forEach { tenant ->

                var user = mor.tenant.tenantUser.query()
                    .where { it.tenant.id match tenant.id }
                    .where { it.adminType match_not_equal TenantAdminTypeEnum.None }
                    .orderByAsc { it.id }
                    .toEntity()

                if (user == null) {
                    return@forEach
                }

                loadTenantToRedis(tenant, user);

                count++;
            }

        if (count > 0) {
            logger.Important("apiToken 同步了 ${count} 条数据")
        }

    }

    fun loadTenantToRedis(tenant: Tenant, tenantAdminUser: TenantUser) {
        var privateKey = mor.tenant.tenantSecretSet.queryByTenantId(tenant.id).toEntity()?.sysPrivateSecret.AsString();
        if (privateKey.isEmpty()) {
            return;
        }

        var token = tenant.id
        var loginUser = LoginUserModel(
            token,
            UserSystemTypeEnum.TenantAdmin.toString(),
            tenantAdminUser.id,
            "loginName",
            tenantAdminUser.loginName,
            true,
            tenantAdminUser.name,
            tenantAdminUser.tenant
        ).also {
            it.ext = privateKey
        }

        rer.sys.apiToken(tenant.id).set(loginUser);
    }
}