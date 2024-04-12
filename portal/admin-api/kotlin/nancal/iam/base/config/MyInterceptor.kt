package nancal.iam.base.config


import nbcp.comm.*
import nbcp.base.mvc.*
import nbcp.web.*
import nbcp.web.*
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by yuxh on 2019/1/17
 */
@Configuration
class MyInterceptor : HandlerInterceptor {
    companion object {
//        private val openActionMap = mutableMapOf<String, Boolean>()
//        private val roleActionMap = mutableMapOf<String, RoleAction?>()
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (response.status >= 400 && response.status <= 600) {
            return true;
        }

        if (handler is HandlerMethod == false) {
            return true;
        }
        if (response.IsOctetContent) {
            return true;
        }

//        //如果 header 中有 token，则使用 token 登录。
//        var token = request.findParameterValue("token").AsString();
//        if (token.HasValue) {
//            request.LoginUser = LoginUserModel.loadFromToken(token) ?: LoginUserModel()
//        }
//        else if (request.session.id != null) {
//            request.LoginUser = LoginUserModel.loadFromToken(request.session.id) ?: LoginUserModel()
//        }

        var beanType = handler.beanType;

        //Swagger2.9.2
        if (beanType.name == "springfox.documentation.swagger.web.ApiResourceController") {
            return true;
        }

        //Swagger3.0.0
        if (beanType.name == "springfox.documentation.swagger2.web.Swagger2ControllerWebMvc" ||
            beanType.name == "springfox.documentation.oas.web.OpenApiControllerWebMvc"
        ) {
            return true;
        }

        if (beanType.annotations.any { it is OpenAction }) {
            return true;
        }
        if (beanType.annotations.any { it is AdminSysOpsAction }) {
            if (request.findParameterStringValue("admin-token") == config.adminToken)
                return true;
        }

        if (request.LoginUser.id.isEmpty()) {
            response.status = 401;
            if (request.findParameterStringValue("iniframe").AsBoolean()) {
                // return true;
                //TODO ######  暂时返回true，方便调试
                response.WriteJsonRawValue("{\"code\":-1,\"msg\":\"您需要登录\"}".ToJson())
            } else {
                // return true;
                //TODO #####  暂时返回true，方便调试
                response.WriteJsonRawValue("{\"code\":-1,\"msg\":\"您需要登录\"}".ToJson())
            }
            return false;
        }


        //检测 RoleAction
//        var roleAction =
//            beanType.annotations.firstOrNull { RoleAction::class.java.isAssignableFrom(it.javaClass) } as RoleAction?
//
//        if (roleAction != null) {
//            if (roleAction.roleNames.intersect(request.LoginUser.roles).any() == false) {
//                response.status = 403;
//                if (request.findParameterStringValue("iniframe").AsBoolean()) {
//                    response.parentAlert("缺少权限")
//                } else {
//                    response.WriteTextValue("缺少权限");
//                }
//                return false;
//            }
//        }

        //自定义URL
//        if (userPermission.allowAdminUserApi(
//                request.tokenValue,
//                request.LoginUser.roles,
//                request.requestURI
//            ) == false
//        ) {
//            response.status = 403;
//            if (request.findParameterStringValue("iniframe").AsBoolean()) {
//                response.parentAlert("缺少权限")
//            } else {
//                response.WriteTextValue("缺少权限");
//            }
//            return false;
//        }

        return super.preHandle(request, response, handler)
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        super.postHandle(request, response, handler, modelAndView)
    }
}