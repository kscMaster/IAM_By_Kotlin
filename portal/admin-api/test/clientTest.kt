package nancal.iam.mvc.tenant

import nancal.iam.TestBase
import nancal.iam.client.MPClient
import nancal.iam.enums.IamMsgEnum
import org.junit.jupiter.api.Test
import javax.annotation.Resource

/**
 *@Author shyf
 * @Date 2022/06/13
 * @see nancal.iam.mvc.tenant.TenantController 注释
 **/
class clientTest  :TestBase(){

    @Resource
    lateinit var mpClient: MPClient

    @Test
    fun res() {

        var res: String = mpClient.res("en", "mp", IamMsgEnum.MSG_44.key)
        println(res)
    }
}

