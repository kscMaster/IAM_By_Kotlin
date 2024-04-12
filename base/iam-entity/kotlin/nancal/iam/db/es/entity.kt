package nancal.iam.db.es.entity

import nbcp.db.*
import nbcp.db.es.*
import nancal.iam.db.mongo.ProductStatusEnum
import java.io.Serializable
import java.time.LocalDateTime


@DbName("biz-log")
@DbEntityGroup("system")
@DbDefines(
    DbDefine(
        "id",
        """{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}}"""
    ),
    DbDefine(
        "module",
        """{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}}"""
    ),
    DbDefine(
        "data",
        """{"properties":{"action":{"type":"keyword","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"appInfo":{"properties":{"code":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"name":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}}}},"browser":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"city":{"type":"keyword","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"ip":{"type":"keyword","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"os":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"remark":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"resource":{"type":"keyword","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"result":{"type":"keyword","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"roles":{"properties":{"id":{"type":"keyword","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"name":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}}}},"tenant":{"properties":{"id":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"name":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}}}}}}"""
    ),
    DbDefine(
        "msg",
        """{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}}"""
    ),
    DbDefine(
        "creator",
        """{"properties":{"id":{"type":"keyword","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"name":{"type":"keyword","fields":{"keyword":{"type":"keyword","ignore_above":256}}}}}"""
    ),
    DbDefine(
        "createAt",
        """{"type":"date"}"""
    )
)
class BizLogData : Serializable {
    var id: String = ""

    @Cn("业务模块")
    var module: String = ""

    @Cn("业务数据")
    var data: Data = Data()

    @Cn("错误信息")
    var msg: String = ""

    @Cn("请求数据")
    var request: RequestLogData = RequestLogData()

    @Cn("回发数据")
    var response: ResponseLogData = ResponseLogData()

    @Cn("创建人")
    var creator: IdName = IdName()

    @Cn("创建时间")
    var createAt: LocalDateTime = LocalDateTime.now()

    @Cn("成功失败状态")
    var status: Int = 0
}

class RequestLogData{
    @Cn("请求地址")
    var url: String = ""

    @Cn("请求方法")
    var method: String = ""

    @Cn("请求体")
    var body: String = ""

    @Cn("请求头")
    var header: String = ""
}

class ResponseLogData{
    @Cn("响应状态")
    var status: String = ""

    @Cn("响应体")
    var body: String = ""

    @Cn("响应头")
    var header: String = ""
}

class Data {
    @Cn("操作类型")
    var action: String = ""

    @Cn("操作资源")
    var resource: String = ""

    @Cn("详情")
    var remark: String = ""

    @Cn("结果")
    var result: String = ""

    @Cn("客户端IP地址")
    var ip: String = ""

    @Cn("操作系统")
    var os: String = ""

    @Cn("浏览器")
    var browser: String = ""

    @Cn("国省市")
    var city: String = ""

    @Cn("租户")
    var tenant: IdName = IdName()

    @Cn("应用IdName")
    var appInfo: CodeName = CodeName()

    @Cn("角色")
    var roles: MutableList<IdName> = mutableListOf()
}

@DbName("app-log")
@DbEntityGroup("system")
@DbDefines(
    DbDefine(
        "logFile",
        """{"type":"text","index":"true","boost":"1","analyzer":"ik_max_word","search_analyzer":"ik_max_word"}"""
    ),
    DbDefine(
        "className",
        """{"type":"text","index":"true","boost":"1","analyzer":"ik_max_word","search_analyzer":"ik_max_word"}"""
    ),
    DbDefine(
        "content",
        """{"type":"text","index":"true","boost":"1","analyzer":"ik_max_word","search_analyzer":"ik_max_word"}"""
    )
)
class AppLogIndex : BaseEntity(), IEsDocument {
    var visitAt: LocalDateTime? = null

    @Cn("文件名")
    var logFile: String = ""
    var level: String = ""
    var group: String = ""
    var requestId: String = ""

    var className: String = ""
    var line: String = ""

    var content: String = ""
}

@DbName("ngin-log")
@DbEntityGroup("system")
@DbDefines(
    DbDefine(
        "url",
        """{"type":"text","index":"true","boost":"1","analyzer":"ik_max_word","search_analyzer":"ik_max_word"}"""
    ),
    DbDefine(
        "referer",
        """{"type":"text","index":"true","boost":"1","analyzer":"ik_max_word","search_analyzer":"ik_max_word"}"""
    ),
    DbDefine(
        "agent",
        """{"type":"text","index":"true","boost":"1","analyzer":"ik_max_word","search_analyzer":"ik_max_word"}"""
    )
)
class NginxLogIndex : BaseEntity(), IEsDocument {
    var ip: String = ""

    var visitAt: LocalDateTime? = null
    var method: String = ""
    var url: String = ""
    var referer: String = ""
    var status: Int = 0
    var agent: String = ""
}


@DbName("product")
@DbEntityGroup("system")
@DbDefines(
    DbDefine(
        "name",
        """{"type":"text","index":"true","boost":"1","analyzer":"ik_max_word","search_analyzer":"ik_max_word"}"""
    ),
    DbDefine(
        "slogan",
        """{"type":"text","index":"true","boost":"1","analyzer":"ik_max_word","search_analyzer":"ik_max_word"}"""
    ),
    DbDefine(
        "detail",
        """{"type":"text","index":"true","boost":"1","analyzer":"ik_max_word","search_analyzer":"ik_max_word"}"""
    ),
    DbDefine(
        "remark",
        """{"type":"text","index":"true","boost":"1","analyzer":"ik_max_word","search_analyzer":"ik_max_word"}"""
    ),
    DbDefine(
        "skuDefines.key",
        """{"type":"text","index":"true","boost":"1","analyzer":"ik_max_word","search_analyzer":"ik_max_word"}""",
    ),
    DbDefine(
        "skuDefines.value",
        """{"type":"text","index":"true","boost":"1","analyzer":"ik_max_word","search_analyzer":"ik_max_word"}"""
    )
)
class ProductIndex : BaseEntity(), IEsDocument {

    var name = ""
    var tenant = IdName()

    var slogan = ""
    var brandPath = mutableListOf<IdCodeName>()
    var corpCategoryPath = mutableListOf<IdCodeName>()
    var categoryPath = mutableListOf<IdCodeName>()

    var detail = ""

    var remark = ""
    var status: ProductStatusEnum? = null
    var guidePrice = 0
    var skuDefines: MutableList<KeyValueString> = mutableListOf()

    //var skuStockPrice: MutableList<ProductSkuStockPrice> = mutableListOf()
}


@DbName("order_main")
@DbEntityGroup("system")
class OrderMain : BaseEntity(), IEsDocument {
    var accountState = ""
    var activePayStartTime = ""
    var activePayTime = ""
    var appraiseState = ""
    var autarky = ""
    var cashBackMoney = 0
    var changeState = ""
    var copartnerRatio = 0
    var couponFreeMoney = 0
    var createOpeTime = ""
    var createOper = ""
    var customerName = ""
    var customerUuid = ""
    var delFlag = 0
    var delayDays = 0
    var distributionMoney = 0
    var distributionRatio = 0
    var distributorState = ""
    var endTime = ""
    var freight = 0
    var fullReduceFreeMoney = 0
    var integral = 0
    var invoiceEmail = ""
    var invoiceState = ""
    var oneProductDiscount = ""
    var opeTime = ""
    var oper = ""
    var orderDetailJson = ""
    var orderFreePrice = 0
    var orderGroupUuid = ""
    var orderId = ""
    var orderState = ""
    var orderType = ""
    var payPrice = 0
    var payPriceStr = ""
    var payRatio = 0
    var payState = ""
    var permanentDel = 0
    var platCouponFreeMoney = 0
    var refundMoney = 0
    var refundState = ""
    var returnState = ""
    var searchQuery = ""
    var sendCouponState = ""
    var sendPointsState = ""
    var serviceFee = 0
    var serviceFeeRatio = 0
    var shipType = ""
    var shopState = ""
    var startTime = ""
    var storeDiscount = ""
    var storeName = ""
    var storeSplitRatio = 0
    var storeUuid = ""
    var sunState = ""
    var tempFreight = 0
    var totalFreePrice = 0
    var totalPrice = 0
    var uuid = ""
    var version = 0
    var payType = ""
    var payTime = ""
    var sendTime = ""
    var receiveTime = ""
}


