//package nancal.iam
//
//import nbcp.base.mvc.*
import nbcp.web.*
//import nbcp.comm.AsString
//import nbcp.comm.HasValue
//import nbcp.comm.config
//import nbcp.utils.SpringUtil
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
//import org.springframework.boot.context.event.ApplicationPreparedEvent
//import org.springframework.boot.context.event.ApplicationReadyEvent
//import org.springframework.boot.context.event.ApplicationStartedEvent
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.context.annotation.Profile
//import org.springframework.context.event.EventListener
//import org.springframework.web.util.ContentCachingRequestWrapper
//import org.springframework.web.util.ContentCachingResponseWrapper
//import springfox.documentation.annotations.ApiIgnore
//import springfox.documentation.builders.ApiInfoBuilder
//import springfox.documentation.spi.DocumentationType
//import springfox.documentation.spring.web.plugins.Docket
//
//import java.util.*
//import javax.servlet.http.HttpServletRequest
//import javax.servlet.http.HttpServletResponse
//
///**
// * springfox-swagger-ui 3.0.0 访问地址： /swagger-ui/index.html
// */
//@Configuration
//@EnableSwagger2
//@Profile("dev", "yuxh")
//class Swagger2Config {
//    private fun getDocket(groupName: String, keyword: String = ""): Docket {
//        var enable = config.getConfig("spring.profiles.active").AsString()
//            .split(",")
//            .let {
//                return@let it.contains("dev") || it.contains("yuxh")
//            }
//
//        Docket(DocumentationType.SWAGGER_2)
//            .useDefaultResponseMessages(true)
//            .ignoredParameterTypes(
//                HttpServletRequest::class.java,
//                HttpServletResponse::class.java,
//                ContentCachingRequestWrapper::class.java,
//                ContentCachingResponseWrapper::class.java,
//                MyHttpRequestWrapper::class.java
//            )
//            .apiInfo(
//                ApiInfoBuilder()
//                    .apply {
//                        if (groupName.HasValue) {
//                            this.title(config.applicationName + " - " + groupName)
//                        } else {
//                            this.title(config.applicationName)
//                        }
//                    }
//                    .build()
//            )
//            .enable(enable)
//            .ignoredParameterTypes(ApiIgnore::class.java)
//            .enableUrlTemplating(true)
//            .apply {
//                if (groupName.HasValue) {
//                    this.groupName(groupName);
//                }
//
//            }
//            .select()
//            .apply {
//                if (keyword.AsString(groupName).HasValue) {
//                    this
//                        .paths { input: String -> input.contains(keyword.AsString(groupName)) }
//
//                }
//
//                return this.build();
//            }
//
//    }
//
//    @Bean
//    fun allSwagger(): Docket {
//        return getDocket("")
//    }
//
//    @Bean
//    fun iamSwagger(): Docket {
//        return getDocket("iam")
//    }
//
//    @Bean
//    fun devSwagger(): Docket {
//        return getDocket("admin")
//    }
//
//    @Bean
//    fun tenantSwagger(): Docket {
//        return getDocket("tenant")
//    }
//
//    @Bean
//    fun sysSwagger(): Docket {
//        return getDocket("sys")
//    }
//
////    @EventListener
////    fun start(event: ApplicationPreparedEvent) {
////        SpringUtil.registerBeanDefinition("allSwagger", getDocket(""))
////        SpringUtil.registerBeanDefinition("opsSwagger", getDocket("ops"))
////        SpringUtil.registerBeanDefinition("devSwagger", getDocket("dev"))
////        SpringUtil.registerBeanDefinition("sysSwagger", getDocket("sys"))
////    }
//
//
////    @Bean
////    fun corsConfigurer(): WebMvcConfigurer {
////        return object : WebMvcConfigurerAdapter() {
////            override fun addCorsMappings(registry: CorsRegistry) {
////                registry
////                    .addMapping("/api/pet")
////                    .allowedOrigins("http://editor.swagger.io")
////
////                registry
////                    .addMapping("/v2/api-docs.*")
////                    .allowedOrigins("http://editor.swagger.io")
////            }
////        }
////    }
//
//}