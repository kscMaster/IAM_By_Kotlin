package nancal.iam.client

import nancal.iam.dto.IdsDTO
import nancal.iam.dto.LezaoMessageDTO
import nancal.iam.dto.LezaoMessageListQueryDTO
import nancal.iam.dto.MessageStatusDTO
import nancal.iam.service.MsgService
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(
    contextId = "RemoteMicroMsgService",
    name = "it-msg-system",
//    url = "http://192.168.5.213/api/it-msg-system",
    fallbackFactory = MPClientFallbackFactory::class
)
interface RemoteMicroMsgClient {

    /**
     * 添加站内信消息推送任务
     *
     * @param lezaoMessageDTO
     * @return
     */
    @PostMapping("/message/add")
    fun createMessage(@RequestBody lezaoMessageDTO: LezaoMessageDTO?): BaseResult

    /**
     * 任务列表
     *
     * @param condition 查询条件
     * @return
     */
    @PostMapping("/message/list",produces = ["application/json;charset=utf8"])
    fun queryMessageList(@RequestBody condition: LezaoMessageListQueryDTO?): BaseMsgResult<MsgService.MessageData<MsgService.Message>>


    /**
     * 删除站内信单个和批量
     */
    @PostMapping(path = ["/message/delete"])
    fun deleteByIds(@RequestBody  ids: IdsDTO): BaseResult

    /**
     * 修改站内信状态单个和批量  状态
     */
    @PostMapping(path = ["/message/set"], produces = ["application/json;charset=utf8"])
    fun updateMessageStatus(@RequestBody condition:MessageStatusDTO): BaseResult


    /**
     * 获取未读信件数量
     */
    @RequestMapping(path = ["/message/unread/list"], method = [RequestMethod.GET])
    fun messageUnreadList(@RequestParam(value = "applicationName") applicationName: String?): BaseResult


    /**
     * 消息详情
     */
    @RequestMapping(path = ["/message/details"], method = [RequestMethod.GET])
    fun messageDetail(@RequestParam(value = "id") id: String?): BaseMsgResult<MsgService.Message>


    /**
     * 获取各个信件数量 TODO 暂时不需要
     */
    @RequestMapping(path = ["/message/detail"], method = [RequestMethod.GET])
    fun messageDetailById(@RequestParam(value = "applicationName") applicationName: String?, @RequestParam(value = "type") type:String): BaseResult

}