package nancal.iam.web.open

//import nbcp.base.config.ActionDocBeanGather

import nbcp.comm.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.AdminLoginUser
import nancal.iam.db.mongo.entity.AdminUser
import nbcp.utils.Md5Util
import org.springframework.web.bind.annotation.*

/**
 * Created by cy on 20-12-3.
 * 系统注册方法  接口
 */
@RestController
@RequestMapping("/open/register")
@OpenAction
class Open_Register_Controller {

    //根据手机号发送验证码功能
    @GetMapping("/sendSms")
    fun sendSms(
            mobile: String
    ): JsonResult {
        //调用  integration 中的短信
//        val sendSms = smsService.sendSms(MobileCodeModuleEnum.Registe, mobile, MyUtil.getRandomWithLength(4))
//
//        if (sendSms.isNotEmpty()) {
//            return JsonResult.error(sendSms)
//        }

        return JsonResult()
    }

    //系统用户注册方法  通过手机验证
    fun registerDevOps(
            mobile: String, //注册账号 手机号
            password: String
    ): JsonResult {

        //查询是否有相同用户名  相同手机号
        if (mor.admin.adminLoginUser.query().where { it.mobile match mobile }.exists()) return JsonResult.error("该手机号已被使用,请更换手机号后重试")

        //开始注册Devops 账户
        val admin = AdminUser();

        admin.loginName = mobile;
        admin.name = mobile
        admin.isAdmin = true;

        mor.admin.adminUser.doInsert(admin);


        val admin_login = AdminLoginUser();
        admin_login.userId = admin.id;
        admin_login.loginName = mobile
        admin_login.password = Md5Util.getBase64Md5(password);

        mor.admin.adminLoginUser.doInsert(admin_login);


        //是否调用注册子系统方法

        return JsonResult()
    }

}