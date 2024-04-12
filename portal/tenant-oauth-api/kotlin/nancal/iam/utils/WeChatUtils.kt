package nancal.iam.utils

import cn.hutool.http.HttpUtil
import nancal.iam.db.mongo.SocialIdentitySourceTypeEnum
import nancal.iam.db.mongo.WeChatLoginTypeEnum
import nancal.iam.db.mongo.entity.WeChatConfig
import nancal.iam.db.mongo.mor
import nancal.iam.weixinaes.AesException
import nbcp.comm.*
import nbcp.db.mongo.match
import nbcp.db.mongo.query
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.StringUtils
import org.dom4j.Document
import org.dom4j.DocumentException
import org.dom4j.DocumentHelper
import org.dom4j.Element
import org.dom4j.io.SAXReader
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URLEncoder
import java.net.URLEncoder.encode
import java.util.*
import java.util.function.Consumer
import javax.servlet.http.HttpServletRequest


/**
 * @Author wrk
 *
 * @Description   微信登录工具类
 * @Date 2022/2/17-10:48
 */
class WeChatUtils {


    class AccessToken(
        val accessToken: String = "",
        val expiresIn: Long = 0L
    )



    companion object {
        val API_DEFAULT_HOST_URL = "https://api.weixin.qq.com"
        val MP_DEFAULT_HOST_URL = "https://mp.weixin.qq.com"

        val USER_INFO_URL = "/cgi-bin/user/info"
        val ACCESS_TOKEN_URL = "/cgi-bin/token"
        val TICKET_URL = "/cgi-bin/qrcode/create"
        val SHOW_QRCODE_URL = "/cgi-bin/showqrcode"

        val GET_ACCESSTOKEN = "client_credential"

        /**
         * @Description
         * 获取accessToken  access_token的有效期目前为2个小时，需定时刷新，重复获取将导致上次获取的access_token失效。
         * 目前access_token的有效期通过返回的expire_in来传达，目前是7200秒之内的值。
         * @param tenantId 租户id
         * @param identitySourceLinkId 乐仓统一身份份认证系统用户设置的唯一标识
         * @return
         * @date 10:49 2022/2/17
         */
        fun accessToken(tenantId: String, identitySourceLinkId: String): AccessToken {
            val tenantWeChatConfig = mor.tenant.socialIdentitySourceConfig.query()
                .where { it.tenant.id match tenantId }
                .where { it.socialType match SocialIdentitySourceTypeEnum.weixin.name }
                .where { it.loginType match SocialIdentitySourceTypeEnum.weixin.name + ":" + WeChatLoginTypeEnum.WeChatOfficialAccount.name }
                .where { it.identitySourceLinkId match identitySourceLinkId }
                .toEntity()
            if (tenantWeChatConfig == null) {
                throw RuntimeException("无配置")
            }
            val config = tenantWeChatConfig.settings.ConvertJson(WeChatConfig::class.java)
            val params: MutableMap<String, Any> = HashMap()
            params["appid"] = config.weChatAppId
            params["secret"] = config.weChatAppSecret
            params["grant_type"] = GET_ACCESSTOKEN
            val accessTokenObject = getFromWeChat(API_DEFAULT_HOST_URL + ACCESS_TOKEN_URL, params.toMap())
            val accessToken: String = accessTokenObject.get("access_token").toString()
            val expire: Long = accessTokenObject.get("expires_in") as Long
            return AccessToken(accessToken, expire)
        }


        /**
         * @Description 获取临时二维码
         *
         * @param accessToken 微信提供的 accessToken
         * @param expire 过期时间 秒
         * @param sceneStr 场景id
         * @return
         * @date 10:49 2022/2/17
         */
        fun qrCodeCreateTmpTicket(accessToken: String, expire: Long, sceneStr: String): String {
            var qrcodeUrl = ""
            val jsonObject = JsonMap()
            jsonObject["expire_seconds"] = expire
            jsonObject["action_name"] = "QR_STR_SCENE"
            val actionInfo = JsonMap()
            val scene = JsonMap()
            scene["scene_str"] = sceneStr
            actionInfo["scene"] = scene
            jsonObject["action_info"] = actionInfo
            val url = API_DEFAULT_HOST_URL + TICKET_URL + "?access_token=${accessToken}"
            val resultJson = postToWeChat(url, jsonObject.toJsonString())
            val ticket = resultJson.get("ticket").toString()

            /**
             * 实际应用中可以根据过期时间服务器存储ticket
             */
            val expire_seconds = resultJson.get("expire_seconds")
            qrcodeUrl = MP_DEFAULT_HOST_URL + SHOW_QRCODE_URL + "?ticket=" + URLEncoder.encode(ticket, "UTF-8")

            return qrcodeUrl
        }

        /**
         * @Description 微信公众号关联验证
         *
         * @param accessToken
         * @param timestamp
         * @param nonce
         * @param signature
         * @return true 信息来自微信  false  信息不是来自微信
         * @date 11:34 2022/2/17
         */
        fun checkSignature(accessToken: String, timestamp: String, nonce: String, signature: String): Boolean {
            return gen(accessToken, timestamp, nonce)
                .equals(signature)
        }

        /**
         * @Description 获取用户信息
         *
         * @param  openId 用户微信标识
         * @param  accessToken 微信提供的accessToken
         * @return  JSONObject 用户信息
         * @date 17:54 2022/2/22
         */
        fun userInfo(openId: String, accessToken: String): JsonMap {
            val params: MutableMap<String, Any> = HashMap()
            params["access_token"] = accessToken
            params["openid"] = openId
            params["lang"] = "zh_CN"
            val jsonObj = getFromWeChat(API_DEFAULT_HOST_URL + USER_INFO_URL, params.toMap())
            return jsonObj

        }

        /**
         * 从request取 xml 并转为map
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
            } catch (e: AesException) {
                e.message
            }
            return mapOf()
        }

        /**
         * @Description 关联微信服务器校验方法
         *
         * @param
         * @return
         * @date 11:11 2022/3/1
         */
        private fun gen(vararg arr: String): String {
            return if (StringUtils.isAnyEmpty(*arr)) {
                throw IllegalArgumentException("非法请求参数，有部分参数为空 : " + Arrays.toString(arr))
            } else {
                Arrays.sort(arr)
                val sb = StringBuilder()
                val var2: Array<String> = arr as Array<String>
                val var3 = arr.size
                for (var4 in 0 until var3) {
                    val a = var2[var4]
                    sb.append(a)
                }
                DigestUtils.sha1Hex(sb.toString())
            }
        }

        /**
         * @Description 微信状态码信息
         *
         * @param
         * @return
         * @date 14:09 2022/2/24
         */
        fun weChatCodeMsg(code: Int): String {
            var msg = ""
            when (code) {
                -1 -> msg = "	系统繁忙，此时请开发者稍候再试	"
                0 -> msg = "	请求成功	"
                40001 -> msg =
                    "	获取 access_token 时 AppSecret 错误，或者 access_token 无效。请开发者认真比对 AppSecret 的正确性，或查看是否正在为恰当的公众号调用接口	"
                40002 -> msg = "	不合法的凭证类型	"
                40003 -> msg = "	不合法的 OpenID ，请开发者确认 OpenID （该用户）是否已关注公众号，或是否是其他公众号的 OpenID	"
                40004 -> msg = "	不合法的媒体文件类型	"
                40005 -> msg = "	不合法的文件类型	"
                40006 -> msg = "	不合法的文件大小	"
                40007 -> msg = "	不合法的媒体文件 id	"
                40008 -> msg = "	不合法的消息类型	"
                40009 -> msg = "	不合法的图片文件大小	"
                40010 -> msg = "	不合法的语音文件大小	"
                40011 -> msg = "	不合法的视频文件大小	"
                40012 -> msg = "	不合法的缩略图文件大小	"
                40013 -> msg = "	不合法的 AppID ，请开发者检查 AppID 的正确性，避免异常字符，注意大小写	"
                40014 -> msg = "	不合法的 access_token ，请开发者认真比对 access_token 的有效性（如是否过期），或查看是否正在为恰当的公众号调用接口	"
                40015 -> msg = "	不合法的菜单类型	"
                40016 -> msg = "	不合法的按钮个数	"
                40017 -> msg = "	不合法的按钮类型	"
                40018 -> msg = "	不合法的按钮名字长度	"
                40019 -> msg = "	不合法的按钮 KEY 长度	"
                40020 -> msg = "	不合法的按钮 URL 长度	"
                40021 -> msg = "	不合法的菜单版本号	"
                40022 -> msg = "	不合法的子菜单级数	"
                40023 -> msg = "	不合法的子菜单按钮个数	"
                40024 -> msg = "	不合法的子菜单按钮类型	"
                40025 -> msg = "	不合法的子菜单按钮名字长度	"
                40026 -> msg = "	不合法的子菜单按钮 KEY 长度	"
                40027 -> msg = "	不合法的子菜单按钮 URL 长度	"
                40028 -> msg = "	不合法的自定义菜单使用用户	"
                40029 -> msg = "	无效的 oauth_code	"
                40030 -> msg = "	不合法的 refresh_token	"
                40031 -> msg = "	不合法的 openid 列表	"
                40032 -> msg = "	不合法的 openid 列表长度	"
                40033 -> msg = "	不合法的请求字符，不能包含 \\uxxxx 格式的字符	"
                40035 -> msg = "	不合法的参数	"
                40038 -> msg = "	不合法的请求格式	"
                40039 -> msg = "	不合法的 URL 长度	"
                40048 -> msg = "	无效的url	"
                40050 -> msg = "	不合法的分组 id	"
                40051 -> msg = "	分组名字不合法	"
                40060 -> msg = "	删除单篇图文时，指定的 article_idx 不合法	"
                40117 -> msg = "	分组名字不合法	"
                40118 -> msg = "	media_id 大小不合法	"
                40119 -> msg = "	button 类型错误	"
                40120 -> msg = "	子 button 类型错误	"
                40121 -> msg = "	不合法的 media_id 类型	"
                40125 -> msg = "	无效的appsecret	"
                40132 -> msg = "	微信号不合法	"
                40137 -> msg = "	不支持的图片格式	"
                40155 -> msg = "	请勿添加其他公众号的主页链接	"
                40163 -> msg = "	oauth_code已使用	"
                41001 -> msg = "	缺少 access_token 参数	"
                41002 -> msg = "	缺少 appid 参数	"
                41003 -> msg = "	缺少 refresh_token 参数	"
                41004 -> msg = "	缺少 secret 参数	"
                41005 -> msg = "	缺少多媒体文件数据	"
                41006 -> msg = "	缺少 media_id 参数	"
                41007 -> msg = "	缺少子菜单数据	"
                41008 -> msg = "	缺少 oauth code	"
                41009 -> msg = "	缺少 openid	"
                42001 -> msg =
                    "	access_token 超时，请检查 access_token 的有效期，请参考基础支持 - 获取 access_token 中，对 access_token 的详细机制说明	"
                42002 -> msg = "	refresh_token 超时	"
                42003 -> msg = "	oauth_code 超时	"
                42007 -> msg = "	用户修改微信密码， accesstoken 和 refreshtoken 失效，需要重新授权	"
                43001 -> msg = "	需要 GET 请求	"
                43002 -> msg = "	需要 POST 请求	"
                43003 -> msg = "	需要 HTTPS 请求	"
                43004 -> msg = "	需要接收者关注	"
                43005 -> msg = "	需要好友关系	"
                43019 -> msg = "	需要将接收者从黑名单中移除	"
                44001 -> msg = "	多媒体文件为空	"
                44002 -> msg = "	POST 的数据包为空	"
                44003 -> msg = "	图文消息内容为空	"
                44004 -> msg = "	文本消息内容为空	"
                45001 -> msg = "	多媒体文件大小超过限制	"
                45002 -> msg = "	消息内容超过限制	"
                45003 -> msg = "	标题字段超过限制	"
                45004 -> msg = "	描述字段超过限制	"
                45005 -> msg = "	链接字段超过限制	"
                45006 -> msg = "	图片链接字段超过限制	"
                45007 -> msg = "	语音播放时间超过限制	"
                45008 -> msg = "	图文消息超过限制	"
                45009 -> msg = "	接口调用超过限制	"
                45010 -> msg = "	创建菜单个数超过限制	"
                45011 -> msg = "	API 调用太频繁，请稍候再试	"
                45015 -> msg = "	回复时间超过限制	"
                45016 -> msg = "	系统分组，不允许修改	"
                45017 -> msg = "	分组名字过长	"
                45018 -> msg = "	分组数量超过上限	"
                45047 -> msg = "	客服接口下行条数超过上限	"
                45064 -> msg = "	创建菜单包含未关联的小程序	"
                45065 -> msg = "	相同 clientmsgid 已存在群发记录，返回数据中带有已存在的群发任务的 msgid	"
                45066 -> msg = "	相同 clientmsgid 重试速度过快，请间隔1分钟重试	"
                45067 -> msg = "	clientmsgid 长度超过限制	"
                46001 -> msg = "	不存在媒体数据	"
                46002 -> msg = "	不存在的菜单版本	"
                46003 -> msg = "	不存在的菜单数据	"
                46004 -> msg = "	不存在的用户	"
                47001 -> msg = "	解析 JSON/XML 内容错误	"
                48001 -> msg = "	api 功能未授权，请确认公众号已获得该接口，可以在公众平台官网 - 开发者中心页中查看接口权限	"
                48002 -> msg = "	粉丝拒收消息（粉丝在公众号选项中，关闭了 “ 接收消息 ” ）	"
                48004 -> msg = "	api 接口被封禁，请登录 mp.weixin.qq.com 查看详情	"
                48005 -> msg = "	api 禁止删除被自动回复和自定义菜单引用的素材	"
                48006 -> msg = "	api 禁止清零调用次数，因为清零次数达到上限	"
                48008 -> msg = "	没有该类型消息的发送权限	"
                50001 -> msg = "	用户未授权该 api	"
                50002 -> msg = "	用户受限，可能是违规后接口被封禁	"
                50005 -> msg = "	用户未关注公众号	"
                53500 -> msg = "	发布功能被封禁	"
                53501 -> msg = "	频繁请求发布	"
                53502 -> msg = "	Publish ID 无效	"
                53600 -> msg = "	Article ID 无效	"
                61451 -> msg = "	参数错误 (invalid parameter)	"
                61452 -> msg = "	无效客服账号 (invalid kf_account)	"
                61453 -> msg = "	客服帐号已存在 (kf_account exsited)	"
                61454 -> msg = "	客服帐号名长度超过限制 ( 仅允许 10 个英文字符，不包括 @ 及 @ 后的公众号的微信号 )(invalid   kf_acount length)	"
                61455 -> msg = "	客服帐号名包含非法字符 ( 仅允许英文 + 数字 )(illegal character in     kf_account)	"
                61456 -> msg = "	客服帐号个数超过限制 (10 个客服账号 )(kf_account count exceeded)	"
                61457 -> msg = "	无效头像文件类型 (invalid   file type)	"
                61450 -> msg = "	系统错误 (system error)	"
                61500 -> msg = "	日期格式错误	"
                63001 -> msg = "	部分参数为空	"
                63002 -> msg = "	无效的签名	"
                65301 -> msg = "	不存在此 menuid 对应的个性化菜单	"
                65302 -> msg = "	没有相应的用户	"
                65303 -> msg = "	没有默认菜单，不能创建个性化菜单	"
                65304 -> msg = "	MatchRule 信息为空	"
                65305 -> msg = "	个性化菜单数量受限	"
                65306 -> msg = "	不支持个性化菜单的帐号	"
                65307 -> msg = "	个性化菜单信息为空	"
                65308 -> msg = "	包含没有响应类型的 button	"
                65309 -> msg = "	个性化菜单开关处于关闭状态	"
                65310 -> msg = "	填写了省份或城市信息，国家信息不能为空	"
                65311 -> msg = "	填写了城市信息，省份信息不能为空	"
                65312 -> msg = "	不合法的国家信息	"
                65313 -> msg = "	不合法的省份信息	"
                65314 -> msg = "	不合法的城市信息	"
                65316 -> msg = "	该公众号的菜单设置了过多的域名外跳（最多跳转到 3 个域名的链接）	"
                65317 -> msg = "	不合法的 URL	"
                87009 -> msg = "	无效的签名	"
                9001001 -> msg = "	POST 数据参数不合法	"
                9001002 -> msg = "	远端服务不可用	"
                9001003 -> msg = "	Ticket 不合法	"
                9001004 -> msg = "	获取摇周边用户信息失败	"
                9001005 -> msg = "	获取商户信息失败	"
                9001006 -> msg = "	获取 OpenID 失败	"
                9001007 -> msg = "	上传文件缺失	"
                9001008 -> msg = "	上传素材的文件类型不合法	"
                9001009 -> msg = "	上传素材的文件尺寸不合法	"
                9001010 -> msg = "	上传失败	"
                9001020 -> msg = "	帐号不合法	"
                9001021 -> msg = "	已有设备激活率低于 50% ，不能新增设备	"
                9001022 -> msg = "	设备申请数不合法，必须为大于 0 的数字	"
                9001023 -> msg = "	已存在审核中的设备 ID 申请	"
                9001024 -> msg = "	一次查询设备 ID 数量不能超过 50	"
                9001025 -> msg = "	设备 ID 不合法	"
                9001026 -> msg = "	页面 ID 不合法	"
                9001027 -> msg = "	页面参数不合法	"
                9001028 -> msg = "	一次删除页面 ID 数量不能超过 10	"
                9001029 -> msg = "	页面已应用在设备中，请先解除应用关系再删除	"
                9001030 -> msg = "	一次查询页面 ID 数量不能超过 50	"
                9001031 -> msg = "	时间区间不合法	"
                9001032 -> msg = "	保存设备与页面的绑定关系参数错误	"
                9001033 -> msg = "	门店 ID 不合法	"
                9001034 -> msg = "	设备备注信息过长	"
                9001035 -> msg = "	设备申请参数不合法	"
                9001036 -> msg = "	查询起始值 begin 不合法	"
                else -> msg = ""
            }
            return msg.trim()
        }

        /**
         * @Description 对微信post请求
         *
         * @param
         * @return
         * @date 11:10 2022/3/1
         */
        fun postToWeChat(url: String, body: String): JsonMap {
            val resultJson = HttpUtil.post(url, body).FromJson(JsonMap::class.java)
            if (resultJson?.get("errcode").toString().isNotEmpty()) {
                val code = resultJson?.get("errcode").toString().AsInt()
                if (code != 0) throw  RuntimeException(weChatCodeMsg(code))
            }
            return resultJson!!
        }

        /**
         * @Description 对微信get请求
         *
         * @param
         * @return
         * @date 11:11 2022/3/1
         */

        fun getFromWeChat(urlString: String, paramMap: Map<String, Any>): JsonMap {
            val resultJson = HttpUtil.get(urlString, paramMap).FromJson(JsonMap::class.java)
            if (resultJson?.get("errcode").toString().isNotEmpty()) {
                val code = resultJson?.get("errcode").toString().AsInt()
                if (code != 0) throw  RuntimeException(weChatCodeMsg(code))
            }
            return resultJson!!
        }

        /**
         * @Description 从request中取出xml数据
         *
         * @param
         * @return
         * @date 11:10 2022/3/1
         */

        @Throws(IOException::class)
        fun streamToString(request: HttpServletRequest): String {
            val reader = BufferedReader(InputStreamReader(request.inputStream))
            val sb = StringBuilder()
            var line: String?
            try {
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return sb.toString()
        }

        /**
         * (多层)xml格式字符串转换为map
         *
         * @param xml xml字符串
         * @return 第一个为Root节点，Root节点之后为Root的元素，如果为多层，可以通过key获取下一层Map
         */
        fun multilayerXmlToMap(xml: String): Map<String, Any> {
            var doc: Document? = null
            try {
                doc = DocumentHelper.parseText(xml)
            } catch (e: DocumentException) {
              throw RuntimeException("解析数据失败")
            }
            val map: MutableMap<String, Any> = HashMap()
            if (null == doc) {
                return map
            }
            // 获取根元素
            val rootElement: Element = doc.getRootElement()
            recursionXmlToMap(rootElement, map)
            return map
        }

        /**
         * multilayerXmlToMap核心方法，递归调用
         *
         * @param element 节点元素
         * @param outmap 用于存储xml数据的map
         */
        private fun recursionXmlToMap(element: Element, outmap: MutableMap<String, Any>) {
            // 得到根元素下的子元素列表
            val list: List<Element> = element.elements()
            val size = list.size
            if (size == 0) {
                // 如果没有子元素,则将其存储进map中
                outmap[element.getName()] = element.getTextTrim()
            } else {
                // innermap用于存储子元素的属性名和属性值
                val innermap: MutableMap<String, Any> = HashMap()
                // 遍历子元素
                list.forEach(Consumer<Element> { childElement: Element ->
                    recursionXmlToMap(
                        childElement,
                        innermap
                    )
                })
                outmap[element.getName()] = innermap
            }
        }


    }
}