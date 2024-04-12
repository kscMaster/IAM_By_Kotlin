package nancal.iam.client

import nbcp.comm.JsonResult
import nancal.iam.db.mongo.MobileCodeModuleEnum
import nancal.iam.dto.LezaoMessageDTO
import nbcp.comm.ApiResult
import nbcp.comm.JsonModel
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest

/**
 * @Classname ECClient
 * @Description sync ec apis
 * @Version 1.0.0
 * @Date 4/12/2021 上午 11:40
 * @Created by kxp
 */
@FeignClient(
//    name = "remoteIamTenantUserService",
    name = "mp-tenant-admin-api",
//    name = "http://192.168.5.213/api/mp-tenant-admin-api",
    fallback = TenantAdminClientFallbackFactory::class
)
interface TenantAdminClient {

    @PostMapping(path = ["/tenant/message/save"])
    fun save(@JsonModel entity: LezaoMessageDTO): BaseResult

}
