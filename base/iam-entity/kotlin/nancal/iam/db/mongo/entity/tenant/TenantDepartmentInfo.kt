package nancal.iam.db.mongo.entity

import nancal.iam.db.mongo.ProtocolEnum
import nbcp.db.*
import org.springframework.data.mongodb.core.mapping.Document
import javax.naming.Name

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/15-17:17
 */
/**
 * 公司部门 信息
 *
 */
@Cn("部门")
@Document
@DbEntityGroup("tenant")
@DbEntityFieldRefs(
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class),
    DbEntityFieldRef("parent.id", "parent.name", TenantDepartmentInfo::class),
    DbEntityFieldRef("roles.id", "roles.$.name", TenantAppRole::class),
    DbEntityFieldRef("manager.id", "manager.$.name", TenantUser::class),
    DbEntityFieldRef("allowApps.id", "allowApps.$.name", SysApplication::class),
    DbEntityFieldRef("denyApps.id", "denyApps.$.name", SysApplication::class),
)
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("tenant.id", unique = false),
)
@VarDatabase("tenant.id")
@SortNumber("sort", "parent.id", 10)
@RemoveToSysDustbin
open class TenantDepartmentInfo(
    @Cn("所属公司")
    var tenant: IdName = IdName(),
    @Cn("部门名称")
    var name: String = "",
    @Cn("上级部门")
    var parent: IdName = IdName(),

    @Cn("部门总人数")
    var userCount: Int = 0,

    @Cn("角色")
    var roles: MutableList<IdName> = mutableListOf(),

    @Cn("部门负责人")
    var manager: MutableList<IdName> = mutableListOf(),
    /**
     * 电话
     */
    @Cn("电话")
    var phone: String = "",
    var allowApps: MutableList<CodeName> = mutableListOf(),
    var denyApps: MutableList<CodeName> = mutableListOf(),
    @Cn("备注")
    var remark: String = "",

    var sort: Float = 0F,
    @Cn("层级")
    var level: Int = 1,

    @Cn("身份源")
    var identitySource: ProtocolEnum? = ProtocolEnum.Self,

    @Cn("distinguishedName")
    var distinguishedName: String? = null,

    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted: Boolean? = false
) : BaseEntity()