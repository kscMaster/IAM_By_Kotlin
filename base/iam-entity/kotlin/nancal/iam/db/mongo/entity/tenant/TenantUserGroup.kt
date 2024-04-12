package nancal.iam.db.mongo.entity

import nbcp.db.*
import nbcp.db.cache.RedisCacheDefine
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/15-17:18
 */
@Document
@DbEntityGroup("tenant")
@Cn("租户用户组")
@DbEntityFieldRefs(
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class),
    DbEntityFieldRef("roles.id", "roles.$.name", TenantAppRole::class),
    DbEntityFieldRef("allowApps.id", "allowApps.$.name", SysApplication::class),
    DbEntityFieldRef("denyApps.id", "denyApps.$.name", SysApplication::class),
)
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("tenant.id", unique = false, cacheable = true),
    DbEntityIndex("name", unique = false),
)
@VarDatabase("tenant.id")
@RemoveToSysDustbin
open class TenantUserGroup @JvmOverloads constructor(
    @Cn("租户")
    var tenant: IdName = IdName(),

    @Cn("组名")
    var name: String = "",

    @Cn("组详情")
    var remark: String = "",

    @Cn("是否冻结组")
    var enabled: Boolean = false,   // 是否冻结组

    var allowApps: MutableList<CodeName> = mutableListOf(),
    var denyApps: MutableList<CodeName> = mutableListOf(),

    @Cn("角色")
    var roles: MutableList<IdName> = mutableListOf(),

    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted :Boolean? =false
) : BaseEntity()