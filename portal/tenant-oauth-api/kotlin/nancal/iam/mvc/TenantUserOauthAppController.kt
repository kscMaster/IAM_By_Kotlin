package nancal.iam.mvc

import io.swagger.annotations.ApiOperation
import nbcp.comm.*
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.SecurityPolicy
import nbcp.db.mongo.*
import nancal.iam.db.redis.rer
import nancal.iam.service.compute.TenantUserService
import nbcp.web.userAuthenticationService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.AntPathMatcher
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@OpenAction
@RestController
class TenantUserOauthAppController {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    @Resource
    lateinit var authService: TenantUserService


    @Autowired
    private lateinit var tenantUserOpController: TenantUserOperationController

    var applicationName = "/mp-tenant-admin-api"

    @PostMapping("/oauth/shift-tenant")
    fun shiftTenant(
        token: String, tenantId: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): JsonResult {
        var loginUser = rer.sys.oauthToken(token).get()
        if (loginUser == null) {
            response.status = 401;
            return JsonResult.error("未登录!")
        }

        var userType = loginUser.system.FromJson<UserSystemTypeEnum>();
        if (userType != UserSystemTypeEnum.TenantUser) {
            throw RuntimeException("只能租户用户才能切换租户")
        }

        var loginName = loginUser.loginName;

        var tenantList = mor.tenant.tenantLoginUser.query()
            .whereOr({ it.loginName match loginName }, { it.mobile match loginName }, { it.email match loginName })
            .toList();

        if (tenantList.map { it.id }.contains(tenantId) == false) {
            return JsonResult.error("非法的租户！")
        }

        var firstLoginUser = tenantList.first { it.tenant.id == tenantId };
        var firstUser = mor.tenant.tenantUser.queryById(firstLoginUser.userId).toEntity();

        if (firstUser == null) {
            throw RuntimeException("找不到用户")
        }

        loginUser.id = firstUser.id;
        loginUser.name = firstUser.name;
        loginUser.organization = firstUser.tenant;
        loginUser.roles = firstUser.roles.map { it.id }
        loginUser.depts = firstUser.depts.map { it.id }
        loginUser.groups = firstUser.groups.map { it.id }

        request.userAuthenticationService.saveLoginUserInfo(request, loginUser);

        return JsonResult()
    }

    @PostMapping("/oauth/shift-tenant-app")
    fun shiftTenantApp(token: String, appCode: String, response: HttpServletResponse): JsonResult {
        TODO("目前业务没有这个需求")
    }


    @PostMapping("/oauth/getAuthResult")
    fun getAuthResult(
        @Require url: String,
        @Require userId: String,
        request: HttpServletRequest
    ): ApiResult<Boolean> {

//        var userId = request.getHeader("user-id")
//        if(!userId.HasValue){
//            return ApiResult.error("请求头中需要user-id")
//        }

        val tenantUser = mor.tenant.tenantUser.queryById(userId).toEntity()
        if (tenantUser  == null) {
            return ApiResult.of(false)
        }
        if(tenantUser.adminType != TenantAdminTypeEnum.None){
            return ApiResult.of(true)
        }

        val myAuthResourcesNew = authService.getMyAuthResourcesNewV3(
            userId,
            ResourceTypeEnum.Api,
            AuthResourceConflictPolicyEnum.Latest,
            AuthResourceTypeEnum.Allow
        )
        if(myAuthResourcesNew.data.isEmpty()){
            return ApiResult.of(false)
        }else {

            val allApi = mor.tenant.tenantResourceInfo.query()
                .where { it.tenant.id match tenantUser.tenant.id }
                .where { it.code match_in myAuthResourcesNew.data.map { it } }
                .select("resource")
                .toList(String::class.java)


            var ant = AntPathMatcher();
            var matched = allApi.firstOrNull { ant.match(it, url) }

//            allApi.forEach {
//                if(it.contains(applicationName + requestURI)){
//                    flag = true
//                    return@forEach
//                }
//            }
            if(matched!=null){
                return ApiResult.of(true)
            }else {
                return ApiResult.of(false)
            }
        }
    }




    class Securitys {
        var leastCharacters: Int = 0
        var leastLength: Int = 0
    }


    @ApiOperation("获取密码策略")
    @PostMapping("/oauth/detailSelfSetting")
    fun detailSelfSetting(
        userId : String,
        @Require   concat: String,
        request: HttpServletRequest
    ): ApiResult<SecurityPolicy> {

        var users = if(userId.HasValue){
            mor.tenant.tenantUser.query()
                .where { it.id match userId }
                .orderByDesc { it.createAt }
                .toList()
                .apply {
                    if (this.size < 1) {
                        return ApiResult.error("找不到用户")
                    }
                }
        }else {
            mor.tenant.tenantUser.query()
                .whereOr({ it.mobile match concat }, { it.email match concat }, { it.loginName match concat })
                .orderByDesc { it.createAt }
                .toList()
                .apply {
                    if (this.size < 1) {
                        return ApiResult.error("找不到用户")
                    }
                }
        }
        mor.tenant.tenant.query()
            .where { it.id match users.get(0).tenant.id }
            .toEntity().must().elseThrow { "找不到该租户" }

//        val pwdPolicy = tenantUserOpController.getTenantPwdPolicy(listOf(users.get(0).tenant.id) as List<String>)

        return ApiResult.of(tenantUserOpController.getTenantPwdPolicy(listOf(users.get(0).tenant.id) as List<String>).get(0).setting.selfSetting.securityPolicy)

    }


}
