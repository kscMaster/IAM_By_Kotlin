package nancal.iam.db.mongo.entity.socialIdentitySource

import nancal.iam.db.mongo.entity.SysApplication
import nancal.iam.db.mongo.entity.Tenant
import nbcp.db.*
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @Description 微信登录账号
 *
 * @param
 * @return
 * @date 14:34 2022/2/10
 */
@DbEntityFieldRefs(
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class)
)
@Document
@DbEntityGroup("tenant")
@RemoveToSysDustbin
@Cn("微信身份源")
data class TenantWeChatLoginUser(
    @Cn("用户id")
    var userId: String = "",
    var tenant: IdName = IdName(),
    @Cn("微信公众号openId")
    var wxOpenId: String = "",
    @Cn("微信小程序openId")
    var wxAppOpenId: String = "",
    @Cn("社会化身份源唯一标识")
    var identitySourceLinkId: String = ""

): BaseEntity()
