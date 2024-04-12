package nancal.iam

import org.springframework.test.context.junit.jupiter.SpringExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.support.GenericXmlApplicationContext
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import nbcp.utils.*
import nbcp.db.mysql.tool.MysqlEntityGenerator
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.util.LinkedMultiValueMap
import javax.sql.DataSource


/**
 * Created by udi on 17-3-27.
 */


@ExtendWith(SpringExtension::class)
//@WebAppConfiguration
@AutoConfigureMockMvc
@SpringBootTest()
@ComponentScan(basePackages = arrayOf("nancal.iam"))
@ActiveProfiles("shyf")
class TestBaseShyf {
    @Autowired
    lateinit var mvc: MockMvc


//    init {
////        System.setProperty("app.upload.host", "dev8.cn:9503");
//        System.setProperty("app.scheduler", "false");
//        System.setProperty("server.scheduler","false")
//    }


    @Test
    fun SqlEntity_Generator() {
        MysqlEntityGenerator.db2Entity().toKotlinCode()
                .map {
                    println(it);
                }
    }
}