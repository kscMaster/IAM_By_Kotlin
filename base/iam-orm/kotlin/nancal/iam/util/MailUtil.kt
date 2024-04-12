package nancal.iam.util

import com.sun.mail.util.MailSSLSocketFactory
import nbcp.comm.HasValue
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Component
import java.util.*
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

/**
 * @ClassName MailUtil
 * @author xuebin
 * @version 1.0.0
 * @Description MailUtil
 * @createTime 2021年12月07日 20:56:00
 */

enum class EmailServiceTypeEnum {
    POP3,
    IMAP,
    Exchange
}

class EmailMessage{
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }
    /**
     * 租户ID
     */
    var tenantId: String = ""

    /**
     * 发件人邮箱
     */
    var sender:  String = ""

    /**
     * 发件人邮箱密码
     */
    var password: String = ""

    /**
     * 收件人邮箱
     */
    var addressee:  List<String> = mutableListOf()

    /**
     * 抄送人邮箱
     */
    var cc: List<String> = mutableListOf()

    /**
     * 邮件主题
     */
    var topic: String = ""

    /**
     * 邮件内容
     */
    var content: String = ""

    /**
     * 接收服务器类型
     */
    var serviceType: EmailServiceTypeEnum = EmailServiceTypeEnum.POP3

    /**
     * POP 服务器
     */
    var popService: String = "pop.exmail.qq.com"

    /**
     * POP 服务器 端口
     */
    var popPort = 995

    /**
     * POP 服务器 是否是 SSL
     */
    var isPopSsl: Boolean = true

    /**
     * SMTP 服务器
     */
    var smtpService: String = "smtp.exmail.qq.com"

    /**
     * SMTP 服务器 端口
     */
    var smtpPort = 465

    /**
     * SMTP 服务器 是否是 SSL
     */
    var isSmtpSsl: Boolean = true
}

private const val MIME_SYNTHETIC_RELATION_MIXED = "mixed"
private const val MIME_SYNTHETIC_RELATION_RELATED = "related"

@Component
class MailUtil {

    fun sendEmail(msg: EmailMessage){
        try {
            val mailSender: JavaMailSenderImpl = getMailSender(msg)
            val message = mailSender.createMimeMessage()
            // 设置发件邮箱
            if (msg.sender.HasValue) {
                message.setFrom(InternetAddress(msg.sender))
            }
            // 收件人
            setEmailToOrCC(msg.addressee, Message.RecipientType.TO, message)
            // 抄送人
            setEmailToOrCC(msg.cc, Message.RecipientType.CC, message)
            // 主题
            message.subject = msg.topic
            // 发送时间
            message.sentDate = Date()
            val mixed: MimeMultipart = MimeMultipart(MIME_SYNTHETIC_RELATION_MIXED)
            message.setContent(mixed)

            // 邮件内容
            createContent(msg, mixed)

            message.saveChanges()
            mailSender.send(message)
            //.info("发送邮件成功")
        } catch (e: Exception) {
            //com.nancal.msg.service.MailService.log.error("发送邮件失败：" + msg.getAddressee() + e)
            e.printStackTrace()
        }
    }

    /**
     * 生成邮件内容
     * @param msg 邮件内容
     * @param mixed 邮件复合模块
     * @throws MessagingException ex
     */
    private fun createContent(msg: EmailMessage, mixed: MimeMultipart) {
        val content = MimeBodyPart()
        val multipart = MimeMultipart()

        val htmlBodyPart = MimeBodyPart()
        htmlBodyPart.setContent(msg.content, "text/html;charset=UTF-8")
        multipart.addBodyPart(htmlBodyPart)
        content.setContent(multipart)
        mixed.addBodyPart(content)
    }

    /**
     * 设置邮件的 收件人 / 抄送人
     * @param emailAddress 邮件地址
     * @param type 收件人类型
     * @param message 邮件主体
     * @throws MessagingException ex
     */
    private fun setEmailToOrCC(emailAddress: List<String>, type: Message.RecipientType, message: MimeMessage) {
        if (emailAddress.isNotEmpty()) {
            val internetAddress = arrayOfNulls<InternetAddress>(emailAddress.size)
            for (i in emailAddress.indices) {
                internetAddress[i] = InternetAddress(emailAddress[i])
            }
            message.setRecipients(type, internetAddress)
        }
    }

    private fun getMailSender(msg: EmailMessage): JavaMailSenderImpl{
        val mailProperties = MailProperties()
        mailProperties.host = msg.smtpService
        mailProperties.port = msg.smtpPort
        mailProperties.username = msg.sender
        mailProperties.password = msg.password
        mailProperties.protocol = "smtp"
        val mailSender = getMailSender(mailProperties)

        //开启ssl
        val properties = Properties()
        properties.setProperty("mail.smtp.timeout", "200000") //设置链接超时
        properties.setProperty("mail.smtp.auth", "true") //开启认证

        if (msg.isSmtpSsl) {
            val sf = MailSSLSocketFactory()
            sf.setTrustAllHosts(true)
            properties["mail.smtp.ssl.enable"] = "true"
            properties["mail.smtp.ssl.socketFactory"] = sf
        }
        mailSender.javaMailProperties = properties
        return mailSender
    }

    private fun getMailSender(properties: MailProperties):JavaMailSenderImpl{
        val sender = JavaMailSenderImpl()
        applyProperties(properties, sender)
        return sender
    }

    private fun applyProperties(properties: MailProperties, sender: JavaMailSenderImpl) {
        sender.host = properties.host
        if (properties.port != null) {
            sender.port = properties.port
        }
        sender.username = properties.username
        sender.password = properties.password
        sender.protocol = properties.protocol
        if (properties.defaultEncoding != null) {
            sender.defaultEncoding = properties.defaultEncoding.name()
        }
        if (!properties.properties.isEmpty()) {
            sender.javaMailProperties = asProperties(properties.properties)
        }
    }

    private fun asProperties(source: Map<String?, String?>): Properties? {
        val properties = Properties()
        properties.putAll(source)
        return properties
    }
}

