package nancal.iam.base.config

import cn.hutool.http.HttpUtil
import cn.hutool.http.useragent.UserAgentUtil
import com.nancal.entity.log.RequestLogData
import com.nancal.entity.log.ResponseLogData
import com.nancal.log.sender.MqSender
import nancal.iam.annotation.BizLog
import nbcp.comm.*
import nbcp.db.CodeName
import nbcp.db.IdName
import nbcp.db.LoginUserModel
import nancal.iam.db.es.entity.BizLogData
import nancal.iam.db.es.entity.Data
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.Tenant
import nancal.iam.db.redis.rer
import nancal.iam.util.IPUtils
import nbcp.base.mvc.*
import nbcp.web.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.util.ContentCachingResponseWrapper
import java.lang.Exception
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class BizLogInterceptor : HandlerInterceptor {
    companion object {

        private val _log_msg = ThreadLocal.withInitial { "" }

        var logMsg: String
            get() {
                return _log_msg.get()
            }
            set(value: String) {
                _log_msg.set(value);
            }


        private val _app_code = ThreadLocal.withInitial { "" }

        var appCode: String
            get() {
                return _app_code.get()
            }
            set(value: String) {
                _app_code.set(value);
            }

        private val _login_user = ThreadLocal.withInitial { LoginUserModel() }

        var loginUser: LoginUserModel
            get() {
                return _login_user.get()
            }
            set(value: LoginUserModel) {
                _login_user.set(value);
            }
    }

    @Resource
    lateinit var mqSender: MqSender

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        if (handler !is HandlerMethod) {
            return
        }

//        //如果是下载, 如图片, 就不处理.
//        if (response.IsOctetContent) {
//            return;
//        }


        val bizLogAnnotation = handler.getMethodAnnotation(BizLog::class.java) ?: return

        var action = bizLogAnnotation.action
        if (action == BizLogActionEnum.Logout && logMsg.isEmpty()) {
            return
        }

        var msg = ""
        var responseBody = ""
        if (response.contentType.AsString().contains("json", true)) {
            val responseWrapper = response as ContentCachingResponseWrapper
            responseBody = responseWrapper.contentAsByteArray.toString(const.utf8)

            if (responseBody.startsWith("{")) {
                val responseJson = responseBody.FromJson<JsonMap>()!!
                msg = responseJson.get("msg").AsString()
            }
        }

        val result: String
        if (msg.HasValue) {
            result = "失败"
        } else {
            result = "成功"
        }

        val appInfo = CodeName(appCode, "")
        if (appCode.HasValue) {
            val app = mor.iam.sysApplication.queryByAppCode(appCode)
                .select { it.name }
                .toEntity()
            appInfo.name = app?.name ?: ""
        }

        val uaStr = request.getHeader("User-Agent")
        val ua = UserAgentUtil.parse(uaStr)

        val ipAddress = IPUtils.getIpAddress(request)

        //获取地域
        var city = rer.sys.ipCity(ipAddress).get()
        if (city.isEmpty()) {
            val ipInfo = IPUtils.getCityInfo(ipAddress)
            if (ipInfo != null) {
                city = ipInfo
                rer.sys.ipCity(ipAddress).set(city)
            }
        } else {
            //续期
            rer.sys.ipCity(ipAddress).renewalKey()
        }

        val roles = mor.tenant.tenantAppRole.query()
            .select { it.name }
            .where { it.id match_in loginUser.roles }
            .toList(IdName::class.java)

        var os = ua.os.name
        val indexOf = os.indexOf(" ")
        if (indexOf > 1) {
            os = os.substring(0, indexOf)
        }

        val data = Data()
        data.action = action.name
        data.resource = bizLogAnnotation.resource.remark
        data.ip = ipAddress
        data.os = os
        data.browser = ua.browser.toString()
        data.city = city
        data.remark = logMsg
        data.result = result
        data.roles = roles
        data.appInfo = appInfo
        data.tenant = loginUser.organization

        val responseLogData = ResponseLogData()
        responseLogData.status = response.status
        responseLogData.body = responseBody

        val requestLogData = RequestLogData()
        requestLogData.url = request.fullUrl
        requestLogData.method = request.method
        requestLogData.body = request.getPostJson().ToJson()

        mqSender.sendLog(
            bizLogAnnotation.module,
            msg,
            IdName(loginUser.id, loginUser.loginName),
            data,
            requestLogData,
            responseLogData
        )

        super.afterCompletion(request, response, handler, ex)
    }
}