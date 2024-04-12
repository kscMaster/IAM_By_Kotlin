//package nancal.iam.base.config
//
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.ComponentScan
//import org.springframework.context.annotation.Configuration
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
//import springfox.documentation.spi.DocumentationType
//import springfox.documentation.spring.web.plugins.Docket
//;
//import springfox.documentation.spi.service.contexts.SecurityContext
//import springfox.documentation.service.*
//import springfox.documentation.service.SecurityReference
//import springfox.documentation.service.ApiKey
//import springfox.documentation.builders.*
//
///**
// * Created by udi on 17-3-19.
// */
//@Configuration
////@EnableSwagger2
//@ComponentScan(basePackages = ["nancal.iam.web", "nancal.iam.mvc"])
//open class Swagger2 : WebMvcConfigurer {
//    companion object {
//        //即使有大写，在 request.header 中传递的时候，也会变成小写。
//        val TOKEN_NAME: String = "token";
//    }
//
////    @Bean
////    open fun petApi(): Docket {
////        return Docket(DocumentationType.SWAGGER_2)
////                .useDefaultResponseMessages(false)
//////                .consumes(mutableSetOf(""))
//////                .produces(mutableSetOf(""))
////                .select()
////                .apis(RequestHandlerSelectors.any())
////                .paths(PathSelectors.any())
////                .build()
////                .pathMapping("/")
////                .securitySchemes(mutableListOf(apiKey()))
////                .securityContexts(mutableListOf(securityContext()))
////    }
//
//
//    private fun apiKey(): ApiKey {
//        return ApiKey(TOKEN_NAME, "手机号", "header");
//    }
//
//    private fun securityContext(): SecurityContext {
//        return SecurityContext.builder()
//            .securityReferences(defaultAuth())
//            //.forPaths(PathSelectors.regex("/anyPath.*"))
//            .build();
//    }
//
//    private fun defaultAuth(): List<SecurityReference> {
//        var authorizationScope = AuthorizationScope("global", "accessEverything");
//        var authorizationScopes = mutableListOf<AuthorizationScope>();
//        authorizationScopes.add(authorizationScope);
//        return mutableListOf(SecurityReference(TOKEN_NAME, authorizationScopes.toTypedArray()));
//    }
//
//    @Override
//    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
//        registry.addResourceHandler("/statics/**").addResourceLocations("classpath:/statics/");
//        // 解决 SWAGGER 404报错
//
//
//        registry.addResourceHandler("/swagger-resources/**").addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/springfox-swagger-ui/**")
//            .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
//
//    }
//
////    @Bean
////    fun funsecurity(): SecurityConfiguration {
////        return SecurityConfiguration(
////                "test-app-client-id",
////                "test-app-client-secret",
////                "test-app-realm",
////                "电商平台",
////                "apiKey",
////                ApiKeyVehicle.HEADER,
////                "api_key",
////                "," /*scope separator*/);
////    }
//
////    @Bean
////    fun uiConfig(): UiConfiguration {
////        return UiConfiguration(
////                "validatorUrl", // url
////                "none", // docExpansion          => none | list
////                "alpha", // apiSorter             => alpha
////                "schema", // defaultModelRendering => schema
////                UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS,
////                false, // enableJsonEditor      => true | false
////                true, // showRequestHeaders    => true | false
////                60000L);      // requestTimeout => in milliseconds, defaults to null (uses jquery xh timeout)
////    }
//}