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
        //老的方式
        var loginUser = LoginUserModel(
            adminUser.id,
            UserSystemTypeEnum.Boss.toString(),
            adminUser.id,
            "loginName",
            adminUser.loginName,
            true,
            adminUser.name
        )
        rer.sys.apiToken(adminUser.id).set(loginUser)

        loadAppApiToken()

        loadTenantApiToken()
    }

    private fun loadAppApiToken() {
        BatchReader.init({ skip, take ->
            return@init mor.iam.sysApplication.query()
                .orderByAsc { it.id }
                .limit(skip, take)
                .toList()
        })
            .forEach { app ->
                loadAppToRedis(app);
            }

    }

    fun loadAppToRedis(app: SysApplication) {
        if (app.privateKey.isNullOrEmpty()) {
            throw RuntimeException("app ${app.name} 私钥为空！")
        }
        var loginUser = LoginUserModel(
            adminUser.id,
            UserSystemTypeEnum.Boss.toString(),
            adminUser.id,
            "loginName",
            adminUser.loginName,
            true,
            adminUser.name
        ).also {
            it.ext = app.privateKey
        }

        rer.sys.apiToken(app.appCode).set(loginUser);
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

                loadTenantToRedis(tenant);

                count++;
            }

        if (count > 0) {
            logger.Important("apiToken 同步了 ${count} 条数据")
        }
    }

    fun loadTenantToRedis(tenant: Tenant) {

        var tenantAdminUser = mor.tenant.tenantUser.query()
            .where { it.tenant.id match tenant.id }
            .where { it.adminType match TenantAdminTypeEnum.Super }
            .orderByAsc { it.id }
            .toEntity()

        if (tenantAdminUser == null) {
            return
        }

        var privateKey = mor.tenant.tenantSecretSet.queryByTenantId(tenant.id).toEntity()?.sysPrivateSecret.AsString();
        if (privateKey.isEmpty()) {
//            throw RuntimeException("租户 ${tenant.name} 私钥为空！")
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