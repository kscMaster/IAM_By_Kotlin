package nancal.iam.comm

import ch.qos.logback.classic.Logger
import nancal.iam.db.mongo.entity.AdminUser
import nancal.iam.db.mongo.mor
import nbcp.comm.AsString
import nbcp.comm.Filter
import nbcp.comm.HasValue
import nbcp.db.mongo.queryById
import nbcp.utils.*
import nbcp.web.LoginUser
import java.io.File
import java.lang.RuntimeException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

fun Logger.getLoggerFile(configName: String): String {
    var appenderList = this.iteratorForAppenders();
    if (appenderList.hasNext()) {
        var fileAppender =
            (appenderList.Filter { it.name == configName }.first() as ch.qos.logback.core.rolling.RollingFileAppender)
        return (MyUtil.getValueByWbsPath(fileAppender, "currentlyActiveFile") as File).absolutePath
    }

    var parent = MyUtil.getValueByWbsPath(this, "parent") as ch.qos.logback.classic.Logger?
    if (parent == null) return "";
    return parent.getLoggerFile(configName);
}

val HttpServletRequest.LoginAdminUser: AdminUser
    get() {
        var ret = this.getAttribute("[LoginAdminUser]") as AdminUser?;
        if (ret != null) {
            return ret;
        }

        var loginUser = this.LoginUser;
        if (loginUser.id.HasValue == false) {
            throw RuntimeException("未登录")
        }

        ret = mor.admin.adminUser.queryById(loginUser.id).toEntity()
        if (ret == null) {
            throw RuntimeException("找不到用户")
        }
        this.setAttribute("[LoginAdminUser]", ret)
        return ret;
    }


var HttpServletRequest.logMsg: String
    get() {
        return this.getAttribute("[LogMsg]").AsString()
    }
    set(value: String) {
        this.setAttribute("[LogMsg]", value)
    }