package nancal.iam.mvc.social

import cn.hutool.core.util.StrUtil
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import nancal.iam.annotation.BizLog
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.TenantIdentitySourceApp
import nancal.iam.db.mongo.entity.WeChatConfig
import nancal.iam.db.mongo.entity.socialIdentitySource.SocialIdentitySourceConfig
import nbcp.comm.*
import nbcp.db.CodeName
import nbcp.db.db
import nbcp.db.mongo.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

/**
 * @Author wrk
 *
 * @Description 社会化身份源
 * @Date 2022/2/11-14:35
 */
@Api(description = "社会化身份源", tags = arrayOf("SocialIdentitySource"))
@RestController
@RequestMapping("/tenant/social-identity-source")
class SocialIdentitySourceConfigController {

    class SocialIdentitySourceVo(
        //显示名称
        var name: String = "",
        //社会化身份源类型
        var socialType: SocialIdentitySourceTypeEnum = SocialIdentitySourceTypeEnum.None,
        //登录方式
        var loginType: String = "",
        //应用启用个数
        var enableAppCount: Int = 0
    )

    class SocialIdentitySourceConfigVo(
        var openCount: Int = 0
    ) : SocialIdentitySourceConfig()


    //针对某个登录方式的应用修改状态
    class TenantAppLoginStatus(
        var id: String = "",
        var configStatus: Boolean? = false,
        var tenantApp: TenantIdentitySourceApp? = TenantIdentitySourceApp()
    )

    @ApiOperation("查社会化身份源列表")
    @PostMapping("/listSocialIdentity")
    fun listSocialIdentity(
        request: HttpServletRequest
    ): ApiResult<List<SocialIdentitySourceVo>> {
        val tenant = request.LoginTenantAdminUser.tenant
        val weChatList = mor.tenant.socialIdentitySourceConfig.query()
            .where { it.tenant.id match tenant.id }
            .toList()
        var socialList = mutableListOf<SocialIdentitySourceVo>()
        weChatList.forEach {
            val socialIdentitySource = SocialIdentitySourceVo()
            socialIdentitySource.enableAppCount = it.tenantApps.filter { it.status == true }.size
            socialIdentitySource.name = it.name
            socialIdentitySource.loginType = it.loginType
            socialIdentitySource.socialType = it.socialType
            socialList.add(socialIdentitySource)
        }
        return ApiResult.of(socialList)
    }

    @ApiOperation("查社会化身份源状态")
    @PostMapping("/listSocialIdentityStatus")
    fun listSocialIdentityStatus(
        request: HttpServletRequest
    ): ApiResult<Map<String, Boolean>> {
        val tenant = request.LoginTenantAdminUser.tenant

        val configList = mor.tenant.socialIdentitySourceConfig.query()
            .where { it.tenant.id match tenant.id }
            .toList()
        val hasWeChat = configList.filter { it.socialType == SocialIdentitySourceTypeEnum.weixin }.isNotEmpty()
        val hasQQ = configList.filter { it.socialType == SocialIdentitySourceTypeEnum.qq }.isNotEmpty()
        val hasAlipay = configList.filter { it.socialType == SocialIdentitySourceTypeEnum.zhifubao }.isNotEmpty()
        return ApiResult.of(
            mapOf(
                SocialIdentitySourceTypeEnum.weixin.name to hasWeChat,
                SocialIdentitySourceTypeEnum.qq.name to hasQQ,
                SocialIdentitySourceTypeEnum.zhifubao.name to hasAlipay
            )
        )
    }

    @BizLog(BizLogActionEnum.Save,BizLogResourceEnum.SocietyIdentity,"社会化身份源")
    @ApiOperation("新增/更新")
    @PostMapping("/saveSocialIdentity")
    fun saveSocialIdentity(
        @JsonModel entity: SocialIdentitySourceConfig,
        request: HttpServletRequest
    ): ApiResult<String> {
        if (entity.id.isEmpty()) request.logMsg="创建社会化身份源{${entity.name}}"
        if (entity.id.isNotEmpty()) request.logMsg="修改社会化身份源{${entity.name}}"
        val tenant = request.LoginTenantAdminUser.tenant
        entity.tenant = tenant
        if (entity.id.isEmpty()) {
            //目前只允许一个租户添加一个公众号配置
            val hasConfig = mor.tenant.socialIdentitySourceConfig.query()
                .where { it.tenant.id match tenant.id }
                .where { it.loginType match entity.loginType }
                .exists()
            if (hasConfig) {
                if (WeChatLoginTypeEnum.WeChatOfficialAccount.name.equals(entity.socialType)) return ApiResult.error("您只能添加一个公众号配置")
                if (WeChatLoginTypeEnum.WeChatAppletAuthorize.name.equals(entity.socialType)) return ApiResult.error("您只能添加一个小程序配置")
                return ApiResult.error("您只能添加一个配置")
            }
        }

        //校验
        val msg = checkIdentitySource(tenant.id, entity)
        if (msg.isNotEmpty()) return ApiResult.error(msg)
        if (entity.id.isEmpty()) {
            //插入
            val tenantApps: MutableList<TenantIdentitySourceApp> = mutableListOf()
            val apps = mor.tenant.tenantApplication.query()
                .where { it.tenant.id match tenant.id }
                .toList()
            apps.forEach {
                val tenantIdentitySourceApp: TenantIdentitySourceApp = TenantIdentitySourceApp()
                tenantIdentitySourceApp.id=it.id
                tenantIdentitySourceApp.tenantAppStatus=it.enabled
                tenantIdentitySourceApp.codeName = CodeName(it.appCode, it.name)
                tenantIdentitySourceApp.logo = it.logo
                tenantIdentitySourceApp.isSysDefine=it.isSysDefine
                //如果是admin侧应用
                if(it.isSysDefine){
                    val sysApp = mor.iam.sysApplication.queryById(it.sysId).toEntity()
                    if (sysApp != null) {
                        tenantIdentitySourceApp.sysAppId = sysApp.id
                        tenantIdentitySourceApp.sysAppStatus = sysApp.enabled
                    }
                }
                tenantApps.add(tenantIdentitySourceApp)

            }
            entity.tenantApps = tenantApps
            mor.tenant.socialIdentitySourceConfig.doInsert(entity)

        } else {
            if(entity.identitySourceLinkId.isNotEmpty()) return ApiResult.error("唯一标识符不可修改")
            mor.tenant.socialIdentitySourceConfig.update()
                .where { it.tenant.id match tenant.id }
                .where { it.id match entity.id }
                .set { it.settings to entity.settings }
                .set { it.name to entity.name }
                .exec()
            if (db.affectRowCount == 0) return ApiResult.error("更新失败")

        }
        return ApiResult.of(entity.id)
    }

    @BizLog(BizLogActionEnum.Delete,BizLogResourceEnum.SocietyIdentity,"社会化身份源")
    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {

        var tenant = request.LoginTenantAdminUser.tenant
        val soc=mor.tenant.socialIdentitySourceConfig.queryById(id)
            .where{it.tenant.id match tenant.id}
            .toEntity()
            .must().elseThrow { "找不到数据" }
        request.logMsg="删除社会化身份源{${soc.name}}"
        mor.tenant.socialIdentitySourceConfig.deleteById(id)
            .where { it.tenant.id match tenant.id }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
                return JsonResult()
            }
    }

    @ApiOperation("详情")
    @PostMapping("/detail")
    fun detail(
        @Require socialType: String,
        request: HttpServletRequest
    ): ListResult<SocialIdentitySourceConfigVo> {
        var tenant = request.LoginTenantAdminUser.tenant
        mor.tenant.socialIdentitySourceConfig.query()
            .where { it.tenant.id match tenant.id }
            .where { it.socialType match socialType }
            .toListResult(SocialIdentitySourceConfigVo::class.java)
            .apply {
                this.data.forEach {
                    it.openCount = it.tenantApps.filter { app -> app.sysAppStatus == true && app.tenantAppStatus == true }
                        .filter { app -> app.status == true }.size
                }
                return this;
            }
    }


    @BizLog(BizLogActionEnum.Enable,BizLogResourceEnum.App,"社会化身份源应用管理")
    @ApiOperation("对应用启用社会化身份源某个登录方式")
    @PostMapping("/enable")
    fun enabled(
        @JsonModel tenantAppLoginStatus: TenantAppLoginStatus,
        request: HttpServletRequest
    ): ApiResult<String> {
        request.logMsg="社会化身份源开启应用"
        if(tenantAppLoginStatus.tenantApp!=null){
            tenantAppLoginStatus.tenantApp!!.status=true
        }
        if(tenantAppLoginStatus.configStatus!=null && !tenantAppLoginStatus.configStatus!!){
            return ApiResult.error("参数错误")
        }

        var msg=checkSetStatus(tenantAppLoginStatus)
        if(msg.isNotEmpty()) return ApiResult.error(msg)
        val tenant = request.LoginTenantAdminUser.tenant
        val config = mor.tenant.socialIdentitySourceConfig.queryById(tenantAppLoginStatus.id)
            .where { it.tenant.id match tenant.id }
            .toEntity()
        if (config == null) return ApiResult.error("找不到配置项")
        if(tenantAppLoginStatus.configStatus != null && tenantAppLoginStatus.configStatus!!) request.logMsg="社会化身份源开启全部应用"
        //全部停用全部启用 调整完毕
        if (tenantAppLoginStatus.configStatus != null) {
            config.tenantApps.forEach {
                if( it.tenantAppStatus){
                    it.status = tenantAppLoginStatus.configStatus!!
                }
            }
            mor.tenant.socialIdentitySourceConfig.update()
                .where { it.tenant.id match tenant.id }
                .where { it.id match tenantAppLoginStatus.id }
                .set { it.configStatus to tenantAppLoginStatus.configStatus }
                .set { it.tenantApps to config.tenantApps }
                .exec()
            if (db.affectRowCount == 0) {
                return ApiResult.error("修改失败")
            }
            return ApiResult()
        }


        config.tenantApps.filter { it.id == tenantAppLoginStatus.tenantApp!!.id }.apply {
            if (this.size == 0) {
                return ApiResult.error("找不到应用")
            }
        }

        //修改某个app的状态
        config.tenantApps.forEach {
            if (it.id == tenantAppLoginStatus.tenantApp!!.id) {
                request.logMsg="社会化身份源应用{${it.codeName.name}}开启"
                if(it.tenantAppStatus){
                    it.status = tenantAppLoginStatus.tenantApp!!.status
                }else{
                    return ApiResult.error("应用已被禁用")
                }

            }
        }
        mor.tenant.socialIdentitySourceConfig.update()
            .where { it.tenant.id match tenant.id }
            .where { it.id match tenantAppLoginStatus.id }
            .set { it.tenantApps to config.tenantApps }
            .exec()
        //如果所有应用为true或者false 则修改总开关
        val newConfig = mor.tenant.socialIdentitySourceConfig.query()
            .where { it.id match tenantAppLoginStatus.id }
            .toEntity()
        val appSize = newConfig!!.tenantApps.filter { it.tenantAppStatus == true }.size
        val openNumber = newConfig!!.tenantApps.filter { it.status == true }.filter { it.tenantAppStatus == true  }.size
        val closeNumber = newConfig!!.tenantApps.filter { it.status == false }.filter { it.tenantAppStatus == true }.size
        if (openNumber == appSize) {
            //修改总开关为true
            mor.tenant.socialIdentitySourceConfig.update()
                .where { it.tenant.id match tenant.id }
                .where { it.id match tenantAppLoginStatus.id }
                .set { it.configStatus to true }
                .exec()
        }
        if (closeNumber == appSize) {
            //修改总开关为false
            mor.tenant.socialIdentitySourceConfig.update()
                .where { it.tenant.id match tenant.id }
                .where { it.id match tenantAppLoginStatus.id }
                .set { it.configStatus to false }
                .exec()

        }
        if (db.affectRowCount == 0) {
            return ApiResult.error("修改失败")
        }
        return ApiResult()
    }
    @BizLog(BizLogActionEnum.Disable,BizLogResourceEnum.App,"社会化身份源应用管理")
    @ApiOperation("对应用停用社会化身份源某个登录方式")
    @PostMapping("/disable")
    fun disabled(
        @JsonModel tenantAppLoginStatus: TenantAppLoginStatus,
        request: HttpServletRequest
    ): ApiResult<String> {
        request.logMsg="社会化身份源关闭应用"
        if(tenantAppLoginStatus.tenantApp!=null){
            tenantAppLoginStatus.tenantApp!!.status=false
        }
        if(tenantAppLoginStatus.configStatus!=null && tenantAppLoginStatus.configStatus!!){
            return ApiResult.error("参数错误")
        }

        var msg=checkSetStatus(tenantAppLoginStatus)
        if(msg.isNotEmpty()) return ApiResult.error(msg)
        val tenant = request.LoginTenantAdminUser.tenant
        val config = mor.tenant.socialIdentitySourceConfig.queryById(tenantAppLoginStatus.id)
            .where { it.tenant.id match tenant.id }
            .toEntity()
        if (config == null) return ApiResult.error("找不到配置项")
        if(tenantAppLoginStatus.configStatus != null  && !tenantAppLoginStatus.configStatus!!) request.logMsg="社会化身份源关闭全部应用"
        //全部停用全部启用 调整完毕
        if (tenantAppLoginStatus.configStatus != null) {
            config.tenantApps.forEach {
                if( it.tenantAppStatus){
                    it.status = tenantAppLoginStatus.configStatus!!
                }
            }
            mor.tenant.socialIdentitySourceConfig.update()
                .where { it.tenant.id match tenant.id }
                .where { it.id match tenantAppLoginStatus.id }
                .set { it.configStatus to tenantAppLoginStatus.configStatus }
                .set { it.tenantApps to config.tenantApps }
                .exec()
            if (db.affectRowCount == 0) {
                return ApiResult.error("修改失败")
            }
            return ApiResult()
        }


        config.tenantApps.filter { it.id == tenantAppLoginStatus.tenantApp!!.id }.apply {
            if (this.size == 0) {
                return ApiResult.error("找不到应用")
            }
        }

        //修改某个app的状态
        config.tenantApps.forEach {
            if (it.id == tenantAppLoginStatus.tenantApp!!.id) {
                request.logMsg="社会化身份源应用{${it.codeName.name}}停用"
                if(it.tenantAppStatus){
                    it.status = tenantAppLoginStatus.tenantApp!!.status
                }else{
                    return ApiResult.error("应用已被禁用")
                }


            }
        }
        mor.tenant.socialIdentitySourceConfig.update()
            .where { it.tenant.id match tenant.id }
            .where { it.id match tenantAppLoginStatus.id }
            .set { it.tenantApps to config.tenantApps }
            .exec()
        //如果所有应用为true或者false 则修改总开关
        val newConfig = mor.tenant.socialIdentitySourceConfig.query()
            .where { it.id match tenantAppLoginStatus.id }
            .toEntity()
        val appSize = newConfig!!.tenantApps.filter { it.tenantAppStatus == true }.size
        val openNumber = newConfig!!.tenantApps.filter { it.status == true }.filter { it.tenantAppStatus == true  }.size
        val closeNumber = newConfig!!.tenantApps.filter { it.status == false }.filter { it.tenantAppStatus == true }.size
        if (openNumber == appSize) {
            //修改总开关为true
            mor.tenant.socialIdentitySourceConfig.update()
                .where { it.tenant.id match tenant.id }
                .where { it.id match tenantAppLoginStatus.id }
                .set { it.configStatus to true }
                .exec()
        }
        if (closeNumber == appSize) {
            //修改总开关为false
            mor.tenant.socialIdentitySourceConfig.update()
                .where { it.tenant.id match tenant.id }
                .where { it.id match tenantAppLoginStatus.id }
                .set { it.configStatus to false }
                .exec()

        }
        if (db.affectRowCount == 0) {
            return ApiResult.error("修改失败")
        }
        return ApiResult()
    }

    fun checkSetStatus(tenantAppLoginStatus: TenantAppLoginStatus):String{
        if(tenantAppLoginStatus.configStatus==null) {
            if (tenantAppLoginStatus.tenantApp == null) return "应用信息为空"
            if (tenantAppLoginStatus.tenantApp!!.id.isEmpty()) return "应用id为空"
        }
        return ""
    }

    fun checkIdentitySource(tenantId: String, entity: SocialIdentitySourceConfig): String {
        //仅支持微信
        if (entity.socialType != SocialIdentitySourceTypeEnum.weixin) return "您只能创建微信社会化身份源"
        if(entity.loginType.isEmpty()) return "登录方式不能为空"
        if (entity.socialType == SocialIdentitySourceTypeEnum.weixin && !"weixin:WeChatOfficialAccount".equals(entity.loginType))  return "登录方式填写错误"
        if (entity.settings == null || entity.settings.isEmpty()) return "请填写微信公众号的配置"

        //唯一标识符 必须由小写字母、数字、- 组成， 且长度小于32位  ^[a-z0-9\-]+$
        if (entity.id.isEmpty()) {
            val identitySourceLinkIdMatch = "^[a-z0-9\\-]+\$".toRegex()
            if (!identitySourceLinkIdMatch.containsMatchIn(entity.identitySourceLinkId)) return "唯一标识符必须由小写字母、数字、-组成,且长度小于32位"
            if (entity.identitySourceLinkId.isEmpty()) return "唯一标识符不能为空"
            if(entity.identitySourceLinkId.length>32) return "唯一标识符不能超过32位"
            val exist = mor.tenant.socialIdentitySourceConfig.query()
                .where { it.tenant.id match tenantId }
                .where { it.identitySourceLinkId match entity.identitySourceLinkId }
                .exists()
            if (exist) return "唯一标识符已存在，请修改唯一标识符"
        }
        if(entity.id.isNotEmpty() && entity.identitySourceLinkId.isNotEmpty()) return "唯一标识符不允许修改"
        if (entity.name.isEmpty()) return "显示名称不能为空"
        if (StrUtil.containsBlank(entity.name)) return "显示名称不能包含空格"
        if (entity.name.length > 20) return "显示名称不能超过20个字符"
        if (entity.name.length < 2) return "显示名称应大于2个字符"
        val config: WeChatConfig = entity.settings.ConvertJson(WeChatConfig::class.java)
        if (config.weChatAppId.isEmpty()) return "AppID不能为空"
        if (StrUtil.containsBlank(config.weChatAppId)) return "AppID不能包含空格"
        if (config.weChatAppId.length > 120) return "AppID不能超过120个字符"
        if (config.weChatAppSecret.isEmpty()) return "AppSecret不能为空"
        if (StrUtil.containsBlank(config.weChatAppSecret)) return "AppSecret不能包含空格"
        if (config.weChatAppSecret.length > 120) return "AppSecret不能超过120个字符"
        if (config.weChatToken.isEmpty()) return "令牌不能为空"
        if (StrUtil.containsBlank(config.weChatToken)) return "令牌不能包含空格"
        if (config.weChatToken.length > 120) return "令牌不能超过120个字符"
        if (config.messageDecryptMethod.isEmpty()) return "消息加密方式不能为空"
        if (config.messageDecryptMethod != WeChatMessageDecryptMethodEnum.plain.name
            && config.messageDecryptMethod != WeChatMessageDecryptMethodEnum.compatible.name
            && config.messageDecryptMethod != WeChatMessageDecryptMethodEnum.secure.name
        ) return "消息加密方式填写错误"
        if (config.messageDecryptMethod == WeChatMessageDecryptMethodEnum.secure.name || config.messageDecryptMethod == WeChatMessageDecryptMethodEnum.compatible.name) {
            //EncodingAESKey判断
            //唯一标识符 必须由小写字母、数字、- 组成， 且长度小于32位  ^[a-z0-9\-]+$
            val keyMatch = "^[a-zA-z0-9]+\$".toRegex()
            if (config.aesKey.isEmpty()) return "EncodingAESKey不能为空"
            if (StrUtil.containsBlank(config.aesKey)) return "EncodingAESKey不能包含空格"
            if (config.aesKey.length != 43) return "EncodingAESKey必须为43位字符"
            if (!keyMatch.containsMatchIn(config.aesKey)) return "EncodingAESKey字符范围必须为A-Z,a-z,0-9"
        }
        return ""
    }
}