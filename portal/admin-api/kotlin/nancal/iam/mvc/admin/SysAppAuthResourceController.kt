package nancal.iam.mvc.admin

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import nancal.iam.annotation.BizLog
import nancal.iam.comm.logMsg
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.mongo.entity.iam.SysAppAuthResource
import nancal.iam.util.CodeUtils
import nbcp.comm.*
import nbcp.db.BaseEntity
import nbcp.db.Cn
import nbcp.db.CodeName
import nbcp.db.IdName
import nbcp.db.mongo.*
import nbcp.utils.CodeUtil
import org.springframework.beans.BeanUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@Api(description = "资源授权", tags = arrayOf("SysAppAuthResource"))
@RestController
@RequestMapping("/admin/sys-auth-resource")
class SysAppAuthResourceController {

    class SysAppAuthResourceDetailVO(
        var role: SysAppRole? = null
    ) : SysAppAuthResource()

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        name: String,
        appCode: String,
        roleName: String,
        resourceName: String,
        keywords: String,
        parentCode: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<SysAppAuthResource> {
        if (!appCode.HasValue) {
            return ListResult.error("appCode不能是空")
        }
        mor.iam.sysAppAuthResource.query()
            .where { it.appInfo.code match appCode }

            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (keywords.HasValue) {
                    this.whereOr({ it.auths.name match_like keywords }, { it.target.name match_like keywords })
                }
                if (resourceName.HasValue) {
                    this.where { it.auths.name match_like resourceName }
                }
                if (roleName.HasValue) {
                    this.where { it.target.name match_like roleName }
                }
            }
            .limit(skip, take).orderByDesc { it.createAt }
            .toListResult()
            .apply {
                return this
            }
    }

    class DetailVO : BaseEntity() {

        var role: SysAppRole? = null

        @Cn("应用IdName")
        var appInfo: CodeName = CodeName()

        @Cn("授权主体类型")
        var type: AuthTypeEnum = AuthTypeEnum.Role

        @Cn("授权主体")
        var target: IdName = IdName()

        @Cn("授权")
        var auths: MutableList<AuthResourceInfoVOTemp> = mutableListOf()

    }

    class AuthResourceInfoVOTemp : ResourceBaseInfo() {
        var id: String = "" //授权Id,唯一标识
        var resourceId: String = "" //资源Id
        var actionIsAll: Boolean = false
        var resourceIsAll: Boolean = false
        var isAllow: Boolean = false
        var rules: MutableList<TenantAuthRule> = mutableListOf()
        var parentResource: MutableList<SysResourceInfo> = mutableListOf()
        var actionOfResource: MutableList<LabelValue> = mutableListOf()
    }

    class LabelValue(toString: String, toString1: String) {
        var label: String = toString
        var value: String = toString1
    }


    @ApiOperation("详情")
    @PostMapping("/detail/{id}")
    fun detail(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<DetailVO> {
        mor.iam.sysAppAuthResource.queryById(id)
            .toEntity(DetailVO::class.java)
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }

                val role = mor.iam.sysAppRole.queryById(this.target.id).unSelect { it.appInfo }.toEntity()
                this.role = role!!
                this.auths.forEach { vo ->
//                    if (vo.action.isEmpty()){
//                        vo.action.add("*")
//                    }
                    mor.iam.sysResourceInfo.query().where { it.id match vo.resourceId }.toEntity()
                        ?.apply {
                            var actionList = mutableListOf<LabelValue>()
                            this.action.forEach { actionObj ->
                                actionList.add(LabelValue(actionObj, actionObj))
                            }
                            vo.actionOfResource = actionList
                            vo.parentResource = listResourceParent(this.code, this.appInfo.code)
                        }
                }
                return ApiResult.of(this)
            }
    }

    fun listResourceParent(code: String, appCode: String): MutableList<SysResourceInfo> {
        val result: MutableList<SysResourceInfo> = mutableListOf()
        val codeList: MutableList<String> = CodeUtils.codeHolder(code)
        if (codeList.size == 1) {
            val vo = SysAppResourceController.SysResourceDetail()
            mor.iam.sysResourceInfo.query()
                .where { it.appInfo.code match appCode }
                .where { it.code match code }.toEntity()
                .let {
                    if (it != null) {
                        result.add(it)
                    }
                }

            return result
        } else {
            codeList.forEach { item ->
                val toEntity: SysResourceInfo? = mor.iam.sysResourceInfo.query()
                    .where { it.appInfo.code match appCode }
                    .where { it.code match item }.toEntity()
                toEntity?.let {
                    result.add(it)
                }
            }
            return result

        }
    }

    @BizLog(BizLogActionEnum.Save, BizLogResourceEnum.AppAuthResource, "应用资源授权")
    @ApiOperation("保存资源授权")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: SysAppAuthResource,
        request: HttpServletRequest
    ): ApiResult<String> {
        request.logMsg = "保存应用资源授权"

        if (entity.target.id.isEmpty()) {
            return ApiResult.error("授权主体不能为空")
        }
        if (entity.auths.isEmpty()) {
            return ApiResult.error("授权资源不能为空")
        }
        mor.iam.sysApplication.query()
            .where { it.appCode match entity.appInfo.code }
            .exists()
            .apply {
                if (!this) {
                    return ApiResult.error("找不到应用")
                }
            }
        //验证角色
        mor.iam.sysAppRole.query()
            .where { it.id match entity.target.id }
            .where { it.appInfo.code match entity.appInfo.code }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到角色")
                }
                entity.target.name = this.name
            }

        //验证资源、移除错误的资源，并填充资源ID和资源授权ID
        mor.iam.sysResourceInfo.query()
            .where { it.appInfo.code match entity.appInfo.code }
            .apply {
                val map = entity.auths.map { it.code }
                if (!map.contains("*")) {
                    this.whereOr(
                        { it.code match_in entity.auths.map { it.code } },
                        { it.id match_in entity.auths.map { it.resourceId } }
                    )
                }
            }
            .toList()
            .apply {
//                if (this.isEmpty()) {
//                    return ApiResult.error("找不到资源")
//                }

                val auths = mutableListOf<AuthResourceInfo>()
                entity.auths.forEach { auth ->
                    if (auth.id.isEmpty()) {
                        auth.id = CodeUtil.getCode()
                    }

                    if (auth.resourceIsAll == true) {
                        auth.resourceId = ""
                        auth.code = "*"
                        auth.actionIsAll = true

                    } else {
                        if (auth.resourceId.equals("") || auth.name.equals("")) {
                            return ApiResult.error("指定资源需要关联id或名称")
                        }

                        mor.iam.sysResourceInfo.query()
                            .where { it.id match auth.resourceId }
                            .where { it.name match auth.name }
                            .toEntity()
                            .apply {
                                if (this == null) {
                                    return ApiResult.error("指定资源id或名称有误")
                                }
                            }
                    }

                    if (auth.actionIsAll) {
                        var emptyList = mutableListOf<String>()
                        emptyList.add("*")
                        auth.action = emptyList
                    }

                    if (auth.resourceId.HasValue) {
                        this.filter { it.id == auth.resourceId || it.code == auth.code }
                            .apply {
                                if (this.isNotEmpty()) {
                                    auth.resourceId = this.get(0).id
                                    auth.code = this.get(0).code
                                    auth.name = this.get(0).name
                                    auth.type = this.get(0).type

                                    if (auth.action.none { it == "*" }) {
                                        auth.action = this.get(0).action.intersect(auth.action).toMutableList()
                                    }
//                                    if (auth.action.isEmpty()){
//                                        auth.action.add("*")
//                                    }

                                    auths.add(auth)
                                }
                            }
                    } else {
                        auth.name = ""
                        auth.type = null
                        auths.add(auth)
                    }
                }
                entity.auths = auths
            }

        var isInsert = false
        mor.iam.sysAppAuthResource.updateWithEntity(entity)
            .run {
                if (entity.id.HasValue) {
                    request.logMsg = "修改应用资源授权{${entity.target.name}}"
                    return@run this.execUpdate()
                } else {
                    request.logMsg = "新增应用资源授权{${entity.target.name}}"
                    isInsert = true
                    return@run this.execInsert()
                }
            }
            .apply {
                if (this == 0) {
                    return ApiResult.error("更新失败")
                }

                //保存租户授权
                saveTenantAppAuthResource(entity, isInsert)

                return ApiResult.of(entity.id)
            }
    }

    /**
     * 保存租户授权
     */
    fun saveTenantAppAuthResource(
        entity: SysAppAuthResource,
        isInsert: Boolean
    ) {
        //1.查询本应用关联的租户应用
        mor.tenant.tenantApplication.query()
            .where { it.appCode match entity.appInfo.code }
            .toList()
            .apply {
                //2.如果有关联的租户就去查询租户侧本应用的资源和角色
                if (this.isNotEmpty()) {
                    //2.1查询本应用的租户侧的资源
                    val resources = mor.tenant.tenantResourceInfo.query()
                        .select("id", "name", "code", "tenant")
                        .where { it.appInfo.code match entity.appInfo.code }
                        .apply {
                            val map = entity.auths.map { it.code }
                            if (!map.contains("*")) {
                                this.whereOr(
                                    { it.code match_in entity.auths.map { it.code } },
                                )
                            }
                        }
                        .toList()

                    //2.2查询本应用的租户侧的角色
                    val roles = mor.tenant.tenantAppRole.query()
                        .where { it.appInfo.code match entity.appInfo.code }
                        .where { it.name match entity.target.name }
                        .toList()

                    //3.保存租户侧授权，需要将角色ID、资源ID改为租户侧的
                    var role: TenantAppRole
                    var auths: MutableList<AuthResourceInfo>
                    this.forEach continuing@{ tenantApp ->
                        //3.1查找本租户下的角色
                        roles.filter { it.tenant.id == tenantApp.tenant.id }.apply {
                            if (this.isNotEmpty()) {
                                role = this.first()
                            } else {
                                //考虑异常数据的情况
                                return@continuing
                            }
                        }

                        //3.2查找本租户下的资源ID
                        auths = mutableListOf()
                        entity.auths.forEach { auth ->
                            resources.filter { it.code == auth.code && it.tenant.id == tenantApp.tenant.id }
                                .apply {
                                    if (auth.code.contains("*")) {
                                        auths.add(auth)
                                    } else if (this.isNotEmpty()) {
                                        auth.resourceId = this.get(0).id
                                        auth.name = this.get(0).name
                                        auths.add(auth)
                                    }
                                }
                        }

                        //3.3保存租户侧授权
                        if (isInsert) {
                            //新增租户侧授权
                            mor.tenant.tenantAppAuthResourceInfo.doInsert(
                                TenantAppAuthResourceInfo(
                                    entity.appInfo,
                                    tenantApp.tenant,
                                    entity.type,
                                    IdName(role.id, role.name),
                                    auths,
                                    false,
                                    true,
                                    entity.id
                                )
                            )
                        } else {
                            //更新租户侧授权
                            mor.tenant.tenantAppAuthResourceInfo.query()
                                .where { it.sysId match entity.id }
                                .where { it.tenant.id match tenantApp.tenant.id }
                                .where { it.appInfo.code match tenantApp.appCode }
                                .toEntity()
                                .apply {
                                    if (this != null) {
                                        this.auths = auths
                                        this.target = IdName(role.id, role.name)
                                        mor.tenant.tenantAppAuthResourceInfo.updateWithEntity(this)
                                            .execUpdate()
                                    }
                                }
                        }
                    }
                }
            }
    }

    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.AppAuthResource, "应用资源授权")
    @ApiOperation("批量删除")
    @PostMapping("/delete/batch")
    fun deleteBatch(
        @Require appCode: String,
        @Require ids: List<String>,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "批量删除应用资源授权"

        if (appCode.isEmpty()) {
            return JsonResult.error("appCode不能为空")
        }
        if (ids.isEmpty()) {
            return JsonResult.error("id不能为空")
        }
        mor.iam.sysApplication.query()
            .where { it.appCode match appCode }
            .exists()
            .apply {
                if (!this) {
                    return JsonResult.error("找不到应用")
                }
            }
        mor.iam.sysAppAuthResource.query()
            .where { it.appInfo.code match appCode }
            .where { it.id match_in ids }
            .count()
            .apply {
                if (this == 0) {
                    return JsonResult.error("找不到授权数据")
                }
            }

        //删除授权
        mor.iam.sysAppAuthResource.delete()
            .where { it.id match_in ids }
            .exec()

        //删除租户授权
        mor.tenant.tenantAppAuthResourceInfo.delete()
            .where { it.sysId match_in ids }
            .exec()

        return JsonResult()
    }
}