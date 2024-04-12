package nancal.iam.base.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer



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

        registry.addInterceptor(bizLogInterceptor)
            .order(-5)
            .addPathPatterns("/*")
            .addPathPatterns("/**")

        registry.addInterceptor(resultMsgInterceptor)
            .order(-6)
            .addPathPatterns("/*")
            .addPathPatterns("/**")
            .excludePathPatterns("/wechat/**")

    }
}