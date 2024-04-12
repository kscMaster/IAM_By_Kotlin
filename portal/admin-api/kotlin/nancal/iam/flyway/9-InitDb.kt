package nancal.iam.flyway

import nancal.iam.db.mongo.mor
import nbcp.db.FlywayVersionBaseService
import nbcp.db.mongo.match
import nbcp.db.mongo.query
import nbcp.db.mongo.update
import org.springframework.stereotype.Component

/**
 * @Author wrk
 *
 * @Description
 * @Date 2022/4/19-17:55
 */
@Component
class `9-InitDb` : FlywayVersionBaseService(9) {

    override fun exec() {
        initSysId()
    }

    fun initSysId() {
        println("为tenantApp补全sysId")
        //补丁  为tenantApp补全sysId
        val sysAppList=mor.iam.sysApplication.query().toList()
        sysAppList.forEach {
            sysApp->
            mor.tenant.tenantApplication.update()
                .where{it.appCode match sysApp.appCode}
                .set { it.sysId to sysApp.id }
                .exec()
        }


    }
}