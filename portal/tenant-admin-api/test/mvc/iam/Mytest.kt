package mvc.iam

import nancal.iam.TestBase
import nancal.iam.db.mongo.ResourceTypeEnum
import nancal.iam.db.mongo.entity.TenantResourceInfo
import nancal.iam.db.mongo.mor
import nancal.iam.util.CodeUtils
import nbcp.comm.HasValue
import nbcp.db.mongo.match
import nbcp.db.mongo.query
import org.junit.jupiter.api.Test
import java.time.LocalDateTime


/**
 *@Author syf
 * @Date 2022 06 02
 * @Description flyway数据同步测试 只处理租户侧不是系统默认的资源
 * TODO 注释
 * @see nancal.iam.base.config.WebSocketConfig
 **/
class mytest : TestBase() {

//    @Test
    fun testDb() {
        println("--------测试数据库连接")
        val dataList: List<TenantResourceInfo> = mor.tenant.tenantResourceInfo.query().toListResult().data.toList()
        println(dataList.size)
    }

    @Test
    fun test() {
        val dataList: List<TenantResourceInfo> = mor.tenant.tenantResourceInfo.query()
//            .where { it.isSysDefine match false }
            .where { it.code match  "test007:test007001"}
            .toListResult().data.toList()
        dataList.forEach{
            println("${it.code},${it.appInfo.code},${it.tenant.id},${it.tenant.name}")
        }
    }

    @Test
    fun test01() {
        println("--------同步子资源历史数据")
        val dataList: List<TenantResourceInfo> = mor.tenant.tenantResourceInfo.query()
            .where { it.isSysDefine match false }
            .where { it.code match  "test007:test007001"}
            .toListResult().data.toList()
        if (dataList.isEmpty()) {
            return
        }
        val sizeList: List<List<TenantResourceInfo>> = 500.splitList(dataList)

        println("总共需要同步${dataList.size}个数据")

        // 补齐数据
        if (sizeList.isEmpty()) {
            return
        }

        var i = 0
        var j = 0
        // 同步数据
        sizeList.forEach{ it ->

            it.forEach{
                asyncResource(it).apply {
                    i++
                    if (!this) {
                        j++
                        // TODO 记录同步失败数据
                        println("失败的数据是：${it.id}:${it.code}:${it.appInfo}:${it.isSysDefine}")
                    }
                }
            }

        }
        println("同步了${i}条数据")
        println("失败了${j}条数据")

    }


    companion object {
        fun Int.splitList(dataList: List<TenantResourceInfo>): List<List<TenantResourceInfo>> {
            val length = dataList.size
            // 计算可以分成多少组
            val num = (length + this - 1) / this
            val newList: MutableList<List<TenantResourceInfo>> = ArrayList(num)
            for (i in 0 until num) {
                // 开始位置
                val fromIndex = i * this
                // 结束位置
                val toIndex = if ((i + 1) * this < length) (i + 1) * this else length
                newList.add(dataList.subList(fromIndex, toIndex))
            }
            return newList
        }

        fun asyncResource(entity: TenantResourceInfo) :Boolean {
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
                    .where { it.code match  item}
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

    }

}