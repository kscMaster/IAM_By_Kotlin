package nancal.iam.service

import cn.hutool.core.date.LocalDateTimeUtil
import nancal.iam.client.MPClient
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.SecurityPolicy
import nancal.iam.db.mongo.entity.TenantLoginUser
import nancal.iam.dto.LezaoMessageDTO
import nancal.iam.util.EmailMessage
import nancal.iam.util.MailUtil
import nbcp.comm.*
import nbcp.db.IdName
import nbcp.db.mongo.*
import nbcp.utils.SpringUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import javax.annotation.Resource

@Component
internal class PasswordSendTask {

    @Resource
    lateinit var msgService: MsgService

    @Resource
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

    @Scheduled(fixedDelay = 5000)
    fun passwordMessageRemind110121101111010() {

        var expires: Pair<PwdExpires, String>? = null

        BatchReader.init { skip, take ->
            mor.tenant.tenantLoginUser.query().limit(skip, take).toList()
        }.forEach { ent ->
            try {
                expires = getExpire(ent.tenant.id, ent)
                if (expires!!.first == PwdExpires.Remind) {
                    val tenantSecretSet = mor.tenant.tenantSecretSet.query()
                        .where { it.tenant.id match ent.tenant.id }
                        .toEntity()

                    val sp = tenantSecretSet?.setting!!.selfSetting?.securityPolicy ?: SecurityPolicy()
                    val epTime = ent.lastUpdatePwdAt.plusDays(sp.expiresDays.toLong())
                    val days = LocalDateTimeUtil.between(LocalDateTime.now(), epTime).toDays()

                    if (ent.autoRemindPwdTimes < 1) {

                        mor.tenant.tenantUser.queryById(ent.userId)
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
                                        msg.topic = "您的密码即将过期，请尽快修改！"
                                        msg.content = "<p>${this.name}，您好</p>" +
                                                " <p style=\"text-indent:2em\">您的账号${this.loginName}的密码将在${days}天后过期，为避免影响系统使用，请尽快登录修改密码。</p>"
                                        msg.popService = mailPop
                                        msg.smtpService = mailSmtp
                                        mailUtil.sendEmail(msg)
                                    }

                                    var dto = LezaoMessageDTO()
                                    dto.userIds = mutableListOf(this.id)
                                    dto.body = "<p style=\"font-size: 16px;padding-bottom:16px\">${this.name}，您好</p>" +
                                            " <p style=\"text-indent: 2rem;font-size: 14px\">您的账号${this.loginName}的密码将在${days}天后过期，为避免影响系统使用，请尽快登录修改密码。</p>"
                                    dto.title = "您的密码即将过期，请尽快修改！"
                                    dto.msgType = TenantMessageType.TenantMail.type
                                    dto.mailType = TenantMailType.DevOps.type
                                    if (this.adminType.name == TenantAdminTypeEnum.None.name) {
                                        dto.applicationName = "os"
                                    }
                                    val save = msgService.save(dto)
                                    println(save)

                                    mor.tenant.tenantLoginUser.updateById(ent.id)
                                        .set { it.autoRemindPwdTimes to 1 }
                                        .exec()
                                }
                            }
                    }

                }
            } catch (e: Exception) {
                // Do nothing
            }
        }


    }

    @Scheduled(fixedDelay = 5000)
    fun passwordMessageExpired011111010() {

        var expires: Pair<PwdExpires, String>? = null

        BatchReader.init { skip, take ->
            mor.tenant.tenantLoginUser.query().limit(skip, take).toList()
        }.forEach { ent ->
            try {
                expires = getExpire(ent.tenant.id, ent)
                if (expires!!.first == PwdExpires.Deprecated) {

                    if (ent.autoExpirePwdTimes < 1) {
                        mor.tenant.tenantUser.queryById(ent.userId)
                            .toEntity()
                            .apply {
                                if (this != null) {
                                    if (this.mobile.HasValue) {
                                        // 发送短信
                                        mpClient.sendSmsNotification(
                                            "",
                                            MobileCodeModuleEnum.PasswordExpired,
                                            this.mobile,
                                            "cn",
                                            mutableListOf(this.name)
                                        )
                                    }

                                    if (this.email.HasValue) {
                                        // 发送邮箱
                                        val msg = EmailMessage()
                                        msg.sender = mailSender
                                        msg.password = mailPwd
                                        msg.addressee = listOf(this.email)
                                        msg.topic = "您的密码已过期，请尽快修改！"
                                        msg.content = "${this.name}，您好\n" +
                                                " <p style=\"text-indent:2em\">您的账号${this.loginName}的密码已过期，为避免影响系统使用，请尽快登录修改密码。</br>"
                                        msg.popService = mailPop
                                        msg.smtpService = mailSmtp
                                        mailUtil.sendEmail(msg)
                                    }

                                    var dto = LezaoMessageDTO()
                                    dto.userIds = mutableListOf(this.id)
                                    dto.body = "<p style=\"font-size: 16px;padding-bottom:16px\">${this.name}，您好！</p>" +
                                            "<p style=\"text-indent: 2rem;font-size: 14px\">您的账号${this.loginName}的密码已过期，为避免影响系统使用，请尽快登录修改密码。</p>"
                                    dto.title = "您的密码已过期，请尽快修改"
                                    dto.msgType = TenantMessageType.TenantMail.type
                                    dto.mailType = TenantMailType.DevOps.type
                                    if (this.adminType == TenantAdminTypeEnum.None){
                                        dto.applicationName = "os"
                                    }
                                    val save = msgService.save(dto)
                                    println(save)

                                    mor.tenant.tenantLoginUser.updateById(ent.id)
                                        .set { it.autoExpirePwdTimes to 1 }
                                        .exec()
                                }
                            }
                    }

                }
            } catch (e: Exception) {
                // Do nothing
            }
        }


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


}