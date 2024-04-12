package nancal.iam.flyway

import nancal.iam.db.mongo.mor
import nbcp.db.FlywayVersionBaseService
import nbcp.db.mongo.*
import org.springframework.stereotype.Component

/**
 * @Author zhaopeng
 *
 * @Description
 * @Date 2022/7/6
 */
@Component
class `16-InitDb` : FlywayVersionBaseService(16) {

    override fun exec() {

        mor.tenant.tenantDepartmentInfo.query()
            .toList()
            .apply {
                this.forEach { dept ->
                    mor.tenant.tenantDepartmentInfo.update()
                        .where { it.name match dept.tenant.name }
                        .where { it.tenant.id match dept.tenant.id }
                        .set { it.parent.id to "" }
                        .set { it.parent.name to "" }
                        .exec()
                }
            }




    }

}