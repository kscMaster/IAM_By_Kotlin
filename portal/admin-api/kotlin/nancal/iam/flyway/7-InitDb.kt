package nancal.iam.flyway

import com.nancal.cipher.SHA256Util
import nancal.iam.client.MPClient
import nancal.iam.db.mongo.MobileCodeModuleEnum
import nancal.iam.db.mongo.SendPasswordType
import nancal.iam.db.mongo.entity.TenantDepartmentInfo
import nancal.iam.db.mongo.mor
import nancal.iam.util.EmailMessage
import nancal.iam.util.MailUtil
import nbcp.comm.BatchReader
import nbcp.db.FlywayVersionBaseService
import nbcp.db.mongo.*
import nbcp.utils.SpringUtil
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.Resource

/**
 * @Author zhaopeng
 *
 * @Description
 * @Date 2022/4/6-11:36
 */
@Component
class `7-InitDb` : FlywayVersionBaseService(7) {

    @Value("\${mail.sender}")
    val mailSender: String = ""

    @Value("\${mail.pwd}")
    val mailPwd: String = ""

    @Value("\${mail.smtp}")
    val mailSmtp: String = ""

    @Value("\${mail.pop}")
    val mailPop: String = ""

    @Autowired
    lateinit var mailUtil: MailUtil

//    @Autowired
//    lateinit var mpClient: MPClient

    override fun exec() {
        initData()
    }

    fun initData() {

        val mpClientBean = SpringUtil.getBean(MPClient::class.java)
        val mailClientBean = SpringUtil.getBean(MailUtil::class.java)

        BatchReader.init { skip, take ->
            mor.admin.adminLoginUser.query().limit(skip, take).toMapList()
        }.forEach { ent ->
            if (!ent.containsKey("passwordSalt")) {
                var salt = UUID.randomUUID().toString().replace("-", "").substring(0, 4)
                val pwd = SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava("qwe123") + salt)
                ent.put("passwordSalt", salt)
                ent.set("password", pwd)
                mor.admin.adminLoginUser.updateWithEntity(ent).execUpdate()
            }
        }

        BatchReader.init { skip, take ->
            mor.tenant.tenantLoginUser.query().limit(skip, take).toMapList()
        }.forEach { ent ->
            if (!ent.containsKey("passwordSalt")) {
                var salt = UUID.randomUUID().toString().replace("-", "").substring(0, 4)
                val pwd = SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava("qwe123") + salt)
                ent.put("passwordSalt", salt)
                ent.set("password", pwd)
                mor.tenant.tenantLoginUser.updateWithEntity(ent).execUpdate()


                mor.tenant.tenantUser.queryById(ent.getString("userId").toString()).toEntity()
                    .apply {
                        try {
                            if(this !=null){
                                if(this.sendPasswordType !=null){
                                    if (this.sendPasswordType == SendPasswordType.Mobile) {

                                        val sendSmsCode =
                                            mpClientBean.sendSmsPwd(MobileCodeModuleEnum.SendPassword, this.mobile, "qwe123")
//                                            mpClient.sendSmsPwd(MobileCodeModuleEnum.SendPassword, this.mobile, "qwe123")
                                        val success = sendSmsCode.success()
                                        println(success.toString())
                                    } else if (this.sendPasswordType == SendPasswordType.Email) {
                                        val msg = EmailMessage()
                                        msg.sender = mailSender
                                        msg.password = mailPwd
                                        msg.addressee = listOf(this.email)
                                        msg.topic = "【能科瑞元】登录密码"
                                        msg.content = "您好，${this.loginName}\n" +
                                                " <p style=\"text-indent:2em\">您的登录密码因系统升级修改为：qwe123 。为了您账户的</br>" +
                                                "安全请勿将密码告知他人。</br>"
                                        msg.popService = mailPop
                                        msg.smtpService = mailSmtp
                                        mailClientBean.sendEmail(msg)
                                    }
                                }
                            }
                        }catch (e : Exception){
                            e.printStackTrace()
                        }

                    }
            }
        }

        BatchReader.init { skip, take ->
            mor.tenant.tenantLoginUser.query().limit(skip, take).toMapList()
        }.forEach { ent ->
            if (!ent.containsKey("passwordSalt")) {
                var salt = UUID.randomUUID().toString().replace("-", "").substring(0, 4)
                val pwd = SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava("qwe123") + salt)
                ent.put("passwordSalt", salt)
                ent.set("password", pwd)
                mor.tenant.tenantLoginUser.updateWithEntity(ent).execUpdate()

                mor.tenant.tenantUser.queryById(ent.getString("userId").toString()).toEntity()
                    .apply {
                        if(this !=null){
                            try{
                                if(this.sendPasswordType !=null){
                                    if (this.sendPasswordType == SendPasswordType.Mobile) {

                                        val sendSmsCode =
                                            mpClientBean.sendSmsPwd(MobileCodeModuleEnum.SendPassword, this.mobile, "qwe123")
                                        val success = sendSmsCode.success()
                                        println(success.toString())
                                    } else if (this.sendPasswordType == SendPasswordType.Email) {
                                        val msg = EmailMessage()
                                        msg.sender = mailSender
                                        msg.password = mailPwd
                                        msg.addressee = listOf(this.email)
                                        msg.topic = "【能科瑞元】登录密码"
                                        msg.content = "您好，${this.loginName}\n" +
                                                " <p style=\"text-indent:2em\">您的登录密码因系统升级修改为：qwe123 。为了您账户的</br>" +
                                                "安全请勿将密码告知他人。</br>"
                                        msg.popService = mailPop
                                        msg.smtpService = mailSmtp
                                        mailClientBean.sendEmail(msg)
                                    }
                                }
                            }catch (e : Exception){
                                e.printStackTrace()
                            }
                        }
                    }
            }
        }

    }

}