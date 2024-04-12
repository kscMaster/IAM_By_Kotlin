package nancal.iam.comm

import ch.qos.logback.classic.Logger
import nancal.iam.db.mongo.entity.TenantUser
import nancal.iam.db.mongo.mor
import nancal.iam.db.redis.rer
import nbcp.comm.AsString
import nbcp.comm.Filter
import nbcp.comm.HasValue
import nbcp.db.mongo.queryById
import nbcp.utils.MyUtil
import nbcp.web.UserId
import nbcp.web.tokenValue
import java.io.File
import javax.servlet.http.HttpServletRequest

fun Logger.getLoggerFile(configName: String): String {
    var appenderList = this.iteratorForAppenders()
    if (appenderList.hasNext()) {
        var fileAppender =
            (appenderList.Filter { it.name == configName }.first() as ch.qos.logback.core.rolling.RollingFileAppender)
        return (MyUtil.getValueByWbsPath(fileAppender, "currentlyActiveFile") as File).absolutePath
    }

    var parent = MyUtil.getValueByWbsPath(this, "parent") as ch.qos.logback.classic.Logger?
    if (parent == null) return ""
    return parent.getLoggerFile(configName)
}


val HttpServletRequest.LoginTenantAdminUser: TenantUser
    get() {
        var ret = this.getAttribute("[TenantAdminUser]") as TenantUser?
        if (ret != null) {
            return ret
        }

        if (this.UserId.HasValue) {
            ret = mor.tenant.tenantUser.queryById(this.UserId).toEntity()
        } else {
            val userId = this.getHeader("user-id")
            if (userId.HasValue) {
                ret = mor.tenant.tenantUser.queryById(userId).toEntity()
            } else {
                val loginUser = rer.sys.oauthToken(this.tokenValue).get() ?: throw RuntimeException("未登录")
                ret = mor.tenant.tenantUser.queryById(loginUser.id).toEntity()
            }
        }
//        val loginUser = this.LoginUser
//        if (loginUser.id.HasValue == false) {
//            throw RuntimeException("未登录")
//        }

//        ret = mor.tenant.tenantUser.queryById(loginUser.id).toEntity()
//        ret = mor.tenant.tenantUser.queryById(this.UserId).toEntity()
        if (ret == null) {
            throw RuntimeException("找不到用户")
        }
//        if (ret.adminType == TenantAdminTypeEnum.None) {
//            throw RuntimeException("没有管理员权限")
//        }

        this.setAttribute("[LoginAdminUser]", ret)
        return ret
    }


var HttpServletRequest.logMsg: String
    get() {
        return this.getAttribute("[LogMsg]").AsString()
    }
    set(value: String) {
        this.setAttribute("[LogMsg]", value)
    }