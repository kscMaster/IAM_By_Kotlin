package nancal.iam

import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired


/**
 *@Author shyf
 * @Date 2022/06/10
 * @see nancal.iam.base.config.WebSocketConfig 注释掉
 **/
class mqSend :TestBaseShyf() {

    @Autowired
    var rabbitTemplate: RabbitTemplate? = null

//    var mqSend : RabbitMqUtils?= null

    @Test
    fun sendMessage() {
        val message  = message()
        message.tenantId = "1111"
        message.appCode = "1111"
        message.name = "1111"
        message.code = "1111"
        message.resource = "1111"
        message.status = "1111"

        rabbitTemplate?.convertAndSend("it.topic.exchange","it.topic.messages", message)
    }
}


/**
 * {
"tenantId": "租户ID",
"appCode": "应用code",
"name": "资源名称", 接口名称
"code": "资源code", 自动生成|唯一值
"resource": "接口URL",
"status":"0|1"
}
 */
class message{
    var tenantId :String = ""
    var appCode :String = ""
    var name :String = ""
    var code :String = ""
    var resource :String = ""
    var status :String = ""
}