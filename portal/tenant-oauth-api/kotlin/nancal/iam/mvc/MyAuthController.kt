package nancal.iam.mvc

import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.TenantUser
import nancal.iam.db.mongo.entity.TenantUserFieldExtend
import nancal.iam.db.redis.rer
import nancal.iam.service.OAuthTenantUserService
import nancal.iam.service.compute.TenantUserService
import nbcp.comm.*
import nbcp.db.CodeName
import nbcp.db.IdName
import nbcp.db.mongo.*
import nbcp.db.mongo.entity.BasicUser
import nbcp.utils.CodeUtil
import nbcp.web.LoginUser
import nbcp.web.userAuthenticationService
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

/**
 * 获取我的授权
 */
@RestController
class MyAuthController {

    @PostMapping("/oauth/fresh-token")
    fun freshToken(
        freshToken: String,
        request: HttpServletRequest,
    ): ApiResult<LoginOpenController.TokenInfoData> {

        val freshTokenData = rer.sys.freshToken(freshToken).get() ?: return ApiResult.error("找不到freshToken");

        val userInfo = rer.sys.oauthToken(freshTokenData.token).get() ?: return ApiResult.error("token超时")

        userInfo.token = CodeUtil.getCode();
        val tokenTimeoutSeconds = request.userAuthenticationService.saveLoginUserInfo(request, userInfo)

        val tokenInfo =
            LoginOpenController.TokenInfoData(userInfo.token, freshToken, tokenTimeoutSeconds.AsLong(0))
        //存
        return ApiResult.of(tokenInfo);
    }

    class BasicData : TenantUser(){
        var fieldExtend : MutableList<TenantUserFieldExtend> = mutableListOf()
    }

    @Autowired
    lateinit var tenantUserService: TenantUserService

    /**
     * 当前用户的应用
     */
    @PostMapping("/oauth/my-apps")
    fun myAuthApp(
        token: String,
        request: HttpServletRequest,
    ): ListResult<CodeName> {
        val loginUser = rer.sys.oauthToken(token).get().must().elseThrow { "异常" }
        return tenantUserService.getMyApps(loginUser.id)
    }

    @Resource
    lateinit var authService: TenantUserService

    @GetMapping("/oauth/user-info")
    fun getUserInfo(
        token: String,
        appCode: String,
        request: HttpServletRequest,
    ): ApiResult<BasicData>{
        var loginUser = rer.sys.oauthToken(token).get()
        var userId = ""
        if (loginUser == null) {
            userId = request.getHeader("user-id")
            if (userId.isBlank()) {
                return ApiResult.error("请求头需要user-id")
            }
        } else {
            userId = loginUser.id
        }
        val tenantId = request.LoginUser.organization.id
        var toList: MutableList<TenantUserFieldExtend> = mor.tenant.tenantUserFieldExtend.query()
            .apply {
                this.where { it.tenant.id match tenantId }
            }.orderByDesc { it.createAt }
            .toList(TenantUserFieldExtend::class.java)

        val roles = mutableSetOf<IdName>()
        //用户
        val tenantUser = mor.tenant.tenantUser.queryById(userId).toEntity() ?: return ApiResult.error("找不到用户信息")
        roles.addAll(tenantUser.roles)
        //用户组
        val groups = mor.tenant.tenantUserGroup.query()
            .where { group -> group.id match_in tenantUser.groups.map { it.id } }
            .toList()
        groups.map {
            roles.addAll(it.roles)
        }
        //部门
        val depts = mor.tenant.tenantDepartmentInfo.query()
            .where { dept -> dept.id match_in tenantUser.depts.map { it.id } }
            .toList()
        depts.map {
            roles.addAll(it.roles)
        }

        //根据应用过滤角色
        if (appCode.HasValue) {
            mor.tenant.tenantAppRole.query()
                .where { it.id match_in roles.map { it.id } }
                .where { it.appInfo.code match appCode }
                .toList(IdName::class.java)
                .apply {
                    tenantUser.roles = this
                }
        }

        val res = BasicData()
        BeanUtils.copyProperties(tenantUser, res)
        res.fieldExtend = toList
        return ApiResult.of(res)
    }

    /**
     * 当前用户所有授权资源
     */
    @PostMapping("/oauth/my-all-resources")
    fun myAuthResourcesFull(
        @Require appCode: String,
        @Require resourceType: ResourceTypeEnum,
        request: HttpServletRequest,
    ): ListResult<String> {
        TODO()
    }

    fun appDisabled(appCode: String, request: HttpServletRequest): Boolean {
        val userInfo = mor.tenant.tenantUser.queryById(request.LoginUser.id).toEntity().must().elseThrow { "找不到用户" }
        mor.tenant.tenantApplication.query()
            .where { it.appCode match appCode }
            .where { it.tenant.id match userInfo.tenant.id }
            .toEntity()
            .apply {
                if (this == null || !this.enabled) { // 不存在&&禁用
                    return false
                }
            }
        return true
    }

    /**
     * 旧版 - 获取我的授权资源，会计算。
     *       只支持角色
     */
    @PostMapping("/oauth/my-auth-resources")
    fun myAuthResources(
        @Require appCode: String,
        @Require resourceType: ResourceTypeEnum,
        policy: AuthResourceConflictPolicyEnum?,
        request: HttpServletRequest,
    ): ListResult<String> {
        if (!appDisabled(appCode, request)) {
            return ListResult.of(mutableListOf())
        }
        return authService.getMyAuthResources(
            request.LoginUser.id,
            appCode,
            resourceType,
            policy ?: AuthResourceConflictPolicyEnum.Deny
        )
    }

    /**
     * new - 获取我的授权资源，我允许的资源。
     */
    @PostMapping("/oauth/my-allow-resources")
    fun myAuthResourcesV2(
        @Require appCode: String,
        @Require resourceType: ResourceTypeEnum,
        @Require policy: AuthResourceConflictPolicyEnum,
        request: HttpServletRequest,
    ): ListResult<String> {
        if (!appDisabled(appCode, request)) {
            return ListResult.of(mutableListOf())
        }
        return authService.getMyAuthResourcesNew(
            request.LoginUser.id,
            appCode,
            resourceType,
            policy,
            AuthResourceTypeEnum.Allow
        )
    }


    /**
     * 我禁止的资源
     */
    @PostMapping("/oauth/my-deny-resources")
    fun myAuthResourcesf(
        @Require appCode: String,
        @Require resourceType: ResourceTypeEnum,
        @Require policy: AuthResourceConflictPolicyEnum,
        request: HttpServletRequest,
    ): ListResult<String> {
        if (!appDisabled(appCode, request)) {
            return ListResult.of(mutableListOf())
        }
        val tenantAppResource = mor.tenant.tenantResourceInfo.query()
            .select { it.code }
            .where { it.appInfo.code match appCode }
            .where { it.tenant.id match request.LoginUser.organization.id }
            .where { it.type match resourceType }
            .toList(String::class.java)

        val myAuthResourcesNew = authService.getMyAuthResourcesNew(
            request.LoginUser.id,
            appCode,
            resourceType,
            policy,
            AuthResourceTypeEnum.Allow
        )

        return ListResult.of((tenantAppResource - myAuthResourcesNew.data).toMutableSet())
    }


    /**
     * 获取我的授权资源，我允许的资源，包含action。
     */
    @PostMapping("/oauth/my-allow-resources-action")
    fun myAuthResourcesAction(
        @Require appCode: String,
        @Require resourceType: ResourceTypeEnum,
        @Require policy: AuthResourceConflictPolicyEnum,
        request: HttpServletRequest,
    ): ListResult<AuthResourceDataTemp> {
        if (!appDisabled(appCode, request)) {
            return ListResult.of(mutableListOf())
        }
        val myAuthResourcesAction = authService.getMyAuthResourcesAction(
            request.LoginUser.id,
            appCode,
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

    open class AuthResourceDataTemp {
        var code: String = ""
        var action: List<String> = listOf()
        var type: ResourceTypeEnum? = null
        var dataAccessLevel: AccessLevelEnum? = null
        var children: MutableList<AuthResourceDataTemp> = mutableListOf()
        var simpleCode: String = ""
    }


    @PostMapping("/oauth/my-allow-resourcesv3")
    fun myAuthResourcesV3(
        @Require resourceType: ResourceTypeEnum,
        @Require policy: AuthResourceConflictPolicyEnum,
        request: HttpServletRequest,
    ): ListResult<String> {
        return authService.getMyAuthResourcesNewV3(
            request.LoginUser.id,
            resourceType,
            policy,
            AuthResourceTypeEnum.Allow
        )
    }
}