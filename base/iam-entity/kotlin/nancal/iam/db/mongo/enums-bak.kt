package nancal.iam.db.mongo

/**
 * 反馈信息枚举
 */
enum class FeedBackEnum(var remark: String) {
    Support("支持"),
    Oppose("反对")
}


enum class MessageTypeEnum(var remark: String) {
    None("默认消息"),
    System("系统消息"),
    Todo("待办消息"),

    //待审批
    WorkOrder("工单消息"),
    WorkFlow("工作流消息"),

    //预警
    StockWarn("库存预警消息")
}


enum class SearchTypeEnum(var remark: String) {
    User("用户")
}

enum class CouponTypeEnum(var remark: String) {
    Discount("优惠券"),
    Freight("运费券"),
}

/**
 * 快递速度类型
 */
enum class LogisticsTypeEnum(var remark: String) {
    Road("普通速度"),
    Freeway("快递"),
    Railway("超快"),
    Airway("加急")
}

enum class LogisticsFeeTypeEnum(var remark: String) {
    None("包邮"),
    Number("按件数"),
    Weight("按重量"),
    Volume("按体积")
}

/**
 * Sku显示样式
 */
enum class SkuLogoStyleEnum(var remark: String) {
    Text("仅显示文字"),
    Logo("仅显示图片"),
    Image("显示大图"),
    Unit("组合样式")
}

enum class ShopActivityTypeEnum(var remark: String) {
    Gift("赠送"),
    Unit("组合套装"),
    ExchangePurchase("换购"),
    BackCoupon("返券"),
    BackDiscount("满减")
}

/**
 * 发票类型
 */
enum class InvoiceTypeEnum(var remark: String) {
    Person("个人"),
    Corp("企业普通发票"), //增值税普通发票
    Tax("企业增值税发票")   //增值税专用发票
}

/**
 * 发票内容
 */
enum class InvoiceContentEnum(var remark: String) {
    Detail("商品明细"),
    Category("商品类别")
}

enum class PayStatusEnum constructor(val remark: String) {
    Unpay("未付款"),
    Payed("已付款")
}


enum class PayTypeEnum(var remark: String) {
    WxPay("微信支付"),
    Alipay("支付宝"), //支付宝
    Transfer("银行汇款")    //银行转帐
}

/**
 * Login、Registe、ChangeMobile: 您的验证码是 ${1},请于5分钟内完成验证,若非本人操作,请忽略本短信。
 * SendPassword: 您的的初始登录密码为：${1}，为了您的账户安全，请勿将密码告知他人,登录后请尽快修改密码。
 * ForgetPassword: 您正在[重置/找回密码]，验证码${1} ，有效时长5分钟。不要转给他人，若非本人操作，请修改密码。
 */
enum class MobileCodeModuleEnum(var remark: String) {
    None("无"),
    Login("登录"),
    Registe("注册"),
    ChangeMobile("更换手机"),
    ForgetPassword("找回密码"),
    BindBankCard("绑定银行卡"),
    SendPassword("发送密码"),
    PasswordWillExpire("密码将要过期"),
    PasswordExpired("密码已过期"),
}

enum class CorpOrderStatusEnum(var remark: String) {
    Created("待付款"),     //客户已下单
    Payed("已付款"),       //客户已付款
    RequestCancel("请求取消"),
    Prepare("待发货"),     //平台已打开订单,正在备货
    Delivered("待收货"),   //平台已发货
    Received("已收货"),    //客户已收货，完成状态。
    Canceled("已取消"),         //非正常完成：过期未付款.
    Refunded("已退款"),
}

//订单状态
enum class OrderStatusEnum(var remark: String) {
    Created("待付款"),     //客户已下单
    Payed("已付款"),       //客户已付款
    RequestCancel("请求取消"),
    Canceled("已取消"),         //非正常完成：过期未付款.
    Refunded("已退款"),        //整体已退款
    Done("已完成")         //全部已收货
}

enum class RefundStatusEnum(var remark: String) {
    Created("已创建"),
    Approved("已审核"),
    Reject("已拒绝"),
    Refunded("已退款"),    //平台已退款，完成状态。
}

/**
 * 快递状态
 */
enum class DeliveryStatusEnum(var remark: String) {
    Start("接单"),
    Step("出发"),
    Hold("接货"),
    Transport("运送"),
    Done("完成"),
    Reject("拒接"),
    Back("退回中"),
    Returned("已退回")
}





//合伙人关系
enum class UserPartnerTypeEnum(var remark: String) {
    Normal(remark = "普通合伙人"),
    Platform(remark = "平台合伙人"),
    Admin("员工")
}

//1在途中 2派件中 3已签收 4派送失败(拒签等
enum class ExpressEnum(var value: Int, var remark: String) {
    None(0, "暂无信息"),
    OnTheWay(1, "在途中"),
    Distribution(2, "派件中"),
    Signed(3, "已签收"),
    Failure(4, "派送失败"),

}

enum class UserStatusEnum(var remark: String) {
    Job("在职"),
    Quit("离职")
}

enum class UserRoleEnum(var remark: String) {
    CmsPublisher("内容发布者"),

}