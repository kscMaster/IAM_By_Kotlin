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
class `15-InitDb` : FlywayVersionBaseService(15) {

    override fun exec() {

        var batchList = mutableListOf<TenantDepartmentInfo>()

        mor.tenant.tenant.query().toList()
            .apply {
                if(this.isNotEmpty()){
                    this.forEach { tenant ->
                        val deptsList = mor.tenant.tenantDepartmentInfo.query()
                            .where { it.name match tenant.name }.toList()
                        if(deptsList.size ==0){
                            var deptInner = TenantDepartmentInfo()
                            deptInner.tenant= IdName(tenant.id,tenant.name)
                            deptInner.name = tenant.name
                            batchList.add(deptInner)
                        }
                    }
                    mor.tenant.tenantDepartmentInfo.batchInsert()
                        .apply {
                            addEntities(batchList)
                        }
                        .exec()
                }
            }


        batchList.forEach { dept ->

            var deptPush = DeptDefine()
            deptPush.id = dept.id
            deptPush.name = dept.name
            deptPush.isMain = true

            mor.tenant.tenantUser.update()
                .where { it.tenant.id match  dept.tenant.id }
                .where { it.depts match_size 0    }
                .push { it.depts to deptPush }
                .exec()

            mor.tenant.tenantDepartmentInfo.update()
                .where { it.tenant.id match dept.tenant.id }
                .where { it.parent.id match "" }
                .where { it.name match_not_equal it.tenant.name }
                .set { it.parent.id to deptPush.id }
                .set { it.parent.name to deptPush.name }
                .exec()
        }

    }

}