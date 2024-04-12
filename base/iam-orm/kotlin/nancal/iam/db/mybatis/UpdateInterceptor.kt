package nancal.iam.db.mybatis

import org.apache.ibatis.executor.Executor
import org.apache.ibatis.mapping.MappedStatement
import org.apache.ibatis.plugin.*
import org.slf4j.LoggerFactory
import nbcp.comm.*
import nbcp.utils.*
import java.util.*
import kotlin.collections.HashMap


@Intercepts(
        Signature(type = Executor::class, method = "update", args = arrayOf(MappedStatement::class, Object::class))
)
class UpdateInterceptor : Interceptor {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    override fun intercept(invocation: Invocation): Any {
        val mappedStatement = invocation.getArgs()[0] as MappedStatement
        val exe = invocation.getTarget() as Executor
        val methodName = invocation.getMethod().name

        if (methodName != "update") {
            return invocation.proceed()
        }

        val mybatisMethodName = mappedStatement.id;

//        val sql = MyUtil.getValueByWbsPath(
//                MyUtil.getValueByWbsPath(mappedStatement.sqlSource, "sqlSource")!!,
//                "sql").AsString()

//        var log_msgs = StringMap()
//        log_msgs["methodName"] = methodName
//        log_msgs["sql"] = sql

        if (mappedStatement.resultMaps.size > 0) {
            val returnType = mappedStatement.resultMaps[0].type

//            val paramMap = mappedStatement.resultMaps[0].idResultMappings

//            log_msgs["returnType"] = returnType.simpleName
//            log_msgs["columnMap"] = paramMap.map { it.column + ":" + it.property }.joinToString(",")
        }


        val parameters = invocation.getArgs()[1] as HashMap<String, *>

//        log_msgs["parameters"] = parameters.map { it.key + ":" + it.value.AsString() }.joinToString(" , ")
//
//
//        logger.info(log_msgs.filter { it.value.HasValue }.map { it.key + ":" + it.value }.joinToString("\n"));

//        var tableName = dataCache.getBrokePatterns(sql, parameters.map { it.value }.toTypedArray())
//
//        if (tableName.mainEntity.isEmpty()) {
//            logger.error("没有分析到主体表: ${sql}")
//            return invocation.proceed()
//        }


//        dataCache.updateDeleteByRegion_BrokeCache (tableName)

        return invocation.proceed()
    }

    private var properties: Properties? = null

    override fun setProperties(properties: Properties?) {
        this.properties = properties
    }

    override fun plugin(target: Any?): Any {
        return Plugin.wrap(target, this); //To change body of created functions use File | Settings | File Templates.
    }
}