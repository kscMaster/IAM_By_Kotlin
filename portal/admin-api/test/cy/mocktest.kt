package nancal.iam.mvc.tenant.cy

import cn.hutool.core.lang.UUID
import nancal.iam.TestBase
import nancal.iam.db.mongo.AccessLevelEnum
import nancal.iam.db.mongo.ResourceTypeEnum
import nancal.iam.db.mongo.entity.SysResourceInfo
import nancal.iam.db.mongo.entity.TenantResourceInfo
import nancal.iam.db.mongo.mor
import nancal.iam.util.CodeUtils
import nbcp.comm.FromJson
import nbcp.comm.HasValue
import nbcp.comm.Slice
import nbcp.comm.ToJson
import nbcp.db.mongo.*
import org.junit.jupiter.api.Test

class mocktest : TestBase() {


    @Test
    fun bathchInsert() {
        // 测试资源树性能 插入测试数据 6281c3c43edf677dbaf67c85 shi-test
        for (i in 1..1000) {
            val vo = SysResourceInfo()
            vo.name = "test$i"
            vo.dataAccessLevel = AccessLevelEnum.None
            vo.action = mutableListOf()
            vo.type = ResourceTypeEnum.Data
            vo.appInfo.code = "adasdasdas"
            vo.appInfo.name = "误删除测试授权"
            vo.code = UUID.randomUUID().toString().replace("-","").substring(0,6) + ":" + UUID.randomUUID().toString().replace("-","").substring(0,6)+ ":" + UUID.randomUUID().toString().replace("-","").substring(0,6)
            save(vo)
        }
    }

    fun save(entity: SysResourceInfo) {
        val code = entity.code
        val id = entity.id
        val dataType = entity.type
        var codeList: MutableList<String> = CodeUtils.codeHolder(entity.code)


        if (codeList.size > 1 && !entity.id.HasValue) { // 子资源父级补齐

            mor.iam.sysResourceInfo.query()
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.code match_in codeList.Slice(0, -1) }
                .toList().map { it.code }
                .apply {
                    (codeList.Slice(0, -1) - this).forEach {
                        entity.code = it // 当前层级的code
                        entity.id = "" // clean上次赋值的id

                        entity.type = ResourceTypeEnum.Data // 默认数据类型
                        asyncTenantResource2(entity) // 租户侧资源同步
                    }
                }

        }
//        val requestJson = entity.ToJson().FromJson<JsonMap>()!!
        entity.code = code
        entity.id = id
        entity.code = code
        entity.type = dataType
        asyncTenantResource2(entity)
    }

    fun asyncTenantResource2(entity: SysResourceInfo): String {
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
                    return ""
                }
            }
        if (isInsert) {
            mor.tenant.tenantApplication.query()
                .where { it.appCode match entity.appInfo.code }
                .toList()
                .apply {

                    if (this.any()) {
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
                            .exec()
                    }
                    return entity.id
                }
        }

        mor.tenant.tenantResourceInfo.update()
            .where { it.appInfo.code match entity.appInfo.code }
            .where { it.sysId match entity.id }
            .set { it.remark to entity.remark }
            .set { it.action to entity.action }
            .set { it.name to entity.name }
            .set { it.code to entity.code }
            .set { it.type to entity.type }
            .set { it.resource to entity.resource }
            .set { it.dataAccessLevel to entity.dataAccessLevel }
            .exec()

        return entity.id

    }
}