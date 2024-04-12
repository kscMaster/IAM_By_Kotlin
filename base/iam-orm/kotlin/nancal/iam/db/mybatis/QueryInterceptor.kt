package nancal.iam.db.mybatis

import org.apache.ibatis.executor.Executor
import org.apache.ibatis.mapping.MappedStatement
import org.apache.ibatis.plugin.*
import org.apache.ibatis.session.ResultHandler
import org.apache.ibatis.session.RowBounds
import org.slf4j.LoggerFactory
import nbcp.comm.*
import nbcp.utils.*
import java.util.*
import kotlin.collections.HashMap


@Intercepts(
        Signature(type = Executor::class, method = "query", args = arrayOf(MappedStatement::class, Object::class, RowBounds::class, ResultHandler::class))
)
class QueryInterceptor : Interceptor {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }


    override fun intercept(invocation: Invocation): Any {
        val mappedStatement = invocation.getArgs()[0] as MappedStatement
        val exe = invocation.getTarget() as Executor
        val methodName = invocation.getMethod().name

        if (methodName != "query") {
            return invocation.proceed()
        }

//        val mybatisMethodName = mappedStatement.id;

        val sql = MyUtil.getValueByWbsPath(
                MyUtil.getValueByWbsPath(mappedStatement.sqlSource, "sqlSource")!!,
                "sql").AsString()


        val returnType = mappedStatement.resultMaps[0].type

        if (mappedStatement.resultMaps.size > 0) {
            val paramMap = mappedStatement.resultMaps[0].idResultMappings
        }


        val parameters = (invocation.getArgs()[1] as HashMap<String, *>)

//        var cacheKey = dataCache.getCacheKey(sql, parameters.map { it.value }.toTypedArray())
//
//        var json = dataCache.getCacheJson(cacheKey)
//        if (json != null) {
//            //想办法,把Json转为实体.
//            return json
//        }

        var cacheObject = invocation.proceed()
//        if (cacheObject != null) {
//            dataCache.setCacheJson(cacheKey, cacheObject.ToJson())
//        }

        return cacheObject
    }

    private var properties: Properties? = null

    override fun setProperties(properties: Properties?) {
        this.properties = properties
    }

    override fun plugin(target: Any?): Any {
        return Plugin.wrap(target, this); //To change body of created functions use File | Settings | File Templates.
    }
}

