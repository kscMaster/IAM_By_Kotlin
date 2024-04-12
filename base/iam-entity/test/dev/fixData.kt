//package dev
//
//import nancal.iam.TestBase
//import com.mongodb.BasicDBList
//import com.mongodb.BasicDBObject
//import com.mongodb.MongoClientURI
//import org.junit.jupiter.api.Test
//import org.springframework.core.convert.support.GenericConversionService
//import org.springframework.data.mongodb.core.MongoTemplate
//import org.springframework.data.mongodb.core.SimpleMongoDbFactory
//import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
//import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
//import org.springframework.data.mongodb.core.convert.MappingMongoConverter
//import org.springframework.data.mongodb.core.mapping.MongoMappingContext
//import nbcp.comm.*
//import nbcp.comm.*
//import nbcp.utils.*
//import nbcp.utils.*
//import nbcp.utils.*
//import nbcp.db.and
//import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
//import nbcp.db.mongo.entity.ProductSkuStockPrice
//import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
//
//class fixData : TestBase() {
//
//    @Test
//    fun fixMenus() {
//        kotlin.run {
//            var dev8 = mor.dev8.jwdDefinition.query().toEntity()!!;
//
//            RecursionUtil.execute(dev8.menus, { it.subMenus }) { it, parent, index ->
//                if (it.id.AsString().length < 9) {
//                    it.code = it.id
//                    it.id = CodeUtil.getId();
//                }
//                return@execute RecursionReturnEnum.Go
//            }
//
//            mor.dev8.jwdDefinition.updateById(dev8.id)
//                    .set{it.menus.toString() to  dev8.menus}
//                    .exec();
//        }
//
//        kotlin.run {
//            var corp = mor.system.sysCorporationDefinition.query().toEntity()!!;
//
//            RecursionUtil.execute(corp.menus, { it.subMenus }) { it, parent, index ->
//                if (it.id.AsString().length < 9) {
//                    it.code = it.id
//                    it.id = CodeUtil.getId();
//                }
//                return@execute RecursionReturnEnum.Go
//            }
//
//            mor.system.sysCorporationDefinition.updateById(corp.id)
//                    .set{it.menus.toString() to  corp.menus}
//                    .exec();
//        }
//
//    }
//
//    /**
//     * 2018.3.20
//     */
//    @Test
//    fun fixProduct() {
//        mor.shop.productInfo.query()
//                .toList(BasicDBObject::class.java)
//                .forEach {
//                    var id = it.get("_id").AsString()
//
//                    if (it.containsField("logos") && !it.containsField("logo")) {
//                        var logo = (it.get("logos") as BasicDBList).firstOrNull();
//
//                        if (logo != null) {
//                            mor.shop.productInfo.updateById(id)
//                                    .set{it.logo.toString() to  logo}
//                                    .exec();
//                        }
//                    }
//                }
//    }
//
//    /**
//     * 2018.3.20
//     */
//    @Test
//    fun fixOrder() {
//        var skip = 0;
//        var take = 10;
//        while (true) {
//            var list = mor.shop.orderInfo.query()
//                    .limit(skip, take)
//                    .toList()
//            if (list.any() == false) break;
//
//            skip += take;
//
//            list.forEach {
//
//                mor.shop.orderInfo.updateById(it.id)
//                        .set{it.amount, it.details.sumBy { it.amount })
//                        .exec();
//            }
//        }
//    }
//
//    /**
//     * 2018.3.20
//     */
//    @Test
//    fun fixOrder_activity() {
//        var skip = 0;
//        var take = 10;
//        while (true) {
//            var list = mor.shop.orderInfo.query()
//                    .limit(skip, take)
//                    .toList(BasicDBObject::class.java)
//            if (list.any() == false) break;
//
//            skip += take;
//
//            list.forEach {
//                var id = it.getValue("_id").toString();
//                var detail = (it.getValue("details") as List<*>).first() as BasicDBObject
//
//                var activityType = detail.getValue("activityType").AsString();
//
//                mor.shop.orderInfo.updateById(id)
//                        .set{it.activityType, activityType)
//                        .exec();
//            }
//        }
//    }
//
//    fun getTemplate(uri: String): MongoTemplate {
//        var dbFactory = SimpleMongoDbFactory(MongoClientURI(uri))!!;
//        var converter = MappingMongoConverter(DefaultDbRefResolver(dbFactory), MongoMappingContext())
//        converter.setTypeMapper(DefaultMongoTypeMapper(null));
//
//        (converter.conversionService as GenericConversionService).addConverter(Date2LocalDateTimeConverter())
//
//        var ret = MongoTemplate(dbFactory, converter);
//
//        return ret;
//    }
//
//    /**
//     * 2018.3.20
//     */
//    @Test
//    fun order_status() {
//        mor.shop.orderInfo.update()
//                .where(mor.shop.orderInfo.status match "Cancelled")
//                .set{it.status, OrderStatusEnum.Over)
//                .exec();
//    }
//
//
//    /**
//     * 2018.3.22
//     */
//    @Test
//    fun shop_tran2test() {
//        var skip = 0;
//        var online = getTemplate("mongodb://nbcp:123@121.40.228.118:27717/shop_test")
//        while (true) {
//            var user = mor.wx.wxAppUser.query()
//                    .useTemplate(online)
//                    .limit(skip, 1)
//                    .toEntity(BasicDBObject::class.java)
//
//            if (user == null) break;
//
//            skip += 1;
//
//            var id = user!!.getValue("_id").toString();
//
//            if (mor.wx.wxAppUser.query().where("_id" match id).exists()) {
//                continue;
//            }
//
//            println(id)
//            mor.wx.wxAppUser.insert(user!!);
//        }
//
//        skip = 0;
//        while (true) {
//            var order = mor.shop.orderInfo.query()
//                    .useTemplate(online)
//                    .limit(skip, 1)
//                    .toEntity(BasicDBObject::class.java)
//
//            if (order == null) break;
//
//            skip += 1;
//
//            var id = order!!.getValue("_id").toString();
//
//            if (mor.shop.orderInfo.query("_id" match id).exists()) {
//                continue;
//            }
//
//            println(id)
//            mor.shop.orderInfo.insert(order!!);
//        }
//
//
//        skip = 0;
//        while (true) {
//            var product = mor.shop.productInfo.query()
//                    .useTemplate(online)
//                    .limit(skip, 1)
//                    .toEntity(BasicDBObject::class.java)
//
//            if (product == null) break;
//
//            skip += 1;
//
//            var id = product!!.getValue("_id").toString();
//
//            if (mor.shop.productInfo.query("_id" match id).exists()) {
//                continue;
//            }
//
//            println(id)
//            mor.shop.productInfo.insert(product!!);
//        }
//    }
//
//    /**
//     * 2018.3.28
//     */
//    @Test
//    fun fixProduct2() {
//        mor.shop.productInfo.update()
//                .where("productStatus" match "Online")
//                .set{it.status to  ProductStatusEnum.Online}
//                .exec();
//
//        mor.shop.productInfo.update()
//                .where("productStatus" match "Offline")
//                .set{it.status to  ProductStatusEnum.Offline}
//                .exec();
//    }
//
//    /**
//     * 添加默认 skuStockPrice @2018.4.16
//     */
//    @Test
//    fun fixProduct3() {
//        mor.shop.productInfo.update()
//                .where { it.skuStockPrice.skuDefine match "默认" }
//                .where { it.skuDefines match_size  0 }
//                .set { it.skuStockPrice.and("$").skuDefine to "" }
//                .exec();
//
//
//        mor.shop.productInfo.update()
//                .where { it.skuStockPrice.toString() match_exists false }
//                .set { it.skuStockPrice.toString() to arrayOf<Any>() }
//                .exec();
//
//        var list = mor.shop.productInfo.query()
//                .where { it.skuStockPrice match_size 0 }
//                .toList();
//
//        list.forEach { product ->
//            mor.shop.productInfo.updateById(product.id)
//                    .set { it.skuStockPrice.toString() to arrayOf(ProductSkuStockPrice("默认", 999, product.guidePrice, product.guidePrice)) }
//                    .exec();
//        }
//    }
//}