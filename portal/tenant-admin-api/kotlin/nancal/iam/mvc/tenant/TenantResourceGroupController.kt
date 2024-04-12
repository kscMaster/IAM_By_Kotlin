package nancal.iam.mvc.tenant

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import nancal.iam.annotation.BizLog
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nancal.iam.db.mongo.BizLogActionEnum
import nancal.iam.db.mongo.BizLogResourceEnum
import nancal.iam.db.mongo.entity.tenant.TenantResourceGroup
import nancal.iam.db.mongo.mor
import nbcp.comm.*
import nbcp.db.Cn
import nbcp.db.IdName
import nbcp.db.mongo.*
import nbcp.scope.JsonSceneEnumScope
import nbcp.scope.getJsonMapper
import nbcp.web.UserId
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/tenant/tenant-resource-group")
@Api(description = "资源分组", tags = arrayOf("TenantResourceGroup"))
class TenantResourceGroupController {

    class TenantResourceGroupVO: TenantResourceGroup(){
        @Cn("资源")
        var resources: MutableList<IdName> = mutableListOf()
    }

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        name: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantResourceGroup> {
        val tenant = request.LoginTenantAdminUser.tenant

        mor.tenant.tenantResourceGroup.query()
            .where { it.tenant.id match tenant.id }
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (name.HasValue) {
                    this.where { it.name match_like name }
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
    ): ApiResult<TenantResourceGroup> {
        val tenant = request.LoginTenantAdminUser.tenant

        mor.tenant.tenantResourceGroup.queryById(id)
            .where { it.tenant.id match tenant.id }
            .toEntity(TenantResourceGroupVO::class.java)
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }
                //查询关联资源
                this.resources = mor.tenant.tenantResourceInfo.query()
                    .where { it.tenant.id match tenant.id }
                    .where { it.groups.id match id }
                    .toList(IdName::class.java)

                return ApiResult.of(this)
            }
    }

    @BizLog(BizLogActionEnum.Save, BizLogResourceEnum.ResourceGroup, "资源组")
    @ApiOperation("更新")
    @PostMapping("/save")
    fun save(
        @JsonModel vo: TenantResourceGroupVO,
        request: HttpServletRequest
    ): ApiResult<String> {
        request.logMsg = "保存资源组"

        val tenant = request.LoginTenantAdminUser.tenant

        if (vo.resources.isEmpty()){
            return ApiResult.error("资源不能是空")
        }
        if (vo.name.length < 2 || vo.name.length > 32){
            return ApiResult.error("资源组名称2～32字符")
        }
        if (vo.code.length < 2 || vo.code.length > 32){
            return ApiResult.error("资源组编码2～32字符")
        }

        //同一租户下资源组名称不能重复
        mor.tenant.tenantResourceGroup.query()
            .where { it.tenant.id match tenant.id }
            .where { it.code match vo.code }
            .where { it.id match_not_equal  vo.id }
            .exists()
            .apply {
                if (this){
                    return ApiResult.error("资源组编码已存在")
                }
            }

        val ids = vo.resources.map { it.id }
        mor.tenant.tenantResourceInfo.query()
            .where { it.tenant.id match tenant.id }
            .where { it.id match_in ids }
            .exists()
            .apply {
                if (!this){
                    return ApiResult.error("资源在租户下不存在")
                }
            }
        if (vo.id.HasValue) {
            mor.tenant.tenantResourceGroup.queryById(vo.id)
                .toEntity()
                .apply {
                    if (this != null && this.tenant.id != tenant.id) {
                        return ApiResult.error("不可以修改其他租户的资源组")
                    }
                }
        }

        val entity = JsonSceneEnumScope.Db.getJsonMapper().convertValue(vo, TenantResourceGroup::class.java)
        entity.tenant = tenant

        var isUpdate = true
        mor.tenant.tenantResourceGroup.updateWithEntity(entity)
            .run {
                if (entity.id.HasValue) {
                    return@run this.execUpdate()
                } else {
                    isUpdate = false
                    return@run this.execInsert()
                }
            }
            .apply {
                if (this == 0) {
                    return ApiResult.error("更新失败")
                }
                if (isUpdate){
                    mor.tenant.tenantResourceInfo.update()
                        .where { it.tenant.id match tenant.id }
                        .where { it.groups.id match entity.id }
                        .pull({ it.groups }, MongoColumnName("id") match entity.id)
                        .exec()
                }
                //更新关联资源
                val group = IdName(entity.id, entity.name)
                mor.tenant.tenantResourceInfo.update()
                    .where { it.tenant.id match tenant.id }
                    .where { it.id match_in ids }
                    .push { it.groups to group }
                    .exec()

                return ApiResult.of(entity.id)
            }
    }

    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.ResourceGroup, "资源组")
    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "删除资源组"

        val tenant = request.LoginTenantAdminUser.tenant

        mor.tenant.tenantResourceGroup.queryById(id)
            .where { it.tenant.id match tenant.id }
            .toEntity() ?: return JsonResult.error("找不到数据")

        mor.tenant.tenantResourceGroup.deleteById(id)
            .where { it.tenant.id match tenant.id }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }

                //删除资源关联
                mor.tenant.tenantResourceInfo.update()
                    .where { it.tenant.id match tenant.id }
                    .where { it.groups.id match id }
                    .pull({ it.groups }, MongoColumnName("id") match id)
                    .exec()

                //删除授权关联，如果只有当前组同时删除授权
                mor.tenant.tenantAuthResourceGroup.query()
                    .where { it.tenant.id match tenant.id }
                    .where { it.auths.id match id }
                    .toList()
                    .apply {
                        if (this.any()){
                            this.forEach {
                                if (it.auths.size == 1){
                                    mor.tenant.tenantAuthResourceGroup.deleteById(it.id).exec()
                                }
                            }
                        }
                    }

                mor.tenant.tenantAuthResourceGroup.update()
                    .where { it.tenant.id match tenant.id }
                    .where { it.auths.id match id }
                    .pull({ it.auths }, MongoColumnName("id") match id)
                    .exec()

                return JsonResult()
            }
    }


}