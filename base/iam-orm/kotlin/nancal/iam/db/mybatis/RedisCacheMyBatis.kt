//package nancal.iam.db.mybatis
//
//import org.apache.ibatis.cache.Cache
//import nbcp.comm.*
//import java.util.concurrent.locks.ReadWriteLock
//
///**
// * 相应的@Mapper注解上添加
// * @CacheNamespace(implementation=(nbcp.db.mybatis.RedisCacheMyBatis::class))
// * 这样很麻烦.
// * 应该在拦截器里统一使用缓存.
// * 缓存的配置,可以放到配置文件.
// */
//class RedisCacheMyBatis() : Cache {
//    var _id: String = "";
//
//    constructor  (id: String) : this() {
//        this._id = id;
//    }
//
//    override fun clear() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun getId(): String {
//        return _id.AsString("RedisCacheMyBatis")
//    }
//
//    override fun removeObject(key: Any?): Any {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun getObject(key: Any?): Any {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun getSize(): Int {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun putObject(key: Any?, value: Any?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun getReadWriteLock(): ReadWriteLock {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}