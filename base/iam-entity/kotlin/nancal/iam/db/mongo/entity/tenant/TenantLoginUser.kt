package nancal.iam.db.mongo.entity

import nbcp.db.*
import nbcp.db.mongo.entity.BasicUserLoginInfo
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/15-17:10
 */
@Document
@DbEntityGroup("tenant")
@DbEntityFieldRefs(
    DbEntityFieldRef("userId", "loginName", TenantUser::class, "id"),
    DbEntityFieldRef("userId", "mobile", TenantUser::class, "id"),
    DbEntityFieldRef("userId", "email", TenantUser::class, "id"),
    DbEntityFieldRef("userId", "enabled", TenantUser::class, "id"),
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class),
)
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("tenant.id", unique = false),
    DbEntityIndex("userId", unique = false),
    DbEntityIndex("loginName", unique = true),
    DbEntityIndex("tenant.id", "mobile", unique = false),
    DbEntityIndex("tenant.id", "email", unique = false),
)
@RemoveToSysDustbin
@Cn("用户登录表")
data class TenantLoginUser(
    @Cn("是否可用")
    var enabled: Boolean = true,
    var tenant: IdName = IdName(),
    @Cn("登录错误次数")
    var errorLoginTimes: Byte = 0,
    @Cn("是否遗忘密码")
    var forget_password: Boolean = false,
    @Cn("上次修改密码时间")
    var lastUpdatePwdAt: LocalDateTime = LocalDateTime.now(),

    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted :Boolean? =false,

    @Cn("是否第一次登录")
    var isFirstLogin : Boolean = true,

    @Cn("密码的盐")
    var passwordSalt : String = "",

    @Cn("窗口期密码提醒次数-自动发送")
    var autoRemindPwdTimes: Int = 0,

    @Cn("窗口期密码提醒次数-手动动发送")
    var manualRemindPwdTimes : Int = 0,

    @Cn("过期密码提醒次数-自动发送")
    var autoExpirePwdTimes: Int = 0,

    @Cn("过期密码提醒次数-手动发送")
    var manualExpirePwdTimes: Int = 0,

) : BasicUserLoginInfo()
