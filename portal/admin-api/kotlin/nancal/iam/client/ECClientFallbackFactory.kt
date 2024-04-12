package nancal.iam.client

import org.slf4j.LoggerFactory
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.stereotype.Component

@Component
class ECClientFallbackFactory : FallbackFactory<ECClient> {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    var throwableThreadLocal = ThreadLocal<Throwable>()
    override fun create(cause: Throwable): ECClient {
        if (log.isInfoEnabled) {
            log.info("call ec client service fail: {}", cause.message)
        }
        if (log.isDebugEnabled) {
            cause.printStackTrace()
        }
        throwableThreadLocal.set(cause)
        return ecclient
    }

    private val ecclient = object : ECClient {
        override fun getApplicationsByPlatform(): BaseResult {
            return BaseResult.fail(-1, throwableThreadLocal.get().cause!!.message)
        }

        override fun getApplicationsByPlatformV2(): BaseResult {
            return BaseResult.fail(-1, throwableThreadLocal.get().cause!!.message)
        }
    }
}
