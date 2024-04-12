package nancal.iam

import nbcp.db.mongo.match
import nancal.iam.db.mongo.mor
import nbcp.db.mongo.query
import nbcp.db.mysql.tool.MysqlEntityGenerator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc


/**
 * Created by udi on 17-3-27.
 */


@ExtendWith(SpringExtension::class)
//@WebAppConfiguration
@AutoConfigureMockMvc
@SpringBootTest()
@ActiveProfiles("yuxh")
class TestBase {
    @Autowired
    lateinit var mvc: MockMvc


    /*  init {
          GenericXmlApplicationContext().environment.setActiveProfiles("test")
          //ParserConfig.getGlobalInstance().putDeserializer(ObjectId::class.java, ObjectIdDeserializer())
      }*/

    init {
        System.setProperty("app.upload.host", "dev8.cn:9503");
        System.setProperty("app.scheduler", "false");
//        System.setProperty("server.scheduler","false")
//        System.setProperty("server.score-url","/")
        //ParserConfig.getGlobalInstance().putDeserializer(ObjectId::class.java, ObjectIdDeserializer())
    }


//    @Autowired
//    lateinit var  restTemplate: TestRestTemplate
//
//    @Test
//    fun test2() {
//        val params = LinkedMultiValueMap<Any, Any>()
//        params.add("grant_type", "client_credentials")
//        val response = restTemplate.withBasicAuth("clientId", "clientSecret")
//                .postForObject("/oauth/token", params, String::class.java)
//        println(response)
//    }


    @Test
    fun SqlEntity_Generator() {
        MysqlEntityGenerator.db2Entity().toKotlinCode()
                .map {
                    println(it);
                }
    }

    @Test
    fun test(){
        mor.tenant.tenant.query()
            .where { it.isLocked match false }
            .toEntity()
    }
}
