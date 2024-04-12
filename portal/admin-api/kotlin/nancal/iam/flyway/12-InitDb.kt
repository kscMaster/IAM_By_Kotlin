package nancal.iam.flyway

import nancal.iam.db.mongo.ResourceTypeEnum
import nancal.iam.db.mongo.entity.SysResourceInfo
import nancal.iam.db.mongo.entity.TenantResourceInfo
import nancal.iam.db.mongo.mor
import nancal.iam.util.CodeUtils
import nbcp.comm.FromJson
import nbcp.comm.HasValue
import nbcp.comm.ToJson
import nbcp.db.FlywayVersionBaseService
import nbcp.db.mongo.*
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils
import java.time.LocalDateTime

@Component
class `12-InitDb`: FlywayVersionBaseService(12) {

    override fun exec() {
        initPersonClassified()
    }

    fun initPersonClassified() {
        println("--------同步子资源历史数据")
        val dataList: List<SysResourceInfo> = mor.iam.sysResourceInfo.query().toListResult().data.toList()
        if (CollectionUtils.isEmpty(dataList)) {
            return
        }
        println("--------同步前数据大小是${dataList.size}")
        // 补齐数据
        if (dataList.isEmpty()) {
            return
        }

        var i = 0
        var j = 0
        // 同步数据
        dataList.forEach{
            val asyncResource = asyncResource(it)
            if (asyncResource) {
                i++
            } else {
                println("失败的数据是：${it.id}:${it.code}:${it.appInfo}")
                j++
            }
        }
        println("数据同步成功${i}个数据，失败${j}个数据")
        val dataList2: List<SysResourceInfo> = mor.iam.sysResourceInfo.query().toListResult().data.toList()
        println("--------同步后数据大小是${dataList2.size}")
    }

    fun asyncResource(entity:SysResourceInfo) : Boolean {
        // code处理
        entity.code.split(":").toMutableList().forEach {
            if (!it.HasValue) {
                return false
            }
        }
        val codeList: MutableList<String> = CodeUtils.codeHolder(entity.code)
        if (codeList.size == 1) {
            return true
        }

        codeList.removeAt(codeList.size - 1) // 当前资源不处理
        codeList.forEach { item ->

            entity.id = ""
            entity.code = item
            entity.type = ResourceTypeEnum.Data
            entity.createAt = LocalDateTime.now()
            entity.updateAt = LocalDateTime.now()
            mor.iam.sysResourceInfo.query()
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.code match item }
                .toEntity()
                .apply {
                    if (this == null) { // 父资源不存在
                        asyncTenantResource(entity).apply {
                            if (!this) {
                                return false
                            }
                        }
                    }
                }
        }
        return true
    }

    /**
     * 同步到租户侧数据
     */
    fun asyncTenantResource(entity: SysResourceInfo):Boolean {
        var isInsert = false
        mor.iam.sysResourceInfo.updateWithEntity(entity)
            .run {
                if (entity.id.HasValue) {
                    return@run this.execUpdate()
                } else {
                    isInsert = true
                    return@run this.execInsert()
                }
            }
            .apply {
                if (this == 0) {
                    return false
                }
                if (isInsert) {
                    mor.tenant.tenantApplication.query()
                        .where { it.appCode match entity.appInfo.code }
                        .toList()
                        .apply {

                            if (this.isNotEmpty()) {
                                var tenantResource: TenantResourceInfo
                                val list = mutableListOf<TenantResourceInfo>()

                                this.forEach {
                                    tenantResource = entity.ToJson().FromJson(TenantResourceInfo::class.java)!!
                                    tenantResource.id = ""
                                    tenantResource.tenant = it.tenant
                                    tenantResource.isSysDefine = true
                                    tenantResource.sysId = entity.id
                                    list.add(tenantResource)
                                }

                                mor.tenant.tenantResourceInfo.batchInsert()
                                    .apply {
                                        addEntities(list)
                                    }
                                    .exec().apply {
                                        if (this == 0) {
                                            return false
                                        }
                                    }
                            }
                        }
                } else {
                    mor.tenant.tenantResourceInfo.query()
                        .where { it.appInfo.code match entity.appInfo.code }
                        .where { it.sysId match entity.id }
                        .toList()
                        .apply {
                            if (this.isNotEmpty()) {
                                this.forEach {
                                    it.remark = entity.remark
                                    it.action = entity.action
                                    it.name = entity.name
                                    it.code = entity.code
                                    it.type = entity.type
                                    it.resource = entity.resource
                                    it.dataAccessLevel = entity.dataAccessLevel

                                    mor.tenant.tenantResourceInfo.updateWithEntity(it).execUpdate().apply {
                                        if (this == 0) {
                                            return false
                                        }
                                    }
                                }
                            }
                        }
                }
                return true
            }
    }

}