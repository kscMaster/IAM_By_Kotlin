package nancal.iam.mvc.tenant

import io.swagger.annotations.*
import nancal.iam.annotation.BizLog
import nancal.iam.aspect.CheckObject
import nancal.iam.aspect.CheckObjects
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nbcp.comm.*
import nbcp.db.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nbcp.web.*
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.servlet.http.*

/**
 * Created by CodeGenerator at 2021-11-17 17:47:42
 */
@Api(description = "应用用户分组", tags = arrayOf("TenantUserGroup"))
@RestController
@RequestMapping("/tenant/tenant-user-group")
class TenantUserGroupAutoController {

    @Value("\${openPrivatization}")
    private val openPrivatization: Boolean = true


    class CountGroup : TenantUserGroup() {
        var count: Int = 0
    }


    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        name: String,
        keywords: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<CountGroup> {

        mor.tenant.tenantUserGroup.query()
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                this.where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                if (keywords.HasValue) {
                    this.where { it.name match_like keywords }
                }
                if (name.HasValue) {
                    this.where { it.name match_like name }
                }
            }
            .limit(skip, take).orderByDesc { it.createAt }.orderByDesc{it.id}
            .toListResult(CountGroup::class.java)
            .apply {
                this.data.forEach { group ->
                    val userCount = mor.tenant.tenantUser.query()
                        .where { it.groups.id match group.id }.count()
                    group.count = userCount
                }
                return this
            }
    }

    class UserTemp : IdName() {
        var deptName :String = ""
        var isMain:Boolean? =null
    }

    /**
     * 详情返回列表队形
     */
    class GroupDetailVO : TenantUserGroup() {
        var users: MutableList<UserTemp> = mutableListOf()

    }

    class GroupUserVo {
        var id :String = ""
        var users: MutableList<UserTemp> = mutableListOf()
        var tenant: IdName = IdName()
        var name: String = ""
        var remark: String = ""
        var enabled: Boolean = false
        var roles: MutableList<IdName> = mutableListOf()
        var isDeleted :Boolean? =false
        var createAt: LocalDateTime = LocalDateTime.now()
        var updateAt: LocalDateTime? = null
    }

    @ApiOperation("批量查询")
    @PostMapping("/bathIds")
    fun detail(
        @Require ids: List<String>,
        request: HttpServletRequest
    ): ApiResult<List<GroupUserVo>> {
        var res : MutableList<GroupUserVo> = mutableListOf()
        if (ids.isEmpty()) {
            return ApiResult.error("参数不能为空")
        }
        val tenant = request.LoginTenantAdminUser.tenant
        mor.tenant.tenantUserGroup.query()
            .where { it.tenant.id match tenant.id }
            .where { it.id match_in ids }
            .toList(GroupDetailVO::class.java)
            .apply {
                if (this.isEmpty()) {
                    return ApiResult.error("找不到数据")
                }
                this.forEach { item ->
                    val users: MutableList<UserTemp> = mutableListOf()
                    // 显示  人的部门 信息
                    val toList: MutableList<TenantUser> = mor.tenant.tenantUser.query()
                        .where { it.groups.id match item.id }
                        .where { it.tenant.id match tenant.id }
                        .toList(TenantUser::class.java)
                    if (toList.isNotEmpty()) {
                        toList.forEach {
                            val mainUser = UserTemp()
                            mainUser.id = it.id
                            mainUser.name = it.name
                            it.depts.forEach {
                                if (it.isMain) {
                                    mainUser.deptName = it.name
                                    mainUser.isMain = true
                                }
                            }
                            users.add(mainUser)
                        }


                        item.users = users
                    }
                    val vo = GroupUserVo()
                    BeanUtils.copyProperties(item, vo)
                    vo.users = item.users
                    vo.remark = item.remark
                    res.add(vo)

                }
                return ApiResult.of(res)
            }


    }

    @ApiOperation("详情")
    @PostMapping("/detail/{id}")
    fun detail(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<GroupDetailVO> {
        val tenant = request.LoginTenantAdminUser.tenant
        mor.tenant.tenantUserGroup.queryById(id)
            .where { it.tenant.id match tenant.id }
            .toEntity(GroupDetailVO::class.java)
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }
                val users: MutableList<UserTemp> = mutableListOf()
                // 显示  人的部门 信息
                mor.tenant.tenantUser.query()
                    .where { it.groups.id match id }
                    .where { it.tenant.id match tenant.id }
                    .apply {
                        if(openPrivatization){
                            this.where { it.adminType match  TenantAdminTypeEnum.None}
                        }
                    }
                    .toList(TenantUser::class.java).apply {
                        this.forEach {
                            var mainUser = UserTemp()
                            mainUser.id = it.id
                            mainUser.name = it.name
                            it.depts.forEach {
                                if(it.isMain ==true){
                                    mainUser.deptName = it.name
                                    mainUser.isMain = true
                                }
                            }
                            users.add(mainUser)
                        }
                    }

                this.users = users

                return ApiResult.of(this)
            }
    }

    @ApiOperation("更新")
    @PostMapping("/save")
    @CheckObjects(
        CheckObject(path = "entity.name",js = "if(value.length < 3) return '组名长度不能小于3位！'; ", reg = "/.{2,18}/"),
        CheckObject(path = "entity.name",js = "if(value.length < 3) return '组名长度不能小于3位！'; "),
        CheckObject(path = "entity.name",js = "if(value.length < 3) return '组名长度不能小于3位！'; ")
    )
    @BizLog(BizLogActionEnum.Save, BizLogResourceEnum.UserGrop, "用户组")
    fun save(
        @JsonModel entity: GroupDetailVO,
        request: HttpServletRequest
    ): ApiResult<String> {
        if(entity.id.isNotEmpty()){
            request.logMsg="编辑用户组{${entity.name}}"
        }else{
            request.logMsg="新建用户组{${entity.name}}"
        }
        var loginUser = request.LoginTenantAdminUser
        entity.tenant = loginUser.tenant

        if(!entity.name.HasValue){
            return ApiResult.error("组名不能为空")
        }
        if(entity.name.length <2){
            return ApiResult.error("组名不能小于2个字符")
        }
        if(entity.name.length >32){
            return ApiResult.error("组名不能大于32个字符")
        }


        // 同一租户下的组名称不能重复
        var exists = mor.tenant.tenantUserGroup.query()
            .where { it.tenant.id match entity.tenant.id }
            .where{it.name match entity.name}
            .apply {
                if(entity.id.HasValue){
                    this.where { it.id match_not_equal entity.id }
                }
            }
            .exists()
        if(exists){
            return ApiResult.error("组名称已存在")
        }
        var isInsert = false

        mor.tenant.tenantUserGroup.updateWithEntity(entity)
            
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

                if(!isInsert){
                    mor.tenant.tenantUser.update()
                        .where { it.tenant.id match loginUser.tenant.id }
                        .where { it.groups.id match entity.id }
                        .apply {
                            if(openPrivatization){
                                this.where{it.adminType match TenantAdminTypeEnum.None}
                            }
                        }
                        .pull({ it.groups }, MongoColumnName("id") match entity.id)
                        .exec()

                    var ids: MutableList<String> = mutableListOf()
                    entity.users.forEach {
                        ids.add(it.id)
                    }

                    var groupEntity :IdName = IdName()
                    groupEntity.id = entity.id
                    groupEntity.name = entity.name

                    mor.tenant.tenantUser.update()
                        .where { it.tenant.id match loginUser.tenant.id }
                        .where { it.id match_in ids }
                        .apply {
                            if(openPrivatization){
                                this.where{it.adminType match TenantAdminTypeEnum.None}
                            }
                        }
                        .push { it.groups to groupEntity }
                        .exec()

                    mor.tenant.tenantAppAuthResourceInfo.update()
                        .where { it.tenant.id match loginUser.tenant.id }
                        .where { it.type match AuthTypeEnum.Group }
                        .where { it.target.id match  entity.id }
                        .set { it.target.name to entity.name  }
                        .exec()

                }

                return ApiResult.of(entity.id)
            }
    }
    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.UserGrop, "删除组")
    @ApiOperation("删除组")
    @PostMapping("/delete/{id}")
    fun delete(
            @Require id: String,
            request: HttpServletRequest
    ): JsonResult {
        request.logMsg="删除用户组{${id}}"
        val tenant = request.LoginTenantAdminUser.tenant
        mor.tenant.tenantUserGroup.queryById(id)
            .where { it.tenant.id match tenant.id }
            .toEntity() ?: return JsonResult.error("找不到数据")
        //删除组
        mor.tenant.tenantUserGroup.delete()
            .where { it.id match id }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .exec().apply {
            if (this == 0) {
                request.logMsg = "删除组失败"
                return JsonResult.error("删除失败")
            }
            //删除组员关联
            mor.tenant.tenantUser.query().where { it.groups.id match id }.toList().apply {
                this.forEach {
                    it.groups.removeAll { it.id == id }
                    mor.tenant.tenantUser.updateWithEntity(it).execUpdate()
                }
            }

            mor.tenant.tenantAppAuthResourceInfo.query().where { it.type match AuthTypeEnum.Group }
                .where { it.target.id match id }.toList().apply {
                    this.forEach {
                        mor.tenant.tenantAppAuthResourceInfo.deleteById(it.id).exec()
                    }
                }


            return JsonResult()
        }
    }
    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.Tenant, "删除组内单个人员")
    @ApiOperation("删除组内单个人员")
    @PostMapping("/deleteGroupUser")
    fun deleteGroupUser(
        @Require id: String,
        @Require groupId: String,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "删除组内单个人员"
        mor.tenant.tenantUserGroup.queryById(groupId).where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }.toEntity() ?: return JsonResult.error("找不到组数据")
        mor.tenant.tenantUser.queryById(id).where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }.toEntity() ?:return JsonResult.error("找不到人员数据")

        mor.tenant.tenantUser.update()
            .where { it.groups.id match groupId }
            .where { it.id match id }
            .pull({ it.groups }, MongoColumnName("id") match groupId)
            .exec()

        return JsonResult()
    }

    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.UserGrop, "用户组")
    @ApiOperation("批量删除1")
    @PostMapping("/delete/batch")
    fun deleteBatch(
        ids: List<String>,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg="批量删除用户组{${ids.map { it }}}"
        mor.tenant.tenantUserGroup.query().where { it.id match_in ids }.exists().apply {
            if (!this) {
                return JsonResult.error("找不到数据")
            }
        }

        mor.tenant.tenantUserGroup.delete()
            .where { it.id match_in ids }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .exec()
            .apply {
            if (this == 0) {
                return JsonResult.error("删除失败")
            }
            //删除组员关联
            mor.tenant.tenantUser.query().where { it.groups.id match_in ids }.toList().apply {
                this.forEach {
                    it.groups.removeAll { it.id in ids }
                    mor.tenant.tenantUser.updateWithEntity(it).execUpdate()
                }
            }

            mor.tenant.tenantAppAuthResourceInfo.query().where { it.type match AuthTypeEnum.Group }
                .where { it.target.id match_in ids }.toList().apply {
                    this.forEach {
                        mor.tenant.tenantAppAuthResourceInfo.deleteById(it.id).exec()
                    }
                }


            }
        return JsonResult()
    }


    @ApiOperation("根据租户ID、用户ID查分组列表")
    @PostMapping("/findByUserId")
    fun findByUserId(
        userId: String,
        tenantId: String,
        request: HttpServletRequest
    ): ApiResult<List<IdName>> {
        val uId = userId.AsString(request.UserId)
        val tId = tenantId.AsString(request.getHeader("tenant-id") ?: "")

        val groups: MutableList<IdName> = mor.tenant.tenantUser.query()
            .where { it.id match uId }
            .where { it.tenant.id match tId }
            .toEntity()?.groups ?: mutableListOf()

        return ApiResult.of(groups)
    }
    class UserGroupAndUsersVo{
        var name: String = ""
        var id:String=""
        var users:MutableList<IdName> = mutableListOf<IdName>()

    }
    @ApiOperation("根据租户获取用户组以及用户组内的人")
    @PostMapping("/getUserGroupAndUsers")
    fun getUserGroupAndUsers(
        request: HttpServletRequest
    ): ApiResult<List<UserGroupAndUsersVo>> {

        val tenant =request.LoginTenantAdminUser.tenant
        val groupIds= mutableListOf<String>()
        val groups=mor.tenant.tenantUserGroup.query()
            .where { it.tenant.id match tenant.id }
            .toList(UserGroupAndUsersVo::class.java)
            .apply {
                this.map {
                    groupIds.add(it.id)
                }
            }
        val users=mor.tenant.tenantUser.query()
            .where { it.tenant.id match tenant.id }
            .where { it.groups.id match_in groupIds }
            .toList()
        groups.forEach{
                group->
            group.users.clear()
            users.forEach {
                    user->
                val finalUser=user.groups.filter { it.id == group.id }
                if(finalUser.isNotEmpty()) group.users.add(user.ConvertJson(IdName::class.java))
            }
        }
        return ApiResult.of(groups)
    }

    @ApiOperation("获取用户组内的人")
    @PostMapping("/getUsersByGroups")
    fun getUsersByGroups(
        @Require groupIds: String,
        request: HttpServletRequest
    ): ApiResult<List<UserConnectVo>> {
        val tenantId = request.LoginTenantAdminUser.tenant.id
        val ids=groupIds.split(",")
        if(ids.size==0){
            return ApiResult.error("用户组不能为空")
        }
        val userIds: MutableList<UserConnectVo> = mutableListOf();
        mor.tenant.tenantUser.query()
            .where { it.groups.id match_in ids }
            .where { it.tenant.id match tenantId }
            .toList()
            .apply {
                this.forEach {
                    var vo= UserConnectVo()
                    vo.name=it.name
                    vo.email=it.email
                    vo.userId=it.id
                    vo.phone=it.mobile
                    userIds.add(vo)
                }
            }
        return ApiResult.of(userIds)
    }

    class UserConnectVo() {
        var userId = ""
        var name=""
        var phone=""
        var email=""
    }
}