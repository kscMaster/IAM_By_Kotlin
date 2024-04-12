package nancal.iam.flyway

import nancal.iam.db.mongo.SettingEnum
import nancal.iam.db.mongo.mor
import nbcp.db.FlywayVersionBaseService
import nbcp.db.mongo.*
import org.springframework.stereotype.Component


@Component
class `13-InitDb` : FlywayVersionBaseService(13) {

    override fun exec() {
        //会话超时策略
        fixSessionTimeout()

        //账户锁定策略
        fixLockingStrategy()
    }

    /*单位为分钟时：请输入15～1440之前的整数；小于15的修复为15*/
    fun fixSessionTimeout() {
        mor.tenant.tenantSecretSet.update()
            .where { it.setting.sessionUnit match SettingEnum.Minute }
            .where { it.setting.sessionTimeout match_lte 15 }
            .where { it.setting.sessionTimeout match_not_equal 15 }
            .set { it.setting.sessionTimeout to 15 }
            .exec()


    }

    /*登陆验证周期：默认15分钟；请输入15～60之前的整数；
      允许登陆失败尝试次数：默认5；请输入3～10之前的整数；
      锁定账号：默认15分钟，请输入15～30之前的整数。*/
    fun fixLockingStrategy() {
        mor.tenant.tenantSecretSet.update()
            .where { it.setting.selfSetting.loginChecking.checkingPeriod match_lte 15 }
            .where { it.setting.selfSetting.loginChecking.checkingPeriod match_not_equal 15 }
            .set { it.setting.selfSetting.loginChecking.checkingPeriod to 15 }
            .exec()
        mor.tenant.tenantSecretSet.update()
            .where { it.setting.selfSetting.loginChecking.retryTime match_lte 3 }
            .where { it.setting.selfSetting.loginChecking.retryTime match_not_equal 3 }
            .set { it.setting.selfSetting.loginChecking.retryTime to 3 }
            .exec()
        mor.tenant.tenantSecretSet.update()
            .where { it.setting.selfSetting.loginChecking.lockDuration match_lte 15 }
            .where { it.setting.selfSetting.loginChecking.lockDuration match_not_equal 15 }
            .set { it.setting.selfSetting.loginChecking.lockDuration to 15 }
            .exec()

    }
}
