package nancal.iam.mvc.tenant

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import nancal.iam.annotation.BizLog
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nbcp.comm.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.redis.rer
import nbcp.db.db
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

/**
 * Created by CodeGenerator at 2021-11-17 17:43:16
 */
@Api(description = "系统设置", tags = ["SysSetting"])
@RestController
@RequestMapping("/tenant/sys-setting")
class TenantSettingController {

    @ApiOperation("更新")
    @PostMapping("/update")
    @BizLog(BizLogActionEnum.Update, BizLogResourceEnum.SysSetting, "系统设置")
    fun update(
        @JsonModel entity: Tenant,
        setting: TenantSetting,
        request: HttpServletRequest
    ): JsonResult {
        if (setting.sessionTimeout < 1){
            return JsonResult.error("会话时长不可以小于1")
        }
        if (setting.selfSetting.loginChecking.checkingPeriod < 15 || setting.selfSetting.loginChecking.checkingPeriod > 60){
            return JsonResult.error("登录验证统计周期 请输入15～60之间的整数")
        }
        if (setting.selfSetting.loginChecking.lockDuration < 15 || setting.selfSetting.loginChecking.lockDuration>30){
            return JsonResult.error("账户被锁定时持续时间 请输入15～30之间的整数")
        }
        if (setting.selfSetting.loginChecking.retryTime < 3 || setting.selfSetting.loginChecking.retryTime >10){
            return JsonResult.error("允许登录失败尝试次数 请输入3～10之间的整数")
        }
        if (setting.selfSetting.securityPolicy.leastCharacters < 2 || setting.selfSetting.securityPolicy.leastCharacters > 4){
            return JsonResult.error("最少字符 请输入2～4之间的整数")
        }

        if (setting.selfSetting.securityPolicy.leastLenght < 6 || setting.selfSetting.securityPolicy.leastCharacters > 32){
            return JsonResult.error("最短长度 请输入6～32位")
        }

        if (setting.selfSetting.securityPolicy.expires){
            if (setting.selfSetting.securityPolicy.expiresDays < 1 || setting.selfSetting.securityPolicy.expiresDays > 180){
                return JsonResult.error("过期天数，请输入1～180之间的整数")
            }
            if (setting.selfSetting.securityPolicy.expiresNotice < 1 || setting.selfSetting.securityPolicy.expiresNotice > 30){
                return JsonResult.error("通知客户天数，请输入1～30之间的整数")
            }
            if (setting.selfSetting.securityPolicy.expiresNotice > setting.selfSetting.securityPolicy.expiresDays){
                return JsonResult.error("通知天数应小于过期天数")
            }
        }

        if(setting.sessionUnit==SettingEnum.Minute){
            if (setting.sessionTimeout < 15 || setting.sessionTimeout > 1440){
                return JsonResult.error("会话超时策略 请输入15～1440之间的整数")
            }
        }else{
            if (setting.sessionTimeout < 1 || setting.sessionTimeout > 24){
                return JsonResult.error("会话超时策略 请输入1～24之间的整数")
            }
        }

        request.logMsg = "修改系统设置基础设置"
        val loginUser = request.LoginTenantAdminUser
        if (entity.id.isNotEmpty()) {
            entity.id = loginUser.tenant.id
            //是否重名
            mor.tenant.tenant.query()
                .where { it.id match_not_equal loginUser.tenant.id }
                .where { it.name match entity.name }
                .exists()
                .apply {
                    if (this) {
                        return JsonResult.error("单位名称重复")
                    }
                }
            //更新租户信息
            mor.tenant.tenant.updateById(entity.id)
                .apply {
                    this.set { it.name to entity.name }
                    this.set { it.remark to entity.remark }
                    this.set { it.logo.id to entity.logo.id }
                    this.set { it.logo.url to entity.logo.url }

                }
                .exec()
            if(db.affectRowCount==0) return JsonResult.error("租户信息更新失败")
        }

        //更新租户设置
        mor.tenant.tenantSecretSet.updateByTenantId(loginUser.tenant.id).apply {
            this.set { it.setting.selfSetting.loginChecking to setting.selfSetting?.loginChecking }
            this.set { it.setting.selfSetting.securityPolicy to setting.selfSetting?.securityPolicy }
            this.set { it.setting.sessionTimeout to setting.sessionTimeout }
            this.set { it.setting.sessionUnit to setting.sessionUnit }
        }
            .exec().apply {
                return if (this > 0) {
                    JsonResult()
                } else {
                    JsonResult.error("租户设置更新失败")
                }
            }

    }

    @ApiOperation("解锁员工冻结状态")
    @PostMapping("/unlock")
    @BizLog(BizLogActionEnum.Unlock, BizLogResourceEnum.LoginUserStatus, "解冻用户账号状态")
    fun unlock(
        @Require userId: String,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "解冻用户账号状态"
        // 校验员工是否在该租户下
        val tenant = request.LoginTenantAdminUser.tenant

        // 更改用户账户的状态 及 删除redis中用户冻结的key
        val user = mor.tenant.tenantLoginUser.query()
            .where { it.tenant.id match tenant.id }
            .where { it.userId match userId }
            .toEntity().apply {
                if (this == null) {
                    return JsonResult.error("找不到该用户")
                }
            }

        mor.tenant.tenantLoginUser.update()
            .where { it.tenant.id match tenant.id }
            .where { it.userId match user!!.userId }
            .set { it.isLocked to false }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("更新失败")
                }
                rer.iamUser.errorLogin(user!!.loginName).deleteKey()
                return JsonResult()
            }
    }
}
