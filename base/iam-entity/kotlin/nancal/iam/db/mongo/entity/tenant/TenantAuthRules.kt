package nancal.iam.db.mongo.entity

import nbcp.db.*
import nancal.iam.db.mongo.*
import org.springframework.data.mongodb.core.mapping.Document

/**
 * 授权限制条件
 */
@Document
@DbEntityGroup("tenant")
@DbEntityFieldRefs(
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class),
)
@VarDatabase("tenant.id")
@RemoveToSysDustbin
@Cn("租户应用授权规则")
class TenantAuthRules(

    @Cn("租户")
    var tenant: IdName = IdName(),

    @Cn("条件因子名称")
    var conditionName : String = "",
    var name: String = "",
    var key: String = "",
    @Cn("是否系统默认")
    var isSysDefine: Boolean = false,
    @Cn("属性类型")
    var paramType: AuthRuleParamTypeEnum? = AuthRuleParamTypeEnum.User,

    @Cn("运算符类型")
    var ruleType : AuthRuleOperationTypeEnum? = AuthRuleOperationTypeEnum.Basic,

    @Cn("运算符")
    var operators: MutableList<Operator> = mutableListOf(),
    @Cn("代码块")
    var codeBlocks: String = "",

    var remark : String = ""

):BaseEntity()
//
//class Operator(
//    var name: String = "",
//    var key: String = "",
//    var value: String = "",
//)