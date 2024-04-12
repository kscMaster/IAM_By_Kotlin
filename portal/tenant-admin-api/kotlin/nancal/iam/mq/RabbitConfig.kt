package nancal.iam.mq

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

/**
 *@Author shyf
 * @Date 2022/06/10
 * @Description IT
 **/
@Configuration
class RabbitConfig{

    @Autowired
    private val connectionFactory: CachingConnectionFactory? = null

    /**
     * 声明交换机
     */
    @Bean("IT_IAM_API_STATUS")
    fun exchange(): Exchange? {
        //durable(true) 持久化，mq重启之后交换机还在
        return ExchangeBuilder.directExchange("IT_IAM_API_STATUS").durable(true).build()
    }

    /**
     * 声明队列
     * new Queue(QUEUE_EMAIL,true,false,false)
     * durable="true" 持久化 rabbitmq重启的时候不需要创建新的队列
     * auto-delete 表示消息队列没有在使用时将被自动删除 默认是false
     * exclusive  表示该消息队列是否只在当前connection生效,默认是false
     */
    @Bean("IT_IAM_API_STATUS_QUEUE")
    fun esQueue(): Queue? {
        return Queue("IT_IAM_API_STATUS_QUEUE")
    }


    /**
     * 队列绑定交换机，指定routingKey
     */
    @Bean
    fun bindingEs(
        @Qualifier("IT_IAM_API_STATUS_QUEUE") queue: Queue?,
        @Qualifier("IT_IAM_API_STATUS") exchange: Exchange?,
    ): Binding? {
        return BindingBuilder.bind(queue).to(exchange).with("itIamApiStatusRouting").noargs()
    }


    /**
     * 如果需要在生产者需要消息发送后的回调，
     * 需要对rabbitTemplate设置ConfirmCallback对象，
     * 由于不同的生产者需要对应不同的ConfirmCallback，
     * 如果rabbitTemplate设置为单例bean，
     * 则所有的rabbitTemplate实际的ConfirmCallback为最后一次申明的ConfirmCallback。
     * @return
     */
//    @Bean
//    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//    fun rabbitTemplate(connectionFactory: ConnectionFactory?): RabbitTemplate? {
//        val template = connectionFactory?.let { RabbitTemplate(it) }
//        template!!.messageConverter = Jackson2JsonMessageConverter()
//        template.setConfirmCallback(RabbitTemplate.ConfirmCallback { correlationData, ack, cause ->
//            println("ConfirmCallback:     相关数据：$correlationData")
//            println("ConfirmCallback:     确认情况：$ack")
//            println("ConfirmCallback:     原因：$cause")
//        })
//        template.setReturnCallback(RabbitTemplate.ReturnCallback { message: Message, replyCode: Int, replyText: String, exchange: String, routingKey: String ->
//            println("ReturnCallback:     消息：$message")
//            println("ReturnCallback:     回应码：$replyCode")
//            println("ReturnCallback:     回应信息：$replyText")
//            println("ReturnCallback:     交换机：$exchange")
//            println("ReturnCallback:     路由键：$routingKey")
//        })
//        return template
//    }

}

