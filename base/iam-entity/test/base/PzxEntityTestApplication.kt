package nancal.iam

import nbcp.utils.*
import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.context.annotation.Import


@SpringBootApplication
@Import(SpringUtil::class)
@EnableAutoConfiguration(exclude = arrayOf(
    DataSourceAutoConfiguration::class,
//    MongoAutoConfiguration::class,
    RedisAutoConfiguration::class,
    ElasticsearchDataAutoConfiguration::class
))
open class PzxEntityTestApplication {

    companion object {
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            //disabled banner, don't want to see the spring logo
            val app = SpringApplication(PzxEntityTestApplication::class.java)
            app.setBannerMode(Banner.Mode.OFF)
            var context = app.run(*args)

//            SpringUtil.context = context;
        }
    }
}