package nancal.iam

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import nbcp.db.IdName
import nbcp.db.db
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.event.SystemApplicationVO
import org.junit.jupiter.api.Test
import nbcp.utils.Md5Util


import com.mongodb.client.model.IndexOptions
import nbcp.base.mvc.*
import nbcp.comm.AsString
import nbcp.comm.HasValue
import nbcp.comm.JsonMap
import nbcp.db.DbEntityIndex
import nbcp.db.FlywayVersionBaseService
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.Serializable

class SyncMongoTest : TestBase() {




    @Test
    fun aa() {
        val jsonStr =
            "{\"id\":1471039029345574913,\"name\":\"派工管理26\",\"address\":\"http://192.168.5.213/lezao-dpu/management/myInitiate6\",\"marketName\":\"wdawde\",\"classification\":\"1455437326350110722\",\"logo\":\"http://192.168.5.219:9000/enterprise-container/http://192.168.5.219:9000/enterprise-container/20211202/应用4.png\",\"applicationType\":1,\"carriage\":null,\"remark\":\"111\",\"groundingPlatform\":1,\"documentUrl\":{\"fileName\":null,\"miniFileName\":null,\"url\":null},\"status\":1,\"protal\":\"1\",\"en\":\"ceshien6\",\"action\":\"update\"}\n"
        val json = ObjectMapper().registerModules(JavaTimeModule(), KotlinModule()).readValue(
            jsonStr, SystemApplicationVO::class.java
        )

//        val bean = ObjectMapper().
//            registerModules(JavaTimeModule(), KotlinModule()).readValue(jsonStr, SystemApplicationVO::class.java)


        println(json.action)
//        println(json.action)
    }


    @Test
    fun cc() {
        /*aaaService.exec()*/
    }
    @Test
    fun generateSysAppAllKey(){
        var sysList=mor.iam.sysApplication.query().toList()
        sysList.forEach {
            if(!it.publicKey.HasValue){
                var keyStore = com.nancal.cipher.RSARawUtil.create()
                it.publicKey = keyStore.publicKeyString
                it.privateKey = keyStore.privateKeyString
                mor.iam.sysApplication.updateWithEntity(it).execUpdate()
            }
        }

    }

}
