package nancal.iam

import org.springframework.test.context.junit.jupiter.SpringExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.support.FileSystemXmlApplicationContext
import org.springframework.context.support.GenericXmlApplicationContext
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import nbcp.utils.*
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles

/**
 * Created by udi on 17-3-27.
 */


@ExtendWith(SpringExtension::class)
@SpringBootTest()
@AutoConfigureMockMvc
@ActiveProfiles("yuxh")
abstract class TestBase {

    init {
        //ParserConfig.getGlobalInstance().putDeserializer(ObjectId::class.java, ObjectIdDeserializer())
    }
}