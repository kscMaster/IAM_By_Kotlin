package nancal.iam.db.mq

import org.slf4j.LoggerFactory


object mqr {

    /**
     * 典型应用：App消息推送
     */
    class SystemGroup {
        val broadcast = MqSenderProxy<String>("broadcast")
    }

    val sys = SystemGroup()
    private val logger = LoggerFactory.getLogger(this::class.java)
}