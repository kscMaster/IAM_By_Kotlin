//package nancal.iam
//
//
//import nbcp.comm.AsString
//import nbcp.comm.HasValue
//import nbcp.comm.config
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.context.annotation.Profile
//import org.springframework.web.bind.annotation.RequestMethod
//import org.springframework.web.util.ContentCachingRequestWrapper
//import org.springframework.web.util.ContentCachingResponseWrapper
//import springfox.documentation.annotations.ApiIgnore
//import springfox.documentation.builders.ApiInfoBuilder
//import springfox.documentation.spi.DocumentationType
//import springfox.documentation.spring.web.plugins.Docket
//
//import javax.servlet.http.HttpServletRequest
//import javax.servlet.http.HttpServletResponse
//
///**
// * springfox-swagger-ui 3.0.0 访问地址： /swagger-ui/index.html
// */
//@Configuration
//@EnableSwagger2
//@Profile("dev", "yuxh")
//class Swagger3Config {
//    private fun getDocket(groupName: String, keyword: String = "", vararg methods: RequestMethod): Docket {
//        var enable = config.getConfig("spring.profiles.active").AsString()
//            .split(",")
//            .let {
//                return@let it.contains("dev") || it.contains("yuxh")
//            }
//
//        Docket(DocumentationType.OAS_30)
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
//                    .description("地址栏添加 ?docExpansion=list 展开API")
//                    .build()
//            )
//            .enable(enable)
//            .ignoredParameterTypes(ApiIgnore::class.java)
//            .enableUrlTemplating(true)
//            .apply {
//                if (groupName.HasValue) {
//                    this.groupName(groupName);
//                }
//            }
//            .select()
//            .apply {
//                if (methods.any()) {
//                    this.apis {
//                        it.supportedMethods().intersect(methods.toList()).any()
//                    }
//
//                } else {
//                    this.apis {
//                        it.supportedMethods().intersect(listOf(RequestMethod.GET, RequestMethod.POST)).any()
//                    }
//                }
//
//                if (keyword.AsString(groupName).HasValue) {
//                    this
//                        .paths { input: String ->
//                            input.contains(keyword.AsString(groupName), ignoreCase = true)
//                        }
//
//                }
//                return this.build();
//            }
//
//    }
//
//    @Bean
//    fun allSwagger(): Docket {
//        return getDocket("全部", "/")
//    }
//
//    @Bean
//    fun postSwagger(): Docket {
//        return getDocket("POST请求", "/", RequestMethod.POST)
//    }
//
//
//    @Bean
//    fun getSwagger(): Docket {
//        return getDocket("GET请求", "/", RequestMethod.GET)
//    }
//
//    @Bean
//    fun adminSwagger(): Docket {
//        return getDocket("admin")
//    }
//
//    @Bean
//    fun listSwagger(): Docket {
//        return getDocket("列表", "list")
//    }
//
//    @Bean
//    fun devSwagger(): Docket {
//        return getDocket("dev")
//    }
//
//    @Bean
//    fun sysSwagger(): Docket {
//        return getDocket("sys")
//    }
//
//
//    @Bean
//    fun iamSwagger(): Docket {
//        return getDocket("iam")
//    }
//
//    @Bean
//    fun tenantSwagger(): Docket {
//        return getDocket("tenant")
//    }
//
//}