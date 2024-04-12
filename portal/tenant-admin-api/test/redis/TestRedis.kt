package nancal.iam

import nancal.iam.TestBase
import nancal.iam.db.redis.rer
import nbcp.db.LoginUserModel
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest


class TestRedis : TestBase() {

    @Test
    fun test01(){
        val userInfo = LoginUserModel()
        rer.sys.oauthToken("5omycynvknpd99").set(userInfo, 3600)
    }
}