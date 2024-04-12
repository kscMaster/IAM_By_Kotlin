package nancal.iam

import nbcp.comm.*
import nancal.iam.db.mongo.*
import nancal.iam.service.compute.TenantUserService
import nbcp.utils.HttpUtil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import java.util.concurrent.locks.ReentrantLock
import javax.annotation.Resource


class cytest : TestBase() {

    private val lock = ReentrantLock();
    @Resource
    lateinit var authService: TenantUserService

    //["basic:inventory:compare","basic:inventory:publish","123456","basic:inventory:edit"]
    @Test
    fun GenEntity_MysqlTable() {

        val d= authService.getMyAuthResourcesNew("625e55cb654ca149467a95d8","it-mse",ResourceTypeEnum.Ui , AuthResourceConflictPolicyEnum.Latest, AuthResourceTypeEnum.Allow)
        println("==========================")
        println(d.data.ToJson())
        println("==========================")


    }

    @Test
    fun t1(){


    }

    @Test
    fun tes1t(){
        println(123)
    }

    //记得删除旧方法
    //["","zzzz","basic:inventory:compare","basic:inventory:publish","basic:inventory:edit"]
//    @Test
//    fun testOldGteAuthResources(){
//        val d= authService.getMyAuthResourcesOld("622eb8888fb6fa2dd8de464e","lzappwb",ResourceTypeEnum.Data,AuthResourceConflictPolicyEnum.Latest)
//        println("==========================")
//        println(d.data.ToJson())
//        println("==========================")
//    }

    @Test
    fun testaddexpress() {
        println(Thread.currentThread().getStackTrace()[1].getMethodName())
    }


    @Value("\${app.wx.appCode:}")
    var appCode: String = "";

    @Value("\${app.wx.appSecret:}")
    var appSecret: String = "";


    @Value("\${app.wx.mchId:}")
    var mchId: String = ""

    @Value("\${app.wx.mchSecret:}")
    var mchSecret: String = ""

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

    }

}