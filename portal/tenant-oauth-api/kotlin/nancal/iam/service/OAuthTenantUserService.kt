package nancal.iam.service

import cn.hutool.core.date.LocalDateTimeUtil
import com.fasterxml.jackson.annotation.JsonIgnore
import com.nancal.cipher.SHA256Util
import nancal.iam.client.MPClient
import nancal.iam.client.TenantAdminClient
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.redis.*
import nancal.iam.dto.LezaoMessageDTO
import nancal.iam.model.LoginLock
import nancal.iam.service.compute.TenantUserService
import nancal.iam.util.EmailMessage
import nancal.iam.util.MailUtil
import nbcp.comm.*
import nbcp.db.CodeName
import nbcp.db.IdName
import nbcp.db.mongo.*
import nbcp.utils.CodeUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.ldap.AuthenticationException
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.odm.annotations.Attribute
import org.springframework.ldap.odm.annotations.Entry
import org.springframework.ldap.odm.annotations.Id
import org.springframework.ldap.query.LdapQueryBuilder
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import javax.annotation.Resource
import javax.naming.Name
import kotlin.collections.HashMap


@Service
class OAuthTenantUserService {
    @Value("\${openPrivatization}")
    private val openPrivatization: Boolean = true

    companion object {
        private val admin_tenant by lazy {
            return@lazy mor.tenant.tenant.query().where { it.code match "open" }.toEntity()!!
        }
    }

    class CodeModel(
        var type: String,
        var code: String,
        var expires: PwdExpires?,
        var daysLeft: String?,
        var isFirstLogin: Boolean?,
        var tenantId: String?,
        var uuid: String,
        var forceExpires: Boolean?
    )

    @Entry(objectClasses = ["person"])
    class LdapUser(
        @Id
        @JsonIgnore
        var distinguishedName: Name? = null,

        /* 登录账号 */
        @Attribute(name = "sAMAccountName")
        var loginName: String = "",

        /* 用户姓名 */
        @Attribute(name = "name")
        var name: String = "",

        /* 邮箱 */
        @Attribute(name = "mail")
        var email: String = "",

        /* 电话 */
        @Attribute(name = "telephoneNumber")
        var mobile: String = "",

        /* 职务 */
        @Attribute(name = "title")
        var title: String = "",

        /* 描述 */
        @Attribute(name = "description")
        var remark: String = "",

        )

    @Autowired
    lateinit var tenantUserService: TenantUserService

    @Autowired
    lateinit var mpClient: MPClient

    @Autowired
    lateinit var ldapTemplate: LdapTemplate

    @Autowired
    lateinit var tenantAdminClient: TenantAdminClient

    fun ldapLogin(
        username: String,
        password: String,
        appInfo: BaseApplication,
        tenant: IdName
    ): ApiResult<OAuthTenantUserService.CodeModel> {
        try {
            //查询用户信息
            var loginUser = mor.tenant.tenantLoginUser.query()
                .where { it.email match username }
                .where { it.tenant.id match tenant.id }
                .toEntity()

            if (loginUser == null) {
                ldapTemplate.setIgnorePartialResultException(true)
                val ldapUserList =
                    ldapTemplate.find(LdapQueryBuilder.query().where("mail").`is`(username), LdapUser::class.java)

                if (ldapUserList.isEmpty()) {
                    return ApiResult.error("找不到用户")
                }

                val ldapUser = ldapUserList.get(0)
                val dn = ldapUser.distinguishedName.toString()

                var dept: DeptDefine? = null
                mor.tenant.tenantDepartmentInfo.query()
                    .where { it.tenant.id match tenant.id }
                    .where { it.distinguishedName match dn.substringAfter(",") }
                    .toEntity()
                    .apply {
                        if (this != null) {
                            dept = DeptDefine(true)
                            dept?.id = this.id
                            dept?.name = this.name
                        }
                    }

                val tenantUser = TenantUser()
                tenantUser.tenant = tenant
                tenantUser.loginName = ldapUser.loginName
                tenantUser.email = ldapUser.email
                tenantUser.mobile = ldapUser.mobile
                tenantUser.name = ldapUser.name
                tenantUser.duty.name = ldapUser.title
                tenantUser.remark = ldapUser.remark
                if (dept != null) {
                    tenantUser.depts.add(dept!!)
                }
                tenantUser.distinguishedName = dn
                tenantUser.identitySource = ProtocolEnum.LDAP
                mor.tenant.tenantUser.doInsert(tenantUser)

                loginUser = TenantLoginUser()
                loginUser.tenant = tenant
                loginUser.loginName = ldapUser.loginName
                loginUser.email = ldapUser.email
                loginUser.mobile = ldapUser.mobile
                loginUser.userId = tenantUser.id
                mor.tenant.tenantLoginUser.doInsert(loginUser)
            }

            /* 验证应用授权 */
            val myApps = tenantUserService.getMyApps(loginUser.userId)
            if (myApps.data.map { it.code }.contains(appInfo.appCode) == false) {
                return ApiResult.error("您没有登录该系统的权限")
            }

            /*LDAP认证*/
            ldapTemplate.setIgnorePartialResultException(true)
            ldapTemplate.authenticate(
                LdapQueryBuilder.query().where("mail").`is`(username),
                password
            )

            val code = CodeUtil.getCode()
            val token = CodeUtil.getCode()

            rer.sys.oauthCode(code).set(
                OAuthCodeData(
                    UserSystemTypeEnum.TenantAdmin,
                    "email",
                    username,
                    token,
                    loginUser.userId,
                    CodeName(appInfo.appCode, appInfo.name)
                )
            )
            return ApiResult.of(
                CodeModel(
                    "oauthCode",
                    code,
                    null,
                    null,
                    loginUser.isFirstLogin,
                    loginUser.tenant.id,
                    "",
                    null
                )
            )
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


    @Resource
    lateinit var mailUtil: MailUtil

    @Resource
    lateinit var oauthPassordService: OauthPassordService

    @Value("\${mail.sender}")
    val mailSender: String = ""

    @Value("\${mail.pwd}")
    val mailPwd: String = ""

    @Value("\${mail.smtp}")
    val mailSmtp: String = ""

    @Value("\${mail.pop}")
    val mailPop: String = ""

    fun loginTenant(
        loginName: String,
        password: String,
        ignorePassword: Boolean,
        appInfo: BaseApplication,
        lang: String
    ): ApiResult<CodeModel> {
        /**
         * loginName全局唯一
         * 1.查到一个用户，返回 oauthCode
         * 2.查到多个用户，返回 loginCode
         */

        //验证账号锁定
        val tenantSettingMap = getSetting(loginName)
        var loginLock = checkLoginLock(loginName, tenantSettingMap)
        if (null != loginLock && loginLock.msg.isNotBlank()) {
            return ApiResult.error(loginLock.msg)
        }
        //判断loginField
        val email = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$".toRegex()
        val mobile =
            "^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$".toRegex()

        var loginField = ""
        if (mobile.containsMatchIn(loginName)) {
            loginField = "mobile"
        } else if (email.containsMatchIn(loginName)) {
            loginField = "email"
        } else {
            loginField = "loginName"
        }
        //获取符合条件的loginUser 电话 邮箱 账号

        val loginResult = findLoginUsers(loginName, loginField)
        if (loginResult.msg.HasValue) {
            return ApiResult.error(loginResult.msg)
        }
        var loginUsers = loginResult.data!!
        //验证账号关联的手机、邮箱、用户名是否锁定
        loginUsers.forEach {
            if (it.loginName.isNotEmpty()) {
                val loginLockLoginName = checkLoginLock(it.loginName, tenantSettingMap)
                if (loginLockLoginName != null) {
                    if (loginLockLoginName!!.msg.HasValue) {
                        return ApiResult.error(loginLockLoginName!!.msg)
                    }
                    return@forEach
                }
            }
            if (it.mobile.isNotEmpty()) {


                val loginLockMobile = checkLoginLock(it.mobile, tenantSettingMap)
                if (loginLockMobile != null) {
                    if (loginLockMobile!!.msg.HasValue) {
                        return ApiResult.error(loginLockMobile!!.msg)
                    }
                    return@forEach
                }
            }
            if (it.email.isNotEmpty()) {


                val loginLockEmail = checkLoginLock(it.email, tenantSettingMap)
                if (loginLockEmail != null) {
                    if (loginLockEmail!!.msg.HasValue) {
                        return ApiResult.error(loginLockEmail!!.msg)
                    }
                    return@forEach
                }
            }
        }


        //私有化控制
        if (openPrivatization) {
            val users = mor.tenant.tenantUser.query()
                .where { it.id match_in loginUsers.map { it.userId } }
                .where { it.adminType match TenantAdminTypeEnum.None }
                .orderByDesc { it.createAt }
                .toList()
            loginUsers = loginUsers.filter { login -> users.map { it.id }.contains(login.userId) }.toMutableList()
        }


        /* 验证密码 */
        val pwdResult =
            passwordVerification(ignorePassword, loginUsers, password, loginName, tenantSettingMap, loginLock)

        if (null != pwdResult && pwdResult.msg.isNotBlank()) {
            return ApiResult.error(pwdResult.msg)
        }

        var enableApps = loginUsers.filter {
            val apps = tenantUserService.getEnableApp(it.userId, appInfo.appCode)
            apps.data.map { it.code }.contains(appInfo.appCode)
        }.toMutableList()
        if (enableApps.isNotEmpty()) {
            return ApiResult.error("应用已被停用")
        }

        /* 验证应用授权 */
        loginUsers = loginUsers.filter {
            val myApps = tenantUserService.getMyApps(it.userId)
            myApps.data.map { it.code }.contains(appInfo.appCode)
        }.toMutableList()
        if (loginUsers.isEmpty()) {
            return ApiResult.error("您没有登录该系统的权限")
        }

        var expires: Pair<PwdExpires, String>? = null
        val loginUser: TenantLoginUser


        val uuid = UUID.randomUUID().toString().replace("-", "");
        rer.sys.smsCode(uuid).set(loginName);

        if (loginUsers.size > 1) {

            val loginUserTenantIds = mor.tenant.tenantApplication.query()
                .where { it.appCode match appInfo.appCode }
                .where { it.tenant.id match_in loginUsers.map { it.tenant.id } }
                .select { it.tenant.id }
                .toList(String::class.java)

            /* 3.查询有应用权限的并且是启用状态的所属租户 */
            val tenantList = mor.tenant.tenant.query()
                .where { it.id match_in loginUserTenantIds }
                .where { it.isLocked match false }
                .orderByDesc { it.createAt }
                .toList()

            if (tenantList.isEmpty()) {
                return ApiResult.error("您没有登录该系统的权限")
            }

            /**
             * 4.只有一个符合条件的租户，返回oauthCode
             * 5.有多个符合条件的租户，返回loginCode，再根据loginCode和租户id换code
             */

            if (tenantList.size > 1) {
                val validTenantIds = tenantList.map { it.id }
                val loginCode = CodeUtil.getCode()
                rer.sys.loginCode(loginCode).set(
                    LoginCodeData(loginField, loginName, CodeName(appInfo.appCode, appInfo.name), validTenantIds)
                )
                return ApiResult.of(CodeModel("loginCode", loginCode, null, null, null, "", uuid, null))
            }

//            loginUser = loginUsers.first { it.tenant.id == tenantList[0].id }
        }
        loginUser = loginUsers[0]


        val tenantSetting =
            mor.tenant.tenantSecretSet.queryByTenantId(loginUser.tenant.id).toEntity() ?: TenantSecretSet()

        expires = getExpire(loginUser.tenant.id, loginUser)
        val code = CodeUtil.getCode()
        val token = CodeUtil.getCode()

        if (expires!!.first == PwdExpires.Remind) {

            if (loginUser.manualRemindPwdTimes < 1) {

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
                                oauthPassordService.sendSmsMessage(
                                    this.mobile,
                                    lang,
                                    mutableListOf(this.name, days.toString())
                                )

                            }
                            // 邮箱通知密码到期
                            if (this.email.HasValue) {
                                // 发送邮箱
                                oauthPassordService.sendMailMessage(
                                    this.email,
                                    lang,
                                    this.name,
                                    this.loginName,
                                    days
                                )
                            }
                            // 站内行通知密码到期
                            oauthPassordService.sendMessage(
                                this.id,
                                lang,
                                this.name,
                                this.loginName,
                                days,
                                this.adminType
                            )

                            mor.tenant.tenantLoginUser.updateById(loginUser.id)
                                .set { it.manualRemindPwdTimes to 1 }
                                .exec()
                        }
                    }
            }
        }


        rer.sys.oauthCode(code).set(
            OAuthCodeData(
                UserSystemTypeEnum.TenantAdmin,
                loginField,
                loginName,
                token,
                loginUser.userId,
                CodeName(appInfo.appCode, appInfo.name)
            ),
            tenantSettingMap["maxToken"].toString().toInt()
        )

        var firstLoginUpdateState = false
        if (loginUser.isFirstLogin && tenantSetting.setting.selfSetting.securityPolicy.firstLoginUpdatePassword) {
            firstLoginUpdateState = true
        }



        return ApiResult.of(
            CodeModel(
                "oauthCode", code, expires?.first, expires?.second,
                firstLoginUpdateState,
                loginUser.tenant.id, uuid, tenantSetting.setting.selfSetting.securityPolicy.expires
            )
        )
    }

    fun passwordVerification(
        ignorePassword: Boolean,
        loginUsers: MutableList<TenantLoginUser>,
        password: String,
        loginName: String,
        tenantSettingMap: Map<String, Any>,
        loginLock: LoginLock?
    ): LoginLock? {
        if (!ignorePassword) {
            loginUsers.find {
                SHA256Util.getSHA256StrJava(password + it.passwordSalt) == it.password
            }.apply {
                if (this == null) {
                    // redis记录失败次数
                    return loginCheckFailed(loginName, loginUsers, tenantSettingMap, loginLock)
                }
            }
            // added this line by kxp at 2021.12.16 删除redis登录失败记录,同时更新用户登录记录
            loginCheckSuccess(loginName, loginUsers)
        }
        return null
    }

    fun checkLoginLock(
        loginName: String,
        tenantSettingMap: Map<String, Any>
    ): LoginLock? {
        // 查看当前要登录的用户是否被锁定，redis记录规则见README.md

        val loginLock = rer.iamUser.errorLogin(loginName).get().FromJson(LoginLock::class.java)
        if (null != loginLock) {
            loginLock.msg = ""
            val ttl = rer.iamUser.errorLogin(loginName).getExpireSeconds()
            if (ttl == -1) { // 被锁定
                loginLock.msg = "您所登录的账户已被锁定，请联系管理员"
            }
            /* if (ttl > -1) { // 且重试次数等于租户的最大重试次数
                 //且租户中最长锁定时间- 已经锁定持续时间>0  就继续锁着
                 if (loginLock.count >= tenantSettingMap["maxRetry"].toString().toInt()
                     && tenantSettingMap["maxDuration"].toString().toInt()
                         .minus(DateUtils.getDaysBetweenMinute(loginLock.lockTime!!, LocalDateTime.now())) > 0
                 ) {
                     //继续锁着
                     loginLock.msg = "您已连续 ${loginLock.count} 次输入错误，账号已被锁定，请 " +
                         "${
                             tenantSettingMap["maxDuration"].toString().toInt()
                                 .minus(DateUtils.getDaysBetweenMinute(loginLock.lockTime!!, LocalDateTime.now()))
                         } " +
                         "分钟后再试！"
                 }
             }*/
            if (ttl > -1) {
                if (loginLock.strongLock) {
                    //继续锁着
                    var lockTime = 1
                    if (ttl % 60 == 0) {
                        lockTime = ttl / 60
                    } else {
                        lockTime = ttl / 60 + 1
                    }
                    loginLock.msg = "错误密码输入次数过多，请稍后再试。"
                    /*loginLock.msg = "您已连续 ${loginLock.count} 次输入错误，账号已被锁定，请 " +
                            "${lockTime}" +
                            "分钟后再试！"*/
                }
            }
        }
        return loginLock
    }

    fun findLoginUsers(
        loginName: String,
        loginField: String,
    ): ApiResult<MutableList<TenantLoginUser>> {
        val loginUsers = mor.tenant.tenantLoginUser.query()
            .where { it.enabled match true }
            .whereOr({ it.loginName match loginName }, { it.mobile match loginName }, { it.email match loginName })
            .orderByDesc { it.createAt }
            .toList()

        if (loginUsers.isEmpty()) {
            return ApiResult.error("用户被停用")
        }

        /* 手机、邮箱 关联查询 */
/*        if (loginUsers.get(0).email.HasValue || loginUsers.get(0).mobile.HasValue) {
            val queryClip = mor.tenant.tenantLoginUser.query().where { it.enabled match true }
                .where { it.id match_notin loginUsers.map { it.id } }

            when (loginField) {
                "loginName" -> {
                    if (loginUsers.get(0).mobile.HasValue) {
                        queryClip.whereOr({ it.mobile match loginUsers.get(0).mobile })
                    }
                    if (loginUsers.get(0).email.HasValue) {
                        queryClip.whereOr({ it.email match loginUsers.get(0).email })
                    }
                }
                "mobile" -> {
                    queryClip.where { it.email match_in loginUsers.filter { it.email.HasValue }.map { it.email } }
                }
                "email" -> {
                    queryClip.where { it.email match_in loginUsers.filter { it.mobile.HasValue }.map { it.mobile } }
                }
            }
            loginUsers.addAll(queryClip.toList())
        }*/
        return ApiResult.of(loginUsers)
    }


    fun mobileLogin(
        mobile: String,
        validateCode: String,
        appInfo: BaseApplication,
        lang: String
    ): ApiResult<CodeModel> {

        //自己校验验证码
        val codeStatus = mpClient.codeStatus(MobileCodeModuleEnum.Login, mobile, validateCode)

        if (codeStatus.code != 0) {
            return ApiResult.error("验证码校验错误，请重试", 500)
        }

        return loginTenant(mobile, "", true, appInfo, lang)
    }

    /**
     *
     * 是否强制过期，否，跳过；是，对比上次密码更新时间，若已失效，则跳转修改密码，否，返回剩余密码有效期
     */
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
                        val notifyTime = tenantLoginUser.lastUpdatePwdAt.plusDays(sp.expiresDays.toLong())
                            .minusDays(sp.expiresNotice.toLong())
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


    // 登录失败时根据租户是否锁定该账户
    private fun loginCheckFailed(
        loginName: String,
        loginUsers: MutableList<TenantLoginUser>,
        tenantSettingMap: Map<String, Any>,
        loginLock: LoginLock?
    ): LoginLock {
        val maxCheckingPeriod = tenantSettingMap["maxCheckingPeriod"].toString().toInt()
        val manual = tenantSettingMap["manual"].toString().toBoolean()
        val maxRetry = tenantSettingMap["maxRetry"].toString().toInt()
        val maxDuration = tenantSettingMap["maxDuration"].toString().toInt()
        var expire = maxCheckingPeriod  // 登录验证统计周期，单位秒
        val accounts =
            loginUsers.filter { it.loginName != null && it.loginName.isNotEmpty() }.map { it.loginName }.toMutableSet()
        val mobiles = loginUsers.filter { it.mobile != null && it.mobile.isNotEmpty() }.map { it.mobile }.toMutableSet()
        val emails = loginUsers.filter { it.email != null && it.email.isNotEmpty() }.map { it.email }.toMutableSet()

        //================================不管使用什么登录方式   全部同步邮箱 账号 电话登录方式================================
        //第一次登录错误
        if (null == loginLock) {
            val result = LoginLock()
            if (manual) {
                expire = -1
            }
            if (maxRetry == 1) {
                expire = maxDuration * 60
                result.lockTime = LocalDateTime.now()
                result.count = maxRetry
                result.strongLock = true
                result.msg = "第1次输入错误，剩余0次，账号已被锁定，请{$maxDuration}分钟后再试！"
            } else {
                result.msg = "第1次输入错误，剩余{" + (maxRetry - 1).toString() + "}次，请输入正确的用户名或密码！"
            }
            //记录进入redis
            accounts.forEach {
                //记录账户
                rer.iamUser.errorLogin(it).set(
                    result.ToJson(),
                    expire
                )
            }
            mobiles.forEach {
                //记录手机号
                rer.iamUser.errorLogin(it).set(
                    result.ToJson(),
                    expire
                )
            }
            emails.forEach {
                //记录邮箱
                rer.iamUser.errorLogin(it).set(
                    result.ToJson(),
                    expire
                )
            }
            if (maxRetry == 1) {
                result.msg = "错误密码输入次数过多，请稍后再试。"
            }
            return result
        } else {
            //第n次登录错误
            val tryCount: Int = loginLock.count + 1

            var cacheExpire = rer.iamUser.errorLogin(loginName).getExpireSeconds()
            if (manual) {
                cacheExpire = -1
            }
            if (tryCount >= maxRetry) {
                cacheExpire = maxDuration * 60
                loginLock.count = tryCount
                loginLock.strongLock = true
                loginLock.lockTime = LocalDateTime.now()
                loginLock.msg = "您已连续{$tryCount}次输入错误，账号已被锁定，请{$maxDuration}分钟后再试！"
                accounts.forEach {
                    //记录账户
                    rer.iamUser.errorLogin(it).set(
                        loginLock.ToJson(),
                        cacheExpire
                    )
                }
                mobiles.forEach {
                    //记录手机号
                    rer.iamUser.errorLogin(it).set(
                        loginLock.ToJson(),
                        cacheExpire
                    )
                }
                emails.forEach {
                    //记录邮箱
                    rer.iamUser.errorLogin(it).set(
                        loginLock.ToJson(),
                        cacheExpire
                    )
                }
                loginLock.msg = "错误密码输入次数过多，请稍后再试。"
                return loginLock
            } else {
                loginLock.count = tryCount
                loginLock.msg = "第{" + tryCount + "}次输入错误，剩余{" +
                        (maxRetry - tryCount) + "}次，请输入正确的用户名或密码！"
                accounts.forEach {
                    //记录账户
                    rer.iamUser.errorLogin(it).set(
                        loginLock.ToJson(),
                        cacheExpire
                    )
                }
                mobiles.forEach {
                    //记录手机号
                    rer.iamUser.errorLogin(it).set(
                        loginLock.ToJson(),
                        cacheExpire
                    )
                }
                emails.forEach {
                    //记录邮箱
                    rer.iamUser.errorLogin(it).set(
                        loginLock.ToJson(),
                        cacheExpire
                    )
                }
                return loginLock
            }

        }
    }

    // 登录成功时修改状态
    private fun loginCheckSuccess(loginName: String, loginUsers: MutableList<TenantLoginUser>) {
        val email = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$".toRegex()
        val mobile =
            "^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$".toRegex()
        loginUsers.forEach {
            if (it.loginName != null && it.loginName.isNotEmpty()) {
                rer.iamUser.errorLogin(it.loginName).deleteKey()
            }
            if (it.mobile != null && it.mobile.isNotEmpty()) {
                rer.iamUser.errorLogin(it.mobile).deleteKey()
            }
            if (it.email != null && it.email.isNotEmpty()) {
                rer.iamUser.errorLogin(it.email).deleteKey()
            }
        }
        mor.tenant.tenantLoginUser.update()
            .apply {
                if (email.containsMatchIn(loginName)) {
                    this.where { it.email match loginName }
                } else if (mobile.containsMatchIn(loginName)) {
                    this.where { it.mobile match loginName }
                } else {
                    this.where { it.loginName match loginName }
                }
            }
            .set { it.isLocked to false }
            .set { it.lastLoginAt to LocalDateTime.now() }
            .set { it.errorLoginTimes to 0 }.exec()
    }

    fun getAllTenantUsers(loginName: String): MutableList<TenantUser> {
        val loginUsers = mor.tenant.tenantLoginUser.query()
            .whereOr({ it.loginName match loginName }, { it.mobile match loginName }, { it.email match loginName })
            .toList()
        return mor.tenant.tenantUser.query()
            .where { it.tenant.id match_in loginUsers.map { it.tenant.id } }
            .toList()
    }

    fun getSetting(loginName: String): Map<String, Any> {
        getAllTenantUsers(loginName).apply {
            if (this.isEmpty()) {
                throw RuntimeException("找不到用户：$loginName")
            }
            mor.tenant.tenantSecretSet.query()
                .where { it.tenant.id match_in this.map { it.tenant.id } }
                .toList().apply {
                    // 租户中最大登录验证统计周期
                    val maxCheckingPeriod =
                        this.map { it.setting.selfSetting?.loginChecking ?: LoginChecking() }.toList()
                            .maxByOrNull { it.checkingPeriod }!!.checkingPeriod * 60
                    // 租户中最大允许登录失败尝试次数
                    val maxRetry =
                        this.map { it.setting.selfSetting?.loginChecking ?: LoginChecking() }.toList()
                            .maxByOrNull { it.retryTime }!!.retryTime
                    // 租户中最大 token 会话时长
                    var maxToken =
                        this.map { it.setting }.toList()
                            .filter { SettingEnum.Hour == it.sessionUnit }
                            .maxByOrNull { it.sessionTimeout }?.sessionTimeout
                    if (null == maxToken) {
                        maxToken = this.map { it.setting }.toList()
                            .filter { SettingEnum.Minute == it.sessionUnit }
                            .maxByOrNull { it.sessionTimeout }!!.sessionTimeout * 60
                    } else {
                        maxToken *= 60 * 60
                    }

                    // TODO 租户中长被锁定时持续时间
                    val maxDuration =
                        this.map { it.setting.selfSetting?.loginChecking ?: LoginChecking() }.toList()
                            .maxByOrNull { it.lockDuration }!!.lockDuration
                    // 租户中最短被锁定时持续时间, 只要有一个是自动的，就按自动解锁的算
                    var manual = true
                    val falseSize =
                        this.map { it.setting.selfSetting?.loginChecking?.manual ?: false }.toList().filter { !it }.size
                    if (falseSize > 0) manual = false

                    val map = HashMap<String, Any>(4)
                    map["maxCheckingPeriod"] = maxCheckingPeriod
                    map["maxRetry"] = maxRetry
                    map["maxDuration"] = maxDuration
                    map["manual"] = manual
                    map["maxToken"] = maxToken
                    return map
                }
        }
    }

}
