package nancal.iam.db.mongo.extend

import com.nancal.cipher.SHA256Util
import nbcp.comm.ApiResult
import nbcp.comm.AsString
import nbcp.comm.minus
import nbcp.db.LoginUserModel
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.table.AdminGroup
import nancal.iam.db.redis.rer
import nbcp.utils.Md5Util
import nbcp.utils.TokenUtil
import java.time.LocalDateTime


/**
 * 登录验证，返回错误消息
 */
fun AdminGroup.AdminUserEntity.doLogin(
    loginName: String,
    password: String,
    requestToken: String,
): ApiResult<LoginUserModel> {

    var loginUser = mor.admin.adminLoginUser.query()
        .where { it.loginName match loginName }
        .toEntity();

    if (loginUser == null) {
        return ApiResult.error("找不到用户{" + loginName + "}");
    }

    if (loginUser.errorLoginTimes > 10 && (LocalDateTime.now() - loginUser.lastLoginAt).seconds < 5) {
        return ApiResult.error("帐户频繁登录失败，已被锁定5分钟！")
    }

    var user = mor.admin.adminUser.query()
        .where { it.loginName match loginName }
        .toEntity()

    if (user == null) {
        return ApiResult.error("找不到用户{" + loginName + "}")
    }


    if (loginUser.password != SHA256Util.getSHA256StrJava(password + loginUser.passwordSalt)) {

        mor.admin.adminLoginUser.update()
            .set { it.lastLoginAt to LocalDateTime.now() }
            .set { it.errorLoginTimes to (loginUser.errorLoginTimes + 1) }
            .where { it.loginName match loginName }
            .exec();
        return ApiResult.error("密码错误")
    }

    var token = requestToken.AsString(TokenUtil.generateToken())
//    mor.admin.adminLoginUser.update()
//        .set { it.lastLoginAt to LocalDateTime.now() }
//        .set { it.errorLoginTimes to 0 }
//        .set { it.token to token }
//        .where { it.loginName match loginName }
//        .exec();


    return ApiResult.of(
        LoginUserModel(
            token,
            UserSystemTypeEnum.Boss.toString(),
            user.id,
            "loginName",
            user.loginName,
            user.isAdmin,
            user.name
        ).also {
            it.roles = user.roles.map { it.id }


            rer.sys.oauthToken(token).set( it);
        }
    );
}