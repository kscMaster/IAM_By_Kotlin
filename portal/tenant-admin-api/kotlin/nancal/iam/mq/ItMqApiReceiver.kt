package nancal.iam.mq

import com.nancal.entity.log.SysLog
import com.nancal.log.sender.MqSender
import com.rabbitmq.client.Channel
import nancal.iam.db.mongo.AccessLevelEnum
import nancal.iam.db.mongo.ResourceTypeEnum
import nancal.iam.db.mongo.entity.TenantResourceInfo
import nancal.iam.db.mongo.mor
import nancal.iam.util.CodeUtils
import nbcp.comm.FromJson
import nbcp.comm.HasValue
import nbcp.db.mongo.*
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.annotation.Resource

/**
 *@Author shyf
 * @Date 2022/06/10
 * @Description it容器侧数据消费api上架同步
 * channel.basicNack(deliveryTag, false, false) // ack返回false，requeue-true并重新回到队列
 * channel.basicAck(deliveryTag, false); 消息的标识，false只确认当前一个消息收到，true确认所有consumer获得的消息（成功消费，消息从队列中删除
 * channel!!.basicReject(deliveryTag, false) true 就重新放回队列 false 丢弃或者死信队列
 **/
@Component
@RabbitListener(queues = ["IT_IAM_API_STATUS_QUEUE"])
class ItMqApiReceiver  {

    class ItMessage{
        var appCode :String = ""
        var code :String = ""  // 资源code
        var name :String = ""   // 资源名称
        var userId :String = ""
        var resource :String = ""  // api 地址
        var status :String = ""   // 0 下架 1 上架
        var tenantId :String = "" // 租户id
    }

    @Resource
    lateinit var mqSender: MqSender

    @RabbitHandler
    fun onMessage(msg : String, message: Message?, channel: Channel?) {
        println("请求参数是${msg}")
        println("====================IT容器同步API上线数据开始===================================")

        val deliveryTag = message!!.messageProperties.deliveryTag
        try {
            val entity: ItMessage = msg.FromJson(ItMessage::class.java) ?: throw RuntimeException("传递参数错误")
            //            entity.tenantId = "6281c3c43edf677dbaf67c85" 测试账号使用
            val data : MutableList<TenantResourceInfo> = mutableListOf()
            println("解析后的参数是$data")
            if (entity.appCode.HasValue) { // producer 以 appcode ,切分匹配多个应用
                entity.appCode.split(",").forEach { appCode ->
                    val vo = TenantResourceInfo()
                    vo.appInfo.code = appCode
                    mor.tenant.tenantApplication.query()
                        .where { it.appCode match appCode }
                        .where { it.tenant.id match entity.tenantId }
                        .toEntity()
                        .apply {
                            if (this == null) {
                                throw RuntimeException("租户下该应用不存在${appCode}")
                            }
                            vo.appInfo.name = this.name
                        }
                    vo.code = entity.code
                    vo.name = entity.name
                    vo.type = ResourceTypeEnum.Api
                    vo.resource = entity.resource
                    vo.tenant.id = entity.tenantId
                    vo.remark = "IT容器侧上架API"
                    vo.dataAccessLevel = AccessLevelEnum.None
                    data.add(vo)
                }

            }
            data.forEach{
                println("ItMqAckReceiver appcode:${entity.appCode}  code:${entity.code}  tenantId:${entity.tenantId}")
                // status == 0 下架 1 上架
                if (entity.status == "1") {
                    saveTenantResource(it)
                } else {
                    delByCode(it.code, it.tenant.id)
                }

            }
            println("====================消费的主题消息来自：" + message.messageProperties.consumerQueue)
            channel!!.basicAck(deliveryTag, false)
            println("====================IT容器同步上线数据成功===================================")
        } catch (e : Exception) {
            println("====================IT容器同步异常记录日志===================================")
            val entity: ItMessage? = msg.FromJson(ItMessage::class.java)
            val log = SysLog()
            log.createAt = LocalDateTime.now()
            log.msg = e.message
            log.data = entity
            log.creator.id = entity!!.userId
            // 记录日志
            mqSender.sendLog(
                "mp-iam-sys-log",
                log
            )
            println("====================IT容器同步异常记录日志结束===================================")
            channel!!.basicAck(deliveryTag, false) // 不再放入队列处理
            println("====================IT容器同步上线数据失败记录日志===================================")
        }
    }

    /**
     * 下架删除
     */
    fun delByCode(code:String, tenantId:String){
        val toEntity = mor.tenant.tenantResourceInfo.query()
            .where { it.code match code }
            .where { it.tenant.id match tenantId }
            .toEntity()
        toEntity ?: return
        // 删除子资源
        val delIds: List<String> = mor.tenant.tenantResourceInfo.query()
            .where {
                it.appInfo.code match toEntity.appInfo.code }
            .apply {
                this.where { it.code match_pattern "^${toEntity.code + ":"}" }
            }.toListResult().data.map { it.id }
        // 删除子资源
        delResource(delIds as MutableList<String>, tenantId)

        val ids: MutableList<String> = mutableListOf()
        ids.add(toEntity.id)
        // 删除资源
        delResource(ids, tenantId)
    }

    /**
     * 子资源删除
     */
    fun delResource(ids: MutableList<String>, tenantId: String){
        if (ids.isEmpty()) {
            return
        }
        mor.tenant.tenantResourceInfo.delete()
            .where { it.id match_in ids }
            .where { it.tenant.id match tenantId }
            .where { it.isSysDefine match false }
            .exec()
            .apply {
                if (this == 0) {
                    throw RuntimeException("下架的非系统资源不存在")
                }
                mor.tenant.tenantResourceInfo.query()
                    .where { it.id match_in ids }
                    .select { it.name }
                    .toList(String::class.java)
                mor.tenant.tenantAppAuthResourceInfo.query()
                    .where { it.auths.resourceId match_in ids }.toList()
                    .apply {
                        this.forEach {
                            it.auths.removeAll { it.resourceId in ids }
                            mor.tenant.tenantAppAuthResourceInfo.updateWithEntity(it).execUpdate()
                            if (it.auths.size == 0) {
                                mor.tenant.tenantAppAuthResourceInfo.deleteById(it.id).exec()
                            }
                        }
                    }
            }
    }

    /**
     * 上架
     */
    fun saveTenantResource(entity: TenantResourceInfo)  {
        entity.code.split(":").toMutableList().forEach{
            if (!it.HasValue) {
                throw RuntimeException("code不能包含::或:开头结尾")
            }
        }
        val codeList = CodeUtils.codeHolder(entity.code)
        if (codeList.size > 5) {
            throw RuntimeException("code层级最多五层")
        }
        if(entity.name.isEmpty()){
            throw RuntimeException("资源名称不能为空")
        }else if(entity.name.length>300){
            throw RuntimeException("资源名称长度不能大于300")
        }
        if(entity.code.isEmpty()){
            throw RuntimeException("code不能为空")
        }else if(entity.code.length>120){
            throw RuntimeException("code长度不能大于120")
        }

        mor.tenant.tenantResourceInfo.query()
            .where { it.appInfo.code match entity.appInfo.code }
            .where { it.tenant.id match entity.tenant.id }
            .where { it.code match entity.code }
            .toEntity()
            .apply {
                if(this != null){
                    throw RuntimeException("租户应用下该资源已存在${entity.code}")
                }
            }
        val code = entity.code
        val id = entity.id
        // 子资源补齐
        if (codeList.size > 1) { // 子资源父级补齐
            codeList.removeAt(codeList.size - 1)
            codeList.forEach { item ->
                entity.id = "" // clean上次赋值的id
                entity.code = item // 当前层级的code
                entity.type = ResourceTypeEnum.Data // 默认数据类型
                mor.tenant.tenantResourceInfo.query()
                    .where { it.appInfo.code match entity.appInfo.code }
                    .where { it.code match item }
                    .toEntity()
                    .apply {
                        if (this == null) { // 父资源不存在
                            mor.tenant.tenantResourceInfo.doInsert(entity)
                        }
                    }
            }
        }
        // 当前API添加
        entity.code = code
        entity.id = id
        entity.type = ResourceTypeEnum.Api
        mor.tenant.tenantResourceInfo.updateWithEntity(entity)
            .run {
                if (entity.id.HasValue) {
                    return@run this.execUpdate()
                } else {
                    return@run this.execInsert()
                }
            }
    }

}