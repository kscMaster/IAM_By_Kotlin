package nancal.iam.db.mongo

import org.slf4j.LoggerFactory
import nbcp.utils.*
import nbcp.db.*
import nancal.iam.db.mongo.table.*
import nbcp.db.mongo.*
import org.springframework.data.mongodb.core.MongoTemplate
import java.lang.RuntimeException

fun <M : MongoBaseMetaCollection<out E>, E : Any> MongoUpdateClip<M, E>.set(key: MongoColumnName, value: Any?): MongoUpdateClip<M, E> {
    return this.set { key to value }
}

fun <M : MongoBaseMetaCollection<out E>, E : Any> MongoUpdateClip<M, E>.set(func: (M) -> MongoColumnName, value: Any?): MongoUpdateClip<M, E> {
    return this.set { func(this.moerEntity) to value }
}

class mor {
    companion object {
        @JvmStatic
        val affectRowCount
            get() = db.affectRowCount


        @JvmStatic
        val log
            get() = LogGroup();

        @JvmStatic
        val dev
            get() = DevGroup();

        @JvmStatic
        val tenant
            get() = TenantGroup();

        @JvmStatic
        val admin
            get() = AdminGroup()

        @JvmStatic
        val base
            get() = db.morBase

        @JvmStatic
        val groups
            get() = db.mongo.groups

        @JvmStatic
        val iam
            get() = IamGroup();

        fun getCollection(collectionName: String): MongoBaseMetaCollection<Any>? = db.mongo.mongoEvents.getCollection(collectionName);

        //        val event = SpringUtil.getBean<MongoEventConfig>()
        //
//        private val execAffectRows: ThreadLocal<Int> = ThreadLocal<Int>()
//
//        fun setMorExecAffectRows(n: Int) {
//            execAffectRows.set(n)
//        }
//
//        @JvmStatic
//        val affectRowCount: Int
//            get() {
//                return execAffectRows.get() ?: -1
//            }
//
//
//        @JvmStatic
//        val util = MongoUtil
        val logger = LoggerFactory.getLogger(this::class.java.declaringClass)


        fun getMongoTemplate(planCode: Int = 0): MongoTemplate {
            var uri = SpringUtil.context.environment.getProperty("spring.data.mongodb.uri");
            if (planCode > 0) {
                uri = uri + "_" + planCode
            }
            var ret = db.mongo.getMongoTemplateByUri(uri)
            if (ret == null) {
                throw RuntimeException("获取动态数据库连接时出现异常")
            }
            return ret;
        }
    }
}

