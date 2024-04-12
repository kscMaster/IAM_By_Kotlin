package nancal.iam.mvc

import cn.hutool.core.map.MapUtil
import cn.hutool.core.util.IdUtil
import nancal.iam.base.constant.SysConstants
import nancal.iam.client.MPClient
import nancal.iam.db.mongo.MobileCodeModuleEnum
import nancal.iam.db.mongo.PwdExpires
import nancal.iam.db.mongo.WeChatMessageDecryptMethodEnum
import nancal.iam.db.mongo.entity.TenantIdentitySourceApp
import nancal.iam.db.mongo.entity.WeChatConfig
import nancal.iam.db.mongo.entity.socialIdentitySource.TenantWeChatLoginUser
import nancal.iam.db.mongo.mor
import nancal.iam.db.redis.WeChatLoginData
import nancal.iam.db.redis.rer
import nancal.iam.db.sql.dbr
import nancal.iam.service.OAuthTenantUserService
import nancal.iam.util.ValidateUtils
import nancal.iam.utils.WeChatUtils
import nancal.iam.weixinaes.WXBizMsgCrypt
import nbcp.comm.ApiResult
import nbcp.comm.ConvertJson
import nbcp.comm.Require
import nbcp.db.CodeName
import nbcp.db.IdName
import nbcp.db.mongo.*
import nbcp.web.userAuthenticationService
import org.dom4j.io.SAXReader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.io.InputStream
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @Author wrk
 *
 * @Description 公众号登录 sass
 * @Date 2022/2/7-13:49
 */
@RestController
@RequestMapping("/wechat")
class WechatController {

    @Resource
    lateinit var mpClient: MPClient

    @Autowired
    lateinit var tenantUserService: OAuthTenantUserService


    /**
     * @Description 获取微信公众号临时二维码
     * 换取临时二维码ticket，设置过期时间-最大不超过2592000（即30天）
     *
     * @param identitySourceLinkId 租户下唯一标识
     * @param tenantId 租户id
     * @param code 应用code
     * @return
     */
    @GetMapping("/tempQRCodeUrl")
    fun getRTempQRCodeUrl(
        @Require identitySourceLinkId: String,
        @Require tenantId: String,
        @Require code: String,
        request: HttpServletRequest
    ): ApiResult<Map<String, String>> {
        //校验
        checkQRCodeParams(identitySourceLinkId, tenantId, code)
            .apply {
                if (this.isNotEmpty()) return ApiResult.error(this)
            }

        //场景id
        val sceneStr: String = IdUtil.fastUUID()
        println("--------场景id-----------$sceneStr")
        val accessToken: WeChatUtils.AccessToken = WeChatUtils.accessToken(tenantId, identitySourceLinkId)
        //将场景accessToken,场景id存入redis access_token的有效期为2个小时
        val weChatLoginData = WeChatLoginData()
        weChatLoginData.identitySourceLinkId = identitySourceLinkId
        weChatLoginData.iamAppCode = code
        weChatLoginData.tenantId = tenantId
        weChatLoginData.sceneValue = sceneStr
        weChatLoginData.accessToken = accessToken.accessToken
        rer.sys.weChatLogin(sceneStr).set(weChatLoginData)
        //二维码有效期1800s
        val qrUrl: String = WeChatUtils.qrCodeCreateTmpTicket(accessToken.accessToken, 1800, sceneStr)
        val qrMap: MutableMap<String, String> = HashMap(16)
        qrMap["qrUrl"] = qrUrl
        qrMap["memberSceneId"] = sceneStr
        qrMap["expire"] = 1800.toString()
        return ApiResult.of(qrMap)
    }

    /**
     * 微信公众号关联验证 在路径区分来自那里
     *
     * @param identitySourceLinkId 用户设置的配置的唯一标识
     * @param tenantId 租户id
     * @param request
     * @param response
     */
    @GetMapping("/security/{tenantId}/{identitySourceLinkId}")
    fun securityGet(
        @Require identitySourceLinkId: String,
        @Require tenantId: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String {
        val config = mor.tenant.socialIdentitySourceConfig.query()
            .where { it.tenant.id match tenantId }
            .where { it.identitySourceLinkId match identitySourceLinkId }
            .toEntity()
        val weChatConfig = config!!.settings.ConvertJson(WeChatConfig::class.java)
        val signature = request.getParameter("signature")
        val timestamp = request.getParameter("timestamp")
        val nonce = request.getParameter("nonce")
        val echostr = request.getParameter("echostr")
        val isFromWeChat = WeChatUtils.checkSignature(weChatConfig.weChatToken, timestamp, nonce, signature)
        return if (isFromWeChat) echostr else "消息来源非微信服务器"
    }

    /**
     * 微信公众号扫码登录
     *
     * @param request
     * @param response
     */
    /* 微信发送的数据 数据结构：{
            "subscribe": true,
            "openId": "o4sUV6IE96R3iphLii3eU_GRUUkA",
            "nickname": "",
            "language": "zh_CN",
            "headImgUrl": "",
            "subscribeTime": 1644461556,
            "remark": "",
            "groupId": 0,
            "tagIds": [],
            "subscribeScene": "ADD_SCENE_QR_CODE",
            "qrScene": "0",
            "qrSceneStr": "fed7c585-1a86-49ce-9eb6-d56dc3814ead"
        }*/
    @PostMapping("/security/{tenantId}/{identitySourceLinkId}")
    fun securityPost(
        @Require tenantId: String,
        @Require identitySourceLinkId: String,
        timestamp: String,
        nonce: String,
        encrypt_type: String,
        msg_signature: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String {
        //校验配置的模式
        var config=mor.tenant.socialIdentitySourceConfig.query()
            .where { it.tenant.id match tenantId }
            .where { it.identitySourceLinkId match identitySourceLinkId }
            .toEntity()

        var fromXml = WeChatUtils.streamToString(request)

        if (config == null) throw RuntimeException("无配置")
        val wxConfig = config.settings.ConvertJson(WeChatConfig::class.java)
        if (wxConfig == null) throw RuntimeException("无配置")
        var mapData: Map<String, Any> = mutableMapOf()
        if(wxConfig.messageDecryptMethod == WeChatMessageDecryptMethodEnum.secure.name){
            if(encrypt_type.isEmpty()) throw RuntimeException("请前往微信公众平台配置消息加密方式为安全模式")
            if(!encrypt_type.equals("aes")) throw RuntimeException("请前往微信公众平台配置消息加密方式为安全模式")
        }
        if(wxConfig.messageDecryptMethod == WeChatMessageDecryptMethodEnum.plain.name){
            if(encrypt_type.isNotEmpty()) throw RuntimeException("请前往微信公众平台配置消息加密方式为明文模式")
        }

        if (encrypt_type.isNotEmpty() && encrypt_type.equals("aes")) {
            //解密操作
            val str = WXBizMsgCrypt.decryptMsg(
                msg_signature,
                timestamp,
                nonce,
                fromXml,
                wxConfig.aesKey,
                wxConfig.weChatAppId,
                wxConfig.weChatToken
            )
            mapData = WeChatUtils.multilayerXmlToMap(str)
        }else{
            mapData = WeChatUtils.multilayerXmlToMap(fromXml)
        }
        if (mapData != null) {
            var data=MapUtil.get(mapData,"xml",Map::class.java)
            //消息类型
            val msgType = data[SysConstants.MSG_TYPE].toString()
            //事件类型
            val event = data[SysConstants.Event].toString()
            //消息类型为事件 //
            if ("event" == msgType) {
                //关注--场景ID,或者已关注
                val eventKey = data[SysConstants.EVENT_KEY].toString()
                if (!event.isNullOrBlank() && event == "subscribe" || event == "SCAN") {
                    val openId = data[SysConstants.FROM_USER_NAME].toString()
                    //关注
                    memberAttention(eventKey, openId, request,fromXml,mapData)
                } else {
                    //取消关注
                    unsubscribe()

                }

            }
        }


        return ""
    }

    class LoginStatus(
        //是否需要绑定手机号
        var needBindPhone: Boolean = true,
        //是否成功登录
        var loginStatus: Boolean = false,
        //成功登录后返回code  使用code换二维码
        var codeModel: CodeModel? = CodeModel("", "", PwdExpires.Validity, ""),
        //二维码是否过期
        var qrCodeExpired: Boolean = false,
        //扫码状态 是否扫码并关注
        var scanQRCodeStatus: Boolean = false,
    )

    class CodeModel(
        var type: String,
        var code: String,
        var expires: PwdExpires?,
        var daysLeft: String?
    )

    /**
     * @Description 轮询接口  返回登录是否成功，是否需要绑定手机号
     *
     * @param
     * @return
     * @date 16:44 2022/2/10
     */
    @PostMapping("/memberLoginInfo/{memberSceneId}")
    fun memberLoginInfo(
        @Require memberSceneId: String,
        request: HttpServletRequest
    ): ApiResult<LoginStatus> {
        var sce = rer.sys.weChatLogin(memberSceneId).get()
        //二维码过期
        if (sce == null) {
            return ApiResult.of(LoginStatus(true, false, null, true, false))
        }
        //未扫码
        if (sce.wxOpenId.isEmpty()) {
            return ApiResult.of(LoginStatus(true, false, null, false, false))
        }
        //查redis 用户是否绑定 已经绑定的话直接登录  未绑定的话返回需要绑定手机号

        if (sce.user.userId.isEmpty()) {
            return ApiResult.of(LoginStatus(true, false, null, false, true))
        }
        //登录操作
        val user = mor.tenant.tenantLoginUser.query()
            .where { it.userId match sce.user.userId }
            .toEntity()
        val appInfo = mor.iam.sysApplication.queryByAppCode(sce.iamAppCode).toEntity()
        request.userAuthenticationService.deleteToken(request)

        var apiRes: ApiResult<OAuthTenantUserService.CodeModel> =
            tenantUserService.loginTenant(user!!.loginName, "", true, appInfo!!,request.getHeader("lang").toString())
        if (apiRes.data != null) {
            var codeModel = apiRes.data!!.ConvertJson(CodeModel::class.java)
            return ApiResult.of(LoginStatus(false, true, codeModel, false, true))
        }
        return ApiResult.error(apiRes.msg)

    }


    /**
     * @Description 发送验证码
     *
     * @param
     * @return
     * @date 16:44 2022/2/10
     */
    @PostMapping("/sendCode")
    fun sendCode(
        @Require phone: String,
        @Require memberSceneId: String,
        request: HttpServletRequest
    ): ApiResult<String> {
        var sce = rer.sys.weChatLogin(memberSceneId).get()
        if (sce == null) {
            return ApiResult.error("二维码过期")
        }
        if (sce.wxOpenId.isEmpty()) {
            return ApiResult.error("请重新扫码关注")
        }
        // 检测手机号码是否存在、状态是否正常，避免短信浪费
        // 判断用户状态是否正常
        if (!ValidateUtils.checkPhoneNumber(phone)) {
            return ApiResult.error("请输入正确的手机号")
        }
        val user = mor.tenant.tenantLoginUser.query()
            .where { it.tenant.id match sce.tenantId }
            .where { it.mobile match phone }
            .toEntity()

        if (null == user) {
            return ApiResult.error("该手机号的用户不存在")
        }
        //查用户是否有该应用登录的权限
        val allowApps = queryUserAllowApps(user.userId, sce.iamAppCode, sce.tenantId)
        var thisApp = allowApps.filter { it.code == sce.iamAppCode }
        if (thisApp.size == 0) {
            return ApiResult.error("您没有登录该系统的权限")
        }

        mpClient.sendSmsCode(MobileCodeModuleEnum.Login, phone)
            .apply {
                return if (this.code == 0) ApiResult() else ApiResult.error(this.msg.toString())
            }
    }

    /**
     * @Description 绑定手机号 后续进行登录
     *
     * @param
     * @return
     * @date 16:44 2022/2/10
     */
    @PostMapping("/bindPhone")
    fun bindPhone(
        @Require phone: String,
        @Require memberSceneId: String,
        @Require validateCode: String,
        request: HttpServletRequest
    ): ApiResult<CodeModel> {
        // 检测手机号码是否存在、状态是否正常，避免短信浪费
        // 判断用户状态是否正常
        var sce = rer.sys.weChatLogin(memberSceneId).get()
        if(sce==null){
            return ApiResult.error("二维码过期，请刷新二维码")
        }
        if (!ValidateUtils.checkPhoneNumber(phone)) {
            return ApiResult.error("请输入正确的手机号")
        }
        val user = mor.tenant.tenantLoginUser.query()
            .where { it.tenant.id match sce!!.tenantId }
            .where { it.mobile match phone }
            .toEntity()
        if (user == null) {
            return ApiResult.error("该手机号的用户不存在")
        }
        if (phone.isBlank()) {
            return ApiResult.error("请输入手机号")
        }
        val codeStatus = mpClient.codeStatus(MobileCodeModuleEnum.Login, phone, validateCode)
        if (codeStatus.code != 0) {
            return ApiResult.error("验证码校验错误，请重试", 500)
        }

        if (sce == null) {
            return ApiResult.error("二维码过期")
        }
        if (sce.wxOpenId.isEmpty()) {
            return ApiResult.error("请重新扫码关注")
        }
        //验证码通过   进行绑定操作
        sce.user.userId = user.userId
        mor.tenant.tenantWeChatLoginUser.doInsert(sce.user)
        if(dbr.affectRowCount==0){
            return ApiResult.error("绑定失败请重试")
        }
        //更新redis
        rer.sys.weChatLogin(memberSceneId).set(sce)
        val appInfo = mor.iam.sysApplication.queryByAppCode(sce.iamAppCode).toEntity()
        //绑定完成后进行登录
        request.userAuthenticationService.deleteToken(request)
        var codeModel =
            tenantUserService.loginTenant(user.loginName, "", true, appInfo!!,request.getHeader("lang").toString()).data?.ConvertJson(CodeModel::class.java)
        return ApiResult.of(codeModel)
    }

    /**
     * xml转为map
     * 得到xml根元素
     * @param httpServletRequest
     * @return
     */
    fun xmlToMap(httpServletRequest: HttpServletRequest): Map<String, String> {
        val map: MutableMap<String, String> = HashMap()
        try {
            val inputStream: InputStream = httpServletRequest.inputStream
            val reader = SAXReader()
            val document = reader.read(inputStream)
            val root = document.rootElement
            // 得到根元素的所有子节点
            val elementList = root.elements()
            for (e in elementList) {
                map[e.name] = e.text
            }
            inputStream.close()
            return map
        } catch (e: Exception) {
            e.message
        }
        return mapOf()
    }


    /**
     * 会员关注公众号操作
     *
     * @param sceneStr 场景ID
     * @param openId   会员唯一码
     */
    fun memberAttention(
        sceneStr: String,
        openId: String,
        request: HttpServletRequest,
        xml:String,
        decryptData:Map<String,Any>
    ) {
        var sceneStr = sceneStr
        if (sceneStr.isEmpty()) {
            throw RuntimeException("场景值为空 openId = $openId")
            return
        }
        if (sceneStr.startsWith(SysConstants.QRSCENE)) {
            sceneStr = sceneStr.substring(SysConstants.CUT_OUT_EIGHT)
        }
        var sce = rer.sys.weChatLogin(sceneStr).get()
        if (sce == null) throw RuntimeException("二维码过期"+"解密前：{"+xml+"}解密后：{"+decryptData+"}")
        if (sce.wxOpenId.isNotEmpty()) throw RuntimeException("二维码已经被扫")


        val wxMpUser = WeChatUtils.userInfo(openId, sce!!.accessToken)
        if ("1" != wxMpUser.get("subscribe").toString()) {
            throw RuntimeException("请扫码关注")
        }

        /**
         * 1.查是否已经存在sass用户
         * 1.1 已经存在 进行登录操作
         * 1.2 不存在
         *      1.2.1 注册操作，绑定操作
         *      1.2.2 登录操作
         *
         *
         */
//TODO 数据结构是否需要更改
        val wxChatLoginUser = mor.tenant.tenantWeChatLoginUser.query()
            .where { it.wxOpenId match openId }
            .where { it.identitySourceLinkId match sce.identitySourceLinkId }
            .toEntity()

        val sysApp = mor.iam.sysApplication.query()
            .where { it.appCode match sce.iamAppCode }
            .toEntity()
        if (sysApp == null) {
            throw RuntimeException("iam不存在该应用")
        }
        val tenantApp = mor.tenant.tenantApplication.query()
            .where { it.tenant.id match sce.tenantId }
            .where { it.appCode match sce.iamAppCode }
            .toEntity()
        if (tenantApp == null) {
            throw RuntimeException("该租户没有此应用的权限")
        }

        if (wxChatLoginUser == null) {
            //不存在
            //信息存入redis 绑定成功后再入库
            val wxUser = TenantWeChatLoginUser()
            wxUser.wxOpenId = openId
            wxUser.tenant = IdName(tenantApp.tenant.id, tenantApp.tenant.name)
            wxUser.identitySourceLinkId = sce.identitySourceLinkId
            sce.user = wxUser

            if (sce == null) {
                throw RuntimeException("二维码过期，请刷新页面")
            }
            sce.wxOpenId = openId
            //将openid存入redis
            rer.sys.weChatLogin(sceneStr).set(sce)
            //后续前端轮询memberLoginInfo接口 获得是否需要登录或者是否需要绑定手机号


        } else {
            if (sce.wxOpenId.isEmpty() || sce.user.userId.isEmpty()) {
                sce.wxOpenId = openId
                sce.user = wxChatLoginUser
                rer.sys.weChatLogin(sceneStr).set(sce)
            }
            //后续前端轮询memberLoginInfo接口 获得是否需要登录或者是否需要绑定手机号
        }
    }

    /**
     * 会员取消关注公众号操作
     *
     * @param openId   会员唯一码
     */
    fun unsubscribe() {
    }


    /**
     * @Description 查某个用户拥有的所有应用登录权限
     *
     * @param userId 用户id
     * @param appCode 应用code
     * @param tenantId 租户id
     * @return MutableList<CodeName>
     * @date 13:27 2022/2/14
     */
    fun queryUserAllowApps(userId: String, appCode: String, tenantId: String): MutableList<CodeName> {
        val allowApps: MutableList<CodeName> = mutableListOf()
        //查人
        val tenantUser = mor.tenant.tenantUser.query()
            .where { it.tenant.id match tenantId }
            .where { it.id match userId }
            .toEntity()
        if (null == tenantUser) {
            throw RuntimeException("该手机号的用户不存在")
        }
        allowApps.addAll(tenantUser.allowApps)
        //查用户组
        val groupIds = tenantUser.groups.map { it.id }
        val tenantUserGroups = mor.tenant.tenantUserGroup.query()
            .where { it.tenant.id match tenantId }
            .where { it.id match_in groupIds }
            .toList()
        tenantUserGroups.forEach {
            allowApps.addAll(it.allowApps)
        }
        //查部门
        val deportmentIds = tenantUser.depts.map { it.id }
        val deportments = mor.tenant.tenantDepartmentInfo.query()
            .where { it.tenant.id match tenantId }
            .where { it.id match_in deportmentIds }
            .toList()
        deportments.forEach {
            allowApps.addAll(it.allowApps)
        }
        //查全部登录的
        mor.tenant.tenantApplication.query()
            .where { it.tenant.id match tenantId }
            .where { it.enabled match true }
            .where { it.isOpen match true }
            .toList()
            .apply {
                this.forEach {
                    allowApps.add(CodeName(it.appCode, it.name))
                }
            }
        return allowApps.distinctBy { it.code }.toMutableList()
    }

    /**
     * @Description 获取临时二维码时校验参数
     *
     * @param identitySourceLinkId 唯一标识
     * @return
     * @date 14:09 2022/2/23
     */
    fun checkQRCodeParams(identitySourceLinkId: String, tenantId: String, code: String): String {
        mor.tenant.tenant.query()
            .where { it.id match tenantId }
            .exists()
            .apply {
                if(!this) return "租户不存在：${tenantId}"
            }

        //应用是否存在
        mor.iam.sysApplication.query()
            .where { it.appCode match code }
            .exists()
            .apply {
                if(!this) return "该应用不存在：{$code}"
            }
        //应用是否在租户下
        mor.tenant.tenantApplication.query()
            .where { it.tenant.id match tenantId }
            .where { it.appCode match code }
            .exists()
            .apply {
                if (!this) return "您没有登录该应用的权限"
            }
        //查该应用是否开启此种登录方式
        val config = mor.tenant.socialIdentitySourceConfig.query()
            .where { it.tenant.id match tenantId }
            .where { it.identitySourceLinkId match identitySourceLinkId }
            .toEntity()
        if (config == null) {
            return "社会化身份源唯一标识不正确或未配置"
        }
        //查应用是否被管理员停用
        config.tenantApps.filter { it.codeName.code == code }
            .first()
            .ConvertJson(TenantIdentitySourceApp::class.java)
            .apply {
                if (!this.tenantAppStatus || !this.status) {
                    return "应用被停用或未开启微信公众号扫码登录"
                }
            }
        return ""
    }


}