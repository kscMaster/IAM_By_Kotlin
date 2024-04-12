package nancal.iam.flyway

import nancal.iam.db.mongo.entity.DeptDefine
import nancal.iam.db.mongo.entity.TenantDepartmentInfo
import nancal.iam.db.mongo.mor
import nbcp.db.FlywayVersionBaseService
import nbcp.db.IdName
import nbcp.db.mongo.*
import org.springframework.stereotype.Component

/**
 * @Author zhaopeng
 *
 * @Description
 * @Date 2022/7/6
 */
@Component
class `18-InitDb` : FlywayVersionBaseService(18) {

    override fun exec() {
        mor.tenant.tenantSecretSet.update()
            .where{it.id match_not_equal ""}
            .set { it.setting.selfSetting.securityPolicy.lowInput to true }
            .set { it.setting.selfSetting.securityPolicy.numberInput to true }
            .set { it.setting.selfSetting.securityPolicy.upInput to true }
            .set { it.setting.selfSetting.securityPolicy.specialInput to false }
            .exec()
    }

}