package nancal.iam.db.mq

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import nbcp.comm.*
import nbcp.utils.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean

@Configuration
@Lazy
@ConditionalOnBean(RabbitAutoConfiguration::class)
open class MqConfig {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    @Autowired
    lateinit var cachingConnectionFactory: CachingConnectionFactory


    @Bean
    @Lazy
    open fun mqTemplate(): RabbitTemplate {
        var ret = RabbitTemplate(cachingConnectionFactory)

        if (cachingConnectionFactory.isPublisherConfirms) {
            //如果消息没有到exchange,则confirm回调,ack=false
            //如果消息到达exchange,则confirm回调,ack=true
            ret.setConfirmCallback { correlationData, ack, cause ->
                if (correlationData == null) return@setConfirmCallback;
                if (correlationData.id == null) return@setConfirmCallback;

                if (ack) {
                } else {
                    logger.error("${correlationData.id}, 消息:${correlationData.returnedMessage.body}, 确认阶段失败, 发送失败")
                }
            }
        }

        if (cachingConnectionFactory.isPublisherReturns) {
            ret.setMandatory(true);
            //exchange到queue成功,则不回调return
            //exchange到queue失败,则回调return(需设置mandatory=true,否则不回回调,消息就丢了)
            ret.setReturnCallback { message, replyCode, replyText, exchange, routingKey ->
                var logId = message.messageProperties.headers["log-id"].AsString()

                logger.error("${exchange}-${routingKey}:${logId}, 消息:${message.body}, 返回 ${replyCode}-${replyText}")
            }
        }
        return ret;
    }
}
