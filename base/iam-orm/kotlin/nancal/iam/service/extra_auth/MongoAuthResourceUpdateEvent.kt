package nancal.iam.service.extra_auth

import nancal.iam.db.mongo.AuthTypeEnum
import nancal.iam.db.mongo.DataActionEnum
import nancal.iam.db.mongo.entity.tenant.authsource.TenantAuthUpdateDetail
import nancal.iam.db.mongo.mor
import nbcp.comm.*
import nbcp.db.*
import nbcp.db.mongo.*
import nbcp.db.mongo.event.IMongoEntityUpdate
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * 同步处理，更新的实体，添加 updateAt 字段。
 */
@Component
class MongoAuthResourceUpdateEvent : IMongoEntityUpdate {
    override fun beforeUpdate(update: MongoBaseUpdateClip): EventResult {
        return EventResult(true, null)
    }

    override fun update(update: MongoBaseUpdateClip, eventData: EventResult) {
        if (update.defEntityName == mor.tenant.tenantAppAuthResourceInfo.tableName) {

          val objList =  mor.tenant.tenantAppAuthResourceInfo.query()
                .apply {
                    this.whereData.addAll(update.whereData)
                }.toList()

            if(objList.size == 0){
                return
            }
            var authDetailList: MutableList<TenantAuthUpdateDetail> = mutableListOf()
            objList.forEach {
                val authDetailEntity = it.ToJson().FromJson<TenantAuthUpdateDetail>()
                if (authDetailEntity != null) {
                    authDetailEntity.actionType = DataActionEnum.Update
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
//            val baseAuthResourceService = BaseAuthResourceService()
//
//            objList.forEach { authObj ->
//                val type = authObj.type.AsString()
//                if(type != AuthTypeEnum.Dept.name){
//                    var auths = mor.tenant.tenantAppAuthResourceInfo.query()
//                        .where { it.type match type }
//                        .where { it.target.id match authObj.target.id }
//                        .where { it.tenant.id match authObj.tenant.id }
//                        .where { it.appInfo.code match authObj.appInfo.code }
//                        .where { it.id match_not_equal authObj.id }
//                        .orderByDesc { it.updateAt }
//                        .toList()
//                        .map { it.auths }
//                        .Unwind()
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