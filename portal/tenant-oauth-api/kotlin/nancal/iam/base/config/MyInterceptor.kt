package nancal.iam.base.config


import nbcp.base.mvc.*
import nbcp.web.*
import nbcp.comm.*
import org.springframework.beans.factory.annotation.Autowired
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
        if (response.IsOctetContent) {
            return true;
        }

        if (handler is HandlerMethod == false) {
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

        if (beanType.name == "springfox.documentation.swagger.web.ApiResourceController") {
            return true;
        }


        var openActionValue =
            beanType.annotations.firstOrNull { OpenAction::class.java.isAssignableFrom(it.javaClass) } as OpenAction?

        if (openActionValue == null && request.LoginUser.id.isEmpty()) {
            response.status = 401;
            if (request.findParameterStringValue("iniframe").AsBoolean()) {
                response.WriteJsonRawValue("{\"code\":-1,\"msg\":\"您需要登录\"}".ToJson())
            } else {
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