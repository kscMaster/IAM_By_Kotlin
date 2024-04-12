package nancal.iam

import nancal.iam.db.mongo.mor
import nbcp.comm.ConvertJson
import nbcp.comm.JsonMap
import nbcp.comm.ToJson
import nbcp.comm.const
import nbcp.db.mongo.match
import nbcp.db.mongo.query
import org.junit.jupiter.api.Test
import java.io.File

/**
 * @Author wrk
 *
 * @Description
 * @Date 2022/5/11-15:17
 */
class InitOneTenantDataTest : TestBase(){
    //导出某个租户的所有相关数据
    private val tenantId ="6279f16a9b1e3a0f6df71503"
    /**
     *  数据项
     *
     *  admin侧
     *  SysApplication Tenant TenantApplication SysAppAuthResource TenantUser SysAppRole SysResourceInfo
     *
     *  租户侧
     *  authsource 1. TenantStandardDeptAuthResource 2. TenantStandardRoleAuthResource 3. TenantStandardUserAuthResource 4. TenantStandardUserGroupAuthResource
     *
     *  TenantAdminRole
     *
     *  TenantAppAuthResourceInfo.kt
     *  TenantAppExtendFieldDataSourceDict
     *
     *  TenantApplicationFieldExtend
     *  TenantAppRole
     *  TenantAuthResourceGroup
     *  TenantAuthRules
     *  TenantDepartmentInfo
     *  TenantDepartmentInfoFieldExtend
     *  TenantDutyDict
     *  TenantExtendFieldDataSourceDict
     *  TenantLoginUser
     *  TenantResourceGroup
     *  TenantResourceInfo
     *  TenantSecretSet
     *
     *  TenantUserFieldExtend
     *  TenantUserGroup
     */
    @Test
    fun initData() {
        var path = Thread.currentThread().contextClassLoader.getResource("").path.split("/target/")[0] + "/resources"
        path += "/flyway-v2"

       val appCodes= mor.tenant.tenantApplication.query()
            .where { it.tenant.id match tenantId }
            .where { it.isSysDefine match true }
            .toList()
            .map { it.appCode }.toList()

        var list = listOf(
            //admin侧
            mor.iam.sysApplication.query().where{it.appCode match_in appCodes},
            mor.tenant.tenant.query().where { it.id match tenantId },
            mor.tenant.tenantApplication.query().where { it.tenant.id match tenantId },
            mor.iam.sysAppAuthResource.query().where { it.appInfo.code match_in appCodes },
            mor.tenant.tenantUser.query().where { it.tenant.id match tenantId },
            mor.iam.sysAppRole.query().where { it.appInfo.code match_in appCodes },
            mor.iam.sysResourceInfo.query().where { it.appInfo.code match_in appCodes },
            //租户侧
            mor.tenant.tenantStandardDeptAuthResource.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantStandardRoleAuthResource.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantStandardUserAuthResource.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantStandardUserGroupAuthResource.query().where { it.tenant.id match tenantId },



            mor.tenant.tenantAppAuthResourceInfo.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantAppExtendFieldDataSourceDict.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantApplicationFieldExtend.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantAppRole.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantAuthResourceGroup.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantAuthRules.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantDepartmentInfo.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantDepartmentInfoFieldExtend.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantDutyDict.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantExtendFieldDataSourceDict.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantLoginUser.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantResourceGroup.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantResourceInfo.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantSecretSet.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantUserFieldExtend.query().where { it.tenant.id match tenantId },
            mor.tenant.tenantUserGroup.query().where { it.tenant.id match tenantId },
        )

        list.forEach { query ->
            var content = query
                .toList()
                .map {
                    var map = it.ConvertJson(JsonMap::class.java);
                    map.remove("createAt")
                    map.remove("updateAt")
                    return@map map.ToJson()
                }
                .joinToString(const.line_break)
            File("${path}/${query.actualTableName}.dat").writeText(content, const.utf8)
        }
    }
}