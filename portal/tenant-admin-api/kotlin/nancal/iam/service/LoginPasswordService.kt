package nancal.iam.service

import cn.hutool.core.date.LocalDateTimeUtil
import nancal.iam.client.MPClient
import nbcp.comm.*
import nbcp.db.CodeName
import nbcp.db.IdName
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.mongo.extend.*
import nancal.iam.dto.LezaoMessageDTO
import nancal.iam.util.EmailMessage
import nancal.iam.util.MailUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class LoginPasswordService {

    @Resource
    lateinit var msgService: MsgService

    @Resource
    lateinit var oauthPassordService: OauthPassordService

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

    @Resource
    lateinit var mpClient: MPClient

    fun passwordSend(userId: String, lang: String) {

        var expires: Pair<PwdExpires, String>? = null
        var ent = mor.tenant.tenantLoginUser.query()
            .where { it.userId match userId }
            .toEntity()!!

        try {
            expires = getExpire(ent.tenant.id, ent)
            if (expires!!.first == PwdExpires.Remind) {
                if (ent.manualRemindPwdTimes < 1) {
                    val tenantSecretSet = mor.tenant.tenantSecretSet.query()
                        .where { it.tenant.id match ent.tenant.id }
                        .toEntity()

                    val sp = tenantSecretSet?.setting!!.selfSetting?.securityPolicy ?: SecurityPolicy()
                    val epTime = ent.lastUpdatePwdAt.plusDays(sp.expiresDays.toLong())
                    val days = LocalDateTimeUtil.between(LocalDateTime.now(), epTime).toDays()

                    mor.tenant.tenantUser.queryById(ent.userId)
                        .toEntity()
                        .apply {
                            if (this != null) {
                                if (this.mobile.HasValue) {
                                    // 发送短信
                                    oauthPassordService.sendSmsMessage(
                                        this.mobile,
                                        lang,
                                        mutableListOf(this.name, days.toString())
                                    )
                                }

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

                                mor.tenant.tenantLoginUser.updateById(ent.id)
                                    .set { it.manualRemindPwdTimes to 1 }
                                    .exec()
                            }
                        }
                }
            }

//                if(expires!!.first == PwdExpires.Deprecated){
//                    if (ent.manualExpirePwdTimes <1) {
//                        mor.tenant.tenantUser.queryById(ent.userId)
//                            .toEntity()
//                            .apply {
//                                if (this != null) {
//                                    if (this.mobile.HasValue) {
//                                        // 发送短信
//                                    }
//
//                                    if (this.email.HasValue) {
//                                        // 发送邮箱
//                                        val msg = EmailMessage()
//                                        msg.sender = mailSender
//                                        msg.password = mailPwd
//                                        msg.addressee = listOf(this.email)
//                                        msg.topic = "您的密码已过期，请尽快修改！"
//                                        msg.content = "${this.name}，您好\n" +
//                                                " <p style=\"text-indent:2em\">您的账号${this.loginName}的密码已过期，</br>" +
//                                                "为避免影响系统使用，请尽快登录修改密码。</br>"
//                                        msg.popService = mailPop
//                                        msg.smtpService = mailSmtp
//                                        mailUtil.sendEmail(msg)
//                                    }
//
//                                    var dto =  LezaoMessageDTO()
//                                    dto.userIds = mutableListOf(this.id)
//                dto.body= "<p style=\"font-size: 16px;padding-bottom:16px\">${this.name}，您好！</p>"+
//                        "<p style=\"text-indent: 2rem;font-size: 14px\">您的账号${this.loginName}的密码已过期，为避免影响系统使用，请尽快登录修改密码。</p>"
//                                    dto.title = "您的密码已过期，请尽快修改"
//                                    dto.mailType = 1
//                                    dto.msgType = 1
//
//                                    val save = msgService.save(dto)
//                                    println(save)
//                                    mor.tenant.tenantLoginUser.updateById(ent.id)
//                                        .set { it.manualExpirePwdTimes to 1 }
//                                        .exec()
//                                }
//                            }
//                    }
//
//                }


        } catch (e: Exception) {
            // Do nothing
        }


    }


}
