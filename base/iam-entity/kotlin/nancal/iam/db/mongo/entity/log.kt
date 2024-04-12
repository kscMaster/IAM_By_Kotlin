package nancal.iam.db.mongo.entity

import nbcp.db.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime



@Document
@DbEntityGroup("log")
data class CorpLoginLog(
    var loginName: String = "",
    var password: String = "", //有密码，表示登录失败
    var app: String = "", //Header 中的 App。
    var clientIp: String = "",
    var client: String = "",
    var remark: String = ""
) : BaseEntity()


@Document
@DbEntityGroup("log")
data class MqLog(
    var name: String = "",
    var arrivedAt: LocalDateTime? = null,      //到达Exchange时间
    var body: String = "",  //消息内容。
    var sendErrorMessage: String = "", //消息未到达的消息.
    var isDone: Boolean = false, //是否完成。
    var consumeAt: LocalDateTime? = null,
    var result: String = ""    //消费结果，成功为空。
) : BaseEntity()


@Document
@DbEntityGroup("log")
data class SmsLog(
    var mobile: String = "",
    var validateCode: String = "",
    var clientIp: String = "",
    var used: Boolean = false,
    var usedAt: LocalDateTime? = null
) : BaseEntity()


@Document
@DbEntityGroup("log")
data class MobileCodeLog(
    var module: MobileCodeModuleEnum? = null,        //使用模块
    var mobile: String = "",
    var templateCode: String = "",
    var param: String = "",

    var bizId: String = "",          // 下发的回执Id.用它来查询状态.,有它表示发过.
    var errorMessage: String = "",  //下发的错误消息

    var sentAt: LocalDateTime? = null,      // 下发时间
    var arrivedAt: LocalDateTime? = null,   //到达时间

) : BaseEntity() {
}



@Document
@DbEntityGroup("log")
data class TraceLog(
    var method: String = "",
    var url: String = "",
    var header: LinkedHashMap<String, String> = linkedMapOf(),
    var body: String = "",   //请求体
    var remark: String = "",    //分析结果
    var user: IdName = IdName()
) : BaseEntity()

