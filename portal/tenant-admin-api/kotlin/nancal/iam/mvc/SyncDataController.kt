package nancal.iam.mvc

import nbcp.comm.JsonMap
import nbcp.comm.ListResult
import nbcp.comm.OpenAction
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nbcp.web.LoginUser
import org.bson.Document
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@OpenAction
@RestController
class SyncDataController {
    @GetMapping("/sys/sync-data/{table}")
    fun syncData(
        table: String,
        lastSyncAt: LocalDateTime,
        skip: Int,      //pageNumber
        take: Int = 50, //pageSize
        request: HttpServletRequest
    ): ListResult<Document> {
        var allowList = listOf(
            mor.tenant.tenant, //只能同步本租户数据
            mor.tenant.tenantUser,
            mor.tenant.tenantAppRole,
            mor.tenant.tenantDepartmentInfo,
            mor.tenant.tenantUserGroup
        )

        if (allowList.map { it.tableName }.contains(table) == false) {
            return ListResult.error("找不到要同步的表")
        }


        return mor.getCollection(table)!!.query()
            .where { "tenant.id" match request.LoginUser.organization.id }
            .where { MongoColumnName("updateAt") match_gte lastSyncAt }
            .limit(skip, take)
            .toMapListResult()
    }
}