package nancal.iam.mvc

import nancal.iam.client.MPClient
import nancal.iam.db.mongo.MobileCodeModuleEnum
import nancal.iam.db.mongo.SendPasswordType
import nancal.iam.db.mongo.entity.TenantLoginUser
import nancal.iam.db.mongo.mor
import nancal.iam.db.redis.rer
import nancal.iam.util.EmailMessage
import nancal.iam.util.MailUtil
import nancal.iam.util.ValidateUtils
import nbcp.comm.*
import nbcp.db.LoginUserModel
import nbcp.db.mongo.match
import nbcp.db.mongo.query
import nbcp.db.mongo.updateWithEntity
import nbcp.utils.MyUtil
import nbcp.web.LoginUser
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@OpenAction
@RestController
@RequestMapping("/oauth/msg")
class ResetMessageController {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    @Resource
    lateinit var mpClient: MPClient

    @Resource
    lateinit var mailUtil: MailUtil

    @Value("\${mail.sender}")
    lateinit var mailSender: String

    @Value("\${mail.pwd}")
    lateinit var mailPwd: String

    @Value("\${mail.smtp}")
    lateinit var mailSmtp: String

    @Value("\${mail.pop}")
    lateinit var mailPop: String

    /**
     * 发送短信或邮件验证码-乐造os
     */
    @PostMapping("/sendCode")
    fun sendCode(
        @Require moduleType: MobileCodeModuleEnum,
        @Require type: SendPasswordType,
        @Require contact: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ApiResult<String> {
        val loginUser: LoginUserModel = request.LoginUser
        // 检测手机号码是否存在、状态是否正常，避免短信浪费
        val validateParam: String = validateParam(moduleType, type, contact, request)
        if (validateParam.HasValue) {
            return ApiResult.error(validateParam)
        }
        val uuid = UUID.randomUUID().toString().replace("-", "");

        if (type == SendPasswordType.Mobile) {
            if (!ValidateUtils.checkPhoneNumber(contact)) {
                return ApiResult.error("请输入正确的手机号")
            }
            rer.sys.smsResetCode(uuid).set(contact)

            mpClient.sendSmsCode(moduleType, contact)
                .apply {
                    logger.info("call msg return ${this.toString()}")
                    return if (this.code == 0) {
                        ApiResult.of(uuid)
                    } else ApiResult.error(this.msg.toString())
                }
        }

        // 校验邮件格式
        if (!ValidateUtils.checkEmail(contact)) {
            return ApiResult.error("请输入正确的邮箱")
        }
        val code = MyUtil.getRandomWithLength(4)
        // 发送
        val msg = EmailMessage()
        // XXXXX！您正在绑定邮箱，验证码为：2345.
        msg.sender = mailSender
        msg.password = mailPwd
        msg.addressee = listOf(contact)
        if (request.getHeader("lang") == "en") {
            msg.topic = "【LeVault IAM】 Modify mailbox"
            msg.content = "${loginUser.name}!You are binding mailbox, and the verification code is：$code。"
        } else {
            msg.topic = "【乐仓统一身份认证系统】修改邮箱"
            msg.content = "${loginUser.name}!您正在绑定邮箱，验证码为：$code。"
        }

        msg.popService = mailPop
        msg.smtpService = mailSmtp
        mailUtil.sendEmail(msg)
        // 验证码存入redis
        rer.sys.smsResetCode("${moduleType.name}-$contact").set(code)
        rer.sys.smsResetCode(uuid).set(contact)
        return ApiResult.of(uuid)
    }

    fun validateParam(
        moduleType: MobileCodeModuleEnum,
        type: SendPasswordType,
        contact: String,
        request: HttpServletRequest
    ) : String{
        val loginUser: LoginUserModel = request.LoginUser
        val tenantLoginUser: TenantLoginUser = mor.tenant.tenantLoginUser.query()
            .where { it.userId match loginUser.id }.toEntity() ?: return "用户不存在"
        val tenantId = tenantLoginUser.tenant.id

        val flag = mor.tenant.tenantUser.query()
            .where { it.tenant.id match tenantId }
            .apply {
            when (type) {
                SendPasswordType.Mobile -> {
                    this.where { it.mobile match contact }
                }
                SendPasswordType.Email -> {
                    this.where { it.email match contact }
                }
                else -> return "枚举类型有误"
            }
        }.exists()

        if (flag && SendPasswordType.Mobile == type) {
            return "您输入的手机号已绑定"
        }
        if (flag && SendPasswordType.Email == type) {
            return "您输入的邮箱号已绑定"
        }
        return ""
    }

    @PostMapping("/reset")
    fun reset(
        @Require contact: String,
        @Require type: SendPasswordType,
        @Require token: String,
        @Require validateCode: String, // 验证码
        @Require moduleType: MobileCodeModuleEnum,
        request: HttpServletRequest
    ): ApiResult<String> {

        val validateParam: String = validateParam(moduleType, type, contact, request)
        if (validateParam.HasValue) {
            return ApiResult.error(validateParam)
        }
        if (type == SendPasswordType.Mobile) {
            if (!ValidateUtils.checkPhoneNumber(contact)) {
                return ApiResult.error("请输入正确的手机号")
            }
            val codeStatus = mpClient.codeStatus(MobileCodeModuleEnum.ChangeMobile, contact, validateCode)
            if (codeStatus.code != 0) {
                return ApiResult.error("验证码校验错误，请重试")
            }
            // 修改用户手机号码
            mor.tenant.tenantUser.query()
                .where { it.id match request.LoginUser.id }
                .toEntity().apply {
                    if (this == null) {
                        return ApiResult.error("用户不存在")
                    } else {
                        // 修改手机号码
                        this.mobile = contact
                        mor.tenant.tenantUser.updateWithEntity(this).execUpdate()
                    }
                }
            // 修改登录账户手机号码
            mor.tenant.tenantLoginUser.query()
                .where { it.userId match request.LoginUser.id }.toEntity()
                .apply {
                    if (this ==null) {
                        return ApiResult.error("用户不存在")
                    }  else {
                        // 修改手机号码
                        this.mobile = contact
                        mor.tenant.tenantLoginUser.updateWithEntity(this).execUpdate()
                    }
                }

        } else {
            // 校验邮件格式
            if (!ValidateUtils.checkEmail(contact)) {
                return ApiResult.error("请输入正确的邮箱")
            }
            if (validateCode != rer.sys.smsResetCode("${moduleType.name}-$contact").get()) {
                return ApiResult.error("验证码校验错误，请重试")
            }
            // 修改用户邮箱
            mor.tenant.tenantUser.query()
                .where { it.id match request.LoginUser.id }
                .toEntity().apply {
                    if (this == null) {
                        return ApiResult.error("用户不存在")
                    } else {
                        // 修改手机号码
                        this.email = contact
                        mor.tenant.tenantUser.updateWithEntity(this).execUpdate()
                    }
                }
            // 修改登录账户邮箱
            mor.tenant.tenantLoginUser.query()
                .where { it.userId match request.LoginUser.id }.toEntity()
                .apply {
                    if (this ==null) {
                        return ApiResult.error("用户不存在")
                    }  else {
                        // 修改手机号码
                        this.email = contact
                        mor.tenant.tenantLoginUser.updateWithEntity(this).execUpdate()
                    }
                }
        }

        rer.sys.smsResetCode("${moduleType.name}-$contact").deleteKey()
        return ApiResult.of("修改成功")
    }
}