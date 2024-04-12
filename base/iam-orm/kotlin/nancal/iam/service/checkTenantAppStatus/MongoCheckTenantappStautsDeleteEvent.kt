//package nancal.iam.db.mongo;
//
//import nbcp.base.mvc.*
//import nbcp.web.*
//import nbcp.comm.AsBooleanWithNull
//import nbcp.comm.AsString
//import nbcp.comm.scopes
//import nbcp.db.*
//import nancal.iam.db.mongo.entity.*
//import nbcp.db.mongo.event.*
//import nancal.iam.service.CheckTenantAppStatusScope
//import nancal.iam.service.checkTenantAppStatus.availableAppCodes
//import nbcp.db.mongo.*
//import nbcp.utils.MyUtil
//import nbcp.base.mvc.*
//import nbcp.utils.ClassUtil
//import nbcp.web.*
//import org.bson.Document
//import org.slf4j.LoggerFactory
//import org.springframework.stereotype.Component
//import java.io.Serializable
//
//
///**
// * 同步处理，删除的数据转移到垃圾箱
// */
//@Component
//class MongoCheckTenantappStautsDeleteEvent : IMongoEntityDelete {
//    companion object {
//        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
//    }
//
//    override fun beforeDelete(delete: MongoDeleteClip<*>): EventResult {
//        if (scopes.getLatest(CheckTenantAppStatusScope::class.java) == null) {
//            return EventResult(true, null)
//        }
//
//        println("scopes:" + scopes.map { it::class.java.simpleName }.joinToString(","))
//        println(Thread.currentThread().stackTrace.map { it.className + ":" + it.methodName +":" + it.lineNumber }.joinToString(","))
//
//        //如果是系统数据同步
//        val sysId = delete.whereData.map { map -> MyUtil.getValueByWbsPath(map, "sysId") }.firstOrNull()
//        if (sysId != null) {
//            return EventResult(true, null)
//        }
//
//        if(HttpContext.hasRequest == false){
//            return EventResult(true, null)
//        }
//
//        var availableAppCodes = HttpContext.request.availableAppCodes;
//
//        //找出数据
//        if (delete.defEntityName == mor.tenant.tenantAppRole.tableName) {
//            delete.whereData.putAll((mor.tenant.tenantAppRole.appInfo.code match_in availableAppCodes).criteriaObject)
//        } else if (delete.defEntityName == mor.tenant.tenantResourceInfo.tableName) {
//            delete.whereData.putAll((mor.tenant.tenantResourceInfo.appInfo.code match_in availableAppCodes).criteriaObject)
//        } else if (delete.defEntityName == mor.tenant.tenantAppAuthResourceInfo.tableName) {
//            delete.whereData.putAll((mor.tenant.tenantAppAuthResourceInfo.appInfo.code match_in availableAppCodes).criteriaObject)
//        } else if (delete.defEntityName == mor.tenant.tenantApplication.tableName) {
//            delete.whereData.putAll((mor.tenant.tenantApplication.appCode match_in availableAppCodes).criteriaObject)
//        }
//
//        return EventResult(true, null)
//    }
//
//    override fun delete(delete: MongoDeleteClip<*>, eventData: EventResult) {
//    }
//}