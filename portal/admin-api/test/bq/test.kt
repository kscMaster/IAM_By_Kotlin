//package bq
//
//import org.junit.jupiter.api.Test
//import org.springframework.test.context.junit.jupiter.SpringExtension
import org.junit.jupiter.api.extension.ExtendWith
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.test.context.junit4.SpringRunner
//import nbcp.PzxApplication
//import nbcp.comm.*
//import nbcp.comm.*
//import nbcp.comm.*
//
//
//import nbcp.db.mongo.entity.Corporation
//import mor
//import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
//
///**
// * Created by dev8 on 2017/7/27.
// */
//@ExtendWith(SpringExtension::class)
//@SpringBootTest(classes = arrayOf(PzxApplication::class))
//class Test {
//
//    @Test
//    fun test(){
//        val info = mor.code.qrCodeInfo.query(mor.code.qrCodeInfo.serialNumber match_gte "00001")
//                .orderBy(true,mor.code.qrCodeInfo.serialNumber)
//                .limit(0, 20)
//                .toList()
//
//        for (x in info){
//            println("===============>"+x.code+"==============="+x.serialNumber)
//        }
//    }
//
//    @Test
//    fun testString(){
//        println("00040">"00038")
//    }
//
//    @Test
//    fun corpCopy(){//先删除modify里的数据
//        var corpList = mor.corp.corporation.findAll()
//        var modifyCorp = Corporation()
//        for(x in corpList){
//            modifyCorp.id = x.id
//            modifyCorp.name = x.name
//            modifyCorp.city = x.city
//            modifyCorp.address = x.address
//            modifyCorp.phone = x.phone
//            modifyCorp.site = x.site
//            modifyCorp.weixin = x.weixin
//            modifyCorp.industry = x.industry
//            modifyCorp.logo = x.logo
//            modifyCorp.qualifications = x.qualifications
//            modifyCorp.images = x.images
//            modifyCorp.detail = x.detail
//            modifyCorp.receiveAddresses = x.receiveAddresses
//
//
//            mor.corp.corporation.insert(modifyCorp)
//        }
//
//        @Test
//        fun corp(){//先删除modify里的数据
//            var id = "59951019e13b1f5a268ad8a1"
//            var x = mor.corp.corporation.findById(id)
//            var modifyCorp = Corporation()
//            if (x != null) {
//                modifyCorp.id = x.id
//                modifyCorp.name = x.name
//                modifyCorp.city = x.city
//                modifyCorp.address = x.address
//                modifyCorp.phone = x.phone
//                modifyCorp.site = x.site
//                modifyCorp.weixin = x.weixin
//                modifyCorp.industry = x.industry
//                modifyCorp.logo = x.logo
//                modifyCorp.qualifications = x.qualifications
//                modifyCorp.images = x.images
//                modifyCorp.detail = x.detail
//                modifyCorp.receiveAddresses = x.receiveAddresses
//
//                mor.corp.corporation.insert(modifyCorp)
//            }
//        }
//
//
//
//
//    }
//
//
//
//
//
//
//
//
//
//}