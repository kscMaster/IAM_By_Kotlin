package nancal.iam.db.mongo.entity.socialIdentitySource

import nancal.iam.db.mongo.SocialIdentitySourceTypeEnum
import nancal.iam.db.mongo.WeChatMessageDecryptMethodEnum
import nancal.iam.db.mongo.entity.SysApplication
import nancal.iam.db.mongo.entity.Tenant
import nancal.iam.db.mongo.entity.TenantApplication
import nancal.iam.db.mongo.entity.TenantIdentitySourceApp
import nbcp.comm.JsonMap
import nbcp.db.*
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @Author wrk
 *
 * @Description
 * @Date 2022/2/11-10:35
 */
@Document
@DbEntityGroup("tenant")
@DbEntityFieldRefs(
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class),
    DbEntityFieldRef("tenantApps.sysAppId", "tenantApps.$.codeName.name", SysApplication::class, "id"),
    DbEntityFieldRef("tenantApps.sysAppId", "tenantApps.$.codeName.code", SysApplication::class, "id", "appCode"),
    DbEntityFieldRef("tenantApps.sysAppId", "tenantApps.$.sysAppStatus", SysApplication::class, "id", "enabled"),
    DbEntityFieldRef("tenantApps.id", "tenantApps.$.tenantAppStatus", TenantApplication::class,"id","enabled"),
    )
@RemoveToSysDustbin
@Cn("社会身份源")
open class SocialIdentitySourceConfig(
    @Cn("应用登录方式  weixin:WeChatOfficialAccount(微信公众号扫码关注登录)weixin:WeChatApplet(小程序扫码登录)")
    var loginType: String= "",
    @Cn("社会化身份源类型")
    var socialType: SocialIdentitySourceTypeEnum = SocialIdentitySourceTypeEnum.None,
    @Cn("身份源连接的唯一标识 租户下唯一")
    var identitySourceLinkId: String = "",
    @Cn("显示名称")
    var name: String = "",
    @Cn("租户")
    var tenant: IdName = IdName(),
    @Cn("配置settings")
    var settings: JsonMap = JsonMap(),
    @Cn("配置是否启用")
    //true 启用   false 停用
    var configStatus: Boolean = false,
    @Cn("租户应用")
    var tenantApps: MutableList<TenantIdentitySourceApp> = mutableListOf()
) : BaseEntity()