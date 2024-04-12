package nancal.iam.service.compute

import nancal.iam.db.mongo.AuthResourceConflictPolicyEnum
import nancal.iam.db.mongo.AuthTypeEnum
import nancal.iam.db.mongo.ResourceTypeEnum
import nancal.iam.db.mongo.entity.AuthResourceInfo
import nancal.iam.db.mongo.mor
import nbcp.comm.HasValue
import nbcp.comm.JsonMap
import nbcp.comm.Unwind
import nbcp.db.db
import nbcp.db.mongo.*
import org.springframework.stereotype.Service
import java.util.stream.Collector
import java.util.stream.Collectors


@Service
class TenantAppRoleAuthResourceService {

    //此处authType仍然为传值  方便后续可能的修改
    fun getAuthResourcesNew(
        authType: AuthTypeEnum,
        tenantId: String,
        appCode: String,
        resourceType: ResourceTypeEnum,
        policy: AuthResourceConflictPolicyEnum,
        roleIds: List<String>,
    ): TenantUserService.AllowAndDenyRec {

        var res =  TenantUserService.AllowAndDenyRec()
        var auths: List<AuthResourceInfo> = mutableListOf();
        //从tenantAppAuthResourceInfo  查询   ->  tenantStandardRoleAuthResource  查询

        // 如果根据role 来查询
        val queryList = mor.tenant.tenantStandardRoleAuthResource.query()
            .where { it.role.id match_in roleIds }
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

        //有选择的资源  不是所有资源的情况
//        if (auths.map { it.code }.toSet().contains("*") == false) {

            val my_resources = auths.filter { it.isAllow }.toMutableList();
            //policy枚举进行的操作
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

                //todo 最后更新时间优先 实际上已经去重  只会更新时间    建议更新列表中的最后一个同类code的isAllow  这样更简单
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
//        }


    }

    fun getAuthResourcesAction(
        authType: AuthTypeEnum,
        tenantId: String,
        appCode: String,
        resourceType: ResourceTypeEnum,
        policy: AuthResourceConflictPolicyEnum,
        roleIds: List<String>,
    ): TenantUserService.AllowAndDenyRec {

        val res =  TenantUserService.AllowAndDenyRec()

        // 查询角色授权标准表
        val auths = mor.tenant.tenantStandardRoleAuthResource.query()
            .where { it.role.id match_in roleIds }
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
        //policy枚举进行的操作
        when (policy) {
            //拒绝优先原则
            AuthResourceConflictPolicyEnum.Deny -> {
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

            //todo 最后更新时间优先 实际上已经去重  只会更新时间    建议更新列表中的最后一个同类code的isAllow  这样更简单
            AuthResourceConflictPolicyEnum.Latest -> {
                //根据最后一次更新的数据进行覆盖   重新定义一个list  存放结果集
                val allowResList = mutableSetOf<AuthResourceInfo>()
                val denyResList = mutableSetOf<AuthResourceInfo>()


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


                //action处理
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

    fun getAuthResources(
        tenantId: String,
        appCode: String,
        resourceType: ResourceTypeEnum,
        policy: AuthResourceConflictPolicyEnum,
        roleIds: List<String>,
    ): Set<String> {

        var auths: List<AuthResourceInfo> = mutableListOf();



        auths = mor.tenant.tenantAppAuthResourceInfo
            .aggregate()
            .beginMatch()
            .where { it.type match AuthTypeEnum.Role }
            .where { it.target.id match_in roleIds }
            .where { it.tenant.id match tenantId }
            .where { it.appInfo.code match appCode }
            .where { it.auths.type match resourceType }
            .endMatch()
            .addPipeLine(
                PipeLineEnum.project, JsonMap(
                    "auths" to db.mongo.filter(
                        "\$auths", "item",
                        (MongoColumnName("\$item.type") match resourceType).toExpression()
                    )
                )
            )
            .toList()
            .map { it.auths }
            .Unwind()
            .filter {
                it.type == resourceType
            }

        if (auths.isEmpty()) {
            return setOf();
        }

        if (auths.map { it.code }.toSet().contains("*") == false) {

            var my_resources = auths.filter { it.isAllow }.toMutableList();
            var denys = auths.filter { it.isAllow == false }.map { it.code };
            my_resources.removeAll { denys.contains(it.code) }
            return my_resources.map { it.code }.toSet()
        }

        var list = mutableListOf<String>()
        var allResource = mor.tenant.tenantResourceInfo(tenantId).query()
            .where { it.type match resourceType }
            .toList()

        auths.filter { it.isAllow }
            .forEach { author ->
                if (author.code == "*") {
                    list.addAll(allResource.map { it.code })

                    return@forEach
                }


                list.addAll(allResource.filter { it.code == author.code }
                    .map { it.code })

            }

        auths.filter { it.isAllow }
            .forEach { author ->
                if (author.code == "*") {
                    list.clear()
                    return@forEach
                }

                if (author.code.contains("*")) {
                    var authReg = author.code.split("*").filter { it.HasValue }.joinToString(".*").toRegex()
                    list.removeAll { authReg.matches(it) }
                    return@forEach
                }

                list.removeAll { it == author.code }
            }


        return list.toSet()
    }

    fun getAuthResourcesNewV3(
        authType: AuthTypeEnum,
        tenantId: String,
        resourceType: ResourceTypeEnum,
        policy: AuthResourceConflictPolicyEnum,
        roleIds: List<String>,
    ): TenantUserService.AllowAndDenyRec {

        var res =  TenantUserService.AllowAndDenyRec()
        var auths: List<AuthResourceInfo> = mutableListOf();
        //从tenantAppAuthResourceInfo  查询   ->  tenantStandardRoleAuthResource  查询

        // 如果根据role 来查询
        val queryList = mor.tenant.tenantStandardRoleAuthResource.query()
            .where { it.role.id match_in roleIds }
            .where { it.tenant.id match tenantId }
            .whereOr ({ it.resources.type match resourceType },{ it.resources.type.match_isNullOrEmpty() })
            .orderByDesc { it.updateAt }
            .toList()
            .map { it.resources }

        if (queryList.isNotEmpty()) {
            auths = queryList.stream().flatMap { it.stream() }.collect(Collectors.toList())
            auths = auths.filter { it.type == null || it.type  == resourceType }
        }

        //有选择的资源  不是所有资源的情况
//        if (auths.map { it.code }.toSet().contains("*") == false) {

        val my_resources = auths.filter { it.isAllow }.toMutableList();
        //policy枚举进行的操作
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

            //todo 最后更新时间优先 实际上已经去重  只会更新时间    建议更新列表中的最后一个同类code的isAllow  这样更简单
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