package nancal.iam.flyway

import com.nancal.cipher.SHA256Util
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
class `23-InitDb` : FlywayVersionBaseService(23) {

    override fun exec() {

        BatchReader.init { skip, take ->
            mor.tenant.tenantLoginUser.query().limit(skip, take).toList()
        }.forEach { ent ->
            mor.tenant.tenantLoginUser.update()
                .where { it.id match ent.id }
                .set { it.manualRemindPwdTimes to 0 }
                .set { it.manualExpirePwdTimes to 0 }
                .set { it.autoExpirePwdTimes to 0 }
                .set { it.autoRemindPwdTimes to 0 }
                .exec()
        }


    }

}