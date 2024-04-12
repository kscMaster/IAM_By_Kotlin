package nancal.iam.base.config

import feign.Request
import java.util.Enumeration

import java.util.LinkedHashMap

import org.springframework.web.context.request.RequestContextHolder

import org.springframework.web.context.request.ServletRequestAttributes

import javax.servlet.http.HttpServletRequest

import feign.RequestInterceptor
import feign.RequestTemplate
import nbcp.comm.HasValue
import nbcp.comm.StringMap
import nbcp.comm.ToJson
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.lang.Exception


/**
 * yuxh at 2021-3-9 10:32:20
 * 微服务之间feign调用请求头丢失的问题
 */
@Configuration
class FeignRequestInterceptor : RequestInterceptor {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    //使用配置文件中的user-system
    @Value("\${app.user-system}")
    var userSystem: String = "";

    override fun apply(template: RequestTemplate) {
        var requestAttribute = RequestContextHolder.getRequestAttributes()
        if (requestAttribute == null) {
            return
        }

        val httpServletRequest = (requestAttribute as ServletRequestAttributes).request
        var headers = StringMap()
        getHeaders(httpServletRequest, "token", "Authorization", "lang")
            .forEach {
                headers.put(it.key, it.value)
            }

        headers.put("user-system", userSystem)


        if (template.request().httpMethod() == Request.HttpMethod.POST &&
            !template.headers().containsKey("Content-Type")
        ) {
            headers.put("Content-Type", "application/json")
        }

        headers.forEach {
            template.header(it.key, it.value)
        }


        logger.info("${template.method()} ${template.url()} ${headers.ToJson()}")
    }

    /**
     * 获取原请求头
     */
    private fun getHeaders(request: HttpServletRequest, vararg headers: String): Map<String, String> {
        val map: MutableMap<String, String> = linkedMapOf()
        headers.filter { it.HasValue }
            .toSet()
            .forEach {
                var value = request.getHeader(it)
                if (value.isNullOrEmpty()) return@forEach
                map.put(it, value)
            }
        return map
    }
}