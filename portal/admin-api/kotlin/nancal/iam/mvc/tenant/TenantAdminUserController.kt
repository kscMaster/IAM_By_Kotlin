package nancal.iam.mvc.tenant

import com.nancal.cipher.SHA256Util
import io.swagger.annotations.*
import nancal.iam.annotation.BizLog
import org.springframework.data.mongodb.core.query.*
import org.springframework.web.bind.annotation.*
import nancal.iam.base.extend.*
import nancal.iam.client.MPClient
import nancal.iam.comm.logMsg
import nbcp.comm.*
import nbcp.db.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.util.*
import nbcp.utils.Md5Util
import nbcp.base.mvc.*
import nbcp.web.*
import org.bson.Document
import org.springframework.beans.factory.annotation.Value
import javax.servlet.http.*
import java.time.*
import java.util.*
import javax.annotation.Resource

/**
 * Created by CodeGenerator at 2021-11-17 15:48:36
 */
@Api(description = "BOSS用户", tags = arrayOf("AdminUser"))
@RestController
@RequestMapping("/tenant/tenant-admin-user")
class TenantUserAutoController {

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        name: String,
        mobile: String,
        email: String,
        adminType: String,
        enabled: Boolean?,
        loginName: String,
        tenantName: String,
        tenantId: String,
        keywords: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantUser> {

        mor.tenant.tenantUser.query()
            .where { it.adminType match_not_equal TenantAdminTypeEnum.None }
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (keywords.HasValue) {
                    this.whereOr(
                        { it.name match_like keywords },
                        { it.tenant.name match_like keywords },
                        { it.mobile match_like keywords },
                        { it.loginName match_like keywords },
                        { it.email match_like keywords }
                    )
                }
                if (name.HasValue) {
                    this.where { it.name match_like name }
                }
                if (tenantName.HasValue) {
                    this.where { it.tenant.name match_like tenantName }
                }
                if (mobile.HasValue) {
                    this.where { it.mobile match_like mobile }
                }
                if (email.HasValue) {
                    this.where { it.email match_like email }
                }
                if (loginName.HasValue) {
                    this.where { it.loginName match_like loginName }
                }
                if (adminType.HasValue) {
                    this.where { it.adminType match adminType }
                }
                if (enabled != null) {
                    this.where { it.enabled match enabled }
                }
                if (tenantId.HasValue) {
                    this.where { it.tenant.id match tenantId }
                }
            }
            .limit(skip, take)
            .orderByDesc { it.createAt }
            .toListResult()
            .apply {
                return this
            }
    }

    @ApiOperation("详情")
    @PostMapping("/detail/{id}")
    fun detail(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<TenantUser> {
        mor.tenant.tenantUser.queryById(id)
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }
                return ApiResult.of(this)
            }
    }


    @BizLog(BizLogActionEnum.Save, BizLogResourceEnum.Admin, "管理员账号")
    @ApiOperation("更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: Document,
        request: HttpServletRequest
    ): ApiResult<String> {
        val tenantUser = entity.ConvertJson(TenantUser::class.java)

        if(!tenantUser.tenant.id.HasValue){
            return ApiResult.error("租户ID不能为空")
        }

        mor.tenant.tenant.queryById(tenantUser.tenant.id).toEntity()
            .apply {
                if(this == null){
                    return ApiResult.error("租户不存在")
                }
                tenantUser.tenant = IdName(tenantUser.tenant.id,this.name)
            }

        val name = if (tenantUser.name.HasValue) tenantUser.name else tenantUser.loginName
        if (tenantUser.id.HasValue) {
            request.logMsg = "修改管理员{$name}"
        } else {
            request.logMsg = "创建管理员{$name}"
            entity.put("enabled",true)
        }

        val validParam = validParam(tenantUser)
        if (validParam.msg.HasValue) {
            return validParam
        }

        // 如果不传，部分字段给默认值
        entity.put("tenant", tenantUser.tenant)
        entity.put("sendPasswordType", tenantUser.sendPasswordType)
        entity.put("userAdminType", tenantUser.adminType)
        entity.put("IdentityCardData", tenantUser.identityCard)

        var isInsert = false
        mor.tenant.tenantUser.updateWithEntity(entity)
            
            .run {
                if (tenantUser.id.HasValue) {

                    if (tenantUser.adminType == TenantAdminTypeEnum.None) {
                        val exists = mor.tenant.tenantUser.query()
                            .where { it.tenant.id match tenantUser.tenant.id }
                            .where { it.id match_not_equal tenantUser.id }
                            .where { it.adminType match_not_equal TenantAdminTypeEnum.None }
                            .exists()
                        if (!exists) {
                            return ApiResult.error("保存失败,至少需要保留一个管理员")
                        }
                    }
                    return@run this.execUpdate()
                } else {
                    isInsert = true
                    return@run this.execInsert()
                }
            }
            .apply {
                if (this == 0) {
                    return ApiResult.error("保存失败")
                }
                if (isInsert) {
                    val loginObj = TenantLoginUser()
                    loginObj.loginName = entity.get("loginName").AsString()
                    loginObj.email = entity.get("email").AsString()
                    loginObj.isLocked = false
                    loginObj.lockedRemark = ""
                    loginObj.mobile = entity.get("mobile").AsString()
                    loginObj.tenant = entity.get("tenant") as IdName

                    var pwd = UUID.randomUUID().toString().replace("-", "").substring(0, 6)
                    var salt = UUID.randomUUID().toString().replace("-", "").substring(0, 4)
                    loginObj.passwordSalt = salt
                    if(tenantUser.sendPasswordType == SendPasswordType.Defined){
                        pwd = entity.get("password").AsString()

                        if(tenantUser.sendPasswordType == SendPasswordType.Defined){
                            if(pwd.equals("")){
                                return ApiResult.error("密码不能为空")
                            }
                        }

                        loginObj.password = SHA256Util.getSHA256StrJava(pwd + salt)
                    }else {
                        loginObj.password = SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(pwd) + salt)
                    }
                    loginObj.userId = entity.get("_id").AsString()

                    mor.tenant.tenantLoginUser.updateWithEntity(loginObj)
                        
                        .run {
                            return@run this.execInsert()
                        }.apply {
                            if (this == 0) {
                                return ApiResult.error("新增loginAdminUser失败")
                            }
                            if (tenantUser.isSendPassword) {
                                sendPassword(tenantUser, pwd, request)
                            }
                        }
                } else {
                    if (tenantUser.mobile.HasValue) {
                        mor.tenant.tenantLoginUser.update()
                            .where { it.userId match tenantUser.id }
                            .set { it.mobile to tenantUser.mobile }.exec()
                        if (mor.affectRowCount < 1) {
                            return ApiResult.error("修改登录信息失败")
                        }
                    }
                    if (tenantUser.email.HasValue) {
                        mor.tenant.tenantLoginUser.update()
                            .where { it.userId match tenantUser.id }
                            .set { it.email to tenantUser.email }.exec()
                        if (mor.affectRowCount < 1) {
                            return ApiResult.error("修改登录信息邮箱失败")
                        }
                    }
                }
            }
        return ApiResult.of(entity.get("_id").AsString())
    }

    /* 发送密码 */
    fun sendPassword(tenantAdminUser: TenantUser, pwd: String, request: HttpServletRequest) {
        if (tenantAdminUser.sendPasswordType == SendPasswordType.Mobile) {
            val sendSmsCode = mpClient.sendSmsPwd(MobileCodeModuleEnum.SendPassword, tenantAdminUser.mobile, pwd)
            val success = sendSmsCode.success()
            println(success.toString())
        } else if (tenantAdminUser.sendPasswordType == SendPasswordType.Email) {
            val msg = EmailMessage()
            msg.sender = mailSender
            msg.password = mailPwd
            msg.addressee = listOf(tenantAdminUser.email)
            // TODO 国际化 短信密码中英文切换
            if (request.getHeader("lang") == "en") {
                msg.topic = "[Nancal Ruiyuan] Login password"
                msg.content = "Hello，${tenantAdminUser.loginName}<br />" +
                        "<p style=\"text-indent:2em\">your login password is：$pwd 。Please do not share</p><br />" +
                        "<br>your password with anyone for the sake of your account security.</br>"
            } else {
                msg.topic = "【能科瑞元】登录密码"
                msg.content = "您好，${tenantAdminUser.loginName}<br />" +
                        "<p style=\"text-indent:2em\">您的登录密码为：$pwd 。为了您账户的</p><br />" +
                        "<br>安全请勿将密码告知他人。</br>"
            }

            msg.popService = mailPop
            msg.smtpService = mailSmtp
            mailUtil.sendEmail(msg)
        }
    }

    /* 验证参数 */
    fun validParam(tenantAdminUser: TenantUser): ApiResult<String> {
        //loginName 不能是Mobile，不能是 email格式
        var matchPhoneParttern =
            "^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$"
        var matchEmailParttern = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$"

        val isPhoneMatch = Regex(matchPhoneParttern).containsMatchIn(tenantAdminUser.loginName)
        if (isPhoneMatch) {
            return ApiResult.error("loginName不能是手机号格式")
        }
        val isEmailMatch = Regex(matchEmailParttern).containsMatchIn(tenantAdminUser.loginName)
        if (isEmailMatch) {
            return ApiResult.error("loginName不能是邮箱格式")
        }


        if (!tenantAdminUser.loginName.HasValue ) {
            return ApiResult.error("请填写管理员用户名")
        }
        if ( tenantAdminUser.loginName.length < 6 ) {
            return ApiResult.error("管理员用户名至少6个字符")
        }
        if ( tenantAdminUser.loginName.length >32) {
            return ApiResult.error("管理员用户名不能超过32个字符")
        }

        if(tenantAdminUser.remark.HasValue && tenantAdminUser.remark.length >255){
            return ApiResult.error("备注不能超过255个字符")
        }
        if (!tenantAdminUser.name.HasValue) {
            return ApiResult.error("请填写管理员姓名")
        }
        if ( tenantAdminUser.name.length < 2 ) {
            return ApiResult.error("管理员姓名至少2个字符")
        }
        if ( tenantAdminUser.name.length >32) {
            return ApiResult.error("管理员姓名不能超过32个字符")
        }


        var existLoginName = mor.tenant.tenantUser.query()
            .where { it.loginName match tenantAdminUser.loginName }
            .exists()
        if (existLoginName) {
            //如果是修改，需要保证除当前组外，其他组没有该组名
            if (tenantAdminUser.id.HasValue) {
                val exists1 = mor.tenant.tenantUser.query()
                    .where { it.loginName match tenantAdminUser.loginName }
                    .where { it.id match_not_equal tenantAdminUser.id }.exists()
                if (exists1) {
                    return ApiResult.error("登录名已存在")
                }
            } else {// 新增
                return ApiResult.error("登录名已存在")
            }
        }
        if (tenantAdminUser.sendPasswordType == null) {
            return ApiResult.error("请选择密码发送方式")
        }
        if (tenantAdminUser.sendPasswordType == SendPasswordType.Email) {
            if (!tenantAdminUser.email.HasValue) {
                return ApiResult.error("请填写邮箱")
            }
        }

        if (tenantAdminUser.email.HasValue) {
            var isEmailMatch = Regex(matchEmailParttern).containsMatchIn(tenantAdminUser.email)
            if (!isEmailMatch) {
                return ApiResult.error("邮箱格式不正确")
            }

            // 同一租户下邮箱称不能重复
            var existEmail = mor.tenant.tenantUser.query()
                .where { it.tenant.id match tenantAdminUser.tenant.id }
                .where { it.email match tenantAdminUser.email }
                .exists()
            if (existEmail) {
                //如果是修改，需要保证除当前组外，其他组没有该组名
                if (tenantAdminUser.id.HasValue) {
                    val exists1 = mor.tenant.tenantUser.query()
                        .where { it.tenant.id match tenantAdminUser.tenant.id }
                        .where { it.email match tenantAdminUser.email }
                        .where { it.id match_not_equal tenantAdminUser.id }.exists()
                    if (exists1) {
                        return ApiResult.error("邮箱已存在")
                    }
                } else {// 新增
                    return ApiResult.error("邮箱已存在")
                }
            }
        }
        if (tenantAdminUser.sendPasswordType == SendPasswordType.Mobile) {
            if (!tenantAdminUser.mobile.HasValue) {
                return ApiResult.error("手机号不能为空")
            }
        }
        if (tenantAdminUser.mobile.HasValue) {
            var isPhoneMatch = Regex(matchPhoneParttern).containsMatchIn(tenantAdminUser.mobile)
            if (!isPhoneMatch) {
                return ApiResult.error("手机格式不正确")
            }
            // 同一租户下手机不能重复
            var existMobile = mor.tenant.tenantUser.query()
                .where { it.tenant.id match tenantAdminUser.tenant.id }
                .where { it.mobile match tenantAdminUser.mobile }
                .exists()
            if (existMobile) {
                //如果是修改，需要保证除当前组外，其他组没有该组名
                if (tenantAdminUser.id.HasValue) {
                    val exists1 = mor.tenant.tenantUser.query()
                        .where { it.tenant.id match tenantAdminUser.tenant.id }
                        .where { it.mobile match tenantAdminUser.mobile }
                        .where { it.id match_not_equal tenantAdminUser.id }.exists()
                    if (exists1) {
                        return ApiResult.error("手机号已存在")
                    }
                } else {// 新增
                    return ApiResult.error("手机号已存在")
                }
            }
        }
        return ApiResult()
    }


//    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.Admin, "管理员账号")
//    @ApiOperation("删除")
//    @PostMapping("/delete/{id}")
//    fun delete(
//        @Require id: String,
//        request: HttpServletRequest
//    ): JsonResult {
//        val tenantAdminUser = mor.tenant.tenantUser.queryById(id).toEntity() ?: return JsonResult.error("找不到数据")
//        request.logMsg = "删除管理员[${tenantAdminUser.name}]"
//
//        val loginUser = mor.tenant.tenantLoginUser.queryByUserId(id).toEntity()!!
//        if (loginUser.enabled) {
//            return JsonResult.error("启用状态不允许删除")
//        }
//        mor.tenant.tenantUser.deleteById(id)
//            .exec()
//            .apply {
//                if (this == 0) {
//                    return JsonResult.error("删除失败")
//                }
//                mor.tenant.tenantLoginUser.delete()
//                    .where { it.userId match id }.exec()
//                //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
//                return JsonResult()
//            }
//    }


    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.Admin, "管理员账号")
    @ApiOperation("批量删除")
    @PostMapping("/delete/batch")
    fun deleteBatch(
        @Require ids: List<String>,
        request: HttpServletRequest
    ): JsonResult {
        if (ids.size < 1) {
            return JsonResult.error("请选择删除数据")
        }
        val names = mor.tenant.tenantUser.query().select { it.name }.where { it.id match_in ids }
            .toList(String::class.java)
        request.logMsg = "批量删除管理员{$names}"

        var user = mor.tenant.tenantUser.query().where { it.id match ids[0] }.toEntity()

        if (user == null) {
            return JsonResult.error("未查询到要删除的数据")
        }
        val allTenantUserList = mor.tenant.tenantUser.query()
            .where { it.adminType match_not_equal TenantAdminTypeEnum.None }
            .where { it.tenant.id match user.tenant.id }
            .toList()

        val deleteTenantUserList = mor.tenant.tenantUser.query()
            .where { it.adminType match_not_equal TenantAdminTypeEnum.None }
            .where { it.id match_in ids }
            .where { it.enabled match false }.toList()


        val exists = mor.tenant.tenantUser.query()
            .where { it.adminType match_not_equal  TenantAdminTypeEnum.None }
            .where { it.tenant.id match user.tenant.id }
            .where { it.id.match_notin(deleteTenantUserList.map { it.id }) }.exists()
        if (!exists) {
            return JsonResult.error("至少需要保留一个管理员")
        }

        if (allTenantUserList.size == deleteTenantUserList.size) {
            return JsonResult.error("至少需要保留一个管理员")
        }
        //鉴权
        mor.tenant.tenantUser.delete()
            .where { it.id match_in ids }
            .where { it.enabled match false }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
                mor.tenant.tenantLoginUser.delete()
                    .where { it.userId match_in ids }.exec()
            }
        //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
        return JsonResult()
    }


    @BizLog(BizLogActionEnum.UpdatePassword, BizLogResourceEnum.Admin, "平台账号")
    @ApiOperation("修改密码")
    @PostMapping("/updateAdminPassword")
    fun updatePassword(
        @Require id: String,
        @Require oldPassword: String,
        @Require newPassword: String,
        request: HttpServletRequest
    ): JsonResult {

        if (oldPassword == newPassword) {
            return JsonResult.error("新密码不能与旧密码一致")
        }
        val user = mor.admin.adminUser.query().where { it.id match id }.toEntity()
        val userLogin = mor.admin.adminLoginUser.query().where { it.userId match id }.toEntity()
        if (user == null||userLogin ==null) {
            return JsonResult.error("管理员未找到")
        }
        // 校验密码格式是否正确
        if (ValidateUtils.containerSpace(newPassword) || ValidateUtils.isContainChinese(newPassword)) {
            return JsonResult.error("密码包含非法字符")
        }
        mor.admin.adminLoginUser.query()
            .where { it.userId match id }
            .where { it.password match SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(oldPassword) + userLogin.passwordSalt) }.toEntity()
            .apply {
                if (this == null) {
                    return JsonResult.error("原密码不正确")
                }
            }
        mor.admin.adminLoginUser.update()
            .where { it.userId match id }
            .set { it.password to SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(newPassword) + userLogin.passwordSalt) }.exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("修改失败")
                }
                request.logMsg = "修改平台管理员{${user.name}}密码"
            }
        return JsonResult()
        //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
    }

    @BizLog(BizLogActionEnum.UpdatePassword, BizLogResourceEnum.Admin, "管理员账号")
    @ApiOperation("修改密码")
    @PostMapping("/updateTenantAdminPassword")
    fun updateTenantAdminPassword(
        @Require id: String,
        @Require oldPassword: String,
        @Require newPassword: String,
        request: HttpServletRequest
    ): JsonResult {
        if (oldPassword == newPassword) {
            return JsonResult.error("新密码不能与旧密码一致")
        }
        val user =
            mor.tenant.tenantUser.query().where { it.id match id }
                .toEntity() ?: return JsonResult.error("用户未找到")

        val userLogin =
            mor.tenant.tenantLoginUser.query().where { it.userId match id }
                .toEntity() ?: return JsonResult.error("用户未找到")

        // 校验密码格式是否正确
        if (ValidateUtils.containerSpace(newPassword) || ValidateUtils.isContainChinese(newPassword)) {
            return JsonResult.error("密码包含非法字符")
        }
        // 判断租户是否正常
        val tenant = mor.tenant.tenant.query()
            .where { it.id match user.tenant.id }
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

        mor.tenant.tenantLoginUser.query()
            .where { it.userId match id }
            .where { it.password match SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(oldPassword) + userLogin.passwordSalt) }.toEntity()
            .apply {

                if (this == null) {
                    return JsonResult.error("原密码不正确")
                }
            }
        mor.tenant.tenantLoginUser.update()
            .where { it.userId match id }
            .set { it.password to SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(newPassword) + userLogin.passwordSalt) }
            .set { it.lastUpdatePwdAt to LocalDateTime.now() }
            .set { it.manualRemindPwdTimes to 0 }
            .set { it.manualExpirePwdTimes to 0 }
            .set { it.autoExpirePwdTimes to 0 }
            .set { it.autoRemindPwdTimes to 0 }
            .exec()
        request.logMsg = "修改管理员{${user.name}}密码"
        return JsonResult()
        //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
    }



    @ApiOperation("详情")
    @PostMapping("/detail/getByLoginName")
    fun getByLoginName(
        @Require loginName: String,
        request: HttpServletRequest
    ): ApiResult<Document> {

        mor.tenant.tenantUser.query().where { it.loginName match loginName }
            .toEntity(Document::class.java)
            .apply {
                if (this == null) {
                    return ApiResult.error<Document>("找不到数据")
                }
                return ApiResult.of(this)
            }
    }


    @Value("\${mail.sender}")
    val mailSender: String = ""

    @Value("\${mail.pwd}")
    val mailPwd: String = ""

    @Value("\${mail.smtp}")
    val mailSmtp: String = ""

    @Value("\${mail.pop}")
    val mailPop: String = ""

    @Resource
    lateinit var mailUtil: MailUtil

    @Resource
    lateinit var mpClient: MPClient


    @BizLog(BizLogActionEnum.ResetPassword, BizLogResourceEnum.Admin, "管理员账号")
    @PostMapping("/reset-pwd")
    fun resetPwd(
        @Require userId: String,
        @Require sendPasswordType: SendPasswordType,
        password: String,
        request: HttpServletRequest
    ): JsonResult {

        request.logMsg = "重置管理员密码"

        val user = mor.tenant.tenantLoginUser.query().where {
            it.userId match userId
        }.toEntity()
        if (user == null) {
            return JsonResult.error("用户不存在")
        }

        var pwd = UUID.randomUUID().toString().replace("-", "").substring(0, 6)

        val userEntity = mor.tenant.tenantUser.query().where {
            it.id match userId
        }.toEntity()

        if (userEntity == null) {
            return JsonResult.error("用户不存在")
        } else {
            if (sendPasswordType== null) {
                return JsonResult.error("该用户未指定发送密码类型")
            }
            if (sendPasswordType == SendPasswordType.Email) {
                if(!userEntity.email.HasValue){
                    return JsonResult.error("该用户未绑定邮箱")
                }
            }
            if (sendPasswordType == SendPasswordType.Mobile) {
                if(!userEntity.mobile.HasValue){
                    return JsonResult.error("该用户未绑定手机")
                }
            }
        }

        if(sendPasswordType == SendPasswordType.Defined){
            pwd = password
            if(pwd.trim().equals("")){
                return JsonResult.error("密码不能为空")
            }
            mor.tenant.tenantLoginUser.updateByUserId(userId)
                .set { it.password to SHA256Util.getSHA256StrJava(pwd + user.passwordSalt) }
                .set { it.lastUpdatePwdAt to LocalDateTime.now() }
                .set { it.manualRemindPwdTimes to 0 }
                .set { it.manualExpirePwdTimes to 0 }
                .set { it.autoExpirePwdTimes to 0 }
                .set { it.autoRemindPwdTimes to 0 }
                .exec()
        }else {
            mor.tenant.tenantLoginUser.updateByUserId(userId)
                .set { it.password to SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(pwd) + user.passwordSalt) }
                .set { it.lastUpdatePwdAt to LocalDateTime.now() }
                .set { it.manualRemindPwdTimes to 0 }
                .set { it.manualExpirePwdTimes to 0 }
                .set { it.autoExpirePwdTimes to 0 }
                .set { it.autoRemindPwdTimes to 0 }
                .exec()
        }

        if (sendPasswordType == SendPasswordType.Mobile) {
            val sendSmsCode = mpClient.sendSmsPwd(MobileCodeModuleEnum.SendPassword, userEntity.mobile, pwd)
            val success = sendSmsCode.success()
            println(success.toString())
        } else if (sendPasswordType == SendPasswordType.Email) {
            val msg = EmailMessage()
            msg.sender = mailSender
            msg.password = mailPwd
            msg.addressee = listOf(userEntity.email)
            // TODO 邮件国际化
            if (request.getHeader("lang") == "en") {
                msg.topic = "[Nancal Ruiyuan] Login password"
                msg.content = "Hello，${userEntity.loginName}<br />" +
                        "<p style=\"text-indent:2em\">your login password is：$pwd 。Please do not share</p><br />" +
                        "<br>your password with anyone for the sake of your account security.</br>"
            } else {
                msg.topic = "【能科瑞元】登录密码"
                msg.content = "您好，${userEntity.loginName}<br />" +
                        "<p style=\"text-indent:2em\">您的登录密码为：$pwd 。为了您账户的</p><br />"+
                        "<br>安全请勿将密码告知他人。</br>"
            }
            msg.popService = mailPop
            msg.smtpService = mailSmtp
            mailUtil.sendEmail(msg)
        }
        return JsonResult()
    }

    @BizLog(BizLogActionEnum.UpOrDownLocation, BizLogResourceEnum.Admin, "管理员账号上下移")
    @ApiOperation("用户上下移置顶")
    @PostMapping("/move")
    fun move(
        @Require ids: List<String>,
        @Require type: String,
        request: HttpServletRequest
    ): JsonResult {

        val users = mor.tenant.tenantUser.query().where {
            it.id match_in ids
        }.toList(IdNameSort::class.java)
        if (users.size != 2) {
            JsonResult.error("未查询到移动对象")
        }
        if (type == MoveType.UpDown.toString()) {//上下移动
            mor.tenant.tenantUser.update().where {
                it.id match users[0].id
            }.set { it.sort to users[1].sort }.exec().apply {
                if (this == 0) {
                    return JsonResult.error("移动失败")
                }
                request.logMsg = "管理员{${users.get(0).name}}上下移"
            }
            mor.tenant.tenantUser.update().where {
                it.id match users[1].id
            }.set { it.sort to users[0].sort }.exec().apply {
                if (this == 0) {
                    return JsonResult.error("移动失败")
                }
                request.logMsg = "管理员{${users.get(1).name}}上下移"
            }
        } else if (type == MoveType.Top.toString()) {// 置顶
            if (users[0].sort >= users[1].sort) {
                mor.tenant.tenantUser.update().where {
                    it.id match users[0].id
                }.set { it.sort to users[1].sort - 0.1F }.exec().apply {
                    if (this == 0) {
                        return JsonResult.error("置顶失败")
                    }
                    request.logMsg = "管理员{${users.get(0).name}}置顶"
                }
            } else {
                mor.tenant.tenantUser.update().where {
                    it.id match users[1].id
                }.set { it.sort to users[0].sort - 0.1F }.exec().apply {
                    if (this == 0) {
                        return JsonResult.error("置顶失败")
                    }
                    request.logMsg = "管理员{${users.get(1).name}}置顶"
                }
            }
        } else {
            return JsonResult.error("移动类型不合法")
        }
        return JsonResult()
    }

    class IdNameSort {
        var id: String = ""
        var sort: Float = 0F
        var name = ""
    }


    @BizLog(BizLogActionEnum.Enable, BizLogResourceEnum.Admin, "管理员账号启用")
    @ApiOperation("管理员启用")
    @PostMapping("/enable")
    fun enabled(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        val enabled=true
        //鉴权
        mor.tenant.tenantLoginUser.query()
            .where { it.userId match id }
            .toEntity()
            .apply {
                if(this == null){
                    return JsonResult.error("用户不存在")
                }
                val hasUser=mor.tenant.tenantUser.query()
                    .where { it.id match this.userId }
                    .where { it.adminType match   TenantAdminTypeEnum.None }
                    .exists()
                if(hasUser){
                    return JsonResult.error("不可以操作非管理员")
                }


                if(this.enabled == enabled){
                    return JsonResult.error("用户已是启用状态")
                }
            }
        mor.tenant.tenantUser.updateById(id)
            .set { it.enabled to enabled }
            .exec()

        mor.tenant.tenantLoginUser.update()
            .where { it.userId match id }
            .set { it.enabled to enabled }
            .exec()

        mor.tenant.tenantUser.queryById(id).toEntity()
            ?.apply {
                val msg = "启用"
                request.logMsg = "管理员{${this.name}}"+msg
            }
        return JsonResult()
    }
    @BizLog(BizLogActionEnum.Disable, BizLogResourceEnum.Admin, "管理员账号停用")
    @ApiOperation("用户停用")
    @PostMapping("/disable")
    fun disabled(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        val enabled=false
        //鉴权
        mor.tenant.tenantLoginUser.query()
            .where { it.userId match id }
            .toEntity()
            .apply {
                if(this == null){
                    return JsonResult.error("用户不存在")
                }
                val hasUser=mor.tenant.tenantUser.query()
                    .where { it.id match this.userId }
                    .where { it.adminType match   TenantAdminTypeEnum.None }
                    .exists()
                if(hasUser){
                    return JsonResult.error("不可以操作非管理员")
                }
                if(this.enabled == enabled){
                    return JsonResult.error("用户已是停用状态")

                }
            }
        mor.tenant.tenantUser.updateById(id)
            .set { it.enabled to enabled }
            .exec()

        mor.tenant.tenantLoginUser.update()
            .where { it.userId match id }
            .set { it.enabled to enabled }
            .exec()

        mor.tenant.tenantUser.queryById(id).toEntity()
            ?.apply {
                val msg = "停用"
                request.logMsg = "管理员{${this.name}}"+msg
            }
        return JsonResult()
    }


    @BizLog(BizLogActionEnum.Update, BizLogResourceEnum.Admin, "管理员账号修改头像")
    @ApiOperation("修改头像")
    @PostMapping("/updateHeadPicture")
    fun updateHeadPicture(
        @Require userId: String,
        @Require pictureId: String,
        @Require url: String,
        request: HttpServletRequest
    ): JsonResult {

        val user = mor.tenant.tenantUser.query()
            .where { it.id match userId }
            .toEntity()
        if (user == null) {
            return JsonResult.error("用户未找到")
        }

        mor.tenant.tenantUser.updateById(userId)
            .set { it.logo.id to pictureId }
            .set { it.logo.url to url }
            .exec()
        if (mor.affectRowCount < 1) {
            return JsonResult.error("修改头像失败")
        }
        request.logMsg = "管理员{${user.name}}修改头像"
        return JsonResult()
    }


}

