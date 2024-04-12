package nancal.iam


import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.ServletComponentScan

import nbcp.comm.*
import nbcp.utils.*
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.web.context.WebServerApplicationContext
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.*
import org.springframework.scheduling.annotation.EnableAsync
import java.util.*


@SpringBootApplication(
    exclude = arrayOf(
        DataSourceAutoConfiguration::class,
        //RabbitAutoConfiguration::class,
        ElasticsearchDataAutoConfiguration::class,
        //RedisAutoConfiguration::class,
        //MongoAutoConfiguration::class
    )
)
@EnableScheduling
@EnableAsync
@ServletComponentScan
@Import(SpringUtil::class)
@EnableDiscoveryClient
@EnableFeignClients(basePackages = ["nancal.iam.client"])
//@EnableCircuitBreaker
open class MainApplication {
    companion object {
        val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

//    @Bean
//    fun webSecurityConfigurerAdapter(): WebSecurityConfigurerAdapter {
//        return object : WebSecurityConfigurerAdapter() {
//            override fun configure(httpSecurity: HttpSecurity) {
//                httpSecurity.formLogin().and().csrf().disable()
//            }
//        }
//    }
}


fun main(args: Array<String>) {

//    System.setProperty("com.sun.jndi.ldap.object.disableEndpointIdentification", "true")

//    局显示定义最省事安全
//    Locale.setDefault(Locale.ENGLISH);//推荐用英语地区的算法
    System.setProperty("user.timezone", "Asia/Shanghai");
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));

//    System.setProperty("logging.file.path", "logs/" + LocalDateTime.now().Format("yyyyMMdd.HHmmss"));
    var context = SpringApplication.run(MainApplication::class.java, *args)

//    var yapi = context.environment.getProperty("spring.data.yapi")
//    db_mongo.bindCollection2Database(mor.yapi.group.tableName, yapi)
//    db_mongo.bindCollection2Database(mor.yapi.project.tableName, yapi)
//    db_mongo.bindCollection2Database(mor.yapi.api.tableName, yapi)
//    db_mongo.bindCollection2Database(mor.yapi.interface_cat.tableName, yapi)

//    db.setDynamicMongo("adminLoginUser", master)
//    db.setDynamicMongo("planInfo", master)
//    db.setDynamicMongo("sysDictionary", master)


    usingScope(LogLevelScope.info) {

        MainApplication.logger.info("mail.sender--##${context.environment.getProperty("mail.sender")}")

        MainApplication.logger.info(
            MyUtil.getCenterEachLine(
                """
================================================
${context.debugServerInfo}
================================================
""".split("\n")
            )
                .map { ' '.NewString(6) + it }
                .joinToString("\n")
        )
    }

//    var stream = MainApplication::class.java.classLoader.getResourceAsStream("license.xml")!!
//    try {
//        License().setLicense(stream)
//    } finally {
//        stream.close()
//    }
}

val ApplicationContext.debugServerInfo: String
    get() {
        var list = mutableListOf<String>()
        if (this is WebServerApplicationContext) {
            var port = this.environment.getProperty("server.port")
            list.add("${this.webServer.javaClass.simpleName}:${port}")
        }
        var applicationName = this.environment.getProperty("spring.application.name")
        var version = this.environment.activeProfiles.joinToString(",")
//        var server = SpringUtil.containsBean(NacosDiscoveryAutoConfiguration::class.java)
//        var config = SpringUtil.containsBean(NacosConfigAutoConfiguration::class.java)


//        if (server) {
//            list.add(
//                "nacos: ${this.environment.getProperty("spring.cloud.nacos.discovery.server-addr")}(${
//                    this.environment.getProperty(
//                        "spring.cloud.nacos.discovery.namespace"
//                    )
//                })"
//            )
//        } else {
//            list.add("nacos:none")
//        }
//
//        if (config) {
//            list.add("nacos-config:enabled")
//        } else {
//            list.add("nacos-config:none")
//        }

        list.add("${applicationName}:${version}")

        return list.joinToString(" -- ");
    }

