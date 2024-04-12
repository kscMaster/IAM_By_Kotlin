package nancal.iam.mvc.admin

import io.swagger.annotations.*
import nancal.iam.annotation.BizLog
import org.springframework.data.mongodb.core.query.*
import org.springframework.web.bind.annotation.*
import nancal.iam.base.extend.*
import nancal.iam.comm.logMsg
import nbcp.comm.*
import nbcp.db.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.mongo.entity.iam.SysAppAuthResource
import nbcp.utils.CodeUtil
import nbcp.base.mvc.*
import nbcp.web.*
import javax.servlet.http.*
import java.time.*
import javax.annotation.Resource

/**
 * Created by CodeGenerator at 2021-12-18 10:35:32
 */
@Api(description = "应用系统角色", tags = arrayOf("SysAppRole"))
@RestController
@RequestMapping("/admin/sys-app-role")
class SysAppRoleController {

    @Resource
    lateinit var authController: SysAppAuthResourceController

    class SysAppRoleVO(
        @Cn("授权")
        var auths: MutableList<AuthResourceInfo> = mutableListOf(),
    ) : SysAppRole()

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        name: String,
        keywords: String,
        appCode: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<SysAppRole> {
        if (!appCode.HasValue) {
            return ListResult.error("appCode不能是空")
        }
        mor.iam.sysAppRole.query()
            .where { it.appInfo.code match appCode }
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (keywords.HasValue) {
                    this.where { it.name match_like keywords }
                }
                if (name.HasValue) {
                    this.where { it.name match_like name }
                }
            }
            .limit(skip, take).orderByDesc { it.createAt }
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
    ): ApiResult<SysAppRole> {
        mor.iam.sysAppRole.queryById(id)
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }

                return ApiResult.of(this)
            }
    }

    @BizLog(BizLogActionEnum.Save, BizLogResourceEnum.AppRole, "应用角色")
    @ApiOperation("初始化应用默认数据")
    @PostMapping("/init-define-data")
    fun init(
        @JsonModel entity: SysAppRoleVO,
        request: HttpServletRequest
    ): ApiResult<String> {
        request.logMsg = "初始化应用默认角色,应用{${entity.appInfo.name}}，角色{${entity.name}}"

        val result: ApiResult<String>

        //保存角色
        val sysAppRole = mor.iam.sysAppRole.queryByAppInfoCodeName(entity.appInfo.code, entity.name).toEntity()
        if (sysAppRole == null) {
            result = save(entity, request)
        } else {
            result = ApiResult.of(sysAppRole.id)
        }

        //查询资源
        val codes = entity.auths.map { it.code }
        val resources = mor.iam.sysResourceInfo.query()
            .select("id", "name")
            .where { it.appInfo.code match entity.appInfo.code }
            .where { it.code match_in codes }
            .toList()

        //auth设置资源ID
        val auths: MutableList<AuthResourceInfo> = mutableListOf()
        var filter: List<SysResourceInfo>
        entity.auths.forEach { auth ->
            filter = resources.filter { it.code == auth.code }
            if (filter.any()) {
                auth.id = CodeUtil.getCode()
                auth.resourceId = filter.first().id
                auth.isAllow = true
                auth.actionIsAll = true
                auth.action.add("*")
                auths.add(auth)
            }else{
                println("找不到资源code：{${auth.code}}")
            }
        }

        //保存授权
        var authResource = mor.iam.sysAppAuthResource.query()
            .where { it.appInfo.code match entity.appInfo.code }
            .where { it.target.name match entity.name }
            .orderByAsc { it.createAt }
            .toEntity()

        if (authResource != null){
            authResource.auths = auths
        }else{
            authResource = SysAppAuthResource()
            authResource.appInfo = entity.appInfo
            authResource.auths = auths
            authResource.target = IdName(entity.id, entity.name)
        }
        request.setAttribute("[Request.PostJson]", authResource.ToJson().FromJson<JsonMap>())
        authController.save(authResource, request)

        return result
    }


    @BizLog(BizLogActionEnum.Save, BizLogResourceEnum.AppRole, "应用角色")
    @ApiOperation("更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: SysAppRole,
        request: HttpServletRequest
    ): ApiResult<String> {
        request.logMsg = "保存应用角色,应用{${entity.appInfo.name}}，角色{${entity.name}}"

        if (entity.name.isEmpty()) {
            return ApiResult.error("应用名称不能为空")
        }
        if (entity.name.length > 32) {
            return ApiResult.error("应用名称长度不能大于32")
        }
        if (entity.remark.length > 255) {
            return ApiResult.error("备注长度不能大于255")
        }

        if (entity.id.HasValue) {
            val appRole = mor.iam.sysAppRole.queryById(entity.id).toEntity()
            if (appRole == null){
                return ApiResult.error("找不到角色")
            }
            if (entity.appInfo.code != appRole.appInfo.code){
                return ApiResult.error("应用与角色不匹配")
            }
        }

        mor.iam.sysAppRole.query()
            .where { it.id match_not_equal entity.id }
            .where { it.appInfo.code match entity.appInfo.code }
            .where { it.name match entity.name }
            .toEntity()
            .apply {
                if (this != null) {
                    return ApiResult.error("角色code已存在")
                }
            }

        mor.tenant.tenantAppRole.query()
            .apply {
                if (entity.id.HasValue) {
                    this.where { it.sysId match_not_equal entity.id }
                }
            }
            .where { it.appInfo.code match entity.appInfo.code }
            .where { it.name match entity.name }
            .toEntity()
            .apply {
                if (this != null) {
                    return ApiResult.error("角色code在已授权应用的租户中已存在")
                }
            }

        mor.iam.sysApplication.query()
            .where { it.appCode match entity.appInfo.code }
            .toEntity()
            .apply {
                if(this == null){
                    return ApiResult.error("应用不存在")
                }
            }


        var isInsert = false
        mor.iam.sysAppRole.updateWithEntity(entity)
            
            .run {
                if (entity.id.HasValue) {
                    return@run this.execUpdate()
                } else {
                    isInsert = true
                    return@run this.execInsert()
                }
            }
            .apply {
                if (this == 0) {
                    return ApiResult.error("更新失败")
                }
                if (isInsert) {
                    mor.tenant.tenantApplication.query()
                        .where { it.appCode match entity.appInfo.code }
                        .toList()
                        .apply {
                            if (this.isNotEmpty()) {
                                var tenantAppRole: TenantAppRole
                                val list = mutableListOf<TenantAppRole>()

                                this.forEach {
                                    tenantAppRole = TenantAppRole(
                                        entity.appInfo,
                                        it.tenant,
                                        entity.name,
                                        true,
                                        entity.id,
                                        entity.remark
                                    )
                                    list.add(tenantAppRole)
                                }

                                mor.tenant.tenantAppRole.batchInsert()
                                    .apply {
                                        addEntities(list)
                                    }
                                    .exec()
                            }
                        }
                } else {
                    mor.tenant.tenantAppRole.query()
                        .where { it.appInfo.code match entity.appInfo.code }
                        .where { it.sysId match entity.id }
                        .toList()
                        .apply {
                            if (this.isNotEmpty()) {
                                this.forEach {
                                    it.name = entity.name
                                    it.remark = entity.remark
                                    mor.tenant.tenantAppRole.updateWithEntity(it).execUpdate()
                                }
                            }
                        }
                }

                return ApiResult.of(entity.id)
            }
    }

    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.AppRole, "应用角色")
    @ApiOperation("批量删除")
    @PostMapping("/delete/batch")
    fun deleteBatch(
        @Require appCode: String,
        @Require ids: List<String>,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "批量删除应用角色"

        if (appCode.isEmpty()){
            return JsonResult.error("appCode不能为空")
        }
        if (ids.isEmpty()){
            return JsonResult.error("id不能为空")
        }
        mor.iam.sysApplication.query()
            .where { it.appCode match appCode }
            .exists()
            .apply {
                if (!this){
                    return JsonResult.error("找不到应用")
                }
            }
        mor.iam.sysAppRole.query()
            .where { it.appInfo.code match appCode }
            .where { it.id match_in ids }
            .count()
            .apply {
                if (this == 0){
                    return JsonResult.error("找不到角色数据")
                }
            }

        //删除角色
        mor.iam.sysAppRole.delete()
            .where { it.appInfo.code match appCode }
            .where { it.id match_in ids }
            .exec()

        //删除授权
        mor.iam.sysAppAuthResource.delete()
            .where { it.appInfo.code match appCode }
            .where { it.target.id match_in ids }
            .exec()

        //查询租户下角色ID
        val tenantRoleIds = mor.tenant.tenantAppRole.query()
            .select { it.id }
            .where { it.appInfo.code match appCode }
            .where { it.sysId match_in ids }
            .toList(String::class.java)

        //删除租户下的角色
        mor.tenant.tenantAppRole.delete()
            .where { it.appInfo.code match appCode }
            .where { it.sysId match_in ids }
            .exec()

        //删除租户下的授权
        mor.tenant.tenantAppAuthResourceInfo.delete()
            .where { it.appInfo.code match appCode }
            .where { it.target.id match_in tenantRoleIds }
            .exec()

        return JsonResult()
    }
}