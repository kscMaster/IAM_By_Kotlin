package nancal.iam.db.mongo;

import nancal.iam.db.mongo.entity.tenant.authsource.TenantAuthUpdateDetail
import nbcp.db.*
import nbcp.db.mongo.event.*
import nancal.iam.service.extra_auth.BaseAuthResourceService
import nancal.iam.service.extra_auth.CheckAuthResourceScope
import nbcp.comm.*
import nbcp.db.mongo.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime


/**
 * 同步处理，删除的数据转移到垃圾箱
 */
@Component
class MongoAuthResourceDeleteEvent : IMongoEntityDelete {

    override fun beforeDelete(delete: MongoDeleteClip<*>): EventResult {
        if (delete.defEntityName == mor.tenant.tenantAppAuthResourceInfo.tableName) {


            val objList =  mor.tenant.tenantAppAuthResourceInfo.query()
                .apply {
                    this.whereData.addAll(delete.whereData)
                }.toList()

            if(objList.size == 0){
                return EventResult(true, null)
            }

            var authDetailList: MutableList<TenantAuthUpdateDetail> = mutableListOf()
            objList.forEach {
                val authDetailEntity = it.ToJson().FromJson<TenantAuthUpdateDetail>()
                if (authDetailEntity != null) {
                    authDetailEntity.actionType = DataActionEnum.Delete
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


//
//
//            val baseAuthResourceService = BaseAuthResourceService()
//
//            val objList =  mor.tenant.tenantAppAuthResourceInfo.query()
//                .apply {
//                    this.whereData.addAll(delete.whereData)
//                }.toList()
//
//            if(objList.size == 0){
//                return EventResult(true, null)
//            }
//
//
//
//            objList.forEach { authObj ->
//
//                val type = authObj.type.AsString()
//
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
//
//                    authObj.auths.removeAll(authObj.auths)
//                    authObj.auths.addAll(auths)
//                }else{
//                    baseAuthResourceService.deptBetterControl(type, authObj, baseAuthResourceService,true)
//                    return EventResult(true, null)
//                }
//                baseAuthResourceService.betterControlAuthResource(authObj, type, mutableListOf())
//            }
//
        }

        return EventResult(true, null)
    }

    override fun delete(delete: MongoDeleteClip<*>,  eventData: EventResult) {

    }
}