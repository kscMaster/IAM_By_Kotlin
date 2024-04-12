package nancal.iam.mvc.tenant

import nancal.iam.TestBaseShyf
import nancal.iam.db.mongo.entity.SysResourceInfo
import nancal.iam.db.mongo.mor
import nancal.iam.mvc.admin.SysAppResourceController
import nbcp.comm.HasValue
import nbcp.db.mongo.match
import nbcp.db.mongo.query
import org.junit.jupiter.api.Test
import org.springframework.beans.BeanUtils

/**
 *@Author syf
 * @Date 2022 06 02
 **/
class SysAppResourceControllerTest : TestBaseShyf() {


    @Test
    fun testDb() {
        val result : MutableList<SysAppResourceController.SysResourceTree> = mutableListOf()
        // 获取一级资源
        var toMutableList: MutableList<SysResourceInfo> = mor.iam.sysResourceInfo.query()
            .where { it.appInfo.code match "lzappwb" }
            .apply {
                this.where { it.code match_pattern "^[^:]+\$" }
            }.toListResult().data.toMutableList()
        println(toMutableList.size)
    }

//    @Test
//    fun test01() {
//        val result : MutableList<SysAppResourceController.SysResourceTree> = mutableListOf()
//        // 获取一级资源
//        mor.iam.sysResourceInfo.query()
//            .where { it.appInfo.code match "lzappwb" }
//            .apply {
//                this.where { it.code match_pattern "^[^:]+\$" }
//            }.toListResult().data.toMutableList().forEach{
//                val vo = SysAppResourceController.SysResourceTree()
//                BeanUtils.copyProperties(it, vo)
//                getChildResource(it.code, "lzappwb")
//                result.add(vo)
//            }
//        println(result.toString())
//    }

    companion object {
        fun getChildResource(parentCode : String, appCode:String):MutableList<SysAppResourceController.SysResourceTree>{
            val result : MutableList<SysAppResourceController.SysResourceTree> = mutableListOf()
            var toMutableList: MutableList<SysResourceInfo> = mor.iam.sysResourceInfo.query()
                .where { it.appInfo.code match appCode }
                .apply {
                    if (!parentCode.HasValue) {
                        this.where { it.code match_pattern "^[^:]+\$" }
                    } else {
                        this.where { it.code match_pattern "^${parentCode}:[^:]+\$" }
                    }
                }.toListResult().data.toMutableList()
            if (toMutableList.isEmpty()) {
                return result
            } else {
                toMutableList.forEach{
                    val vo = SysAppResourceController.SysResourceTree()
                    BeanUtils.copyProperties(it, vo)
                    val childResource: MutableList<SysAppResourceController.SysResourceTree> = getChildResource(it.code, appCode)
                    vo.children = childResource
                    result.add(vo)
                }
                return  result
            }

        }
    }
}