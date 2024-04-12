package nancal.iam

import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import
import nbcp.utils.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner


@ExtendWith(SpringExtension::class)
@SpringBootTest
@Import(SpringUtil::class)
//@EnableAutoConfiguration(exclude = arrayOf())
@EnableAutoConfiguration(exclude = arrayOf(DataSourceAutoConfiguration::class))
open class PzxEntityWebApplication {

}