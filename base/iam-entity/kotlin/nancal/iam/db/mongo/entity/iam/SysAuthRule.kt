package nancal.iam.db.mongo.entity

import nbcp.db.*
import nancal.iam.db.mongo.*
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/22-15:11
 */
@Document
@DbEntityGroup("iam")
@Cn("应用授权规则")
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex( "name", unique = true),
)
@RemoveToSysDustbin
open class SysAuthRule @JvmOverloads constructor(
    @Cn("条件因子名称")
    var conditionName : String = "",
    @Cn("属性名")
    var name: String = "",
    @Cn("属性KEY")
    var key: String = "",
    @Cn("属性类型")
    var paramType: AuthRuleParamTypeEnum? = AuthRuleParamTypeEnum.User,

    @Cn("运算符类型")
    var ruleType : AuthRuleOperationTypeEnum? = AuthRuleOperationTypeEnum.None,

    @Cn("运算符")
    var operators: MutableList<Operator> = mutableListOf(),
    @Cn("代码块")
    var codeBlocks: String = "",

    @Cn("备注")
    var remark : String = ""

) : BaseEntity()

class Operator(
    var name: String = "",
    var key: String = "",
    var value: String = "",
)
