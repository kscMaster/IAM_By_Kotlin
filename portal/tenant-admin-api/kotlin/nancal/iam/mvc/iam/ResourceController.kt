package nancal.iam.mvc.iam

import io.swagger.annotations.*
import nancal.iam.annotation.BizLog
import nancal.iam.annotation.CheckTenantAppStatus
import org.springframework.web.bind.annotation.*
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nbcp.comm.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.util.CodeUtils
import nancal.iam.util.CodeUtils.Companion.codeHolder
import nancal.iam.utils.List2TreeUtils
import nbcp.db.CodeName
import nbcp.db.IdName
import org.springframework.beans.BeanUtils
import javax.servlet.http.*
import java.time.*
import kotlin.streams.toList

/**
 * Created by CodeGenerator at 2021-11-26 11:42:27
 */
@CheckTenantAppStatus
@Api(description = "api资源", tags = arrayOf("ResourceInfo"))
@RestController
@RequestMapping("/tenant/resource-info")
class ResourceInfoAutoController {

    @ApiOperation("资源列表-层级获取")
    @PostMapping("/listResource")
    fun listResource(
        id: String, //当列表列新一条后，刷新时使用
        parentCode: String, // 获取子资源列表
        name: String,
        code: String,
        remark: String,
        resource: String,
        action: String,
        type: String,
        appInfoId: String,
        keywords: String,
        createAt: LocalDateTime?,
        endAt: LocalDateTime?,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantResourceInfo> {

        val loginTenantAdminUser = request.LoginTenantAdminUser

        if (name.HasValue || code.HasValue || type.HasValue || keywords.HasValue) { // 模糊匹配
            mor.tenant.tenantResourceInfo.query()
                .apply {

                    this.where { it.tenant.id match  loginTenantAdminUser.tenant.id }

                    if (keywords.HasValue) {
                        this.whereOr( { it.type match keywords }, { it.name match_like keywords }, { it.code match_like keywords })
                    }
                    if (appInfoId.HasValue) {
                        this.where { it.appInfo.code match appInfoId }
                    }
                    if (id.HasValue) {
                        this.where { it.id match id }
                    }
                    if (name.HasValue) {
                        this.where { it.name match_like  name }
                    }
                    if (code.HasValue) {
                        this.where { it.code match_like code }
                    }
                    if (type.HasValue) {
                        this.where { it.type match type }
                    }
                }
                .limit(skip, take).orderByDesc { it.createAt }
                .toListResult()
                .apply {
                    return this;
                }
        }
        // 层级返回
        mor.tenant.tenantResourceInfo.query()
            .apply {

                this.where { it.tenant.id match  loginTenantAdminUser.tenant.id }

                if (appInfoId.HasValue) {
                    this.where { it.appInfo.code match appInfoId }
                }
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
                return this;
            }
    }

    class TenantResourceInfoDetail {
        var id : String? = ""
        var appInfo: CodeName = CodeName()
        var parentCode : String? = "" // 父级code
        var name: String = "" //中文名称
        var code: String = ""
        var type: String = ""
        var resource: String = "" //仅在Api时，定义为Url，其它类型 = name
    }

    @ApiOperation("资源详情包含父级")
    @PostMapping("/detailInfo")
    fun detailInfo(
        id: String,
        code: String,
        appCode: String,
        request: HttpServletRequest
    ): ListResult<TenantResourceInfoDetail> {

        val result : MutableList<TenantResourceInfoDetail> = mutableListOf()
        if (!appCode.HasValue) {
            return ListResult.error("appCode不能是空")
        }
        if (!code.HasValue) {
            return ListResult.error("code不能是空")
        }
        var codeList: MutableList<String> = codeHolder(code)
        if (codeList.size == 1) {
            val vo = TenantResourceInfoDetail()
            mor.tenant.tenantResourceInfo.query()
                .where { it.appInfo.code match appCode }
                .where { it.code match code }.toEntity()
                .let {
                    if (it != null) {
                        BeanUtils.copyProperties(it, vo)
                        vo.type = it.type.toString()
                    }
                }
            result.add(vo)
            return ListResult.of(result, result.size)

        } else {
            var codeStr = ""
            codeList.forEach{ item ->
                val vo = TenantResourceInfoDetail()
                if (codeStr.HasValue) {
                    vo.parentCode = codeStr
                } else {
                    vo.parentCode = ""
                }
                codeStr = item
                val toEntity: TenantResourceInfo? = mor.tenant.tenantResourceInfo.query()
                    .where { it.appInfo.code match appCode }
                    .where { it.code match item }.toEntity()
                toEntity?.let {
                    BeanUtils.copyProperties(it, vo)
                    vo.type = it.type.toString()
                    result.add(vo) }
            }

            return ListResult.of(result, result.size)
        }
    }

    class TenantResourceInfoTree {
        var id: String? = ""
        var appInfo: CodeName = CodeName()
        var parentCode: String? = "" // 父级code
        var name: String = "" //中文名称
        var code: String = ""
        var type: ResourceTypeEnum? = null
        var remark: String = ""
        var action: MutableList<String> = mutableListOf()
        var resource: String = "" //仅在Api时，定义为Url，其它类型 = name
        var children: MutableList<TenantResourceInfoTree>? = null
    }

    @ApiOperation("资源授权树")
    @PostMapping("/resource_tree")
    fun resourceTree(
        id: String,
        code: String,
        appCode: String,
        request: HttpServletRequest
    ): ListResult<TenantResourceInfoTree> {
        if (!appCode.HasValue) {
            return ListResult.error("appCode不能是空")
        }
        val loginTenantAdminUser = request.LoginTenantAdminUser
        val tenantId = loginTenantAdminUser.tenant.id
//        var result: MutableList<TenantResourceInfoTree> = getChildResource("", appCode, tenantId)
        val result: MutableList<TenantResourceInfoTree> = list2Tree(appCode, tenantId)
        return ListResult.of(result, result.size)
    }

    fun list2Tree(appCode: String, tenantId: String) :  MutableList<TenantResourceInfoTree>{
        val toList: MutableList<TenantResourceInfoTree> = mor.tenant.tenantResourceInfo.query()
            .where { it.appInfo.code match appCode }
            .where { it.tenant.id match tenantId }
            .toList(TenantResourceInfoTree::class.java).toMutableList()
        return List2TreeUtils.list2Tree(toList).toMutableList()
    }

    fun getChildResource(parentCode : String, appCode:String, tenantId:String):MutableList<TenantResourceInfoTree>{
        val result : MutableList<TenantResourceInfoTree> = mutableListOf()
        // 获取子资源
        val toMutableList: MutableList<TenantResourceInfoTree> = mor.tenant.tenantResourceInfo.query()
            .where { it.appInfo.code match appCode }
            .where { it.tenant.id match  tenantId }
            .apply {
                if (!parentCode.HasValue) {
                    this.where { it.code match_pattern "^[^:]+\$" }
                } else {
                    this.where { it.code match_pattern "^${parentCode}:[^:]+\$" }
                }
            }.toList(TenantResourceInfoTree::class.java).toMutableList()
        return if (toMutableList.isEmpty()) { // a:b
            result
        } else {
            toMutableList.forEach{
                it.parentCode = parentCode
                result.add(it)
                val childResource: MutableList<TenantResourceInfoTree> = getChildResource(it.code, appCode, tenantId )
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
        remark: String,
        resource: String,
        action: String,
        type: String,
        appInfoId: String,
        createAt: LocalDateTime?,
        endAt: LocalDateTime?,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantResourceInfo> {

        val loginTenantAdminUser = request.LoginTenantAdminUser

        mor.tenant.tenantResourceInfo.query()
            .apply {

                this.where { it.tenant.id match  loginTenantAdminUser.tenant.id }

                if (appInfoId.HasValue) {
                    this.where { it.appInfo.code match appInfoId }
                }
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (remark.HasValue) {
                    this.where { it.remark match_like remark }
                }
                if (resource.HasValue) {
                    this.where { it.resource match_like resource }
                }
                if (name.HasValue) {
                    this.where { it.name match_like name }
                }
                if (code.HasValue) {
                    this.where { it.code match_like code }
                }
                if (action.HasValue) {
                    this.where { it.action match action }
                }
                if (type.HasValue) {
                    this.where { it.type match type }
                }
                if (createAt != null) {
                    this.where { it.createAt match_gte  createAt }
                }
                if (endAt != null) {
                    this.where { it.createAt match_lte  endAt  }
                }


            }
            .limit(skip, take).orderByDesc { it.createAt }
            .toListResult()
            .apply {
                return this;
            }
    }


    @ApiOperation("列表")
    @PostMapping("/list_temp")
    fun list_temp(
        nameOrCode: String,
        @Require appInfoId: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantResourceInfo> {

        val loginTenantAdminUser = request.LoginTenantAdminUser


        mor.tenant.tenantApplication.query()
            .where { it.appCode match appInfoId }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity()
            .apply {
                if(this == null){
                    return ListResult.error("租户下不存在该应用")
                }
            }

        mor.tenant.tenantResourceInfo.query()
            .apply {
                this.where { it.tenant.id match  loginTenantAdminUser.tenant.id }
                if (appInfoId.HasValue) {
                    this.where { it.appInfo.code match appInfoId }
                }
                if (nameOrCode.HasValue) {
                    this.whereOr ({ it.name match_like nameOrCode },{ it.code match_like nameOrCode })
                }
            }
            .limit(skip, take)
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
    ): ApiResult<TenantResourceInfo> {
        mor.tenant.tenantResourceInfo.query()
            .where { it.id match id }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }
                return ApiResult.of(this)
            }
    }
    @ApiOperation("详情")
    @PostMapping("/detailByCode/{code}")
    fun detailByCode(
        @Require code: String,
        @Require appCode: String,
        request: HttpServletRequest
    ): ApiResult<TenantResourceInfo> {
        mor.tenant.tenantResourceInfo.query()
            .where { it.code match code }
            .where { it.appInfo.code match appCode }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }
                return ApiResult.of(this)
            }
    }

    @BizLog(BizLogActionEnum.Save, BizLogResourceEnum.Resource, "租户侧资源新增、修改")
    @ApiOperation("新增、更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: TenantResourceInfo,
        request: HttpServletRequest
    ): ApiResult<String> {
        //鉴权
        val loginTenantAdminUser = request.LoginTenantAdminUser
        entity.tenant = loginTenantAdminUser.tenant

        // code规则限制
        entity.code.split(":").toMutableList().forEach{
            if (!it.HasValue) {
                return ApiResult.error("Code格式错误")
            }
        }
        if(entity.name.isEmpty()){
            return ApiResult.error("名称不能为空")
        }else if(entity.name.length>300){
            return ApiResult.error("名称不能大于300")
        }

        if(entity.code.isEmpty()){
            return ApiResult.error("code不能为空")
        }else if(entity.code.length>120){
            return ApiResult.error("code不能大于120")
        }
        // 截取code
        var codeList = codeHolder(entity.code)

        if (entity.type != ResourceTypeEnum.Data && entity.type != ResourceTypeEnum.Api
            && entity.type != ResourceTypeEnum.Menu && entity.type != ResourceTypeEnum.Ui) {
            return ApiResult.error("资源类型错误")
        }
        if (codeList.size > 5) {
            return ApiResult.error("资源层级最多五层")
        }

        if(entity.code.contains("*")||entity.code.contains("&")){
            return ApiResult.error("code参数存在非法字符，请核对")
        }

        entity.action.forEach {
            if(it.contains("*")||it.contains("&")){
                return ApiResult.error("操作参数存在非法字符，请核对")
            }
        }

        if(entity.action.contains("*")||entity.code.contains("&")){
            return ApiResult.error("code参数存在非法字符，请核对")
        }

        if(entity.remark.HasValue && entity.remark.length>255){
            return ApiResult.error("备注不能大于255")
        }
        if(entity.action.isNotEmpty()){
            entity.action.forEach {
                if(it.length>120){
                    return ApiResult.error("操作类型不能大于120")
                }
            }
        }
        //数据资源访问级别
        if (entity.type != ResourceTypeEnum.Data && entity.dataAccessLevel != null) {
            return ApiResult.error("访问级别必须为空")
        }
        // 修改资源名称与resource
        if(entity.type != ResourceTypeEnum.Api){
            entity.resource = entity.name
        }else{
            if(entity.resource.length>300){
                return ApiResult.error("API地址长度不能大于300")
            }
            if(!entity.resource.trim().HasValue){
                return ApiResult.error("API地址不能为空")
            }
        }

        //资源code不可编辑
        if(entity.id.HasValue){
            val oldResourceInfo= mor.tenant.tenantResourceInfo.query()
                .where { it.id match entity.id }
                .toEntity() ?: return ApiResult.error("资源不存在")
            if(oldResourceInfo.code!=entity.code){
                return ApiResult.error("资源Code不可更改")
            }

        }
        // 应用校验
        mor.tenant.tenantApplication.query()
            .where { it.appCode match entity.appInfo.code }
            .where { it.tenant.id match loginTenantAdminUser.tenant.id }
            .toEntity()
            .apply {
                if(this == null){
                    return ApiResult.error("应用不存在")
                }
            }

        // 资源校验
        if(entity.id.HasValue){
            mor.tenant.tenantResourceInfo.query()
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                .where { it.id match entity.id }
                .toEntity()
                .apply {
                    if(this == null){
                        return ApiResult.error("资源不存在")
                    }
                }
            // 如果包含字资源不可修改数据类型
            mor.tenant.tenantResourceInfo.query()
                .apply {
                    this.where { it.tenant.id match  loginTenantAdminUser.tenant.id }
                    this.where { it.code match_pattern "^${entity.code}:[^:]+\$" }
                }
                .toListResult()
                .apply {
                    if (this.data.isNotEmpty() && entity.type != ResourceTypeEnum.Data) {
                        return ApiResult.error("存在子资源,不可修改数据类型")
                    }
                }
        }

        if (entity.appInfo.code.HasValue) {
            // 同一租户下的资源名称不能重复
            var exists = mor.tenant.tenantResourceInfo.query()
                .where { it.tenant.id match entity.tenant.id }
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.code match entity.code }
                .apply {
                    if (entity.id.HasValue) {
                        this.where { it.id match_not_equal entity.id }
                    }
                }
                .exists()
            if (exists) {
                //如果是修改，需要保证除当前应用外，其他应用没有该资源名称
                return ApiResult.error("资源code已存在")
            }
        } else {
            return ApiResult.error("应用code不能为空")
        }

        if (codeList.size > 1 && entity.id.HasValue) { // 子资源编辑
            mor.tenant.tenantResourceInfo.query()
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.code match_in codeList.Slice(0, -1)}
                .toList().map { it.code }
                .apply {
                    if (this.size != codeList.size - 1) {
                        return ApiResult.error("父资源{${(this - codeList.Slice(0, -1)).joinToString()}}不存在")
                    }
                }
        }

        val code = entity.code
        val id = entity.id
        val dataType = entity.type
        if (codeList.size > 1 && !id.HasValue) { // 子资源父级补齐
            mor.tenant.tenantResourceInfo.query()
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.code match_in codeList.Slice(0, -1) }
                .toList().map { it.code }
                .apply {
                    (codeList.Slice(0, -1) - this).forEach {
                        entity.code = it // 当前层级的code
                        entity.id = "" // clean上次赋值的id
                        entity.type = ResourceTypeEnum.Data // 默认数据类型
                        mor.tenant.tenantResourceInfo.doInsert(entity)
                            .apply {
                                if (this.isEmpty()) {
                                    return ApiResult.error("父资源更新失败")
                                }
                            }
                    }
                }
        }

        entity.code = code
        entity.id = id
        entity.code = code
        entity.type = dataType
        mor.tenant.tenantResourceInfo.updateWithEntity(entity)
            .run {
                if (entity.id.HasValue) {
                    request.logMsg = "租户侧资源{${entity.name}}修改"
                    return@run this.execUpdate()
                } else {
                    request.logMsg = "租户侧资源{${entity.name}}新增"
                    return@run this.execInsert()
                }
            }
            .apply {
                if (this == 0) {
                    return ApiResult.error("更新失败")
                }
                return ApiResult.of(entity.id)
            }
    }


    class CodeMessage{
        var code: String = ""

        // 用户所属部门
        var message: String = ""
    }


    @BizLog(BizLogActionEnum.Add, BizLogResourceEnum.Resource, "租户侧资源新增")
    @ApiOperation("新增")
    @PostMapping("/batchInsert")
    fun batchInsert(
        @JsonModel entitys: List<TenantResourceInfo>,
        request: HttpServletRequest
    ): ApiResult<MutableList<CodeMessage>> {

        request.logMsg = "租户侧资源批量新增"

        //鉴权
        val loginTenantAdminUser = request.LoginTenantAdminUser

        var insertList = mutableListOf<TenantResourceInfo>()

        var errorList = mutableListOf<CodeMessage>()

        val distinctBy = entitys.distinctBy { it.code }

        distinctBy.forEach {  entity ->

            var codeMessage = CodeMessage()
            codeMessage.code = entity.code

            entity.tenant = loginTenantAdminUser.tenant

            if(entity.name.isEmpty()){
                codeMessage.message = "名称不能为空"
                errorList.add(codeMessage)
                return@forEach
            }else if(entity.name.length>32){
                codeMessage.message = "名称不能大于32"
                errorList.add(codeMessage)
                return@forEach
            }

            if(entity.code.isEmpty()){
                codeMessage.message = "code不能为空"
                errorList.add(codeMessage)
                return@forEach
            }else if(entity.code.length>120){
                codeMessage.message = "code不能大于120"
                errorList.add(codeMessage)
                return@forEach
            }

            if(entity.remark.HasValue && entity.remark.length>255){
                codeMessage.message = "备注不能大于255"
                errorList.add(codeMessage)
                return@forEach
            }
            if(entity.action.isNotEmpty()){
                entity.action.forEach {
                    if(it.length>120){
                        codeMessage.message = "操作类型不能大于120"
                        errorList.add(codeMessage)
                        return@forEach
                    }
                }
            }

            mor.tenant.tenantApplication.query()
                .where { it.appCode match entity.appInfo.code }
                .where { it.tenant.id match loginTenantAdminUser.tenant.id }
                .toEntity()
                .apply {
                    if(this == null){
                        codeMessage.message = "应用不存在"
                        errorList.add(codeMessage)
                        return@forEach
                    }
                }


            if(entity.id.HasValue){
                entity.id = ""
            }

            if (entity.appInfo.code.HasValue) {
                // 同一租户下的资源名称不能重复
                var exists = mor.tenant.tenantResourceInfo.query()
                    .where { it.tenant.id match entity.tenant.id }
                    .where { it.appInfo.code match entity.appInfo.code }
                    .where { it.code match entity.code }
                    .apply {
                        if (entity.id.HasValue) {
                            this.where { it.id match_not_equal entity.id }
                        }
                    }
                    .exists()
                if (exists) {
                    //如果是修改，需要保证除当前应用外，其他应用没有该资源名称
                    codeMessage.message = "资源code已存在"
                    errorList.add(codeMessage)
                    return@forEach
                }
            } else {
                codeMessage.message = "应用code不能为空"
                errorList.add(codeMessage)
                return@forEach
            }
            entity.code.split(":").forEach {
                if (!it.HasValue) {
                    codeMessage.message = "code格式错误"
                    errorList.add(codeMessage)
                    return@forEach
                }
            }

            // 修改资源名称与resource
            if(entity.type != ResourceTypeEnum.Api){
                entity.resource = entity.name
            }
            // 子资源处理
            val childResourceBath: MutableList<TenantResourceInfo>? = childResourceBath(entity)
            if (childResourceBath?.isNotEmpty() == true) {
                insertList.addAll(childResourceBath)

            }
            insertList.add(entity)
        }


        mor.tenant.tenantResourceInfo.batchInsert()
            .apply {
                addEntities(insertList)
            }
            .exec()

        return ApiResult.of(errorList)


    }

    @ApiOperation("批量新增子资源处理")
    fun childResourceBath(entity:TenantResourceInfo) : MutableList<TenantResourceInfo>?{
        val result : MutableList<TenantResourceInfo> = mutableListOf()
        entity.code.split(":").forEach {
            if (!it.HasValue) {
                return result
            }
        }
        val codeList: MutableList<String> = codeHolder(entity.code)
        if (codeList.size == 1) {
            return result
        }
        codeList.removeAt(codeList.size - 1)
        codeList.forEach{ item ->
            mor.tenant.tenantResourceInfo.query()
                .where { it.tenant.id match entity.tenant.id }
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.code match  item}
                .toEntity()
                .apply {
                    if (this == null) {
                        val vo = TenantResourceInfo()
                        BeanUtils.copyProperties(entity, vo)
                        vo.id = ""
                        vo.code = item
                        vo.type = ResourceTypeEnum.Data
                        result.add(vo)
                    }
                }
        }
        return result
    }


    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.Resource, "租户侧资源删除")
    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "资源删除"
        val toEntity = mor.tenant.tenantResourceInfo.queryById(id).toEntity()
        toEntity ?: return JsonResult.error("找不到数据")
        // 删除子资源
        mor.tenant.tenantResourceInfo.query()
            .where { it.id match id }.toListResult().data.toMutableList().forEach { a ->
                val codeParent = a.code + ":"
                val delIds: List<String> = mor.tenant.tenantResourceInfo.query().where {
                    it.appInfo.code match a.appInfo.code
                }.apply {
                    this.where { it.code match_pattern "^$codeParent" }
                }.toListResult().data.map { it.id }
                // 删除子资源
                delResource(delIds as MutableList<String>, request.LoginTenantAdminUser.tenant.id)
            }
        // 删除资源
        mor.tenant.tenantResourceInfo.delete()
            .where { it.id match id }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .where { it.isSysDefine match false }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }

                mor.tenant.tenantAppAuthResourceInfo.query()
                    .where { it.auths.resourceId match id }.toList()
                    .apply {
                        this.forEach {
                            it.auths.removeAll{ it.resourceId ==id}
                            mor.tenant.tenantAppAuthResourceInfo.updateWithEntity(it).execUpdate()

                            if(it.auths.size == 0){
                                mor.tenant.tenantAppAuthResourceInfo.deleteById(it.id).exec()
                            }
                        }
                    }
                return JsonResult()
            }
    }

    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.Resource, "租户侧资源删除")
    @ApiOperation("批量删除")
    @PostMapping("/delete/batch")
    fun deleteBatch(
        @Require ids: MutableList<String>,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "批量删除资源"
        mor.tenant.tenantResourceInfo.query().where { it.id match_in ids }.exists().apply {
            if (!this) {
                return JsonResult.error("找不到数据")
            }
        }
        //  删除子资源
        mor.tenant.tenantResourceInfo.query()
            .where { it.id match_in ids }.toListResult().data.toMutableList().forEach { a ->
                val codeParent = a.code + ":"
                val delIds: List<String> = mor.tenant.tenantResourceInfo.query().where {
                    it.appInfo.code match a.appInfo.code
                }.apply {
                    this.where { it.code match_pattern "^$codeParent" }
                }.toListResult().data.map { it.id }
                // 删除子资源
                delResource(delIds as MutableList<String>, request.LoginTenantAdminUser.tenant.id)
            }
        // 删除资源
        delResource(ids, request.LoginTenantAdminUser.tenant.id)
        return JsonResult()
    }

    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.Resource, "租户侧资源删除")
    @ApiOperation("批量删除")
    @PostMapping("/deleteByCode/batch")
    fun deleteBatchByCode(
        @Require appCode: String,
        @Require codes: MutableList<String>,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "批量删除资源"
        val tenantId=request.LoginTenantAdminUser.tenant.id
        mor.tenant.tenantApplication.query()
            .where { it.appCode match appCode }
            .where { it.tenant.id match tenantId }
            .toEntity()
            .apply {
                if(this==null){
                    return JsonResult.error("应用不存在")
                }else{
                    if(!this.enabled){
                        return JsonResult.error("应用已停用")
                    }
                }
            }
        var fatherIds= mutableListOf<String>()
        var sonIds= mutableListOf<String>()
        mor.tenant.tenantResourceInfo.query()
            .where { it.appInfo.code match appCode }
            .where { it.tenant.id match tenantId }
            .where { it.code match_in codes }
            .toList()
            .apply {
                if (this.size==0) {
                    return JsonResult.error("找不到数据")
                }else{
                    sonIds=this.filter { it.code.contains(":") }.toList().map { it.id }.toMutableList()
                    fatherIds=this.filter { !it.code.contains(":") }.toList().map { it.id }.toMutableList()
                }
            }
        //  删除父资源 并将子资源加到子资源id集合里
        mor.tenant.tenantResourceInfo.query()
            .where { it.tenant.id match tenantId }
            .where { it.appInfo.code match appCode }
            .where { it.id match_in fatherIds }
            .toList()
            .forEach { a ->
                    val codeParent = a.code + ":"
                    //查要删除的子资源
                    val delIds = mor.tenant.tenantResourceInfo.query()
                        .where{it.tenant.id match tenantId}
                        .where {it.appInfo.code match appCode}
                        .where { it.code match_pattern "^$codeParent" }
                        .toList()
                        .map { it.id }
                        .toMutableList()
                    sonIds.plus(delIds)
                    // 删除父资源
                    delResource(mutableListOf(a.id), tenantId)
            }
        // 删除子资源
        delResource(sonIds.distinctBy { it }.toMutableList(),tenantId)
        return JsonResult()
    }

    /**
     * 删除资源
     */
    fun delResource(ids: MutableList<String>, tenantId: String) : JsonResult {
        mor.tenant.tenantResourceInfo.delete()
            .where { it.id match_in ids }
            .where { it.tenant.id match tenantId }
            .where { it.isSysDefine match false }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
                mor.tenant.tenantResourceInfo.query()
                    .where { it.id match_in ids }
                    .select { it.name }
                    .toList(String::class.java)
                mor.tenant.tenantAppAuthResourceInfo.query()
                    .where { it.auths.resourceId match_in ids }.toList()
                    .apply {
                        this.forEach {
                            it.auths.removeAll { it.resourceId in ids }
                            mor.tenant.tenantAppAuthResourceInfo.updateWithEntity(it).execUpdate()
                            if (it.auths.size == 0) {
                                mor.tenant.tenantAppAuthResourceInfo.deleteById(it.id).exec()
                            }
                        }
                    }
            }
        return JsonResult()
    }


    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.Resource, "租户侧资源删除")
    @ApiOperation("根据code删除资源")
    @PostMapping("/deleteByCode")
    fun deleteByCode(
        @Require code: String,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "资源删除"
        val toEntity = mor.tenant.tenantResourceInfo.query()
            .where { it.code match code }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity()
        toEntity ?: return JsonResult.error("找不到数据")
        // 删除子资源
        val delIds: List<String> = mor.tenant.tenantResourceInfo.query()
            .where {
                it.appInfo.code match toEntity.appInfo.code }
            .apply {
                this.where { it.code match_pattern "^${toEntity.code + ":"}" }
            }.toListResult().data.map { it.id }
        delResource(delIds as MutableList<String>, request.LoginTenantAdminUser.tenant.id)

        // 删除资源
        return delOneResource(code, request.LoginTenantAdminUser.tenant.id)
    }


    @ApiOperation("根据资源code查所有授权主体和action")
    @PostMapping("/allTargetAndActByCode")
    fun allTargetAndActByCode(
        @Require codes: List<String>,
        @Require appCode: String,
        request: HttpServletRequest
    ): ApiResult<List<TenantAppAuthResourceInfo>> {
        val tenantId = request.LoginTenantAdminUser.tenant.id
        if (tenantId.isBlank()) {
            return ApiResult.error("租户ID不能为空")
        }
        val resourceMap = mor.tenant.tenantResourceInfo.query()
            .where{it.tenant.id match tenantId}
            .where { it.appInfo.code match appCode }
            .where { it.code match_in codes }
            .where { it.isDeleted match_not_equal true }
            .toList().associateBy ({it.id}, {it})
        var authList: MutableList<TenantAppAuthResourceInfo> = mutableListOf()
        // 资源不存在，返回空
        if (resourceMap.isEmpty()) {
            return ApiResult.of(authList);
        }
        val resourceIds = resourceMap.keys
        authList = mor.tenant.tenantAppAuthResourceInfo.query()
            .where { it.tenant.id match tenantId }
            .where { it.appInfo.code match  appCode}
            .where { it.isDeleted match_not_equal true }
            .whereOr({ it.auths.code match_in resourceMap.values.stream().map { c -> c.code }.toList() },{it.auths.code match "*"})
            .toList()

        //所有被授权主体
        authList.forEach{
                    tenantAppAuth->
            val resAuthResourceInfo = mutableListOf<AuthResourceInfo>()
            tenantAppAuth.auths.forEach {
                    auth->
                // 数据库能找到资源的 id 及 资源：*，操作：*的情况
                if(auth.resourceIsAll || resourceIds.contains(auth.resourceId)){
                    if (!auth.resourceIsAll) {
                        auth.dataAccessLevel=resourceMap[auth.resourceId]?.dataAccessLevel
                    }
                    resAuthResourceInfo.add(auth)
                }
            }
            tenantAppAuth.auths=resAuthResourceInfo
        }
        return ApiResult.of(authList);
    }

    @BizLog(BizLogActionEnum.Update, BizLogResourceEnum.Resource, "租户侧资源修改")
    @ApiOperation("根据资源code修改")
    @PostMapping("/updateByCode")
    fun updateByCode(
        @JsonModel entity: TenantResourceInfo,
        request: HttpServletRequest
    ): ApiResult<String> {

        //鉴权
        val loginTenantAdminUser = request.LoginTenantAdminUser
        entity.tenant = loginTenantAdminUser.tenant
        if(!entity.code.HasValue) return ApiResult.error("资源code不能为空")
        val res = mor.tenant.tenantResourceInfo.query()
            .where{it.code match entity.code}
            .where{it.appInfo.code match  entity.appInfo.code}
            .where{it.tenant.id match entity.tenant.id}
            .toEntity()?:return ApiResult.error("资源不存在")
        entity.id=res.id

        // code规则限制
        entity.code.split(":").toMutableList().forEach{
            if (!it.HasValue) {
                return ApiResult.error("code格式错误")
            }
        }
        if(entity.name.trim().isEmpty()){
            return ApiResult.error("名称不能为空")
        }else if(entity.name.length>300){
            return ApiResult.error("名称不能大于300")
        }

        if(entity.code.isEmpty()){
            return ApiResult.error("code不能为空")
        }else if(entity.code.length>120){
            return ApiResult.error("code不能大于120")
        }
        // 截取code
        var codeList = codeHolder(entity.code)

        if(entity.code.contains("*")||entity.code.contains("&")){
            return ApiResult.error("code参数存在非法字符，请核对")
        }

        entity.action.forEach {
            if(it.contains("*")||it.contains("&")){
                return ApiResult.error("操作参数存在非法字符，请核对")
            }
        }

        if(entity.action.contains("*")||entity.code.contains("&")){
            return ApiResult.error("code参数存在非法字符，请核对")
        }

        if(entity.remark.HasValue && entity.remark.length>255){
            return ApiResult.error("备注不能大于255")
        }
        if(entity.action.isNotEmpty()){
            entity.action.forEach {
                if(it.length>120){
                    return ApiResult.error("操作类型不能大于120")
                }
            }
        }
        if (entity.type != ResourceTypeEnum.Data && entity.type != ResourceTypeEnum.Api
            && entity.type != ResourceTypeEnum.Menu && entity.type != ResourceTypeEnum.Ui) {
            return ApiResult.error("资源类型错误")
        }
        if (codeList.size > 5) {
            return ApiResult.error("资源层级最多五层")
        }
        //数据资源访问级别
        if (entity.type != ResourceTypeEnum.Data && entity.dataAccessLevel != null) {
            return ApiResult.error("访问级别必须为空")
        }
        // 修改资源名称与resource
        if(entity.type != ResourceTypeEnum.Api){
            entity.resource = entity.name
        }else{
            if(entity.resource.length>300){
                return ApiResult.error("API地址长度不能大于300")
            }
            if(!entity.resource.trim().HasValue){
                return ApiResult.error("API地址不能为空")
            }
        }

        //资源code不可编辑
        if(entity.id.HasValue){
            val oldResourceInfo= mor.tenant.tenantResourceInfo.query()
                .where { it.id match entity.id }
                .toEntity() ?: return ApiResult.error("资源不存在")
            if(oldResourceInfo.code!=entity.code){
                return ApiResult.error("资源Code不可更改")
            }

        }
        // 应用校验
        mor.tenant.tenantApplication.query()
            .where { it.appCode match entity.appInfo.code }
            .where { it.tenant.id match loginTenantAdminUser.tenant.id }
            .toEntity()
            .apply {
                if(this == null){
                    return ApiResult.error("应用不存在")
                }else{
                    if(!this.enabled){
                        return ApiResult.error("应用已停用")
                    }
                }
            }

        // 资源校验
        if(entity.id.HasValue){
            mor.tenant.tenantResourceInfo.query()
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                .where { it.id match entity.id }
                .toEntity()
                .apply {
                    if(this == null){
                        return ApiResult.error("资源不存在")
                    }
                }
        }

        if (entity.appInfo.code.HasValue) {
            // 同一租户下的资源名称不能重复
            var exists = mor.tenant.tenantResourceInfo.query()
                .where { it.tenant.id match entity.tenant.id }
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.code match entity.code }
                .apply {
                    if (entity.id.HasValue) {
                        this.where { it.id match_not_equal entity.id }
                    }
                }
                .exists()
            if (exists) {
                //如果是修改，需要保证除当前应用外，其他应用没有该资源名称
                return ApiResult.error("资源code已存在")
            }
        } else {
            return ApiResult.error("应用code不能为空")
        }

        if (codeList.size > 1 && entity.id.HasValue) { // 子资源编辑
            codeList.removeAt(codeList.size - 1)
            // 父资源校验
            codeList.forEach { item->
                mor.tenant.tenantResourceInfo.query()
                    .where { it.appInfo.code match entity.appInfo.code }
                    .where { it.code match item}
                    .toEntity()
                    .apply {
                        if (this == null) {
                            return ApiResult.error("父资源不存在")
                        }
                    }
            }
        }

        val code = entity.code
        val id = entity.id
        val dataType = entity.type
        if (codeList.size > 1 && !id.HasValue) { // 子资源父级补齐
            codeList.removeAt(codeList.size - 1)
            codeList.forEach { item->
                entity.id = "" // clean上次赋值的id
                entity.code = item // 当前层级的code
                entity.type = ResourceTypeEnum.Data // 默认数据类型
                mor.tenant.tenantResourceInfo.query()
                    .where { it.appInfo.code match entity.appInfo.code }
                    .where { it.code match  item}
                    .toEntity()
                    .apply {
                        if (this == null) { // 父资源不存在
                            // add
                            mor.tenant.tenantResourceInfo.doInsert(entity)
                                .apply {
                                    if (this.isEmpty()) {
                                        return ApiResult.error("父资源更新失败")
                                    }
                                }
                        }
                    }
            }

        }

        entity.code = code
        entity.id = id
        entity.code = code
        entity.type = dataType
        mor.tenant.tenantResourceInfo.updateWithEntity(entity)
            .run {
                if (entity.id.HasValue) {
                    request.logMsg = "租户侧资源{${entity.name}}修改"
                    return@run this.execUpdate()
                } else {
                    request.logMsg = "租户侧资源{${entity.name}}新增"
                    return@run this.execInsert()
                }
            }
            .apply {
                if (this == 0) {
                    return ApiResult.error("更新失败")
                }
                return ApiResult.of(entity.id)
            }
    }

    fun  delOneResource(code:String, tenantId:String) : JsonResult {
        mor.tenant.tenantResourceInfo.delete()
            .where { it.code match code }
            .where { it.tenant.id match tenantId }
            .where { it.isSysDefine match false }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }

                mor.tenant.tenantAppAuthResourceInfo.query()
                    .where { it.auths.code match code }.toList()
                    .apply {
                        this.forEach {
                            it.auths.removeAll{ it.code ==code}
                            mor.tenant.tenantAppAuthResourceInfo.updateWithEntity(it).execUpdate()

                            if(it.auths.size == 0){
                                mor.tenant.tenantAppAuthResourceInfo.deleteById(it.id).exec()
                            }
                        }
                    }
                return JsonResult()
            }
    }

}