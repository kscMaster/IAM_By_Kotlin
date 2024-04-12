package nancal.iam.base.config

import cn.hutool.http.HttpUtil
import cn.hutool.http.useragent.UserAgentUtil
import com.nancal.entity.log.RequestLogData
import com.nancal.entity.log.ResponseLogData
import com.nancal.log.sender.MqSender
import nancal.iam.annotation.BizLog
import nancal.iam.comm.LoginAdminUser
import nancal.iam.comm.logMsg
import nbcp.comm.*
import nbcp.db.CodeName
import nbcp.db.IdName
import nancal.iam.db.es.entity.BizLogData
import nancal.iam.db.es.entity.Data
import nancal.iam.db.mongo.BizLogActionEnum
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.mongo.mor
import nbcp.db.mongo.queryById
import nancal.iam.db.redis.rer
import nancal.iam.util.IPUtils
import nbcp.base.mvc.*
import nbcp.web.*
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
    @Resource
    lateinit var mqSender: MqSender

    companion object {
        private val _login_user = ThreadLocal.withInitial { AdminUser() }
        var loginAdminUser: AdminUser
            get() {
                return _login_user.get()
            }
            set(value: AdminUser) {
                _login_user.set(value);
            }
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        if (handler !is HandlerMethod) {
            return
        }
//        if (response.IsOctetContent) {
//            return;
//        }
        val bizLogAnnotation = handler.getMethodAnnotation(BizLog::class.java) ?: return

        //save 区分创建和修改
        var action = bizLogAnnotation.action
        if (action == BizLogActionEnum.Save) {
            val id = request.findParameterStringValue("id")
            if (id.isEmpty()) {
                action = BizLogActionEnum.Add
            } else {
                action = BizLogActionEnum.Update
            }
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

        if (action != BizLogActionEnum.Login && action != BizLogActionEnum.Logout){
            loginAdminUser = request.LoginAdminUser
        }

        val parameterValue = request.findParameterValue("tenant")

        var tenant: Tenant? = parameterValue.ToJson().FromJson()
        if (tenant == null) {
            val tenantId = request.findParameterStringValue("tenantId")
            if (tenantId.isEmpty()) {
                tenant = null
            } else {
                tenant = mor.tenant.tenant.queryById(tenantId).toEntity()
            }
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

        val roles: MutableList<IdName> = mutableListOf()
        val role = IdName("admin", "超级管理员")
        roles.add(role)

        val data = Data()
        data.action = action.name
        data.resource = bizLogAnnotation.resource.remark
        data.ip = ipAddress
        data.os = ua.os.name
        data.browser = ua.browser.toString()
        data.city = city
        data.remark = request.logMsg
        data.result = result
        data.roles = roles
        data.appInfo = CodeName("mp-iam-boos", "乐仓统一身份认证系统")
        if (tenant != null) {
            data.tenant = IdName(tenant.id, tenant.name)
        }

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
            IdName(loginAdminUser.id, loginAdminUser.loginName),
            data,
            requestLogData,
            responseLogData
        )

        super.afterCompletion(request, response, handler, ex)
    }
}