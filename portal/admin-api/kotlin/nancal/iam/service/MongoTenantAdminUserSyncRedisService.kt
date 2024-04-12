package nancal.iam.service

import nancal.iam.ApiTokenService
import nbcp.comm.ConvertJson
import nbcp.db.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nbcp.comm.IsSimpleType
import nbcp.db.mongo.event.IMongoEntityDelete
import nbcp.db.mongo.event.IMongoEntityInsert
import nbcp.db.mongo.event.IMongoEntityUpdate
import nbcp.utils.SpringUtil
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class MongoTenantAdminUserSyncRedisService : IMongoEntityInsert, IMongoEntityUpdate, IMongoEntityDelete {
    override fun beforeInsert(insert: MongoBaseInsertClip): EventResult {
        return EventResult(true)
    }

    override fun insert(insert: MongoBaseInsertClip, eventData: EventResult) {
        if (insert.defEntityName != mor.tenant.tenantUser.tableName) {
            return;
        }

        var apiTokenService = SpringUtil.getBean<ApiTokenService>()

        insert.entities.forEach {
            var tenantAdminUser = it.ConvertJson(TenantUser::class.java)
            if (tenantAdminUser.adminType != TenantAdminTypeEnum.Super) {
                return@forEach;
            }

            var tenant = mor.tenant.tenant.queryById(tenantAdminUser.tenant.id)
                    .toEntity();

            if (tenant == null) {
                return@forEach
            }

            apiTokenService.loadTenantToRedis(tenant)
        }
    }

    override fun beforeUpdate(update: MongoBaseUpdateClip): EventResult {
        if (update.defEntityName != mor.tenant.tenantUser.tableName) {
            return EventResult(true)
        }

        var query = MongoBaseQueryClip(update.actualTableName);
        query.whereData.addAll(update.whereData)
        query.selectField(mor.tenant.tenantUser.tenant.id.toString())
        query.whereData.addWhere(mor.tenant.tenantUser.adminType.toString(), TenantAdminTypeEnum.Super.toString())
        
        var tenantIds = query.toList(String::class.java).toSet()

        return EventResult(true, tenantIds)
    }

    override fun update(update: MongoBaseUpdateClip, eventData: EventResult) {
        if (update.defEntityName != mor.tenant.tenantUser.tableName) {
            return;
        }
        if (eventData.extData == null) {
            return;
        }

        var apiTokenService = SpringUtil.getBean<ApiTokenService>()

        (eventData.extData as Collection<String>).forEach { tenantId ->
            var tenant = mor.tenant.tenant.queryById(tenantId)
                    .toEntity();

            if (tenant == null) {
                return@forEach
            }

            apiTokenService.loadTenantToRedis(tenant)
        }
    }

    override fun beforeDelete(delete: MongoDeleteClip<*>): EventResult {
        if (delete.defEntityName != mor.tenant.tenantUser.tableName) {
            return EventResult(true);
        }

        var query = MongoBaseQueryClip(delete.actualTableName);
        query.whereData.addAll(delete.whereData)
        query.whereData.addWhere(mor.tenant.tenantUser.adminType.toString(), TenantAdminTypeEnum.Super.toString())
        query.selectField(mor.tenant.tenantUser.tenant.id.toString())
        var tenantIds = query.toList(String::class.java).toSet()

        return EventResult(true, tenantIds);
    }


    override fun delete(delete: MongoDeleteClip<*>, eventData: EventResult) {
        if (delete.defEntityName != mor.tenant.tenantUser.tableName) {
            return;
        }

        if (eventData.extData == null) {
            return;
        }
        var apiTokenService = SpringUtil.getBean<ApiTokenService>()

        (eventData.extData as Collection<String>).forEach { tenantId ->
            var tenant = mor.tenant.tenant.queryById(tenantId)
                    .toEntity();

            if (tenant == null) {
                return@forEach
            }

            apiTokenService.loadTenantToRedis(tenant)
        }
    }
}

