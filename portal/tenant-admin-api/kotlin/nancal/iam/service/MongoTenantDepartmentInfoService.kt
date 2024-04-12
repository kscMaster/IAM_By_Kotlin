package nancal.iam.service


import nbcp.comm.*
import nbcp.db.EventResult
import nbcp.db.db
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nbcp.db.mongo.event.IMongoEntityDelete
import nbcp.db.mongo.event.IMongoEntityInsert
import nbcp.db.mongo.event.IMongoEntityUpdate
import nancal.iam.db.redis.rer
import nbcp.db.IdName
import nbcp.utils.MyUtil
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class MongoTenantUserService : IMongoEntityUpdate, IMongoEntityInsert, IMongoEntityDelete {

    private fun work(tenantId: String, oriVersion: String) {
        var depts = getDepts(tenantId)
        if (depts.any() == false) {
            rer.iamUser.deptCount(tenantId).deleteKey();
            return;
        }

        var data = mutableMapOf<String, Int>()
        var version = "";
        depts.keys.all { deptId ->

            version = rer.iamUser.deptCount(tenantId).get();
            if (oriVersion != version) {
                return@all false
            }


            var deptIds = mutableSetOf<String>()
            deptIds.add(deptId);
            deptIds.addAll(depts.get(deptId)!!);

            var count = mor.tenant.tenantUser.query()
                    .where { it.depts.id match_in deptIds }
                    .count();

            data.put(deptId, count);

            return@all true
        }

        if (oriVersion != version) {
            work(tenantId, version);
            return;
        }

        //更新到部门表。
        data.keys.forEach { deptId ->
            mor.tenant.tenantDepartmentInfo.updateById(deptId)
                    .set { it.userCount to data.get(deptId) }
                    .exec()
        }

        rer.iamUser.deptCount(tenantId).deleteKey();
    }

    /**
     * 计算部门人员
     */
    @Scheduled(fixedDelay = 60_000)
    fun computeDepartmentUserCountTask() {
        println("执行定时任务：更新部门人数")
        //TODO: 放列表
        db.redis.scanKeys("mp",   "deptCount:*")
                .apply {
                    if (this.size == 0) {
                        return;
                    }
                }
                .forEach {
//                var tenantId = it.Slice(rer.iamUser.deptCount.group.length + 1)
                    var tenantId = it.split(":").get(it.split(":").size - 1)
                    var v = rer.iamUser.deptCount(tenantId).get()
                    work(tenantId, v);
                }
    }

    private fun startWork(tenantId: String) {
        var ver = LocalDateTime.now().toNumberString()
        rer.iamUser.deptCount(tenantId).set(ver)
    }

    override fun beforeUpdate(update: MongoBaseUpdateClip): EventResult {
        return EventResult(true)
    }

    override fun update(update: MongoBaseUpdateClip, eventData: EventResult) {
        if (update.defEntityName != mor.tenant.tenantUser.tableName) return;

        if (update.setData.any { it.key.startsWith("depts") } == false) {
            return;
        }

        var tenantId =
                update.whereData.findValueFromRootLevel(mor.tenant.tenantUser.tenant.id.toString()).AsString()
        if (tenantId.HasValue) {
            startWork(tenantId);
            return;
        }

        //需要二次查询
        mor.tenant.tenantUser.query()
                .apply {
                    this.whereData.addAll(update.whereData);
                }
                .select { it.tenant.id }
                .toList(String::class.java)
                .toSet()
                .forEach { tenantId ->
                    startWork(tenantId)
                }
    }

    override fun beforeDelete(delete: MongoDeleteClip<*>): EventResult {
        if (delete.defEntityName != mor.tenant.tenantUser.tableName) return EventResult(true);

        var list = mutableSetOf<String>()
        var tenantId =
                delete.whereData.findValueFromRootLevel(mor.tenant.tenantUser.tenant.id.toString()).AsString()
        if (tenantId.HasValue) {
            list.add(tenantId)
        } else {
            mor.tenant.tenantUser.query()
                    .apply {
                        this.whereData.addAll(delete.whereData);
                    }
                    .select { it.tenant.id }
                    .toList(String::class.java)
                    .forEach { tenantId ->
                        list.add(tenantId)
                    }
        }
        return EventResult(true, list)
    }

    override fun delete(delete: MongoDeleteClip<*>, eventData: EventResult) {
        if (eventData.extData == null) return;
        var list = eventData.extData as MutableSet<String>

        list.forEach {
            startWork(it);
        }
    }

    override fun beforeInsert(insert: MongoBaseInsertClip): EventResult {
        return EventResult(true)
    }

    override fun insert(insert: MongoBaseInsertClip, eventData: EventResult) {
        if (insert.defEntityName != mor.tenant.tenantUser.tableName) return;

        var list = mutableSetOf<String>()
        insert.entities.forEach {
            /* var tenantId =
                 MyUtil.getValueByWbsPath(it, mor.tenant.tenantUser.tenant.id.toString()).AsString();*/
           /* val tenant =
                    MyUtil.getValueByWbsPath(it, "tenant").AsString();*/
            val tenantUser=it.ConvertJson(TenantUser::class.java)
            var tenantId = tenantUser.tenant.id

            if (tenantId.HasValue) {
                list.add(tenantId);
            }
        }

        list.forEach {
            startWork(it);
        }
    }

    fun getDepts(tenantId: String): MutableMap<String, MutableList<String>> {
        val depdb = mor.tenant.tenantDepartmentInfo.query()
                .where { it.tenant.id match tenantId }
                .toList()
        val depts: MutableMap<String, MutableList<String>> = mutableMapOf()
        depdb.forEach {
            var ids: MutableList<String> = mutableListOf()
            getChilDep(it.id, depdb, ids)
            depts.set(it.id, ids)
        }
        return depts
    }

    fun getChilDep(fatherId: String, depdb: List<TenantDepartmentInfo>, ids: MutableList<String>) {
        var idss = depdb.filter { it.parent.id == fatherId }.map { it.id }.toMutableList()
        if (idss.size > 0) {
            ids.addAll(idss)
        }
        idss.forEach {
            getChilDep(it, depdb, ids)
        }
    }

}