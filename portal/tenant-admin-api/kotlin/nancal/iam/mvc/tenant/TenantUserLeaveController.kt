package nancal.iam.mvc.tenant

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import nancal.iam.annotation.BizLog
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nbcp.comm.*
import org.bson.Document
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

/**
 * Created by CodeGenerator at 2021-11-17 17:43:16
 */
@Api(description = "租户离职用户", tags = ["TenantUserLeave"])
@RestController
@RequestMapping("/tenant/tenant-user-leave")
class TenantUserLeaveController {

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        name: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantUserLeave> {
        mor.tenant.tenantUserLeave.query()
            .apply {
                this.where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
               if(name.HasValue){
                   this.whereOr (
                       { it.name match_like  name },
                       { it.loginName match_like name },
                       { it.mobile match_like name },
                       { it.email match_like name }
                   )
               }
            }
            .limit(skip, take)
            .orderByAsc { it.sort }
            .toListResult()
            .apply {
                return this
            }
    }


    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.User, "成员管理")
    @ApiOperation("批量删除")
    @PostMapping("/delete/batch")
    fun deleteBatch(
        @Require ids: List<String>,
        request: HttpServletRequest
    ): JsonResult {

        request.logMsg = "批量删除离职成员"
        mor.tenant.tenantUserLeave.query()
            .where { it.id match_in ids }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .count()
            .apply {
                if(this != ids.size){
                    return JsonResult.error("用户在当前租户下找不到")
                }
            }

        mor.tenant.tenantUserLeave.delete().where { it.id match_in ids }.exec()

        return JsonResult()
    }

    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.User, "成员管理")
    @ApiOperation("清空离职列表")
    @PostMapping("/deleteLeaveUserAll")
    fun clearLeaveUser(
        request: HttpServletRequest
    ): JsonResult {

        request.logMsg = "清空离职列表"
        mor.tenant.tenantUserLeave.delete()
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .exec()

        return JsonResult()
    }



}
