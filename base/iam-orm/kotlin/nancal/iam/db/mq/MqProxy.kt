package nancal.iam.db.mq

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.core.RabbitTemplate
import nbcp.comm.*
import nbcp.utils.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import org.springframework.amqp.rabbit.connection.CorrelationData
import java.nio.charset.Charset

class MqSenderProxy<T>(val exchangeName: String = "") {

    //返回 MqLog.id
    fun send(entity: T, charset: Charset = const.utf8): String {
        var json = entity.ToJson();

        var log = MqLog();
        log.name = exchangeName;
        log.body = json
        mor.log.mqLog.doInsert(log)

        var properties = MessageProperties();
        properties.setHeader("log-id", log.id)
        var message = Message(json.toByteArray(charset), properties);

        mqTemplate.convertAndSend(exchangeName, "", message, CorrelationData(log.id));

        return log.id;
    }

    private val mqTemplate by lazy {
        SpringUtil.getBean<RabbitTemplate>()
    }
}