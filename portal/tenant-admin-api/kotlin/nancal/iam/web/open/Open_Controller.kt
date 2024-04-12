package nancal.iam.web.open

//import nbcp.base.config.ActionDocBeanGather

import com.nancal.cipher.SHA256Util
import io.swagger.annotations.ApiOperation
import nancal.iam.annotation.BizLog
import nancal.iam.base.config.BizLogInterceptor
import nancal.iam.client.MPClient
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.mongo.extend.CodeModel
import nancal.iam.db.mongo.extend.doLogin
import nancal.iam.db.mongo.extend.getLoginUsers
import nancal.iam.db.redis.OAuthCodeData
import nancal.iam.db.redis.rer
import nancal.iam.service.LoginPasswordService
import nancal.iam.util.PwdVerifyStrategy
import nancal.iam.util.ValidateUtils
import nancal.iam.web.sys.LoginInfo_Controller
import nbcp.comm.*
import nbcp.db.LoginUserModel
import nbcp.db.mongo.*
import nbcp.utils.TokenUtil
import nbcp.base.mvc.*
import nbcp.web.*
import nbcp.base.mvc.*
import nbcp.base.mvc.handler.AuthImageServlet
import nbcp.utils.CodeUtil
import nbcp.utils.Md5Util
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import javax.annotation.Resource
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

    @Resource
    lateinit var authImageServlet: AuthImageServlet

    @Autowired
    lateinit var loginInfo: LoginInfo_Controller

    @Autowired
    lateinit var loginPasswordService: LoginPasswordService

    @Autowired
    lateinit var mpClient: MPClient

    @Value("\${spring.application.name}")
    var applicationName: String = ""

    @Value("\${captcha.vip.open}")
    var captchaFlag: Boolean = false

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    @ApiOperation(value = "获取token")
    @PostMapping("/oauth/token")
    fun token(request: HttpServletRequest): ApiResult<String> {
        val token = request.tokenValue
        return ApiResult.of(token)
    }

    @ApiOperation(value = "获取验证码")
    @GetMapping("/oauth/validate-img")
    fun validateImg(request: HttpServletRequest, response: HttpServletResponse) {
        authImageServlet.doGet(request, response)
    }

    @BizLog(BizLogActionEnum.Logout, BizLogResourceEnum.Define, "租户侧退出")
    @ApiOperation(value = "退出登录")
    @PostMapping("/logout")
    fun logout(token: String, request: HttpServletRequest): JsonResult {
        if (token.isEmpty()) return JsonResult()

        var loginUser = rer.sys.oauthToken(token).get()
        if (loginUser == null) {
            return JsonResult()
        }

        /*操作日志*/
        request.logMsg = "退出账户{${loginUser.loginName}}"
        BizLogInterceptor.loginAdminUser = request.LoginTenantAdminUser

        request.userAuthenticationService.deleteToken(request)
        request.session.invalidate()

        return JsonResult()
    }


    @ApiOperation(value = "登录")
    @PostMapping("/login")
    fun login(
        loginName: String,
        password: String,
        mobile: String,
        validateCode: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
        session: HttpSession
    ): ApiResult<CodeModel> {
        if (validateCode.isEmpty()) {
            return ApiResult.error("请输入验证码！")
        }

        if (captchaFlag && validateCode.lowercase(Locale.getDefault()).equals("vip888")) {
            logger.info("vip通道开启了")
        }else if (mobile.isBlank() && validateCode != request.userAuthenticationService.getValidateCode(request.tokenValue)) {
            return ApiResult.error("验证码输入错误或已过期，请重试！")
        }
        request.userAuthenticationService.deleteToken(request)

        val loginResult = if (mobile.HasValue) {
            mobileLogin(mobile, validateCode, request.tokenValue)
        } else {
            mor.tenant.tenantUser.doLogin(loginName, password, request.tokenValue, false)
        }

        if (loginResult.msg.HasValue) {
            return ApiResult.error(loginResult.msg)
        }
        val uuid = UUID.randomUUID().toString().replace("-", "");

        val isUpdatePassword = mor.tenant.tenantSecretSet.query().where { it.tenant.id match loginResult.data?.tenantId }
            .select { it.setting.selfSetting.securityPolicy.firstLoginUpdatePassword }
            .toEntity(Boolean::class.java)

        if(loginResult.data?.isFirstLogin == true  && isUpdatePassword == true){

            if (mobile.HasValue) {
                rer.sys.smsCode(uuid).set(mobile)
            }else {
                rer.sys.smsCode(uuid).set(loginName)
            }
            loginResult.data!!.uuid = uuid
        }else {
            loginResult.data!!.isFirstLogin = false
        }

        if(loginResult.data!!.type == "oauthCode"){
            val get = rer.sys.oauthCode(loginResult.data!!.code).get()
            if (get != null) {
                loginPasswordService.passwordSend(get.userId,request.getHeader("lang").toString())
            }
        }
        return loginResult
    }

    /**
     * 手机验证码登录验证
     */
    private fun mobileLogin(
        mobile: String,
        validateCode: String,
        requestToken: String
    ): ApiResult<CodeModel> {
        //自己校验验证码
        val codeStatus = mpClient.codeStatus(MobileCodeModuleEnum.Login, mobile, validateCode)

        if (codeStatus.code != 0) {
            return ApiResult.error("验证码校验错误，请重试", 500)
        }

        return mor.tenant.tenantUser.doLogin(mobile, "", requestToken, true)
    }


    @GetMapping("/user-info")
    fun getUserInfo(
        token: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ApiResult<TenantUser> {
//        val loginUser = rer.sys.oauthToken.get(token) ?: return ApiResult.error("找不到token")
        return loginInfo.getMine(request, response)
    }

    @PostMapping("/code2tenants")
    fun code2tenants(loginCode: String, oauthToken: String): ListResult<Tenant> {
        if (!loginCode.HasValue && !oauthToken.HasValue) {
            return ListResult.error("loginCode和oauthToken不能同时为空")
        }

        var loginField = ""
        var loginName = ""

        if (loginCode.HasValue) {
            val loginCodeData = rer.sys.loginCode(loginCode).get() ?: return ListResult.error("非法的loginCode")
            loginField = loginCodeData.loginField
            loginName = loginCodeData.loginName
        }
        if (oauthToken.HasValue) {
            val loginUserModel = rer.sys.oauthToken(oauthToken).get() ?: return ListResult.error("非法的oauthToken")
            loginField = loginUserModel.loginField
            loginName = loginUserModel.loginName
        }

        val loginResult = mor.tenant.tenantUser.getLoginUsers(loginName, loginField)
        if (loginResult.msg.HasValue) {
            return ListResult.error(loginResult.msg)
        }
        val loginUsers = loginResult.data!!

        /* 查询有应用权限的并且是启用状态的所属租户 */
        return mor.tenant.tenant.query()
            .where { it.id match_in loginUsers.map { it.tenant.id } }
            .where { it.isLocked match false }
            .toListResult()
    }

    @PostMapping("/loginCode2oauthCode")
    fun loginCode2oauthCode(
        loginCode: String,
        tenantId: String,
        request: HttpServletRequest
    ): ApiResult<LoginCode2oauthCodeResult> {
        val loginCodeData = rer.sys.loginCode(loginCode).get() ?: return ApiResult.error("非法code")

        val loginResult = mor.tenant.tenantUser.getLoginUsers(loginCodeData.loginName, loginCodeData.loginField)
        if (loginResult.msg.HasValue) {
            return ApiResult.error(loginResult.msg)
        }
        val loginUsers = loginResult.data!!

        val loginUser = mor.tenant.tenantLoginUser.query()
            .where { it.tenant.id match tenantId }
            .where { it.enabled match true }
            .where { it.id match_in loginUsers.map { it.id } }
            .toEntity().must().elseThrow { "找不到用户登录信息" }

        val user = mor.tenant.tenantUser.queryById(loginUser.userId)
            .toEntity().must().elseThrow { "找不到用户 ${loginCodeData.loginName}" }

        /* 非管理员用户不能登录租户侧 */
        if (user.adminType == TenantAdminTypeEnum.None ){
            return ApiResult.error("您没有登录该系统的权限")
        }


        val tenantSetting = mor.tenant.tenantSecretSet.queryByTenantId(tenantId).toEntity() ?: TenantSecretSet()

        var tokenTime = tenantSetting.setting.sessionTimeout * 60
        if (tenantSetting.setting.sessionUnit == SettingEnum.Hour) {
            tokenTime = tenantSetting.setting.sessionTimeout * 60 * 60
        }

        val oauthCode = CodeUtil.getCode()
        val token = CodeUtil.getCode()

        val result = LoginCode2oauthCodeResult()
        result.token = token
        result.isFirstLogin = loginUser.isFirstLogin
        result.code = oauthCode

        val uuid = UUID.randomUUID().toString().replace("-", "");

        if(loginUser.isFirstLogin && tenantSetting.setting.selfSetting.securityPolicy.firstLoginUpdatePassword == true){
            rer.sys.smsCode(uuid).set(loginCodeData.loginName)
            result.uuid = uuid
        }

        var expires: Pair<PwdExpires, String>? = null
        expires = getExpire(loginUser.tenant.id, loginUser)
        result.expires = expires.first
        result.daysLeft = expires.second
        result.forceExpires = tenantSetting.setting.selfSetting.securityPolicy.expires

        rer.sys.oauthCode(oauthCode).set(
            OAuthCodeData(
                UserSystemTypeEnum.TenantAdmin,
                loginCodeData.loginField,
                loginCodeData.loginName,
                token,
                loginUser.userId
            ),
            tokenTime
        )

        loginPasswordService.passwordSend(loginUser.userId,request.getHeader("lang").toString())

        return ApiResult.of(result)
    }

    class LoginCode2oauthCodeResult {
        var token: String = ""
        var isFirstLogin: Boolean? = null
        var uuid = ""
        var code: String = ""
        var expires: PwdExpires? = null
        var daysLeft: String? = null
        var forceExpires : Boolean? = null
    }

    @BizLog(BizLogActionEnum.UpdatePassword, BizLogResourceEnum.Admin, "管理员")
    @ApiOperation("首次登录强制修改密码")
    @PostMapping("/updatePasswordFirstLogin")
    fun updatePasswordFirstLogin(
        @Require uuid: String,
        @Require tenantId: String,
        @Require newPassword: String,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "管理员修改密码"


        val phoneOrEmail = rer.sys.smsCode(uuid).get().toString()
        if(phoneOrEmail.equals("")){
            return JsonResult.error("验证已过期，请重新操作")
        }

        var salt = ""
        val user =
            mor.tenant.tenantUser.query()
                .whereOr({ it.mobile match phoneOrEmail },
                    { it.email match phoneOrEmail },
                    { it.loginName match phoneOrEmail })
                .apply {
                    if (tenantId.HasValue) {
                        this.where { it.tenant.id match tenantId }
                    }
                }
                .toList()
                .apply {
                    if (this.size < 1) {
                        return JsonResult.error("用户未找到")
                    }
                }

        mor.tenant.tenantLoginUser.query().where { it.userId match user[0].id }.toEntity()
            .apply {
                if (this == null) {
                    return JsonResult.error("用户数据有误")
                }
                if(this.isFirstLogin ==false){
                    return JsonResult.error("该用户已不是第一次登录，请核对")
                }
                salt = this.passwordSalt
            }

        // 校验密码格式是否正确
        if (ValidateUtils.containerSpace(newPassword) || ValidateUtils.isContainChinese(newPassword)) {
            return JsonResult.error("密码包含非法字符")
        }
        if (newPassword.length < 6) {
            return JsonResult.error("密码需要大于6位")
        }
        if (tenantId.HasValue) {
            // 判断租户是否正常
            val tenant = mor.tenant.tenant.query()
                .where { it.id match user[0].tenant.id }
                .toEntity().must().elseThrow { "找不到该租户" }
                .apply {
                    if (this.isLocked) {
                        return JsonResult.error("您的租户已被冻结")
                    }
                }
            val tss = mor.tenant.tenantSecretSet.queryByTenantId(tenant.id).toEntity()
            val sp = tss?.setting?.selfSetting?.securityPolicy ?: SecurityPolicy()

            if (!PwdVerifyStrategy.pwdVerification(newPassword ,sp.leastLenght, sp.lowInput,sp.upInput,sp.specialInput,sp.numberInput)) {
                return JsonResult.error( PwdVerifyStrategy.getPwdVerificationPrompt(newPassword ,sp.leastLenght, sp.lowInput,sp.upInput,sp.specialInput,sp.numberInput))
            }
        } else {
            if (user.size > 1) {
                return JsonResult.error("存在多个租户，请核对")
            }
        }
        mor.tenant.tenantLoginUser.update()
            .where { it.userId match user[0].id }
            .set { it.password to SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(newPassword) + salt) }
            .set { it.lastUpdatePwdAt to LocalDateTime.now() }
            .set { it.manualRemindPwdTimes to 0 }
            .set { it.manualExpirePwdTimes to 0 }
            .set { it.autoExpirePwdTimes to 0 }
            .set { it.autoRemindPwdTimes to 0 }
            .set { it.isFirstLogin to false }
            .exec()
        return JsonResult()
        //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
    }


    @PostMapping("/temp/userData")
    fun userData(): ApiResult<String> {
        var result = ""
        mor.tenant.tenantAdminUser.query()
            .toList(TenantUser::class.java)
            .apply {
                result += "查询到租户管理员用户信息 " + this.size + "条，"
                var execSave = 0
                this.forEach { user ->
                    user.adminType = TenantAdminTypeEnum.Super
                    user.id = ""

                    execSave += mor.tenant.tenantUser.updateWithEntity(user)
                        .whereColumn { it.loginName }
                        .withoutColumns("depts", "roles", "groups","allowApps","denyApps","identitySource","distinguishedName")
                        .doubleExecSave()
                }
                result += "执行保存租户管理员用户信息 " + execSave + "条，"
            }


        mor.tenant.tenantAdminLoginUser.query()
            .toList(TenantLoginUser::class.java)
            .apply {
                result += "查询到租户管理员账号 " + this.size + "条，"
                var execSave = 0

                this.forEach { loginUser ->
                    loginUser.id = ""

                    execSave += mor.tenant.tenantLoginUser.updateWithEntity(loginUser)
                        .whereColumn { it.loginName }
                        .withoutColumn { it.userId }
                        .doubleExecSave()
                }

                result += "执行保存租户管理员账号 " + execSave + "条，"
            }

        val userList = mor.tenant.tenantUser.query().toList()

        var exec = mor.tenant.tenantLoginUser.delete()
            .where { it.tenant.id match_notin userList.map { it.tenant.id } }
            .exec()

        result += "删除租户ID不存在的账号 " + exec + "条，"

        exec = mor.tenant.tenantLoginUser.delete()
            .where { it.loginName match_notin userList.map { it.loginName } }
            .exec()

        result += "删除loginname不存在的账号 " + exec + "条，"

        mor.tenant.tenantLoginUser.query()
            .toList()
            .apply {
                val map = userList.map { it.loginName to it.id }.toMap()

                var execUpdate = 0
                this.forEach { loginUser ->
                    if(map.get(loginUser.loginName) != loginUser.userId){
                        loginUser.userId = map.get(loginUser.loginName).toString()
                        execUpdate += mor.tenant.tenantLoginUser.updateWithEntity(loginUser).execUpdate()
                    }
                }
                result += "更新账号的userid " + execUpdate + "条，"
            }

        mor.tenant.tenantUser.query()
            .where { it.adminType match_exists false }
            .toList()
            .apply {
                this.forEach { user ->
                    mor.tenant.tenantUser.updateWithEntity(user).execUpdate()
                }
            }

        return ApiResult.of(result)
    }

    data class TokenInfoData(
        var token: String,
        var freshToken: String,
        var expriein: Long = 259200,
        var createAt: Long = LocalDateTime.now().AsDate().time
    )

    @BizLog(BizLogActionEnum.Login, BizLogResourceEnum.Define, "租户侧登录")
    @PostMapping("/code2token")
    fun code2token(code: String, request: HttpServletRequest): ApiResult<TokenInfoData> {
        val codeValue = rer.sys.oauthCode(code).get() ?: return ApiResult.error("非法code")

        val loginUser = LoginUserModel()
        val user = mor.tenant.tenantUser.queryById(codeValue.userId).toEntity().must().elseThrow { "找不到用户" }
        loginUser.id = user.id
        loginUser.name = user.name
        loginUser.isAdmin = user.adminType != TenantAdminTypeEnum.None
        loginUser.loginName = codeValue.loginName
        loginUser.loginField = codeValue.loginField
        loginUser.system = codeValue.type.toString()
        loginUser.organization = user.tenant

        val freshToken = CodeUtil.getCode()

        loginUser.token = codeValue.token
        loginUser.freshToken = freshToken

        var sessionTimeoutSeconds = request.userAuthenticationService.saveLoginUserInfo(request, loginUser)
        rer.sys.oauthCode(code).deleteKey()

        val ret = TokenInfoData(codeValue.token, freshToken, sessionTimeoutSeconds.toLong())

        /*日志相关*/
        request.logMsg = "登录账户{${loginUser.loginName}}"
        BizLogInterceptor.loginAdminUser = user

        return ApiResult.of(ret)
    }

    fun getExpire(tenantId: String, tenantLoginUser: TenantLoginUser): Pair<PwdExpires, String> {
        mor.tenant.tenantSecretSet.queryByTenantId(tenantId)
            .where { it.tenant.id match tenantId }
            .toEntity().must().elseThrow { "找不到租户" }
            .apply {
                if (this.setting.protocol == ProtocolEnum.Self) {
                    val sp = this.setting.selfSetting?.securityPolicy ?: SecurityPolicy()

                    // 该租户的过期时间
                    val epTime = tenantLoginUser.lastUpdatePwdAt.plusDays(sp.expiresDays.toLong())
                    if (LocalDateTime.now() < epTime) { // 密码未过期， 查看是否需要到期提醒
                        val notifyTime = tenantLoginUser.lastUpdatePwdAt.plusDays(sp.expiresDays.toLong()).minusDays(sp.expiresNotice.toLong())
                        if (LocalDateTime.now() < notifyTime) { // 未到到期提醒日期
                            return Pair(PwdExpires.Validity, "未过期")
                        }
                        return Pair(
                            PwdExpires.Remind,
                            Duration.between(epTime, LocalDateTime.now()).toString()
                        ) // 到期，提醒剩余时间.当前时间-过期时间
                    } else { // 密码已过期，需强制修改密码
                        return Pair(PwdExpires.Deprecated, "已过期")
                    }
                }
                return Pair(PwdExpires.Never, "永不过期") // 永不过期
            }
    }

}