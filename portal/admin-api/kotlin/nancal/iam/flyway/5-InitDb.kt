package nancal.iam.flyway

import nancal.iam.db.mongo.entity.TenantDepartmentInfo
import nancal.iam.db.mongo.mor
import nbcp.db.FlywayVersionBaseService
import nbcp.db.mongo.match
import nbcp.db.mongo.query
import nbcp.db.mongo.updateById
import org.springframework.stereotype.Component

/**
 * @Author wrk
 *
 * @Description 部门人数
 * @Date 2022/3/9-17:36
 */
@Component
class `5-InitDb` : FlywayVersionBaseService(5) {

    override fun exec() {
        initData()
    }

    fun initData() {
        println("初始化：更新部门人数")
        //更新部门人数
        val tenantIds = mor.tenant.tenant.query()
            .toList()
            .map { it.id }
        var i=0
        tenantIds.forEach {
            initTenantDepUserCount(it)
            println("租户--${it}--部门人数初始化结束---------->第${i}个")
            i++
        }


    }

    fun getDepts(tenantId: String): MutableMap<String, MutableList<String>> {
        val depdb = mor.tenant.tenantDepartmentInfo.query()
            .where { it.tenant.id match tenantId }
            .toList()
        val depts: MutableMap<String, MutableList<String>> = mutableMapOf()
        depdb.forEach {
            val ids: MutableList<String> = mutableListOf()
            getChilDep(it.id, depdb, ids)

            depts.set(it.id, ids)
        }
        return depts
    }

    fun getChilDep(fatherId: String, depdb: List<TenantDepartmentInfo>, ids: MutableList<String>) {
        val idss = depdb.filter { it.parent.id == fatherId }.map { it.id }.toMutableList()
        if (idss.size > 0) {
            ids.addAll(idss)
        }else{
            return
        }
        idss.forEach {
            //脏数据检查，防止出错,父部门与子部门互为父子部门
            if(! ids.contains(fatherId))  getChilDep(it, depdb, ids)

        }
    }

    fun initTenantDepUserCount(tenantId: String) {
        val countData: MutableMap<String, Int> = mutableMapOf()
        val depts = getDepts(tenantId)
        depts.keys.forEach { deptId ->
            var deptIds = mutableSetOf<String>()
            deptIds.add(deptId);
            deptIds.addAll(depts.get(deptId)!!);
            var userCount = mor.tenant.tenantUser.query()
                .where { it.tenant.id match tenantId }
                .where { it.depts.id match_in deptIds }
                .count()
            countData.put(deptId, userCount)
        }
        //更新到部门表
        countData.keys.forEach { deptId ->
            mor.tenant.tenantDepartmentInfo.updateById(deptId)
                .set { it.userCount to countData.get(deptId) }
                .exec()
        }
    }
}