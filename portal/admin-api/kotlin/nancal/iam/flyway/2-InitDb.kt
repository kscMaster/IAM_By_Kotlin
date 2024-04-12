package nancal.iam.flyway

import nbcp.db.FlywayVersionBaseService
import org.springframework.stereotype.Component


@Component
class `2-InitDb` : FlywayVersionBaseService(2) {

    override fun exec() {
        pushAllResourcesData(true)

        initData()
    }

    fun initData() {


    }
}
