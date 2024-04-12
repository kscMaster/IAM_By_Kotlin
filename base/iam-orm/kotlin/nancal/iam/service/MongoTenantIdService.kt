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
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Component

@Component
class MongoTenantIdDbService : IMongoEntityQuery, IMongoEntityInsert, IMongoEntityUpdate, IMongoEntityDelete,
    IMongoEntityAggregate {
    companion object {
        private val tenantIdTables by lazy {
            return@lazy mor.tenant.getEntities()
                .filter { it::class.java.getAnnotation(VarDatabase::class.java) != null }
        }
    }


//    fun run(collection: String, isRead: Boolean): MongoTemplate? {
//        var table = tenantIdTables.firstOrNull { it.tableName == collection };
//        if (table == null) return null;
//
//        var varDbKey = table::class.java.getAnnotation(VarDatabase::class.java)?.value.AsString();
//        if (varDbKey.isEmpty()) return null;
//        if (!HttpContext.hasRequest) return null;
//        var request = HttpContext.request;
//
//        var tenantId = request.getHeader("tenant-id").AsString()
//        if (tenantId.isEmpty()) return null;
//
//
//        //看租户是否启用了 isAloneDbMode
//        var tenant = db.getRedisCacheJson(Tenant::class.java, "id", tenantId, "") {
//            return@getRedisCacheJson mor.tenant.tenant.queryById(tenantId).toEntity()
//        }
//
//        if (tenant == null) {
//            return null;
//        }
//
//        if (tenant.aloneDbConnection.isEmpty()) {
//            return null;
//        }
//
//        return db.mongo.getMongoTemplateByUri(getConnectionString(tenant.aloneDbConnection, tenantId))
//    }

    private fun getVarDatabaseValue(tableName: String): String {
        var table = tenantIdTables.firstOrNull { it.tableName == tableName };
        if (table == null) return "";

        var varDbKey = table::class.java.getAnnotation(VarDatabase::class.java)?.value.AsString();
        if (varDbKey.isEmpty()) return "";
        return db.mongo.getMongoColumnName(varDbKey)
    }

    private fun getDs(dbTenantId: String): String {
        var tenantId = dbTenantId;
        if (tenantId.isEmpty() && HttpContext.hasRequest) {
            tenantId = HttpContext.request.getHeader("tenant-id").AsString()
        }

        if (tenantId.isEmpty()) return "";

        var tenant = db.getRedisCacheJson(Tenant::class.java, "id", tenantId, "") {
            return@getRedisCacheJson mor.tenant.tenant.queryById(tenantId).toEntity()
        }

        if (tenant == null) {
            return "";
        }

        if (tenant.aloneDbConnection.isEmpty()) {
            return "";
        }

        return getConnectionString(tenant.aloneDbConnection, tenantId)
    }


    private fun getConnectionString(connString: String, tenantId: String): String {
        return connString.replace("@tenantId@", tenantId)
//        return """mongodb://iam:mp-iam-2021@mongo:27017/iam-@tenantId@""".replace("@tenantId@", tenantId)
    }

    override fun aggregate(query: MongoAggregateClip<*, out Any>, eventData: EventResult) {

    }

    override fun beforeAggregate(query: MongoAggregateClip<*, out Any>): EventResult {
        var varDatabase = getVarDatabaseValue(query.defEntityName);
        if (varDatabase.isEmpty()) return EventResult(true);
        var tenantId = query.pipeLines
            .filter { it.first == "\$match" }
            .map { it.second as Criteria }
            .map { MongoWhereClip(it.criteriaObject).findValueFromRootLevel(varDatabase).AsString() }
            .firstOrNull { it.HasValue }
            .AsString()

        return EventResult(true, null, getDs(tenantId), "")
    }

    override fun beforeDelete(delete: MongoDeleteClip<*>): EventResult {
        var varDatabase = getVarDatabaseValue(delete.defEntityName);
        if (varDatabase.isEmpty()) return EventResult(true);
        var tenantId = delete.whereData.findValueFromRootLevel(varDatabase).AsString();

        return EventResult(true, null, getDs(tenantId), "")
    }

    override fun delete(delete: MongoDeleteClip<*>,  eventData: EventResult) {

    }

    override fun beforeInsert(insert: MongoBaseInsertClip): EventResult {
        var varDatabase = getVarDatabaseValue(insert.defEntityName);
        if (varDatabase.isEmpty()) return EventResult(true);

        var tenantIds = insert.entities.map { MyUtil.getValueByWbsPath(varDatabase).AsString() }.toSet()

        if (tenantIds.any { it.isEmpty() } || tenantIds.size > 1) {
            return EventResult(true);
        }


        return EventResult(true, null, getDs(tenantIds.firstOrNull().AsString()), "")
    }

    override fun insert(insert: MongoBaseInsertClip, eventData: EventResult) {

    }


    override fun beforeQuery(query: MongoBaseQueryClip): EventResult {
        var varDatabase = getVarDatabaseValue(query.defEntityName);
        if (varDatabase.isEmpty()) return EventResult(true);
        var tenantId = query.whereData.findValueFromRootLevel(varDatabase).AsString();

        return EventResult(true, null, getDs(tenantId), "")
    }

    override fun query(query: MongoBaseQueryClip, eventData: EventResult) {

    }

    override fun beforeUpdate(update: MongoBaseUpdateClip): EventResult {
        var varDatabase = getVarDatabaseValue(update.defEntityName);
        if (varDatabase.isEmpty()) return EventResult(true);
        var tenantId = update.whereData.findValueFromRootLevel(varDatabase).AsString();

        return EventResult(true, null, getDs(tenantId), "")
    }

    override fun update(update: MongoBaseUpdateClip,  eventData: EventResult) {

    }
}