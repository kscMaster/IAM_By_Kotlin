package nancal.iam.client

import nbcp.comm.JsonResult
import nancal.iam.db.mongo.MobileCodeModuleEnum
import nancal.iam.dto.LezaoMessageDTO
import nbcp.comm.ApiResult
import org.slf4j.LoggerFactory
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class TenantAdminClientFallbackFactory : FallbackFactory<TenantAdminClient> {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    var throwableThreadLocal = ThreadLocal<Throwable>()
    override fun create(cause: Throwable): TenantAdminClient {
        if (log.isInfoEnabled()) {
            log.info("call mp client service fail: {}", cause.message)
        }
        if (log.isDebugEnabled()) {
            cause.printStackTrace()
        }
        throwableThreadLocal.set(cause)
        return tenantAdminClient
    }

    private val tenantAdminClient = object : TenantAdminClient {
        override fun save(entity: LezaoMessageDTO): BaseResult {
            TODO("Not yet implemented")
        }


    }
}
