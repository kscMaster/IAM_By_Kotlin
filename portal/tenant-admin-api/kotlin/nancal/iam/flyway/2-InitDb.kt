package nancal.iam.flyway

import nancal.iam.db.mongo.ResourceTypeEnum
import nancal.iam.db.mongo.entity.TenantResourceInfo
import nancal.iam.db.mongo.mor
import nancal.iam.util.CodeUtils
import nbcp.comm.HasValue
import nbcp.db.FlywayVersionBaseService
import nbcp.db.mongo.match
import nbcp.db.mongo.query
import org.springframework.stereotype.Component
import java.time.LocalDateTime


@Component
class `2-InitDb` : FlywayVersionBaseService(2) {

    override fun exec() {
        initPersonClassified()
    }

    fun initPersonClassified() {
        println("--------同步子资源历史数据")
        val dataList: List<TenantResourceInfo> = mor.tenant.tenantResourceInfo.query()
            .where { it.isSysDefine match false }
            .toListResult().data.toList()
        if (dataList.isEmpty()) {
            return
        }
        val sizeList: List<List<TenantResourceInfo>> = 500.splitList(dataList)

        println("同步前数据大小是${dataList.size}个数据")

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
                        // 记录同步失败数据
                        println("失败的数据是：${it.id}:${it.code}:${it.appInfo}:${it.isSysDefine}")
                    }
                }
            }

        }
        println("同步了${i}条数据")
        println("失败了${j}条数据")
        val dataList2: List<TenantResourceInfo> = mor.tenant.tenantResourceInfo.query()
            .where { it.isSysDefine match false }
            .toListResult().data.toList()

        println("同步后共${dataList2.size}个数据")
    }

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