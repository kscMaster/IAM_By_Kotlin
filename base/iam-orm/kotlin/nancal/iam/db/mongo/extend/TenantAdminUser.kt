package nancal.iam.db.mongo.extend

import com.nancal.cipher.SHA256Util
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.LoginChecking
import nancal.iam.db.mongo.entity.SecurityPolicy
import nancal.iam.db.mongo.entity.TenantLoginUser
import nancal.iam.db.mongo.entity.TenantUser
import nancal.iam.db.mongo.table.TenantGroup
import nancal.iam.db.redis.LoginCodeData
import nancal.iam.db.redis.OAuthCodeData
import nancal.iam.db.redis.rer
import nancal.iam.model.LoginLock
import nancal.iam.util.DateUtils
import nbcp.comm.*
import nbcp.db.CodeName
import nbcp.db.LoginUserModel
import nbcp.db.mongo.*
import nbcp.utils.CodeUtil
import nbcp.utils.TokenUtil
import java.time.Duration
import java.time.LocalDateTime


/**
 * 登录验证，返回错误消息
 */
fun TenantGroup.TenantUserEntity.doLogin(
    loginName: String,
    password: String,
    requestToken: String,
    ignorePassword: Boolean
): ApiResult<CodeModel> {

    val tenantSettingMap = getSetting(loginName)

    // 查看当前要登录的用户是否被锁定，redis记录规则见README.md

    var loginLock = checkLoginLock(loginName, tenantSettingMap)
    if (null != loginLock && loginLock.msg.isNotBlank()) {
        return ApiResult.error(loginLock.msg)
    }

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

    val loginResult = getLoginUsers(loginName, loginField)
    if (loginResult.msg.HasValue) {
        return ApiResult.error(loginResult.msg)
    }

    var loginUsers = loginResult.data!!

    loginUsers.forEach {
        if(it.loginName.isNotEmpty()) {
            val loginLockLoginName = checkLoginLock(it.loginName, tenantSettingMap)
            if (loginLockLoginName != null) {
                if (loginLockLoginName!!.msg.HasValue) {
                    return ApiResult.error(loginLockLoginName!!.msg)
                }
                return@forEach
            }
        }
        if(it.mobile.isNotEmpty()) {


           val loginLockMobile = checkLoginLock(it.mobile, tenantSettingMap)
            if (loginLockMobile != null) {
                if (loginLockMobile!!.msg.HasValue) {
                    return ApiResult.error(loginLockMobile!!.msg)
                }
                return@forEach
            }
        }
        if(it.email.isNotEmpty()) {


            val loginLockEmail = checkLoginLock(it.email, tenantSettingMap)
            if (loginLockEmail != null) {
                if (loginLockEmail!!.msg.HasValue) {
                    return ApiResult.error(loginLockEmail!!.msg)
                }
                return@forEach
            }
        }
    }
    /* 验证密码 */
    val pwdResult =
        passwordVerification(ignorePassword, loginUsers, password, loginName, tenantSettingMap, loginLock)

    if (null != pwdResult && pwdResult.msg.isNotBlank()) {
        return ApiResult.error(pwdResult.msg)
    }

    var expires: Pair<PwdExpires, String>? = null
    val loginUser: TenantLoginUser
    if (loginUsers.size > 1) {
        /* 3.查询有应用权限的并且是启用状态的所属租户 */
        val tenantList = mor.tenant.tenant.query()
            .where { it.id match_in loginUsers.map { it.tenant.id } }
            .where { it.isLocked match false }
            .orderByDesc { it.createAt }
            .toList()

        if (tenantList.isEmpty()) {
            return ApiResult.error("您没有登录该系统的权限")
        }

        if (tenantList.size > 1) {
            val loginCode = CodeUtil.getCode()
            rer.sys.loginCode(loginCode).set(LoginCodeData(loginField, loginName, CodeName(), tenantList.map { it.id }))
            return ApiResult.of(CodeModel("loginCode", loginCode, null, null, false, "","",null))
        }

        loginUser = loginUsers.filter { it.tenant.id == tenantList[0].id }.first()
    } else {
        loginUser = loginUsers[0]
//        expires = getExpire(loginUser.tenant.id, loginUser)
    }

    val user = mor.tenant.tenantUser.query().where { it.id match loginUser.userId }
        .toEntity() ?: return ApiResult.error("找不到用户{$loginName}")

    /* 非管理员用户不能登录租户侧 */
    if (user.adminType == TenantAdminTypeEnum.None ){
        return ApiResult.error("您没有登录该系统的权限")
    }

    val code = CodeUtil.getCode()
    val token = CodeUtil.getCode()

    rer.sys.oauthCode(code).set(
          OAuthCodeData(
            UserSystemTypeEnum.TenantAdmin,
            loginField,
            loginName,
            token,
            loginUser.userId,
            CodeName()
        ),
        tenantSettingMap["maxToken"].toString().toInt()
    )

    val tenantSecretSet = mor.tenant.tenantSecretSet.query().where { it.tenant.id match user.tenant.id }
        .toEntity()

    mor.tenant.tenantLoginUser.query().where { it.userId match loginUser.userId }.toEntity()
        .apply {
            if (this == null) {
                return ApiResult.error("找不到用户{$loginName}")
            }
            expires = getExpire(loginUser.tenant.id, loginUser)

            if (this.isFirstLogin && tenantSecretSet!!.setting.selfSetting.securityPolicy.firstLoginUpdatePassword) {
                return ApiResult.of(
                    CodeModel(
                        "oauthCode",
                        code,
                        expires?.first,
                        expires?.second,
                        true,
                        this.tenant.id,
                        "",
                        tenantSecretSet.setting.selfSetting.securityPolicy.expires
                    )
                )
            }
        }
    return ApiResult.of(
        CodeModel(
            "oauthCode",
            code,
            expires?.first,
            expires?.second,
            false,
            loginUser.tenant.id,
            "",
            tenantSecretSet!!.setting.selfSetting.securityPolicy.expires
        ))

}

fun TenantGroup.TenantUserEntity.getLoginUsers(
    loginName: String,
    loginField: String,
): ApiResult<MutableList<TenantLoginUser>> {
    var loginUsers = mor.tenant.tenantLoginUser.query()
        .whereOr({ it.loginName match loginName }, { it.mobile match loginName }, { it.email match loginName })
        .toList()


    /* 非管理员用户不能登录租户侧 */
    mor.tenant.tenantUser.query()
        .select { it.id }
        .where { it.id match_in loginUsers.map { it.userId } }
        .where { it.adminType match_not_equal TenantAdminTypeEnum.None }
        .toList(String::class.java)
        .apply {
            if (this.isEmpty()) {
                return ApiResult.error("您没有登录该系统的权限")
            }
            loginUsers = loginUsers.filter { it.userId in this }.toMutableList()
            if (loginUsers.isEmpty()) {
                return ApiResult.error("您没有登录该系统的权限")
            }
        }
    loginUsers=loginUsers.filter { it.enabled }.toMutableList()
    if (loginUsers.isEmpty()) {
        return ApiResult.error("管理员账号被停用")
    }

    return ApiResult.of(loginUsers)
}


private fun passwordVerification(
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
                return loginCheckFailed(loginName,loginUsers, tenantSettingMap, loginLock)
            }
        }
        // added this line by kxp at 2021.12.16 删除redis登录失败记录,同时更新用户登录记录
        loginCheckSuccess(loginName,loginUsers)
    }
    return null
}


// 登录失败时根据租户是否锁定该账户
private fun loginCheckFailed(
    loginName:String,
    loginUsers: MutableList<TenantLoginUser>,
    tenantSettingMap: Map<String, Any>,
    loginLock: LoginLock?
): LoginLock {
    val accounts=loginUsers.filter { it.loginName!=null && it.loginName.isNotEmpty() }.map { it.loginName }.toMutableSet()
    val mobiles= loginUsers.filter { it.mobile!=null && it.mobile.isNotEmpty() }.map { it.mobile }.toMutableSet()
    val emails= loginUsers.filter { it.email!=null && it.email.isNotEmpty() }.map { it.email }.toMutableSet()
    val maxCheckingPeriod = tenantSettingMap["maxCheckingPeriod"].toString().toInt()
    val manual = tenantSettingMap["manual"].toString().toBoolean()
    val maxRetry = tenantSettingMap["maxRetry"].toString().toInt()
    val maxDuration = tenantSettingMap["maxDuration"].toString().toInt()

    var expire = maxCheckingPeriod  // 登录验证统计周期，单位秒

    if (null == loginLock) {
        val result = LoginLock()
        if (manual) {
            expire = -1
        }

        if (maxRetry == 1) {
            result.lockTime = LocalDateTime.now()
            result.count = maxRetry
            result.msg = "第1次输入错误，剩余0次，账号已被锁定，请{$maxDuration}分钟后再试！"
            result.strongLock=true
        } else {
            result.msg = "第1次输入错误，剩余{" + (maxRetry - 1).toString() + "}次，请输入正确的用户名或密码！"
        }


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
        if(maxRetry==1){
            result.msg="错误密码输入次数过多，请稍后再试。"
        }

        return result
    } else {
        val tryCount: Int = loginLock.count + 1
        var cacheExpire = rer.iamUser.errorLogin(loginName).getExpireSeconds()
        if (manual) {
            cacheExpire = -1
        }

        if (manual) {
            cacheExpire = -1
        }
        if (tryCount >= maxRetry) {

            loginLock.count = tryCount
            loginLock.lockTime = LocalDateTime.now()
            loginLock.strongLock=true
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
            loginLock.msg="错误密码输入次数过多，请稍后再试。"
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
        if(it.loginName!=null && it.loginName.isNotEmpty()){
            rer.iamUser.errorLogin("it.loginName").deleteKey()
        }
        if(it.mobile!=null && it.mobile.isNotEmpty()){
            rer.iamUser.errorLogin(it.mobile).deleteKey()
        }
        if(it.email!=null && it.email.isNotEmpty()){
            rer.iamUser.errorLogin(it.email).deleteKey()
        }
    }
    mor.tenant.tenantLoginUser.update()
        .apply {
            if(email.containsMatchIn(loginName)){
                this .where { it.email match loginName }
            }else if(mobile.containsMatchIn(loginName)){
                this.where{it.mobile match loginName}
            }else{
                this.where{it.loginName match  loginName}
            }
        }
        .set { it.isLocked to false }
        .set { it.lastLoginAt to LocalDateTime.now() }
        .set { it.errorLoginTimes to 0 }.exec()


}


fun getSetting(loginName: String): Map<String, Any> {
    getAllTenantUsers(loginName).apply {
        if (this.isEmpty()) {
            throw RuntimeException("找不到用户信息")
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

                // TODO 租户中最长被锁定时持续时间
                val maxDuration =
                    this.map { it.setting.selfSetting?.loginChecking ?: LoginChecking() }.toList()
                        .maxByOrNull { it.lockDuration }!!.lockDuration
                // 租户中最短被锁定时持续时间, 只要有一个是自动的，就按自动解锁的算
                var manual = true
                val falseSize =
                    this.map { it.setting.selfSetting?.loginChecking?.manual ?: false }.toList().filter { !it }.size
                if (falseSize > 0)
                    manual = false

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


fun getAllTenantUsers(loginName: String): MutableList<TenantUser> {
    val loginUsers = mor.tenant.tenantLoginUser.query()
        .whereOr({ it.loginName match loginName }, { it.mobile match loginName }, { it.email match loginName })
        .toList()
    return mor.tenant.tenantUser.query()
        .where { it.tenant.id match_in loginUsers.map { it.tenant.id } }
        .where{it.adminType match_not_equal  TenantAdminTypeEnum.None}
        .toList()
}


private fun checkLoginLock(
    loginName: String,
    tenantSettingMap: Map<String, Any>
): LoginLock? {

    val loginLock = rer.iamUser.errorLogin(loginName).get().FromJson(LoginLock::class.java)
    if (null != loginLock) {
        loginLock.msg = ""
        val ttl = rer.iamUser.errorLogin(loginName).getExpireSeconds()
        if (ttl == -1) { // 被锁定
            loginLock.msg = "您所登录的账户已被锁定，请联系管理员"
        }

        //DateUtils.getDaysBetweenMinute(loginLock.lockTime!!, LocalDateTime.now())
/*        if (ttl > -1) { // 且重试次数等于租户的最大重试次数
            //判断是否由于多租户登录方式换成单用户登录方式导致账户强制锁定
            if(loginLock.strongLock){
                loginLock.msg = "由于更换账号登录，次数超限，账号已被锁定，请 " +
                "${
                    tenantSettingMap["minDuration"].toString().toInt()
                        .minus(DateUtils.getDaysBetweenMinute(loginLock.lockTime!!, LocalDateTime.now()))
                }"+
                        "分钟后再试！"
                return loginLock
            }
            if (loginLock.count >= tenantSettingMap["maxRetry"].toString().toInt()
                && tenantSettingMap["minDuration"].toString().toInt()
                    .minus(DateUtils.getDaysBetweenMinute(loginLock.lockTime!!, LocalDateTime.now())) > 0
            ) {
                loginLock.msg = "您已连续 ${loginLock.count} 次输入错误，账号已被锁定，请 " +
                    "${
                        tenantSettingMap["minDuration"].toString().toInt()
                            .minus(DateUtils.getDaysBetweenMinute(loginLock.lockTime!!, LocalDateTime.now()))
                    } " +
                    "分钟后再试！"
            }
        }*/
        if(ttl>-1) {
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


class CodeModel(
    var type: String,
    var code: String,
    var expires: PwdExpires?,
    var daysLeft: String?,
    var isFirstLogin: Boolean?,
    var tenantId: String?,
    var uuid: String,
    var forceExpires : Boolean?
)
