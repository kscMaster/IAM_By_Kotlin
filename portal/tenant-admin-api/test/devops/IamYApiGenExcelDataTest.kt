package devops

import cn.hutool.core.convert.Convert
import cn.hutool.core.map.MapUtil
import cn.hutool.core.util.IdUtil
import cn.hutool.http.HttpUtil
import cn.hutool.poi.excel.ExcelUtil
import nbcp.comm.FromJson
import nbcp.comm.FromListJson
import nbcp.comm.ToJson
import org.junit.jupiter.api.Test
import java.io.File
import javax.swing.filechooser.FileSystemView





/**
 * @Author wrk
 *
 * @Description
 * @Date 2022/3/16-15:27
 */
class IamYApiGenExcelDataTest {

    private val loginName = "wangrk@nancal.com"
    private val pass = "wangruikang123"
    private val logOutUrl = "http://192.168.5.213:31010/api/user/logout"
    private val loginUrl = "http://192.168.5.213:31010/api/user/login"
    private val tenantApiDataUrl =
        "http://192.168.5.213:31010/api/plugin/export?type=json&pid=296&status=all&isWiki=false"
    private val adminApiDataUrl =
        "http://192.168.5.213:31010/api/plugin/export?type=json&pid=341&status=all&isWiki=false"
    private val loginApiDataUrl = "http://192.168.5.213:31010/api/plugin/export?type=json&pid=365&status=all&isWiki=false"
    private val apiDetail="http://192.168.5.213:31010/api/interface/get"

    //分类
    class Cate(
        var index: Int = 0,
        var name: String = "",
        var desc: String = "",
        var list: MutableList<Api> = mutableListOf<Api>()
    )

    //分类里的接口
    class Api(
        //path String
        //params []
        var query_path: Map<String, Any> = mapOf(),
        var status: String = "",
        var method: String = "",
        var title: String = "",
        var path: String = "",
        var _id:String = ""
    )

    class ApiVo(
        //  一级功能点	二级功能	接口名称	接口地址	字段是否已做校验	测试情况	接口负责人
        //path String
        //params []
        var one: String = "",
        var two: String = "",
        var title: String = "",
        var path: String = "",
        var check: String = "",
        var test: String = "",
        var user: String = ""
    )

    @Test
    fun genExcelApiData() {
        val logOutRes = logOut()
        if (logOutRes.isNotEmpty()) throw RuntimeException(logOutRes)
        val loginRes = login()
        if (loginRes.isNotEmpty()) throw RuntimeException(logOutRes)
        val tenantApilist = getTenantApiData()
        val adminApilist = getAdminApiData()
        val loginApilist = getLoginApiData()
        val tenantData = mutableListOf<ApiVo>()
        val adminData = mutableListOf<ApiVo>()
        val loginData = mutableListOf<ApiVo>()
        tenantApilist.forEach {
            val jsonStr=it.ToJson()
            val ss = Convert.convert(Cate::class.java,jsonStr)
            ss.list.forEach { api ->
                var vo = ApiVo()
                vo.one = ss.name
                vo.path = api.path
                vo.title = api.title
                vo.user=getApiData(api._id.toInt())
                tenantData.add(vo)
            }
        }
        adminApilist.forEach {
            val jsonStr=it.ToJson()
            val ss = Convert.convert(Cate::class.java,jsonStr)
            ss.list.forEach { api ->
                var vo = ApiVo()
                vo.one = ss.name
                vo.path = api.path
                vo.title = api.title
                vo.user=getApiData(api._id.toInt())
                adminData.add(vo)
            }
        }
        loginApilist.forEach {
            val jsonStr=it.ToJson()
            val ss = Convert.convert(Cate::class.java,jsonStr)
            ss.list.forEach { api ->
                var vo = ApiVo()
                vo.one = ss.name
                vo.path = api.path
                vo.title = api.title
                vo.user=getApiData(api._id.toInt())
                loginData.add(vo)
            }
        }
        //输出excel
        // 通过工具类创建writer
        val writer = ExcelUtil.getWriter("${getDesktopUrl()}/接口数据-${IdUtil.fastUUID()}.xlsx","IAM租户侧")


        //自定义标题别名
        writer.addHeaderAlias("one", "一级功能点");
        writer.addHeaderAlias("two", "二级功能");
        writer.addHeaderAlias("title", "接口名称");
        writer.addHeaderAlias("path", "接口地址");
        writer.addHeaderAlias("check", "字段是否已做校验");
        writer.addHeaderAlias("test", "测试情况");
        writer.addHeaderAlias("user", "创建人");
        // 合并单元格后的标题行，使用默认标题样式
        writer.merge(6, "IAM租户侧")
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(tenantData, true)
        writer.autoSizeColumnAll()
        writer.setSheet("IAM Admin侧");
        writer.merge(6, "IAM Admin侧")
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(adminData, true)
        writer.autoSizeColumnAll()

        writer.setSheet("统一登录")
        writer.merge(6, "统一登录")
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(loginData, true)
        writer.autoSizeColumnAll()
        writer.close()
        logOut()
    }

    fun login(): String {
        val res = HttpUtil.post(loginUrl, mapOf<String, String>("email" to loginName, "password" to pass)).toString()
        val map = res.FromJson(Map::class.java)
        if (map?.get("errcode") == 0) {
            return ""
        }
        return res
    }

    fun logOut(): String {
        val res = HttpUtil.get(logOutUrl)
        val map = res.FromJson(Map::class.java)
        if ("ok" == map?.get("data").toString()) {
            return ""
        }
        return res
    }

    fun getTenantApiData(): Collection<*> {
        var res = HttpUtil.get(tenantApiDataUrl).replace("\n", "").ToJson()
        var retrunRes = res.FromListJson(Cate::class.java)
        return retrunRes
    }

    fun getAdminApiData(): Collection<*> {
        var res = HttpUtil.get(adminApiDataUrl).replace("\n", "").ToJson()
        var retrunRes = res.FromListJson(Cate::class.java)
        return retrunRes
    }
    fun getLoginApiData(): Collection<*> {
        var res = HttpUtil.get(loginApiDataUrl).replace("\n", "").ToJson()
        var retrunRes = res.FromListJson(Cate::class.java)
        return retrunRes
    }

    fun getApiData(id:Int): String{
        var res = HttpUtil.get(apiDetail, mapOf("id" to id)).replace("\n", "").ToJson()
        val jsonObj = res.FromJson(Map::class.java)
        if(0!=jsonObj?.get("errcode")) return ""
        return jsonObj.get("data").toString().FromJson(Map::class.java)?.get("username").toString()
    }

    fun getDesktopUrl():String{
        val fsv = FileSystemView.getFileSystemView()
        val com: File = fsv.homeDirectory
        //println(com.getPath().replace("\\","/"))
        return com.getPath().replace("\\","/")
    }
}