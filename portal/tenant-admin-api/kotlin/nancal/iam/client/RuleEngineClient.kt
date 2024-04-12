package nancal.iam.client

import nbcp.comm.JsonResult
import nancal.iam.db.mongo.MobileCodeModuleEnum
import nancal.iam.mvc.tenant.AppAuthResourceInfoAutoController
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

//@FeignClient(name = "192.168.5.213/api/mp-rules-engine",  fallback = MPClientFallbackFactory::class)
@FeignClient(name = "mp-rules-engine",  fallback = MPClientFallbackFactory::class)
interface RuleEngineClient {

    @GetMapping("/sys/sysCode/getSysCode")
    fun getSysCode(): BaseResult


    // 授权规则添加到规则引擎
    @PostMapping("/rules/condition/addOrEditCondition")
    fun addConditionValue(insertRuleResult  :MutableList<AppAuthResourceInfoAutoController.RuleInsertVo>): BaseResult

    // 删除
    @PostMapping("/rules/condition/deleteConditionByResourceIds")
    fun deleteConditionValue(deleteIds  :MutableList<String>): BaseResult

}
