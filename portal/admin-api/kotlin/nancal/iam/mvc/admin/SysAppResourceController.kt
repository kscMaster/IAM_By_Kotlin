package nancal.iam.mvc.admin

import io.swagger.annotations.*
import nancal.iam.annotation.BizLog
import org.springframework.web.bind.annotation.*
import nancal.iam.comm.logMsg
import nbcp.comm.*
import nbcp.db.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.util.CodeUtils.Companion.codeHolder
import nancal.iam.utils.List2TreeUtils
import org.springframework.beans.BeanUtils
import javax.servlet.http.*

/**
 * Created by CodeGenerator at 2021-12-18 10:41:25
 */
@Api(description = "应用资源", tags = arrayOf("SysResourceInfo"))
@RestController
@RequestMapping("/admin/sys-resource-info")
class SysAppResourceController {

    @ApiOperation("层级资源列表")
    @PostMapping("/listResource")
    fun listResource(
        id: String, //当列表列新一条后，刷新时使用
        name: String,
        code: String,
        appCode: String,
        keywords: String,
        parentCode: String,
        type: ResourceTypeEnum?,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest,
    ): ListResult<SysResourceInfo> {
        if (!appCode.HasValue) {
            return ListResult.error("appCode不能是空")
        }
        if (name.HasValue || code.HasValue || type != null || keywords.HasValue) {
            // 模糊查询
            mor.iam.sysResourceInfo.query()
                .where { it.appInfo.code match appCode }
                .apply {
                    if (id.HasValue) {
                        this.where { it.id match id }
                    }
                    if (keywords.HasValue) {
                        this.whereOr({ it.name match_like keywords }, { it.code match_like keywords })
                    }
                    if (name.HasValue) {
                        this.where { it.name match_like name }
                    }
                    if (code.HasValue) {
                        this.where { it.code match_like code }
                    }
                    if (type != null) {
                        this.where { it.type match type }
                    }
                }
                .limit(skip, take).orderByDesc { it.createAt }.orderByDesc { it.id }
                .toListResult()
                .apply {
                    return this
                }
        }
        // 父资源列表
        mor.iam.sysResourceInfo.query()
            .where { it.appInfo.code match appCode }
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (!parentCode.HasValue) {
                    this.where { it.code match_pattern "^[^:]+\$" }
                } else {
                    this.where { it.code match_pattern "^${parentCode}:[^:]+\$" }
                }
            }
            .limit(skip, take).orderByDesc { it.createAt }
            .toListResult()
            .apply {
                return this
            }

    }

    class SysResourceDetail {
        var id: String? = ""
        var appInfo: CodeName = CodeName()
        var parentCode: String? = "" // 父级code
        var name: String = "" //中文名称
        var code: String = ""
        var type: String = "" //资源类型
        var resource: String = "" //仅在Api时，定义为Url，其它类型 = name
    }

    @ApiOperation("资源详情包含父级")
    @PostMapping("/detailInfo")
    fun detailInfo(
        id: String,
        code: String,
        appCode: String,
        request: HttpServletRequest,
    ): ListResult<SysResourceDetail> {

        val result: MutableList<SysResourceDetail> = mutableListOf()
        if (!appCode.HasValue) {
            return ListResult.error("appCode不能是空")
        }
        if (!code.HasValue) {
            return ListResult.error("code不能是空")
        }
        var codeList: MutableList<String> = codeHolder(code)
        if (codeList.size == 1) {
            val vo = SysResourceDetail()
            mor.iam.sysResourceInfo.query()
                .where { it.appInfo.code match appCode }
                .where { it.code match code }.toEntity()
                .let {
                    if (it != null) {
                        BeanUtils.copyProperties(it, vo)
                    }
                }
            result.add(vo)
            return ListResult.of(result, result.size)
        } else {
            var codeStr = ""
            codeList.forEach { item ->
                val vo = SysResourceDetail()
                if (codeStr.HasValue) {
                    vo.parentCode = codeStr
                } else {
                    vo.parentCode = ""
                }
                codeStr = item
                val toEntity: SysResourceInfo? = mor.iam.sysResourceInfo.query()
                    .where { it.appInfo.code match appCode }
                    .where { it.code match codeStr }.toEntity()
                toEntity?.let {
                    BeanUtils.copyProperties(it, vo)
                    result.add(vo)
                }
            }


            return ListResult.of(result, result.size)
        }
    }

    class SysResourceTree {
        var id: String? = ""
        var appInfo: CodeName = CodeName()
        var parentCode: String? = "" // 父级code
        var name: String = "" //中文名称
        var code: String = ""
        var type: ResourceTypeEnum? = null
        var remark: String = ""
        var action: MutableList<String> = mutableListOf()
        var resource: String = "" //仅在Api时，定义为Url，其它类型 = name
        var children: MutableList<SysResourceTree>? = null // 默认值使用null用于前端不展示叶子节点图标
    }

    @ApiOperation("资源授权树")
    @PostMapping("/resource_tree")
    fun resourceTree(
        appCode: String,
        request: HttpServletRequest,
    ): ListResult<SysResourceTree> {
        if (!appCode.HasValue) {
            return ListResult.error("appCode不能是空")
        }
//        val result = getChildResource("", appCode)
        var result: MutableList<SysResourceTree> = list2Tree(appCode)
        return ListResult.of(result, result.size)
    }

    fun list2Tree(appCode: String): MutableList<SysResourceTree> {
        val toList: MutableList<SysResourceTree> = mor.iam.sysResourceInfo.query()
            .where { it.appInfo.code match appCode }
            .toList(SysResourceTree::class.java).toMutableList()
        return List2TreeUtils.list2Tree(toList).toMutableList()
    }

    fun getChildResource(parentCode: String, appCode: String): MutableList<SysResourceTree> {
        val result: MutableList<SysResourceTree> = mutableListOf() // a
        // 获取子资源
        val toMutableList: MutableList<SysResourceTree> = mor.iam.sysResourceInfo.query()
            .where { it.appInfo.code match appCode }
            .apply {
                if (!parentCode.HasValue) {
                    this.where { it.code match_pattern "^[^:]+\$" }
                } else {
                    this.where { it.code match_pattern "^${parentCode}:[^:]+\$" }
                }
            }.toList(SysResourceTree::class.java).toMutableList()
        return if (toMutableList.isEmpty()) { // a:b
            result
        } else {
            toMutableList.forEach {
                it.parentCode = parentCode
                result.add(it)
                val childResource: MutableList<SysResourceTree> = getChildResource(it.code, appCode)
                if (childResource.isEmpty()) {
                    it.children = null
                } else {
                    it.children = childResource
                }
            }
            result
        }

    }

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        name: String,
        code: String,
        appCode: String,
        type: ResourceTypeEnum?,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest,
    ): ListResult<SysResourceInfo> {
        if (!appCode.HasValue) {
            return ListResult.error("appCode不能是空")
        }
        // 父资源列表
        mor.iam.sysResourceInfo.query()
            .where { it.appInfo.code match appCode }
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (name.HasValue) {
                    this.where { it.name match_like name }
                }
                if (code.HasValue) {
                    this.where { it.code match_like code }
                }
                if (type != null) {
                    this.where { it.type match type }
                }
            }
            .limit(skip, take).orderByDesc { it.createAt }
            .toListResult()


            .apply {
                return this
            }

    }

    @ApiOperation("列表")
    @PostMapping("/list_temp")
    fun list_temp(
        nameOrCode: String,
        @Require appCode: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest,
    ): ListResult<SysResourceInfo> {
        if (appCode.isEmpty()) {
            return ListResult.error("appCode不能是空")
        }
        mor.iam.sysResourceInfo.query()
            .apply {
                if (appCode.HasValue) {
                    this.where { it.appInfo.code match appCode }
                }
                if (nameOrCode.HasValue) {
                    this.whereOr({ it.name match_like nameOrCode }, { it.code match_like nameOrCode })
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
        request: HttpServletRequest,
    ): ApiResult<SysResourceInfo> {
        mor.iam.sysResourceInfo.queryById(id)
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }

                return ApiResult.of(this)
            }
    }

    @ApiOperation("根据资源code查详情")
    @PostMapping("/detailByCodeOrId")
    fun detailByCodeOrId(
        id: String,
        code: String,
        appCode: String,
        request: HttpServletRequest,
    ): ApiResult<SysResourceInfo> {
        if (!id.HasValue && !code.HasValue) {
            return ApiResult.error("必须填写id和code中任意一项参数")
        }
        if (!id.HasValue && !appCode.HasValue) {
            return ApiResult.error("必须填写appCode")
        }
        mor.iam.sysResourceInfo.query()
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (code.HasValue) {
                    this.where { it.code match code }
                }
                if (appCode.HasValue) {
                    this.where { it.appInfo.code match appCode }
                }
            }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }

                return ApiResult.of(this)
            }
    }


    @BizLog(BizLogActionEnum.Save, BizLogResourceEnum.AppResource, "应用资源")
    @ApiOperation("初始化应用默认数据")
    @PostMapping("/init-define-data")
    fun init(
        @JsonModel entity: SysResourceInfo,
        request: HttpServletRequest,
    ): ApiResult<String> {
        request.logMsg = "初始化应用默认资源,应用{${entity.appInfo.name}},资源{${entity.name}}"

        mor.iam.sysResourceInfo.query()
            .where { it.appInfo.code match entity.appInfo.code }
            .where { it.code match it.code }
            .toEntity()
            .apply {
                if (this != null) {
                    entity.id = this.id
                }
            }

        return save(entity, request)
    }

    @BizLog(BizLogActionEnum.Save, BizLogResourceEnum.AppResource, "应用资源")
    @ApiOperation("更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: SysResourceInfo,
        request: HttpServletRequest,
    ): ApiResult<String> {
        request.logMsg = "保存应用资源,应用{${entity.appInfo.name}},资源{${entity.name}}"
        val code = entity.code
        val id = entity.id
        val dataType = entity.type
        var codeList: MutableList<String> = codeHolder(code)
        // code规则限制
        code.split(":").toMutableList().forEach {
            if (!it.HasValue) {
                return ApiResult.error("code格式错误")
            }
        }
        if (entity.type != ResourceTypeEnum.Data && entity.type != ResourceTypeEnum.Api
            && entity.type != ResourceTypeEnum.Menu && entity.type != ResourceTypeEnum.Ui
        ) {
            return ApiResult.error("资源类型错误")
        }
        if (codeList.size > 5) {
            return ApiResult.error("资源层级最多五层")
        }
        if (entity.name.isEmpty()) {
            return ApiResult.error("名称不能为空")
        } else if (entity.name.length > 300) {
            return ApiResult.error("名称不能大于300")
        }

        if (entity.code.isEmpty()) {
            return ApiResult.error("code不能是空")
        } else if (entity.code.length > 120) {
            return ApiResult.error("code长度不能大于120")
        }
        if (entity.code.contains("*") || entity.code.contains("&")) {
            return ApiResult.error("code参数存在非法字符，请核对")
        }

        entity.action.forEach {
            if (it.contains("*") || it.contains("&")) {
                return ApiResult.error("操作参数存在非法字符，请核对")
            }
        }

        if (entity.remark.HasValue && entity.remark.length > 255) {
            return ApiResult.error("备注长度不能超过255")
        }
        if (entity.action.isNotEmpty()) {
            entity.action.forEach {
                if (it.length > 120) {
                    return ApiResult.error("操作类型不能大于120")
                }
            }
        }

        //数据资源访问级别
        if (dataType != ResourceTypeEnum.Data && entity.dataAccessLevel != null) {
            return ApiResult.error("访问级别必须为空")
        }
        // 修改资源名称与resource
        if (dataType != ResourceTypeEnum.Api) {
            entity.resource = entity.name
        } else {
            if (entity.resource.length > 300) {
                return ApiResult.error("API地址长度不能大于300")
            }
            if (!entity.resource.trim().HasValue) {
                return ApiResult.error("API地址不能为空")
            }
        }

        //  资源是否存在
        mor.iam.sysResourceInfo.query()
            .where { it.id match_not_equal id }
            .where { it.appInfo.code match entity.appInfo.code }
            .where { it.code match code }
            .toEntity()
            .apply {
                if (this != null) {
                    return ApiResult.error("资源code已存在")
                }
            }

        // 应用是否存在
        mor.iam.sysApplication.query().where { it.appCode match entity.appInfo.code }.toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("应用不存在")
                }
            }


        // 租户侧资源唯一性
        mor.tenant.tenantResourceInfo.query()
            .apply {
                if (id.HasValue) {
                    this.where { it.sysId match_not_equal id }
                }
            }
            .where { it.appInfo.code match entity.appInfo.code }
            .where { it.code match code }
            .toEntity()
            .apply {
                if (this != null) {
                    return ApiResult.error("资源code在已授权应用的租户中已存在")
                }
            }

        // 编辑code校验
        if (id.HasValue) { //当前资源编辑
            mor.iam.sysResourceInfo.query()
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.id match id }
                .toEntity()
                .apply {
                    if (this == null) {
                        return ApiResult.error("资源不存在")
                    } else {
                        if (this.code != code) {
                            return ApiResult.error("资源code不可编辑")
                        }
                    }
                }
            // 如果包含字资源不可修改数据类型
            mor.iam.sysResourceInfo.query()
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.code match_pattern "^${entity.code}:[^:]+\$" }
                .toList(SysResourceTree::class.java).toMutableList()
                .apply {
                    if (this.isNotEmpty() && entity.type != ResourceTypeEnum.Data ) {
                        return ApiResult.error("存在子资源,不可修改数据类型")
                    }
                }
        }

        if (codeList.size > 1 && id.HasValue) { // 子资源编辑
            // 父资源校验 TODO where match_in
            mor.iam.sysResourceInfo.query()
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.code match_in codeList.Slice(0, -1) }
                .toList().map { it.code }
                .apply {
                    if (this.size != codeList.size - 1) {
                        return ApiResult.error("父资源{${(this - codeList.Slice(0, -1)).joinToString()}}不存在")
                    }
                }
        }

        if (codeList.size > 1 && !id.HasValue) { // 子资源父级补齐

            mor.iam.sysResourceInfo.query()
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.code match_in codeList.Slice(0, -1) }
                .toList().map { it.code }
                .apply {
                    (codeList.Slice(0, -1) - this).forEach {
                        entity.code = it // 当前层级的code
                        entity.id = "" // clean上次赋值的id

                        entity.type = ResourceTypeEnum.Data // 默认数据类型
                        asyncTenantResource(entity) // 租户侧资源同步
                    }
                }

        }
//        val requestJson = entity.ToJson().FromJson<JsonMap>()!!
        entity.code = code
        entity.id = id
        entity.code = code
        entity.type = dataType
        val entityId: String = asyncTenantResource(entity)
        return if (entityId.isEmpty()) ApiResult.error("更新失败") else ApiResult.of(entity.id)

    }

    @BizLog(BizLogActionEnum.Update, BizLogResourceEnum.AppResource, "应用资源")
    @ApiOperation("根据资源code修改")
    @PostMapping("/updateResourceInfoByCode")
    fun updateResourceInfoByCode(
        @JsonModel entity: SysResourceInfo,
        request: HttpServletRequest,
    ): ApiResult<String> {
        request.logMsg = "修改应用资源,应用{${entity.appInfo.name}}，资源{${entity.name}}"
        val code = entity.code
        var id = entity.id
        val dataType = entity.type
        var codeList: MutableList<String> = codeHolder(code)
        if (!code.HasValue) {
            return ApiResult.error("code不能为空")
        }
        // code规则限制
        code.split(":").toMutableList().forEach {
            if (!it.HasValue) {
                return ApiResult.error("code格式错误")
            }
        }
        if (entity.type != ResourceTypeEnum.Data && entity.type != ResourceTypeEnum.Api
            && entity.type != ResourceTypeEnum.Menu && entity.type != ResourceTypeEnum.Ui
        ) {
            return ApiResult.error("资源类型错误")
        }
        if (codeList.size > 5) {
            return ApiResult.error("资源层级最多五层")
        }

        if (entity.name.trim().isEmpty()) {
            return ApiResult.error("名称不能为空")
        } else if (entity.name.length > 300) {
            return ApiResult.error("名称不能大于300")
        }

        if (entity.code.isEmpty()) {
            return ApiResult.error("code不能是空")
        } else if (entity.code.length > 120) {
            return ApiResult.error("code长度不能大于120！")
        }
        if (entity.code.contains("*") || entity.code.contains("&")) {
            return ApiResult.error("code参数存在非法字符，请核对")
        }

        entity.action.forEach {
            if (it.contains("*") || it.contains("&")) {
                return ApiResult.error("操作参数存在非法字符，请核对")
            }
        }

        if (entity.remark.HasValue && entity.remark.length > 255) {
            return ApiResult.error("备注长度不能超过255")
        }
        if (entity.action.isNotEmpty()) {
            entity.action.forEach {
                if (it.length > 120) {
                    return ApiResult.error("操作类型不能大于120")
                }
            }
        }

        //数据资源访问级别
        if (dataType != ResourceTypeEnum.Data && entity.dataAccessLevel != null) {
            return ApiResult.error("访问级别必须为空")
        }
        // 修改资源名称与resource
        if (dataType != ResourceTypeEnum.Api) {
            entity.resource = entity.name
        } else {
            if (entity.resource.length > 300) {
                return ApiResult.error("API地址长度不能大于300")
            }
            if (!entity.resource.trim().HasValue) {
                return ApiResult.error("API地址不能为空")
            }
        }
        // 应用是否存在
        mor.iam.sysApplication.query().where { it.appCode match entity.appInfo.code }.toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("应用不存在")
                } else {
                    if (!this.enabled) {
                        return ApiResult.error("应用已停用")
                    }
                }
            }

        //  资源是否存在
        mor.iam.sysResourceInfo.query()
            .where { it.appInfo.code match entity.appInfo.code }
            .where { it.code match code }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("资源不存在")
                } else {
                    id = this.id
                    entity.id = this.id
                }
            }
        // 授权资源是否存在
        mor.tenant.tenantResourceInfo.query()
            .apply {
                if (id.HasValue) {
                    this.where { it.sysId match_not_equal id }
                }
            }
            .where { it.appInfo.code match entity.appInfo.code }
            .where { it.code match code }
            .toEntity()
            .apply {
                if (this != null) {
                    return ApiResult.error("资源code在已授权应用的租户中已存在")
                }
            }

        // 编辑code校验
        if (id.HasValue) { //当前资源编辑
            mor.iam.sysResourceInfo.query()
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.id match id }
                .toEntity()
                .apply {
                    if (this == null) {
                        return ApiResult.error("资源不存在")
                    } else {
                        if (this.code != code) {
                            return ApiResult.error("资源code不可编辑")
                        }
                    }
                }
        }

        if (codeList.size > 1 && id.HasValue) { // 子资源编辑
//            if(dataType != ResourceTypeEnum.Data) {
//                return ApiResult.error("子资源的资源类型必须是数据！")
//            }
            codeList.removeAt(codeList.size - 1)
            // 父资源校验
            codeList.forEach { item ->
                mor.iam.sysResourceInfo.query()
                    .where { it.appInfo.code match entity.appInfo.code }
                    .where { it.code match item }
                    .toEntity()
                    .apply {
                        if (this == null) {
                            return ApiResult.error("父资源不存在")
                        }
                    }
            }
        }

        if (codeList.size > 1 && !id.HasValue) { // 子资源父级补齐
            codeList.removeAt(codeList.size - 1)
            codeList.forEach { item -> // code
                entity.id = "" // clean上次赋值的id
                entity.code = item // 当前层级的code
                entity.type = ResourceTypeEnum.Data // 默认数据类型
                mor.iam.sysResourceInfo.query()
                    .where { it.appInfo.code match entity.appInfo.code }
                    .where { it.code match item }
                    .toEntity()
                    .apply {
                        if (this == null) { // 父资源不存在
                            asyncTenantResource(entity) // 租户侧资源同步
                        }
                    }
            }

        }
//        val requestJson = entity.ToJson().FromJson<JsonMap>()!!
        entity.code = code
        entity.id = id
        entity.code = code
        entity.type = dataType
        val res: String = asyncTenantResource(entity)
        return if (res.isEmpty()) ApiResult.error("更新失败") else ApiResult.of(entity.id)

    }

    /**
     * @Description admin&&租户侧资源同步
     */
    fun asyncTenantResource(entity: SysResourceInfo): String {
        var isInsert = false
        mor.iam.sysResourceInfo.updateWithEntity(entity)
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
                    return ""
                }
            }
        if (isInsert) {
            mor.tenant.tenantApplication.query()
                .where { it.appCode match entity.appInfo.code }
                .toList()
                .apply {

                    if (this.any()) {
                        var tenantResource: TenantResourceInfo
                        val list = mutableListOf<TenantResourceInfo>()

                        this.forEach {
                            tenantResource = entity.ToJson().FromJson(TenantResourceInfo::class.java)!!
                            tenantResource.id = ""
                            tenantResource.tenant = it.tenant
                            tenantResource.isSysDefine = true
                            tenantResource.sysId = entity.id
                            list.add(tenantResource)
                        }

                        mor.tenant.tenantResourceInfo.batchInsert()
                            .apply {
                                addEntities(list)
                            }
                            .exec()
                    }
                    return entity.id
                }
        }

        mor.tenant.tenantResourceInfo.update()
            .where { it.appInfo.code match entity.appInfo.code }
            .where { it.sysId match entity.id }
            .set { it.remark to entity.remark }
            .set { it.action to entity.action }
            .set { it.name to entity.name }
            .set { it.code to entity.code }
            .set { it.type to entity.type }
            .set { it.resource to entity.resource }
            .set { it.dataAccessLevel to entity.dataAccessLevel }
            .exec()

        return entity.id

    }

    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.AppResource, "应用资源")
    @ApiOperation("批量删除")
    @PostMapping("/delete/batch")
    fun deleteBatch(
        @Require appCode: String,
        @Require ids: List<String>,
        request: HttpServletRequest,
    ): JsonResult {
        request.logMsg = "批量删除应用资源"

        if (appCode.isEmpty()) {
            return JsonResult.error("appCode不能为空")
        }
        if (ids.isEmpty()) {
            return JsonResult.error("id不能为空")
        }
        mor.iam.sysApplication.query()
            .where { it.appCode match appCode }
            .toEntity()
            .apply {
                if (this == null) {
                    return JsonResult.error("应用不存在")
                }
            }
        mor.iam.sysResourceInfo.query()
            .where { it.appInfo.code match appCode }
            .where { it.id match_in ids }
            .count()
            .apply {
                if (this == 0) {
                    return JsonResult.error("找不到资源数据")
                }
            }

        // 删除子资源
        mor.iam.sysResourceInfo.query()
            .where { it.id match_in ids }.toListResult().data.toMutableList().forEach { a ->
                val codeParent = a.code + ":"
                val delIds: List<String> = mor.iam.sysResourceInfo.query().where {
                    it.appInfo.code match a.appInfo.code
                }.apply {
                    this.where { it.code match_pattern "^$codeParent" }
                }.toListResult().data.map { it.id }
                // 删除子资源
                mor.iam.sysResourceInfo.delete()
                    .where { it.appInfo.code match appCode }
                    .where { it.id match_in delIds }
                    .exec()
                delResource(delIds, appCode)
                delTenantResource(delIds, appCode)
            }


        //删除资源
        mor.iam.sysResourceInfo.delete()
            .where { it.appInfo.code match appCode }
            .where { it.id match_in ids }
            .exec()
        delResource(ids, appCode)

        // 删除租户侧授权资源
        delTenantResource(ids, appCode)
        return JsonResult()
    }

    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.AppResource, "应用资源")
    @ApiOperation("根据资源code批量删除")
    @PostMapping("/deleteByCode/batch")
    fun deleteByCodeBatch(
        @Require appCode: String,
        @Require codes: List<String>,
        request: HttpServletRequest,
    ): JsonResult {
        request.logMsg = "批量删除应用资源"

        if (appCode.isEmpty()) {
            return JsonResult.error("appCode不能为空")
        }
        if (codes.isEmpty()) {
            return JsonResult.error("codes不能为空")
        }
        mor.iam.sysApplication.query()
            .where { it.appCode match appCode }
            .toEntity()
            .apply {
                if (this == null) {
                    return JsonResult.error("应用不存在")
                }
            }
        var fatherIds = mutableListOf<String>()
        var sonIds = mutableListOf<String>()
        mor.iam.sysResourceInfo.query()
            .where { it.appInfo.code match appCode }
            .where { it.code match_in codes }
            .toList()
            .apply {
                if (this.size == 0) {
                    return JsonResult.error("找不到资源数据")
                } else {
                    sonIds = this.filter { it.code.contains(":") }.toList().map { it.id }.toMutableList()
                    fatherIds = this.filter { !it.code.contains(":") }.toList().map { it.id }.toMutableList()
                }
            }

        // 删除父资源 并将子资源加到子资源id集合里
        mor.iam.sysResourceInfo.query()
            .where { it.code match_in codes }
            .where { it.id match_in fatherIds }
            .toList()
            .forEach { a ->
                val codeParent = a.code + ":"
                //该父资源的子资源
                val delIds = mor.iam.sysResourceInfo.query()
                    .where { it.appInfo.code match a.appInfo.code }
                    .where { it.code match_pattern "^$codeParent" }
                    .toList()
                    .map { it.id }
                    .toMutableList()
                //将子资源添加到子资源id集合里
                sonIds.plus(delIds)
                //删除父资源
                mor.iam.sysResourceInfo.delete()
                    .where { it.appInfo.code match appCode }
                    .where { it.id match a.id }
                    .exec()
                delResource(mutableListOf(a.id), appCode)
                delTenantResource(mutableListOf(a.id), appCode)

                //sonIds=sonIds.subtract(delIds).toMutableList()
                /* val mList1 =arrayListOf(0,1,2,3,4,5,6)
                 val mList2 =arrayListOf(0,1,2,3,9,8,7)
                 println("============subtract=============")
                 A中去掉和 B 相同的元素
                 //A.subtract(B)
                 println(mList1.subtract(mList2))
                 //[4, 5, 6]*/

            }


        //删除子资源

        mor.iam.sysResourceInfo.delete()
            .where { it.appInfo.code match appCode }
            .where { it.id match_in sonIds.distinctBy { it }.toMutableList() }
            .exec()

        delResource(sonIds.distinctBy { it }.toMutableList(), appCode)

        // 删除租户侧授权资源
        delTenantResource(sonIds.distinctBy { it }.toMutableList(), appCode)
        return JsonResult()
    }


    /**
     * 删除资源授权 boss
     */
    fun delResource(ids: List<String>, appCode: String) {
        mor.iam.sysAppAuthResource.query()
            .where { it.appInfo.code match appCode }
            .where { it.auths.resourceId match_in ids }
            .toList()
            .apply {
                this.forEach {
                    it.auths.removeAll { it.resourceId in ids }
                    if (it.auths.size > 0) {
                        mor.iam.sysAppAuthResource.updateWithEntity(it).execUpdate()
                    } else {
                        mor.iam.sysAppAuthResource.deleteById(it.id).exec()
                    }
                }
            }

    }

    /**
     * 租户侧授权删除 tenant
     */
    fun delTenantResource(ids: List<String>, appCode: String) {

        mor.tenant.tenantResourceInfo.query()
            .select { it.id }
            .where { it.appInfo.code match appCode }
            .where { it.sysId match_in ids }
            .toList(String::class.java)
            .apply {
                val data = this

                //删除租户资源
                mor.tenant.tenantResourceInfo.delete()
                    .where { it.id match_in data }
                    .exec()

                //删除租户授权
                mor.tenant.tenantAppAuthResourceInfo.query()
                    .where { it.auths.resourceId match_in data }
                    .toList()
                    .apply {
                        this.forEach {
                            it.auths.removeAll { it.resourceId in data }
                            if (it.auths.size > 0) {
                                mor.tenant.tenantAppAuthResourceInfo.updateWithEntity(it).execUpdate()
                            } else {
                                mor.tenant.tenantAppAuthResourceInfo.deleteById(it.id).exec()
                            }
                        }
                    }
            }
    }
}