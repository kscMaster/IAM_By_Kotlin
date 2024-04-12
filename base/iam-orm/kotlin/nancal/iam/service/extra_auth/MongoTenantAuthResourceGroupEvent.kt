package nancal.iam.service.extra_auth

import nancal.iam.db.mongo.AuthTypeEnum
import nancal.iam.db.mongo.DataActionEnum
import nancal.iam.db.mongo.entity.TenantAuthResourceGroup
import nancal.iam.db.mongo.entity.tenant.authsource.*
import nancal.iam.db.mongo.mor
import nbcp.comm.AsString
import nbcp.comm.ConvertJson
import nbcp.comm.Unwind
import nbcp.db.EventResult
import nbcp.db.mongo.*
import nbcp.db.mongo.event.*
import org.springframework.stereotype.Component

@Component
class MongoTenantAuthResourceGroupEvent : IMongoEntityInsert, IMongoEntityUpdate, IMongoEntityDelete {

    override fun beforeDelete(delete: MongoDeleteClip<*>): EventResult {
        if (delete.defEntityName == mor.tenant.tenantAuthResourceGroup.tableName) {

            val id = delete.whereData.findValueFromRootLevel("_id").AsString()
            val entity = mor.tenant.tenantAuthResourceGroup.queryById(id).toEntity() ?: return EventResult(true, null)

            syncStandardData(entity, DataActionEnum.Delete)
        }
        return EventResult(true, null)
    }

    override fun delete(delete: MongoDeleteClip<*>, eventData: EventResult) {
    }

    override fun beforeInsert(insert: MongoBaseInsertClip): EventResult {
        return EventResult(true, null)
    }

    override fun insert(insert: MongoBaseInsertClip, eventData: EventResult) {
        if (insert.defEntityName == mor.tenant.tenantAuthResourceGroup.tableName) {
            //暂不考虑批量新增
            val entity = insert.entities.get(0).ConvertJson(TenantAuthResourceGroup::class.java)

            syncStandardData(entity, DataActionEnum.Insert)
        }
    }

    override fun beforeUpdate(update: MongoBaseUpdateClip): EventResult {
        return EventResult(true, null)
    }

    override fun update(update: MongoBaseUpdateClip, eventData: EventResult) {
        if (update.defEntityName == mor.tenant.tenantAuthResourceGroup.tableName) {

            val id = update.whereData.findValueFromRootLevel("_id").AsString()
            val entity = mor.tenant.tenantAuthResourceGroup.queryById(id).toEntity() ?: return

            syncStandardData(entity, DataActionEnum.Update)
        }
    }

    /**
     * 同步标准数据表
     */
    fun syncStandardData(entity: TenantAuthResourceGroup, action: DataActionEnum) {

        //根据租户、授权主体查询授权定义表数据，更新时间降序
        val definedData = mor.tenant.tenantAuthResourceGroup.query()
            .where { it.type match entity.type }
            .where { it.target.id match entity.target.id }
            .where { it.tenant.id match entity.tenant.id }
            .apply {
                if (action == DataActionEnum.Delete) {
                    this.where { it.id match_not_equal entity.id }
                }
            }
            .orderByDesc { it.updateAt }
            .toList()
            .apply {
                if (this.isEmpty()) {
                    return
                }
            }

        //授权部门数据需要设置子部门继承
        if (entity.type == AuthTypeEnum.Dept) {
            //设置子部门继承，并去重
            val resourceGroups = definedData.map { targ ->
                targ.auths.map { arg ->
                    TenantDeptAuthResourceGroup().apply {
                        this.heredity = targ.heredity
                        this.isAllow = arg.isAllow
                        this.code = arg.code
                        this.name = arg.name
                        this.id = arg.id
                    }
                }
            }.Unwind().distinctBy {
                listOf(it.id, it.isAllow, it.heredity)
            }.toMutableList()

            //保存部门标准数据
            if (action == DataActionEnum.Insert) {
                TenantStandardDeptAuthResource().apply {
                    this.tenant = entity.tenant
                    this.dept = entity.target
                    this.resourceGroups = resourceGroups

                    mor.tenant.tenantStandardDeptAuthResource.doInsert(this)
                }
            } else {
                mor.tenant.tenantStandardDeptAuthResource.update()
                    .set { it.resourceGroups to this }
                    .where { it.dept.id match entity.target.id }
                    .where { it.tenant.id match entity.tenant.id }
                    .exec()
            }
        } else {
            //授权定义数据去重
            val resourceGroups = definedData.map { it.auths }
                .Unwind()
                .distinctBy {
                    Pair(it.id, it.isAllow)
                }.toMutableList()

            //保存用户标准数据
            if (entity.type == AuthTypeEnum.People) {
                if (action == DataActionEnum.Insert) {
                    TenantStandardUserAuthResource().apply {
                        this.tenant = entity.tenant
                        this.user = entity.target
                        this.resourceGroups = resourceGroups

                        mor.tenant.tenantStandardUserAuthResource.doInsert(this)
                    }
                } else {
                    mor.tenant.tenantStandardUserAuthResource.update()
                        .set { it.resourceGroups to resourceGroups }
                        .where { it.user.id match entity.target.id }
                        .where { it.tenant.id match entity.tenant.id }
                        .exec()
                }
            }

            //保存用户组标准数据
            if (entity.type == AuthTypeEnum.Group) {
                if (action == DataActionEnum.Insert) {
                    TenantStandardUserGroupAuthResource().apply {
                        this.tenant = entity.tenant
                        this.group = entity.target
                        this.resourceGroups = resourceGroups

                        mor.tenant.tenantStandardUserGroupAuthResource.doInsert(this)
                    }
                } else {
                    mor.tenant.tenantStandardUserGroupAuthResource.update()
                        .set { it.resourceGroups to resourceGroups }
                        .where { it.group.id match entity.target.id }
                        .where { it.tenant.id match entity.tenant.id }
                        .exec()
                }
            }
        }
    }
}