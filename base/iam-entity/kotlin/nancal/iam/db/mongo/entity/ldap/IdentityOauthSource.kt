package nancal.iam.db.mongo.entity.ldap

import nancal.iam.db.mongo.LoginTypeEnum
import nancal.iam.db.mongo.entity.SysApplication
import nancal.iam.db.mongo.entity.Tenant
import nancal.iam.db.mongo.entity.TenantApplication
import nancal.iam.db.mongo.entity.TenantIdentitySourceApp
import nbcp.db.*
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.time.LocalDateTime


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
@Cn("Oauth2.0企业身份源")
open class IdentityOauthSource (

    @Cn("主键")
    var id: String = "",

    @Cn("所属租户")
    var tenant: IdName = IdName(),

    @Cn("唯一标识符")
    var code: String = "",

    @Cn("显示名称")
    var name: String = "",

    @Cn("授权url")
    var url: String = "",

    @Cn("tokenUrl")
    var tokenUrl: String = "",

    @Cn("创建日期")
    var buildAt: LocalDateTime? = null,

    @Cn("Bind DN")
    var scope: String = "",

    @Cn("client ID")
    var clientId: String = "",

    @Cn("client Security")
    var clientSecurity: String = "",

    @Cn("登录模式")
    var loginType: LoginTypeEnum = LoginTypeEnum.NormalMode,

    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted: Boolean? = false,

    @Cn("授权的应用")
    var tenantApps: MutableList<TenantIdentitySourceApp> = mutableListOf(),

    ): Serializable