package nancal.iam.db.mongo.entity

import nbcp.db.*
import org.springframework.data.mongodb.core.mapping.Document

@Document
@DbEntityGroup("tenant")
@Cn("应用扩展字段数据源字典")
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("code", unique = false),
)
@VarDatabase("tenant.id")
@RemoveToSysDustbin
open class TenantAppExtendFieldDataSourceDict @JvmOverloads constructor(
    var tenant:IdName = IdName(),
    @Cn("编码")
    var code: String = "",
    @Cn("名称")
    var name: String = "",

    @Cn("数据源")
    var dataSource: String = "",

    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted :Boolean? =false
) : BaseEntity()