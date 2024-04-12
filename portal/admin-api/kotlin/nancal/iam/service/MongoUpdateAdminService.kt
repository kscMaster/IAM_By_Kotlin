package nancal.iam.service

import nbcp.comm.IsSimpleType
import nbcp.db.*
import nbcp.db.mongo.MongoBaseUpdateClip
import nancal.iam.db.mongo.entity.*
import nbcp.db.mongo.event.IMongoEntityUpdate
import nancal.iam.db.mongo.mor
import nbcp.comm.AsString
import org.bson.types.ObjectId
import org.springframework.stereotype.Component


@Component
class MongoUpdateAdminService : IMongoEntityUpdate {
    override fun beforeUpdate(update: MongoBaseUpdateClip): EventResult {


        return EventResult(true)
    }

    override fun update(update: MongoBaseUpdateClip,  eventData: EventResult) {
        if (update.defEntityName != mor.admin.adminUser.tableName) {
            return;
        }


        var idValue = update.whereData.findValueFromRootLevel("_id").AsString();
        if (idValue.isNullOrEmpty()) {
            return;
        }
        var idValueType = idValue::class.java
        if (ObjectId::class.java.isAssignableFrom(idValueType) || idValueType.IsSimpleType()) {
            db.brokeRedisCache(AdminUser::class, "id", idValue.toString())
        }
    }

}

