package nancal.iam.db.mongo.entity

import nbcp.db.*
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.tenant.TenantResourceGroup
import org.springframework.data.mongodb.core.mapping.Document

@Document
@RemoveToSysDustbin
@Cn("租户资源组授权")
@DbEntityGroup("tenant")
@VarDatabase("tenant.id")
@DbEntityFieldRefs(
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class),
    DbEntityFieldRef("target.id", "target.$.name", TenantAppRole::class),
    DbEntityFieldRef("target.id", "target.$.name", TenantUser::class),
    DbEntityFieldRef("target.id", "target.$.name", TenantUserGroup::class),
    DbEntityFieldRef("target.id", "target.$.name", TenantDepartmentInfo::class),
)
open class TenantAuthResourceGroup @JvmOverloads constructor(
    @Cn("租户")
    var tenant: IdName = IdName(),

    @Cn("授权主体类型")
    var type: AuthTypeEnum? = null,

    @Cn("授权主体")
    var target: IdName = IdName(),

    @Cn("授权资源组")
    var auths: MutableList<AuthResourceGroup> = mutableListOf(),

    @Cn("子部门是否授权，只在type=Dept时用")
    var heredity : Boolean = true,

) : BaseEntity()


open class AuthResourceGroup(
    @Cn("允许/拒绝")
    var isAllow: Boolean = false,
) : IdCodeName()

