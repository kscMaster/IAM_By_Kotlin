package nancal.iam.mvc.tenant

import io.swagger.annotations.*
import nancal.iam.annotation.BizLog
import nancal.iam.annotation.CheckAuthSource
import nancal.iam.annotation.CheckTenantAppStatus
import nancal.iam.client.RuleEngineClient
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import org.springframework.web.bind.annotation.*
import nbcp.comm.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.mongo.table.TenantGroup
import nancal.iam.mvc.iam.ResourceInfoAutoController
import nancal.iam.service.AuthSourceService
import nancal.iam.util.CodeUtils
import nbcp.db.*
import nbcp.utils.CodeUtil
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Value
import javax.annotation.Resource
import javax.servlet.http.*

/**
 * Created by CodeGenerator at 2021-12-13 15:32:17
 */
@CheckAuthSource
@CheckTenantAppStatus
@Api(description = "资源授权", tags = arrayOf("AppAuthResourceInfo"))
@RestController
@RequestMapping("/tenant/authrization")
class AppAuthResourceInfoAutoController {
    @Value("\${openPrivatization}")
    private val openPrivatization: Boolean = true

    @Resource
    lateinit var authSourceService: AuthSourceService;

    @Resource
    lateinit var ruleClient: RuleEngineClient

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        name: String,// 授权主体名称
        remark: String,
        @Require appInfoId: String,
        appInfoName: String,
        sourceName: String, // 资源名称
        type: String,
        keywords: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantAppAuthResourceInfo> {


        mor.tenant.tenantAppAuthResourceInfo.query()
            .apply {
                checkListParam(id, type, name, remark, sourceName, keywords)
                if (appInfoName.HasValue) {
                    this.where { it.appInfo.name match_like appInfoName }
                }
                this.where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                this.where { it.appInfo.code match appInfoId }
            }
            .limit(skip, take).orderByDesc { it.createAt }.orderByDesc { it.id }
            .toListResult()
            .apply {
                return this;
            }
    }

    private fun MongoQueryClip<TenantGroup.TenantAppAuthResourceInfoEntity, TenantAppAuthResourceInfo>.checkListParam(
        id: String,
        type: String,
        name: String,
        remark: String,
        sourceName: String,
        keywords: String
    ) {
        if (id.HasValue) {
            this.where { it.id match id }
        }
        if (keywords.HasValue) {
            this.where { it.auths.isAllow match true }
            this.whereOr({ it.type match keywords }, { it.target.name match_like keywords }, { it.auths.name match_like keywords })
        }
        if (type.HasValue) {
            this.where { it.type match type }
        }
        if (name.HasValue) {
            this.where { it.target.name match_like name }
        }
        if (remark.HasValue) {
            this.where { it.remark match_like remark }
        }
        if (sourceName.HasValue) {
            this.where { it.auths.isAllow match true }
            this.where { it.auths.name match_like sourceName }
        }
    }

    class DetailVO : BaseEntity() {

        var appInfo: CodeName = CodeName()

        var tenant: IdName = IdName()

        var type: AuthTypeEnum? = null

        var target: IdName = IdName()

        var auths: MutableList<AuthResourceInfoVO> = mutableListOf()

        var childDeptsAll: Boolean = false

        var isSysDefine: Boolean = false

        var sysId: String = ""

        var remark: String = ""

        var isDeleted: Boolean? = false

    }

    class DetailVOTemp : BaseEntity() {

        var appInfo: CodeName = CodeName()

        var tenant: IdName = IdName()

        var type: AuthTypeEnum? = null

        var target: IdName = IdName()

        var auths: MutableList<AuthResourceInfoVOTemp> = mutableListOf()

        var childDeptsAll: Boolean = false

        var isSysDefine: Boolean = false

        var sysId: String = ""

        var remark: String = ""

        var isDeleted: Boolean? = false

    }

    class AuthResourceInfoVO : ResourceBaseInfo() {
        var id: String = "" //授权Id,唯一标识
        var resourceId: String = "" //资源Id
        var actionIsAll: Boolean = false
        var resourceIsAll: Boolean = false
        var isAllow: Boolean = false
        var rules: MutableList<TenantAuthRules> = mutableListOf()
        var actionOfResource: MutableList<String> = mutableListOf()
    }

    class AuthResourceInfoVOTemp : ResourceBaseInfo() {
        var id: String = "" //授权Id,唯一标识
        var resourceId: String = "" //资源Id
        var actionIsAll: Boolean = false
        var resourceIsAll: Boolean = false
        var isAllow: Boolean = false
        var rules: MutableList<TenantAuthRule> = mutableListOf()
        var parentResource: MutableList<TenantResourceInfo> = mutableListOf()
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

        mor.tenant.tenantAppAuthResourceInfo.query()
            .where { it.id match id }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity(DetailVO::class.java)
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }

                this.auths.forEach { vo ->
                    mor.tenant.tenantResourceInfo.query().where { it.id match vo.resourceId }.toEntity()
                        ?.apply {
                            vo.actionOfResource = this.action
                            vo.dataAccessLevel = this.dataAccessLevel
                        }
                }

                return ApiResult.of(this)
            }
    }


    @ApiOperation("详情")
    @PostMapping("/detailTemp/{id}")
    fun detailTemp(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<DetailVOTemp> {
        var toEntity: DetailVOTemp? = mor.tenant.tenantAppAuthResourceInfo.query()
            .where { it.id match id }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity(DetailVOTemp::class.java)

        if (toEntity == null) {
            return ApiResult.error("找不到数据")
        }

        toEntity.auths.forEach { vo ->
            mor.tenant.tenantResourceInfo.query().where { it.id match vo.resourceId }.toEntity()
                ?.apply {
                    var count = 0
                    var actionList = mutableListOf<LabelValue>()
                    this.action.forEach { actionObj ->
                        actionList.add(LabelValue(actionObj, actionObj))
                        count++
                    }
                    if (!vo.resourceIsAll) {
                        vo.parentResource =
                            listResourceParent(this.code, this.appInfo.code, request.LoginTenantAdminUser.tenant.id)
                    }
                    vo.actionOfResource = actionList

                }
        }
        return ApiResult.of(toEntity)
    }


    fun listResourceParent(code: String, appCode: String, tenantId: String): MutableList<TenantResourceInfo> {
        val result: MutableList<TenantResourceInfo> = mutableListOf()
        var codeList: MutableList<String> = CodeUtils.codeHolder(code)
        if (codeList.size == 1) {
            mor.tenant.tenantResourceInfo.query()
                .where { it.appInfo.code match appCode }
                .where { it.tenant.id match tenantId }
                .where { it.code match code }.toEntity()
                .let {
                    if (it != null) {
                        result.add(it)
                    }
                }

            return result

        } else {
            codeList.forEach { item ->
                val toEntity: TenantResourceInfo? = mor.tenant.tenantResourceInfo.query()
                    .where { it.appInfo.code match appCode }
                    .where { it.tenant.id match tenantId }
                    .where { it.code match item }.toEntity()
                toEntity?.let {
                    result.add(it)
                }
            }
            return result
        }
    }


    @BizLog(BizLogActionEnum.Authorize, BizLogResourceEnum.AuthResource, "租户端资源授权")
    @ApiOperation("资源授权")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: TenantAppAuthResourceInfo,
        targets: MutableList<IdName>,
        request: HttpServletRequest
    ): ApiResult<String> {


        if (!entity.id.HasValue) {
            request.logMsg = "新增授权"
        } else {
            request.logMsg = "修改授权"
        }

        //鉴权
        var loginUser = request.LoginTenantAdminUser
        entity.tenant = loginUser.tenant
        if (entity.auths.size > 0) {
            entity.auths.forEach { auth ->
                if (!auth.id.HasValue) {
                    auth.id = CodeUtil.getCode()
                }

                if (auth.resourceIsAll) {
                    auth.code = "*"
                    auth.actionIsAll = true
                } else {
                    if (auth.resourceId.equals("") || auth.name.equals("")) {
                        return ApiResult.error("指定资源需要关联id或名称")
                    }
                    mor.tenant.tenantResourceInfo.query()
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
            }
        }
        if (entity.appInfo.code.isEmpty() || entity.appInfo.name.isEmpty()) {
            return ApiResult.error("应用不能为空")
        }
        val checkInsertParam = checkInsertParam(entity, targets)
        if (checkInsertParam != "") {
            return ApiResult.error(checkInsertParam)
        }

        var authList: MutableList<TenantAppAuthResourceInfo> = mutableListOf()
        if (entity.type != AuthTypeEnum.Role) {
            targets.forEach {
                val authObj = entity.ToJson().FromJson<TenantAppAuthResourceInfo>()
                if (authObj != null) {
                    authObj.target = it
                }
                if (authObj != null) {
                    authList.add(authObj)
                }
            }
        } else {
            authList.add(entity)
        }

        // 校验新增
        val extraSaveCheck = extraSaveCheck(authList)
        if (extraSaveCheck != "") {
            return ApiResult.error(extraSaveCheck)
        }

        if (entity.id.HasValue) {

            if (authList.size != 1) { // 修改
                return ApiResult.error("修改只能修改单条")
            }

            mor.tenant.tenantAppAuthResourceInfo.query().where { it.id match entity.id }.toEntity()
                .apply {
                    if (this == null) {
                        return ApiResult.error("修改数据不存在")
                    }
                    if (entity.type != this.type) {
                        return ApiResult.error("授权主体类型不允许修改")
                    }

                    if (entity.type == AuthTypeEnum.Role) {
                        if (entity.target.id != this.target.id) {
                            return ApiResult.error("授权主体id不允许修改")
                        }
                        if (entity.target.name != this.target.name) {
                            return ApiResult.error("授权主体名称不允许修改")
                        }
                    } else {
                        if (targets.get(0).id != this.target.id) {
                            return ApiResult.error("授权主体id不允许修改")
                        }
                        if (targets.get(0).name != this.target.name) {
                            return ApiResult.error("授权主体名称不允许修改")
                        }
                    }
                }


            var insertRuleResult: MutableList<RuleInsertVo> = mutableListOf()

            authList[0].auths.forEach {
                if (it.rules.size > 0) {
                    var insertRule = RuleInsertVo()
                    insertRule.conditionValueList = it.rules
                    insertRule.resourceId = it.id
                    insertRuleResult.add(insertRule)
                }
            }
            if (insertRuleResult.size > 0) {

                val checkAuthRule = checkAuthRuleParam(insertRuleResult)
                if (checkAuthRule != "") {
                    return ApiResult.error(checkAuthRule)
                }

                val addConditionValue = ruleClient.addConditionValue(insertRuleResult)
                if (addConditionValue.code != 0) {
                    return ApiResult.error(addConditionValue.msg.toString())
                }

                var deleteRuleResult: MutableList<String> = mutableListOf()

                mor.tenant.tenantAppAuthResourceInfo.queryById(entity.id).toEntity()
                    .apply {
                        if (this != null) {
                            val newAuths = authList[0].auths.map { it.id }
                            this.auths.map { it.id }.forEach {
                                if (!newAuths.contains(it)) {
                                    deleteRuleResult.add(it)
                                }
                            }
                        }
                    }
                if (deleteRuleResult.size > 0) {
                    ruleClient.deleteConditionValue(deleteRuleResult)
                }
            }

            mor.tenant.tenantAppAuthResourceInfo.updateWithEntity(authList[0]).execUpdate()
                .apply {
                    if (this == 0) {
                        return ApiResult.error("修改失败")
                    }
                    return ApiResult()
                }
        }

        var insertRuleResult: MutableList<RuleInsertVo> = mutableListOf()
        authList.forEach { authss ->
            authss.auths.forEach {
                if (it.rules.size > 0) {
                    var insertRule = RuleInsertVo()
                    insertRule.conditionValueList = it.rules
                    insertRule.resourceId = it.id
                    insertRuleResult.add(insertRule)
                }
            }
        }

        if (insertRuleResult.size > 0) {
            val checkAuthRule = checkAuthRuleParam(insertRuleResult)
            if (checkAuthRule != "") {
                return ApiResult.error(checkAuthRule)
            }
            val addConditionValue = ruleClient.addConditionValue(insertRuleResult)
            if (addConditionValue.code != 0) {
                return ApiResult.error(addConditionValue.msg.toString())
            }
        }


        mor.tenant.tenantAppAuthResourceInfo.batchInsert()
            .apply {
                addEntities(authList)
            }
            .exec()

        if (authList.size == 1) {
            return ApiResult.of(authList[0].id)
        }
        return ApiResult()
    }

    private fun checkAuthRuleParam(insertRuleResult: MutableList<RuleInsertVo>): String {
        insertRuleResult.forEach { vo ->
            vo.conditionValueList.forEach {
                if (it.conditionType != 202 && it.conditionType != 203) {
                    it.conditionType = 201
                }
                if (!it.parentCodeName.HasValue) {
                    return "一级名称 不能为空"
                }
                if (!it.parentId.HasValue || it.parentId == 0) {
                    return "parentId参数错误"
                }
                if (!it.rulesCodeVoSon.codeName.HasValue) {
                    return "二级名称 不能为空"
                }
                if (!it.rulesCodeVoSon.codeId.HasValue || it.rulesCodeVoSon.codeId == 0) {
                    return "codeId参数错误"
                }
                if (!it.conditionValue.HasValue) {
                    return "值不能为空"
                }
                if (it.conditionValue.length > 125) {
                    return "值不能超过125"
                }
            }
        }
        return ""
    }

    class RuleInsertVo(
        var resourceId: String = "",
        var conditionValueList: MutableList<TenantAuthRule> = mutableListOf()
    )


    private fun extraSaveCheck(authList: MutableList<TenantAppAuthResourceInfo>): String {

        if (authList.size < 1) {
            return "请选择授权资源"
        }

        // 校验授权主体是否存在
        if (authList.get(0).type == AuthTypeEnum.People) {
            mor.tenant.tenantUser.query()
                .where { it.id match authList.get(0).target.id }
                .where { it.enabled match true }
                .apply {
                    if (openPrivatization) {
                        this.where { it.adminType match TenantAdminTypeEnum.None }
                    }
                }
                .toEntity()
                .apply {
                    if (this == null) {
                        return "授权主体人不存在或被禁用"
                    }
                }
        }

        if (authList.get(0).type == AuthTypeEnum.Role) {
            mor.tenant.tenantAppRole.query()
                .where { it.id match authList.get(0).target.id }
                .toEntity()
                .apply {
                    if (this == null) {
                        return "授权主体角色不存在"
                    }
                }
        }

        if (authList.get(0).type == AuthTypeEnum.Dept) {
            mor.tenant.tenantDepartmentInfo.query()
                .where { it.id match authList.get(0).target.id }
                .toEntity()
                .apply {
                    if (this == null) {
                        return "授权主体部门不存在"
                    }
                }
        }

        if (authList.get(0).type == AuthTypeEnum.Group) {
            mor.tenant.tenantUserGroup.query()
                .where { it.id match authList.get(0).target.id }
                .toEntity()
                .apply {
                    if (this == null) {
                        return "授权主体用户组不存在"
                    }
                }
        }

        return ""

    }

    private fun checkInsertParam(entity: TenantAppAuthResourceInfo, targets: MutableList<IdName>): String {


        mor.tenant.tenantApplication.query()
            .where { it.appCode match entity.appInfo.code }
            .whereOr({ it.name match entity.appInfo.name }, { it.ename match entity.appInfo.name })
            .where { it.tenant.id match entity.tenant.id }
            .toEntity()
            .apply {
                if (this == null) {
                    return "应用不存在，请核对"
                }
            }

        if (entity.type == null) {
            return "授权主体类型不能为空"
        }

        if (entity.type == AuthTypeEnum.Role) {

            if (!entity.target.id.HasValue) {
                return "授权主体id不能为空"
            }
            mor.tenant.tenantAppRole.query()
                .where { it.id match entity.target.id }
                .where { it.name match entity.target.name }
                .where { it.appInfo.code match entity.appInfo.code }
//                .where { it.appInfo.name match entity.appInfo.name }
                .where { it.tenant.id match entity.tenant.id }
                .toEntity()
                .apply {
                    if (this == null) {
                        return "角色授权主体不存在或数据有误"
                    }
                }
        }


        if (entity.type == AuthTypeEnum.People) {

            if (targets.size < 1) {
                return "用户授权主体不能为空"
            }

            targets.forEach { targetTemp ->
                mor.tenant.tenantUser.query()
                    .where { it.id match targetTemp.id }
                    .where { it.name match targetTemp.name }
                    .where { it.tenant.id match entity.tenant.id }
                    .toEntity()
                    .apply {
                        if (this == null) {
                            return "用户授权主体不存在或数据有误"
                        }
                    }
            }
        }

        if (entity.type == AuthTypeEnum.Dept) {

            if (targets.size < 1) {
                return "用户授权主体不能为空"
            }

            targets.forEach { targetTemp ->
                mor.tenant.tenantDepartmentInfo.query()
                    .where { it.id match targetTemp.id }
                    .where { it.name match targetTemp.name }
                    .where { it.tenant.id match entity.tenant.id }
                    .toEntity()
                    .apply {
                        if (this == null) {
                            return "部门授权主体不存在或数据有误"
                        }
                    }
            }
        }

        if (entity.type == AuthTypeEnum.Group) {

            if (targets.size < 1) {
                return "用户授权主体不能为空"
            }

            targets.forEach { targetTemp ->
                mor.tenant.tenantUserGroup.query()
                    .where { it.id match targetTemp.id }
                    .where { it.name match targetTemp.name }
                    .where { it.tenant.id match entity.tenant.id }
                    .toEntity()
                    .apply {
                        if (this == null) {
                            return "用户组授权主体不存在或数据有误"
                        }
                    }
            }
        }

        if (entity.auths.size > 0) {
            entity.auths.forEach { authResourceInfo ->
                if (authResourceInfo.resourceIsAll == false) {
                    if (!authResourceInfo.id.HasValue) {
                        authResourceInfo.id = CodeUtil.getCode()
                    }
                    if (!authResourceInfo.resourceId.HasValue) {
                        return "资源ID不能为空"
                    }

                    if (authResourceInfo.type == null) {
                        return "资源类型不能为空"
                    }
                    if (!authResourceInfo.resource.HasValue) {
                        return "资源resource不能为空"
                    }
                    if (!authResourceInfo.code.HasValue) {
                        return "资源code不能为空"
                    }

                    mor.tenant.tenantResourceInfo.query()
                        .where { it.id match authResourceInfo.resourceId }
                        .where { it.appInfo.code match entity.appInfo.code }
//                        .where { it.appInfo.name match entity.appInfo.name }
                        .where { it.name match authResourceInfo.name }
                        .where { it.code match authResourceInfo.code }
                        .where { it.type match authResourceInfo.type }
                        .toEntity()
                        .apply {
                            if (this == null) {
                                return "资源与应用关系不匹配"
                            }
                            this.action.add("*")
                            if (!this.action.containsAll(authResourceInfo.action)) {
                                return "资源操作数据异常"
                            }
                        }

                } else {
                    if (authResourceInfo.actionIsAll == false) {
                        return "所有资源的操作必须选择所有"
                    }
                }

            }
        }
        return ""
    }

    @BizLog(BizLogActionEnum.CancelAuthorize, BizLogResourceEnum.AuthResource, "租户端删除资源授权")
    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "取消授权"

        var entity = mor.tenant.tenantAppAuthResourceInfo.queryById(id).toEntity()
        if (entity == null) {
            return JsonResult.error("找不到数据")
        }

        var deleteRuleResult: MutableList<String> = mutableListOf()

        if (entity.auths.size > 0) {
            entity.auths.forEach {
                if (it.rules.size > 0) {
                    deleteRuleResult.add(it.id)
                }
            }
        }

        mor.tenant.tenantAppAuthResourceInfo.delete()
            .where { it.id match id }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .where { it.isSysDefine match false }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
                if (deleteRuleResult.size > 0) {
                    ruleClient.deleteConditionValue(deleteRuleResult)
                }
                //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
                return JsonResult()
            }
    }

//    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.AuthResource, "租户端批量删除资源授权")
//    @ApiOperation("批量删除")
//    @PostMapping("/delete/batch")
//    fun deleteBatch(
//        @Require ids: List<String>,
//        request: HttpServletRequest
//    ): JsonResult {
//        request.logMsg = "批量删除授权"
//        mor.tenant.tenantAppAuthResourceInfo.delete()
//            .where { it.id match_in ids }
//            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
//            .where { it.isSysDefine match false }
//            .exec()
//        return JsonResult()
//    }


    @ApiOperation("查询应用下用户的资源列表")
    @PostMapping("/app-user-authrization/list")
    fun userAuthrizatinList(
        @Require userId: String,
        @Require appCode: String, //当列表列新一条后，刷新时使用
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantAppAuthResourceInfo> {
        return authSourceService.userAuthrizatinList(userId, appCode, null, skip, take)
    }


}