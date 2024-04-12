package nancal.iam.db.mongo.entity

import nbcp.db.*
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
@DbEntityGroup("iam")
@RemoveToSysDustbin
@DbEntityFieldRefs(
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class),
    DbEntityFieldRef("tenantApps.sysAppId", "tenantApps.$.codeName.name", SysApplication::class, "id"),
    DbEntityFieldRef("tenantApps.sysAppId", "tenantApps.$.codeName.code", SysApplication::class, "id","appCode"),
    DbEntityFieldRef("tenantApps.sysAppId", "tenantApps.$.sysAppStatus", SysApplication::class, "id","enabled"),
    DbEntityFieldRef("tenantApps.sysAppId", "tenantApps.$.logo.id", SysApplication::class, "id"),
    DbEntityFieldRef("tenantApps.sysAppId", "tenantApps.$.logo.url", SysApplication::class, "id"),
    DbEntityFieldRef("tenantApps.id", "tenantApps.$.tenantAppStatus", TenantApplication::class,"id","enabled"),
)
@DbEntityIndexes(
    DbEntityIndex("code", unique = false),
    DbEntityIndex("name", unique = false),
)
@Cn("企业身份源")
open class IdentitySource(

    @Cn("主键")
    var id: String = "",

    @Cn("所属租户")
    var tenant: IdName = IdName(),
    @Cn("唯一标识符")
    var code: String = "",
    @Cn("显示名称")
    var name: String = "",
    @Cn("创建日期")
    var buildAt: LocalDateTime? = null,
    @Cn("LDAP链接")
    var url: String = "",

    @Cn("Bind DN")
    var bindDN: String = "",
    @Cn("Bind DN密码")
    var bindDNPassword: String = "",
    @Cn("Base DN")
    var baseDN: String = "",
    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted: Boolean? = false,

    @Cn("授权的应用")
    var tenantApps: MutableList<TenantIdentitySourceApp> = mutableListOf(),

) :Serializable


