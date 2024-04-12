package nancal.iam

import com.nancal.cipher.ApiTokenUtil
import nancal.iam.db.mongo.TenantAdminTypeEnum
import nancal.iam.db.mongo.UserSystemTypeEnum
import nbcp.comm.HasValue
import nbcp.comm.config
import nbcp.db.LoginUserModel
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.mongo.mor
import nancal.iam.db.redis.OAuthFreshTokenData
import nbcp.db.redis.proxy.RedisStringProxy
import nancal.iam.db.redis.rer
import nbcp.db.mongo.match
import nbcp.db.mongo.query
import nbcp.db.mongo.queryById
import nbcp.base.mvc.*
import nbcp.web.*
import nbcp.base.mvc.*
import nbcp.base.mvc.service.IUserAuthenticationService
import nbcp.comm.AsLong
import nbcp.db.db
import nbcp.web.tokenValue
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.Exception
import javax.servlet.http.HttpServletRequest


@Component
class UserAuthenticationService : IUserAuthenticationService {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    private val userSystemRedis
        get() = RedisStringProxy(config.tokenKey, config.tokenCacheSeconds)


    private fun validateCodeRedis(token:String) =
        RedisStringProxy("validateCode:${token}", config.validateCodeCacheSeconds)

    override fun deleteToken(request: HttpServletRequest) {
        var token = request.tokenValue;
        if (token.isEmpty()) return;
        rer.sys.oauthToken(token).deleteKey ( );
    }

    override fun getLoginInfoFromToken(request: HttpServletRequest, renewal: Boolean): LoginUserModel? {
        loadFromApiToken(request).apply {
            if (this != null) {
                return this;
            }
        }

        var token = request.tokenValue
        if (token.isEmpty()) return null;

        rer.sys.oauthToken(token).get().apply {
            if (this != null && renewal) {

                rer.sys.oauthToken(token).renewalKey();
            }

            return this;
        }
    }

    fun loadFromApiToken(request: HttpServletRequest): LoginUserModel? {
        var tokenValue = request.findParameterStringValue("api-token")

        if (tokenValue.isEmpty()) return null;

        if (tokenValue.endsWith(":" + ApiTokenService.adminUser.id)) {
            logger.info("[UserAuthenticationService]--loadFromApiToken--tokenValue endWiths")
            return rer.sys.apiToken(tokenValue.split(":").first()).get()
        }


        var appCode = "";
        try {
            // tokenValue = 安全加密串.租户Id!应用编码
            appCode = tokenValue.split(".").get(1).split("!").get(1);
        } catch (e: Exception) {
            return null;
        }
        logger.info("[UserAuthenticationService]--get appCode is $appCode")
        if (appCode.isEmpty()) return null
        var secretKey = rer.sys.apiToken(appCode).get()?.ext
        if (secretKey.isNullOrEmpty()) {
            var app = mor.iam.sysApplication.queryByAppCode(appCode).toEntity();
            if (app != null) {
                loadAppToRedis(app);
            }
        }
        if (secretKey.isNullOrEmpty()) return null;
        var real_tenantId_appCode = ApiTokenUtil.getTenantIdFromApiToken(tokenValue, secretKey)
        if (real_tenantId_appCode.isEmpty()) return null;

        var real_tenantId = real_tenantId_appCode.split("!").get(0)
        logger.info("[UserAuthenticationService]--real tenantId is $real_tenantId")
        var ret = rer.sys.apiToken(real_tenantId).get();

        if (ret == null) {
            var tenant = mor.tenant.tenant.queryById(real_tenantId).toEntity();
            if (tenant != null) {
                var tenantAdmin = mor.tenant.tenantUser.query()
                    .where { it.tenant.id match tenant.id }
                    .where { it.adminType match TenantAdminTypeEnum.Super }
                    .where { it.enabled match true }
                    .toEntity()

                if (tenantAdmin != null) {
                    apiTokenService.loadTenantToRedis(tenant, tenantAdmin)
                }
            }
        }

        return ret;
    }

    @Autowired
    lateinit var apiTokenService: ApiTokenService;

    fun loadAppToRedis(app: SysApplication) {
        var loginUser = LoginUserModel(
            ApiTokenService.adminUser.id,
            UserSystemTypeEnum.Boss.toString(),
            ApiTokenService.adminUser.id,
            "loginName",
            ApiTokenService.adminUser.loginName,
            true,
            ApiTokenService.adminUser.name
        ).also {
            it.ext = app.privateKey
        }

        rer.sys.apiToken(app.appCode).set(loginUser);
    }


    override fun getValidateCode(token: String): String {
        return validateCodeRedis(token).get();
    }

    override fun saveLoginUserInfo(request: HttpServletRequest, userInfo: LoginUserModel): Int {
        if (userInfo.token.isEmpty()) return 0;
        val tenantId = userInfo.organization.id;
        val sessionTimeoutSeconds = mor.tenant.tenantSecretSet.queryByTenantId(tenantId)
            .toEntity()?.setting?.sessionTimeoutSeconds ?: 1800

        rer.sys.oauthToken(userInfo.token).set(userInfo, sessionTimeoutSeconds);
        if (userInfo.freshToken.HasValue) {
            rer.sys.freshToken(userInfo.freshToken).set(OAuthFreshTokenData(userInfo.token, (sessionTimeoutSeconds * 2).AsLong(0)), sessionTimeoutSeconds * 2)
        }

        return sessionTimeoutSeconds
    }

    override fun setValidateCode(token: String, value: String) {
        validateCodeRedis(token).set(value);
    }
}