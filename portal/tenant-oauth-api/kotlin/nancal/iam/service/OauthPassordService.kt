package nancal.iam.service

import nancal.iam.client.MPClient
import nancal.iam.client.TenantAdminClient
import nancal.iam.db.mongo.*
import nancal.iam.dto.LezaoMessageDTO
import nancal.iam.util.EmailMessage
import nancal.iam.util.MailUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import javax.annotation.Resource

@Service
class OauthPassordService {

    @Autowired
    lateinit var mpClient: MPClient

    @Autowired
    lateinit var tenantAdminClient: TenantAdminClient

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

    @Async
    fun sendSmsMessage(mobile: String, lang: String, days: MutableList<String>) {
        mpClient.sendSmsNotification(
            "",
            MobileCodeModuleEnum.PasswordWillExpire,
            mobile,
            lang,
            days
        )
    }

    @Async
    fun sendMailMessage(email: String, lang: String, name: String, loginName: String, days: Long) {
        // 发送邮箱
        val msg = EmailMessage()
        msg.sender = mailSender
        msg.password = mailPwd
        msg.addressee = listOf(email)
        if (lang == "cn" || lang == "") {
            msg.topic = "您的密码即将过期，请尽快修改！"
            msg.content = "${name}，您好\n" +
                    " <p style=\"text-indent:2em\">您的账号${loginName}的密码将在${days}天后过期，为避免影响系统使用，请尽快登录修改密码。</br>"
        } else {
            msg.topic = "Your password is about to expire, please change it as soon as possible！"
            msg.content = "${name}，Hello\n" +
                    " <p style=\"text-indent:2em\">\n" + "The password of your account ${loginName} will expire in ${days} days</br>" +
                    "To avoid affecting the use of the system, please log in and change the password as soon as possible</br>"
        }
        msg.popService = mailPop
        msg.smtpService = mailSmtp
        mailUtil.sendEmail(msg)
    }

    @Async
    fun sendMessage(id: String, lang: String, name: String, loginName: String, days: Long,adminType: TenantAdminTypeEnum) {
        val dto = LezaoMessageDTO()
        dto.userIds = mutableListOf(id)
        if (lang == "cn" || lang == "") {
            dto.body = "<p style=\"font-size: 16px;padding-bottom:16px\">${name}，您好</p>" +
                    " <p style=\"text-indent: 2rem;font-size: 14px\">您的账号${loginName}的密码将在${days}天后过期，为避免影响系统使用，请尽快登录修改密码。</p>"
            dto.title = "您的密码即将过期，请尽快修改！"
        } else {
            dto.title = "Your password is about to expire, please change it as soon as possible！"
            dto.body = "<p>${name}，Hello</p>" +
                    "<p style=\"text-indent:2em\">\n" + "The password of your account $loginName will expire in $days days</p>" +
                    "<p>To avoid affecting the use of the system, please log in and change the password as soon as possible</p>"
        }
        dto.msgType = TenantMessageType.TenantMail.type
        dto.mailType = TenantMailType.DevOps.type
        if (adminType == TenantAdminTypeEnum.None) {
            dto.applicationName = "os"
        }
        tenantAdminClient.save(dto)
    }
}