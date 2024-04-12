package nancal.iam.mvc.tenant

import io.swagger.annotations.*
import nancal.iam.annotation.BizLog
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nancal.iam.db.mongo.AuthTypeEnum
import nancal.iam.db.mongo.BizLogActionEnum
import nancal.iam.db.mongo.BizLogResourceEnum
import nancal.iam.db.mongo.entity.AuthResourceGroup
import nancal.iam.db.mongo.entity.TenantAuthResourceGroup
import nancal.iam.db.mongo.mor
import org.springframework.data.mongodb.core.query.*
import org.springframework.web.bind.annotation.*
import nbcp.db.mongo.*
import nbcp.db.mongo.entity.*
import nbcp.comm.*
import nbcp.db.*
import nbcp.base.mvc.*
import nbcp.web.*
import javax.servlet.http.*
import java.time.*

/**
 * Created by CodeGenerator at 2022-03-10 20:30:59
 */
@Api(description = "资源组授权", tags = arrayOf("TenantAuthResourceGroup"))
@RestController
@RequestMapping("/tenant/tenant-auth-resource-group")
class TenantAuthResourceGroupController {

    class TenantAuthResourceGroupVO(
        @Cn("授权主体类型")
        var type: AuthTypeEnum,

        @Cn("授权主体集合")
        var targetList: MutableList<IdName> = mutableListOf(),

        @Cn("授权资源组")
        var auths: MutableList<AuthResourceGroup> = mutableListOf(),

        @Cn("子部门是否授权，只在type=Dept时用")
        var childDeptsAll: Boolean = true
    )

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantAuthResourceGroup> {
        val tenant = request.LoginTenantAdminUser.tenant

        mor.tenant.tenantAuthResourceGroup.query()
            .where { it.tenant.id match tenant.id }
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
            }
            .limit(skip, take).orderByDesc { it.createAt }
            .toListResult()
            .apply {
                return this;
            }
    }

    @ApiOperation("详情")
    @PostMapping("/detail/{id}")
    fun detail(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<TenantAuthResourceGroup> {
        val tenant = request.LoginTenantAdminUser.tenant

        mor.tenant.tenantAuthResourceGroup.queryById(id)
            .where { it.tenant.id match tenant.id }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }

                return ApiResult.of(this)
            }
    }

    // 只做新增，授权主体没有角色
    @BizLog(BizLogActionEnum.Add, BizLogResourceEnum.AuthResourceGroup, "资源组授权")
    @ApiOperation("更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entityVO: TenantAuthResourceGroupVO,
        request: HttpServletRequest
    ): ApiResult<String> {
        request.logMsg = "新增资源组授权"

        val tenant = request.LoginTenantAdminUser.tenant

        if (entityVO.targetList.isEmpty()) {
            return ApiResult.error("授权主体不能为空")
        }
        if (entityVO.auths.isEmpty()) {
            return ApiResult.error("授权资源组不能为空")
        }
        if (entityVO.type == AuthTypeEnum.Dept) {
            if (entityVO.targetList.size > 1) {
                return ApiResult.error("部门只能选一个")
            }
            mor.tenant.tenantDepartmentInfo.queryById(entityVO.targetList.first().id)
                .where { it.tenant.id match tenant.id }
                .exists()
                .apply {
                    if (!this) {
                        return ApiResult.error("部门在当前租户下找不到")
                    }
                }
        }
        if (entityVO.type == AuthTypeEnum.Role) {
            return ApiResult.error("资源组不支持角色授权")
        }
        if (entityVO.type == AuthTypeEnum.People) {
            mor.tenant.tenantUser.query()
                .select { it.name }
                .where { it.tenant.id match tenant.id }
                .where { it.id match_in entityVO.targetList.map { it.id } }
                .toList(IdName::class.java)
                .apply {
                    if (this.isEmpty()) {
                        return ApiResult.error("用户在当前租户下找不到")
                    }
                    entityVO.targetList = this
                }
        }
        if (entityVO.type == AuthTypeEnum.Group) {
            mor.tenant.tenantUserGroup.query()
                .select { it.name }
                .where { it.tenant.id match tenant.id }
                .where { it.id match_in entityVO.targetList.map { it.id } }
                .toList(IdName::class.java)
                .apply {
                    if (this.isEmpty()) {
                        return ApiResult.error("用户组在当前租户下找不到")
                    }
                    entityVO.targetList = this
                }
        }
        mor.tenant.tenantResourceGroup.query()
            .select { it.code }
            .select { it.name }
            .where { it.tenant.id match tenant.id }
            .where { it.id match_in entityVO.auths.map { it.id } }
            .toList(IdCodeName::class.java)
            .apply {
                if (this.isEmpty()) {
                    return ApiResult.error("资源组在当前租户下找不到")
                }

                var first: IdCodeName
                val ids = this.map { it.id }
                entityVO.auths = entityVO.auths.filter { it.id in ids }.toMutableList()
                entityVO.auths.forEach { arg ->
                    first = this.filter { it.id == arg.id }.first()
                    arg.name = first.name
                    arg.code = first.code
                }
            }


        var result = ""
        var entity: TenantAuthResourceGroup
        entityVO.targetList.forEach { target ->
            entity = TenantAuthResourceGroup()
            entity.tenant = tenant
            entity.type = entityVO.type
            entity.target = target
            entity.auths = entityVO.auths
            entity.heredity = entityVO.childDeptsAll

            result += mor.tenant.tenantAuthResourceGroup.doInsert(entity) + ","
        }
        return ApiResult.of(result)
    }

    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.AuthResourceGroup, "资源组授权")
    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "删除资源组授权"

        val tenant = request.LoginTenantAdminUser.tenant

        mor.tenant.tenantAuthResourceGroup.queryById(id)
            .where { it.tenant.id match tenant.id }
            .toEntity() ?: return JsonResult.error("找不到数据")

        mor.tenant.tenantAuthResourceGroup.deleteById(id)
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
                //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
                return JsonResult()
            }
    }
}