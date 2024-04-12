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

@Component
class MongoDefaultOrderByDescUpdateAtService : IMongoEntityQuery {
    companion object {
    }

    override fun beforeQuery(query: MongoBaseQueryClip): EventResult {
        if (query.sort.isEmpty()) {
            query.sort.put("createAt", -1);
        }

        return EventResult(true, null, "", "")
    }

    override fun query(query: MongoBaseQueryClip, eventData: EventResult) {

    }

}