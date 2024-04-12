package nancal.iam

import nancal.iam.base.config.TenantEnConfig
import nancal.iam.db.mongo.ResourceTypeEnum
import nancal.iam.db.mongo.entity.TenantApplication
import nancal.iam.db.mongo.entity.TenantResourceInfo
import nancal.iam.db.mongo.entity.TenantUser
import nancal.iam.db.mongo.mor
import nancal.iam.mvc.iam.ResourceInfoAutoController
import nancal.iam.util.CodeUtils
import nancal.iam.utils.EnumMatchUtils
import nbcp.comm.HasValue
import nbcp.db.mongo.match
import nbcp.db.mongo.query
import nbcp.db.mongo.updateWithEntity
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import javax.annotation.Resource
import kotlin.streams.toList

/**
 *@Author shyf
 * @Date 2022/06/25
 **/
class myTest : TestBase() {

    @Resource
    lateinit var tenantEnConfig: TenantEnConfig

    @Resource
    lateinit var utils: EnumMatchUtils


    @Test
    fun test010() {
        val toList: MutableList<TenantUser> = mor.tenant.tenantUser.query().toList(TenantUser::class.java)
        val groupBy: Map<String, List<TenantUser>> = toList.groupBy { it.tenant.id }
        println(groupBy)
    }

    @Test
    fun test009() {
        val toList: MutableList<TenantApplication> = mor.tenant.tenantApplication.query()
            .toList { TenantApplication::class.java }

        toList.forEach {
            if (!it.ename.HasValue) {
                it.ename = ""
            }
            mor.tenant.tenantApplication.updateWithEntity(it)
        }
    }

    @Test
    fun test01() {
        val cnKey = tenantEnConfig.bundleBizLog.getString("资源")
        println(cnKey)
        val en = utils.getCnKey("创建资源")
        println(en)

        val en1 = tenantEnConfig.bundle.getString("修改应用")
        println(en1)

        val msgKey = utils.getMsgKey("新增授权{你好}")
        println(msgKey)
    }


    @Test
    fun exec() {
        initPersonClassified()
    }

    fun initPersonClassified() {
        val dataList: List<TenantResourceInfo> = mor.tenant.tenantResourceInfo.query()
            .where { it.isSysDefine match false }
            .toListResult().data.toList()
        // 补齐数据
        if (dataList.isEmpty()) {
            return
        }

        dataList.forEach {
            asyncResource(it)
        }
    }

}

fun asyncResource(entity: TenantResourceInfo): Boolean {
    if (entity.code.isEmpty()) {
        println("code格式错误${entity.id}")
    }
    // code处理
    entity.code.split(":").toMutableList().forEach {
        if (!it.HasValue) {
            println("code格式错误${entity.code}")
            return false
        }
    }
    val codeList: MutableList<String> = CodeUtils.codeHolder(entity.code)
    if (codeList.size == 1) {
        return true
    }

    codeList.removeAt(codeList.size - 1) // 当前资源不处理
    codeList.forEach { item ->
        entity.id = ""
        entity.code = item
        entity.type = ResourceTypeEnum.Data
        entity.createAt = LocalDateTime.now()
        entity.updateAt = LocalDateTime.now()
        mor.tenant.tenantResourceInfo.query()
            .where { it.appInfo.code match entity.appInfo.code }
            .where { it.code match item }
            .where { it.tenant.id match entity.tenant.id }
            .toEntity()
            .apply {
                if (this == null) { // 父资源不存在 租户侧资源同步
                    mor.tenant.tenantResourceInfo.doInsert(entity).apply {
                        if (this.isEmpty()) {
                            println("添加父资源失败")
                            return false
                        }
                    }
                }
            }
    }
    return true
}

fun main() {
    val list: MutableList<ResourceInfoAutoController.TenantResourceInfoTree> = mutableListOf()

    val a = ResourceInfoAutoController.TenantResourceInfoTree()
    a.code = "a"
    val b = ResourceInfoAutoController.TenantResourceInfoTree()
    b.code = "a:b"
    val c = ResourceInfoAutoController.TenantResourceInfoTree()
    c.code = "a:b:c"
    list.add(a)
    list.add(b)
    list.add(c)
    // 过滤出不带：
    list2Tree(list)

}

fun list2Tree(list: MutableList<ResourceInfoAutoController.TenantResourceInfoTree>) {
    val toList: List<ResourceInfoAutoController.TenantResourceInfoTree> =
        list.stream().filter { a -> !a.code.contains(":") }.toList()
    list.removeAll(toList)
    val maxTreeSize: Int = maxTreeSize(list)
    toList.forEach {
        setValue(it, list, 0, maxTreeSize)
    }

}

fun setValue(vo : ResourceInfoAutoController.TenantResourceInfoTree,
             list: List<ResourceInfoAutoController.TenantResourceInfoTree>,
             count:Int,
             maxTreeSize: Int ) :ResourceInfoAutoController.TenantResourceInfoTree{
    if (count == maxTreeSize) {
        return vo
    }
    val toList: List<ResourceInfoAutoController.TenantResourceInfoTree> =
        list.stream().filter { a -> a.code.startsWith(vo.code + ":") && countStr(a.code, ":") == count + 1 }.toList()
    vo.children = toList.toMutableList()

    toList.forEach {
        setValue(it, list, count + 1, maxTreeSize)
    }
    return vo
}

fun maxTreeSize(list: List<ResourceInfoAutoController.TenantResourceInfoTree>): Int {
    var max = 0
    list.stream().filter{a-> countStr(a.code, ":") == 2}.toList()
    list.forEach {
        val countStr: Int = countStr(it.code, ":")
        if (countStr> max) {
            max = countStr
        }
    }
    return max
}


fun countStr(longStr: String, mixStr: String): Int {
    var count = 0
    var index = 0
    while (longStr.indexOf(mixStr, index).also { index = it } != -1) {
        index += mixStr.length
        count++
    }
    return count
}