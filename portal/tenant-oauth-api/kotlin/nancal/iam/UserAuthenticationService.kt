package nancal.iam

import nancal.iam.db.mongo.mor
import nancal.iam.db.redis.OAuthFreshTokenData
import nancal.iam.db.redis.rer
import nbcp.base.mvc.*
import nbcp.base.mvc.service.IUserAuthenticationService
import nbcp.web.*
import nbcp.comm.AsLong
import nbcp.comm.HasValue
import nbcp.comm.config
import nbcp.db.LoginUserModel
import nbcp.db.redis.proxy.RedisStringProxy
import nbcp.web.tokenValue
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class UserAuthenticationService : IUserAuthenticationService {

    override fun deleteToken(request: HttpServletRequest) {
        var token = request.tokenValue
        if (token.isEmpty()) return
        rer.sys.oauthToken(token).deleteKey();
    }

    override fun getLoginInfoFromToken(request: HttpServletRequest, renewal: Boolean): LoginUserModel? {
        var token = request.tokenValue
        if (token.isEmpty()) return null;
        rer.sys.oauthToken(token).get().apply {
            if (this != null && renewal) {

                rer.sys.oauthToken(token).renewalKey();
            }

            return this;
        }
    }

    private fun validateCodeRedis(token:String) =
        RedisStringProxy("validateCode:${token}", config.validateCodeCacheSeconds)

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