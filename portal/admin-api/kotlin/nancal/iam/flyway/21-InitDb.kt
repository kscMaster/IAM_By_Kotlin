package nancal.iam.flyway

import nancal.iam.db.mongo.ProtocolEnum
import nancal.iam.db.mongo.mor
import nbcp.comm.BatchReader
import nbcp.db.FlywayVersionBaseService
import nbcp.db.mongo.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * @Author zhaopeng
 *
 * @Description
 * @Date 2022/7/6
 */
@Component
class `21-InitDb` : FlywayVersionBaseService(21) {

    override fun exec() {

        BatchReader.init { skip, take ->
            mor.tenant.tenantSecretSet.query().limit(skip, take).toList()
        }.forEach { ent ->
            mor.tenant.tenantSecretSet.update()
                .where { it.id match ent.id }
                .where { it.setting.protocol match_exists  false }
                .set { it.setting.protocol to ProtocolEnum.Self }
                .exec()
        }


    }

}