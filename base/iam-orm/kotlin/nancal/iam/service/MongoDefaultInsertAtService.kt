package nancal.iam.service

import nbcp.base.mvc.*
import nbcp.web.*
import nbcp.comm.*
import nbcp.db.VarDatabase
import nbcp.db.db
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.mongo.mor
import nbcp.db.EventResult
import nbcp.db.mongo.*
import nbcp.db.mongo.event.*
import nbcp.utils.MyUtil
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class MongoDefaultInsertAtService : IMongoEntityInsert {
    companion object {
    }

    override fun beforeInsert(insert: MongoBaseInsertClip): EventResult {
        insert.entities.forEach { entity->
            MyUtil.setValueByWbsPath(entity,"updateAt", value = LocalDateTime.now())
        }

        return EventResult(true, null, "", "")
    }

    override fun insert(insert: MongoBaseInsertClip, eventData: EventResult) {

    }

}