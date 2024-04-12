package nancal.iam.base.config

import nancal.iam.comm.*
import nbcp.comm.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.match
import nancal.iam.db.mongo.mor
import nancal.iam.service.compute.TenantUserService
import nbcp.base.mvc.*
import nbcp.web.*
import nbcp.db.mongo.query
import nbcp.db.mongo.queryById
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class ApiAuthInterceptor : HandlerInterceptor {

    @Resource
    lateinit var authService: TenantUserService

    @Value("\${spring.application.name}")
    var applicationName: String = ""


    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        val tenantUser = mor.tenant.tenantUser.queryById(request.LoginUser.id).toEntity()
        if (tenantUser != null) {
            if (tenantUser.adminType != TenantAdminTypeEnum.None){
                return true;
            }
        }
        val appCode = request.getHeader("fromApp")

        if(appCode==""||appCode==null){
            response.status = 409;
            response.WriteTextValue("header中需要fromApp参数")
            return false
        }


        val myAuthResourcesNew = authService.getMyAuthResourcesAction(
            request.LoginUser.id,
            appCode,
            ResourceTypeEnum.Api,
            AuthResourceConflictPolicyEnum.Latest,
            AuthResourceTypeEnum.Allow
        )
        if(myAuthResourcesNew.data.isEmpty()){
            response.status = 403;
            response.WriteTextValue("没有权限")
            return false
        }else {

            val allApi = mor.tenant.tenantResourceInfo.query()
                .where { it.tenant.id match request.LoginUser.organization.id }
                .where { it.appInfo.code match appCode }
                .where { it.code match_in myAuthResourcesNew.data.map { it.code } }
                .select("resource")
                .toList(String::class.java)

            var flag = false

            var requestURI = request.requestURI
            if(
                requestURI.contains("delete/")
                || requestURI.contains("detail/")
                || requestURI.contains("detailTemp/")
            ){
                requestURI = requestURI.substring(0,requestURI.lastIndexOf("/"))
            }

            allApi.forEach {
                if(it.contains(applicationName + requestURI)){
                    flag = true
                    return@forEach
                }
            }
            if(flag){
                return true
            }else {
                response.status = 403;
                response.WriteTextValue("没有权限")
                return false
            }
        }
    }


}