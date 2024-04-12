package nancal.iam

import cn.hutool.core.lang.UUID
import nancal.iam.db.mongo.AccessLevelEnum
import nancal.iam.db.mongo.ResourceTypeEnum
import nancal.iam.db.mongo.entity.TenantResourceInfo
import nancal.iam.db.mongo.mor
import nancal.iam.util.CodeUtils
import nbcp.comm.HasValue
import nbcp.comm.Slice
import nbcp.db.mongo.*
import org.junit.jupiter.api.Test

class mocktest : TestBase() {

    @Test
    fun bathchInsert() {
        /**
         * {
        "name": "111",
        "dataAccessLevel": "None",
        "action": [],
        "remark": "",
        "type": "Data",
        "resource": "",
        "appInfo": {
        "code": "adasdasdas",
        "name": "aaaa"
        },
        "code": "ffff:121212"
        }
         */
        // 测试资源树性能 插入测试数据 6281c3c43edf677dbaf67c85 shi-test
        for (i in 1..10000) {
            val vo = TenantResourceInfo()
            vo.name = "test$i"
            vo.dataAccessLevel = AccessLevelEnum.None
            vo.action = mutableListOf()
            vo.type = ResourceTypeEnum.Data
            vo.appInfo.code = "adasdasdas"
            vo.appInfo.name = "aaaa"
            vo.code = UUID.randomUUID().toString().replace("-","").substring(0,6) + ":" + UUID.randomUUID().toString().replace("-","").substring(0,6)+ ":" + UUID.randomUUID().toString().replace("-","").substring(0,6)
            save(vo, "6281c3c43edf677dbaf67c85")
        }
    }

    fun save(entity: TenantResourceInfo, tenantId :String) {
        entity.tenant.id = tenantId
        entity.tenant.name = "shi-test"
        // code规则限制
        val codeList = CodeUtils.codeHolder(entity.code)
        // 资源校验
        if(entity.id.HasValue){
            mor.tenant.tenantResourceInfo.query()
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.tenant.id match tenantId }
                .where { it.id match entity.id }
                .toEntity()
                .apply {
                    if(this == null){
                        return
                    }
                }
        }

        if (entity.appInfo.code.HasValue) {
            // 同一租户下的资源名称不能重复
            val exists = mor.tenant.tenantResourceInfo.query()
                .where { it.tenant.id match entity.tenant.id }
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.code match entity.code }
                .apply {
                    if (entity.id.HasValue) {
                        this.where { it.id match_not_equal entity.id }
                    }
                }
                .exists()
            if (exists) {
                //如果是修改，需要保证除当前应用外，其他应用没有该资源名称
                return
            }
        } else {
            return
        }

        if (codeList.size > 1 && entity.id.HasValue) { // 子资源编辑
            mor.tenant.tenantResourceInfo.query()
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.code match_in codeList.Slice(0, -1)}
                .toList().map { it.code }
                .apply {
                    if (this.size != codeList.size - 1) {
                        return
                    }
                }
        }

        val code = entity.code
        val id = entity.id
        val dataType = entity.type
        if (codeList.size > 1 && !id.HasValue) { // 子资源父级补齐
            mor.tenant.tenantResourceInfo.query()
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.code match_in codeList.Slice(0, -1) }
                .toList().map { it.code }
                .apply {
                    (codeList.Slice(0, -1) - this).forEach {
                        entity.code = it // 当前层级的code
                        entity.id = "" // clean上次赋值的id
                        entity.type = ResourceTypeEnum.Data // 默认数据类型
                        mor.tenant.tenantResourceInfo.doInsert(entity)
                    }
                }
        }
        entity.code = code
        entity.id = id
        entity.code = code
        entity.type = dataType
        mor.tenant.tenantResourceInfo.updateWithEntity(entity)
            .run {
                if (entity.id.HasValue) {
                    return@run this.execUpdate()
                } else {
                    return@run this.execInsert()
                }
            }
    }
}