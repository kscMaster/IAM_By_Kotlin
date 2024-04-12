package nancal.iam

import com.nancal.cipher.ApiTokenUtil
import nbcp.comm.HasValue
import nbcp.comm.config
import nbcp.db.LoginUserModel
import nancal.iam.db.mongo.mor
import nbcp.db.redis.proxy.RedisStringProxy
import nancal.iam.db.redis.rer
import nbcp.base.mvc.*
import nbcp.web.*
import nbcp.base.mvc.*
import nbcp.base.mvc.service.IUserAuthenticationService
import nbcp.web.tokenValue
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest


@Component
class UserAuthenticationService : IUserAuthenticationService {

    private val userSystemRedis
        get() = RedisStringProxy(config.tokenKey, config.tokenCacheSeconds)


    private fun validateCodeRedis(token:String) =
        RedisStringProxy("validateCode:${token}", config.validateCodeCacheSeconds)

    override fun deleteToken(request: HttpServletRequest) {
        var token = request.tokenValue;
        if (token.isEmpty()) return;
        rer.sys.oauthToken(token).deleteKey();
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

    private fun loadFromApiToken(request: HttpServletRequest): LoginUserModel? {
        var tokenValue = request.findParameterStringValue("api-token");
        if (tokenValue.isEmpty()) return null;

        if (tokenValue == ApiTokenService.adminUser.id) {
            return rer.sys.apiToken(tokenValue).get()
        }

        var appCode = tokenValue.split(".").get(1);
        if (appCode.isEmpty()) return null;

        var loginUser = rer.sys.apiToken(appCode).get();
        if (loginUser == null) return null;

        var secretKey = rer.sys.apiToken(appCode).get()?.ext;
        if (secretKey.isNullOrEmpty()) return null;

        var real_appCode = ApiTokenUtil.getTenantIdFromApiToken(tokenValue, secretKey)
        if (real_appCode != appCode) {
            throw RuntimeException("api-token非法!")
        }

        return loginUser
    }

    override fun getValidateCode(token: String): String {
        return validateCodeRedis(token).get();
    }

    override fun saveLoginUserInfo(request: HttpServletRequest, userInfo: LoginUserModel) :Int{
        if (userInfo.token.isEmpty()) return 0;
        rer.sys.oauthToken(userInfo.token).set(userInfo);
        return rer.sys.oauthToken(userInfo.token).defaultCacheSeconds;
    }

    override fun setValidateCode(token: String, value: String) {
        validateCodeRedis(token).set(value);
    }
}