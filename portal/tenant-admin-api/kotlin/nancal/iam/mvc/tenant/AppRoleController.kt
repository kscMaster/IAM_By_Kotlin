package nancal.iam.mvc.tenant

import io.swagger.annotations.*
import nancal.iam.annotation.BizLog
import nancal.iam.annotation.CheckTenantAppStatus
import nancal.iam.base.extend.*
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.service.compute.TenantUserService
import nbcp.comm.*
import nbcp.db.*
import nbcp.db.mongo.*
import nbcp.base.mvc.*
import nbcp.web.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.query.*
import org.springframework.web.bind.annotation.*
import java.time.*
import javax.annotation.Resource
import javax.servlet.http.*


/**
 * Created by CodeGenerator at 2021-11-20 09:54:46
 */
@CheckTenantAppStatus
@Api(description = "应用角色", tags = arrayOf("AppRole"))
@RestController
@RequestMapping("/tenant/app-role")
class AppRoleAutoController {
    @Value("\${openPrivatization}")
    private val openPrivatization: Boolean = true

    @Resource
    lateinit var authService: TenantUserService

    class RoleListVO : IdName() {
        var remark: String = ""
        var createAt: LocalDateTime? = null
    }

    @ApiOperation("角色列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        name: String,
        remark: String,
        appInfoId: String,
        appInfoName: String,
        keywords: String,
        createAt: LocalDateTime?,
        endAt: LocalDateTime?,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantAppRole> {
        mor.tenant.tenantAppRole.query().apply {
            if (id.HasValue) {
                this.where { it.id match id }
            }
            if (name.HasValue) {
                this.where { it.name match_like name }
            }
            if (keywords.HasValue) {
                this.whereOr(
                    { it.name match_like keywords },
                    { it.remark match_like keywords })
            }
            if (remark.HasValue) {
                this.where { it.remark match_like remark }
            }
            if (appInfoName.HasValue) {
                this.where { it.appInfo.name match_like appInfoName }
            }
            if (appInfoId.HasValue) {
                this.where { it.appInfo.code match appInfoId }
            }
            this.where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            if (createAt != null) {
                this.where { it.createAt match_gte createAt }
            }
            if (endAt != null) {
                this.where { it.createAt match_lte endAt }
            }
        }.limit(skip, take).orderByDesc { it.createAt }.toListResult().apply {
            return this
        }
    }

    class AppRoleDetailVO : IdName() {
        var remark: String = ""

        var isSysDefine: Boolean = false

        class UserMobileEmail : IdName() {
            var mobile: String = ""
            var email: String = ""
        }

        class IdNameCount : IdName() {
            var count: Int = 0
        }

        var appInfo: IdCodeName = IdCodeName()
        var tenant: IdName = IdName()
        var users: MutableList<UserMobileEmail> = mutableListOf()
        var depts: MutableList<IdNameCount> = mutableListOf()
        var groups: MutableList<IdNameCount> = mutableListOf()
        var sysId: String = ""

        @Cn("不允许被删除")
        var notAllowDeleted: Boolean = false
    }

    @ApiOperation("角色批量获取")
    @PostMapping("/bathIds")
    fun bathIds(
        @Require ids: List<String>, request: HttpServletRequest
    ): ApiResult<List<AppRoleDetailVO>> {
        if (ids.isEmpty()) {
            return ApiResult.error("参数不能为空")
        }
        val res: MutableList<AppRoleDetailVO> = mutableListOf()
        var toList: MutableList<AppRoleDetailVO> = mor.tenant.tenantAppRole.query()
            .where { it.id match_in ids }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toList(AppRoleDetailVO::class.java)
        if (toList.isNotEmpty()) {
            toList.forEach { item ->
                // 用户信息
                item.users = mor.tenant.tenantUser.query()
                    .where { it.roles.id match item.id }
                    .apply {
                        if (openPrivatization) {
                            this.where { it.adminType match TenantAdminTypeEnum.None }
                        }
                    }.toList(AppRoleDetailVO.UserMobileEmail::class.java)
                // 部门信息
                item.depts = mor.tenant.tenantDepartmentInfo.query().where { it.roles.id match item.id }
                    .toList(AppRoleDetailVO.IdNameCount::class.java)
                    .apply {
                        this.forEach { dept ->
                            dept.count = mor.tenant.tenantUser.query()
                                .where { it.depts.id match dept.id }
                                .apply {
                                    if (openPrivatization) {
                                        this.where { it.adminType match TenantAdminTypeEnum.None }
                                    }
                                }
                                .count()
                        }
                    }
                item.groups = mor.tenant.tenantUserGroup.query().where { it.roles.id match item.id }
                    .toList(AppRoleDetailVO.IdNameCount::class.java)
                    .apply {
                        this.forEach { group ->
                            group.count = mor.tenant.tenantUser.query()
                                .where { it.groups.id match group.id }
                                .apply {
                                    if (openPrivatization) {
                                        this.where { it.adminType match TenantAdminTypeEnum.None }
                                    }
                                }
                                .count()
                        }
                    }

                res.add(item)

            }
            return ApiResult.of(toList)
        }
        return ApiResult.error("找不到数据")
    }


    @ApiOperation("角色详情")
    @PostMapping("/detail/{id}")
    fun detail(
        @Require id: String, request: HttpServletRequest
    ): ApiResult<AppRoleDetailVO> {
        mor.tenant.tenantAppRole.query()
            .where { it.id match id }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity(AppRoleDetailVO::class.java).apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }
                this.users = mor.tenant.tenantUser.query()
                    .where { it.roles.id match this.id }
                    .apply {
                        if (openPrivatization) {
                            this.where { it.adminType match TenantAdminTypeEnum.None }
                        }
                    }
                    .toList(AppRoleDetailVO.UserMobileEmail::class.java)

                this.depts = mor.tenant.tenantDepartmentInfo.query().where { it.roles.id match this.id }
                    .toList(AppRoleDetailVO.IdNameCount::class.java).apply {
                        // 遍历部门
                        // 通过部门ID 查出用户数
                        // 赋值给各部门用户数
                        this.forEach { dept ->
                            dept.count = mor.tenant.tenantDepartmentInfo.query()
                                .where { it.id match dept.id }
                                .select { it.userCount }
                                .toEntity(Int::class.java)!!
                        }
                    }

                this.groups = mor.tenant.tenantUserGroup.query().where { it.roles.id match this.id }
                    .toList(AppRoleDetailVO.IdNameCount::class.java).apply {
                        // 遍历组
                        // 通过组ID 查出用户数
                        // 赋值给各组用户数
                        this.forEach { group ->
                            group.count = mor.tenant.tenantUser.query()
                                .where { it.groups.id match group.id }
                                .apply {
                                    if (openPrivatization) {
                                        this.where { it.adminType match TenantAdminTypeEnum.None }
                                    }
                                }
                                .count()
                        }
                    }
                return ApiResult.of(this)
            }
    }


    class AppRoleSaveVO : IdName() {
        @Cn("应用IdName")
        var appInfo: CodeName = CodeName()

        @Cn("租户")
        var tenant: IdName = IdName()
        var remark: String = ""
        var users: MutableList<IdName> = mutableListOf()
        var depts: MutableList<IdName> = mutableListOf()
        var groups: MutableList<IdName> = mutableListOf()
        var isSysDefine: Boolean = false
        var sysId: String = ""
        var notAllowDeleted: Boolean = false
    }

    @BizLog(BizLogActionEnum.Save, BizLogResourceEnum.TenantRole, "角色更新")
    @ApiOperation("角色更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: AppRoleSaveVO, request: HttpServletRequest
    ): ApiResult<String> {
        if (!entity.appInfo.code.HasValue || !entity.name.HasValue) {
            request.logMsg = "角色名称和应用ID均不能为空"
            return ApiResult.error("角色名称和应用ID均不能为空")
        }

        if (entity.name.length > 32) {
            return ApiResult.error("角色名称不能超过32位")
        }

        if (entity.remark.HasValue && entity.remark.length > 255) {
            return ApiResult.error("备注不能超过255位")
        }

        entity.users.forEach { obj ->

            if (!obj.id.HasValue) {
                return ApiResult.error("用户id不能为空")
            }

            mor.tenant.tenantUser.query()
                .where { it.id match obj.id }
                .where { it.name match obj.name }
                .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                .apply {
                    if (openPrivatization) {
                        this.where { it.adminType match TenantAdminTypeEnum.None }
                    }
                }
                .toEntity()
                .apply {
                    if (this == null) {
                        return ApiResult.error("{" + obj.name + "}" + "用户不存在")
                    }
                }
        }

        entity.depts.forEach { obj ->
            if (!obj.id.HasValue) {
                return ApiResult.error("部门id不能为空")
            }
            mor.tenant.tenantDepartmentInfo.query().where { it.id match obj.id }
                .where { it.name match obj.name }
                .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                .toEntity()
                .apply {
                    if (this == null) {
                        return ApiResult.error("{" + obj.name + "}" + "部门不存在")
                    }
                }
        }

        entity.groups.forEach { obj ->
            if (!obj.id.HasValue) {
                return ApiResult.error("用户组id不能为空")
            }
            mor.tenant.tenantUserGroup.query().where { it.id match obj.id }
                .where { it.name match obj.name }
                .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                .toEntity()
                .apply {
                    if (this == null) {
                        return ApiResult.error("{" + obj.name + "}" + "组不存在")
                    }
                }
        }

        val loginUser = request.LoginTenantAdminUser
        //鉴权
        var userId = request.UserId
        // 角色  修改/新增  只操作基础的三个字段
        val entityTemp = TenantAppRole()
        entityTemp.id = entity.id
        entityTemp.name = entity.name
        entityTemp.remark = entity.remark
        entityTemp.appInfo = entity.appInfo
        entityTemp.tenant = loginUser.tenant
        entity.tenant = loginUser.tenant
        entityTemp.notAllowDeleted = entity.notAllowDeleted

        if (entity.isSysDefine == true) {
            entityTemp.isSysDefine = entity.isSysDefine
            entityTemp.sysId = entity.sysId
        }

        // 同一应用下的角色名称不能重复
        mor.tenant.tenantAppRole
            .query()
            .where { it.appInfo.code match entityTemp.appInfo.code }
            .where { it.name match entityTemp.name }
            .where { it.tenant.id match loginUser.tenant.id }
            .apply {
                if (entity.id.HasValue) {
                    this.where { it.id match_not_equal entity.id }
                }
            }
            .exists()
            .also { appRoleExists ->
                if (appRoleExists) {
                    request.logMsg = "角色{${entity.name}}已存在"
                    return ApiResult.error("角色名称已存在")
                }
            }

        mor.tenant.tenantApplication.query()
            .where { it.appCode match entity.appInfo.code }
            .where { it.tenant.id match loginUser.tenant.id }
            .toEntity()
            .apply {
                if (this == null) {
                    request.logMsg = "应用{${entity.appInfo.code}}不存在"
                    return ApiResult.error("应用不存在！")
                }
            }

        var isInsert = false

        mor.tenant.tenantAppRole
            .updateWithEntity(entityTemp)

            .run {
                if (entity.id.HasValue) {
                    request.logMsg = "角色{${entityTemp.name}}修改成功"
                    return@run this.execUpdate()
                } else {
                    isInsert = true
                    request.logMsg = "角色{${entityTemp.name}}添加成功"
                    return@run this.execInsert()
                }
            }.apply {
                if (this == 0) {
                    request.logMsg = "角色{${entityTemp.name}}操作失败"
                    return ApiResult.error("更新失败")
                }
                // 角色修改，涉及用户、组、部门 的授权，此处为修改授权信息
                if (isInsert == false) {
                    // 入参为null时会报异常：
                    // Parameter specified as non-null is null: method nancal.iam.mvc.tenant.AppRoleAutoController.updateDepts, parameter depts
                    updateDepts(IdName(entity.id, entity.name), entity.depts)
                    updateGroups(IdName(entity.id, entity.name), entity.groups)
                    //私有化
                    val finalUsers = mor.tenant.tenantUser.query()
                        .where { it.tenant.id match loginUser.tenant.id }
                        .where { user -> user.id match_in entity.users.map { it.id } }
                        .apply {
                            if (openPrivatization) {
                                this.where { it.adminType match TenantAdminTypeEnum.None }
                            }
                        }
                        .toList(IdName::class.java)
                    entity.users = finalUsers
                    updateUsers(IdName(entity.id, entity.name), entity.users)
                    mor.tenant.tenantAppAuthResourceInfo.update()
                        .where { it.type match AuthTypeEnum.Role }
                        .where { it.target.id match entity.id }
                        .set { it.target.name to entity.name }
                        .exec()
                }
            }
        return ApiResult.of(entityTemp.id)
    }

    fun updateGroups(role: IdName, groups: MutableList<IdName>) {
        mor.tenant.tenantUserGroup
            .update()
            .where { it.roles.id match role.id }
            .pull({ it.roles }, MongoColumnName("id") match role.id)
            .exec()


        if (groups.isEmpty()) return;
        var ids = groups.map { it.id }

        mor.tenant.tenantUserGroup.update()
            .where { it.id match_in ids }
            .push { it.roles to role }
            .exec()
    }

    fun updateDepts(role: IdName, depts: MutableList<IdName>) {
        mor.tenant.tenantDepartmentInfo
            .update()
            .where { it.roles.id match role.id }
            .pull({ it.roles }, MongoColumnName("id") match role.id)
            .exec()

        if (depts.isEmpty()) return;

        var ids = depts.map { it.id }
        mor.tenant.tenantDepartmentInfo
            .update()
            .where { it.id match_in ids }
            .push { it.roles to role }
            .exec()
    }

    fun updateUsers(role: IdName, userIds: MutableList<IdName>) {
        mor.tenant.tenantUser
            .update()
            .where { it.roles.id match role.id }
            .pull({ it.roles }, MongoColumnName("id") match role.id)
            .exec()

        var ids = userIds.map { it.id }

        mor.tenant.tenantUser
            .update()
            .where { it.id match_in ids }
            .push { it.roles to role }
            .exec()
    }

    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.TenantRole, "角色删除")
    @ApiOperation("角色删除")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String, request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "角色删除"
        val tenantAppRole = mor.tenant.tenantAppRole.queryById(id).toEntity() ?: return JsonResult.error("找不到数据")

        if (tenantAppRole.notAllowDeleted) {
            return JsonResult.error("当前角色不允许被删除")
        }

        mor.tenant.tenantAppRole.delete()
            .where { it.id match id }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .where { it.isSysDefine match false }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
                //拥有该角色的组、部门、人员，删除对应角色关联
                mor.tenant.tenantUserGroup.query().where { it.roles.id match id }.toList().apply {
                    this.forEach {
                        it.roles.removeAll { it.id == id }
                        mor.tenant.tenantUserGroup.updateWithEntity(it).execUpdate()
                    }
                }
                mor.tenant.tenantDepartmentInfo.query().where { it.roles.id match id }.toList().apply {
                    this.forEach {
                        it.roles.removeAll { it.id == id }
                        mor.tenant.tenantDepartmentInfo.updateWithEntity(it).execUpdate()
                    }
                }
                mor.tenant.tenantUser.query().where { it.roles.id match id }.toList().apply {
                    this.forEach {
                        it.roles.removeAll { it.id == id }
                        mor.tenant.tenantUser.updateWithEntity(it).execUpdate()
                    }
                }

                mor.tenant.tenantAppAuthResourceInfo.query().where { it.type match AuthTypeEnum.Role }
                    .where { it.target.id match id }.toList().apply {
                        this.forEach {
                            mor.tenant.tenantAppAuthResourceInfo.deleteById(it.id).exec()
                        }
                    }

                return JsonResult()
            }
    }

    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.TenantRole, "角色批量删除")
    @ApiOperation("批量删除")
    @PostMapping("/delete/batch")
    fun deleteBatch(
        @Require ids: List<String>,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "角色批量删除"
        val roleList = mor.tenant.tenantAppRole.query().where { it.id match_in ids }.toList()

        if (roleList.isEmpty()) {
            // 为空直接返回
            return JsonResult.error("未查询到要删除的数据")
        }

        val opt = roleList.stream().filter { r -> r.isSysDefine }.findFirst()
        if (opt.isPresent) {
            return JsonResult.error("系统默认角色：{${opt.get().name}} 不允许被删除！")
        }

        mor.tenant.tenantAppRole.delete()
            .where { it.id match_in ids }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .where { it.isSysDefine match false }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
                //拥有该角色的组、部门、人员，删除对应角色关联
                mor.tenant.tenantUserGroup
                    .query()
                    .where { it.roles.id match_in ids }
                    .toList()
                    .forEach {
                        it.roles.removeAll { it.id in ids }
                        mor.tenant.tenantUserGroup.updateWithEntity(it).execUpdate()
                    }

                mor.tenant.tenantDepartmentInfo.query().where { it.roles.id match_in ids }.toList().apply {
                    this.forEach {
                        it.roles.removeAll { it.id in ids }
                        mor.tenant.tenantDepartmentInfo.updateWithEntity(it).execUpdate()
                    }
                }
                mor.tenant.tenantUser
                    .query()
                    .where { it.roles.id match_in ids }
                    .toList()
                    .forEach {
                        it.roles.removeAll { it.id in ids }
                        mor.tenant.tenantUser.updateWithEntity(it).execUpdate()
                    }


                mor.tenant.tenantAppAuthResourceInfo
                    .query()
                    .where { it.type match AuthTypeEnum.Role }
                    .where { it.target.id match_in ids }
                    .toList()
                    .apply {
                        if (this.size > 0) {
                            this.forEach {
                                mor.tenant.tenantAppAuthResourceInfo.deleteById(it.id).exec()
                            }
                        }
                    }
            }
        return JsonResult()
    }

    @BizLog(BizLogActionEnum.Update, BizLogResourceEnum.TenantRole, "更新用户角色")
    @ApiOperation("角色更新")
    @PostMapping("/updateUserRole")
    fun updateUserRole(
        loginName: String, // 可以传loginName  mobile  email
        oldRoleId: String,
        newRoleId: String,
        request: HttpServletRequest
    ): JsonResult {

        request.logMsg = "更新用户的角色"
        var loginUser = request.LoginTenantAdminUser
        if (!loginName.HasValue) {
            return JsonResult.error("账户标识信息不能为空")
        }
        if (oldRoleId.HasValue) {
            val oldRole = mor.tenant.tenantAppRole.queryById(oldRoleId)
                .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                .toEntity()
            if (oldRole == null) {
                return JsonResult.error("旧角色不存在")
            }
            mor.tenant.tenantUser.update()
                .whereOr({ it.loginName match loginName }, { it.mobile match loginName }, { it.email match loginName })
                .where { it.tenant.id match loginUser.tenant.id }
                .apply {
                    if (openPrivatization) {
                        this.where { it.adminType match TenantAdminTypeEnum.None }
                    }
                }
                .pull({ it.roles }, MongoColumnName("id") match oldRoleId).exec().apply {
                    if (this == 0) {
                        return JsonResult.error("删除旧角色失败")
                    }
                }
        }
        if (newRoleId.HasValue) {
            val newRole = mor.tenant.tenantAppRole.queryById(newRoleId)
                .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                .toEntity()
            if (newRole == null) {
                return JsonResult.error("新角色不存在")
            }

            val hasRole = mor.tenant.tenantUser.query()
                .where {
                    it.roles.id match newRoleId
                }
                .whereOr({ it.loginName match loginName }, { it.mobile match loginName }, { it.email match loginName })
                .apply {
                    if (openPrivatization) {
                        this.where { it.adminType match TenantAdminTypeEnum.None }
                    }
                }
                .exists()
            if (hasRole) {
                return JsonResult.error("用户已拥有该角色")
            }

            val roleObj: IdName = IdName()
            roleObj.id = newRoleId
            roleObj.name = newRole.name

            mor.tenant.tenantUser.update()
                .apply {
                    if (openPrivatization) {
                        this.where { it.adminType match TenantAdminTypeEnum.None }
                    }
                }
                .whereOr({ it.loginName match loginName }, { it.mobile match loginName }, { it.email match loginName })
                .where { it.tenant.id match loginUser.tenant.id }.push { it.roles to roleObj }.exec().apply {
                    if (this == 0) {
                        return JsonResult.error("新增新角色失败")
                    }
                }
        }

        return JsonResult()
    }

    class RoleUserInfo : IdName() {
        var email: String = ""

        // 用户所属部门
        var depts: MutableList<DeptDefine> = mutableListOf()
    }

    class RoleUserResp {
        var users: MutableList<RoleUserInfo> = mutableListOf()

        // 角色已授权部门
        var roleDepts: MutableList<IdName> = mutableListOf()

        // 角色已授权用户组
        var roleGroups: MutableList<IdName> = mutableListOf()
    }

    @ApiOperation("获得某个角色下的所有用户列表（用户姓名、邮箱、末级(主)部门名称")
    @PostMapping("/getUserListByRoleId")
    fun getUserListByRoleId(
        @Require roleId: String, request: HttpServletRequest
    ): ApiResult<RoleUserResp> {
        val tenantId = request.LoginTenantAdminUser.tenant.id

        val depts = mor.tenant.tenantDepartmentInfo.query().where { it.roles.id match roleId }
            .where { it.tenant.id match tenantId }.toList(IdName::class.java)

        val groups =
            mor.tenant.tenantUserGroup.query().where { it.roles.id match roleId }.where { it.tenant.id match tenantId }
                .toList(IdName::class.java)

        val users = mor.tenant.tenantUser.query()
            .where { it.tenant.id match tenantId }
            .apply {
                if (openPrivatization) {
                    this.where { it.adminType match TenantAdminTypeEnum.None }
                }
            }
            .apply {
                if (depts.isNotEmpty() && groups.isNotEmpty()) {
                    // 角色下有部门和分组
                    this.whereOr({ it.roles.id match roleId },
                        { it.depts.id match_in depts.map { it.id } },
                        { it.groups.id match_in groups.map { it.id } })
                } else if (depts.isNotEmpty() && groups.isEmpty()) {
                    // 角色下有部门
                    this.whereOr({ it.roles.id match roleId }, { it.depts.id match_in depts.map { it.id } })
                } else if (depts.isEmpty() && groups.isNotEmpty()) {
                    // 角色下有分组
                    this.whereOr({ it.roles.id match roleId }, { it.groups.id match_in groups.map { it.id } })
                } else {
                    // 角色下既没有部门也没有分组
                    this.where { it.roles.id match roleId }
                }
            }.toList(RoleUserInfo::class.java).sortedBy { it.name }

        val resp: RoleUserResp = RoleUserResp()
        if (users.isNotEmpty()) {
            resp.users.addAll(users)
        }
        if (depts.isNotEmpty()) {
            resp.roleDepts = depts.sortedBy { it.name } as MutableList<IdName>
        }
        if (groups.isNotEmpty()) {
            resp.roleGroups = groups.sortedBy { it.name } as MutableList<IdName>
        }

        return ApiResult.of(resp)
    }


    @ApiOperation("通过资源ID查询授权的角色")
    @PostMapping("/getRoleListByResourceId")
    fun getRoleListByResourceId(
        @Require resourceId: String,
        request: HttpServletRequest
    ): ListResult<TenantAppRole> {
        mor.tenant.tenantAppAuthResourceInfo.query()
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .where { it.type match AuthTypeEnum.Role }
            .where { it.auths.resourceId match resourceId }
            .toList()
            .apply {
                if (this.size < 1) {
                    return ListResult.error("未查询到数据")
                }
                var roleIdList = mutableListOf<String>()
                this.forEach {
                    roleIdList.add(it.target.id)
                }
                mor.tenant.tenantAppRole.query()
                    .where { it.id match_in roleIdList }
                    .toListResult()
                    .apply {
                        return this
                    }
            }
    }


    class RoleListWithUsersVO : TenantAppRole() {
        var users: MutableList<IdName> = mutableListOf()
    }

    @ApiOperation("角色列表")
    @PostMapping("/list-with-users")
    fun listWithUsers(
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<RoleListWithUsersVO> {
        mor.tenant.tenantAppRole.query()
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .limit(skip, take)
            .orderByDesc { it.createAt }
            .toListResult(RoleListWithUsersVO::class.java)
            .apply {

                this.data.forEach { roleListVO ->
                    roleListVO.users = mor.tenant.tenantUser.query()
                        .where { it.roles.id match roleListVO.id }
                        .apply {
                            if (openPrivatization) {
                                this.where { it.adminType match TenantAdminTypeEnum.None }
                            }
                        }
                        .toList(IdName::class.java)
                }
                return this
            }
    }

    open class AuthResourceDataTemp {
        var code: String = ""
        var action: List<String> = listOf()
        var type: ResourceTypeEnum? = null
        var dataAccessLevel: AccessLevelEnum? = null
        var children: MutableList<AuthResourceDataTemp> = mutableListOf()
        var simpleCode: String = ""
    }


    @PostMapping("/my-allow-resources-action")
    fun myAuthResourcesAction(
        @Require appCode: String,
        @Require roleCode: String,
        @Require resourceType: ResourceTypeEnum,
        @Require policy: AuthResourceConflictPolicyEnum,
        request: HttpServletRequest,
    ): ListResult<AuthResourceDataTemp> {
        val myAuthResourcesAction = authService.getMyAuthResourcesActionOfRole(
            request.LoginUser.id,
            appCode,
            roleCode,
            resourceType,
            policy,
            AuthResourceTypeEnum.Allow
        )

        var queryData = myAuthResourcesAction.data

        var controlData = queryData.ConvertListJson(AuthResourceDataTemp::class.java)
        var resultData = controlData


        var codes = mutableListOf<String>()

        if (controlData.isNotEmpty()) {
            controlData.forEach {
                val split = it.code.split(":")
                for (i in resultData.indices) {

                    val newCodeSize = resultData.get(i).code.split(":").size

                    resultData.get(i).simpleCode = resultData.get(i).code.split(":").get(newCodeSize - 1)

                    if (newCodeSize < split.size) {

                        conditionMethod(split, resultData, i, it, codes)
                    }
                }
            }
        }
        if (codes.size > 0) {
            val filterNot = resultData.filterNot { it.code in codes }
            return ListResult.of(filterNot)
        }

        return ListResult.of(resultData)
    }


    private fun conditionMethod(
        split: List<String>,
        resultData: List<AuthResourceDataTemp>,
        i: Int,
        it: AuthResourceDataTemp,
        codes: MutableList<String>,
    ) {
        val newCodeSize = resultData.get(i).code.split(":").size
        var list = mutableListOf<Boolean>()
        for (k in 0..newCodeSize - 1) {
            list.add(split.get(k).equals(resultData.get(i).code.split(":").get(k)))
        }
        if (
            !list.contains(false) && newCodeSize + 1 == it.code.split(":").size
        ) {
            resultData.get(i).children.add(it)
            codes.add(it.code)
        }
    }

}
