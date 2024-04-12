//package nancal.iam.service.checkTenantAppStatus;
//
//import nancal.iam.db.mongo.*
//import nancal.iam.service.CheckTenantAppStatusScope
//import nbcp.base.mvc.*
//import nbcp.web.*
//import nbcp.comm.scopes
//import nbcp.db.*
//import nbcp.db.mongo.*
//import nbcp.db.mongo.event.IMongoEntityUpdate
//import nbcp.utils.MyUtil
//import org.springframework.stereotype.Component
//
///**
// * 同步处理，更新的实体，添加 updateAt 字段。
// */
//@Component
//class MongoCheckTenantAppStatusUpdateEvent : IMongoEntityUpdate {
//    override fun beforeUpdate(update: MongoBaseUpdateClip): EventResult {
//        if (scopes.getLatest(CheckTenantAppStatusScope::class.java) == null) {
//            return EventResult(true, null)
//        }
//
//        //如果是系统数据同步
//
//        val sysId = update.whereData.map { map -> MyUtil.getValueByWbsPath(map, "sysId") }.firstOrNull()
//        if (sysId != null) {
//            return EventResult(true, null)
//        }
//
//        var availableAppCodes = HttpContext.request.availableAppCodes;
//
//        if (update.defEntityName == mor.tenant.tenantAppRole.tableName) {
//            update.whereData.putAll((mor.tenant.tenantAppRole.appInfo.code match_in availableAppCodes).criteriaObject)
//        } else if (update.defEntityName == mor.tenant.tenantResourceInfo.tableName) {
//            update.whereData.putAll((mor.tenant.tenantResourceInfo.appInfo.code match_in availableAppCodes).criteriaObject)
//        } else if (update.defEntityName == mor.tenant.tenantAppAuthResourceInfo.tableName) {
//            update.whereData.putAll((mor.tenant.tenantAppAuthResourceInfo.appInfo.code match_in availableAppCodes).criteriaObject)
//        }
////        else if (update.defEntityName == mor.tenant.tenantApplication.tableName) {
////            update.whereData.putAll((mor.tenant.tenantApplication.appCode match_in availableAppCodes).criteriaObject)
////        }
//
//        return EventResult(true, null)
//    }
//
//    override fun update(update: MongoBaseUpdateClip, eventData: EventResult) {
//    }
//}