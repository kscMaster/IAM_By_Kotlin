package nancal.iam.db.sql.extend

import nbcp.comm.*
import nbcp.comm.minus
import nbcp.db.IdName
import nbcp.db.LoginUserModel
import nancal.iam.db.mongo.UserSystemTypeEnum
import nancal.iam.db.sql.*
import nbcp.db.sql.*
import nbcp.db.sql.*
import nancal.iam.db.sql.table.AdminGroup
import nbcp.utils.*
import java.time.LocalDateTime


/**
 * 登录验证，返回错误消息
 */
fun AdminGroup.admin_user_table.doLogin(
    loginName: String,
    password: String,
    requestToken: String,
):
        ApiResult<LoginUserModel> {

    var loginUser = dbr.admin.admin_login_user.query()
        .where { it.loginName match loginName }
        .toEntity();

    if (loginUser == null) {
        return ApiResult.error("找不到用户{" + loginName + "}");
    }

    if (loginUser.errorLoginTimes > 10 && (LocalDateTime.now() - loginUser.lastLoginAt).toMinutes() < 5) {
        return ApiResult.error("帐户频繁登录失败，已被锁定5分钟！")
    }

    var user = dbr.admin.admin_user.query()
        .where { it.loginName match loginName }
        .toEntity()

    if (user == null) {
        return ApiResult.error("找不到用户{" + loginName + "}")
    }

    if (loginUser.password != Md5Util.getBase64Md5(password)) {

        dbr.admin.admin_login_user.update()
            .set { it.lastLoginAt to LocalDateTime.now() }
            .set { it.errorLoginTimes to (loginUser.errorLoginTimes + 1) }
            .where { it.loginName match loginName }
            .exec();
        return ApiResult.error("密码错误")
    }

    var token = requestToken.AsString(CodeUtil.getCode())
    dbr.admin.admin_login_user.update()
        .set { it.lastLoginAt to LocalDateTime.now() }
        .set { it.errorLoginTimes to 0 }
        .set { it.token to token }
        .where { it.loginName match loginName }
        .exec();


    return ApiResult.of(
        LoginUserModel(
            token,
            UserSystemTypeEnum.Boss.toString(),
            user.id,
            "loginName",
            user.loginName,
            user.isAdmin,
            user.name
        )
    )
}