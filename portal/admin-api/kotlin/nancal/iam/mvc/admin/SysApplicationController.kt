package nancal.iam.mvc.admin

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import nancal.iam.annotation.BizLog
import nancal.iam.client.ECClient
import nancal.iam.comm.logMsg
import nancal.iam.db.mongo.AppTagTypeEnum
import nancal.iam.db.mongo.BizLogActionEnum
import nancal.iam.db.mongo.BizLogResourceEnum
import nancal.iam.db.mongo.entity.SysApplication
import nancal.iam.db.mongo.entity.TenantApplication
import nancal.iam.db.mongo.mor
import nancal.iam.event.SystemApplicationVO
import nbcp.comm.*
import nbcp.db.CodeName
import nbcp.db.IdUrl
import nbcp.db.mongo.*
import nbcp.web.UserId
import org.bson.Document
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import kotlin.collections.ArrayList


/**
 * Created by CodeGenerator at 2021-11-17 17:43:16
 */
//@CheckTenantAppStatus
@Api(description = "应用", tags = ["SysApplication"])
@RestController
@RequestMapping("/admin/sys-application")
@OpenAction
class SysApplicationAutoController {

    @Resource
    lateinit var ecClient: ECClient

    class AppVo {
        var id: String = ""
        var name: String? = ""
        var appCode: String = ""
        var url: String? = ""
        var version: String = ""
        var authTenantCount = 0
        var appSourceDbId = ""
    }

    @ApiOperation("BOOS侧查询租户侧应用列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        name: String,
        appCode: String,
        address: String,
        keywords: String,
        isOnLine: Boolean?,
        tenantId: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<Document> {
        var appCodes: MutableList<String> = mutableListOf()
        mor.tenant.tenantApplication.query()
            .apply {
                if (tenantId.isEmpty()) {
                    this.defEntityName = mor.iam.sysApplication.tableName
                } else {
                    where { it.tenant.id match tenantId }
                }
            }.toList()
            .forEach {
                appCodes.add(it.appCode)
            }


        mor.tenant.tenantApplication.query()
            .where { it.isSysDefine match true }
            .apply {
                if (tenantId.isEmpty()) {
                    this.defEntityName = mor.iam.sysApplication.tableName
                    this.unSelect { MongoColumnName("privateKey") }
                    this.unSelect { MongoColumnName("publicKey") }
                } else {
                    this.where { it.tenant.id match tenantId }
                }


                this.where { it.appCode match_in appCodes }

                if (name.HasValue) {
                    whereOr({ it.name match_like name }, { it.ename match_like name })
                }
                if (id.HasValue) {
                    where { it.id match id }
                }
                if (keywords.HasValue) {
                    whereOr({ it.name match_like keywords }, { it.ename match_like keywords }, { it.appCode match_like keywords })
                }
                if (appCode.HasValue) { // 前端改名导致一系列映射发生改变
                    where { it.appCode match_like appCode }
                }
                if (address.HasValue) {
                    where { it.url match_like address }
                }
                if (isOnLine != null) {
                    where { it.isOnLine match isOnLine }
                }
            }
            .orderByDesc { it.createAt }
            .limit(skip, take)
            .toMapListResult()
            .apply {
                return this
            }
    }


    open class BossAppListVO : SysApplication() {
        var tenantCount: Int = 0

    }

    @ApiOperation("BOOS侧应用列表")
    @PostMapping("/boss-auth-list")
    fun bossAappListTo(
        id: String, //当列表列新一条后，刷新时使用
        name: String, // 支持name 和 appcode 模糊搜索
        appCode: String,
        enabled: Boolean?,
        isOnLine: Boolean?,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<BossAppListVO> {

        mor.iam.sysApplication.query()
            .apply {
                if (id.HasValue) {
                    where { it.id match id }
                }
                if (name.HasValue) {
                    whereOr({ it.name match_like name }, { it.appCode match_like name }, { it.ename match_like name })
                }
                if (appCode.HasValue) { // 前端改名导致一系列映射发生改变
                    where { it.appCode match_like appCode }
                }
                if (isOnLine != null) {
                    where { it.isOnLine match isOnLine }
                }
                if (enabled != null) {
                    where { it.enabled match enabled }
                }
            }
            .orderByDesc { it.createAt }
            .limit(skip, take)
            .toListResult(BossAppListVO::class.java)
            .apply {
                if (this.data.size > 0) {
                    this.data.forEach { bossApp ->
                        bossApp.tenantCount = mor.tenant.tenantApplication.query()
                            .where { tenantApp -> tenantApp.appCode match bossApp.appCode }
                            .count()
                    }
                }
                return this
            }
    }

    @ApiOperation("BOOS侧应用列表")
    @PostMapping("/boss-app-list")
    fun bossAappList(
        id: String, //当列表列新一条后，刷新时使用
        name: String, // 支持name 和 appcode 模糊搜索
        appCode: String,
        remark: String,
        enabled: Boolean?,
        isOnLine: Boolean?,
        keywords: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<BossAppListVO> {

        mor.iam.sysApplication.query()
            .apply {
                if (id.HasValue) {
                    where { it.id match id }
                }
                if (keywords.HasValue) {
                    whereOr({ it.name match_like keywords }, { it.ename match_like keywords },{ it.appCode match_like keywords })
                }
                if (name.HasValue) {
                    whereOr({ it.name match_like name }, { it.ename match_like name })
                }
                if (appCode.HasValue) { // 前端改名导致一系列映射发生改变
                    where { it.appCode match_like appCode }
                }
                if (remark.HasValue) {
                    where { it.remark match_like remark }
                }
                if (isOnLine != null) {
                    where { it.isOnLine match isOnLine }
                }
                if (enabled != null) {
                    where { it.enabled match enabled }
                }
            }
            .orderByDesc { it.createAt }.orderByDesc{it.id}
            .limit(skip, take)
            .toListResult(BossAppListVO::class.java)
            .apply {
                if (this.data.size > 0) {
                    this.data.forEach { bossApp ->
                        bossApp.tenantCount = mor.tenant.tenantApplication.query()
                            .where { tenantApp -> tenantApp.appCode match bossApp.appCode }
                            .count()
                    }
                }
                return this
            }
    }

/*
    @ApiOperation("详情")
    @PostMapping("/detail")
    fun detail(
         id: String,
         appCode: String,
        request: HttpServletRequest
    ): ApiResult<org.bson.Document> {
        if(!id.HasValue && !appCode.HasValue){
            return ApiResult.error("appCode 和 id 至少填写其中一项参数")
        }
        mor.iam.sysApplication.query()
            .unSelect { it.privateKey }
            .apply {
                if(appCode.HasValue){
                    this.where { it.appCode match appCode }
                }
                if(id.HasValue){
                    this.where { it.id match id }
                }
            }

            .toEntity(org.bson.Document::class.java)
            .apply {
                if (this == null) {
                    return ApiResult.error<org.bson.Document>("找不到数据")
                }
                return ApiResult.of(this)
            }
    }*/

    @ApiOperation("详情 id传的是id")
    @PostMapping("/detailByid")
    fun detailByid(
        id: String,
        appCode: String,
        request: HttpServletRequest
    ): ApiResult<SysApplication> {
        if (!id.HasValue && !appCode.HasValue) {
            return ApiResult.error("appCode和id至少填写其中一项参数")
        }
        mor.iam.sysApplication.query()
            .unSelect { it.privateKey }
            .apply {
                if (appCode.HasValue) {
                    this.where { it.appCode match appCode }
                }
                if (id.HasValue) {
                    this.where { it.id match id }
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

    @ApiOperation("同步列表")
    @PostMapping("/inStepList")
    fun inStepList(
/*        id: String, //当列表列新一条后，刷新时使用*/
        name: String,
        appCode: String,
        address: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<AppVo> {
        var resList: MutableList<AppVo> = ArrayList()
        var resListResult: ListResult<AppVo> = ListResult()
        mor.iam.sysApplication.query()
            .unSelect { it.privateKey }
            .unSelect { it.publicKey }
            .where { it.isOnLine match true }
            .apply {
                if (name.HasValue) {
                    this.where {
                        it.name match_like name
                    }
                }
                if (appCode.HasValue) {
                    this.where { it.appCode match_like appCode }
                }
                if (address.HasValue) {
                    this.where { it.url match_like address }
                }
                this.where { it.isOnLine match true }
            }
            .orderByDesc { it.createAt }
            .limit(skip, take)
            .toListResult()
            .apply {
                this.data.map { it ->
                    var app: AppVo = AppVo()
                    app.id = it.id
                    app.appCode = it.appCode
                    app.url = it.url
                    app.name = it.name
                    app.version = it.version
                    app.appSourceDbId = it.appSourceDbId
                    app.authTenantCount = mor.tenant.tenantApplication.query()
                        .where { it.appCode match app.appCode }
                        .count()


                    resList.add(app)
                }
                resListResult.total = this.total
                resListResult.code = this.code
            }
        resListResult.data = resList
        return resListResult

    }

    @BizLog(BizLogActionEnum.Enable, BizLogResourceEnum.App, "应用管理")
    @ApiOperation("BOSS侧应用启用")
    @PostMapping("/enable")
    fun enabled(
        @Require appCode: String,
        request: HttpServletRequest
    ): ApiResult<String> {
        val enabled = true
        request.logMsg = "应用启用{$appCode}"

        mor.iam.sysApplication.query()
            .where { it.appCode match appCode }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("应用不存在")
                }
                if (this.enabled == enabled) {
                    return ApiResult.error("应用已启用")
                }
            }

        mor.iam.sysApplication.update()
            .where { it.appCode match appCode }
            .set { it.enabled to enabled }
            .exec()
        if (enabled) {
            mor.tenant.tenantApplication.update()
                .where { it.appCode match appCode }
                .set { it.enabled to enabled }
                .exec()
        }


        return ApiResult()
    }

    @BizLog(BizLogActionEnum.Disable, BizLogResourceEnum.App, "应用管理")
    @ApiOperation("BOSS侧应用停用")
    @PostMapping("/disable")
    fun disabled(
        @Require appCode: String,
        request: HttpServletRequest
    ): ApiResult<String> {
        val enabled = false
        request.logMsg = "应用停用{$appCode}"

        mor.iam.sysApplication.query()
            .where { it.appCode match appCode }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("应用不存在")
                }
                if (this.enabled == enabled) {
                    return ApiResult.error("应用已停用")
                }
            }

        mor.iam.sysApplication.update()
            .where { it.appCode match appCode }
            .set { it.enabled to enabled }
            .exec()
        if (!enabled) {
            mor.tenant.tenantApplication.update()
                .where { it.appCode match appCode }
                .set { it.enabled to enabled }
                .exec()
        }


        return ApiResult()
    }

    class SaveToken {
        var id: String = ""
        var publicKey: String = ""

    }

    fun enameValidate(entity : SysApplication, request: HttpServletRequest): String {
        if (request.getHeader("lang") == "en" && !entity.ename.HasValue) {
            return "英文状态下英文名称不能为空"
        }
        if (request.getHeader("lang") == "cn" && !entity.name.HasValue) {
            return "中文状态下中文名称不能为空"
        }
        if (entity.name.length > 32 || entity.ename.length > 32) {
            return "名称长度不能超过32"
        }
        return ""
    }

    @BizLog(BizLogActionEnum.Save, BizLogResourceEnum.App, "应用管理")
    @ApiOperation("更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: SysApplication,
        request: HttpServletRequest
    ): ApiResult<SaveToken> {
        request.logMsg = "保存应用{${entity.appCode} ${entity.name}}"

        // 应用名称校验
        val enameValidate = enameValidate(entity, request)

        if (enameValidate != "") {
            return ApiResult.error(enameValidate)
        }
        // 校验参数
        val checkApplicationParam = checkApplicationParam(entity)

        if (checkApplicationParam != "") {
            return ApiResult.error(checkApplicationParam)
        }

        val saveToken = SaveToken()


        // 修改
        if (entity.id.HasValue) {
            val toEntity = mor.iam.sysApplication.queryById(entity.id).toEntity()
            if (toEntity == null) {
                return ApiResult.error("数据不存在")
            }
            mor.iam.sysApplication.updateWithEntity(entity)
                .withoutColumn { it.privateKey }
                .withoutColumn { it.publicKey }
                .withoutColumn { it.appCode }
                .execUpdate()
                .apply {
                    if (this == 0) {
                        return ApiResult.error("修改失败")
                    }
                    updateRelationList(toEntity, entity)

                    saveToken.id = entity.id
                    saveToken.publicKey = entity.publicKey
                    return ApiResult.of(saveToken)
                }
        } else { // 新增
            val keyStore = com.nancal.cipher.RSARawUtil.create()
            entity.privateKey = keyStore.privateKeyString
            entity.publicKey = keyStore.publicKeyString
            entity.appSourceDbId = UUID.randomUUID().toString().replace("-", "")
            if (entity.isOnLine == null) {
                entity.isOnLine = true
            }
            mor.iam.sysApplication.updateWithEntity(entity).execInsert()
                .apply {
                    if (this == 0) {
                        return ApiResult.error("新增失败")
                    }
                    saveToken.id = entity.id
                    saveToken.publicKey = entity.publicKey
                    return ApiResult.of(saveToken)
                }

        }
    }

    @BizLog(BizLogActionEnum.Update, BizLogResourceEnum.App, "应用管理")
    @ApiOperation("更新")
    @PostMapping("/updateByAppCode")
    fun updateByAppCode(
        @JsonModel entity: SysApplication,
        request: HttpServletRequest
    ): ApiResult<SaveToken> {
        request.logMsg = "保存应用{${entity.appCode} ${entity.name}}"

        // 应用名称校验
        val enameValidate = enameValidate(entity, request)
        if (enameValidate != "") {
            return ApiResult.error(enameValidate)
        }
        // 校验参数
        val checkApplicationParam = checkAppUpdateParam(entity)
        if (checkApplicationParam != "") {
            return ApiResult.error(checkApplicationParam)
        }
        val saveToken = SaveToken()
        // 修改
        val toEntity = mor.iam.sysApplication.queryByAppCode(entity.appCode).toEntity() ?: return ApiResult.error("应用不存在")
        entity.id=toEntity.id
        entity.publicKey=toEntity.publicKey
        mor.iam.sysApplication.update()
            .set { it.logo to entity.logo }
            .set { it.name to entity.name }
            .set { it.ename to entity.ename }
            .set { it.remark to entity.remark }
            .set { it.lable to entity.lable }
            .set { it.url to entity.url }
            .where{it.appCode match entity.appCode}
            .exec()
            .apply {
                if (this == 0) {
                    return ApiResult.error("修改失败")
                }
                updateRelationList(toEntity, entity)

                saveToken.id = entity.id
                saveToken.publicKey = entity.publicKey
                return ApiResult.of(saveToken)
            }

    }

    private fun updateRelationList(toEntity: SysApplication, entity: SysApplication) {


        // 级联修改租户侧相同《应用》的属性 【级联修改已经通过注解实现】
        mor.tenant.tenantApplication.query().where { it.appCode match toEntity.appCode }.toEntity()
            .apply {
                if (this != null) {
                    mor.tenant.tenantApplication.update()
                        .where { it.appCode match toEntity.appCode }
                        .set { it.enabled to entity.enabled }
                        .exec()
                }
            }

        // 级联修改租户侧拥有该应用的《用户》的属性
        mor.tenant.tenantUser.query().where { it.allowApps.code match toEntity.appCode }.toList()
            .apply {
                this.forEach { tenantUser ->
                    tenantUser.allowApps.forEach {
                        if (it.code == toEntity.appCode && it.name != entity.name) {
                            it.name = entity.name
                            mor.tenant.tenantUser.updateWithEntity(tenantUser).execUpdate()
                        }
                    }
                }
            }
        // 级联修改租户侧拥有该应用的《部门》的属性
        mor.tenant.tenantDepartmentInfo.query().where { it.allowApps.code match toEntity.appCode }.toList()
            .apply {
                this.forEach { tenantDepart ->
                    tenantDepart.allowApps.forEach {
                        if (it.code == toEntity.appCode && it.name != entity.name) {
                            it.name = entity.name
                            mor.tenant.tenantDepartmentInfo.updateWithEntity(tenantDepart).execUpdate()
                        }
                    }
                }
            }

        // 级联修改租户侧拥有该应用的《用户组》的属性
        mor.tenant.tenantUserGroup.query().where { it.allowApps.code match toEntity.appCode }.toList()
            .apply {
                this.forEach { group ->
                    group.allowApps.forEach {
                        if (it.code == toEntity.appCode && it.name != entity.name) {
                            it.name = entity.name
                            mor.tenant.tenantUserGroup.updateWithEntity(group).execUpdate()
                        }
                    }
                }
            }

        // 级联修改租户侧拥有该应用的《角色》的属性 【级联修改已经通过注解实现】
        // 级联修改租户侧拥有该应用的《资源》的属性 【级联修改已经通过注解实现】
        // 级联修改租户侧拥有该应用的《授权》的属性 【级联修改已经通过注解实现】
        // 级联修改BOSS侧拥有该应用的《资源》的属性 【级联修改已经通过注解实现】
        // 级联修改BOSS侧拥有该应用的《授权》的属性 【级联修改已经通过注解实现】
        // 级联修改BOSS侧拥有该应用的《角色》的属性 【级联修改已经通过注解实现】

    }

    private fun checkApplicationParam(ent: SysApplication): String {

        if (!ent.appCode.HasValue) {
            return "appCode不能为空"
        } else if (ent.appCode.length > 32) {
            return "appCode长度不能超过32"
        }
        if (ent.remark.HasValue && ent.remark!!.length > 255) {
            return "备注长度不能超过255"
        }
        if (!ent.url.HasValue) {
            return "url不能为空"
        } else {
            val urlPattern = "((http|ftp|https)://)([a-zA-Z0-9_-]+\\.)*"
            val isEmailMatch = Regex(urlPattern).containsMatchIn(ent.url!!)
            if (!isEmailMatch) {
                return "url地址格式错误"
            }
        }
        if (ent.url.toString().length > 225) {
            return "url长度最大225"
        }
        if (ent.lable.isEmpty()) {
            return "标签不能为空"
        }
        if (ent.lable.isNotEmpty()) {
            ent.lable.forEach {
                if (!tagValidate(it)) {
                    return "标签参数错误"
                }
            }
        }
        //编辑
        if (ent.id.HasValue) {
            val app = mor.iam.sysApplication.query()
                .where { it.id match ent.id }
                .toEntity()
            if (app == null) {
                return "应用不存在"
            }
            if (app.appCode != ent.appCode) {
                return "appCode不可更改"
            }
        }
        //新增
        if (!ent.id.HasValue) {
            mor.iam.sysApplication.query()
                .where { it.appCode match ent.appCode }
                .exists()
                .apply {
                    if (this) {
                        return "appCode不能重复"
                    }
                }
            mor.tenant.tenantApplication.query()
                .where { it.appCode match ent.appCode }
                .exists()
                .apply {
                    if (this) {
                        return "appCode已存在"
                    }
                }
        }
        return ""
    }

    fun tagValidate(codeName: CodeName) : Boolean {
        AppTagTypeEnum.values().forEach { b->
            if(codeName.code == b.name) {
                if (codeName.name == b.remark)  {
                    return true
                }
            }
        }
        return false
    }

    private fun checkAppUpdateParam(ent: SysApplication): String {

        if (!ent.appCode.HasValue) {
            return "appCode不能为空"
        }
        if (!ent.name.trim().HasValue) {
            return "应用名称不能为空"
        } else if (ent.name.length > 32) {
            return "名称长度不能超过32"
        }
        if (ent.remark.HasValue && ent.remark!!.length > 255) {
            return "备注长度不能超过255"
        }
        if (!ent.url.HasValue) {
            return "url不能为空"
        } else {
            val urlPattern = "((http|ftp|https)://)([a-zA-Z0-9_-]+\\.)*"
            val isEmailMatch = Regex(urlPattern).containsMatchIn(ent.url!!)
            if (!isEmailMatch) {
                return "url地址格式错误"
            }
        }
        if (ent.url.toString().length > 225) {
            return "url长度最大225"
        }
        if (ent.lable.isNotEmpty()) {
            ent.lable.forEach {
                if (!tagValidate(it)) {
                    return "标签参数错误"
                }
            }
        }
        mor.iam.sysApplication.query()
            .where { it.appCode match ent.appCode }
            .toEntity()
            .apply {
                if (this==null) {
                    return "应用不存在"
                }else{
                    if(!this.enabled) return "应用已停用"
                }
            }
        return ""
    }

    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.App, "应用管理")
    @ApiOperation("删除")
    @PostMapping("/delete")
    fun delete(
        id: String,
        appCode: String,
        request: HttpServletRequest
    ): JsonResult {
        if (!id.HasValue && !appCode.HasValue) {
            return JsonResult.error("appCode和id至少填写其中一项参数")
        }
        val toEntity = mor.iam.sysApplication
            .query()
            .apply {
                if (appCode.HasValue) {
                    this.where { it.appCode match appCode }
                }
                if (id.HasValue) {
                    this.where { it.id match id }
                }
            }
            .toEntity() ?: return JsonResult.error("找不到数据")
        val id = toEntity.id
        val appCode = toEntity.appCode

        request.logMsg = "删除应用{${toEntity.appCode}}"

        mor.iam.sysApplication.deleteById(id)
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }

                mor.iam.sysAppRole.delete()
                    .where { it.appInfo.code match toEntity.appCode }.exec()

                mor.iam.sysResourceInfo.delete()
                    .where { it.appInfo.code match toEntity.appCode }.exec()

                mor.iam.sysAppAuthResource.delete()
                    .where { it.appInfo.code match toEntity.appCode }.exec()

                mor.tenant.tenantAppRole.delete()
                    .where { it.appInfo.code match toEntity.appCode }.exec()

                mor.tenant.tenantResourceInfo.delete()
                    .where { it.appInfo.code match toEntity.appCode }.exec()

                mor.tenant.tenantAppAuthResourceInfo.delete()
                    .where { it.appInfo.code match toEntity.appCode }.exec()

                mor.tenant.tenantApplication.delete()
                    .where { it.appCode match toEntity.appCode }.exec()

                mor.tenant.tenantDepartmentInfo.query()
                    .where { it.allowApps.code match toEntity.appCode }.toList()
                    .apply {
                        this.forEach {
                            it.allowApps.removeAll { it.code == toEntity.appCode }
                            mor.tenant.tenantDepartmentInfo.updateWithEntity(it).execUpdate()
                        }
                    }

                mor.tenant.tenantUserGroup.query()
                    .where { it.allowApps.code match toEntity.appCode }.toList()
                    .apply {
                        this.forEach {
                            it.allowApps.removeAll { it.code == toEntity.appCode }
                            mor.tenant.tenantUserGroup.updateWithEntity(it).execUpdate()
                        }
                    }

                mor.tenant.tenantUser.query()
                    .where { it.allowApps.code match toEntity.appCode }.toList()
                    .apply {
                        this.forEach {
                            it.allowApps.removeAll { it.code == toEntity.appCode }
                            mor.tenant.tenantUser.updateWithEntity(it).execUpdate()
                        }
                    }
                return JsonResult()
            }
    }

    companion object {
        fun singleUpdate(app: SystemApplicationVO): Int {
            val sa = SysApplication()
            sa.appSourceDbId = app.id
            sa.name = app.applicationName
            sa.logo = IdUrl("", "${app.logo}")
            sa.isOnLine = app.status == 1
            sa.url = app.address
            sa.appCode = app.appCode
            sa.protal = app.protal

            val sysApp = mor.iam.sysApplication.query().where { it.appSourceDbId match sa.appSourceDbId }.toEntity()
            val keyStore = com.nancal.cipher.RSARawUtil.create()
            if (sysApp != null) {
                sa.privateKey = sysApp.privateKey
                sa.publicKey = sysApp.publicKey
            } else {
                sa.privateKey = keyStore.privateKeyString
                sa.publicKey = keyStore.publicKeyString
            }

            // 企业容器的MQ分三种，新增修改，启用禁用。新增修改时自动去更新库，启用禁用与IAM无关，不需要更新库
            if (app.action == "启用" || app.action == "禁用") {
                return 1
            }

            return mor.iam.sysApplication.updateWithEntity(sa)
                .whereColumn { it.appSourceDbId }
                .execUpdate()
        }
    }
}

