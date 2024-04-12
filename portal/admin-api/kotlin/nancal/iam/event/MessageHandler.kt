package nancal.iam.event

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.rabbitmq.client.Channel
import nbcp.comm.FromJson
import nbcp.db.BaseEntity
import nbcp.db.Cn
import nancal.iam.mvc.admin.SysApplicationAutoController
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.ExchangeTypes
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.stereotype.Component
import java.io.IOException

@Component
@ConditionalOnProperty(name = ["app.task"], havingValue = "true", matchIfMissing = true)
class MessageHandler {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    /**
     * 企业容器应用变动监听事件，目前可以自动ACK，但写成了手动。有问题看日志吧，再重消费几次也白搭
     *
     * @param message
     */
    /*@RabbitListener(
        bindings = [QueueBinding(
            value = Queue(value = "iam.topic.queue.sync.app", durable = "true", autoDelete = "false"),
            exchange = Exchange(value = "ec_app_exchange", type = ExchangeTypes.TOPIC),
            key = arrayOf("#.message"),
        )],
        concurrency = "1",
        ackMode = "MANUAL",
//        ackMode = "AUTO",
    )*/
    fun handleMessage(channel: Channel, message: Message, str: String) {
        try {
            logger.info("更新入库操作${message.body}")
            val app = str.FromJson(SystemApplicationVO::class.java)
            SysApplicationAutoController.singleUpdate(app!!).apply {
                if (this > 0) {
                    logger.info("更新入库正常${message.body}")
                    channel.basicAck(message.messageProperties.deliveryTag, false)
                } else {
                    logger.info("更新入库出现了异常，可能上游数据结构有变动${message.body}")
                    channel.basicAck(message.messageProperties.deliveryTag, false)
                }
            }
        } catch (e: IOException) {
            logger.info("更新解析出现了异常，可能上游数据结构有变动${message.body}")
            // 滚吧，老子不要你这条垃圾消息了，测试时有些垃圾数据导致异常，对于这类消息直接丢掉
            channel.basicAck(message.messageProperties.deliveryTag, false)
        }
    }
}

/** apis from ec **/

@Cn("企业容器的API实体")
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
data class
SystemApplicationVO(
    @Cn("应用版本名称")
    var versionName: String? = "",
    @Cn("应用名称")
    var applicationName: String  = "",
    @Cn("实际访问地址")
    var actualAddress: String? = null,
    @Cn("上架状态 0 未上架 ，1 已经上架")
    var status: Int? = 0,
    @Cn("应用编码")
    var appCode: String = "",
    @Cn("应用id")
    var applicationId: String?= null,
    @Cn("访问地址")
    var address: String? = "",
    @Cn("分类")
    var classification: String?= "",
    @Cn("logo")
    var logo: String? = null,
    @Cn("0-内部用用，1-外部应用")
    var applicationType: Int? = 0,
    @Cn("帮助文档")
    var documentUrl: Any? = "",
    @Cn("是否是乐造平台的壳属性  1:不是  2:是")
    var protal: String? = null,
    @Cn("应用私钥")
    var privateKey: String  = "",
    @Cn("应用公钥")
    var publicKey: String  = "",
    @Cn("mq里加的，操作类型")
    var action: String? = "",
) : BaseEntity()
