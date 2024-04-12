package nancal.iam.mvc

import cn.hutool.core.date.LocalDateTimeUtil
import com.nancal.cipher.SHA256Util
import io.swagger.annotations.ApiOperation
import nancal.iam.annotation.BizLog
import nancal.iam.base.config.BizLogInterceptor
import nancal.iam.base.config.BizLogInterceptor.Companion.appCode
import nancal.iam.base.config.BizLogInterceptor.Companion.loginUser
import nancal.iam.client.MPClient
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.redis.OAuthCodeData
import nancal.iam.db.redis.rer
import nancal.iam.service.OAuthTenantUserService
import nancal.iam.service.compute.TenantUserService
import nancal.iam.util.EmailMessage
import nancal.iam.util.MailUtil
import nancal.iam.util.PwdVerifyStrategy
import nancal.iam.util.ValidateUtils
import nbcp.base.mvc.getPostJson
import nbcp.base.mvc.handler.AuthImageServlet
import nbcp.comm.*
import nbcp.db.LoginUserModel
import nbcp.db.mongo.*
import nbcp.utils.CodeUtil
import nbcp.web.basicLoginNamePassword
import nbcp.web.tokenValue
import nbcp.web.userAuthenticationService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.ldap.AuthenticationException
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.query.LdapQueryBuilder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@OpenAction
@RestController
class LoginOpenController {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    @Autowired
    lateinit var ldapTemplate: LdapTemplate

    @Autowired
    lateinit var tenantUserService: OAuthTenantUserService

    @Resource
    lateinit var authImageServlet: AuthImageServlet

    @Autowired
    lateinit var userService: TenantUserService

    @Value("\${captcha.vip.open}")
    var captchaFlag: Boolean = false
    @Value("\${openPrivatization}")
    private val openPrivatization: Boolean = true

    @PostMapping("/oauth/token")
    fun token(request: HttpServletRequest): ApiResult<String> {
        val token = request.tokenValue
        return ApiResult.of(token)
    }

    @GetMapping("/oauth/validate-img")
    fun validateImg(request: HttpServletRequest, response: HttpServletResponse) {
        authImageServlet.doGet(request, response)
    }

    @PostMapping("/oauth/login")
    fun login(
        token: String,
        appCode: String,
        validateCode: String,
        request: HttpServletRequest
    ): ApiResult<OAuthTenantUserService.CodeModel> {
        val postJson = request.getPostJson()
        val mobile = postJson.getStringValue("mobile") ?: ""
        var loginName = postJson.getStringValue("loginName") ?: ""
        var password = postJson.getStringValue("password") ?: ""
        if (captchaFlag && validateCode.lowercase(Locale.getDefault()).equals("vip888")) {
            logger.info("vip通道开启了")
        } else if (mobile.isBlank() && validateCode != request.userAuthenticationService.getValidateCode(token)) {
            return ApiResult.error("验证码输入错误或已过期，请重试！")
        }
        request.userAuthenticationService.deleteToken(request)

        if (!appCode.HasValue) {
            return ApiResult.error("应用编码不能是空")
        }

        //admin端应用
        var appInfo: BaseApplication? = mor.iam.sysApplication.query()
            .where { it.appCode match appCode }
            .toEntity()

        if (appInfo == null) {
            //租户端应用
            appInfo = mor.tenant.tenantApplication.query()
                .where { it.appCode match appCode }
                .toEntity() ?: return ApiResult.error("找不到应用")
        }

        //电话登录
        if (mobile.HasValue) {
            // 私有化管理员登录限制
            if(!openPrivatization){
                return tenantUserService.mobileLogin(mobile, validateCode, appInfo,request.getHeader("lang").toString())
            }else{

                val loginUsers = mor.tenant.tenantUser.query()
                    .where { it.mobile match mobile }
                    .where{it.adminType match TenantAdminTypeEnum.None}
                    .toList()
                if (loginUsers.size==0) return ApiResult.error("找不到用户", 500)
                return tenantUserService.mobileLogin(mobile, validateCode, appInfo,request.getHeader("lang").toString())
            }

        }
        if (loginName.isEmpty()) {
            val loginNamePassword = request.basicLoginNamePassword
            loginName = loginNamePassword.loginName
            password = loginNamePassword.password
        }

        //LDAP认证
        //目前先支持邮箱登录
        val matchEmailParttern = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$"
        val isEmailMatch = Regex(matchEmailParttern).containsMatchIn(loginName)
        //邮箱登录
        if (isEmailMatch) {
            //根据邮箱后缀查询租户配置是否是LDAP
            val suffix = loginName.substringAfter("@")
            //查询租户配置
            val tenantSecretSet = mor.tenant.tenantSecretSet.query()
                .where { it.setting.protocol match ProtocolEnum.LDAP }
                .where { it.setting.ldapSetting.mailSuffix match suffix }
                .toEntity()

            if (tenantSecretSet != null) {
                //私有化管理员登录限制
                if(!openPrivatization){
                    return tenantUserService.ldapLogin(loginName, password, appInfo, tenantSecretSet.tenant)
                }else{
                    mor.tenant.tenantUser.query()
                        .where {it.tenant.id match tenantSecretSet.tenant.id  }
                        .where{it.adminType match TenantAdminTypeEnum.None}
                        .where { it.email match loginName }
                        .toList()
                        .apply {
                            if(this.size==0) return ApiResult.error("找不到用户", 500)
                            return tenantUserService.ldapLogin(loginName, password, appInfo, tenantSecretSet.tenant)
                        }
                }

            }
        }
        // 账号密码 电话密码 邮箱密码 登录 私有化管理员登录限制
        if(!openPrivatization){
            return tenantUserService.loginTenant(loginName, password, false, appInfo, request.getHeader("lang").toString())
        }else{
            var users= mutableListOf<TenantUser>()
            //loginName可能是手机号账号邮箱
            val mobile =
                "^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$".toRegex()
            val email = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$".toRegex()
            if (mobile.containsMatchIn(loginName)) {
             //手机号
               users= mor.tenant.tenantUser.query()
                    .where { it.mobile match loginName }
                    .where{it.adminType match TenantAdminTypeEnum.None}
                    .toList()
            } else if(email.containsMatchIn(loginName)) {
                //邮箱
                users= mor.tenant.tenantUser.query()
                    .where { it.email match loginName }
                    .where{it.adminType match TenantAdminTypeEnum.None}
                    .toList()
            }else{
                //用户名
                users= mor.tenant.tenantUser.query()
                    .where { it.loginName match loginName }
                    .where{it.adminType match TenantAdminTypeEnum.None}
                    .toList()
            }
            users.apply {
                if (this.size == 0) return ApiResult.error("找不到用户", 500)
                return tenantUserService.loginTenant(loginName, password, false, appInfo,request.getHeader("lang").toString())
            }
        }

    }


    class AppLogin {
        var appName: String = ""
        var ename: String = ""
        var appCode: String = ""
        var logoUrl : String = ""
    }

    @PostMapping("/oauth/getAppName")
    fun getSysAppList(appCode: String, request: HttpServletRequest): ApiResult<AppLogin> {
        val appLogin = AppLogin()
        //admin端应用
        var appInfo: BaseApplication? = mor.iam.sysApplication.query()
            .where { it.appCode match appCode }
            .toEntity()

        if (appInfo == null) {
            //租户端应用
            appInfo = mor.tenant.tenantApplication.query()
                .where { it.appCode match appCode }
                .toEntity() ?: return ApiResult.error("找不到应用")
        }
        if (appInfo.name.isNotBlank() || appInfo.ename.isNotBlank()) {
            appLogin.appName = appInfo.name
            appLogin.ename = appInfo.ename
            appLogin.appCode = appInfo.appCode
            appLogin.logoUrl = appInfo.logo!!.url
        }
        return ApiResult.of(appLogin)
    }


    data class TokenInfoData(
        var token: String,
        var freshToken: String,
        var expriein: Long = 259200,
        var createAt: Long = LocalDateTime.now().AsDate().time
    )

    @BizLog(BizLogActionEnum.Login, BizLogResourceEnum.Define, "统一登录")
    @PostMapping("/oauth/code2token")
    fun code2token(code: String, request: HttpServletRequest): ApiResult<TokenInfoData> {
        val codeValue = rer.sys.oauthCode(code).get() ?: return ApiResult.error("非法code")

        val loginUser = LoginUserModel()
        val user = mor.tenant.tenantUser.queryById(codeValue.userId).toEntity().must().elseThrow { "找不到用户" }
        if(openPrivatization){
            if(user.adminType!=TenantAdminTypeEnum.None){
                return ApiResult.error("管理员不允许登录")
            }
        }
        loginUser.id = user.id
        loginUser.name = user.name
        loginUser.isAdmin = false
        loginUser.loginName = codeValue.loginName
        loginUser.loginField = codeValue.loginField
        loginUser.system = codeValue.type.toString()
        loginUser.organization = user.tenant
        loginUser.depts = user.depts.map { it.id }
        loginUser.groups = user.groups.map { it.id }
        loginUser.roles = user.roles.map { it.id }

        val freshToken = CodeUtil.getCode()

        loginUser.token = codeValue.token
        loginUser.freshToken = freshToken

        var sessionTimeoutSeconds = request.userAuthenticationService.saveLoginUserInfo(request, loginUser)
        rer.sys.oauthCode(code).deleteKey()

        val ret = TokenInfoData(codeValue.token, freshToken, sessionTimeoutSeconds.toLong())

        /*日志相关*/
        BizLogInterceptor.logMsg = "登录账户{${loginUser.loginName}}"
        BizLogInterceptor.appCode = codeValue.app.code
        BizLogInterceptor.loginUser = loginUser

        return ApiResult.of(ret)
    }


    @Autowired
    lateinit var mpClient: MPClient

    @Resource
    lateinit var mailUtil: MailUtil

    @Value("\${mail.sender}")
    val mailSender: String = ""

    @Value("\${mail.pwd}")
    val mailPwd: String = ""

    @Value("\${mail.smtp}")
    val mailSmtp: String = ""

    @Value("\${mail.pop}")
    val mailPop: String = ""


    @PostMapping("/oauth/loginCode2oauthCode")
    fun loginCode2oauthCode(loginCode: String, tenantId: String): ApiResult<LoginCode2oauthCodeResult> {
        val loginCodeData = rer.sys.loginCode(loginCode).get() ?: return ApiResult.error("非法code")

        val loginResult = tenantUserService.findLoginUsers(loginCodeData.loginName, loginCodeData.loginField)
        if (loginResult.msg.HasValue) {
            return ApiResult.error(loginResult.msg)
        }
        val loginUsers = loginResult.data!!

        val loginUser = mor.tenant.tenantLoginUser.query()
            .where { it.tenant.id match tenantId }
            .where { it.enabled match true }
            .where { it.id match_in loginUsers.map { it.id } }
            .toEntity().must().elseThrow { "找不到用户" }

        // added this line by kxp at 2021.12.16
        // TODO 登录成功，判断用户是否需要强制修改密码
//        val expire = tenantUserService.getExpire(tenantId, loginUser)

        val tenantSetting = mor.tenant.tenantSecretSet.queryByTenantId(tenantId).toEntity() ?: TenantSecretSet()

        var tokenTime = tenantSetting.setting.sessionTimeout * 60
        if (tenantSetting.setting.sessionUnit == SettingEnum.Hour) {
            tokenTime = tenantSetting.setting.sessionTimeout * 60 * 60
        }

        val oauthCode = CodeUtil.getCode()
        val token = CodeUtil.getCode()

        val isUpdatePassword = mor.tenant.tenantSecretSet.query().where { it.tenant.id match loginUser.tenant.id }
            .select { it.setting.selfSetting.securityPolicy.firstLoginUpdatePassword }
            .toEntity(Boolean::class.java)


        var result = LoginCode2oauthCodeResult()
        result.token = token
        result.isFirstLogin = loginUser.isFirstLogin && isUpdatePassword == true
        result.code = oauthCode


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
                token, loginUser.userId
            ),
            tokenTime
        )

        if(expires.first == PwdExpires.Remind) {
            if (loginUser.manualRemindPwdTimes <1){
                val tenantSecretSet = mor.tenant.tenantSecretSet.query()
                    .where { it.tenant.id match loginUser.tenant.id }
                    .toEntity()

                val sp = tenantSecretSet?.setting!!.selfSetting?.securityPolicy ?: SecurityPolicy()
                val epTime = loginUser.lastUpdatePwdAt.plusDays(sp.expiresDays.toLong())
                val days = LocalDateTimeUtil.between(LocalDateTime.now(), epTime).toDays()

                mor.tenant.tenantUser.queryById(loginUser.userId)
                    .toEntity()
                    .apply {
                        if (this != null) {
                            if (this.mobile.HasValue) {
                                // 发送短信

                                mpClient.sendSmsNotification(
                                    "",
                                    MobileCodeModuleEnum.PasswordWillExpire,
                                    this.mobile,
                                    "cn",
                                    mutableListOf(this.name, days.toString())
                                )

                            }

                            if (this.email.HasValue) {
                                // 发送邮箱
                                val msg = EmailMessage()
                                msg.sender = mailSender
                                msg.password = mailPwd
                                msg.addressee = listOf(this.email)
                                msg.topic = "您的密码即将过期， 请尽快修改！"
                                msg.content = "${this.name}，您好\n" +
                                        " <p style=\"text-indent:2em\">您的账号${this.loginName}的密码将在${days}天后过期，为避免影响系统使用，请尽快登录修改密码。</br>"
                                msg.popService = mailPop
                                msg.smtpService = mailSmtp
                                mailUtil.sendEmail(msg)
                            }

                            // 发送站内信
    //                        var dto =  LezaoMessageDTO()
    //                        dto.userIds = mutableListOf(this.id)
//                            dto.body= "<p style=\"font-size: 16px;padding-bottom:16px\">${this.name}，您好！</p>"+
//                                    "<p style=\"text-indent: 2rem;font-size: 14px\">您的账号${this.loginName}的密码已过期，为避免影响系统使用，请尽快登录修改密码。</p>"
    //                        dto.title = "您的密码已过期，请尽快修改！"
    //                        dto.mailType = 1
    //                        dto.msgType = 1
    //
    //                        tenantAdminClient.save(dto)

                            mor.tenant.tenantLoginUser.updateById(loginUser.id)
                                .set { it.manualRemindPwdTimes to 1 }
                                .exec()
                        }
                    }
             }
        }





        return ApiResult.of(result)
    }

    class LoginCode2oauthCodeResult {
        var token: String = ""
        var isFirstLogin: Boolean? = null
        var code: String = ""
        var expires: PwdExpires? = null
        var daysLeft: String? = null
        var forceExpires : Boolean? = null
    }

    @PostMapping("/oauth/code2tenants")
    fun code2tenants(loginCode: String, oauthToken: String, @Require appCode: String): ListResult<Tenant> {
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

        val loginResult = tenantUserService.findLoginUsers(loginName, loginField)
        if (loginResult.msg.HasValue) {
            return ListResult.error(loginResult.msg)
        }
        var loginUsers = loginResult.data!!

        //admin端应用
        var appInfo: BaseApplication? = mor.iam.sysApplication.query()
            .where { it.appCode match appCode }
            .toEntity()

        if (appInfo == null) {
            //租户端应用
            appInfo = mor.tenant.tenantApplication.query()
                .where { it.appCode match appCode }
                .toEntity() ?: return ListResult.error("找不到应用")
        }

        /* 过滤没有应用权限的用户 */
        loginUsers = loginUsers.filter { loginUser ->
            val myApps = userService.getMyApps(loginUser.userId)
            myApps.data.map { it.code }.contains(appInfo.appCode)
        }.toMutableList()
        /*开启私有化 过滤掉是管理员的用户*/
        if(openPrivatization){
            val userIds=loginUsers.map { it.userId }.toMutableList()
            var users=mor.tenant.tenantUser.query()
                .where{it.id match_in userIds}
                .toList().toMutableList()
            users=users.filter { it.adminType==TenantAdminTypeEnum.None }.toMutableList()
            val yesIds=users.map { it.id }.toMutableList()
            loginUsers = loginUsers.filter { loginUser ->
                yesIds.contains(loginUser.userId)
            }.toMutableList()
        }


        /* 查询有应用权限的并且是启用状态的所属租户 */
        val tenantIds = loginUsers.map { it.tenant.id }

        val appTenantIds = mor.tenant.tenantApplication.query()
            .where { it.appCode match appInfo.appCode }
            .where { it.tenant.id match_in tenantIds }
            .select { it.tenant.id }
            .toList(String::class.java)

        return mor.tenant.tenant.query()
            .where { it.id match_in appTenantIds }
            .where { it.isLocked match false }
            .toListResult()
    }


    @PostMapping("/ldap/login")
    fun login(username: String, password: String): ApiResult<Boolean> {
/*        val filter = AndFilter()
        filter.and(EqualsFilter("sAMAccountName", username))

        ldapTemplate.setIgnorePartialResultException(true)
        val authenticate = ldapTemplate.authenticate("", filter.toString(), password)*/

        /*
        * 加载证书到JDK中的cacerts
        * keytool -import -v -trustcacerts -alias dc01.nancal.com-192.168.50.143 -file C:\Users\lenovo\Desktop\ldap.cer -storepass changeit -keystore "D:\Java\jdk-11.0.8\lib\security\cacerts"
        * */


        try {
            ldapTemplate.setIgnorePartialResultException(true)

            ldapTemplate.authenticate(
                LdapQueryBuilder.query().where("mail").`is`(username),
                password
            )
            return ApiResult.of(true)
        } catch (er: EmptyResultDataAccessException) {
            println(er)
            return ApiResult.error("用户名或密码错误")
        } catch (ea: AuthenticationException) {
            println(ea)
            if (ea.message.toString().contains("775")) {
                return ApiResult.error("账户已被锁定")
            }
            return ApiResult.error("用户名或密码错误")
        } catch (ex: Exception) {
            return ApiResult.error(ex.message.toString())
        }

    }

    @PostMapping("/ldap/user")
    fun getLdapUser(mail: String): ApiResult<OAuthTenantUserService.LdapUser> {
        ldapTemplate.setIgnorePartialResultException(true)
        val ldapUserList = ldapTemplate.find(
            LdapQueryBuilder.query().where("mail").`is`(mail),
            OAuthTenantUserService.LdapUser::class.java
        )

        if (ldapUserList.isEmpty()) {
            return ApiResult.error("找不到用户")
        }
        return ApiResult.of(ldapUserList.get(0))
    }

    //    @BizLog(BizLogActionEnum.Update, BizLogResourceEnum.User, "首次登录强制修改密码")
    @ApiOperation("首次登录强制修改密码")
    @PostMapping("/oauth/updatePasswordFirstLogin")
    fun updatePasswordFirstLogin(
        @Require uuid: String,
        @Require tenantId: String,
        @Require newPassword: String,
        request: HttpServletRequest
    ): JsonResult {

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

        /*操作日志*/
        BizLogInterceptor.logMsg = "首次登录强制修改密码{${loginUser.loginName}}"
        BizLogInterceptor.appCode = appCode
        BizLogInterceptor.loginUser = loginUser
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
            .set { it.isFirstLogin to false }
            .set { it.lastUpdatePwdAt to LocalDateTime.now() }
            .set { it.manualRemindPwdTimes to 0 }
            .set { it.manualExpirePwdTimes to 0 }
            .set { it.autoExpirePwdTimes to 0 }
            .set { it.autoRemindPwdTimes to 0 }
            .exec()
        return JsonResult()
        //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
    }

    @BizLog(BizLogActionEnum.Logout, BizLogResourceEnum.Define, "统一登录")
    @PostMapping("/oauth/logout")
    fun logout(token: String, appCode: String, request: HttpServletRequest): JsonResult {
        if (token.isEmpty()) return JsonResult()

        var loginUser = rer.sys.oauthToken(token).get()
        if (loginUser == null) {
            return JsonResult()
        }

        /*操作日志*/
        BizLogInterceptor.logMsg = "退出账户{${loginUser.loginName}}"
        BizLogInterceptor.appCode = appCode
        BizLogInterceptor.loginUser = loginUser

        if (loginUser.freshToken.HasValue) {
            rer.sys.freshToken(loginUser.freshToken).deleteKey()
        }

        rer.sys.oauthToken(token).deleteKey()

        return JsonResult()
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



    /*
    * 17所项目定制化接口
    * 为第三方应用对接API提供认证
    * */
    @PostMapping("/project/password2token")
    fun projectLogin(
        @Require loginName: String,
        @Require password: String,
        request: HttpServletRequest
    ): ApiResult<TokenInfoData>  {
        request.userAuthenticationService.deleteToken(request)

        //查询用户信息
        var user= mor.tenant.tenantUser.query()
            .where { it.loginName match loginName }
            .where{it.adminType match TenantAdminTypeEnum.None}
            .toEntity()
        if (user == null){
            return ApiResult.error("找不到用户", 500)
        }

        //验证账号锁定
        val tenantSettingMap = tenantUserService.getSetting(loginName)
        var loginLock = tenantUserService.checkLoginLock(loginName, tenantSettingMap)
        if (null != loginLock && loginLock.msg.isNotBlank()) {
            return ApiResult.error(loginLock.msg)
        }

        //查询用户登录信息
        val loginUsers = mor.tenant.tenantLoginUser.query()
            .where { it.enabled match true }
            .where { it.loginName match loginName }
            .toList()

        if (loginUsers.isEmpty()) {
            return ApiResult.error("用户被停用")
        }

        // 验证密码
        val pwdResult =
            tenantUserService.passwordVerification(false, loginUsers, password, loginName, tenantSettingMap, loginLock)

        if (null != pwdResult && pwdResult.msg.isNotBlank()) {
            return ApiResult.error(pwdResult.msg)
        }

        //颁发token
        val loginUser = LoginUserModel()
        loginUser.id = user.id
        loginUser.name = user.name
        loginUser.isAdmin = false
        loginUser.loginName = loginName
        loginUser.loginField = "loginName"
        loginUser.system = ""
        loginUser.organization = user.tenant
        loginUser.depts = user.depts.map { it.id }
        loginUser.groups = user.groups.map { it.id }
        loginUser.roles = user.roles.map { it.id }

        val freshToken = CodeUtil.getCode()
        val token = CodeUtil.getCode()

        loginUser.token = token
        loginUser.freshToken = freshToken

        var sessionTimeoutSeconds = request.userAuthenticationService.saveLoginUserInfo(request, loginUser)

        val ret = TokenInfoData(token, freshToken, sessionTimeoutSeconds.toLong())

        return ApiResult.of(ret)
    }

}
