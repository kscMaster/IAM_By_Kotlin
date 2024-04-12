package nancal.iam

import org.junit.jupiter.api.Test
import nbcp.utils.*
import nbcp.db.sql.*
import nbcp.db.sql.*
import nbcp.db.sql.entity.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

/**
 * Created by yuxh on 2018/7/31
 */
class TestCache : TestBase() {
    //@Before
    fun init() {
//        dbr.system.s_corp.insertIfNotExists(s_corp(id = 1, name = "集团", city_code = 110))
//        dbr.system.s_corp.insertIfNotExists(s_corp(id = 2, name = "工作室", city_code = 110))
//
//        dbr.system.s_user.insertIfNotExists(s_user(id = 1, name = "1a", corp_id = 1))
//        dbr.system.s_user.insertIfNotExists(s_user(id = 10, name = "1b", corp_id = 1))
//        dbr.system.s_user.insertIfNotExists(s_user(id = 11, name = "1c", corp_id = 1))
//        dbr.system.s_user.insertIfNotExists(s_user(id = 2, name = "2a", corp_id = 2))
//        dbr.system.s_user.insertIfNotExists(s_user(id = 20, name = "2b", corp_id = 2))
//        dbr.system.s_user.insertIfNotExists(s_user(id = 21, name = "2c", corp_id = 2))
    }


    @Test
    fun testMd5() {
        var str = "再次HMAC加密，服务器里也会拿出以前存放的密文加上时间再次加密。所以就算黑客在中途截取了密码的密文\n" +
                "\n" +
                "也在能在1分钟只能破译才能有效，大大加强了安全性。服务器为了考虑到网络的延迟一般会多算一种答案，如23分过来的密码\n" +
                "\n" +
                "他会把23分和22分的都算一下和用户匹配只要对上一个就允许登陆。\n" +
                "\n" +
                "作者：指尖猿\n" +
                "链接：https://www.jianshu.com/p/6a0f5c9a8873\n" +
                "來源：简书\n" +
                "简书著作权归作者所有，任何形式的转载都请联系作者获得授转载都请联系作者获得授转载都请联系作者获得授转载都请联系作者获得授转载都请联系作者获得授转载都请联系作者获得授权并注明出处。"

        var startAt = System.currentTimeMillis()
        println(Md5Util.getBase64Md5(str))
        println(Md5Util.getMd5(str))

        println(System.currentTimeMillis() - startAt)
    }
}