//package nancal.iam.service.checkTenantAppStatus;
//
//import nbcp.base.mvc.*
//import nbcp.web.*
//import nbcp.comm.*
//import nancal.iam.db.mongo.*
//import nbcp.db.mongo.*;
//import nbcp.db.*
//import nbcp.db.mongo.event.IMongoEntityInsert
//import nancal.iam.service.CheckTenantAppStatusScope
//import nbcp.utils.MyUtil
//import nbcp.base.mvc.*
//import nbcp.web.*
//import org.bson.types.ObjectId
//import org.springframework.stereotype.Component
//import java.time.LocalDateTime
//
///**
// * 同步处理，插入的实体，添加 createAt 字段。
// */
//@Component
//class MongoCheckTenantAppStatusInsertEvent : BaseTenantAppStatusService(), IMongoEntityInsert {
//    override fun beforeInsert(insert: MongoBaseInsertClip): EventResult {
//        if (scopes.getLatest(CheckTenantAppStatusScope::class.java) == null) {
//            return EventResult(true, null)
//        }
//
//        var availableAppCodes = HttpContext.request.availableAppCodes;
//
//        insert.entities.forEach { entity ->
//
//            //如果是系统数据同步
//            val sysId = MyUtil.getValueByWbsPath(entity, "sysId").AsString()
//            if (sysId.HasValue) {
//                return@forEach
//            }
//
//            var appCode = MyUtil.getValueByWbsPath(entity, "appInfo.code").AsStringWithNull()
//            if (appCode == null) {
//                return@forEach
//            }
//
//            if (insert.defEntityName == mor.tenant.tenantAppRole.tableName) {
//                if (appCode.HasValue && !availableAppCodes.contains(appCode)) {
//                    throw RuntimeException("appCode{${appCode}}是禁用状态,不允许新增角色")
//                }
//            } else if (insert.defEntityName == mor.tenant.tenantResourceInfo.tableName) {
//                if (appCode.HasValue && !availableAppCodes.contains(appCode)) {
//
//                    println("availableAppCodes:------->"+availableAppCodes.toString())
//                    println("appCode:------->"+appCode.toString())
//                    println("entity:------->"+entity.toString())
//
//                    throw RuntimeException("appCode{${appCode}}是禁用状态,不允许新增资源")
//                }
//            } else if (insert.defEntityName == mor.tenant.tenantAppAuthResourceInfo.tableName) {
//                if (appCode.HasValue && !availableAppCodes.contains(appCode)) {
//                    throw RuntimeException("appCode{${appCode}}是禁用状态,不允许新增授权")
//                }
//            }
//        }
//
//        return EventResult(true, null);
//    }
//
//    override fun insert(insert: MongoBaseInsertClip, eventData: EventResult) {
//
//    }
//
//}