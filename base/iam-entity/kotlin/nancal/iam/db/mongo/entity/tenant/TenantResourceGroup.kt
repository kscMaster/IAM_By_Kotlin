package nancal.iam.db.mongo.entity.tenant

import nancal.iam.db.mongo.entity.SysApplication
import nancal.iam.db.mongo.entity.Tenant
import nancal.iam.db.mongo.entity.TenantAppRole
import nbcp.db.*
import org.springframework.data.mongodb.core.mapping.Document

@Document
@RemoveToSysDustbin
@Cn("资源组")
@DbEntityGroup("tenant")
@VarDatabase("tenant.id")
@DbEntityFieldRefs(
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class),
)
open class TenantResourceGroup @JvmOverloads constructor(
    @Cn("租户")
    var tenant: IdName = IdName(),

    @Cn("组名")
    var name: String = "",

    @Cn("组编码")
    var code: String = "",

) : BaseEntity()