package nancal.iam.flyway

import nancal.iam.db.mongo.mor
import nbcp.db.FlywayVersionBaseService
import nbcp.db.mongo.match
import nbcp.db.mongo.query
import nbcp.db.mongo.update
import org.springframework.stereotype.Component

/**
 * @Author wrk
 *
 * @Description
 * @Date 2022/4/19-17:55
 */
//@Component
//class `10-InitDb` : FlywayVersionBaseService(10) {
//
//    override fun exec() {
//        initSysId()
//    }
//
//    fun initSysId() {
//        try {
//            pushAllResourcesData(true)
//        }catch (e:Exception){
//            //插入失败代表已存在，不做处理
//        }
//
//    }
//}