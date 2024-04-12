package nancal.iam.service

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import nancal.iam.client.BaseMsgResult
import nancal.iam.client.BaseResult
import nancal.iam.client.RemoteMicroMsgClient
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.db.mongo.entity.TenantUser
import nancal.iam.db.mongo.mor
import nancal.iam.dto.LezaoMessageDTO
import nancal.iam.dto.LezaoMessageListQueryDTO
import nancal.iam.dto.MessageStatusDTO
import nbcp.comm.*
import nbcp.db.mongo.match
import nbcp.db.mongo.query
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.Serializable
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest


@Component
class MsgService {

    @Resource
    lateinit var client: RemoteMicroMsgClient

    var applicationName = "tenant"

    /**
     * 推送消息
     */
    fun save( msg: LezaoMessageDTO): ApiResult<String> {
        if (msg.userIds.isEmpty()) {
            return ApiResult.error("用户id不能为空")
        }
        if (!msg.title.HasValue) {
            return ApiResult.error("标题不能为空")
        }
        if (!msg.body.HasValue) {
            return ApiResult.error("内容不能为空")
        }
        if (msg.title.length > 128) {
            return ApiResult.error("消息标题最长为128个字符")
        }
        if (msg.body.length > 1000) {
            return ApiResult.error("消息内容最长为1000个字符")
        }
        try {
            val createMessage: BaseResult = client.createMessage(msg)
            if (createMessage.code == 0) {
                return ApiResult.of(msg.userIds.size.toString())
            }
            return ApiResult.error(createMessage.msg.toString())
        } catch (e: Exception) {
            return ApiResult.error("推送失败")
        }
    }

    class Message : Serializable {
        var id: String = ""
        var createAt: String = ""
        var createBy: String = ""
        var updateAt: String = ""
        var deleted: String = ""
        var status: String = ""
        var title: String = ""
        var applicationName: String = "tenant"
        var ruleId: String = ""
        var body: String = ""
        var type: Int = 0 // 类型 0未读 1已读
        var receivingTime: String = "" // 创建时间
        var userId: String = ""
        var mailType: String = "" // '1：产品消息   2：运维消息  3：活动消息   4 服务消息'
        var userName: String = ""
    }

    class MessageData<T> {
        var records : List<T> = listOf()
        var total : Int = 0
    }

    /**
     * 消息列表
     */
    fun queryMessageList(entity: LezaoMessageListQueryDTO, request: HttpServletRequest): ListResult<Message> {
        val loginTenantAdminUser = request.LoginTenantAdminUser
        val tenantUser: TenantUser = mor.tenant.tenantUser.query()
            .where { it.id match loginTenantAdminUser.id }.toEntity() ?: return ListResult.error("找不到数据")

        if (!entity.title.HasValue) {
            entity.title = null
        }
        try {
            val result: BaseMsgResult<MessageData<Message>> = client.queryMessageList(entity)
            if (result.code != 0) {
                return ListResult.error(result.msg.toString())
            }

            val  data = result.data!!.records
            if (data.isEmpty()) {
                return ListResult.of(mutableListOf(), data.size)
            }

            data.forEach {
                it.userName = tenantUser.tenant.name
            }
            return ListResult.of(data, result.data!!.total)
        } catch (e:Exception) {
            println(e.message)
            return ListResult.of(mutableListOf(), 0)
        }

    }

    /**
     * 全部已读
     */
    fun allRead(): ApiResult<String> {
        val param = LezaoMessageListQueryDTO()
        param.pageSize = Int.MAX_VALUE

        val result: BaseMsgResult<MessageData<Message>> = client.queryMessageList(param)
        if (result.code != 0) {
            return ApiResult.error(result.msg.toString())
        }
        if (result.data!!.records.isEmpty()) {
            return ApiResult.error("成功")
        }
        val ids: List<String> = result.data!!.records.map { it.id }.toList()
        if (ids.isEmpty()) {
            return ApiResult.of("成功")
        }
        val readParam = MessageStatusDTO()
        readParam.ids = ids
        readParam.applicationName = applicationName
        val res: BaseResult = client.updateMessageStatus(readParam)
        if (res.code != 0) {
            return ApiResult.error(res.msg.toString())
        }
        return ApiResult.of("成功")
    }

    /**
     * 消息详情
     */
    fun detail(id: String,request: HttpServletRequest): ApiResult<Message> {
        val loginTenantAdminUser = request.LoginTenantAdminUser
        val tenantUser: TenantUser = mor.tenant.tenantUser.query()
            .where { it.id match loginTenantAdminUser.id }.toEntity() ?: return ApiResult.error("找不到数据")

        val res: BaseMsgResult<Message> = client.messageDetail(id)
        if (res.code != 0) {
            return ApiResult.error(res.code.toString())
        }
        if (!res.data.toString().HasValue) {
            return ApiResult.error("查询无数据")
        }
        val  data = res.data
        data!!.userName = tenantUser.tenant.name
        return ApiResult.of(data)
    }

    fun jsonToObject(json: String): Message {
        val objectMapper = ObjectMapper()
        return objectMapper.readValue(json, Message::class.java)
    }

    fun json2List(json: String): MutableList<Message> {
        val objectMapper = ObjectMapper()
        val javaType1: JavaType = objectMapper.typeFactory.constructParametricType(
            ArrayList::class.java,
            Message::class.java
        )
        return objectMapper.readValue(json, javaType1)
    }

}