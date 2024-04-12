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
class `17-InitDb` : FlywayVersionBaseService(17) {

    override fun exec() {

        mor.tenant.tenantDepartmentInfo.query()
            .toList()
            .apply {
                this.forEach { dept ->
                    mor.tenant.tenantDepartmentInfo.update()
                        .where { it.name match_not_equal  dept.tenant.name }
                        .where { it.tenant.id match dept.tenant.id }
                        .set { it.level to (dept.level +1) }
                        .exec()
                }
            }
    }

}