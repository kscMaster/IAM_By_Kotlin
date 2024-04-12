package nancal.iam.db.redis

import nancal.iam.db.mongo.entity.socialIdentitySource.TenantWeChatLoginUser

/**
 * @Author wrk
 *
 * @Description
 * @Date 2022/2/9-14:58
 */
data class WeChatLoginData(
    //场景id
    var sceneValue: String = "",
    //配置的唯一标识
    var identitySourceLinkId:String="",
    //微信公众号用户openid
    var wxOpenId: String = "",
    //微信小程序用户openid
    var wxAppOpenId: String = "",
    //iam应用
    var iamAppCode:String="",
    //accessToken
    var accessToken: String = "",
    //租户id
    var tenantId:String="",
    //用户信息
    var user:TenantWeChatLoginUser= TenantWeChatLoginUser()
)