package nancal.iam.mvc.tenant

import io.swagger.annotations.*
import nancal.iam.annotation.BizLog
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nbcp.comm.*
import nbcp.db.*
import nbcp.db.mongo.*
import nbcp.db.mongo.entity.*
import nbcp.web.LoginUser
import nbcp.web.UserId
import org.bson.Document
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.*

/**
 * Created by CodeGenerator at 2021-11-17 17:43:16
 */
@Api(description = "应用", tags = arrayOf("SysApplication"))
@RestController
@RequestMapping("/tenant/sys-application")
class SysApplicationAutoController {
    @Value("\${openPrivatization}")
    private val openPrivatization: Boolean = true

    class AuthObjectVo {
        var name = ""
        var cate = AuthObjectEnum.TenantUser
        var id = ""

    }

    //主要用于给用户授权
    class AuthObject {
        var app: IdName = IdName()
        var cate = AuthObjectEnum.TenantUser

        //授权对象id
        var id = ""
    }

    //用户组 部门 用户 授权
    class AuthObj {
        var cate = AuthObjectEnum.TenantUser

        //授权对象id
        var id = ""
    }

    class AuthAppVo {
        var appCode: String = ""
        var name: String = ""
        var ename: String = ""
        var logo: IdUrl? = IdUrl()
        var industry: MutableList<IdName> = mutableListOf()
        var isOnLine: Boolean? = true
        var url: String? = ""
        var tenant: IdName = IdName()
        var lable: MutableList<CodeName> = mutableListOf()
        var id: String = ""
        var authObj: MutableList<String> = mutableListOf()
        var enabled: Boolean = true

    }

    class RoleAppVo {
        var appCode: String = ""
        var name: String = ""
        var ename: String = ""
        var logo: IdUrl? = IdUrl()
        var tenant: IdName = IdName()
        var industry: MutableList<IdName> = mutableListOf()
        var isOnLine: Boolean? = true
        var url: String? = ""
        var lable: MutableList<CodeName> = mutableListOf()
        var id: String = ""
        var roleCode: MutableList<String> = mutableListOf()
        var enabled: Boolean = true

    }

    class ResourceAppVo {
        var appCode: String = ""
        var name: String = ""
        var ename: String = ""
        var logo: IdUrl? = IdUrl()
        var tenant: IdName = IdName()
        var industry: MutableList<IdName> = mutableListOf()
        var isOnLine: Boolean? = true
        var url: String? = ""
        var lable: MutableList<CodeName> = mutableListOf()
        var id: String = ""
        var resourceName: MutableList<String> = mutableListOf()
        var enabled: Boolean = true
    }

    class IdNameVersionVo {
        var id: String = ""
        var name: String = ""
        var ename: String = ""
        var version: String = ""
        var appCode: String = ""
        var enabled: Boolean = true
        var logo: IdUrl = IdUrl()
        var url = ""
        var remark = ""

    }

    class AppDetailVo {
        var id: String = ""
        var appCode: String = ""
        var name: String = ""
        var url: String = ""
        var logo: IdUrl = IdUrl()
        var remark = ""
        var enabled: Boolean = true
        var publicKey: String = ""

    }

    class AppVo {
        var id: String = ""
        var appCode: String = ""
        var name: String = ""
        var count = 0

    }

    class SysApplicationVO : TenantApplication()

    class TenantAppSetting : IdName() {
        var isOpen: Boolean = false
        var enabled: Boolean = true

    }


    /**
     * @Description 列表租户管理员调用
     *
     * @param
     * @return
     * @date 10:21 2021/12/24
     */
    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        name: String,
        appCode: String,
        keywords: String,
        enabled: Boolean?,
        @Require skip: Int,
        @Require
        take: Int,
        request: HttpServletRequest
    ): ListResult<TenantApplication> {
        var tenantId = request.LoginTenantAdminUser.tenant.id
        mor.tenant.tenant.queryById(tenantId).toEntity() ?: return ListResult.error("找不到租户")
        var toListResult: ListResult<TenantApplication> = mor.tenant.tenantApplication.query()
            .apply {
                this.where { it.tenant.id match tenantId }
                if (keywords.HasValue) {
                    whereOr({ it.name match_like keywords }, { it.ename match_like keywords },
                        { it.appCode match_like keywords })
                }
                if (name.HasValue) {
                    whereOr({ it.name match_like name }, { it.ename match_like name })
                }
                if (appCode.HasValue) {
                    this.where { it.appCode match_like appCode }
                }
                if (enabled != null) {
                    this.where { it.enabled match enabled }
                }
            }.orderByDesc { it.createAt }.orderByDesc { it.id }.limit(skip, take)
            .toListResult()

        return toListResult

    }


    /**
     * @Description 当前应用详情列表（用户组，部门，用户）租户管理员调用
     *
     * @param
     * @return
     * @date 10:21 2021/12/24
     */
    @ApiOperation("当前应用详情列表（用户组，部门，用户）")
    @PostMapping("/detail")
    fun detail(
        @Require appCode: String,
        request: HttpServletRequest
    ): ApiResult<Any> {
        val tenantId = request.LoginTenantAdminUser.tenant.id
        mor.tenant.tenantApplication.query().where { it.appCode match appCode }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error<Any>("找不到数据")
                }
                mor.tenant.tenantApplication.query()
                    .where { it.tenant.id match tenantId }
                    .where { it.appCode match appCode }
                    .exists()
                    .apply {
                        if (!this) return ApiResult.error("您没有该应用的权限")
                    }

                /*
            * 1.查组织机构
            * 2.查用户组
            * 3.查用户
            * 4.拼在一起
            * */
                var list: ArrayList<AuthObjectVo>? = ArrayList()
                mor.tenant.tenantDepartmentInfo.query().apply {
                    this.where { it.tenant.id match tenantId }
                    this.where { it.allowApps.code match appCode }
                }.toListResult().data.map { data ->
                    if (list != null) {
                        var obj: AuthObjectVo = AuthObjectVo()
                        obj.id = data.id
                        obj.name = data.name
                        obj.cate = AuthObjectEnum.DepartmentInfo
                        list.add(obj)
                    }
                }

                mor.tenant.tenantUserGroup.query().apply {
                    this.where { it.tenant.id match tenantId }
                    this.where { it.allowApps.code match appCode }
                }.toListResult().data.map { data ->
                    if (list != null) {
                        var obj: AuthObjectVo = AuthObjectVo()
                        obj.id = data.id
                        obj.name = data.name
                        obj.cate = AuthObjectEnum.TenantUserGroup
                        list.add(obj)
                    }
                }

                mor.tenant.tenantUser.query().apply {
                    this.where { it.tenant.id match tenantId }
                    this.where { it.allowApps.code match appCode }
                }.toListResult().data.map { data ->
                    if (list != null) {
                        var obj: AuthObjectVo = AuthObjectVo()
                        obj.id = data.id
                        obj.name = data.name
                        obj.cate = AuthObjectEnum.TenantUser
                        list.add(obj)
                    }
                }
                if (list != null) {
                    list.toArray().toSet()
                }
                val tenant = mor.tenant.tenant.queryById(tenantId).toEntity() ?: return ApiResult.error("找不到租户")
                var tenantApp = mor.tenant.tenantApplication.query()
                    .apply {
                        this.where { it.tenant.id match tenantId }
                        this.where { it.appCode match appCode }
                    }.toEntity(Document::class.java)
                val resMap = mutableMapOf("app" to this, "appDetail" to list, "tenantAppSetting" to tenantApp)
                return ApiResult.of(resMap)
            }
    }


    /**
     * @Description 根据appCode，用户id查应用
     *
     * @param
     * @return
     * @date 10:21 2021/12/24
     */
    @ApiOperation("根据appCode，用户id查应用   管理员调用")
    @PostMapping("/appDetailByAppId")
    fun appDetailByAppId(
        @Require appIds: Array<String>,
        userId: String,
        request: HttpServletRequest
    ): ApiResult<List<AppDetailVo>> {
        val loginUser = request.LoginTenantAdminUser
        val apps = queryUserAllowApps(userId, loginUser.tenant.id)
        val appCodes = apps.map { it.code }.intersect(appIds.toList())
        mor.tenant.tenantApplication.query().apply {
            this.where { it.tenant.id match loginUser.tenant.id }
            this.where { it.appCode match_in appCodes }
        }
            .toList(AppDetailVo::class.java)
            .apply {
                return ApiResult.of(this)
            }
    }

    class AppInfo {
        var appid: String = ""
        var code: String = ""
        var appName: String = ""
        var ename: String = ""
        var appDesc = ""
        var appIcon: String = ""
        var address: String = ""
        var status: Int = 0
        var type: MutableList<String> = mutableListOf()
    }

    @ApiOperation("根据appCodes 乐造OS调用")
    @PostMapping("/appList")
    fun appList(
        @Require appIds: Array<String>,
        userId: String,
        request: HttpServletRequest
    ): ApiResult<List<AppInfo>> {
        val res: MutableList<AppInfo> = mutableListOf()
        val loginUser = request.LoginTenantAdminUser
        val apps = queryUserAllowApps(userId, loginUser.tenant.id)
        val appCodes = apps.map { it.code }.intersect(appIds.run { toList().toSet() })
        val toList: MutableList<TenantApplication> = mor.tenant.tenantApplication.query().apply {
            this.where { it.tenant.id match loginUser.tenant.id }
            this.where { it.appCode match_in appCodes }
            this.where { it.lable.code match AppTagTypeEnum.LOS.name }
        }.toList(TenantApplication::class.java)
        toList.forEach { it ->
            val vo = AppInfo()
            vo.appid = it.id
            vo.code = it.appCode
            vo.appName = it.name
            vo.ename = it.ename
            vo.appIcon = it.logo!!.url
            vo.appDesc = it.remark.toString()
            vo.address = it.url.toString()
            if (it.enabled) vo.status = 3 else vo.status = 2 //启用true/禁用false 乐造os  应用启用状态: 状态：1-到期  2-停用  3-启用
            if (it.lable.isNotEmpty()) {
                vo.type = it.lable.map { it.code }.toMutableList()
            }
            res.add(vo)
        }
        return ApiResult.of(res)
    }

    /**
     * @Description 根据appCode，用户id查应用
     *
     * @param
     * @return
     * @date 10:21 2021/12/24
     */
    @ApiOperation("根据appCode查用户的应用   用户调用")
    @PostMapping("/appDetailByAppIdForUser")
    fun appDetailByAppIdForUser(
        @Require appIds: Array<String>,
        request: HttpServletRequest
    ): ApiResult<List<AppDetailVo>> {
        val tenantId = request.LoginUser.organization.id
        val userId = request.UserId
        val apps = queryUserAllowApps(userId, tenantId)
        val appCodes = apps.map { it.code }.intersect(appIds.toList())
        mor.tenant.tenantApplication.query().apply {
            this.where { it.tenant.id match tenantId }
            this.where { it.appCode match_in appCodes }
        }
            .toList(AppDetailVo::class.java)
            .apply {
                return ApiResult.of(this)
            }
    }

    /**
     * @Description 租户管理员调用 对多种授权类型，多个不同应用的授权
     *
     * @param
     * @return
     * @date 10:21 2021/12/24
     */
    @BizLog(BizLogActionEnum.Authorize, BizLogResourceEnum.App, "应用管理")
    @ApiOperation("登录授权")
    @PostMapping("/permit")
    fun permit(
        @Require apps: Array<AuthObject>,
        request: HttpServletRequest
    ): ApiResult<String> {

        var tenant = request.LoginTenantAdminUser.tenant
        if (apps.size > 1) request.logMsg = "授权（部门，用户组，用户）登录多个应用"
        if (apps.size == 1) {
            when (apps.first().cate.name) {
                AuthObjectEnum.DepartmentInfo.name -> {
                    request.logMsg = "授权部门{${apps.first().id}}登录应用{${apps.first().app.name}}"
                }
                AuthObjectEnum.TenantUserGroup.name -> {
                    request.logMsg = "授权用户组{${apps.first().id}}登录应用{${apps.first().app.name}}"
                }
                AuthObjectEnum.TenantUser.name -> {
                    request.logMsg = "授权用户{${apps.first().id}}登录应用{${apps.first().app.name}}"
                }
                else -> {
                    throw RuntimeException("授权对象类型错误")
                }
            }
        }

        apps.forEach { obj ->
            var codeName = CodeName()
            var ap = mor.tenant.tenantApplication.query().apply {
                this.where { it.appCode match obj.app.id }
                this.where { it.tenant.id match tenant.id }
            }.toEntity()
            if (ap == null) {
                throw RuntimeException("您没有该应用的权限")
            } else {
                codeName.code = ap.appCode
                codeName.name = ap.name
            }

            when (obj.cate.name) {
                AuthObjectEnum.DepartmentInfo.name -> {
                    var idList = getDepIds(obj.id, tenant.id)
                    idList.forEach { id ->
                        updateDeportmentApps(codeName, id)
                    }
                }
                AuthObjectEnum.TenantUserGroup.name -> {
                    mor.tenant.tenantUserGroup.query().where { it.tenant.id match tenant.id }
                        .where { it.id match obj.id }.toEntity().must().elseThrow { "找不到用户组" }
                    updateUserGroupApps(codeName, obj.id)
                }
                AuthObjectEnum.TenantUser.name -> {
                    mor.tenant.tenantUser.query().where { it.tenant.id match tenant.id }.where { it.id match obj.id }
                        .toEntity().must().elseThrow { "找不到用户" }
                    updateUserApps(codeName, obj.id)
                }
                else -> {
                    throw RuntimeException("授权对象类型错误")
                }
            }
        }

        return ApiResult.of("")
    }

    /**
     * @Description 租户管理员调用，移除授权 多种授权类型，多个应用
     *
     * @param
     * @return
     * @date 10:21 2021/12/24
     */
    @BizLog(BizLogActionEnum.CancelAuthorize, BizLogResourceEnum.App, "应用管理")
    @ApiOperation("移除授权")
    @PostMapping("/remove")
    fun remove(
        @Require apps: Array<AuthObject>,
        depIds: String,
        request: HttpServletRequest
    ): ApiResult<String> {
        if (apps.size > 1) request.logMsg = "移除（部门，用户组，用户）登录多个应用的授权"
        if (apps.size == 1) {
            when (apps.first().cate.name) {
                AuthObjectEnum.DepartmentInfo.name -> {
                    if (apps.size == 1) request.logMsg = "移除部门{${apps.first().id}}登录应用{${apps.first().app.name}}的授权"
                }
                AuthObjectEnum.TenantUserGroup.name -> {
                    if (apps.size == 1) request.logMsg = "移除用户组{${apps.first().id}}登录应用{${apps.first().app.name}}的授权"
                }
                AuthObjectEnum.TenantUser.name -> {
                    if (apps.size == 1) request.logMsg = "移除用户{${apps.first().id}}登录应用{${apps.first().app.name}}的授权"
                }
                else -> {
                    throw RuntimeException("授权对象类型错误")
                }
            }
        }

        var tenant = request.LoginTenantAdminUser.tenant
        apps.forEach { obj ->
            var codeName = CodeName()
            var ap = mor.tenant.tenantApplication.query().apply {
                this.where { it.appCode match obj.app.id }
                this.where { it.tenant.id match tenant.id }
            }.toEntity()
            if (ap == null) {
                throw RuntimeException("您没有该应用的权限")
            } else {
                codeName.code = ap.appCode
                codeName.name = ap.name
            }
            when (obj.cate.name) {
                AuthObjectEnum.DepartmentInfo.name -> {
                    mor.tenant.tenantDepartmentInfo.query().where { it.tenant.id match tenant.id }
                        .where { it.id match obj.id }.toEntity().must().elseThrow { "找不到部门" }
                    removeDeportmentApps(codeName, obj.id)
                }
                AuthObjectEnum.TenantUserGroup.name -> {
                    mor.tenant.tenantUserGroup.query().where { it.tenant.id match tenant.id }
                        .where { it.id match obj.id }.toEntity().must().elseThrow { "找不到用户组" }
                    removeUserGroupApps(codeName, obj.id)
                }
                AuthObjectEnum.TenantUser.name -> {
                    mor.tenant.tenantUser.query().where { it.tenant.id match tenant.id }.where { it.id match obj.id }
                        .toEntity().must().elseThrow { "找不到用户" }
                    removeUserApps(codeName, obj.id)
                }
                else -> {
                    throw RuntimeException("授权对象类型错误")
                }
            }
        }

        return ApiResult.of("")
    }

    /**
     * @Description 租户管理员调用 新增授权 多个授权类型（人，部门，用户组），一个应用
     *
     * @param
     * @return
     * @date 10:21 2021/12/24
     */
    @BizLog(BizLogActionEnum.Authorize, BizLogResourceEnum.App, "应用管理")
    @ApiOperation("登录授权 用户组 部门 人 iam前端使用")
    @PostMapping("/permitAuthObj")
    fun permitAuthObj(
        @Require app: CodeName,
        @Require authObjs: Array<AuthObj>,
        request: HttpServletRequest
    ): ApiResult<String> {


        var tenant = request.LoginTenantAdminUser.tenant
        var ap = mor.tenant.tenantApplication.query().apply {
            this.where { it.appCode match app.code }
            this.where { it.tenant.id match tenant.id }
        }.toEntity()
        if (ap == null) {
            throw RuntimeException("您没有该应用的权限")
        }
        request.logMsg = "授权（部门，用户组，用户）登录应用{${ap.name}}"
        //对部门授权
        var depIds = ""
        authObjs.forEach {
            if (AuthObjectEnum.DepartmentInfo.name.equals(it.cate.name)) {
                depIds += it.id
                depIds = "$depIds,"
            }
        }
        removeAllDeportmentApps(tenant.id, app)
        removeAllUserGroupApps(tenant.id, app)
        removeAllUserApps(tenant.id, app)
        if (depIds.HasValue) {
            var idList = getDepIds(depIds, tenant.id)
            idList.forEach { id ->
                updateDeportmentApps(app, id)
            }
        }

        authObjs.forEach { obj ->
            when (obj.cate.name) {
                AuthObjectEnum.DepartmentInfo.name -> {

                }
                AuthObjectEnum.TenantUserGroup.name -> {
                    mor.tenant.tenantUserGroup.query().where { it.tenant.id match tenant.id }
                        .where { it.id match obj.id }.toEntity().must().elseThrow { "找不到用户组" }
                    updateUserGroupApps(app, obj.id)
                }
                AuthObjectEnum.TenantUser.name -> {
                    val user = mor.tenant.tenantUser.query()
                        .where { it.tenant.id match tenant.id }
                        .where { it.id match obj.id }
                        .toEntity()
                    if (user == null) {
                        throw RuntimeException("找不到用户")
                    } else {
                        if (!openPrivatization) {
                            updateUserApps(app, obj.id)
                        } else {
                            if (user.adminType == TenantAdminTypeEnum.None) {
                                updateUserApps(app, obj.id)
                            }
                        }

                    }

                }
                else -> {
                    throw RuntimeException("授权对象类型错误")
                }
            }
        }

        return ApiResult.of("")
    }

    /**
     * @Description 租户管理员调用 移除授权 多个授权类型（人，部门，用户组），一个应用
     *
     * @param
     * @return
     * @date 10:21 2021/12/24
     */
    @BizLog(BizLogActionEnum.CancelAuthorize, BizLogResourceEnum.App, "应用管理")
    @ApiOperation("移除授权 用户组 部门 人 iam前端使用")
    @PostMapping("/removeAuthObj")
    fun removeAuthObj(
        @Require app: CodeName,
        @Require authObjs: Array<AuthObj>,
        request: HttpServletRequest
    ): ApiResult<String> {
        var tenant = request.LoginTenantAdminUser.tenant
        var ap = mor.tenant.tenantApplication.query().apply {
            this.where { it.appCode match app.code }
            this.where { it.tenant.id match tenant.id }
        }.toEntity()
        if (ap == null) {
            throw RuntimeException("您没有该应用的权限")
        }
        request.logMsg = "移除(部门，用户组，用户)登录应用{${ap.name}}的授权"

        authObjs.forEach { obj ->

            when (obj.cate.name) {
                AuthObjectEnum.DepartmentInfo.name -> {
                    if (!obj.id.HasValue) {
                        throw RuntimeException("未传应用信息")
                    } else {
                        mor.tenant.tenantDepartmentInfo.query().where { it.tenant.id match tenant.id }
                            .where { it.id match obj.id }.toEntity().must().elseThrow { "找不到部门" }
                        removeDeportmentApps(app, obj.id)
                    }
                }
                AuthObjectEnum.TenantUserGroup.name -> {
                    mor.tenant.tenantUserGroup.query().where { it.tenant.id match tenant.id }
                        .where { it.id match obj.id }.toEntity().must().elseThrow { "找不到用户组" }
                    removeUserGroupApps(app, obj.id)
                }
                AuthObjectEnum.TenantUser.name -> {
                    mor.tenant.tenantUser.query().where { it.tenant.id match tenant.id }.where { it.id match obj.id }
                        .toEntity().must().elseThrow { "找不到用户" }
                    removeUserApps(app, obj.id)
                }
                else -> {
                    throw RuntimeException("授权对象类型错误")
                }
            }
        }

        return ApiResult.of("")
    }

    /**
     * @Description 管理员调用 用户存在角色就移除授权（多个用户）
     *
     * @param  persons 人ids
     * @param  app app信息
     * @return
     * @date 15:48 2021/12/20
     */
    @BizLog(BizLogActionEnum.CancelAuthorize, BizLogResourceEnum.App, "应用管理")
    @ApiOperation("移除授权 根据角色判断 对人")
    @PostMapping("/removeAuthObjByHasRole")
    fun removeAuthObjByHasRole(
        @Require app: CodeName,
        @Require userIds: Array<String>,
        request: HttpServletRequest
    ): ApiResult<String> {

        var loginUser = request.LoginTenantAdminUser
        var ap = mor.iam.sysApplication.query().apply {
            this.where { it.appCode match app.code }
        }.toEntity()
        if (ap == null) {
            throw RuntimeException("找不到应用或应用未同步")
        }
        mor.tenant.tenantApplication.query()
            .where { it.tenant.id match loginUser.tenant.id }
            .where { it.appCode match app.code }
            .exists()
            .apply {
                if (!this) return ApiResult.error("找不到应用或应用未同步")
            }
        request.logMsg = "移除有角色的用户登录应用{${ap.name}}的授权"
        var dbUsers: MutableList<String> = mutableListOf()
        mor.tenant.tenantUser.query().apply {
            this.where { it.tenant.id match loginUser.tenant.id }
            this.where { it.id match_in userIds }
        }.toList().forEach {
            dbUsers.add(it.id)
        }
        val ids = userIds.toMutableList()
        ids.removeAll(dbUsers)
        if (ids.size > 0) {
            throw RuntimeException("用户不存在或者不属于该租户")
        }
        userIds.forEach { userId ->
            //判断是否有角色
            var roleIds: MutableList<String> = mutableListOf()
            mor.tenant.tenantAppRole.query().apply {
                this.where { it.tenant.id match loginUser.tenant.id }
                this.where { it.appInfo.code match app.code }
            }.toList().apply {
                this.forEach {
                    roleIds.add(it.id)
                }
            }
            var has = mor.tenant.tenantUser.query().apply {
                this.where { it.id match userId }
                this.where { it.roles.id match_in roleIds }
            }.exists()
            if (!has) {
                removeUserApps(app, userId)
            }
        }
        return ApiResult.of("")
    }

    /**
     * @Description 租户管理员调用 对多个用户登录授权，一个应用
     *
     * @param
     * @return
     * @date 10:21 2021/12/24
     */
    @BizLog(BizLogActionEnum.Authorize, BizLogResourceEnum.App, "应用管理")
    @ApiOperation("登录授权 用户")
    @PostMapping("/permitAuthUser")
    fun permitAuthUser(
        @Require app: CodeName,
        @Require authObjs: Array<AuthObj>,
        request: HttpServletRequest
    ): ApiResult<String> {

        var tenant = request.LoginTenantAdminUser.tenant
        var ap = mor.tenant.tenantApplication.query().apply {
            this.where { it.appCode match app.code }
            this.where { it.tenant.id match tenant.id }
        }.toEntity()
        if (ap == null) {
            throw RuntimeException("您没有该应用的权限")
        }
        request.logMsg = "对多个用户授权登录应用{${ap.name}}"
        authObjs.forEach { obj ->

            when (obj.cate.name) {
                AuthObjectEnum.TenantUser.name -> {
                    val user = mor.tenant.tenantUser.query()
                        .where { it.tenant.id match tenant.id }
                        .where { it.id match obj.id }
                        .toEntity()
                    if (user == null) {
                        throw RuntimeException("找不到用户")
                    } else {
                        if (!openPrivatization) {
                            updateUserApps(app, obj.id)
                        } else {
                            if (user.adminType == TenantAdminTypeEnum.None) {
                                updateUserApps(app, obj.id)
                            }
                        }

                    }
                }
                else -> {
                    throw RuntimeException("授权对象类型错误")
                }
            }
        }

        return ApiResult.of("")
    }

    /**
     * @Description 租户管理员调用 移除一个人所有应用的登录授权
     *
     * @param
     * @return
     * @date 10:21 2021/12/24
     */
    @BizLog(BizLogActionEnum.CancelAuthorize, BizLogResourceEnum.App, "应用管理")
    @ApiOperation("移除授权")
    @PostMapping("/removeAuthAppByPerson")
    fun removeAuthAppByPerson(
        @Require userId: String,
        request: HttpServletRequest
    ): ApiResult<String> {
        request.logMsg = "移除{${userId}}对所有应用的登录授权"
        val tenant = request.LoginTenantAdminUser.tenant
        var has = mor.tenant.tenantUser.queryById(userId)
            .where { it.tenant.id match tenant.id }
            .exists()
        if (!has) {
            return ApiResult.error("用户不存在")
        }
        mor.tenant.tenantUser.updateById(userId)
            .where { it.tenant.id match tenant.id }
            .set {
                it.allowApps to null
            }.exec()
        if (mor.affectRowCount > 0) {
            return ApiResult.of("")
        } else {
            return ApiResult.error("删除失败")
        }
    }


    /**
     * @Description 租户管理员调用 可能废弃了
     *
     * @param
     * @return
     * @date 10:21 2021/12/24
     */
    @BizLog(BizLogActionEnum.Update, BizLogResourceEnum.App, "应用管理")
    @ApiOperation("修改租户应用信息(访问策略)")
    @PostMapping("/accessStrategy")
    fun accessStrategy(
        @JsonModel loginStrategy: Document,
        request: HttpServletRequest
    ): ApiResult<String> {
        val enabled = loginStrategy.get("enabled").toString()
        val code = loginStrategy.get("id")
        val isOpen = loginStrategy.get("isOpen").toString()

        if (
            (!"true".equals(enabled) && !"false".equals(enabled))
            ||
            (!"true".equals(isOpen) && !"false".equals(isOpen))
            ||
            code == null || code.toString().isEmpty()
        ) {

            return ApiResult.error("请检查传入参数是否正确")
        }
        val loginStrategy = loginStrategy.ConvertJson(TenantApplication::class.java)
        val tenant = request.LoginTenantAdminUser.tenant

        val tenantApp = mor.tenant.tenantApplication.query().apply {
            this.where { it.tenant.id match tenant.id }
            this.where { it.appCode match loginStrategy.id }
        }
            .toEntity()
        if (tenantApp == null) return ApiResult.error("您没有该应用的权限")
        request.logMsg = "修改应用{${tenantApp.name}}的访问策略"
        mor.tenant.tenantApplication.update()
            .where { it.tenant.id match tenant.id }
            .where { it.appCode match loginStrategy.id }
            .set { it.enabled to loginStrategy.enabled }
            .set { it.isOpen to loginStrategy.isOpen }
            .exec()
        if (db.affectRowCount > 0) {
            return ApiResult()
        } else return ApiResult.error("修改失败")
    }

    /**
     * @Description 租户管理员调用 iam
     *
     * @param
     * @return
     * @date 10:21 2021/12/24
     */
    @BizLog(BizLogActionEnum.Update, BizLogResourceEnum.App, "应用管理")
    @ApiOperation("修改租户应用")
    @PostMapping("/updateTenantApp")
    fun updateTenantApp(
        @Require tenantApplication: org.bson.Document,
        authObjs: Array<AuthObj>,

        request: HttpServletRequest
    ): ApiResult<String> {
        val loginUser = request.LoginTenantAdminUser
        tenantApplication.put("tenant", IdName(loginUser.tenant.id, loginUser.tenant.name))
        val entity = tenantApplication.ConvertJson(TenantApplication::class.java)
        request.logMsg = "修改应用{${entity.name}}的信息，包括扩展字段信息与授权信息等全部允许修改的信息"

        val app = CodeName(entity.appCode, entity.name)

        mor.iam.sysApplication.query()
            .where { it.appCode match entity.appCode }
            .toEntity()
            .apply {
                if (this != null) {
                    if (this.name != entity.name) return ApiResult.error("不可修改管理端应用")
                    if (this.remark != entity.remark) return ApiResult.error("不可修改管理端应用")
                }
            }

        entity.tenant = request.LoginTenantAdminUser.tenant
        entity.userType = UserSystemTypeEnum.TenantUser

        if (!entity.id.HasValue) {
            return ApiResult.error("应用ID不能为空")
        }
        val toEntity = mor.tenant.tenantApplication.queryById(entity.id)
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity()
        if (toEntity == null) {
            return ApiResult.error("数据不存在")
        }
        entity.sysId = toEntity.sysId
        val checkParam = checkParam(entity)
        if (checkParam != "") {
            return ApiResult.error(checkParam)
        }


        val parse = Document.parse(entity.ConvertJson(TenantApplication::class.java).ToJson())
        parse.put("fileds", tenantApplication.get("fileds"))

        mor.tenant.tenantApplication.updateWithEntity(tenantApplication)
            .withoutColumns("isSysDefine", "sysId")
            .execUpdate()
            .apply {
                if (this == 0) {
                    return ApiResult.error("修改失败")
                }
                updateRelationList(toEntity, entity, request)
            }

        if (entity.isOpen) {
            return ApiResult()
        }
        //应用登录授权
        //对部门授权
        var depIds = ""
        authObjs.forEach {
            if (AuthObjectEnum.DepartmentInfo.name.equals(it.cate.name)) {
                depIds += it.id
                depIds = "$depIds,"
            }
        }
        removeAllDeportmentApps(loginUser.tenant.id, app)
        removeAllUserGroupApps(loginUser.tenant.id, app)
        removeAllUserApps(loginUser.tenant.id, app)
        if (depIds.HasValue) {
            var idList = getDepIds(depIds, loginUser.tenant.id)
            idList.forEach { id ->
                updateDeportmentApps(app, id)
            }
        }

        authObjs.forEach { obj ->
            when (obj.cate.name) {
                AuthObjectEnum.DepartmentInfo.name -> {

                }
                AuthObjectEnum.TenantUserGroup.name -> {
                    updateUserGroupApps(app, obj.id)
                }
                AuthObjectEnum.TenantUser.name -> {
                    if (!openPrivatization) {
                        updateUserApps(app, obj.id)
                    } else {
                        val user = mor.tenant.tenantUser.queryById(obj.id).toEntity()
                        if (user != null && user.adminType == TenantAdminTypeEnum.None) {
                            updateUserApps(app, obj.id)
                        }
                    }
                }
                else -> {
                    throw RuntimeException("授权对象类型错误")
                }
            }
        }
        return ApiResult()


    }

    @ApiOperation("授权管理应用列表")
    @PostMapping("/authAppList")
    fun authAppList(
        name: String,
        appCode: String,
        authObj: String,
        keywords: String,
        enabled: Boolean?,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<AuthAppVo> {
        val tenantId = request.LoginTenantAdminUser.tenant.id
        val appRoleIds: MutableList<String> = mutableListOf()
        //查角色授权主体
        if (keywords.HasValue) {
            mor.tenant.tenantAppAuthResourceInfo.query()
                .apply {
                    this.where { it.tenant.id match tenantId }
                    if (keywords.HasValue) {
                        this.where { it.auths.isAllow match true }
                        this.whereOr(
                            { it.type match keywords },
                            { it.target.name match_like keywords },
                            { it.auths.name match_like keywords })
                    }
                }.select { it.appInfo }.toList().map {
                    appRoleIds.add(it.appInfo.code)
                }
        }
        // 获取当前租户所拥有的 appCode
        val res = mor.tenant.tenantApplication.query().apply {
            this.where { it.tenant.id match tenantId }
            if (keywords.HasValue) {
                this.whereOr(
                    { it.name match_like keywords },
                    { it.appCode match_like keywords },
                    { it.ename match_like keywords },
                    { it.appCode match_in appRoleIds })
            }
            if (name.HasValue) {
                this.where { it.name match_like name }
            }
            if (appCode.HasValue) {
                this.where { it.appCode match_like appCode }
            }
            if (enabled != null) {
                this.where { it.enabled match enabled }
            }
        }.limit(skip, take).orderByDesc { it.createAt }
            .toListResult(AuthAppVo::class.java).apply {
                //查角色有没有资源 有的话返回角色名称
                this.data.map { vo ->
                    mor.tenant.tenantAppAuthResourceInfo.query().apply {
//                        this.where { it.type match AuthTypeEnum.Role.name }
                        this.where { it.appInfo.code match vo.appCode }
                        this.where { it.tenant.id match tenantId }
                    }.toList().apply {
                        vo.authObj = this.map { it.target.name }.toSet().toMutableList()
                    }
                }

            }
        return res
    }

    @ApiOperation("角色管理应用列表")
    @PostMapping("/roleAppList")
    fun roleAppList(
        name: String,
        appCode: String,
        roleCode: String,
        enabled: Boolean?,
        keywords: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<RoleAppVo> {
        var loginUser = request.LoginTenantAdminUser
        val appRoleIds: MutableList<String> = mutableListOf()
        if (keywords.HasValue) {
            val listtr = mor.tenant.tenantAppRole.query().apply {
                this.where { it.tenant.id match loginUser.tenant.id }
                this.where { it.name match_like keywords }
            }.select { it.appInfo }.toList()
            listtr.forEach {
                appRoleIds.add(it.appInfo.code)
            }
        }
        val res = mor.tenant.tenantApplication.query().apply {
            this.where { it.tenant.id match loginUser.tenant.id }
            if (keywords.HasValue) {
                this.whereOr(
                    { it.name match_like keywords },
                    { it.appCode match_like keywords },
                    { it.ename match_like keywords },
                    { it.appCode match_in appRoleIds })
            }
            if (name.HasValue) {
                this.where { it.name match_like name }
            }
            if (appCode.HasValue) {
                this.where { it.appCode match_like appCode }
            }
            if (enabled != null) {
                this.where { it.enabled match enabled }
            }

        }.limit(skip, take).orderByDesc { it.createAt }
            .toListResult(RoleAppVo::class.java)
            .apply {
                this.data.map { vo ->
                    mor.tenant.tenantAppRole.query().apply {
                        this.where { it.appInfo.code match vo.appCode }
                        this.where { it.tenant.id match loginUser.tenant.id }
                    }.select { it.name }.toList().forEach {
                        vo.roleCode.add(it.name)
                    }
                }
            }
        return res
    }

    @ApiOperation("资源管理应用列表")
    @PostMapping("/resourceAppList")
    fun resourceAppList(
        name: String,
        appCode: String,
        resourceName: String,
        keywords: String,
        enabled: Boolean?,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<ResourceAppVo> {
        val tenantId = request.LoginTenantAdminUser.tenant.id
        val resourceAppCodes: MutableList<String> = mutableListOf()
        if (keywords.HasValue) {
            mor.tenant.tenantResourceInfo.query().apply {
                this.where { it.tenant.id match tenantId }
                this.whereOr({ it.name match_like keywords })
            }.select { it.appInfo.code }.toList()
                .forEach {
                    resourceAppCodes.add(it.appInfo.code)
                }
        }
        val res = mor.tenant.tenantApplication.query().apply {
            this.where { it.tenant.id match tenantId }
            if (keywords.HasValue) {
                this.whereOr(
                    { it.name match_like keywords },
                    { it.appCode match_like keywords },
                    { it.ename match_like keywords },
                    { it.appCode match_in resourceAppCodes })
            }
            if (name.HasValue) {
                this.where { it.name match_like name }
            }
            if (appCode.HasValue) {
                this.where { it.appCode match_like appCode }
            }
            if (enabled != null) {
                this.where { it.enabled match enabled }
            }

        }.limit(skip, take).orderByDesc { it.createAt }
            .toListResult(ResourceAppVo::class.java)
            .apply {
                this.data.map { vo ->
                    var datas = mor.tenant.tenantResourceInfo.query().apply {
                        this.where { it.tenant.id match tenantId }
                        this.where { it.appInfo.code match vo.appCode }
                    }.select { it.name }.toList().forEach {
                        vo.resourceName.add(it.name)
                    }
                }
            }
        return res
    }


    @ApiOperation("查当前用户的所有app IdName Version  管理员调用")
    @PostMapping("/listAppIdVersion")
    fun listAppIdVersions(
        @Require userId: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<IdNameVersionVo> {
        val tenantId = request.LoginTenantAdminUser.tenant.id


        val tenantApps = mor.tenant.tenantApplication.query()
            .where { it.tenant.id match tenantId }
            .toList()
        val userAllowApps = queryUserAllowApps(userId, tenantId)
        var userAllowAppCodes = userAllowApps.map { it.code }
        val tenantAllowAppCodes = tenantApps.map { it.appCode }
        //取交集
        userAllowAppCodes = userAllowAppCodes.intersect(tenantAllowAppCodes).toMutableList()


        mor.tenant.tenantApplication.query()
            .where { it.tenant.id match tenantId }
            .where { it.appCode match_in userAllowAppCodes }.orderByDesc { it.createAt }
            .toListResult(IdNameVersionVo::class.java)
            .apply {
                this.data.forEach {
                    it.enabled = tenantApps.first { app -> app.appCode == it.appCode }.enabled
                }
                return this
            }

    }

    @ApiOperation("查当前用户的所有app IdName Version 用户调用")
    @PostMapping("/listAppIdVersionForUser")
    fun listAppIdVersionForUser(
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<IdNameVersionVo> {
        val tenantId = request.LoginUser.organization.id
        val userId = request.UserId
        val tenantApps = mor.tenant.tenantApplication.query()
            .where { it.tenant.id match tenantId }
            .toList()
        val userAllowApps = queryUserAllowApps(userId, tenantId)
        var userAllowAppCodes = userAllowApps.map { it.code }
        val tenantAllowAppCodes = tenantApps.map { it.appCode }
        //取交集
        userAllowAppCodes = userAllowAppCodes.intersect(tenantAllowAppCodes).toMutableList()


        mor.iam.sysApplication.query()
            .where { it.appCode match_in userAllowAppCodes }.orderByDesc { it.createAt }
            .toListResult(IdNameVersionVo::class.java)
            .apply {
                this.data.forEach {
                    it.enabled = tenantApps.first { app -> app.appCode == it.appCode }.enabled
                }
                return this
            }

    }

    @ApiOperation("查当用户所能访问的应用列表")
    @PostMapping("/enableAppsList")
    fun enableAppsList(
        @Require userId: String,
        @Require tenantId: String,
        request: HttpServletRequest
    ): ListResult<IdNameVersionVo> {
        mor.tenant.tenant.queryById(tenantId)
            .toEntity() ?: return ListResult.error("租户不存在")
        val tenantUser = mor.tenant.tenantUser.queryById(userId)
            .toEntity() ?: return ListResult.error("用户不存在")

        if (request.LoginTenantAdminUser.tenant.id != tenantId) {
            return ListResult.error("不可以查询其他租户")
        }
        if (tenantUser.tenant.id != tenantId) {
            return ListResult.error("当前用户与租户不匹配")
        }

        val tenantAppSettings = mor.tenant.tenantApplication.query().apply {
            this.where { it.tenant.id match tenantId }
            this.where { it.enabled match true }
        }.toList()
        val appCodes: MutableList<String> = mutableListOf()
        for (app in tenantAppSettings) {
            if (app.isOpen) {
                appCodes.add(app.appCode)
            } else if (tenantUser.denyApps.filterNot { it.code == app.appCode }.isNullOrEmpty()
                && tenantUser.allowApps.any { it.code == app.appCode }
            ) {
                appCodes.add(app.appCode)
            }
        }
        //查用户组
        val groupIds = tenantUser.groups.map { it.id }
        mor.tenant.tenantUserGroup.query()
            .where { it.tenant.id match tenantId }
            .where { it.id match_in groupIds }
            .toList()
            .filter { !it.allowApps.isNullOrEmpty() }.forEach { appCodes.addAll(it.allowApps.map { app -> app.code }) }

        //查部门
        val departmentIds = tenantUser.depts.map { it.id }
        mor.tenant.tenantDepartmentInfo.query()
            .where { it.tenant.id match tenantId }
            .where { it.id match_in departmentIds }
            .toList()
            .filter { !it.allowApps.isNullOrEmpty() }.forEach { appCodes.addAll(it.allowApps.map { app -> app.code }) }


        return mor.iam.sysApplication.query()
            .where { it.appCode match_in appCodes }.orderByDesc { it.createAt }
            .toListResult(IdNameVersionVo::class.java)
    }

    @ApiOperation("app下当前租户下的使用人数")
    @PostMapping("/getPersonCount")
    fun getPersonCount(
        appCodes: Array<String>,
        request: HttpServletRequest
    ): ApiResult<List<AppVo>> {
        var loginUser = request.LoginTenantAdminUser
        var res: ApiResult<List<AppVo>> = ApiResult()

        res.data = getPersonNum(loginUser.tenant.id, appCodes)
        return res
    }


    fun getDepIds(ids: String, tenantId: String): List<String> {

        //去重的所有部门ids
        var allIds: MutableList<String> = mutableListOf()
        //找根
        var idList = ids.split(",")
        mor.tenant.tenantDepartmentInfo.query().apply {
            this.where { it.tenant.id match tenantId }
            this.where { it.id match_in idList }
        }.exists().apply {
            if (!this) {
                throw RuntimeException("部门都不存在")
            }
        }
        var allDepList = mor.tenant.tenantDepartmentInfo.query().where { it.tenant.id match tenantId }.toList(
            DepartmentInfoAutoController.DeportmentInfoTree::class.java
        )
        var depList = mor.tenant.tenantDepartmentInfo.query().apply {
            this.where { it.tenant.id match tenantId }
            this.where { it.id match_in idList }
        }.toList(DepartmentInfoAutoController.TransferDeportmentVo::class.java).apply {
            this.forEach { dep ->
                dep.hasChildren = mor.tenant.tenantDepartmentInfo.query().apply {
                    this.where { it.tenant.id match tenantId }
                    this.where { it.parent.id match dep.id }
                }.exists()
                if (dep.hasChildren) {
                    var s: DepService = DepService()
                    var idArr: MutableList<String> = mutableListOf()
                    s.getChildrenIds(allDepList, idArr, dep.id)
                    allIds.add(dep.id)
                    allIds.addAll(idArr)

                } else {
                    allIds.add(dep.id)
                }
            }
        }
        return allIds.map { it }.toTypedArray().toSet().toList()
    }

    fun updateDeportmentApps(app: CodeName, deportmentId: String) {
        mor.tenant.tenantDepartmentInfo.update()
            .where { it.id match deportmentId }
            .pull({ it.allowApps }, MongoColumnName("code") match app.code)
            .exec()
        mor.tenant.tenantDepartmentInfo.update()
            .where { it.id match deportmentId }
            .push { it.allowApps to app }
            .exec()
    }

    fun updateUserGroupApps(app: CodeName, userGroupId: String) {
        mor.tenant.tenantUserGroup.update()
            .where { it.id match userGroupId }
            .pull({ it.allowApps }, MongoColumnName("code") match app.code)
            .exec()
        mor.tenant.tenantUserGroup.update()
            .where { it.id match userGroupId }
            .push { it.allowApps to app }
            .exec()
    }

    fun updateUserApps(app: CodeName, userId: String) {
        mor.tenant.tenantUser.update()
            .where { it.id match userId }
            .pull({ it.allowApps }, MongoColumnName("code") match app.code)
            .exec()
        mor.tenant.tenantUser.update()
            .where { it.id match userId }
            .push { it.allowApps to app }
            .exec()
    }

    fun removeDeportmentApps(app: CodeName, deportmentId: String) {
        mor.tenant.tenantDepartmentInfo.update()
            .where { it.id match deportmentId }
            .pull({ it.allowApps }, MongoColumnName("code") match app.code)
            .exec()
    }

    fun removeUserGroupApps(app: CodeName, userGroupId: String) {
        mor.tenant.tenantUserGroup.update()
            .where { it.id match userGroupId }
            .pull({ it.allowApps }, MongoColumnName("code") match app.code)
            .exec()
    }

    fun removeUserApps(app: CodeName, userId: String) {
        mor.tenant.tenantUser.update()
            .where { it.id match userId }
            .pull({ it.allowApps }, MongoColumnName("code") match app.code)
            .exec()
    }

    fun removeAllDeportmentApps(tenantId: String, app: CodeName) {
        //在本租户下，把所有部门中的该应用删除
        mor.tenant.tenantDepartmentInfo.update()
            .where { it.tenant.id match tenantId }
            .pull({ it.allowApps }, MongoColumnName("code") match app.code)
            .exec()
    }

    fun removeAllUserGroupApps(tenantId: String, app: CodeName) {
        //在本租户下，把所有用户组中的该应用删除
        mor.tenant.tenantUserGroup.update()
            .where { it.tenant.id match tenantId }
            .pull({ it.allowApps }, MongoColumnName("code") match app.code)
            .exec()
    }

    fun removeAllUserApps(tenantId: String, app: CodeName) {
        //在本租户下，把所有用户中的该应用删除
        mor.tenant.tenantUser.update()
            .where { it.tenant.id match tenantId }
            .pull({ it.allowApps }, MongoColumnName("code") match app.code)
            .exec()
    }


    //查人数
    fun getPersonNum(tenantId: String, appIds: Array<String>): List<AppVo> {
        var appss: MutableList<AppVo> = mutableListOf()
        appIds.forEach { appCode ->
            var en = mor.iam.sysApplication.query().apply {
                this.where { it.appCode match appCode }
            }.toEntity()
            if (en == null) {
                throw RuntimeException("找不到应用")
            }
            var code = en.appCode
            var p1: List<TenantUser> = mutableListOf()
            //查租户的应用配置
            var apps =
                mor.tenant.tenantApplication.query().apply { this.where { it.tenant.id match tenantId } }.toList()
            apps.map { app ->
                if (app.appCode == code) {
                    if (app.enabled == true) {
                        if (app.isOpen == true) {
                            p1 = mor.tenant.tenantUser.query().apply {
                                this.where { it.tenant.id match tenantId }
                            }.toList()
                        } else if (app.isOpen == false) {
                            p1 = somePersonPermit(tenantId, code)
                        }
                    }
                    var size: Int = 0
                    if (!p1.isEmpty()) {
                        println(p1.map { it.id }.toTypedArray().toSet().size)
                        size = p1.map { it.id }.toTypedArray().toSet().size
                    }
                    var vo: AppVo = AppVo()
                    if (en != null) {
                        vo.id = en.id
                        vo.appCode = en.appCode
                        vo.name = en.name
                        vo.count = size
                        appss.add(vo)
                    }
                }
            }


        }
        return appss
    }


    /**
     * @Description 根据应用查人数
     *
     * @param  tenantId 租户id
     * @param  code 应用code
     * @return
     * @date 16:38 2021/12/15
     */
    fun somePersonPermit(tenantId: String, code: String): List<TenantUser> {
        //查人
        var p1 = mor.tenant.tenantUser.query().apply {
            this.where {
                it.tenant.id match tenantId
            }
            this.where {
                it.allowApps.code match code
            }
        }.toList()

        //查用户组
        var groupIds: MutableList<String> = mutableListOf()
        mor.tenant.tenantUserGroup.query().apply {
            this.where {
                it.tenant.id match tenantId
            }
            this.where {
                it.allowApps.code match code
            }
        }.toList().apply {
            this.map {
                groupIds.add(it.id)
            }
        }
        //查组的人
        var p2 = mor.tenant.tenantUser.query().apply {
            this.where {
                it.tenant.id match tenantId
            }
            this.where {
                it.groups.id match_in groupIds
            }
        }.toList()
        //查部门
        var deptIds: MutableList<String> = mutableListOf()
        mor.tenant.tenantDepartmentInfo.query().apply {
            this.where {
                it.tenant.id match tenantId
            }
            this.where {
                it.allowApps.code match code
            }
        }.toList().apply {
            this.map {
                deptIds.add(it.id)
            }
        }
        //查部门的人
        var p3 = mor.tenant.tenantUser.query().apply {
            this.where {
                it.depts.id match_in deptIds
            }
            this.where {
                it.tenant.id match tenantId
            }
        }.toList()
        p1.addAll(p2)
        p1.addAll(p3)
        return p1
    }

    /**
     * @Description 查某个用户拥有的所有应用登录权限
     *
     * @param userId 用户id
     * @param tenantId 租户id
     * @return MutableList<CodeName>
     * @date 13:27 2022/2/14
     */
    fun queryUserAllowApps(userId: String, tenantId: String): MutableList<CodeName> {
        val allowApps: MutableList<CodeName> = mutableListOf()
        //查人
        val tenantUser = mor.tenant.tenantUser.query()
            .where { it.tenant.id match tenantId }
            .where { it.id match userId }
            .toEntity()
        if (null == tenantUser) {
            throw RuntimeException("用户不存在")
        }
        allowApps.addAll(tenantUser.allowApps)
        //查用户组
        val groupIds = tenantUser.groups.map { it.id }
        val tenantUserGroups = mor.tenant.tenantUserGroup.query()
            .where { it.tenant.id match tenantId }
            .where { it.id match_in groupIds }
            .toList()
        tenantUserGroups.forEach {
            allowApps.addAll(it.allowApps)
        }
        //查部门
        val deportmentIds = tenantUser.depts.map { it.id }
        val deportments = mor.tenant.tenantDepartmentInfo.query()
            .where { it.tenant.id match tenantId }
            .where { it.id match_in deportmentIds }
            .toList()
        deportments.forEach {
            allowApps.addAll(it.allowApps)
        }
        //查全部登录的
        mor.tenant.tenantApplication.query()
            .where { it.tenant.id match tenantId }
            .where { it.enabled match true }
            .where { it.isOpen match true }
            .toList()
            .apply {
                this.forEach {
                    allowApps.add(CodeName(it.appCode, it.name))
                }
            }
        return allowApps.distinctBy { it.code }.toMutableList()
    }

    fun enameValidate(entity: TenantApplication, request: HttpServletRequest): String {
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
    @ApiOperation("新增")
    @PostMapping("/save")
    fun save(
//        @JsonModel entitys: TenantApplication,
        @JsonModel tenantUserMap: Document,
        request: HttpServletRequest
    ): ApiResult<String> {

        request.logMsg = "新增应用"

        var entity = tenantUserMap.ConvertJson(TenantApplication::class.java)

        // 应用名称校验
        val enameValidate = enameValidate(entity, request)
        if (enameValidate != "") {
            return ApiResult.error(enameValidate)
        }

        entity.tenant = request.LoginTenantAdminUser.tenant
        entity.userType = UserSystemTypeEnum.TenantUser

        entity.appCode = entity.appCode.trim()
        val parse = Document.parse(entity.ConvertJson(TenantApplication::class.java).ToJson())
        parse.put("fileds", tenantUserMap.get("fileds"))


        val checkParam = checkParam(entity)
        if (checkParam != "") {
            return ApiResult.error(checkParam)
        }

        if (entity.isOnLine == null) {
            entity.isOnLine = true
        }
        mor.tenant.tenantApplication.updateWithEntity(parse).execInsert()
            .apply {
                if (this == 0) {
                    return ApiResult.error("新增失败")
                }
                entity.id = parse.get("_id").toString()
                UpdateldapAndSocialApp(request, entity)

                return ApiResult.of(entity.id)
            }
    }

    @BizLog(BizLogActionEnum.Update, BizLogResourceEnum.App, "应用管理")
    @ApiOperation("根据appCode修改应用")
    @PostMapping("/updateByAppCode")
    fun updateByAppCode(
        @JsonModel tenantUserMap: Document,
        request: HttpServletRequest
    ): ApiResult<String> {

        request.logMsg = "修改应用"

        var entity = tenantUserMap.ConvertJson(TenantApplication::class.java)

        // 应用名称校验
        val enameValidate = enameValidate(entity, request)
        if (enameValidate != "") {
            return ApiResult.error(enameValidate)
        }

        entity.tenant = request.LoginTenantAdminUser.tenant
        entity.userType = UserSystemTypeEnum.TenantUser
        val checkParam = checkUpdateParam(entity)
        if (checkParam != "") {
            return ApiResult.error(checkParam)
        }
        val toEntity = mor.tenant.tenantApplication.query()
            .where { it.tenant.id match entity.tenant.id }
            .where { it.appCode match entity.appCode }
            .toEntity() ?: return ApiResult.error("应用不存在")
        entity.id = toEntity.id
        val parse = Document.parse(entity.ConvertJson(TenantApplication::class.java).ToJson())
        parse.put("fileds", tenantUserMap.get("fileds"))

        if (entity.isOnLine == null) {
            entity.isOnLine = true
        }
        mor.tenant.tenantApplication
            .update()
            .set { it.logo to entity.logo }
            .set { it.name to entity.name }
            .set { it.ename to entity.ename }
            .set { it.remark to entity.remark }
            .set { it.lable to entity.lable }
            .set { it.url to entity.url }
            .where { it.appCode match entity.appCode }
            .where { it.tenant.id match entity.tenant.id }
            .exec()
            .apply {
                if (this == 0) {
                    return ApiResult.error("修改失败")
                }
                UpdateldapAndSocialApp(request, entity)

                return ApiResult.of(entity.id)
            }
    }

    private fun UpdateldapAndSocialApp(
        request: HttpServletRequest,
        entity: TenantApplication
    ) {
        mor.iam.identitySource.query().where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }.toEntity()
            .apply {
                if (this != null) {
                    mor.iam.identityTypeList.query().where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                        .toEntity()
                        .apply {
                            if (this == null) {
                                var identityTypeList = IdentityTypeList()
                                // 不存在，就新增
                                identityTypeList.tenant = request.LoginTenantAdminUser.tenant
                                identityTypeList.ldap = true
                                mor.iam.identityTypeList.doInsert(identityTypeList)
                            } else {
                                mor.iam.identityTypeList.update()
                                    .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                                    .set { it.ldap to true }.exec()
                            }
                        }

                    var identitySourceObj = this

                    // 查询租户的所有应用，关联ldap
                    mor.tenant.tenantApplication.query()
                        .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                        .toList().apply {
                            this.forEach {
                                if (identitySourceObj.tenantApps.map { it.id }.contains(entity.id)) {
                                    var objId = entity.id
                                    mor.iam.identitySource.update()
                                        .where { it.id match identitySourceObj.id }
                                        .pull({ it.tenantApps }, MongoColumnName("id") match objId)
                                        .exec()
                                }
                            }

                            var obj = TenantIdentitySourceApp()
                            obj.codeName.code = entity.appCode
                            obj.codeName.name = entity.name
                            obj.logo = entity.logo
                            obj.id = entity.id

                            mor.iam.identitySource.update()
                                .where { it.id match identitySourceObj.id }
                                .push { it.tenantApps to obj }
                                .exec()

                        }
                }
            }


        mor.tenant.socialIdentitySourceConfig.query()
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity()
            .apply {
                if (this != null) {
                    //插入
                    val tenantApps: MutableList<TenantIdentitySourceApp> = mutableListOf()
                    val apps = mor.tenant.tenantApplication.query()
                        .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                        .toList()

                    var identitySourceObj = this
/*
                    apps.forEach {

                        if(apps.map { it.id }.contains(it.id)){
                            var objId = it.id
                            mor.tenant.socialIdentitySourceConfig.update()
                                .where { it.id match identitySourceObj.id }
                                .pull({ it.tenantApps }, MongoColumnName("id") match objId)
                                .exec()
                        }
                    }*/
                    mor.tenant.socialIdentitySourceConfig.update()
                        .where { it.id match identitySourceObj.id }
                        .pull({ it.tenantApps }, MongoColumnName("id") match entity.id)
                        .exec()

                    var tenantIdentitySourceApp = TenantIdentitySourceApp()
                    tenantIdentitySourceApp.codeName = CodeName(entity.appCode, entity.name)
                    tenantIdentitySourceApp.logo = entity.logo
                    tenantIdentitySourceApp.isSysDefine = entity.isSysDefine
                    tenantIdentitySourceApp.sysAppId = entity.sysId
                    tenantIdentitySourceApp.sysAppStatus = entity.enabled
                    tenantIdentitySourceApp.id = entity.id
                    tenantIdentitySourceApp.status = false
                    mor.tenant.socialIdentitySourceConfig.update()
                        .where { it.id match identitySourceObj.id }
                        .push { it.tenantApps to tenantIdentitySourceApp }
                        .exec()
                }
            }
    }

    private fun checkParam(entity: TenantApplication): String {
        if (!entity.appCode.HasValue) {
            return "appCode不能为空"
        } else if (entity.appCode.length > 32) {
            return "appCode长度不能超过32"
        }
        if (!entity.url.HasValue) {
            return "url不能为空"
        } else {
            val urlPattern = "((http|ftp|https)://)([a-zA-Z0-9_-]+\\.)*"
            val isEmailMatch = Regex(urlPattern).containsMatchIn(entity.url!!)
            if (!isEmailMatch) {
                return "url地址格式错误"
            }
        }
        if (entity.url.toString().length > 225) {
            return "url长度最大225"
        }
        if (entity.lable.isEmpty()) {
            return "标签不能为空"
        }
        if (entity.lable.isNotEmpty()) {
            entity.lable.forEach {
                if (!tagValidate(it)) {
                    return "标签参数错误"
                }
            }
        }

        if (entity.remark.HasValue && entity.remark!!.length > 255) {
            return "备注长度不能超过255"
        }
        //编辑
        if (entity.id.HasValue) {
            val oldApp = mor.tenant.tenantApplication.query()
                .where { it.id match entity.id }
                .toEntity() ?: return "应用不存在"
            if (entity.appCode != oldApp.appCode) {
                return "AppCode不可更改"
            }

        }
        //新增
        if (!entity.id.HasValue) {
            mor.iam.sysApplication.query()
                .where { it.appCode match entity.appCode }
                .exists()
                .apply {
                    if (this) {
                        return "添加失败，AppCode已存在"

                    }
                }
            mor.tenant.tenantApplication.query()
                .where { it.appCode match entity.appCode }
                .where { it.tenant.id match entity.tenant.id }
                .exists()
                .apply {
                    if (this) {
                        return "添加失败，AppCode已存在"
                    }
                }
        }
        return ""
    }

    fun tagValidate(codeName: CodeName): Boolean {
        AppTagTypeEnum.values().forEach { b ->
            if (codeName.code == b.name) {
                if (codeName.name == b.remark) {
                    return true
                }
            }
        }
        return false
    }

    private fun checkUpdateParam(entity: TenantApplication): String {
        if (!entity.appCode.HasValue) {
            return "appCode不能为空"
        }

        if (!entity.name.trim().HasValue) {
            return "应用名称不能为空"
        } else if (entity.name.length > 32) {
            return "名称长度不能超过32"
        }
        if (entity.remark.HasValue && entity.remark!!.length > 255) {
            return "备注长度不能超过255"
        }
        mor.iam.sysApplication.query()
            .where { it.appCode match entity.appCode }
            .exists()
            .apply {
                if (this) {
                    return "不可修改管理端应用"

                }
            }
        mor.tenant.tenantApplication.query()
            .where { it.appCode match entity.appCode }
            .where { it.tenant.id match entity.tenant.id }
            .toEntity()
            .apply {
                if (this == null) {
                    return "应用不存在"
                } else {
                    if (!this.enabled) return "应用已停用"
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
        if (!id.HasValue && !appCode.HasValue) return JsonResult.error("appCode与id至少填写其中一个")
        val toEntity = mor.tenant.tenantApplication
            .query()
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (appCode.HasValue) {
                    this.where { it.appCode match appCode }
                }
            }
            .where { it.isSysDefine match false }// 只允许删除租户自己创建的应用
            .toEntity() ?: return JsonResult.error("找不到数据")
        val appCode = toEntity.appCode
        val id = toEntity.id

        request.logMsg = "删除租户侧应用{${toEntity.appCode}}"

        mor.tenant.tenantApplication.delete()
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (appCode.HasValue) {
                    this.where { it.appCode match appCode }
                }
            }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
                //todo Why?下面这段代码 有用吗

                mor.tenant.tenantApplication.delete()
                    .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                    .where { it.appCode match toEntity.appCode }
                    .exec()

                mor.tenant.tenantDepartmentInfo.query()
                    .where { it.allowApps.code match toEntity.appCode }
                    .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                    .toList()
                    .apply {
                        this.forEach {
                            it.allowApps.removeAll { it.code == toEntity.appCode }
                            mor.tenant.tenantDepartmentInfo.updateWithEntity(it).execUpdate()
                        }
                    }

                mor.tenant.tenantUserGroup.query()
                    .where { it.allowApps.code match toEntity.appCode }
                    .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                    .toList()
                    .apply {
                        this.forEach {
                            it.allowApps.removeAll { it.code == toEntity.appCode }
                            mor.tenant.tenantUserGroup.updateWithEntity(it).execUpdate()
                        }
                    }

                mor.tenant.tenantUser.query()
                    .where { it.allowApps.code match toEntity.appCode }
                    .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                    .toList()
                    .apply {
                        this.forEach {
                            it.allowApps.removeAll { it.code == toEntity.appCode }
                            mor.tenant.tenantUser.updateWithEntity(it).execUpdate()
                        }
                    }

                mor.tenant.tenantAppRole.delete()
                    .where { it.appInfo.code match toEntity.appCode }.exec()

                mor.tenant.tenantResourceInfo.delete()
                    .where { it.appInfo.code match toEntity.appCode }.exec()

                mor.tenant.tenantAppAuthResourceInfo.delete()
                    .where { it.appInfo.code match toEntity.appCode }.exec()

                return JsonResult()
            }
    }


    private fun updateRelationList(
        toEntity: TenantApplication,
        entity: TenantApplication,
        request: HttpServletRequest
    ) {


        // 级联修改租户侧拥有该应用的《用户》的属性
        mor.tenant.tenantUser.query()
            .where { it.allowApps.code match toEntity.appCode }
            .where { it.tenant.id match toEntity.tenant.id }
            .toList()
            .apply {
                this.forEach { tenantUser ->
                    tenantUser.allowApps.forEach {
                        var flag = false
                        if (it.code == toEntity.appCode && it.code != entity.appCode) {
                            it.code = entity.appCode
                            flag = true
                        }
                        if (it.name == toEntity.name && it.name != entity.name) {
                            it.name = entity.name
                            flag = true
                        }
                        if (flag) {
                            mor.tenant.tenantUser.updateWithEntity(tenantUser).execUpdate()
                        }
                    }
                }
            }
        // 级联修改租户侧拥有该应用的《部门》的属性
        mor.tenant.tenantDepartmentInfo.query().where { it.allowApps.code match toEntity.appCode }
            .where { it.tenant.id match toEntity.tenant.id }
            .toList()
            .apply {
                this.forEach { tenantDepart ->
                    tenantDepart.allowApps.forEach {
                        var flag = false
                        if (it.code == toEntity.appCode && it.code != entity.appCode) {
                            it.code = entity.appCode
                            flag = true
                        }
                        if (it.name == toEntity.name && it.name != entity.name) {
                            it.name = entity.name
                            flag = true
                        }
                        if (flag) {
                            mor.tenant.tenantDepartmentInfo.updateWithEntity(tenantDepart).execUpdate()
                        }
                    }
                }
            }

        // 级联修改租户侧拥有该应用的《用户组》的属性
        mor.tenant.tenantUserGroup.query().where { it.allowApps.code match toEntity.appCode }
            .where { it.tenant.id match toEntity.tenant.id }
            .toList()
            .apply {
                this.forEach { group ->
                    group.allowApps.forEach {
                        var flag = false
                        if (it.code == toEntity.appCode && it.code != entity.appCode) {
                            it.code = entity.appCode
                            flag = true
                        }
                        if (it.name == toEntity.name && it.name != entity.name) {
                            it.name = entity.name
                            flag = true
                        }
                        if (flag) {
                            mor.tenant.tenantUserGroup.updateWithEntity(group).execUpdate()
                        }
                    }
                }
            }

        // 级联修改租户侧拥有该应用的《角色》的属性
        mor.tenant.tenantAppRole.query().where { it.appInfo.code match toEntity.appCode }
            .where { it.tenant.id match toEntity.tenant.id }
            .toList()
            .apply {
                this.forEach { role ->
                    var flag = false
                    if (role.appInfo.code == toEntity.appCode && role.appInfo.code != entity.appCode) {
                        role.appInfo.code = entity.appCode
                        flag = true
                    }
                    if (role.appInfo.name == toEntity.name && role.appInfo.name != entity.name) {
                        role.appInfo.name = entity.name
                        flag = true
                    }
                    if (flag) {
                        mor.tenant.tenantAppRole.updateWithEntity(role).execUpdate()
                    }
                }
            }

        // 级联修改租户侧拥有该应用的《资源》的属性
        mor.tenant.tenantResourceInfo.query().where { it.appInfo.code match toEntity.appCode }
            .where { it.tenant.id match toEntity.tenant.id }
            .toList()
            .apply {
                this.forEach { resource ->
                    var flag = false
                    if (resource.appInfo.code == toEntity.appCode && resource.appInfo.code != entity.appCode) {
                        resource.appInfo.code = entity.appCode
                        flag = true
                    }
                    if (resource.appInfo.name == toEntity.name && resource.appInfo.name != entity.name) {
                        resource.appInfo.name = entity.name
                        flag = true
                    }
                    if (flag) {
                        mor.tenant.tenantResourceInfo.updateWithEntity(resource).execUpdate()
                    }
                }
            }

        // 级联修改租户侧拥有该应用的《授权》的属性
        mor.tenant.tenantAppAuthResourceInfo.query().where { it.appInfo.code match toEntity.appCode }
            .where { it.tenant.id match toEntity.tenant.id }
            .toList()
            .apply {
                this.forEach { auth ->
                    var flag = false
                    if (auth.appInfo.code == toEntity.appCode && auth.appInfo.code != entity.appCode) {
                        auth.appInfo.code = entity.appCode
                        flag = true
                    }
                    if (auth.appInfo.name == toEntity.name && auth.appInfo.name != entity.name) {
                        auth.appInfo.name = entity.name
                        flag = true
                    }
                    if (flag) {
                        mor.tenant.tenantAppAuthResourceInfo.updateWithEntity(auth).execUpdate()
                    }
                }
            }

        UpdateldapAndSocialApp(request, entity)

    }


    @BizLog(BizLogActionEnum.Enable, BizLogResourceEnum.App, "租户应用启用")
    @ApiOperation("租户的应用启用")
    @PostMapping("/enable")
    fun enabled(
        @Require appCode: String,
        request: HttpServletRequest
    ): ApiResult<String> {
        val enabled = true
        val msg = "启用"
        request.logMsg = "租户{${request.LoginTenantAdminUser.tenant.name}},{${appCode}}的${msg}"

        mor.tenant.tenantApplication.query()
            .where { it.appCode match appCode }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("应用不存在")
                }
                mor.iam.sysApplication.query()
                    .where { it.appCode match appCode }
                    .toEntity()
                    .apply {
                        if (this != null) {
                            if (this.enabled == false) {
                                return ApiResult.error("应用已在admin端禁用，不允许修改状态")
                            }
                        }
                    }
                if (this.enabled == enabled) {
                    return ApiResult.error("应用已启用")
                }
            }


        mor.tenant.tenantApplication.update()
            .where { it.appCode match appCode }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .set { it.enabled to enabled }
            .exec()
            .apply {
                if (this == 0) {
                    return ApiResult.error("操作失败")
                }
            }

        return ApiResult()
    }

    @BizLog(BizLogActionEnum.Disable, BizLogResourceEnum.App, "租户应用停用")
    @ApiOperation("租户的应用禁用")
    @PostMapping("/disable")
    fun disabled(
        @Require appCode: String,
        request: HttpServletRequest
    ): ApiResult<String> {
        val enabled = false
        val msg = "停用"
        request.logMsg = "租户{${request.LoginTenantAdminUser.tenant.name}},{${appCode}}的${msg}"

        mor.tenant.tenantApplication.query()
            .where { it.appCode match appCode }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("应用不存在")
                }
                mor.iam.sysApplication.query()
                    .where { it.appCode match appCode }
                    .toEntity()
                    .apply {
                        if (this != null) {
                            if (this.enabled == false) {
                                return ApiResult.error("应用已在admin端禁用，不允许修改状态")
                            }
                        }
                    }
                if (this.enabled == enabled) {
                    return ApiResult.error("应用已停用")
                }
            }


        mor.tenant.tenantApplication.update()
            .where { it.appCode match appCode }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .set { it.enabled to enabled }
            .exec()
            .apply {
                if (this == 0) {
                    return ApiResult.error("操作失败")
                }
            }

        return ApiResult()
    }

}
