package nancal.iam.db.mongo.entity.tenant.authsource

import nancal.iam.db.mongo.entity.*
import nbcp.db.*
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/15-17:16
 */
@Document
@DbEntityGroup("tenant")
@Cn("应用部门授权标准表")
@DbEntityFieldRefs(
    DbEntityFieldRef("appInfo.code", "appInfo.name", SysApplication::class),
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class),
    DbEntityFieldRef("sources.id", "sources.$.type", TenantResourceInfo::class),
    DbEntityFieldRef("sources.id", "sources.$.name", TenantResourceInfo::class),
    DbEntityFieldRef("sources.id", "sources.$.code", TenantResourceInfo::class),
    DbEntityFieldRef("sources.id", "sources.$.resource", TenantResourceInfo::class),
    DbEntityFieldRef("sources.id", "sources.$.action", TenantResourceInfo::class),
    DbEntityFieldRef("dept.id", "dept.name", TenantDepartmentInfo::class),
)
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("tenant.id", unique = false),
    DbEntityIndex("appInfo.code", unique = false),
    DbEntityIndex("type", unique = false),
)
@VarDatabase("tenant.id")
@RemoveToSysDustbin
open class TenantStandardDeptAuthResource @JvmOverloads constructor(
    @Cn("应用IdName")
    var appInfo: CodeName = CodeName(),

    @Cn("租户")
    var tenant: IdName = IdName(),

    @Cn("授权主体")
    var dept: IdName = IdName(),

    @Cn("授权的资源")
    var resources: MutableList<TenantDeptAuthResourceInfo> = mutableListOf(),

    @Cn("授权的资源组")
    var resourceGroups: MutableList<TenantDeptAuthResourceGroup> = mutableListOf(),

    ) : BaseEntity()


@Cn("租户部门授权资源")
class TenantDeptAuthResourceInfo : AuthResourceInfo() {
    @Cn("子部门继承")
    var heredity: Boolean = true
}

@Cn("租户部门授权资源组")
class TenantDeptAuthResourceGroup : AuthResourceGroup() {
    @Cn("子部门继承")
    var heredity: Boolean = true
}
