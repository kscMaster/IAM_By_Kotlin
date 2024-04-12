package nancal.iam.db.redis.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import nbcp.db.CodeName
import nbcp.db.IdName
import nbcp.db.CityCodeName
import java.util.*
import java.time.LocalDateTime

/**
 * Created by udi on 17-5-13.
 */


//abstract class BaseSessionData(
//        var user: IdName = IdName(),
//        var city: CodeName = CodeName()
//) : java.io.Serializable {}

//品质信企业用户
class PzxSessionData(
        private val userId: String = "",
        private val userName: String = "",
        var permissions: MutableList<String> = mutableListOf(),
        var city: CityCodeName = CityCodeName()
) : IdName(userId, userName) {}


//品质信企业用户
class OpenSessionData(
        private val userId: String = "",
        private val userName: String = "",
        city: CodeName = CodeName()
) : IdName(userId, userName) {}

//金维度用户
class JwdSessionData(
        private val userId: String = "",
        private val userName: String = "",
        var isAdmin:Boolean = false,
        var permissions: MutableList<String> = mutableListOf(),
        var city: CityCodeName = CityCodeName()
) : IdName(userId, userName) {}


