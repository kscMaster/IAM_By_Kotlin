package nancal.iam.mvc.ldap

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import nancal.iam.annotation.BizLog
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.TenantDepartmentInfo
import nancal.iam.db.mongo.entity.ldap.IdentitySyncRiskData
import nbcp.comm.*
import nbcp.db.IdName
import nbcp.db.mongo.*
import nbcp.web.UserId
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

/**
 * Created by CodeGenerator at 2022-02-22 19:55:18
 */
@Api(description = "identitySyncRiskData", tags = arrayOf("IdentitySyncRiskData"))
@RestController
@RequestMapping("/tenant/identity-sync-risk-data")
class IdentitySyncRiskDataController {

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        status: SyncJobRiskDataStatusEnum,
        id: String, //当列表列新一条后，刷新时使用
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<IdentitySyncRiskData> {
        val tenant = request.LoginTenantAdminUser.tenant

        mor.tenant.identitySyncRiskData.query()
            .where { it.status match status }
            .where { it.tenant.id match tenant.id }
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
            }
            .orderByDesc { it.status }
            .orderByDesc { it.createAt }
            .limit(skip, take)
            .toListResult()
            .apply {
                return this
            }
    }

    @ApiOperation("详情")
    @PostMapping("/detail/{id}")
    fun detail(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<IdentitySyncRiskData> {
        val tenant = request.LoginTenantAdminUser.tenant

        mor.tenant.identitySyncRiskData.queryById(id)
            .where { it.tenant.id match tenant.id }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }
                return ApiResult.of(this)
            }
    }

    @BizLog(action = BizLogActionEnum.Cancel, resource = BizLogResourceEnum.SyncRiskData, module = "风险数据")
    @ApiOperation("取消")
    @PostMapping("/cancel/{id}")
    fun cancel(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "风险数据取消"

        val tenant = request.LoginTenantAdminUser.tenant

        mor.tenant.identitySyncRiskData.queryById(id)
            .where { it.tenant.id match tenant.id }
            .toEntity()
            .apply {
                if (this == null) {
                    return JsonResult.error("找不到数据")
                }
                if (this.status != SyncJobRiskDataStatusEnum.Unenforced) {
                    return JsonResult.error("数据${this.status.remark}，不可以再次操作")
                }
            }

        mor.tenant.identitySyncRiskData.updateById(id)
            .set { it.status to SyncJobRiskDataStatusEnum.Canceled }
            .set { it.updateAt to LocalDateTime.now() }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("取消失败")
                }
                return JsonResult()
            }
    }

    @BizLog(action = BizLogActionEnum.Execute, resource = BizLogResourceEnum.SyncRiskData, module = "风险数据")
    @ApiOperation("执行")
    @PostMapping("/execute/{id}")
    fun execute(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "风险数据执行"

        val tenant = request.LoginTenantAdminUser.tenant

        mor.tenant.identitySyncRiskData.queryById(id)
            .where { it.tenant.id match tenant.id }
            .toEntity()
            .apply {
                if (this == null) {
                    return JsonResult.error("找不到数据")
                }
                if (this.status != SyncJobRiskDataStatusEnum.Unenforced) {
                    return JsonResult.error("数据${this.status.remark}，不可以再次操作")
                }

                if (this.objectType == SyncJobDataObjectTypeEnum.User) {
                    //删除用户信息
                    mor.tenant.tenantUser
                        .deleteById(this.objectData.id)
                        .exec()
                    //删除用户登录信息
                    mor.tenant.tenantLoginUser
                        .deleteByUserId(this.objectData.id)
                        .exec()
                    //删除关联的部门负责人
                    mor.tenant.tenantDepartmentInfo.update()
                        .where { it.manager.id match this.objectData.id }
                        .set { it.manager to mutableListOf<IdName>() }
                        .exec()
                } else if (this.objectType == SyncJobDataObjectTypeEnum.Dept) {
                    //部门下有子部门不能删除
                    mor.tenant.tenantDepartmentInfo.query()
                        .where { it.parent.id match this.objectData.id }
                        .exists()
                        .apply {
                            if (this) {
                                return JsonResult.error("该部门下存在子部门，无法删除")
                            }
                        }
                    //部门下有用户不能删除
                    mor.tenant.tenantUser.query()
                        .where { it.depts.id match id }
                        .exists()
                        .apply {
                            if (this) {
                                return JsonResult.error("该部门下存在用户，无法删除")
                            }
                        }
                    //删除部门
                    mor.tenant.tenantDepartmentInfo.deleteById(this.objectData.id).exec()
                }
            }

        mor.tenant.identitySyncRiskData.updateById(id)
            .set { it.status to SyncJobRiskDataStatusEnum.Executed }
            .set { it.updateAt to LocalDateTime.now() }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("执行失败")
                }
                return JsonResult()
            }
    }

}