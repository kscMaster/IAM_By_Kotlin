package nancal.iam

import nbcp.comm.*
import nbcp.utils.HttpUtil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import java.util.concurrent.locks.ReentrantLock

@Component
class t1 {
    @Cacheable(value = ["user"], key = "'city_id:010'")
    fun query1(): String {
        println("=== from db1===")
        return "OK1"
    }

    @Cacheable(value = ["user:corp"], key = "'city_id:010'")
    fun query2(): String {
        println("=== from db2===")
        return "OK2"
    }

    @Cacheable(value = ["product:user"], key = "'corp_id:1'")
    fun query3(): String {
        println("=== from db3===")
        return "OK3"
    }

    @Cacheable(value = ["user::city"], key = "'corp_id:020'")
    fun query4(): String {
        println("=== from db4===")
        return "OK4"
    }

    @Cacheable(value = ["user:corp"], key = "'corp_id:2'")
    fun query5(): String {
        println("=== from db5===")
        return "OK5"
    }

    @Cacheable(value = ["product:user"], key = "'city_id:020'")
    fun query6(): String {
        println("=== from db6===")
        return "OK6"
    }

    @Cacheable(value = ["user:product"])
    fun query7(): String {
        println("=== from db7===")
        return "OK7"
    }

    @Caching(
        evict = [
            CacheEvict(value = ["user"], allEntries = true)
        ]
    )
    fun delete() {

    }
}

class cytest : TestBase() {

    private val lock = ReentrantLock();

    @Test
    fun GenEntity_MysqlTable() {
        var match = AntPathMatcher()
        var ret = match.match("/hi/{\\w*}", "/hi/abc")

        println(ret)
    }

    @Test
    fun testaddexpress() {
        println(Thread.currentThread().getStackTrace()[1].getMethodName())
    }

    @Autowired
    lateinit var t: t1

    @Test
    fun tsetorder() {
        for (i in 1..3) {
            t.query1()
            t.query2()
            t.query3()
            t.query4()
            t.query5()
            t.query6()
            t.query7()
        }
    }

    @Test
    fun tsetorder2() {
        t.delete()
    }

    @Value("\${app.wx.appCode:}")
    var appCode: String = "";

    @Value("\${app.wx.appSecret:}")
    var appSecret: String = "";


    @Value("\${app.wx.mchId:}")
    var mchId: String = ""

    @Value("\${app.wx.mchSecret:}")
    var mchSecret: String = ""

//    @Test
//    fun  testFund(){
//        val res = WxRefundPayRequestData(appCode,mchId,"202002222108295401038",out_refund_no = CodeUtil.getCode(),total_fee = 1,refund_fee = 1,refund_desc = "退款测试").doRefundPay(mchSecret)
//
//        //设置订单状态
//        mor.shop.orderInfo.update()
//                .where { it.orderCode match this.out_trade_no }
//                .set { it.status to OrderStatusEnum.Refunded }
//                .set { it.payInfo.remark to "已退款" }
//                .exec()
//        println(res.ToJson())
//    }
//
//    @Test
//    fun testPayPerson(){
//
//        val res = Wx2PayPersonServerRequestData(
//                appCode, mchId, amount = 500, openid = "oX8Us5KTN8u1yUIwPbEOkvCpSB6E", partner_trade_no = "202002222108295401038", desc = "test"
//        ).doPayPerson(mchSecret)
//
//        //设置订单状态
//        mor.shop.orderInfo.update()
//                .where { it.orderCode match this.out_trade_no }
//                .set { it.status to OrderStatusEnum.Refunded }
//                .set { it.payInfo.remark to "已退款" }
//                .exec()
//        println(res.ToJson())
//    }

    @Test
    fun testGetHeader() {
        val serverUrl = "http://localhost:3000/user/login"
        val http = HttpUtil(serverUrl)

        val data = JsonMap()

        data.put("user_name", "cy");
        data.put("password", "!QAZ2wsx")

        http.request.headers.set("lang", "zh-CN");
        http.request.contentType = "application/x-www-form-urlencoded"

        //response
        val responseData = http.doPost(data)
        println(responseData)

        //获取响应cookie  添加到同cok
        val responseCk = http.response.headers.get("set-cookie")
        println(responseCk)

//        HttpContext.Current.Response.AppendCookie(responseCk)

    }

/*
    @Autowired
    lateinit var smsService: SmsService

    @Test
    fun testsms(){
        val sendSms = smsService.sendSms(MobileCodeModuleEnum.Registe,"18500681612", MyUtil.getRandomWithLength(4))

       *//* if (sendSms.isNotEmpty()) {
            return JsonResult.error(sendSms)
        }

        return JsonResult()*//*

    }*/


}