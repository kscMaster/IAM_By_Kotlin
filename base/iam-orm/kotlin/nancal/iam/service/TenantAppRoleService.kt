//package nancal.iam.service
//
//import nbcp.comm.HasValue
//import nbcp.comm.IsIn
//import nbcp.comm.JsonResult
//import nbcp.comm.ListResult
//import nbcp.db.IdName
//import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
//import nbcp.db.mongo.entity.AuthResourceInfo
//import nbcp.db.mongo.entity.ResourceBaseInfo
//import org.springframework.stereotype.Service
//
//@Service
//class TenantAppRoleService {
//
//    /**
//     * 从 AppAuthResourceInfo 同步到 AppRole 。
//     */
//    fun syncFromAppAuthResourceInfoToAppRole(appCode: String): JsonResult {
//        var from = mor.tenant.tenantAppAuthResourceInfo.query()
//            .where { it.appInfo.id match appCode }
//            .toList();
//
//        from
//            .filter { it.type == AuthTypeEnum.Role }
//            .groupBy { it.target.id }
//            .forEach { roleIdGroup ->
//                var roleId = roleIdGroup.key
//
//                var roleDefines = mutableListOf<AuthResourceInfo>()
//
//                roleIdGroup.value.forEach {
//                    roleDefines.addAll(it.auths)
//                }
//
//                mor.tenant.tenantAppRole.updateById(roleId)
//                    .set { it.auths to roleDefines }
//                    .exec()
//            }
//        return JsonResult()
//    }
//
//}