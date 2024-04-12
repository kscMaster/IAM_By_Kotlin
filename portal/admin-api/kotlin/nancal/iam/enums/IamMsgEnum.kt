package nancal.iam.enums

import nancal.iam.enums.IamMsgEnum.Companion.getDescByCode

/**
 *@Author shyf
 * @Date 2022/06/13
 **/
enum class IamMsgEnum(val key:String, val cn: String, val en:String) {
    MSG_01("iam_admin_001","redis系统错误", "redis system error"),
    MSG_02("iam_admin_002","redis数据连接失败", "redis data connection failure"),
    MSG_03("iam_admin_003","redis流水线执行错误", "redis pipeline execution error"),
    MSG_04("iam_admin_004","sql执行错误", "sql execution error"),
    MSG_05("iam_admin_005","唯一键冲突", "unique key conflict"),
    MSG_06("iam_admin_006","数据访问错误", "data access error"),
    MSG_07("iam_admin_007","mongo执行错误", "mongo execution error"),
    MSG_08("iam_admin_008","RSA解密错误", "RSA decryption error"),
    MSG_09("iam_admin_009","系统错误", "system error"),
    MSG_10("iam_admin_010","您需要登录", "You need to login."),
    MSG_11("iam_admin_011","找不到数据", "data not found"),
    MSG_12("iam_admin_012","更新失败", "update failed"),
    MSG_13("iam_admin_013","添加失败", "add failed"),
    MSG_14("iam_admin_014","删除失败", "delete failed"),
    MSG_15("iam_admin_015","找不到用户信息", "user information not found"),
    MSG_16("iam_admin_016","新密码不能与旧密码一致", "The new password cannot be the same as the current password."),
    MSG_17("iam_admin_017","未查询到管理员", "no Administrator found"),
    MSG_18("iam_admin_018","未查询到用户数据", "no user data found"),
    MSG_19("iam_admin_019","修改失败", "change failed"),
    MSG_20("iam_admin_020","行业名称不能为空", "Industry name cannot be null."),
    MSG_21("iam_admin_021","行业名称不能超过32个字符", "Industry name cannot exceed 32 characters."),
    MSG_22("iam_admin_022","行业名称code不能为空", "Industry name code cannot be null."),
    MSG_23("iam_admin_023","行业名称code不能超过32个字符", "Industry name code cannot exceed 32 characters."),
    MSG_24("iam_admin_023","行业code已存在，请重新填写", "Industry code already exists. Please re-enter"),
    MSG_25("iam_admin_025","appCode不能为空", "AppCode cannot be null."),
    MSG_26("iam_admin_026","授权主体不能为空", "Authorization subject cannot be null"),
    MSG_27("iam_admin_027","授权资源不能为空", "Authorization resource cannot be null"),
    MSG_28("iam_admin_028","找不到应用", "app not found"),
    MSG_29("iam_admin_029","找不到角色", "role not found"),
    MSG_30("iam_admin_030","id不能为空", "id cannot be null"),
    MSG_31("iam_admin_031","找不到授权数据", "authorization data not found"),
    MSG_32("iam_admin_32","应用不存在", "App doesn't exist"),
    MSG_33("iam_admin_033","应用已启用", "The app has been enabled"),
    MSG_34("iam_admin_034","应用已停用", "The app has been disabled"),
    MSG_35("iam_admin_035","数据不存在", "Data doesn't exist."),
    MSG_36("iam_admin_036","新增失败", "add failed"),
    MSG_37("iam_admin_037","appCode长度不能超过32", "The length of appCode cannot exceed 32"),
    MSG_38("iam_admin_038","应用名称不能为空", "App name cannot be null."),
    MSG_39("iam_admin_039","名称长度不能超过32", "The length of name cannot exceed 32"),
    MSG_40("iam_admin_040","备注长度不能超过255", "The length of note cannot exceed 255"),
    MSG_41("iam_admin_041","appCode不可更改", "AppCode cannot be changed."),
    MSG_42("iam_admin_042","appCode不能重复", "AppCode cannot be duplicated."),
    MSG_43("iam_admin_043","appCode已存在", "AppCode already exists."),
    MSG_44("iam_admin_044","code不能是空", "Code cannot be null."),
    MSG_45("iam_admin_045","名称不能大于300", "Name cannot exceed 300."),
    MSG_46("iam_admin_046","code长度不能大于120", "The length of code cannot exceed 120."),
    MSG_47("iam_admin_047","code参数存在非法字符，请核对", "Invalid characters in code parameter. Please check."),
    MSG_48("iam_admin_048","操作参数存在非法字符，请核对", "Invalid characters in operation parameter. Please check."),
    MSG_49("iam_admin_049","操作类型不能大于120", "Operation type cannot exceed 120."),

    MSG_50("iam_admin_050","访问级别必须为空", "Access class must be null."),
    MSG_51("iam_admin_051","API地址长度不能大于300", "The length of API address cannot exceed 300."),
    MSG_52("iam_admin_052","资源code已存在", "Resource code already exists."),
    MSG_53("iam_admin_053","子资源的资源类型必须是数据", "The resource type of subresource must be data."),
    MSG_54("iam_admin_054","资源code在已授权应用的租户中已存在", "Resource code already exists in tenant with authorized app. "),
    MSG_55("iam_admin_055","资源不存在", "Resource doesn't exist."),
    MSG_56("iam_admin_056","资源code不可编辑", "Resource code is uneditable."),
    MSG_57("iam_admin_057","父资源不存在", "Parent reosurce doesn't exist."),
    MSG_58("iam_admin_058","找不到资源数据", "resource data not found"),
    MSG_59("iam_admin_059","备注长度不能大于255", "The length of note cannot exceed 255."),
    MSG_60("iam_admin_060","应用名称长度不能大于32", "The length of app name cannot exceed 32."),
    MSG_61("iam_admin_061","找不到角色", "role not found"),
    MSG_62("iam_admin_062","应用与角色不匹配", "App does not match role."),

    MSG_63("iam_admin_063","角色code已存在", "Role code already exists."),
    MSG_64("iam_admin_064","角色code在已授权应用的租户中已存在", "Role code already exists in tenant with authorized app. "),
    MSG_65("iam_admin_065","应用不存在", "App doesn't exist."),
    MSG_66("iam_admin_066","找不到角色数据", "role data not found"),
    MSG_67("iam_admin_067","找不到appCode", "appCode not found"),
    MSG_68("iam_admin_068","找不到租户", "tenant not found"),
    MSG_69("iam_admin_069","失败信息", "failure information"),
    MSG_70("iam_admin_070","租户ID不能为空", "Tenant ID cannot be null."),
    MSG_71("iam_admin_071","租户不存在", "Tenant doesn't exist."),
    MSG_72("iam_admin_072","保存失败,至少需要保留一个管理员", "Save failed. At least 1 Administrator is required. "),
    MSG_73("iam_admin_073","保存失败", "save failed"),
    MSG_74("iam_admin_074","新增loginAdminUser失败", "add loginAdminUser failed"),
    MSG_75("iam_admin_075","修改登录信息失败", "change login information failed"),
    MSG_76("iam_admin_076","修改登录信息邮箱失败", "change login information email failed"),
    MSG_77("iam_admin_077","loginName不能是手机号格式", "LoginName cannot be in phone number format."),
    MSG_78("iam_admin_078","loginName不能是邮箱格式", "LoginName cannot be in email format."),
    MSG_79("iam_admin_079","请填写管理员用户名", "Please enter Administrator user name."),
    MSG_80("iam_admin_080","管理员用户名至少6个字符", "Administrator user name contains at least 6 characters."),
    MSG_81("iam_admin_081","管理员用户名不能超过32个字符", "Admnistrator user name cannot exceed 32 characters."),
    MSG_82("iam_admin_082","备注不能超过255个字符", "Note cannot exceed 255 characters."),
    MSG_83("iam_admin_083","请填写管理员姓名", "Please enter Administrator name."),
    MSG_84("iam_admin_084","管理员姓名至少2个字符", "Administrator name contains at least 2 characters."),
    MSG_85("iam_admin_085","管理员姓名不能超过32个字符", "Admnistrator name cannot exceed 32 characters."),
    MSG_86("iam_admin_086","登录名已存在", "Login name already exists."),
    MSG_87("iam_admin_087","请选择密码发送方式", "Please select password sending method."),
    MSG_88("iam_admin_088","请填写邮箱", "Please enter email."),
    MSG_89("iam_admin_089","邮箱格式不正确", "Invalid email format."),
    MSG_90("iam_admin_090","邮箱已存在", "Email already exists."),
    MSG_91("iam_admin_091","手机号不能为空", "Phone number cannot be null."),
    MSG_92("iam_admin_092","手机格式不正确", "Invalid phone number format."),
    MSG_93("iam_admin_093","手机号已存在", "Phone number already exists."),
    MSG_94("iam_admin_094","请选择删除数据", "Please select delete data."),
    MSG_95("iam_admin_095","未查询到要删除的数据", "Unable to find data to be deleted."),

    MSG_96("iam_admin_096","至少需要保留一个管理员", "At least 1 Administrator is required."),
    MSG_97("iam_admin_097","管理员未找到", "Administrator not found"),
    MSG_98("iam_admin_098","密码包含非法字符", "Invalid characters in password."),
    MSG_99("iam_admin_099","原密码不正确", "The original password is incorrect."),
    MSG_100("iam_admin_100","用户未找到", "user not found"),
    MSG_101("iam_admin_101","密码包含非法字符", "Invalid characters in password."),
    MSG_102("iam_admin_102","找不到该租户", "tenant not found"),
    MSG_103("iam_admin_103","您的租户已被冻结", "Your tenant has been suspended."),
    MSG_104("iam_admin_104","用户不存在", "User doesn't exist."),
    MSG_105("iam_admin_105","该用户未指定发送密码类型", "The user doesn't specify a password sending method."),
    MSG_106("iam_admin_106","未查询到移动对象", "no moving object found"),
    MSG_107("iam_admin_107","移动失败", "move failed"),
    MSG_108("iam_admin_108","置顶失败", "Pin failed"),
    MSG_109("iam_admin_109","移动类型不合法", "Invalid move type"),
    MSG_110("iam_admin_110","不可以操作非管理员", "unable to operate non-Administrator"),
    MSG_111("iam_admin_111","用户已是启用状态", "The user is enabled."),
    MSG_112("iam_admin_112","用户已是停用状态", "The user is disabled."),
    MSG_113("iam_admin_113","修改头像失败", "change avatar failed"),
    MSG_114("iam_admin_114","页码和页条数未传或不正确", "Page and number of items per page are not transmitted or incorrect."),
    MSG_115("iam_admin_115","找不到管理员", "Administrator not found"),

    MSG_116("iam_admin_116","租户管理员不能为空", "Tenant Administrator cannot be null."),
    MSG_117("iam_admin_117","更新管理员失败", "update Administrator failed"),
    MSG_118("iam_admin_118","角色选择错误", "role selection error"),
    MSG_119("iam_admin_119","添加管理员失败", "add Administrator failed"),
    MSG_120("iam_admin_120","操作失败", "operation failed"),
    MSG_121("iam_admin_121","请传应用code", "Please transmit app code."),
    MSG_122("iam_admin_122","授权失败", "authorization failed"),
    MSG_123("iam_admin_123","移除失败，租户或应用不存在或应用不在租户下", "remove failed. Tenant or app doesn't exist, or the app doesn't exist in tenant."),
    MSG_124("iam_admin_124","请填写租户名称", "Please enter tenant name."),
    MSG_125("iam_admin_125","租户名称至少2个字符", "Tenant name contains at least 2 characters."),
    MSG_126("iam_admin_126","租户名称最多32个字符", "Tenant name contains at most 32 characters."),
    MSG_127("iam_admin_127","租户email地址无效", "Invalid tenant email address."),
    MSG_128("iam_admin_128","租户手机号码格式不正确", "Contact's phone number format is incorrect."),
    MSG_129("iam_admin_129","联系人姓名最多32个字符", "Contact's name contains at most 32 characters."),
    MSG_130("iam_admin_130","联系地址不能超过255个字符", "Contact address cannot exceed 255 characters."),
    MSG_131("iam_admin_131","备注不能超过255个字符", "Note cannot exceed 255 characters."),
    MSG_132("iam_admin_132","租户重复，请重新填写租户名", "Tenant is duplicated. Please re-enter tenant name."),
    MSG_133("iam_admin_133","租户code已存在", "Tenant code already exists."),
    MSG_134("iam_admin_134","所属行业不存在", "Industry doesn't exist."),
    MSG_135("iam_admin_135","请填写邮箱或者电话", "Please enter email or phone number."),

    MSG_136("iam_admin_136","您好，xx   您的登录密码为：xxxx 。为了您账户的安全请勿将密码告知他人。", "Hello, xx, your login password is: xxxx. Please do not share your password with anyone for the sake of your account security."),
    MSG_137("iam_admin_137","【统一身份认证系统】登录密码", "[IAM] login password"),
    MSG_138("iam_admin_138","发送密码请选择手机或者请填写邮箱", "Please select phone number or enter email for sending password."),
    MSG_139("iam_admin_139","发送密码请选择邮箱或者请填写手机号", "Please select email or enter phone number for sending password."),
    MSG_140("iam_admin_140","管理员手机号码格式错误", "Administrator phone number format error"),
    MSG_141("iam_admin_141","管理员email地址无效", "Invalid Administrator email address"),
    MSG_142("iam_admin_142","用户名重复", "User name is duplicated."),
    MSG_143("iam_admin_143","管理员手机号码格式不正确", "Invalid Administrator phone number format"),
    MSG_144("iam_admin_144","超级管理员id不能为空", "Super Adminsitrator id cannot be null."),
    MSG_145("iam_admin_145","找不到超级管理员", "Super Adminsitrator not found"),
    MSG_146("iam_admin_146","code不唯一", "Code is not unique."),
    MSG_147("iam_admin_147","租户已有成员数据，不可以修改为LDAP协议", "Tenant has member data and cannot be changed into LDAP protocol."),
    MSG_148("iam_admin_148","租户邮箱后缀已被使用", "The emial suffix has been used."),
    MSG_149("iam_admin_149","租户设置更新失败", "Tenant settings update failed."),
    MSG_150("iam_admin_150","租户已有成员数据，不可以重置协议", "Tenant has member data and you cannot reset protocol."),


    MSG_151("iam_admin_151","租户名称不符", ""),
    MSG_152("iam_admin_152","【能科瑞元】登录密码", ""),
    MSG_153("iam_admin_153","【xxxx】个租户设置需要迁移，【xxx】个租户设置迁移成功 【失败信息】 xxx", ""),
    MSG_154("iam_admin_154","找不到数据库连接", ""),
    MSG_155("iam_admin_155","不识别的类型", ""),
    MSG_156("iam_admin_156","找不到组名", ""),
    MSG_157("iam_admin_157","找不到 xxx 实体名", ""),
    MSG_158("iam_admin_158","不识别的数据库类型", ""),

    MSG_159("iam_admin_159","请输入验证码", ""),
    MSG_160("iam_admin_160","验证码输入错误或已过期，请重试", ""),
    MSG_161("iam_admin_161","该手机号已被使用,请更换手机号后重试", ""),
    MSG_162("iam_admin_162","插入失败", ""),
    MSG_163("iam_admin_163","重置用户密码失败", ""),
    MSG_164("iam_admin_164","app xxx 私钥为空", ""),
    MSG_165("iam_admin_165","api-token非法", ""),
    MSG_166("iam_admin_166","API资源下不能创建子资源", ""),
    MSG_167("iam_admin_167","该资源下有子资源时不可修改数据类型", ""),
    MSG_168("iam_admin_168","资源Code不可更改", "");



    companion object {
        fun getDescByCode(key:String, cn: String) : String? {
            val filter: List<IamMsgEnum> = IamMsgEnum.values().filter {
                it.key == key
            }
            return when (cn) {
                "cn" -> {
                    filter[0].cn
                }
                "en" -> {
                    filter[0].en
                }
                else -> {
                    ""
                }
            }
        }
    }

}

fun main() {
    val descByCode = getDescByCode("iam_admin_044", "en")
    println(descByCode)
}