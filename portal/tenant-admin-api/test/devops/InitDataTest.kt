package nancal.iam

import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nbcp.comm.ToJson
import nbcp.comm.const
import org.junit.jupiter.api.Test
import java.io.File

class InitDataTest : TestBase() {
    @Test
    fun initData() {
        var path = Thread.currentThread().contextClassLoader.getResource("").path.split("/target/")[0] + "/resources"

        path += "/flyway-v1"

        mor.admin.adminUser.query()
            .where { it.loginName match "admin" }
            .toEntity()!!
            .apply {
                File("${path}/adminUser.dat").writeText(this.ToJson(), const.utf8)
            }

        mor.admin.adminLoginUser.query()
            .where { it.loginName match "admin" }
            .toEntity()!!
            .apply {
                File("${path}/adminLoginUser.dat").writeText(this.ToJson(), const.utf8)
            }
    }
}