package nancal.iam.mvc.iam

import io.swagger.annotations.*
import nancal.iam.annotation.CheckTenantAppStatus
import org.springframework.data.mongodb.core.query.*
import org.springframework.web.bind.annotation.*
import nancal.iam.base.extend.*
import nbcp.comm.*
import nbcp.db.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.redis.rer
import nancal.iam.service.TenantAdminUserService
import nancal.iam.web.open.Open_Controller
import nbcp.base.mvc.*
import nbcp.utils.CodeUtil
import nbcp.web.*
import org.springframework.beans.factory.annotation.Autowired
import javax.servlet.http.*
import java.time.*

/**
 * Created by CodeGenerator at 2021-11-26 11:42:27
 */
@RestController
class MyAuthController {
    @Autowired
    lateinit var tenantAdminUserService: TenantAdminUserService

    /**
     * 获取的的授权资源,会计算。
     */
    @PostMapping("/sys/my-menus")
    fun myAuthResources(
        request: HttpServletRequest
    ): ListResult<String> {
        return tenantAdminUserService.getMyMenus(request.UserId);
    }


    @PostMapping("/fresh-token")
    fun freshToken(
        freshToken: String,
        request: HttpServletRequest
    ): ApiResult<Open_Controller.TokenInfoData> {

        val freshTokenData = rer.sys.freshToken(freshToken).get() ?: return ApiResult.error("找不到freshToken");

        val userInfo = rer.sys.oauthToken(freshTokenData.token).get() ?: return ApiResult.error("token超时")

        userInfo.token = CodeUtil.getCode();
        val tokenTimeoutSeconds = request.userAuthenticationService.saveLoginUserInfo(request, userInfo)

        val tokenInfo =
            Open_Controller.TokenInfoData(userInfo.token, freshToken, tokenTimeoutSeconds.AsLong(0))
        //存
        return ApiResult.of(tokenInfo);
    }
}