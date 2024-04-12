package nancal.iam

import org.junit.jupiter.api.Test
import nancal.iam.db.es.entity.AppLogIndex
import nancal.iam.db.es.esr

import java.time.LocalDateTime

class fixData : TestBase() {

    @Test
    fun abc() {
        var e1 = AppLogIndex()
        e1.visitAt = LocalDateTime.now()

        esr.system.appLogIndex.doInsert(e1)

        println(esr.affectRowCount)
    }
}