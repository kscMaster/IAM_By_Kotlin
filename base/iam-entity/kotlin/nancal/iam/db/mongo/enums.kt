package nancal.iam.db.mongo

/**
 * 用户类型
 */
enum class UserSystemTypeEnum(val remark: String) {
    TenantUser("互联网用户"),
    TenantAdmin("企业用户"),
    Boss("管理用户")
}

/**
 * 租户管理员类型
 */
enum class TenantAdminTypeEnum(val remark: String) {
    Super("超级管理员"),
    Business("安全管理员"),
    User("系统管理员"),
    Auditor("安全审计员"),
    None("非管理员")
}

enum class PersonClassifiedEnum(val remark: String) {
    None("无"),
    Core("核心"),
    Important("重要"),
    General("一般"),
    NonConfidential("非密"),
}

enum class TenantDictType(val remark: String, val type: Int) {
    PersonClassified("人员密集", 1)
}

enum class TenantMessageType(val remark: String, val type: Int) {
    TenantMail("站内信", 1),
    Mail("邮件", 2),
    APP("app推送", 3),
    Message("短信", 4)
}

enum class LoginTypeEnum(val remark: String) {
    NormalMode("常规模式")
}

enum class TenantMailType(val remark: String, val type: Int) {
    Product("产品消息", 1),
    DevOps("运维消息", 2),
    Activity("活动消息", 3),
    Service("服务消息", 4)
}

enum class AppTagTypeEnum(val remark: String) {
    LPC("生产力中台"),
    LOS("乐造OS应用管理平台"),
    RFZ("后厂造商城")
}


enum class ResourceTypeEnum(val remark: String) {
    Api("Api"),
    Menu("菜单"),
    Data("数据"),
    Ui("界面")
}

/**
 * 数据资源访问级别 无、团队，组织、父级。
 */
enum class AccessLevelEnum(val remark: String) {
    None("无"),
    Team("团队"),
    Organization("组织"),
    Father("父级")
}

/**
 * 认证资源，冲突策略
 */
enum class AuthResourceConflictPolicyEnum(val remark: String) {
    Latest("最后更新优先原则"),
    Deny("拒绝优先原则")
}

/**
 * 用户授权资源
 */
enum class AuthResourceTypeEnum(val remark: String) {
    Allow("允许的资源"),
    Deny("禁止的资源")
}

//认证的资源类型  看做不同的分组
enum class AuthTypeEnum(val remark: String) {
    People("用户"),
    Role("角色"),
    Dept("部门"),
    Group("用户组")
}


enum class loginFailStrategyEnum(var remark: String) {
    Lock("锁定"),
    Code("验证码")
}

enum class ApproveStatusEnum(var remark: String) {
    Create("已创建"),
    Read("已读"),
    Agree("同意"),
    Reject("拒绝")
}

enum class ClientTypeEnum(var remark: String) {
    app("手机客户端"),
    wxApp("微信小程序"),
    h5("H5"),
    Web("Web")
}


enum class FormFieldTypeEnum(var remark: String) {
    Text("字符串"),
    Number("数字"),
    Radio("单选"),
    Check("多选"),
    Boolean("布尔"),
    File("文件"), //可多个
    Date("日期"),
    Select("下拉框"),
    Textarea("多行字符")
}


//统计类型   day /  month
enum class StatisticsEnum(var remark: String) {
    Day(remark = "天"),
    Month(remark = "月")
}

//统计类型   day /  month
enum class SettingEnum(var remark: String) {
    Minute("分钟"),
    Hour("小时"),
}

// 密码过期枚举
enum class PwdExpires(var remark: String) {
    Never("永不过期"),
    Deprecated("已过期"),
    Validity("未过期"),
    Remind("到提醒日"),
}

enum class JenkinsLanguageEnum {
    Java,
    Vue,
    Node,
    Python,
    Php,
    Normal;
}

enum class SysDictionaryGroupEnum(var remark: String) {
    BossSecret("Boss密钥"),
}

enum class JenkinsGitBranchEnum(var remark: String) {
    dev("开发版"),
    test("测试版"),
    demo("演示版"),
    pre("预发版"),
    main("正式版");
}


enum class BaseServiceEnum {
    mariadb,
    mongo,
    redis,
    rabbitmq,
    es,
    influxdb,
    sentinel,
    minio
}

enum class MoveType(val remark: String) {
    UpDown("上下移"),
    Top("置顶")
}

enum class ProductStatusEnum(var remark: String) {
    Online("在售"),
    Promotion("促销"),
    Offline("下架")
}

enum class SendPasswordType(val remark: String) {
    Mobile("手机"),
    Email("邮箱"),
    Defined("自定义")
}

enum class AuthObjectEnum(var remark: String) {
    DepartmentInfo("组织部门"),
    TenantUserGroup("用户组"),
    TenantUser("租户用户")
}

enum class EmployeeTypeEnum(var remark: String) {
    FullTime("全职"),
    PartTime("兼职"),
    Epiboly("外包"),
    ReturnHire("返聘")
}

enum class EmployeeStatusEnum(var remark: String) {
    Formal("正式"),
    Try("试用")
}

// 协议
enum class ProtocolEnum(var remark: String) {
    Self("自有"),
    OAUTH_2("oauth2.0"),
    SAML_2("saml2.0"),
    LDAP("ldap"),
}

enum class BizLogActionEnum(var remark: String) {
    Define(""),
    Save("保存"),
    Add("创建"),
    Update("修改"),
    Delete("删除"),
    Sync("同步"),
    Import("导入"),
    Export("导出"),
    Login("登录"),
    Logout("退出"),
    Authorize("授权"),
    CancelAuthorize("取消授权"),
    EnableOrDisable("启用禁用"),
    UpdatePassword("修改密码"),
    ResetPassword("重置密码"),
    UpOrDownLocation("上下移"),
    Cancel("取消"),
    Execute("执行"),
    Unlock("解锁"),
    Enable("启用"),
    Disable("禁用")

}

enum class BizLogResourceEnum(var remark: String) {
    Define(""),
    Tenant("租户"),
    Admin("管理员"),
    User("成员"),
    App("应用"),
    AppRole("应用角色"),
    AppResource("应用资源"),
    AppAuthResource("应用资源授权"),
    Dept("部门"),
    UserGrop("用户组"),
    DutyDict("岗位字典"),
    IndustryDict("行业字典"),
    SocietyIdentity("社会化身份源"),
    TenantApplicationFieldExtend("应用扩展字段"),
    ExtendFieldDataSourceDict("扩展字段数据源字典"),
    TenantUserFieldExtend("租户用户扩展字段"),
    DepartmentInfoFieldExtend("部门扩展字段"),
    Resource("资源"),
    OrganizationIdentity("企业身份源"),
    TenantRole("租户侧角色"),
    AuthResource("授权资源"),
    ResourceGroup("资源组"),
    AuthResourceGroup("资源组授权"),
    SyncRiskData("风险数据"),
    SyncJob("同步任务"),
    SysSetting("系统设置"),
    LoginUserStatus("员工状态"),
}

enum class AuthRuleParamTypeEnum(val remark: String) {
    User("用户"),
    Environment("环境"),
    Resource("资源")
}

enum class AuthRuleOperationTypeEnum(val remark: String) {
    Basic("基本条件"),
    Expression("表达式"),
    None("未选择")
}

//微信身份源登录方式
enum class WeChatLoginTypeEnum(var remark: String) {
    WeChatAppletAuthorize("小程序授权登录"),
    WeChatOfficialAccount("微信公众号扫码关注登录"),
    None("无")
}

//微信公众号消息解密方式
enum class WeChatMessageDecryptMethodEnum(var remark: String) {
    plain("明文模式"),
    compatible("兼容模式"),
    secure("安全模式")
}

//社会化身份源类型
enum class SocialIdentitySourceTypeEnum(var remark: String) {
    weixin("微信"),
    qq("QQ"),
    zhifubao("支付宝"),
    None("无")
}

enum class SyncJobSchemaEnum(var remark: String) {
    Manually("手动"),
    Cron("定时")
}

enum class SyncJobStatusEnum(var remark: String) {
    Ready("未同步"),
    Runing("同步中"),
    Completed("已同步"),
}

enum class SyncJobRiskDataStatusEnum(var remark: String) {
    Unenforced("未执行"),
    Executed("已执行"),
    Canceled("已取消"),
}

enum class SyncJobDataObjectTypeEnum(var remark: String) {
    Define(""),
    User("成员"),
    Dept("部门"),
}

enum class DeportmentMoveType(val remark: String) {
    Up("上移"),
    Top("置顶"),
    Down("下移")
}

//数据操作类型枚举
enum class DataActionEnum(var remark: String) {
    Insert("新增"),
    Update("修改"),
    Delete("删除"),
}