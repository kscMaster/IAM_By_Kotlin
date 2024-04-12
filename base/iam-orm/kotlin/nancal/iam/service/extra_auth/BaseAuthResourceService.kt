package nancal.iam.service.extra_auth

import nancal.iam.db.mongo.AuthTypeEnum
import nancal.iam.db.mongo.entity.AuthResourceInfo
import nancal.iam.db.mongo.entity.TenantAppAuthResourceInfo
import nancal.iam.db.mongo.entity.tenant.authsource.*
import nbcp.db.mongo.match
import nancal.iam.db.mongo.mor
import nbcp.comm.Unwind
import nbcp.db.mongo.delete
import nbcp.db.mongo.query

class BaseAuthResourceService {

    fun betterControlAuthResource(
        authObj: TenantAppAuthResourceInfo,
        type: String,
        deptAuthList: MutableList<TenantDeptAuthResourceInfo>
    ) {

        var listTemp : List<AuthResourceInfo> = mutableListOf()
        var listTempDept : List<TenantDeptAuthResourceInfo> = mutableListOf()
        if(type != AuthTypeEnum.Dept.name) {
            val authLists = authObj.auths.distinctBy {
                listOf(
                    it.type,
                    it.isAllow,
                    it.action,
                    it.resourceId,
                    it.actionIsAll,
                    it.resourceIsAll,
                    it.code,
                    it.name,
                    it.resource
                )
            }
            listTemp =authLists
        }else{
            val authLists = deptAuthList.distinctBy {
                listOf(
                    it.type,
                    it.isAllow,
                    it.action,
                    it.resourceId,
                    it.actionIsAll,
                    it.resourceIsAll,
                    it.code,
                    it.name,
                    it.resource,
                    it.heredity
                )
            }
            listTempDept =authLists
        }

        val blankList = ArrayList(listTemp)
        val blankListDept = ArrayList(listTempDept)

        if (type == AuthTypeEnum.People.name) {
            val tenantUserAuth = TenantStandardUserAuthResource(
                authObj.appInfo, authObj.tenant, authObj.target, blankList
            )
            mor.tenant.tenantStandardUserAuthResource.delete()
                .where { it.user.id match authObj.target.id }
                .where { it.appInfo.code match authObj.appInfo.code }
                .exec()
            if(blankList.size>0){
                mor.tenant.tenantStandardUserAuthResource.doInsert(tenantUserAuth)
            }
        }
        if (type == AuthTypeEnum.Role.name) {
            val tenantRoleAuth = TenantStandardRoleAuthResource(
                authObj.appInfo, authObj.tenant, authObj.target, blankList
            )
            mor.tenant.tenantStandardRoleAuthResource.delete()
                .where { it.role.id match authObj.target.id }
                .where { it.appInfo.code match authObj.appInfo.code }
                .exec()
            if(blankList.size>0){
                mor.tenant.tenantStandardRoleAuthResource.doInsert(tenantRoleAuth)
            }
        }
        if (type == AuthTypeEnum.Group.name) {
            val tenantGroupAuth = TenantStandardUserGroupAuthResource(
                authObj.appInfo, authObj.tenant, authObj.target, blankList,
            )
            mor.tenant.tenantStandardUserGroupAuthResource.delete()
                .where { it.group.id match authObj.target.id }
                .where { it.appInfo.code match authObj.appInfo.code }
                .exec()
            if(blankList.size>0){
                mor.tenant.tenantStandardUserGroupAuthResource.doInsert(tenantGroupAuth)
            }
        }
        if (type == AuthTypeEnum.Dept.name) {
            val tenantDeptAuth = TenantStandardDeptAuthResource(
                authObj.appInfo, authObj.tenant, authObj.target, blankListDept
            )
            mor.tenant.tenantStandardDeptAuthResource.delete()
                .where { it.dept.id match authObj.target.id }
                .where { it.appInfo.code match authObj.appInfo.code }
                .exec()
            if(blankListDept.size > 0){
                mor.tenant.tenantStandardDeptAuthResource.doInsert(tenantDeptAuth)
            }
        }
    }


    fun deptBetterControl(
        type: String,
        authObj: TenantAppAuthResourceInfo,
        baseAuthResourceService: BaseAuthResourceService,
        isDelete :Boolean
    ) {
        var authsChilFalse = mor.tenant.tenantAppAuthResourceInfo.query()
            .where { it.type match type }
            .where { it.target.id match authObj.target.id }
            .where { it.tenant.id match authObj.tenant.id }
            .where { it.appInfo.code match authObj.appInfo.code }
            .apply {
                if(isDelete){
                    this.where { it.id match_not_equal authObj.id }
                }
            }
            .where { it.childDeptsAll match false }
            .orderByDesc { it.updateAt }
            .toList()
            .map { it.auths }
            .Unwind()


        var falseList = mutableListOf<TenantDeptAuthResourceInfo>()

        authsChilFalse.forEach {
            falseList.add(TenantDeptAuthResourceInfo().apply {
                this.heredity = false
                this.id = it.id
                this.code = it.code
                this.action = it.action
                this.isAllow = it.isAllow
                this.actionIsAll = it.actionIsAll
                this.name = it.name
                this.resource = it.resource
                this.resourceId = it.resourceId
                this.resourceIsAll = it.resourceIsAll
                this.rules = it.rules
                this.type = it.type
            })
        }

        var trueList = mutableListOf<TenantDeptAuthResourceInfo>()

        var authsChilTrue = mor.tenant.tenantAppAuthResourceInfo.query()
            .where { it.type match type }
            .where { it.target.id match authObj.target.id }
            .where { it.tenant.id match authObj.tenant.id }
            .where { it.appInfo.code match authObj.appInfo.code }
            .apply {
                if(isDelete){
                    this.where { it.id match_not_equal authObj.id }
                }
            }
            .where { it.childDeptsAll match true }
            .orderByDesc { it.updateAt }
            .toList()
            .map { it.auths }
            .Unwind()


        authsChilTrue.forEach {
            trueList.add(TenantDeptAuthResourceInfo().apply {
                this.heredity = true
                this.id = it.id
                this.code = it.code
                this.action = it.action
                this.isAllow = it.isAllow
                this.actionIsAll = it.actionIsAll
                this.name = it.name
                this.resource = it.resource
                this.resourceId = it.resourceId
                this.resourceIsAll = it.resourceIsAll
                this.rules = it.rules
                this.type = it.type
            })
        }
        falseList.addAll(trueList)
        baseAuthResourceService.betterControlAuthResource(authObj, type, falseList)
    }

}