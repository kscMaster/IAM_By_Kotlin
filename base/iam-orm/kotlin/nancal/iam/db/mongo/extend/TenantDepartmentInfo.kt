package nancal.iam.db.mongo.extend

import nbcp.comm.*
import nbcp.db.IdName
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.table.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*


class TenantDepartmentInfoWithWbs : TenantDepartmentInfo() {
    var wbs: MutableList<IdName> = mutableListOf()
}

/**
 * @name 连续级别的部门名称
 * @return 返回精确的最后一级的部门。
 * @sample queryByDeptFullName("tenantId","a","b","c")
 */
fun TenantGroup.TenantDepartmentInfoEntity.queryByDeptFullName(
    tenantId: String,
    vararg queryDeptNames: String
): ApiResult<DeptDefine> {

    var queryDeptWbs = queryDeptNames.joinToString("/")
    //从数据库中查出 所有name级别的部门

    var db_depts = mor.tenant.tenantDepartmentInfo.query()
        .where { it.tenant.id match tenantId }
        .where { it.name match_in queryDeptNames }
        .toList(TenantDepartmentInfoWithWbs::class.java)

    if (db_depts.map { it.name }.toSet().size != queryDeptNames.toSet().size) {
        return ApiResult.error("找不到部门")
    }

    //计算每个部门的 wbs

    db_depts.forEach {
        it.wbs = getDeptWbs(db_depts, it)
    }

    var findedDepts = db_depts.filter { it.wbs.size == queryDeptNames.size }
        .filter { it.wbs.map { it.name }.joinToString("/") == queryDeptWbs }

    if (findedDepts.any() == false) {
        return ApiResult.error("找不到部门")
    } else if (findedDepts.size == 1) {
        var findedDept = findedDepts.first();
        var dept = DeptDefine()
        dept.id = findedDept.id
        dept.name = findedDept.name
        dept.isMain = false
        return ApiResult.of(dept);
    }
    return ApiResult.error("存在多个部门链路，请核对");

}

private fun getDeptWbs(
    dbDepts: MutableList<TenantDepartmentInfoWithWbs>,
    extendWbs: TenantDepartmentInfoWithWbs
): MutableList<IdName> {
    if (extendWbs.wbs.any()) {
        return extendWbs.wbs;
    }

    if (extendWbs.parent.id.isEmpty()) {
        extendWbs.wbs = mutableListOf(IdName(extendWbs.id, extendWbs.name))
        return extendWbs.wbs;
    }

    var parentNode = dbDepts.find { it.id == extendWbs.parent.id };
    if (parentNode == null) {
        extendWbs.wbs = mutableListOf(IdName(extendWbs.id, extendWbs.name))
        return extendWbs.wbs;
    }


    extendWbs.wbs.addAll(getDeptWbs(dbDepts, parentNode))
    extendWbs.wbs.add(IdName(extendWbs.id, extendWbs.name));
    return extendWbs.wbs;
}

/**
 * 获取部门从根开始的路径
 */
fun TenantGroup.TenantDepartmentInfoEntity.getDeptsWithWbs(
    tenantId: String,
    vararg deptIds: String
): ListResult<TenantDepartmentInfoWithWbs> {
    var list = mor.tenant.tenantDepartmentInfo.aggregate()
        .beginMatch()
        .where { it.tenant match tenantId }
        .where { it.id match_in deptIds }
        .endMatch()
        .addGraphLookup({ it.parent.id }, { it.id }, "wbs")
        .toList(TenantDepartmentInfoWithWbs::class.java)

    list.forEach { dept ->
        dept.wbs = dept.wbs.reversed().toMutableList()
        dept.wbs.add(IdName(dept.id, dept.name))
    }

    return ListResult.of(list)
}

/**
 * 根据部门ids获取全部上级部门
 */
fun TenantGroup.TenantDepartmentInfoEntity.getParentDepts(
    tenantId: String,
    deptIds: List<String>,
    hasThis: Boolean
): MutableList<TenantDepartmentInfo> {
    val parentList: MutableList<TenantDepartmentInfo> = mutableListOf()

    val depts = mor.tenant.tenantDepartmentInfo.query()
        .where { it.id match_in  deptIds }
        .toList()

    if (depts.isEmpty()){
        return parentList
    }

    val allList = mor.tenant.tenantDepartmentInfo.query()
        .where { it.tenant.id match tenantId }
        .where { it.id match_notin deptIds }
        .toList()

    depts.forEach { dept ->
        findParents(dept, parentList, allList)
    }

    if (hasThis) {
        parentList.addAll(depts)
    }

    return parentList
}

private fun findParents(
    dept: TenantDepartmentInfo,
    parentList: MutableList<TenantDepartmentInfo>,
    allList: List<TenantDepartmentInfo>
) {
    if (dept.parent.id.isNotEmpty()) {
        val parent = allList.filter { it.id == dept.parent.id }
        if (parent.isNotEmpty()) {
            val first = parent.first()
            parentList.add(first)
            findParents(first, parentList, allList)
        }
    }
}

