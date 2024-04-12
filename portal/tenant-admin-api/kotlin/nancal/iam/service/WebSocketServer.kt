package nancal.iam.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

@Component
@ServerEndpoint("/websocket/{param}")
class WebSocketServer {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java.declaringClass)

        var onlineCount = AtomicInteger(0)
        var sessionMap = mutableMapOf<String, Session>()

        /**
         * 发送消息，每次浏览器刷新，session会发生变化。
         * @param session
         * @param message
         */
        fun sendMessage(session: Session, message: String?) {
            try {
                session.basicRemote.sendText(message)
            } catch (e: IOException) {
                log.error("发送消息出错：{}", e.message)
                e.printStackTrace()
            }
        }

        /**
         * 群发消息
         * @param message
         * @throws IOException
         */
        @Throws(IOException::class)
        fun sendMessageToAll(message: String?) {
            val sessions: Collection<Session> = sessionMap.values
            for (session in sessions) {
                if (session.isOpen) {
                    sendMessage(session, message)
                }
            }
        }

        /**
         * 指定租户发送消息
         * @param userIds
         * @param message
         * @throws IOException
         */
        @Throws(IOException::class)
        fun sendMessageToTenant(message: String, tenantId: String) {
            var i = 0
            for (entry in sessionMap) {
                if (entry.key.startsWith(tenantId)){
                    sendMessage(entry.value, message)
                    i++
                }
            }
            if (i == 0){
                log.warn("没有找到指定租户ID的会话：{}", tenantId)
            }
        }


        /**
         * 指定单个用户发送消息
         * @param userId
         * @param message
         * @throws IOException+
         */
        @Throws(IOException::class)
        fun sendMessageToUser(message: String, userId: String) {
            var i = 0
            for (entry in sessionMap) {
                if (entry.key.contains(userId)){
                    sendMessage(entry.value, message)
                    i++
                }
            }
            if (i == 0){
                log.warn("没有找到指定用户ID的会话：{}", userId)
            }
        }
    }


    /**
     * 连接建立
     * @param session
     */
    @OnOpen
    fun onOpen(session: Session, @PathParam("param") param: String) {
        /* param 格式为 租户ID,用户ID,token 例如：61e0135ae3dee11ecc5e68d0,61e0135be3dee11ecc5e68d2,st!5kmgz4k92ozk */
        sessionMap.put(param, session)
        log.info(
            "有连接加入，param：{}，sessionId：{}，当前连接数为：{}",
            param,
            session.id,
            onlineCount.incrementAndGet()
        )
        sendMessage(session, "连接成功")
    }

    /**
     * 连接关闭
     * @param session
     */
    @OnClose
    fun onClose(session: Session, @PathParam("param") param: String?) {
        sessionMap.remove(param, session)
        log.info(
            "有连接关闭，param：{}，sessionId：{}，当前连接数为：{}",
            param,
            session.id,
            onlineCount.decrementAndGet()
        )
    }

    /**
     * 收到消息
     * @param message
     * @param session
     */
    @OnMessage
    fun onMessage(message: String, session: Session) {
        log.info("收到消息：{}", message)
        sendMessage(session, "收到消息，消息内容：$message")
    }

    /**
     * 出现错误
     * @param session
     * @param error
     */
    @OnError
    fun onError(session: Session, error: Throwable) {
        log.error("发生错误：{}，Session ID： {}", error.message, session.id)
        error.printStackTrace()
    }
}