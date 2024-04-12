package nancal.iam

import nbcp.base.mvc.*
import nbcp.web.*
import org.slf4j.LoggerFactory
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener

@Configuration
class SnowFlakeMachineIdService {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

//    @Autowired
//    lateinit var nacosService: SnowFlakeRedisService;

    fun setSnowFlakeMachineId(ip: String, port: Int) {
//        val machineId = nacosService.getSnowFlakeMachineId(
//            SpringUtil.context.environment.getProperty("spring.profiles.active"),
//            SpringUtil.context.environment.getProperty("spring.application.name"),
//            ip,
//            port
//        )
//
//        SpringUtil.getBean<SnowFlake>().machineId = machineId
//        logger.info("机器Id：${machineId}")
    }

//    @EventListener
//    fun afterPropertiesSet(event: InstanceRegisteredEvent<NacosDiscoveryProperties>) {
//        var config = event.config;
//        this.setSnowFlakeMachineId(config.ip, config.port)
//    }
}