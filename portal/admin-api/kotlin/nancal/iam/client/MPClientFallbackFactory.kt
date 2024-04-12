package nancal.iam.client

import nbcp.comm.JsonResult
import nancal.iam.db.mongo.MobileCodeModuleEnum
import org.slf4j.LoggerFactory
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.stereotype.Component

@Component
class MPClientFallbackFactory : FallbackFactory<MPClient> {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    var throwableThreadLocal = ThreadLocal<Throwable>()
    override fun create(cause: Throwable): MPClient {
        if (log.isInfoEnabled()) {
            log.info("call mp client service fail: {}", cause.message)
        }
        if (log.isDebugEnabled()) {
            cause.printStackTrace()
        }
        throwableThreadLocal.set(cause)
        return mpClient
    }

    private val mpClient = object : MPClient {

        override fun codeStatus(module: MobileCodeModuleEnum, mobile: String, code: String): BaseResult {
            throw RuntimeException("the call to the mp service failed", throwableThreadLocal.get().cause)
        }

        override fun sendSmsCode(module: MobileCodeModuleEnum, mobile: String): BaseResult {
            throw RuntimeException("the call to the mp service failed", throwableThreadLocal.get().cause)
        }

        override fun sendSmsPwd(module: MobileCodeModuleEnum, mobile: String, code: String): BaseResult {
            throw RuntimeException("the call to the mp service failed", throwableThreadLocal.get().cause)
        }

        override fun sendEmail(title: String, content: String, emails: MutableList<String>): JsonResult {
            throw RuntimeException("the call to the mp service failed", throwableThreadLocal.get().cause)
        }

        override fun res(lang: String, productLineCode: String, key: String): String {
            throw RuntimeException("the call to the mp service failed", throwableThreadLocal.get().cause)
        }
    }
}
