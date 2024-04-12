package nancal.iam

import nancal.iam.client.RemoteMicroMsgClient
import nancal.iam.dto.LezaoMessageDTO
import org.junit.jupiter.api.Test
import javax.annotation.Resource

class msgtest : TestBase() {

    @Resource
    lateinit var client: RemoteMicroMsgClient

    @Test
    fun testCreate() {
        val msg = LezaoMessageDTO()
        msg.applicationName = "aaa"
        msg.title = "这是一个测试数据"
        msg.userIds = mutableListOf("6281c7a9eb2535609ea12df9")
        msg.body = "<p>这是一个测试内容</p>"
        msg.mailType = 1
        val createMessage = client.createMessage(msg)
        println(createMessage)
    }
}