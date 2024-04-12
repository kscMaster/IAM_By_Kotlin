package nancal.iam.service.compute

import nancal.iam.db.mongo.AuthResourceConflictPolicyEnum
import nancal.iam.db.mongo.AuthTypeEnum
import nancal.iam.db.mongo.ResourceTypeEnum
import nancal.iam.db.mongo.entity.AuthResourceInfo
import nancal.iam.db.mongo.mor
import nbcp.comm.HasValue
import nbcp.comm.Unwind
import nbcp.db.mongo.*
import org.springframework.stereotype.Service
import java.util.stream.Collectors


@Service
class TenantAppUserGroupAuthResourceService {

    fun getAuthResources(
        authType: AuthTypeEnum,
        groupIds: MutableList<String>,
        tenantId: String,
        appCode: String,
        resourceType: ResourceTypeEnum,
        policy: AuthResourceConflictPolicyEnum
    ): TenantUserService.AllowAndDenyRec {

        var res =  TenantUserService.AllowAndDenyRec()
        var auths: List<AuthResourceInfo> = mutableListOf();

        val queryList = mor.tenant.tenantStandardUserGroupAuthResource.query()
            .where { it.group.id match_in groupIds }
            .where { it.tenant.id match tenantId }
            .where { it.appInfo.code match appCode }
            .whereOr ({ it.resources.type match resourceType },{ it.resources.type.match_isNullOrEmpty() })
            .orderByDesc { it.updateAt }
            .toList()
            .map { it.resources }

        if (queryList.isNotEmpty()) {
            auths = queryList.stream().flatMap { it.stream() }.collect(Collectors.toList())
            auths = auths.filter { it.type == null || it.type  == resourceType }
        }


//        if (auths.map { it.code }.toSet().contains("*") == false) {

            val my_resources = auths.filter { it.isAllow }.toMutableList();

            when (policy) {
                //拒绝优先原则
                AuthResourceConflictPolicyEnum.Deny -> {
                    //定义拒绝的资源  需要返回
                    val denys = auths.filter { it.isAllow == false }.map { it.code };
                    my_resources.removeAll { denys.contains(it.code) }
                    res.allow = my_resources.map { it.code }.toMutableSet()
                    res.deny = denys.toMutableSet()
                    return res
                }

                AuthResourceConflictPolicyEnum.Latest -> {
                    //根据最后一次更新的数据进行覆盖   重新定义一个list  存放结果集
                    val allowResList = mutableSetOf<AuthResourceInfo>();
                    val denyResList = mutableSetOf<AuthResourceInfo>();


                    var allowFlag = true
                    var denyFlag = true

                    auths.distinctBy { it.resourceId }.forEach {
                        if (it.isAllow) {
                            if(allowFlag) {
                                allowResList.add(it)
                                if (it.code.contains("*")) {
                                    denyFlag = false
                                }
                            }
                        } else {
                            if(denyFlag){
                                denyResList.add(it)
                                if(it.code.contains("*")){
                                    allowFlag = false
                                }
                            }
                        }
                    }

                    res.allow = allowResList.map { it.code }.toMutableSet()
                    res.deny = denyResList.map { it.code }.toMutableSet()

                    return res
                }
            }
    }


    fun getAuthResourcesAction(
        authType: AuthTypeEnum,
        groupIds: MutableList<String>,
        tenantId: String,
        appCode: String,
        resourceType: ResourceTypeEnum,
        policy: AuthResourceConflictPolicyEnum
    ): TenantUserService.AllowAndDenyRec {

        val res =  TenantUserService.AllowAndDenyRec()

        val auths = mor.tenant.tenantStandardUserGroupAuthResource.query()
            .where { it.group.id match_in groupIds }
            .where { it.tenant.id match tenantId }
            .where { it.appInfo.code match appCode }
            .whereOr ({ it.resources.type match resourceType },{ it.resources.type.match_isNullOrEmpty() })
            .orderByDesc { it.updateAt }
            .toList()
            .map { it.resources }
            .Unwind()
            .filter {
                it.type == null || it.type  == resourceType
            }


        var allAllowResourceFalse = true
        var allDenyResourceFalse = true

        auths.forEach {
            if(it.code.equals("*") && it.resourceIsAll == true && it.isAllow == true){
                allAllowResourceFalse = false
            }
            if(it.code.equals("*") && it.resourceIsAll == true && it.isAllow == false){
                allDenyResourceFalse = false
            }
        }

        when (policy) {
            //拒绝优先原则
            AuthResourceConflictPolicyEnum.Deny -> {
                //定义拒绝的资源  需要返回
                val denys = mutableListOf<String>()
                val allows = mutableListOf<String>()
                auths.forEach {
                    if (it.isAllow) {
                        if (it.actionIsAll) {
                            if (allAllowResourceFalse) {
                                allows.add(it.code + "&&*")
                            }else {
                                allows.add("*" + "&&*")
                            }
                        } else {
                            if (allAllowResourceFalse) {
                                allows.addAll(
                                    it.action.map { a -> it.code + "&&" + a }
                                )
                            }else {
                                allows.addAll(
                                    it.action.map { a -> "*" + "&&" + "*" }
                                )
                            }
                        }
                    } else {
                        if (it.actionIsAll) {
                            if (allDenyResourceFalse) {
                                denys.add(it.code + "&&*")
                            }else {
                                denys.add("*"+ "&&*")
                            }
                        } else {
                            if (allDenyResourceFalse) {
                                denys.addAll(
                                    it.action.map { a -> it.code + "&&" + a }
                                )
                            }else {
                                denys.addAll(
                                    it.action.map { a -> "*" + "&&" + "*" }
                                )
                            }
                        }
                    }
                }

                allows.removeAll { denys.contains(it) }
                res.allow = allows.toMutableSet()
                res.deny = denys.toMutableSet()
                return res
            }

            AuthResourceConflictPolicyEnum.Latest -> {
                //根据最后一次更新的数据进行覆盖   重新定义一个list  存放结果集
                val allowResList = mutableSetOf<AuthResourceInfo>();
                val denyResList = mutableSetOf<AuthResourceInfo>();

                var allowFlag = true
                var denyFlag = true

                auths.distinctBy { it.resourceId }.forEach {
                    if (it.isAllow) {
                        if(allowFlag) {
                            allowResList.add(it)
                            if (it.code.contains("*")) {
                                denyFlag = false
                            }
                        }
                    } else {
                        if(denyFlag){
                            denyResList.add(it)
                            if(it.code.contains("*")){
                                allowFlag = false
                            }
                        }
                    }
                }

                val denys = mutableListOf<String>()
                val allows = mutableListOf<String>()
                allowResList.forEach {
                    if (it.actionIsAll) {
                        if (allAllowResourceFalse) {
                            allows.add(it.code + "&&*")
                        }else {
                            allows.add("*" + "&&*")
                        }
                    } else {
                        if (allAllowResourceFalse) {
                            allows.addAll(
                                it.action.map { a -> it.code + "&&" + a }
                            )
                        }else {
                            allows.addAll(
                                it.action.map { a -> "*" + "&&" + "*" }
                            )
                        }
                    }
                }
                denyResList.forEach {
                    if (it.actionIsAll) {
                        if (allDenyResourceFalse) {
                            denys.add(it.code + "&&*")
                        }else {
                            denys.add("*" + "&&*")
                        }
                    } else {
                        if (allDenyResourceFalse) {
                            denys.addAll(
                                it.action.map { a -> it.code + "&&" + a }
                            )
                        }else {
                            denys.addAll(
                                it.action.map { a -> "*" + "&&" + "*" }
                            )
                        }
                    }
                }

                res.allow = allows.toMutableSet()
                res.deny = denys.toMutableSet()
                return res
            }
        }
    }


    fun getAuthResourcesV3(
        authType: AuthTypeEnum,
        groupIds: MutableList<String>,
        tenantId: String,
        resourceType: ResourceTypeEnum,
        policy: AuthResourceConflictPolicyEnum
    ): TenantUserService.AllowAndDenyRec {

        var res =  TenantUserService.AllowAndDenyRec()
        var auths: List<AuthResourceInfo> = mutableListOf();

        val queryList = mor.tenant.tenantStandardUserGroupAuthResource.query()
            .where { it.group.id match_in groupIds }
            .where { it.tenant.id match tenantId }
            .whereOr ({ it.resources.type match resourceType },{ it.resources.type.match_isNullOrEmpty() })
            .orderByDesc { it.updateAt }
            .toList()
            .map { it.resources }

        if (queryList.isNotEmpty()) {
            auths = queryList.stream().flatMap { it.stream() }.collect(Collectors.toList())
            auths = auths.filter { it.type == null || it.type  == resourceType }
        }


//        if (auths.map { it.code }.toSet().contains("*") == false) {

        val my_resources = auths.filter { it.isAllow }.toMutableList();

        when (policy) {
            //拒绝优先原则
            AuthResourceConflictPolicyEnum.Deny -> {
                //定义拒绝的资源  需要返回
                val denys = auths.filter { it.isAllow == false }.map { it.code };
                my_resources.removeAll { denys.contains(it.code) }
                res.allow = my_resources.map { it.code }.toMutableSet()
                res.deny = denys.toMutableSet()
                return res
            }

            AuthResourceConflictPolicyEnum.Latest -> {
                //根据最后一次更新的数据进行覆盖   重新定义一个list  存放结果集
                val allowResList = mutableSetOf<AuthResourceInfo>();
                val denyResList = mutableSetOf<AuthResourceInfo>();


                var allowFlag = true
                var denyFlag = true

                auths.distinctBy { it.resourceId }.forEach {
                    if (it.isAllow) {
                        if(allowFlag) {
                            allowResList.add(it)
                            if (it.code.contains("*")) {
                                denyFlag = false
                            }
                        }
                    } else {
                        if(denyFlag){
                            denyResList.add(it)
                            if(it.code.contains("*")){
                                allowFlag = false
                            }
                        }
                    }
                }

                res.allow = allowResList.map { it.code }.toMutableSet()
                res.deny = denyResList.map { it.code }.toMutableSet()

                return res
            }
        }
    }
}