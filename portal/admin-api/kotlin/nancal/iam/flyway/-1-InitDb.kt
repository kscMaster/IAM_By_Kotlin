package nancal.iam.flyway

import nbcp.db.FlywayVersionBaseService
import nancal.iam.db.mongo.*
import org.springframework.stereotype.Component


@Component
class `-1-InitDb` : FlywayVersionBaseService(-1) {

    override fun exec() {
        initMongoIndex(
            {
                if (it.tableName == mor.admin.sysMongoAdminUser.tableName) {
                    return@initMongoIndex false
                }
                return@initMongoIndex true
            },
            rebuild = true
        )
    }
}
