package nancal.iam.web.open

//import nbcp.base.config.ActionDocBeanGather
import ch.qos.logback.classic.Logger
import io.swagger.annotations.*
import nancal.iam.annotation.BizLog
import nancal.iam.base.config.BizLogInterceptor
import nancal.iam.comm.LoginAdminUser
import nancal.iam.comm.logMsg

import nbcp.comm.*
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.AdminUser
import nbcp.db.mongo.*
import nancal.iam.db.mongo.extend.*
import nancal.iam.db.redis.rer
import nbcp.base.mvc.*
import nbcp.web.*
import nancal.iam.web.sys.LoginInfo_Controller
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import java.io.File
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

/**
 * Created by udi on 17-3-19.
 */
@RestController
@RequestMapping("/open")
@OpenAction
class Open_Controller {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    @Value("\${captcha.vip.open}")
    var captchaFlag: Boolean = false


    @BizLog(BizLogActionEnum.Logout, BizLogResourceEnum.Define, "Admin侧退出")
    @ApiOperation(value = "退出登录")
    @PostMapping("/logout")
    fun logout(token: String, request: HttpServletRequest): JsonResult {
        val loginUser = rer.sys.oauthToken(token).get() ?: return JsonResult()

        /*操作日志*/
        request.logMsg = "退出账户{${loginUser.loginName}}"
        BizLogInterceptor.loginAdminUser = request.LoginAdminUser

        request.userAuthenticationService.deleteToken(request)
        request.session.invalidate();

        return JsonResult()
    }

    @BizLog(BizLogActionEnum.Login, BizLogResourceEnum.Define, "Admin登录")
    @ApiOperation(value = "登录")
    @PostMapping("/login")
    fun login(
        loginName: String,
        password: String,
        validateCode: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
        session: HttpSession
    ): ApiResult<AdminUser> {
        /*日志相关*/
        request.logMsg = "登录账户{${loginName}}"

        if (validateCode.isEmpty()) {
            return ApiResult.error("请输入验证码！")
        }

        if (captchaFlag && validateCode.lowercase(Locale.getDefault()).equals("vip888")) {
            logger.info("vip通道开启了")
        } else if (validateCode != request.userAuthenticationService.getValidateCode(request.tokenValue)) {
            return ApiResult.error("验证码输入错误或已过期，请重试！")
        }

        request.userAuthenticationService.deleteToken(request)


        val loginResult = mor.admin.adminUser.doLogin(loginName, password, request.tokenValue);
        if (loginResult.msg.HasValue) {
            return ApiResult.error(loginResult.msg);
        }

        val result = loginInfo.getMine(request, response)

        /*日志相关*/
        BizLogInterceptor.loginAdminUser = result.data!!

        return result
    }

    @Autowired
    lateinit var loginInfo: LoginInfo_Controller

//    @ApiOperation(value = "获取登录者信息")
//    @PostMapping("/getLocations")
//    fun getLocations(session: HttpServletRequest): ListResult<IdName> {
//        var ret = ListResult<IdName>();
//
//        ret.data = mor.system.sysDictionary.query()
//                .where { it.group match SysDictionaryGroupEnum.LocationForSchool }
//                .select { it.name }
//                .toList(IdName::class.java)
//
//        return ret;
//    }

//    data class cn_cityModel(var c: Int, var n: String)

//    @PostMapping("/getChildCitys")
//    fun getChildCitys(code: Int, response: HttpServletResponse): JsonResult? {
//        var ret = JsonResult()
//        if (code == 0) {
//            return JsonResult.error("城市code不能为空")
//        }
//        if (code % 100 != 0) return ret;
//
//        var value = rer.sys.sysCityJson.get(code.toString());
//        if (value.HasValue) {
//            response.WriteJsonRawValue(value);
//            return null;
//        }
//
//
//        value = ApiResult(
//                data = mor.system.sysCity.query()
//                        .where(mor.system.sysCity.pcode match code)
//                        .toList(CodeName::class.java)
//                        .map { cn_cityModel(it.code, it.name) }
//        )
//                .ToJson();
//
//
//        rer.sys.sysCityJson.set(code.toString(), value);
//
//        response.WriteJsonRawValue(value);
//        return null;
//    }

    @Value("\${spring.application.name}")
    var applicationName: String = "";


    @GetMapping("/log")
    fun log(
        filter: List<String>?, not: List<String>?, tail: Boolean?,
        extCount: Int = 0, matchLines: Int, help: Boolean?,
        f: List<String>?, c: Int = 0, n: Int = 0,
        request: HttpServletRequest
    ): String {
        if (config.debug == false) {
            return "debug:${config.debug}"
        }


        var logFile = (logger as Logger).getLoggerFile("FILE-OUT");


        var log_text = File(logFile).FilterLines(
            n.AsInt(matchLines),
            c.AsInt(extCount),
            f ?: filter ?: emptyList(),
            not ?: emptyList(),
            tail
                ?: true
        ).joinToString("\n")

        var help_html = "";
        if (help == true) {
            help_html = """<pre>
    日志参数,优先使用简写参数：
        filter:f  表示过滤匹配条件，可以多个
        not:      表示过滤匹配不满足条件的行，可以多个
        tail:     表示是否从最底部显示，默认是 true
        extCount:c 表示如果有 filter , not 的话，返回匹配行的前后多少行。
        matchLines:n 表示返回的总行数
</pre>
<hr />"""
        }

        return """<!DOCTYPE html>
<html>
<head><title>${applicationName} - 日志</title></head>
<body>${help_html}<pre>${log_text}</pre></body>
</html>
"""
    }

}