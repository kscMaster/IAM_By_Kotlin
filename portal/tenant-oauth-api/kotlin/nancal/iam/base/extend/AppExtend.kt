package nancal.iam.comm

import ch.qos.logback.classic.Logger
import nbcp.comm.Filter
import nbcp.utils.*
import java.io.File
import javax.servlet.http.HttpServletRequest

fun Logger.getLoggerFile(configName: String): String {
    var appenderList = this.iteratorForAppenders();
    if (appenderList.hasNext()) {
        var fileAppender = (appenderList.Filter { it.name == configName }.first() as ch.qos.logback.core.rolling.RollingFileAppender)
        return (MyUtil.getValueByWbsPath(fileAppender, "currentlyActiveFile") as File).absolutePath
    }

    var parent = MyUtil.getValueByWbsPath(this, "parent") as ch.qos.logback.classic.Logger?
    if (parent == null) return "";
    return parent.getLoggerFile(configName);
}

