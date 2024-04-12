package nancal.iam.flyway

import nancal.iam.db.mongo.PersonClassifiedEnum
import nancal.iam.db.mongo.entity.tenant.authsource.TenantAuthUpdateDetailLastTime
import nancal.iam.db.mongo.mor
import nbcp.comm.AsDate
import nbcp.comm.AsLocalDateTime
import nbcp.db.FlywayVersionBaseService
import nbcp.db.mongo.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class `-2-InitDb`: FlywayVersionBaseService(-2) {

    override fun exec() {

        mor.tenant.tenantAuthUpdateDetailLastTime.query()
            .toList()
            .apply {

                var aa = TenantAuthUpdateDetailLastTime()
                aa.createAt = LocalDateTime.now().AsDate().AsLocalDateTime()!!
                aa.updateAt = LocalDateTime.now().AsDate().AsLocalDateTime()!!
                if (!this.isEmpty()){
                    mor.tenant.tenantAuthUpdateDetailLastTime.deleteById(this.get(0).id).exec()
                }
                mor.tenant.tenantAuthUpdateDetailLastTime.doInsert(aa)
            }



    }

}