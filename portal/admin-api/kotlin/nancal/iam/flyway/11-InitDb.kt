package nancal.iam.flyway

import nancal.iam.db.mongo.PersonClassifiedEnum
import nancal.iam.db.mongo.mor
import nbcp.db.FlywayVersionBaseService
import nbcp.db.mongo.update
import org.springframework.stereotype.Component

@Component
class `11-InitDb`: FlywayVersionBaseService(11) {

    override fun exec() {
        initPersonClassified()
    }

    fun initPersonClassified() {
        println("--------初始化人员密级字段")
        mor.tenant.tenantUser.update()
            .where{it.id match_not_equal ""}
            .set { it.personClassified to PersonClassifiedEnum.None }
            .exec()
    }
}