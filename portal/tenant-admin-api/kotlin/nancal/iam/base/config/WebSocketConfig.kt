package nancal.iam.base.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.server.standard.ServerEndpointExporter

@Configuration
class WebSocketConfig {
    /**
     * ServerEndpointExporter 作用
     * 这个Bean会自动注册使用@ServerEndpoint注解声明的websocket endpoint
     */
    @Bean
    fun serverEndpointExporter(): ServerEndpointExporter? {
        return ServerEndpointExporter()
    }
}