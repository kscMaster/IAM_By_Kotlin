package nancal.iam.db.mongo.entity

import nancal.iam.db.mongo.AppTagTypeEnum
import nbcp.db.*
import nancal.iam.db.mongo.UserSystemTypeEnum

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/22-15:10
 */
@RemoveToSysDustbin
open class BaseApplication(
    @Cn("应用Id")
    var appCode: String = "",

    @Cn("中文应用名称")
    var name: String = "",   //应用名称

    @Cn("英文应用名称或其他类型名称")
    var ename: String = "",

    @Cn("备注")
    var remark: String? = "",

    @Cn("logo")
    var logo: IdUrl? = IdUrl(),

    @Cn("所属行业")
    var industry: MutableList<IdName> = mutableListOf(),

    @Cn("用户类型")
    var userType: UserSystemTypeEnum = UserSystemTypeEnum.TenantAdmin,

    @Cn("是否上架")
    var isOnLine: Boolean? = true,

    @Cn("地址")
    var url: String? = "",

    @Cn("标签")
    var lable: MutableList<CodeName> = mutableListOf(),

    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted: Boolean? = false

) : BaseEntity()
