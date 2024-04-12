package nancal.iam.service.extra_auth

import nbcp.comm.*
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.TenantAppAuthResourceInfo
import nancal.iam.db.mongo.entity.tenant.authsource.TenantAuthUpdateDetail
import nancal.iam.db.mongo.entity.tenant.authsource.TenantDeptAuthResourceInfo
import nbcp.db.mongo.*
import nbcp.db.*
import nbcp.db.mongo.event.IMongoEntityInsert
import nbcp.utils.MyUtil
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * 同步处理，插入的实体，添加 createAt 字段。
 */
@Component
class MongoAuthResourceInsertEvent : IMongoEntityInsert {
    override fun beforeInsert(insert: MongoBaseInsertClip): EventResult {
        return EventResult(true, null)
    }

    override fun insert(insert: MongoBaseInsertClip, eventData: EventResult) {
        if (insert.defEntityName == mor.tenant.tenantAppAuthResourceInfo.tableName) {

            var authDetailList: MutableList<TenantAuthUpdateDetail> = mutableListOf()
            insert.entities.forEach {
                val authDetailEntity = it.ToJson().FromJson<TenantAuthUpdateDetail>()
                if (authDetailEntity != null) {
                    authDetailEntity.actionType = DataActionEnum.Insert
                    authDetailEntity.authId = authDetailEntity.id
                    authDetailEntity.id = ""
                    authDetailEntity.createAt = LocalDateTime.now()
                    authDetailList.add(authDetailEntity)
                }
            }

            mor.tenant.tenantAuthUpdateDetail.batchInsert()
                .apply {
                    addEntities(authDetailList)
                }
                .exec()

//            insert.entities.forEach {  authObj_obj ->
//                val baseAuthResourceService = BaseAuthResourceService()
//                val authObj = authObj_obj.ToJson().FromJson<TenantAppAuthResourceInfo>()
//
//                if (authObj == null) {  return  }
//
//                val type = MyUtil.getValueByWbsPath(authObj_obj, "type").AsStringWithNull().toString()
//
//                if(type != AuthTypeEnum.Dept.name){
//                    var auths = mor.tenant.tenantAppAuthResourceInfo.query()
//                        .where { it.type match type }
//                        .where { it.target.id match authObj.target.id }
//                        .where { it.tenant.id match authObj.tenant.id }
//                        .where { it.appInfo.code match authObj.appInfo.code }
//                        .orderByDesc { it.updateAt }
//                        .toList()
//                        .map { it.auths }
//                        .Unwind()
//
//                    authObj.auths.removeAll(authObj.auths)
//                    authObj.auths.addAll(auths)
//                }else{
//                    baseAuthResourceService.deptBetterControl(type, authObj, baseAuthResourceService,false)
//                    return
//                }
//                baseAuthResourceService.betterControlAuthResource(authObj, type, mutableListOf())
//            }

        }
        return
    }


}