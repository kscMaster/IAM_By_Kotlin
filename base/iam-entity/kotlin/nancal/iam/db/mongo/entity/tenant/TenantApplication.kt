package nancal.iam.db.mongo.entity

import nbcp.db.*
import nbcp.db.cache.RedisCacheDefine
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.time.LocalDateTime

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/15-17:05
 */
/**
 * 企业信息
 */
@Document
@DbEntityGroup("tenant")
@RemoveToSysDustbin
@DbEntityIndexes(
    DbEntityIndex("tenant.id", unique = false, cacheable = true)
)
@DbEntityFieldRefs(
    DbEntityFieldRef("appCode", "name", SysApplication::class, "appCode"),
    DbEntityFieldRef("appCode", "remark", SysApplication::class, "appCode"),
    DbEntityFieldRef("appCode", "logo", SysApplication::class, "appCode"),
//    DbEntityFieldRef("appCode", "logo.name", SysApplication::class, "appCode"),
    DbEntityFieldRef("appCode", "industry", SysApplication::class, "appCode"),
    DbEntityFieldRef("appCode", "userType", SysApplication::class, "appCode"),
    DbEntityFieldRef("appCode", "url", SysApplication::class, "appCode"),
    DbEntityFieldRef("appCode", "ename", SysApplication::class, "appCode"),
    DbEntityFieldRef("appCode", "lable", SysApplication::class, "appCode"),
//    DbEntityFieldRef("id", "name", SysApplication::class ),
//    DbEntityFieldRef("id", "appCode", SysApplication::class),
//    DbEntityFieldRef("id", "remark", SysApplication::class ),
//    DbEntityFieldRef("id", "logo.id", SysApplication::class ),
//    DbEntityFieldRef("id", "logo.name", SysApplication::class),
//    DbEntityFieldRef("id", "industry", SysApplication::class),
//    DbEntityFieldRef("id", "userType", SysApplication::class),
//    DbEntityFieldRef("id", "url", SysApplication::class),
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class)
)
@VarDatabase("tenant.id")
@Cn("租户应用")
open class TenantApplication() : BaseApplication() {

    /**
     * 租户
     */
    var tenant: IdName = IdName()

    /**
     * =true 除了黑名单的人，所有人能登录
     * =false 除了黑名单的人，只允许白名单登录。
     * 也即 黑名单的人优先级最高。
     */
    @Cn("是否是开放系统")
    var isOpen: Boolean = false

    /**
     * =true
     * =false 这个租户下的全部人员不能登录
     */
    @Cn("启用true/禁用false")
    var enabled: Boolean = true

    @Cn("是否系统默认")
    var isSysDefine: Boolean = false

    @Cn("系统默认ID")
    var sysId: String = ""


}