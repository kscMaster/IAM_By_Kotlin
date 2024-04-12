package nancal.iam.db.mongo.entity


import nbcp.db.*
import nancal.iam.db.mongo.*
import org.springframework.validation.annotation.Validated
import java.io.Serializable

/**
 * Created by udi on 17-6-10.
 */


open class MenuDefine(
    var id: String = "",
    @Cn("菜单名称")
    var name: String = "",
    @Cn("菜单链接")
    var url: String = "",
    @Cn("class")
    var css: String = "",
    @Cn("资源编码")
    var code: String = "",
    @Cn("排序")
    var sort: Float = 0F,
    var parent: IdName = IdName()
) : ITreeTable, Serializable {
    override fun getParentId(): String {
        return this.parent.id;
    }
}

/**
 * resource=*,action=* 表示所有
 */
open class ResourceBaseInfo(
        var type: ResourceTypeEnum? = null,
        var name: String = "",  //中文名称
        var code: String = "",
        var resource: String = "", //仅在Api时，定义为Url，其它类型 = name
        var action: MutableList<String> = mutableListOf(),
        var dataAccessLevel:AccessLevelEnum?=null //在数据资源下有访问级别  其他类型资源的时候默认为空
) : Serializable


/**
 * 租户安全设置
 */
data class TenantSetting(
    @Cn("认证协议")
    var protocol: ProtocolEnum = ProtocolEnum.Self,
    @Cn("会话超时策略")
    var sessionTimeout: Int = 1,
    @Cn("会话超时单位")
    var sessionUnit: SettingEnum = SettingEnum.Hour,
    @Cn("配置")
    var selfSetting: SelfSetting = SelfSetting(),
    @Cn("LDAP配置")
    var ldapSetting: LdapSetting? = null,
    @Cn("OAuth配置")
    var oauthSetting: Any? = null,
    @Cn("SAML配置")
    var samlSetting: Any? = null,

    ) {
    val sessionTimeoutSeconds: Int
        get() {
            if (sessionUnit == SettingEnum.Minute) {
                return sessionTimeout * 60;
            } else if (sessionUnit == SettingEnum.Hour) {
                return sessionTimeout * 3600;
            }
            throw RuntimeException("不识别")
        }
}

class LdapSetting(
    @Cn("地址")
    var utls: String = "",
    @Cn("base dn")
    var base: String = "",
    @Cn("用户名")
    var username: String = "",
    @Cn("密码")
    var password: String = "",
    /*全局不能重复*/
    @Cn("邮箱后缀")
    var mailSuffix: String = "",
)

class SelfSetting(
    @Cn("登录认证")
    var loginChecking: LoginChecking = LoginChecking(),
    @Cn("密码策略")
    var securityPolicy: SecurityPolicy = SecurityPolicy()
)

class LoginChecking(
    @Cn("登录验证统计周期")
    var checkingPeriod: Int = 15,
    @Cn("允许登录失败尝试次数")
    var retryTime: Int = 5,
    @Cn("账户被锁定时持续时间")
    var lockDuration: Int = 15,
    @Cn("手动解锁账户")
    var manual: Boolean = false,
    @Cn("账户锁定策略说明")
    var accountPolicyDescription: String = "",
)

data class SecurityPolicy(
    @Cn("密码最少字符")
    var leastCharacters: Int = 3,

    @Cn("小写字符")
    var lowInput : Boolean = true,

    @Cn("大写字符")
    var upInput : Boolean = true,

    @Cn("特殊字符")
    var specialInput : Boolean = false,

    @Cn("数字字符")
    var numberInput : Boolean = true,

    @Cn("最短长度")
    var leastLenght: Int = 6,
    @Cn("强制密码过期")
    var expires: Boolean = false,

    @Cn("首次登录是否修改密码")
    var firstLoginUpdatePassword: Boolean = true,

    @Cn("过期天数")
    var expiresDays: Int = 90,
    @Cn("到期通知")
    var expiresNotice: Int = 15,
    @Cn("密码策略说明")
    var secretPolicyDescription: String = "",
)

/**
 * 扩展字段定义
 */
open class ExtendFieldDefine constructor(
    var tenant: IdName = IdName(),

    @Cn("字段code")
    var code: String = "",

    @Cn("字段name")
    var name: String = "",

    @Cn("字段类型")
    var fieldType: FormFieldTypeEnum = FormFieldTypeEnum.Text,

    @Cn("备注")
    var remark: String = "",

    @Cn("字典项")
    var dataSource: String = "",
) : BaseEntity()


class MongoCredentialItemData(
    var iterationCount: Int = 0,
    var salt: String = "",
    var storedKey: String = "",
    var serverKey: String = ""
) : Serializable

class MongoCredentialsData(
    @DbName("SCRAM-SHA-1")
    var sha1: MongoCredentialItemData = MongoCredentialItemData(),

    @DbName("SCRAM-SHA-256")
    var sha256: MongoCredentialItemData = MongoCredentialItemData()
) : Serializable

class MongoRoleData(
    var db: String = "",
    var role: String = ""
) : Serializable

/**
 * @Description 外接身份源登录的应用
 *
 * @date 18:32 2022/2/18
 */
class TenantIdentitySourceApp(
    //租户侧应用id
    var id : String = "",
    //admin侧应用id
    var sysAppId: String = "",
    var codeName: CodeName = CodeName(),
    var logo: IdUrl?  = IdUrl(),
    //租户自己设置的状态 true  启用  false 停用
    var status: Boolean = false,
    //admin侧应用状态 true  启用  false 停用
    var sysAppStatus:Boolean = true,
    //租户侧应用状态
    var tenantAppStatus : Boolean = true,
    //是否是系统应用 true  是  false 不是
    var isSysDefine:Boolean=false
) : Serializable

/**
 * @Description 微信扫码关注公众号登录配置
 *
 * @param
 * @return
 * @date 17:06 2022/2/22
 */
class WeChatConfig(

    var weChatAppId: String = "",
    var weChatAppSecret: String = "",
    var weChatToken: String = "",
    var aesKey: String = "",
    var messageDecryptMethod: String = "",
)