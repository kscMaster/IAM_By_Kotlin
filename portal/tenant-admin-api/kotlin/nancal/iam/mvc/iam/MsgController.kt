package nancal.iam.mvc.iam

import io.swagger.annotations.ApiOperation
import nancal.iam.client.BaseResult
import nancal.iam.client.RemoteMicroMsgClient
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.dto.IdsDTO
import nancal.iam.dto.LezaoMessageDTO
import nancal.iam.dto.LezaoMessageListQueryDTO
import nancal.iam.dto.MessageStatusDTO
import nancal.iam.service.MsgService
import nbcp.comm.*
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

@OpenAction
@RestController
@RequestMapping("/tenant/message")
class MsgController {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    var applicationName = "tenant"

    @Resource
    lateinit var client: RemoteMicroMsgClient

    @Resource
    lateinit var msgService: MsgService

    @ApiOperation("添加站内信")
    @PostMapping("/save")
    fun save(@JsonModel entity: LezaoMessageDTO): ApiResult<String> {
//        entity.userIds = mutableListOf(request.LoginTenantAdminUser.id)
        return msgService.save(entity)
    }

    @ApiOperation("站内信已读未读状态查询")
    @PostMapping("/list")
    fun list(@JsonModel entity: LezaoMessageListQueryDTO, request: HttpServletRequest): ListResult<MsgService.Message> {

        return msgService.queryMessageList(entity, request)
    }

    @ApiOperation("全部已读")
    @PostMapping("/allRead")
    fun allRead(request: HttpServletRequest):ApiResult<String> {
       return msgService.allRead()
    }

    @ApiOperation("批量删除消息")
    @PostMapping("/delBath")
    fun delete(
        @Require ids: List<String>, request: HttpServletRequest
    ): ApiResult<Int> {
        if (ids.isEmpty()) {
            return ApiResult.error("ids不能为空")
        }
        val param = IdsDTO(ids)
        val res: BaseResult = client.deleteByIds(param)
        if (res.code != 0) {
            return ApiResult.error(res.msg.toString())
        }
        return ApiResult.of(ids.size)
    }

    @ApiOperation("修改站内信状态单个和批量 已读")
    @PostMapping("/updateStatus")
    fun update(@Require ids: List<String>, request: HttpServletRequest): ApiResult<Int> {
        if (ids.isEmpty()) {
            return ApiResult.error("ids不能为空")
        }
        val condition = MessageStatusDTO()
        condition.ids = ids
        condition.applicationName = applicationName
        val res: BaseResult = client.updateMessageStatus(condition)
        if (res.code != 0) {
            return ApiResult.error(res.msg.toString())
        }
        return ApiResult.of(ids.size)
    }

    @ApiOperation("获取未读信件数量")
    @PostMapping("/count")
    fun count(request: HttpServletRequest): ApiResult<Int> {
        if (!request.LoginTenantAdminUser.id.HasValue) {
            return ApiResult.error("找不到用户登录信息")
        }
        val res: BaseResult = client.messageUnreadList(applicationName)
        if (res.code != 0) {
            return ApiResult.error(res.msg.toString())
        }
        return ApiResult.of(res.data.toString().toInt())
    }

    @ApiOperation("详情")
    @PostMapping("/detail")
    fun detail(@Require id:String, request: HttpServletRequest): ApiResult<MsgService.Message> {
        return msgService.detail(id, request)
    }

}
