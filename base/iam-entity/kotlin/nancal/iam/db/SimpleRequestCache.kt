//package nancal.iam.db
//
//import org.springframework.stereotype.Service
//import nbcp.comm.*
//
//
//@Service
//class SimpleRequestCache :IDataCacheService4Sql, IRequestCache {
//    var map = ThreadLocal.withInitial { return@withInitial StringMap(); }
//
//    override fun setCacheJson(cacheKey: CacheKey, retJsons: String) {
//        var key = cacheKey.getExpression();
//        if (key.isEmpty()) return;
//        map.get().put(key, retJsons)
//    }
//
//    override fun getCacheJson(cacheKey: CacheKey): String {
//        var key = cacheKey.getExpression();
//        if (key.isEmpty()) return "";
//        return map.get().get(key) ?: ""
//    }
//
//    override fun clear() {
//        map.get().clear();
//    }
//
//}