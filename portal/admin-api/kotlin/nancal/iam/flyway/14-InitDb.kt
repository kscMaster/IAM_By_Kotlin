package nancal.iam.flyway

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
class `14-InitDb` : FlywayVersionBaseService(14) {

    override fun exec() {

        mor.tenant.tenantDepartmentInfo.update()
            .where { it.id match_not_equal "" }
            .set { it.manager to mutableListOf<IdName>() }
            .exec()

    }

}