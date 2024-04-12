package nancal.iam.db.mongo.entity.tenant

import nancal.iam.db.mongo.TenantDictType
import nancal.iam.db.mongo.entity.*
import nbcp.db.*
import org.springframework.data.mongodb.core.mapping.Document


@Document
@DbEntityGroup("tenant")
@Cn("数据字典")
@VarDatabase("tenant.id")
@DbEntityFieldRefs(DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class))
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("tenant.id", unique = false)
)
@RemoveToSysDustbin
open class TenantGroupDict @JvmOverloads constructor(
    @Cn("编码")
    var code: String = "",
    @Cn("名称")
    var name: String = "",
    @Cn("说明")
    var remark: String = "",
    @Cn("类型")
    var group: Int = TenantDictType.PersonClassified.type,
    @Cn("顺序")
    var number: Int = 0,
    @Cn("租户")
    var tenant: IdName = IdName(),
    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted :Boolean? =false

) : BaseEntity()