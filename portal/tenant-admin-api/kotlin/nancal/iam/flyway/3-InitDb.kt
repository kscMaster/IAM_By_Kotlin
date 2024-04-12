package nancal.iam.flyway

import nancal.iam.db.mongo.entity.TenantApplication
import nancal.iam.db.mongo.mor
import nbcp.comm.HasValue
import nbcp.db.FlywayVersionBaseService
import nbcp.db.mongo.query
import nbcp.db.mongo.updateWithEntity
import org.springframework.stereotype.Component


@Component
class `3-InitDb` : FlywayVersionBaseService(3) {

    override fun exec() {
        initPersonClassified()
    }


    fun initPersonClassified() {
        val toList: MutableList<TenantApplication> = mor.tenant.tenantApplication.query()
            .toList { TenantApplication::class.java }

        toList.forEach {
            if(!it.ename.HasValue) {
                it.ename = ""
            }
            mor.tenant.tenantApplication.updateWithEntity(it)
        }
    }
}