package nancal.iam.service.compute

import nancal.iam.db.mongo.AuthResourceConflictPolicyEnum
import nancal.iam.db.mongo.AuthTypeEnum
import nancal.iam.db.mongo.ResourceTypeEnum
import nancal.iam.db.mongo.entity.AuthResourceInfo
import nancal.iam.db.mongo.extend.getParentDepts
import nancal.iam.db.mongo.mor
import nbcp.comm.HasValue
import nbcp.comm.Unwind
import nbcp.db.mongo.*
import org.springframework.stereotype.Service
import java.util.stream.Collectors


@Service
class TenantAppDeptAuthResourceService {

    fun getAuthResources(
        authType: AuthTypeEnum,
        deptIds: MutableList<String>,
        tenantId: String,
        appCode: String,
        resourceType: ResourceTypeEnum,
        policy: AuthResourceConflictPolicyEnum,
    ): TenantUserService.AllowAndDenyRec {

        var res = TenantUserService.AllowAndDenyRec()
        var auths: List<AuthResourceInfo> = mutableListOf();

        //查询本部门的资源
        val queryList = mor.tenant.tenantStandardDeptAuthResource.query()
            .where { it.dept.id match_in deptIds }
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

        //获取所有父部门  对部门进行二次查询   查询   就算本部门没有资源  看是否上级部门有授权的资源继承过来
        val deptPIds = mor.tenant.tenantDepartmentInfo.getParentDepts(tenantId, deptIds, true).stream().map { it.id }
            .collect(Collectors.toList())

        //查询父部门 Pids 的资源  且  heredity = true
        val deptPIdAuths = mor.tenant.tenantStandardDeptAuthResource.query()
            .where { it.dept.id match_in deptPIds }
            .where { it.resources.heredity match true }
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





//        if (auths.map { it.code }.toMutableSet().contains("*") == false) {

            val my_resources = deptPIdAuths.filter { it.isAllow }.toMutableList();

            //todo  如何考虑子部门
            when (policy) {
                //拒绝优先原则
                AuthResourceConflictPolicyEnum.Deny -> {
                    //定义拒绝的资源  需要返回
                    val denys = deptPIdAuths.filter { it.isAllow == false }.map { it.code };
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

                    deptPIdAuths.distinctBy { it.resourceId }.forEach {
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
        deptIds: MutableList<String>,
        tenantId: String,
        appCode: String,
        resourceType: ResourceTypeEnum,
        policy: AuthResourceConflictPolicyEnum,
    ): TenantUserService.AllowAndDenyRec {

        val res =  TenantUserService.AllowAndDenyRec()

        //查询本部门的资源
        val auths = mor.tenant.tenantStandardDeptAuthResource.query()
            .where { it.dept.id match_in deptIds }
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


        //获取所有父部门  对部门进行二次查询   查询   就算本部门没有资源  看是否上级部门有授权的资源继承过来
        val deptPIds = mor.tenant.tenantDepartmentInfo.getParentDepts(tenantId, deptIds, true).stream().map { it.id }
            .collect(Collectors.toList())

        //查询父部门 Pids 的资源  且  heredity = true
        val deptPIdAuths = mor.tenant.tenantStandardDeptAuthResource.query()
            .where { it.dept.id match_in deptPIds }
            .where { it.resources.heredity match true }
            .where { it.tenant.id match tenantId }
            .where { it.appInfo.code match appCode }
            .whereOr ({ it.resources.type match resourceType },{ it.resources.type.match_isNullOrEmpty() })
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
                deptPIdAuths.forEach {
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

                deptPIdAuths.distinctBy { it.resourceId }.forEach {
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


    fun getAuthResourcesV3(
        authType: AuthTypeEnum,
        deptIds: MutableList<String>,
        tenantId: String,
        resourceType: ResourceTypeEnum,
        policy: AuthResourceConflictPolicyEnum,
    ): TenantUserService.AllowAndDenyRec {

        var res = TenantUserService.AllowAndDenyRec()
        var auths: List<AuthResourceInfo> = mutableListOf();

        //查询本部门的资源
        val queryList = mor.tenant.tenantStandardDeptAuthResource.query()
            .where { it.dept.id match_in deptIds }
            .where { it.tenant.id match tenantId }
            .whereOr ({ it.resources.type match resourceType },{ it.resources.type.match_isNullOrEmpty() })
            .orderByDesc { it.updateAt }
            .toList()
            .map { it.resources }

        if (queryList.isNotEmpty()) {
            auths = queryList.stream().flatMap { it.stream() }.collect(Collectors.toList())
            auths = auths.filter { it.type == null || it.type  == resourceType }
        }

        //获取所有父部门  对部门进行二次查询   查询   就算本部门没有资源  看是否上级部门有授权的资源继承过来
        val deptPIds = mor.tenant.tenantDepartmentInfo.getParentDepts(tenantId, deptIds, true).stream().map { it.id }
            .collect(Collectors.toList())

        //查询父部门 Pids 的资源  且  heredity = true
        val deptPIdAuths = mor.tenant.tenantStandardDeptAuthResource.query()
            .where { it.dept.id match_in deptPIds }
            .where { it.resources.heredity match true }
            .where { it.tenant.id match tenantId }
            .whereOr ({ it.resources.type match resourceType },{ it.resources.type.match_isNullOrEmpty() })
            .orderByDesc { it.updateAt }
            .toList()
            .map { it.resources }
            .Unwind()
            .filter {
                it.type == null || it.type  == resourceType
            }


        val my_resources = deptPIdAuths.filter { it.isAllow }.toMutableList();

        //todo  如何考虑子部门
        when (policy) {
            //拒绝优先原则
            AuthResourceConflictPolicyEnum.Deny -> {
                //定义拒绝的资源  需要返回
                val denys = deptPIdAuths.filter { it.isAllow == false }.map { it.code };
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

                deptPIdAuths.distinctBy { it.resourceId }.forEach {
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
}