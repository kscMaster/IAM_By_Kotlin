package nancal.iam.db.mongo.entity

import nbcp.db.*
import nbcp.db.mongo.entity.BasicUserLoginInfo
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/15-17:14
 */
@Document
@DbEntityGroup("tenant")
@DbEntityFieldRefs(
    DbEntityFieldRef("userId", "loginName", TenantAdminUser::class, "id"),
    DbEntityFieldRef("userId", "mobile", TenantAdminUser::class, "id"),
    DbEntityFieldRef("userId", "email", TenantAdminUser::class, "id"),
    DbEntityFieldRef("userId", "enabled", TenantAdminUser::class, "id"),
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class),
)
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("tenant.id", unique = false),
    DbEntityIndex("userId", unique = false),
    DbEntityIndex("loginName", unique = true),
    DbEntityIndex("mobile", unique = false),
    DbEntityIndex("email", unique = false),
)
@RemoveToSysDustbin
@Cn("租户管理员账号（废弃）")
data class TenantAdminLoginUser(
    var tenant: IdName = IdName(),
    @Cn("是否可用")
    var enabled: Boolean = true,
    @Cn("登录错误次数")
    var errorLoginTimes: Byte = 0,
    @Cn("是否遗忘密码")
    var forget_password: Boolean = false,
    @Cn("上次更新密码时间")
    var lastUpdatePwd: LocalDateTime = LocalDateTime.now(),

    @Cn("是否第一次登录")
    var isFirstLogin : Boolean = true,

    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted :Boolean? =false,

    @Cn("密码的盐")
    var passwordSalt : String = "",
) : BasicUserLoginInfo()
