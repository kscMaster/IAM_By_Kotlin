package nancal.iam.db.mongo.entity

import nbcp.db.*
import nbcp.db.cache.RedisCacheDefine
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/15-17:15
 */
@Document
@DbEntityGroup("tenant")
@Cn("租户应用角色")
@DbEntityFieldRefs(
    DbEntityFieldRef("appInfo.code", "appInfo.name", TenantApplication::class, "appCode"),
   // DbEntityFieldRef("appInfo.code", "appInfo.name", SysApplication::class),
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class)
)
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("tenant.id", unique = false),
    DbEntityIndex("appInfo.code", unique = false),
    DbEntityIndex("tenant.id", "appInfo.code", "name", unique = true),
)
@VarDatabase("tenant.id")
@RemoveToSysDustbin
open class TenantAppRole @JvmOverloads constructor(
    @Cn("应用IdName")
    var appInfo: CodeName = CodeName(),

    @Cn("租户")
    var tenant: IdName = IdName(),

    @Cn("角色名称")
    var name: String = "",

    @Cn("是否系统默认")
    var isSysDefine: Boolean = false,

    @Cn("系统默认ID")
    var sysId: String = "",

    @Cn("备注")
    var remark: String = "",

    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted :Boolean? =false,

    @Cn("不允许被删除(true：隐藏删除按钮；false：不隐藏删除按钮)")
    var notAllowDeleted: Boolean = false

) : BaseEntity()