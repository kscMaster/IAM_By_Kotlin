package nancal.iam.base.config;

import com.mongodb.MongoException
import nancal.iam.weixinaes.AesException
import nbcp.base.mvc.*
import nbcp.comm.*
import org.slf4j.LoggerFactory
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.data.redis.RedisSystemException
import org.springframework.data.redis.connection.RedisPipelineException
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import java.sql.SQLException
import javax.crypto.BadPaddingException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ControllerAdvice
@ResponseBody
class GlobalProcController {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    @ExceptionHandler(Exception::class, Error::class)
    fun exp(ex: Throwable, request: HttpServletRequest, response: HttpServletResponse) {
        logger.error(ex.message, ex);

        if (response.status == 401) {
            return;
        }

        response.status = 200;
        //ex.cause 可能是 RuntimeException包装的异常
        var msg = getExceptionSafeMessage(ex.cause ?: ex);
        response.WriteJsonRawValue(msg.ToJson())
    }

    private fun getExceptionSafeMessage(ex: Throwable): JsonResult {
        if (ex is RedisSystemException) {
            return JsonResult.error("redis系统错误", -10);
        }
        if (ex is RedisConnectionFailureException) {
            return JsonResult.error("redis数据连接失败", -11);
        }
        if (ex is RedisPipelineException) {
            return JsonResult.error("redis流水线执行错误", -12);
        }
        if (ex is SQLException) {
            return JsonResult.error("sql执行错误", -20);
        }
        if (ex is org.springframework.dao.DuplicateKeyException) {
            return JsonResult.error("唯一键冲突", -21);
        }
        if (ex is org.springframework.dao.DataAccessException) {
            return JsonResult.error("数据访问错误", -22);
        }

        if (ex is MongoException) {
            return JsonResult.error("mongo执行错误", -30);
        }
        if (ex is BadPaddingException) {
            return JsonResult.error("RSA解密错误", -40);
        }
        if (ex is AesException) {
            return JsonResult.error(ex.getMessage(ex.getCode()),-50);
        }
        if (ex is RequireException) {
            return JsonResult.error("{"+ex.key+"}"+ex.message!!.split("\u0020").get(1),-1);
        }

        return JsonResult.error(ex.message.AsString("系统错误").replace(" ",""));
    }
}