package nancal.iam.base.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import nbcp.utils.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
//import org.springframework.session.web.http.DefaultCookieSerializer
//import org.springframework.messaging.converter.StringMessageConverter
import java.util.ArrayList


/**
 * Created by udi on 2017.3.11.
 */
@Configuration
open class MyWebMvcConfig : WebMvcConfigurer {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    @Autowired
    lateinit var myInterceptor: MyInterceptor
    @Autowired
    lateinit var bizLogInterceptor: BizLogInterceptor

    @Autowired
    lateinit var resultMsgInterceptor: ResultMsgInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(myInterceptor)
            .order(-5)
            .addPathPatterns("/*")
            .addPathPatterns("/**")
            .excludePathPatterns("/wechat/**")

        registry.addInterceptor(bizLogInterceptor)
            .order(-2)
            .addPathPatterns("/*")
            .addPathPatterns("/**")
            .excludePathPatterns("/wechat/**")

        registry.addInterceptor(resultMsgInterceptor)
            .order(-6)
            .addPathPatterns("/*")
            .addPathPatterns("/**")
            .excludePathPatterns("/wechat/**")
    }
}