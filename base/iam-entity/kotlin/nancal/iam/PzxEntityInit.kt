package nancal.iam

import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent

/**
 * Created by yuxh on 2018/9/11
 */

@Configuration
open class PzxEntityInit : ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {

    }
}