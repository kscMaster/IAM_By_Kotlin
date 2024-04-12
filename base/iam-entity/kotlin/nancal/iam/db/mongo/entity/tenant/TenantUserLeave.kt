package nancal.iam.db.mongo.entity

import nancal.iam.db.mongo.*
import nbcp.db.*
import nbcp.db.cache.RedisCacheDefine
import nbcp.db.mongo.entity.BasicUser
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.time.LocalDateTime

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/15-17:07
 */
//企业用户
@Document
@DbEntityGroup("tenant")
@Cn("租户用户离职表")
@DbEntityIndexes(
    DbEntityIndex("loginName", unique = false),
    DbEntityIndex("mobile", unique = false),
    DbEntityIndex("email", unique = false),
    DbEntityIndex("name", unique = false),
)
@VarDatabase("tenant.id")
@RedisCacheDefine("id")
open class TenantUserLeave(
    @Cn("地址")
    var address: String = "",
    @Cn("工号")
    var code: String = "",
    @Cn("租户")
    var tenant: IdName = IdName(),
    @Cn("部门")
    var depts: MutableList<DeptDefine> = mutableListOf(),   //多部门使用
    @Cn("角色")
    var roles: MutableList<IdName> = mutableListOf(),
    @Cn("用户组")
    var groups: MutableList<IdName> = mutableListOf(),
    @Cn("岗位")
    var duty: IdName = IdName(),
    @Cn("发送密码方式")
    var sendPasswordType: SendPasswordType? = null,
    @Cn("是否发送密码")
    var isSendPassword: Boolean = true,
    @Cn("入职时间")
    var goJobTime: LocalDateTime = LocalDateTime.now(),
    @Cn("员工类型")
    var employeeType: EmployeeTypeEnum = EmployeeTypeEnum.FullTime,
    @Cn("员工状态")
    var employeeStatus: EmployeeStatusEnum = EmployeeStatusEnum.Try,
    @Cn("上级领导")
    var leader: IdName = IdName(),
    @Cn("排序")
    var sort: Float = 0F,
    @Cn("允许应用")
    var allowApps: MutableList<CodeName> = mutableListOf(),
    @Cn("拒绝应用")
    var denyApps: MutableList<CodeName> = mutableListOf(),
    @Cn("启用/停用")
    var enabled: Boolean = true,
    @Cn("身份源")
    var identitySource: ProtocolEnum? = ProtocolEnum.Self,
    @Cn("AD中的标识字段DN")
    var distinguishedName: String? = null,
    @Cn("管理员类型")
    var adminType: TenantAdminTypeEnum = TenantAdminTypeEnum.None,
    @Cn("人员密级")
    var personClassified:PersonClassifiedEnum = PersonClassifiedEnum.None,
    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted: Boolean? = false
) : BasicUser(), Serializable
