package nancal.iam

import nancal.iam.TestBase
import nancal.iam.db.mongo.mor
import nbcp.db.mongo.delete
import nbcp.db.mongo.query
import nbcp.utils.SpringUtil
import org.junit.jupiter.api.Test

class NacosTest : TestBase() {
    @Test
    fun testDeleteUsers() {

        val userIdList = mor.tenant.tenantLoginUser.query()
            .select { it.userId }
            .toList(String::class.java)

        val deleteList = mor.tenant.tenantUser.delete()
            .where { it.id match_notin userIdList }
            .exec()
        println("123")

    }
}