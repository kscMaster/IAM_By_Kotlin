package nancal.iam.service

import nbcp.comm.ListResult
import nbcp.comm.Require
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nbcp.comm.scopes
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AuthSourceService {



    fun userAuthrizatinList(
        @Require userId: String,
        @Require appCode: String, //当列表列新一条后，刷新时使用
        resourceType: ResourceTypeEnum?,
        @Require skip: Int,
        @Require take: Int
    ): ListResult<TenantAppAuthResourceInfo> {

        // 1.根据appId查询应用
        val appObj = mor.iam.sysApplication.query().where { it.appCode match appCode }.toEntity()
        if (appObj == null) {
            return ListResult.error("应用未找到")
        }

        val tenantId = mor.tenant.tenantUser.queryById(userId).toEntity()?.tenant?.id

        if (tenantId == null || tenantId.isBlank()) {
            return ListResult.error("用户ID未找到")
        }
        // 2.根据应用查询该应用下的所有角色ID集合
        val appRoleIds = mor.tenant.tenantAppRole.query()
            .where{ it.tenant.id match tenantId}
            .where { it.appInfo.code match appObj.appCode }
            .select { it.id }
            .toList(String::class.java)

        if (appRoleIds.size == 0) {
            return ListResult.error("该应用没有角色")
        }


        // 3.查询人所拥有的应用下的角色
        val userRoleList = mor.tenant.tenantUser.query()
            .where { it.id match userId }
            .where {
            it.roles.id match_in appRoleIds
        }
            .select { it.roles }.toList()

        val roleIds: MutableList<String> = mutableListOf()
        userRoleList.forEach {
            it.roles.forEach { roleIds.add(it.id) }
        }

        // 4.查询人拥有的角色下的所有资源
        // 目前只查角色
        mor.tenant.tenantAppAuthResourceInfo.query()
            .where { it.tenant.id match tenantId}
            .where { it.appInfo.code match appCode }
            .where { it.type match AuthTypeEnum.Role }
            .where { it.target.id match_in roleIds }
            .apply {
                if (resourceType != null) {
                    this.where { it.auths.type match resourceType }
                }
            }
            .limit(skip, take).orderByDesc { it.createAt }
            .toListResult()
            .apply {
                if (resourceType != null) {
                    this.data.forEach { auth ->
                        auth.auths.removeAll { it.type != resourceType }
                    }
                }
                return this
            }
    }
}