package nancal.iam.db.redis

import nbcp.comm.*
import nbcp.db.LoginUserModel
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import nbcp.utils.*
import nbcp.db.redis.proxy.*
import java.time.Duration

/**
 * Created by udi on 17-5-13.
 */


@Suppress("SpringKotlinAutowiring")
@Configuration
@ComponentScan(basePackages = arrayOf("nancal.iam.db.redis"))
open class rer {
    companion object {
        open class SystemGroup {
            fun ipCity(ip:String) =
                RedisStringProxy("ipCity:${ip}", Duration.ofDays(1).seconds.AsInt())

            fun smsCode(code:String) =
                RedisStringProxy("smsCode:${code}", Duration.ofMinutes(5).seconds.AsInt());

            fun smsResetCode(code:String) =
                RedisStringProxy("ResetSmsCode:${code}", Duration.ofMinutes(5).seconds.AsInt());

            /**
             * 客户端一次性登录码，用于换token
             */
            fun oauthCode(code:String) =
                RedisJsonProxy("oauthCode:${code}", OAuthCodeData::class.java, Duration.ofMinutes(15).seconds.AsInt());

            /**
             * 临时登录码，用于选择多租户，换取 oauthCode
             */
            fun loginCode(code:String) =
                RedisJsonProxy("loginCode:${code}", LoginCodeData::class.java, Duration.ofMinutes(15).seconds.AsInt())

            fun apiToken(token:String) =
                RedisJsonProxy("apiToken:${token}", LoginUserModel::class.java, -1);

            /**
             * 用户的token
             */
            fun oauthToken(token:String) =
                RedisJsonProxy("oauthToken:${token}", LoginUserModel::class.java, Duration.ofDays(3).seconds.AsInt(), true);

            fun freshToken(token:String) =
                RedisJsonProxy("oauthFreshToken:${token}", OAuthFreshTokenData::class.java, Duration.ofDays(3).seconds.AsInt());

            /**
             * 微信登录
             */
            fun weChatLogin(scene:String) =
                RedisJsonProxy("weChat:${scene}", WeChatLoginData::class.java, Duration.ofMinutes(30).seconds.AsInt())


            fun regexApi(number:String) = RedisSortedSetProxy("regex-api:${number}");
            fun directApi(number:String) = RedisSortedSetProxy("direct-api:${number}");

            val apiOpenNumber = RedisNumberProxy("api-open");


            private val adminTokenValue by lazy {
                return@lazy SpringUtil.context.environment.getProperty("token");
            }

        }

        internal val logger = LoggerFactory.getLogger(this::class.java.declaringClass)

        val sys by lazy { SystemGroup() }
        val iamUser by lazy { IAMUserGroup() }

        open class IAMUserGroup {
            fun errorLogin(user:String) =
                RedisStringProxy("errorLogin:${user}", Duration.ofMinutes(5).seconds.AsInt());

            fun deptCount(number:String) =
                RedisStringProxy("deptCount:${number}", Duration.ofMinutes(5).seconds.AsInt());
        }
    }
}
