package nancal.iam.mvc

import nancal.iam.client.MPClient
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.redis.rer
import nancal.iam.util.EmailMessage
import nancal.iam.util.MailUtil
import nbcp.utils.MyUtil
import nancal.iam.util.ValidateUtils
import nbcp.comm.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletResponse

@OpenAction
@RestController
@RequestMapping("tenant/msg")
class MessageController {
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
     * 发送短信或邮件验证码
     */
    @PostMapping("/sendCode")
    fun findPwd(
        @Require moduleType: MobileCodeModuleEnum,
        @Require type: SendPasswordType,
        @Require contact: String,
        response: HttpServletResponse
    ): ApiResult<String> {
        // 检测手机号码是否存在、状态是否正常，避免短信浪费
        // 判断用户状态是否正常
        val flag = mor.tenant.tenantLoginUser.query().apply {
            when (type) {
                SendPasswordType.Mobile -> {
                    this.where { it.mobile match contact }
                }
                SendPasswordType.Email -> {
                    this.where { it.email match contact }
                }
                else -> return ApiResult.error("枚举类型有误")
            }
        }.exists()

        if (!flag&&SendPasswordType.Mobile==type) {
            return ApiResult.error("您输入的手机号不存在")
        }
        if (!flag&&SendPasswordType.Email==type) {
            return ApiResult.error("您输入的邮箱号不存在")
        }

        val uuid = UUID.randomUUID().toString().replace("-", "");

        if (type == SendPasswordType.Mobile) {
            if (!ValidateUtils.checkPhoneNumber(contact)) {
                return ApiResult.error("请输入正确的手机号")
            }
            rer.sys.smsCode(uuid).set(contact)

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
        msg.sender = mailSender
        msg.password = mailPwd
        msg.addressee = listOf(contact)
        msg.topic = "【乐仓统一身份认证系统】找回密码"
        msg.content = "您好，您的验证码为：$code。"
        msg.popService = mailPop
        msg.smtpService = mailSmtp
        mailUtil.sendEmail(msg)
        // 验证码存入redis
        rer.sys.smsCode("${moduleType.name}-$contact").set(code)
        rer.sys.smsCode(uuid).set(contact)
        return ApiResult.of(uuid)
    }
}
