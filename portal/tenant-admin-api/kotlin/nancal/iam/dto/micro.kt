package nancal.iam.dto

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import nancal.iam.db.mongo.TenantMailType
import nancal.iam.db.mongo.TenantMessageType

class LezaoMessageDTO {
    /**
     * 应用名称
     */
    var applicationName: String? = "tenant"

    /**
     * 标题
     */
    var title: String = ""

    /**
     * 内容
     */
    var body: String = ""

    /**
     * 目标群组 用户组id 默认所有用户
     */
    var userIds: List<String> = mutableListOf()

    /**
     * '1:站内信   2:邮件   3:app推送   4:短信'
     */
    var msgType: Int = TenantMessageType.TenantMail.type

    /**
     * 站内信消息类型'1：产品消息   2：运维消息  3：活动消息   4 服务消息'
     */
    var mailType: Int = TenantMailType.DevOps.type
}

class LezaoMessageListQueryDTO{
    /**
     * 页码
     */
    @JsonAlias("pageNumber")
    var pageNo: Int? = 1

    /**
     * 页大小
     */
    var pageSize: Int? = 10

    /**
     * 通知名称
     */
    var title: String? = null

    /**
     * 应用名称
     */
    var applicationName: String? = "tenant"

    /**
     * 0未读 1已读   不穿查询所有
     */
    var type: Int? = null
}

class MessageStatusDTO {
    var applicationName : String = "tenant"
    var ids : List<String> = mutableListOf()
}

class IdsDTO(var ids: List<String?>? = ArrayList())