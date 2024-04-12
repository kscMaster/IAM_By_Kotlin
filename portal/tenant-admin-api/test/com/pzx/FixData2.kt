package nancal.iam

import com.zaxxer.hikari.HikariDataSource
import nbcp.utils.SpringUtil
import org.junit.jupiter.api.Test
import javax.sql.DataSource

class FixData2 : TestBase() {

    @Test
    fun test3() {
        var d = SpringUtil.getBean<DataSource>() as HikariDataSource
        println(d.jdbcUrl)
        println(d.maximumPoolSize)

        println(d.password)
    }
}