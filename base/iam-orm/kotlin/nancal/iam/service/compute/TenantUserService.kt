package nancal.iam.service.compute

import nbcp.comm.*
import nbcp.db.CodeName
import nbcp.db.IdName
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.mongo.extend.getParentDepts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class TenantUserService {


    @Autowired
    lateinit var tenantAppRoleAuthResourceService: TenantAppRoleAuthResourceService;


    @Autowired
    lateinit var tenantAppDeptAuthResourceService: TenantAppDeptAuthResourceService


    @Autowired
    lateinit var tenantAppUserAuthResourceService: TenantAppUserAuthResourceService;


    @Autowired
    lateinit var tenantAppUserGroupAuthResourceService: TenantAppUserGroupAuthResourceService

    fun getEnableApp(userId: String, appCode: String ) : ListResult<CodeName> {
        val user = mor.tenant.tenantUser.queryById(userId).toEntity().must().elseThrow { "找不到用户" }
        val tenant = mor.tenant.tenant.queryById(user.tenant.id).toEntity().must().elseThrow { "找不到用户租户" }
        val list = mutableListOf<CodeName>();
        val tenantApps = mor.tenant.tenantApplication.query()
            .where { it.tenant.id match tenant.id }
            .where { it.appCode match  appCode }
            .where { it.isOpen match  true }
            .where { it.enabled match  false }
            .toList()
        val enableApps = tenantApps.map { CodeName(it.appCode, it.name) }
        list.addAll(enableApps)
        return ListResult.of(list)
    }

    /**
     * 获取我的应用Id，从组织 ， 用户组， 用户 里找。
     */
    fun getMyApps(userId: String): ListResult<CodeName> {
        val user = mor.tenant.tenantUser.queryById(userId).toEntity().must().elseThrow { "找不到用户" }
        val tenant = mor.tenant.tenant.queryById(user.tenant.id).toEntity().must().elseThrow { "找不到用户租户" }

/*        val depts = mor.tenant.tenantDepartmentInfo.query()
            .where { it.id match_in user.depts.map { it.id } }
            .toList()*/

        //获取部门
        val depts = mor.tenant.tenantDepartmentInfo.getParentDepts(tenant.id, user.depts.map { it.id }, true)

        val groups = mor.tenant.tenantUserGroup.query()
            .where { it.id match_in user.groups.map { it.id } }
            .toList();

        val list = mutableListOf<CodeName>();

        /*
        三态：
            全部 == enabled && isOpen
            部分 == enabled && !isOpen
            禁用 == !enabled
         */

        val tenantApps = mor.tenant.tenantApplication.query()
            .where { it.tenant.id match tenant.id }
            .toList()

        val isOpens = tenantApps.filter { it.enabled && it.isOpen }.map { CodeName(it.appCode, it.name!!) }
        list.addAll(isOpens)

        val part = mutableListOf<CodeName>();
        depts.forEach { dept ->
            part.addAll(dept.allowApps)
        }
        depts.forEach { dept ->
            part.removeAll { app -> dept.denyApps.any { it.code == app.code } }
        }

        groups.forEach { group ->
            part.addAll(group.allowApps)
        }
        groups.forEach { group ->
            part.removeAll { app -> group.denyApps.any { it.code == app.code } }
        }

        part.addAll(user.allowApps);
        part.removeAll { app -> user.denyApps.any { it.code == app.code } }

        //部分应用，走过滤
        val partAllApps = tenantApps.filter { it.enabled && !it.isOpen }.map { it.appCode }
        list.addAll(part.filter { it.code.IsIn(partAllApps) })

        return ListResult.of(list.distinctBy { it.code }.toList())
    }


    /**
     * 获取我的角色： 从 组织， 用户组，用户里找。
     */
    fun getMyRoles(userId: String, appCode: String): ListResult<IdName> {
        var user = mor.tenant.tenantUser.queryById(userId).toEntity().must().elseThrow { "找不到用户" }

        //从我的应用中过滤。
        var myApps = getMyApps(userId);
        if (myApps.data.map { it.code }.contains(appCode) == false) {
            return ListResult()
        }

        var depts = mor.tenant.tenantDepartmentInfo.query()
            .where { it.id match_in user.depts.map { it.id } }
            .toList()

        var groups = mor.tenant.tenantUserGroup.query()
            .where { it.id match_in user.groups.map { it.id } }
            .toList();

        var list = mutableListOf<IdName>();

        depts.forEach { dept ->
            list.addAll(dept.roles)
        }

        groups.forEach { group ->
            list.addAll(group.roles)
        }

        list.addAll(user.roles);

        list = list.distinctBy { it.id }.toMutableList();

        //角色在应用中再过滤。
        var appRoles = mor.tenant.tenantAppRole.query()
            .where { it.appInfo.code match appCode }
            .select { it.id }
            .toList(String::class.java)

        list = list.filter { it.id.IsIn(appRoles) }.toMutableList()

        return ListResult.of(list)
    }



    /**
     * 获取我的角色： 从 组织， 用户组，用户里找。
     */
    fun getMyRolesV3(userId: String): ListResult<IdName> {
        var user = mor.tenant.tenantUser.queryById(userId).toEntity().must().elseThrow { "找不到用户" }

        var depts = mor.tenant.tenantDepartmentInfo.query()
            .where { it.id match_in user.depts.map { it.id } }
            .toList()

        var groups = mor.tenant.tenantUserGroup.query()
            .where { it.id match_in user.groups.map { it.id } }
            .toList();

        var list = mutableListOf<IdName>();

        depts.forEach { dept ->
            list.addAll(dept.roles)
        }

        groups.forEach { group ->
            list.addAll(group.roles)
        }

        list.addAll(user.roles);

        list = list.distinctBy { it.id }.toMutableList();

        //角色在应用中再过滤。
        var appRoles = mor.tenant.tenantAppRole.query()
            .select { it.id }
            .toList(String::class.java)

        list = list.filter { it.id.IsIn(appRoles) }.toMutableList()

        return ListResult.of(list)
    }


    class AuthResourceData(var code: String = "", var action: List<String> = listOf(), var type: ResourceTypeEnum? = null, var dataAccessLevel:AccessLevelEnum?=null )

    class AllowAndDenyRec(
        var allow: MutableSet<String> = mutableSetOf(),
        var deny: MutableSet<String> = mutableSetOf()
    ) {

        infix fun cover(other: AllowAndDenyRec): AllowAndDenyRec {
            var ret = AllowAndDenyRec();
            ret.allow = (this.allow - this.deny + other.allow - other.deny).toMutableSet()
            ret.deny = (this.deny - other.allow + other.deny).toMutableSet();
            return ret;
        }
    }

    /**
     * 仅返回的我资源，不包括 action,很实用。
     * 获取我的授权，从组织，角色，用户组，用户中。计算并返回具体的资源
     */
    fun getMyAuthResourcesNew(
        userId: String,
        appCode: String,
        resourceType: ResourceTypeEnum,
        policy: AuthResourceConflictPolicyEnum,
        type: AuthResourceTypeEnum,
    ): ListResult<String> {

        val userInfo = mor.tenant.tenantUser.queryById(userId).toEntity().must().elseThrow { "找不到用户" }

        //获取我的角色资源
        val myRolesResult = getMyRoles(userId, appCode);
        if (myRolesResult.msg.HasValue) {
            return ListResult.error(myRolesResult.msg)
        }

        // 根据dept 来查询
        val deptIds = userInfo.depts.stream().map { it.id }.collect(Collectors.toList())
        val deptRecList = tenantAppDeptAuthResourceService.getAuthResources(
//        val deptRecList = tenantAppDeptAuthResourceService.getAuthResourcesAction(
            AuthTypeEnum.Dept,
            deptIds,
            userInfo.tenant.id,
            appCode,
            resourceType,
            policy
        )

        //根据角色查询
        val roleRecList = tenantAppRoleAuthResourceService.getAuthResourcesNew(
//        val roleRecList = tenantAppRoleAuthResourceService.getAuthResourcesAction(
            AuthTypeEnum.Role,
            userInfo.tenant.id,
            appCode,
            resourceType,
            policy,
            myRolesResult.data.map { it.id })

        //获取用户所在组的 groupIds
        val groupIds = userInfo.groups.stream().map { it.id }.collect(Collectors.toList())

        // 根据用户组
        var userGroupRecList = AllowAndDenyRec()
        if (groupIds.isNotEmpty()) {
            userGroupRecList = tenantAppUserGroupAuthResourceService.getAuthResources(
//            userGroupRecList = tenantAppUserGroupAuthResourceService.getAuthResourcesAction(
                AuthTypeEnum.Group,
                groupIds,
                userInfo.tenant.id,
                appCode,
                resourceType,
                policy
            )
        }

        // 根据用户 来查询
        val userRecList = tenantAppUserAuthResourceService.getAuthResources(
//        val userRecList = tenantAppUserAuthResourceService.getAuthResourcesAction(
            AuthTypeEnum.People,
            userId,
            userInfo.tenant.id,
            appCode,
            resourceType,
            policy
        )

        //返回允许的结果
        if (type == AuthResourceTypeEnum.Allow){
            (deptRecList cover roleRecList cover userGroupRecList cover userRecList).allow
                .apply {

                    if(this.map { it }.contains("*")){
                        var allResource = mor.tenant.tenantResourceInfo.query()
                            .where { it.tenant.id match  userInfo.tenant.id}
                            .where { it.appInfo.code match appCode }
                            .where { it.type match resourceType }
                            .select{it.code}
                            .toList(String::class.java)

                        val deny = (deptRecList cover roleRecList cover userGroupRecList cover userRecList).deny
                        allResource.removeAll(deny)

                        println("最后允许的结果 = $allResource")
                        return ListResult.of(allResource)
                    }
                    if((deptRecList cover roleRecList cover userGroupRecList cover userRecList).deny.contains("*")){
                        this.clear()
                    }
                    println("最后允许的结果 = $this")
                    return ListResult.of(this)
                }

        }

        //返回拒绝的结果
        if (type == AuthResourceTypeEnum.Deny){
            (deptRecList cover roleRecList cover userGroupRecList cover userRecList).deny
                .apply {
                    if(this.map { it }.contains("*")){
                        this.clear()
                    }
                    println("最后拒绝的结果 = $this")
                    return ListResult.of(this);
                }
        }

        return ListResult()
    }

    /**
     * 仅返回的我资源，包括 action
     * 从组织，角色，用户组，用户 中计算
     */
    fun getMyAuthResourcesAction(
        userId: String,
        appCode: String,
        resourceType: ResourceTypeEnum,
        policy: AuthResourceConflictPolicyEnum,
        type: AuthResourceTypeEnum,
    ): ListResult<AuthResourceData> {
        val userInfo = mor.tenant.tenantUser.queryById(userId).toEntity().must().elseThrow { "找不到用户" }

        //获取我的角色
        val myRolesResult = getMyRoles(userId, appCode);
        if (myRolesResult.msg.HasValue) {
            return ListResult.error(myRolesResult.msg)
        }
        //根据角色查询
        val roleRecList = tenantAppRoleAuthResourceService.getAuthResourcesAction(
            AuthTypeEnum.Role,
            userInfo.tenant.id,
            appCode,
            resourceType,
            policy,
            myRolesResult.data.map { it.id })


        // 根据用户 来查询
        val userRecList = tenantAppUserAuthResourceService.getAuthResourcesAction(
            AuthTypeEnum.People,
            userId,
            userInfo.tenant.id,
            appCode,
            resourceType,
            policy
        )


        //获取用户所在组的 groupIds
        val groupIds = userInfo.groups.stream().map { it.id }.collect(Collectors.toList())

        // 根据用户组
        var userGroupRecList = AllowAndDenyRec()
        if (groupIds.isNotEmpty()) {
            userGroupRecList = tenantAppUserGroupAuthResourceService.getAuthResourcesAction(
                AuthTypeEnum.Group,
                groupIds,
                userInfo.tenant.id,
                appCode,
                resourceType,
                policy
            )
        }


        // 根据dept 来查询
        val deptIds = userInfo.depts.stream().map { it.id }.collect(Collectors.toList())
        val deptRecList = tenantAppDeptAuthResourceService.getAuthResourcesAction(
            AuthTypeEnum.Dept,
            deptIds,
            userInfo.tenant.id,
            appCode,
            resourceType,
            policy
        )

        //合并结果
        val allow = (deptRecList cover roleRecList cover userGroupRecList cover userRecList).allow


        //返回结果处理
        val resAct = mutableMapOf<String, MutableList<String>>()
        allow.forEach {
            val split = it.split("&&")
            if (resAct.get(split[0]) == null){
                resAct.put(split[0], mutableListOf(split[1]))
            }else{
                resAct.get(split[0])!!.add(split[1])
            }
        }

        var result = resAct.map { AuthResourceData(it.key, it.value) }

        var allResource = mor.tenant.tenantResourceInfo(userInfo.tenant.id).query()
            .where { it.appInfo.code match appCode }
            .where { it.type match resourceType }
            .toList()

        val collect = result.stream().map { it.code }.collect(Collectors.toList())
        if(collect.contains("*")){

            val blankResult = mutableListOf<AuthResourceData>()


            mor.tenant.tenantResourceInfo.query()
                .where { it.appInfo.code match appCode }
                .where { it.type match resourceType }
                .where { it.tenant.id match userInfo.tenant.id }
                .toList()
                .apply {
                    this.forEach {
                        blankResult.add(AuthResourceData(it.code,it.action,it.type,it.dataAccessLevel))
                    }
                }

            var resultStringArray= mutableListOf<String>()

            (deptRecList cover roleRecList cover userGroupRecList cover userRecList).deny
                .forEach{
                    resultStringArray.add( it.split("&&").get(0))
                }


            var tempblankResult  = mutableListOf<AuthResourceData>()

            blankResult.forEach {
                if(resultStringArray.contains(it.code)){
                    tempblankResult.add(it)
                }
            }

            result =  blankResult.filterNot { tempblankResult.map { it.code }.contains(it.code) }

        }else {
            var first: ResourceBaseInfo
            result.forEach {

                first = allResource.filter { all -> it.code == all.code }.first()
                it.type = first.type
                it.dataAccessLevel=first.dataAccessLevel
                if (it.action.contains("*")){
                    it.action = first.action
                }
            }
        }
        return ListResult.of(result)
    }


    /**
     * 旧版  -  仅返回的我资源，不包括 action,很实用。
     */
    fun getMyAuthResources(
        userId: String,
        appCode: String,
        resourceType: ResourceTypeEnum,
        policy: AuthResourceConflictPolicyEnum,
    ): ListResult<String> {

        val userInfo = mor.tenant.tenantUser.queryById(userId).toEntity().must().elseThrow { "找不到用户" }

        //获取我的角色资源
        val myRolesResult = getMyRoles(userId, appCode);
        if (myRolesResult.msg.HasValue) {
            return ListResult.error(myRolesResult.msg)
        }

        val list = tenantAppRoleAuthResourceService.getAuthResources(
            userInfo.tenant.id,
            appCode,
            resourceType,
            policy,
            myRolesResult.data.map { it.id })


        return ListResult.of(list)
    }

    fun getMyAuthResourcesNewV3(
        userId: String,
        resourceType: ResourceTypeEnum,
        policy: AuthResourceConflictPolicyEnum,
        type: AuthResourceTypeEnum,
    ): ListResult<String> {

        val userInfo = mor.tenant.tenantUser.queryById(userId).toEntity().must().elseThrow { "找不到用户" }

        //获取我的角色资源
        val myRolesResult = getMyRolesV3(userId);

        // 根据dept 来查询
        val deptIds = userInfo.depts.stream().map { it.id }.collect(Collectors.toList())
        val deptRecList = tenantAppDeptAuthResourceService.getAuthResourcesV3(
//        val deptRecList = tenantAppDeptAuthResourceService.getAuthResourcesAction(
            AuthTypeEnum.Dept,
            deptIds,
            userInfo.tenant.id,
            resourceType,
            policy
        )

        //根据角色查询
        val roleRecList = tenantAppRoleAuthResourceService.getAuthResourcesNewV3(
//        val roleRecList = tenantAppRoleAuthResourceService.getAuthResourcesAction(
            AuthTypeEnum.Role,
            userInfo.tenant.id,
            resourceType,
            policy,
            myRolesResult.data.map { it.id })

        //获取用户所在组的 groupIds
        val groupIds = userInfo.groups.stream().map { it.id }.collect(Collectors.toList())

        // 根据用户组
        var userGroupRecList = AllowAndDenyRec()
        if (groupIds.isNotEmpty()) {
            userGroupRecList = tenantAppUserGroupAuthResourceService.getAuthResourcesV3(
//            userGroupRecList = tenantAppUserGroupAuthResourceService.getAuthResourcesAction(
                AuthTypeEnum.Group,
                groupIds,
                userInfo.tenant.id,
                resourceType,
                policy
            )
        }

        // 根据用户 来查询
        val userRecList = tenantAppUserAuthResourceService.getAuthResourcesV3(
//        val userRecList = tenantAppUserAuthResourceService.getAuthResourcesAction(
            AuthTypeEnum.People,
            userId,
            userInfo.tenant.id,
            resourceType,
            policy
        )

        //返回允许的结果
        if (type == AuthResourceTypeEnum.Allow){
            (deptRecList cover roleRecList cover userGroupRecList cover userRecList).allow
                .apply {

                    if(this.map { it }.contains("*")){
                        var allResource = mor.tenant.tenantResourceInfo.query()
                            .where { it.tenant.id match  userInfo.tenant.id}
                            .where { it.type match resourceType }
                            .select{it.code}
                            .toList(String::class.java)

                        val deny = (deptRecList cover roleRecList cover userGroupRecList cover userRecList).deny
                        allResource.removeAll(deny)

                        println("最后允许的结果 = $allResource")
                        return ListResult.of(allResource)
                    }
                    if((deptRecList cover roleRecList cover userGroupRecList cover userRecList).deny.contains("*")){
                        this.clear()
                    }
                    println("最后允许的结果 = $this")
                    return ListResult.of(this)
                }

        }

        //返回拒绝的结果
        if (type == AuthResourceTypeEnum.Deny){
            (deptRecList cover roleRecList cover userGroupRecList cover userRecList).deny
                .apply {
                    if(this.map { it }.contains("*")){
                        this.clear()
                    }
                    println("最后拒绝的结果 = $this")
                    return ListResult.of(this);
                }
        }

        return ListResult()
    }


    fun getMyAuthResourcesActionOfRole(
        userId: String,
        appCode: String,
        roleCode: String,
        resourceType: ResourceTypeEnum,
        policy: AuthResourceConflictPolicyEnum,
        type: AuthResourceTypeEnum,
    ): ListResult<AuthResourceData> {
        val userInfo = mor.tenant.tenantUser.queryById(userId).toEntity().must().elseThrow { "找不到用户" }

       var roleEntity =  mor.tenant.tenantAppRole.query()
            .where { it.appInfo.code match appCode }
            .where { it.name match roleCode }
            .toEntity()
           .must().elseThrow { "找不到角色" }


        var roleIds = mutableListOf<String>()

        roleIds.add(roleEntity.id)

        //根据角色查询
        val roleRecList = tenantAppRoleAuthResourceService.getAuthResourcesAction(
            AuthTypeEnum.Role,
            userInfo.tenant.id,
            appCode,
            resourceType,
            policy,
            roleIds)

        //合并结果
        val allow = roleRecList.allow


        //返回结果处理
        val resAct = mutableMapOf<String, MutableList<String>>()
        allow.forEach {
            val split = it.split("&&")
            if (resAct.get(split[0]) == null){
                resAct.put(split[0], mutableListOf(split[1]))
            }else{
                resAct.get(split[0])!!.add(split[1])
            }
        }

        var result = resAct.map { AuthResourceData(it.key, it.value) }

        var allResource = mor.tenant.tenantResourceInfo(userInfo.tenant.id).query()
            .where { it.appInfo.code match appCode }
            .where { it.type match resourceType }
            .toList()

        val collect = result.stream().map { it.code }.collect(Collectors.toList())
        if(collect.contains("*")){

            val blankResult = mutableListOf<AuthResourceData>()


            mor.tenant.tenantResourceInfo.query()
                .where { it.appInfo.code match appCode }
                .where { it.type match resourceType }
                .where { it.tenant.id match userInfo.tenant.id }
                .toList()
                .apply {
                    this.forEach {
                        blankResult.add(AuthResourceData(it.code,it.action,it.type,it.dataAccessLevel))
                    }
                }

            var resultStringArray= mutableListOf<String>()

            roleRecList.deny
                .forEach{
                    resultStringArray.add( it.split("&&").get(0))
                }


            var tempblankResult  = mutableListOf<AuthResourceData>()

            blankResult.forEach {
                if(resultStringArray.contains(it.code)){
                    tempblankResult.add(it)
                }
            }

            result =  blankResult.filterNot { tempblankResult.map { it.code }.contains(it.code) }

        }else {
            var first: ResourceBaseInfo
            result.forEach {

                first = allResource.filter { all -> it.code == all.code }.first()
                it.type = first.type
                it.dataAccessLevel=first.dataAccessLevel
                if (it.action.contains("*")){
                    it.action = first.action
                }
            }
        }
        return ListResult.of(result)
    }

}
