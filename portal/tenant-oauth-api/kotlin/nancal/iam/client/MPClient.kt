package nancal.iam.client

import nbcp.comm.JsonResult
import nancal.iam.db.mongo.MobileCodeModuleEnum
import nbcp.comm.Require
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * @Classname ECClient
 * @Description sync ec apis
 * @Version 1.0.0
 * @Date 4/12/2021 上午 11:40
 * @Created by kxp
 */
//@FeignClient(name = "192.168.5.213/api/mp-integration", fallback = MPClientFallbackFactory::class)
@FeignClient(name = "mp-integration", fallback = MPClientFallbackFactory::class)
interface MPClient {



    // 发送手机验证码
    @PostMapping("open/sms-code/send")
    fun sendSmsCode(
        @RequestParam module: MobileCodeModuleEnum,
        @RequestParam mobile: String,
    ): BaseResult

    // 发送账号密码
    @PostMapping("open/sms-code/send")
    fun sendSmsPwd(
        @RequestParam module: MobileCodeModuleEnum,
        @RequestParam mobile: String,
        @RequestParam code: String
    ): BaseResult

    //验证 手机短信验证码 是否正确
    @PostMapping("open/sms-code/checkCode")
    fun codeStatus(
        @RequestParam module: MobileCodeModuleEnum,
        @RequestParam mobile: String,
        @RequestParam code: String
    ): BaseResult

    // 发送邮箱验证码
    @PostMapping("open/email/send")
    fun sendEmail(
        @RequestParam title: String,   //邮件标题
        @RequestParam content: String,    //邮件内容
        @RequestParam emails: MutableList<String>    //发送的邮件地址集合
    ): JsonResult


    @PostMapping("open/sms-notification/send")
    fun sendSmsNotification(
        productLineCode: String,
        @RequestParam module: MobileCodeModuleEnum,
        @RequestParam mobile: String,
        @RequestParam lang: String,
        @RequestParam params: MutableList<String>
    ): JsonResult

}
