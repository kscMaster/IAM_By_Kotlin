package nancal.iam.service

import com.nancal.cipher.SHA256Util
import nbcp.comm.ApiResult
import nancal.iam.db.mongo.*
import nbcp.db.mongo.match
import nancal.iam.db.mongo.mor
import nancal.iam.db.redis.OAuthCodeData
import nbcp.db.mongo.query
import nancal.iam.db.redis.rer
import nbcp.utils.CodeUtil
import nbcp.utils.Md5Util
import org.springframework.stereotype.Service

@Service
class OAuthTenantAdminUserService {
    companion object {
        private val admin_tenant by lazy {
            return@lazy mor.tenant.tenant.query().where { it.code match "open" }.toEntity()!!
        }
    }


    fun loginBoss(loginName: String, password: String): ApiResult<String> {
        var loginUser = mor.admin.adminLoginUser.queryByLoginName(loginName).toEntity()
        if (loginUser == null) {
            return ApiResult.error("找不到用户")
        }

        if (SHA256Util.getSHA256StrJava(password+loginUser.passwordSalt) != loginUser.password) {
            return ApiResult.error("密码不正确")
        }

        var code = CodeUtil.getCode();
        var token = CodeUtil.getCode();

        rer.sys.oauthCode(code).set(OAuthCodeData(UserSystemTypeEnum.Boss, "loginName", loginName, token, loginUser.userId))

        return ApiResult.of(code)
    }
}