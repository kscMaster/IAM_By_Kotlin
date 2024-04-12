package nancal.iam.flyway

import nancal.iam.db.mongo.entity.TenantDepartmentInfo
import nancal.iam.db.mongo.mor
import nbcp.comm.BatchReader
import nbcp.db.FlywayVersionBaseService
import nbcp.db.mongo.*
import org.springframework.stereotype.Component

/**
 * @Author zhaopeng
 *
 * @Description
 * @Date 2022/4/6-11:36
 */
@Component
class `6-InitDb` : FlywayVersionBaseService(6) {

    override fun exec() {
        initData()
    }

    fun initData() {
        BatchReader.init { skip, take ->
            mor.tenant.tenantApplication.query().limit(skip, take).toMapList()
        }.forEach { ent ->
            if (!ent.containsKey("isSysDefine")) {
                mor.iam.sysApplication.query()
                    .where { it.appCode match ent.get("appCode").toString() }
                    .toEntity()
                    .apply {
                        if(this !=null){
                            ent.put("isSysDefine", true)
                            ent.put("sysId", this.id)
                        }
                        ent.put("isSysDefine", false)
                        ent.put("sysId", "")
                    }
                mor.tenant.tenantApplication.updateWithEntity(ent).execUpdate()
            }
        }
    }

}