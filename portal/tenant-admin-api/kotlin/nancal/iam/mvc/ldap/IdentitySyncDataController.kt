package nancal.iam.mvc.ldap

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.db.mongo.SyncJobDataObjectTypeEnum
import nancal.iam.db.mongo.entity.ldap.IdentitySyncData
import nancal.iam.db.mongo.mor
import nbcp.comm.*
import nbcp.db.mongo.match
import nbcp.db.mongo.query
import nbcp.db.mongo.queryById
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

/**
 * Created by CodeGenerator at 2022-02-22 19:42:03
 */
@Api(description = "identitySyncData", tags = arrayOf("IdentitySyncData"))
@RestController
@RequestMapping("/tenant/identity-sync-data")
class IdentitySyncDataController {

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        jobLogId: String,
        objectType: SyncJobDataObjectTypeEnum?,
        id: String, //当列表列新一条后，刷新时使用
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<IdentitySyncData> {
        val tenant = request.LoginTenantAdminUser.tenant

        mor.tenant.identitySyncJobLog.queryById(jobLogId)
            .where { it.tenant.id match tenant.id }
            .exists()
            .apply {
                if (!this){
                    return ListResult()
                }
            }

        mor.tenant.identitySyncData.query()
            .where { it.jobLogId match jobLogId }
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (objectType != null){
                    this.where { it.objectType match objectType }
                }
            }
//            .orderByAsc { it.result }
            .orderByDesc { it.createAt }
            //如果时间相同,按时间排序会出现数据重复,所以加一个id排序
            .orderByAsc { it.id }
            .limit(skip, take)
            .toListResult()
            .apply {
                return this;
            }
    }
}