package nancal.iam.db.mongo.entity

import nbcp.db.*
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/15-17:22
 */
@Document
@DbEntityGroup("tenant")
@Cn("扩展字段数据源字典")
@VarDatabase("tenant.id")
@DbEntityFieldRefs(DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class))
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("code", unique = false)
)
@RemoveToSysDustbin
open class TenantExtendFieldDataSourceDict @JvmOverloads constructor(
    @Cn("编码")
    var code: String = "",
    @Cn("名称")
    var name: String = "",

    @Cn("数据源")
    var dataSource: String = "",

    @Cn("租户")
    var tenant: IdName = IdName(),

    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted :Boolean? =false
) : BaseEntity()