package nancal.iam.db.mongo.entity

import nbcp.db.*
import nancal.iam.db.mongo.FormFieldTypeEnum
import org.springframework.data.mongodb.core.mapping.Document

@Document
@DbEntityGroup("tenant")
@Cn("应用自定义字段")
@DbEntityFieldRefs(
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class)
)
@VarDatabase("tenant.id")
@RemoveToSysDustbin
open class TenantApplicationFieldExtend @JvmOverloads constructor(
    @Cn("租户")
    var tenant: IdName = IdName(),

    @Cn("应用id")
    var appCode: String = "",

    @Cn("字段code")
    var code: String = "",

    @Cn("字段name")
    var name: String = "",

    @Cn("字段类型")
    var fieldType: FormFieldTypeEnum = FormFieldTypeEnum.Text,

    @Cn("备注")
    var remark: String = "",

    @Cn("字典项")
    var dataSource: String = "",

    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted :Boolean? =false
) : BaseEntity()

