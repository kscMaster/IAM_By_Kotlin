package nancal.iam.flyway

import nbcp.db.FlywayVersionBaseService
import org.springframework.stereotype.Component


@Component
class `0-InitDb` : FlywayVersionBaseService(0) {

    override fun exec() {
        pushAllResourcesData(true)
    }
}
