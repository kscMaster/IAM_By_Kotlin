package nancal.iam.base.config

import nancal.iam.comm.*
import nbcp.comm.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.match
import nancal.iam.db.mongo.mor
import nbcp.base.mvc.*
import nbcp.web.*
import nbcp.db.mongo.query
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class AuditerAdminInterceptor : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        if (response.status in 400..600) {
            return true;
        }

        if (response.IsOctetContent) {
            return true;
        }
        if (handler !is HandlerMethod) {
            return true;
        }

        val beanType = handler.beanType;

        if (beanType.name == "springfox.documentation.swagger.web.ApiResourceController") {
            return true;
        }

        val openActionValue =
            beanType.annotations.firstOrNull { OpenAction::class.java.isAssignableFrom(it.javaClass) } as OpenAction?

        if (openActionValue == null) {

            val loginUser = try {
                request.LoginTenantAdminUser
            } catch (e: Exception) {
                return true
            }

            mor.tenant.tenantUser.query()
                .where { it.id match loginUser.id }.toEntity()
                ?.apply {
                    if(this ==null){
                        response.status = 401;
                        response.parentAlert("token用户不存在")
                        return false
                    }
//                    if(this.adminType == TenantAdminTypeEnum.Auditor){
//                        var uris = request.requestURI.toString()
//                        if(uris != "/tenant/audit-log/list"){
//                            response.status = 401;
//                            response.WriteJsonRawValue(JsonResult.error("权限不足，审计员只能查看审计日志").ToJson())
//                            return false
//                        }
//                    }
                }
        }


        return true
    }


}