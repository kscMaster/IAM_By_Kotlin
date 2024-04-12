package nancal.iam.service

import nancal.iam.db.mongo.AuthTypeEnum
import nancal.iam.db.mongo.DataActionEnum
import nancal.iam.db.mongo.ResourceTypeEnum
import nancal.iam.db.mongo.entity.TenantAppAuthResourceInfo
import nancal.iam.db.mongo.entity.tenant.authsource.TenantAuthUpdateDetail
import nancal.iam.db.mongo.entity.tenant.authsource.TenantAuthUpdateDetailLastTime
import nancal.iam.db.mongo.mor
import nancal.iam.db.redis.rer
import nancal.iam.service.extra_auth.BaseAuthResourceService
import nbcp.comm.*
import nbcp.db.mongo.*
import nbcp.utils.MyUtil
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
internal class AuthStandardTask {
    @Scheduled(fixedDelay = 1000)
    fun authStandardTask() {
        var latestAt = LocalDate.now().atStartOfDay()
        var latestAtTemp = LocalDateTime.now()
        var hasFlag = false
        // 最后一次更新时间
        var lastTime = mor.tenant.tenantAuthUpdateDetailLastTime.query()
            .orderByDesc { it.createAt }
            .toEntity()
            .apply {
                if (this != null) {
                    latestAt = this.updateAt
                    hasFlag = true
                }
            }
//            ?.createAt ?: LocalDate.now().atStartOfDay()

        val baseAuthResourceService = BaseAuthResourceService()

        val toList = mor.tenant.tenantAuthUpdateDetail.query()
            .where { it.createAt match_gte latestAt }
            .orderByAsc { it.createAt }
            .toList()

        try {
            toList.forEach {
                val authObj = it.ToJson().FromJson<TenantAppAuthResourceInfo>()
                    .must()
                    .elseThrow { "授权转换错误" }


                val type = MyUtil.getValueByWbsPath(it, "type").AsString()
                if (type != AuthTypeEnum.Dept.name) {
                    var auths = mor.tenant.tenantAppAuthResourceInfo.query()
                        .where { it.type match type }
                        .where { it.target.id match authObj.target.id }
                        .where { it.tenant.id match authObj.tenant.id }
                        .where { it.appInfo.code match authObj.appInfo.code }
                        .orderByDesc { it.updateAt }
                        .toList()
                        .map { it.auths }
                        .Unwind()

                    authObj.auths.removeAll(authObj.auths)
                    authObj.auths.addAll(auths)
                } else {
                    baseAuthResourceService.deptBetterControl(type, authObj, baseAuthResourceService, false)
                    return
                }
                baseAuthResourceService.betterControlAuthResource(authObj, type, mutableListOf())
            }
        } catch (e: Exception) {
            //更新失败处理
        } finally {
            if (!hasFlag) {
                var tenantAuthUpdateDetailLastTime = TenantAuthUpdateDetailLastTime()
                tenantAuthUpdateDetailLastTime.createAt = latestAt
                mor.tenant.tenantAuthUpdateDetailLastTime.doInsert(tenantAuthUpdateDetailLastTime)
            } else {
                if (lastTime != null && toList.isNotEmpty()) {
                    lastTime.createAt = latestAtTemp
                    mor.tenant.tenantAuthUpdateDetailLastTime.updateWithEntity(lastTime).execUpdate()
                }
            }
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    fun clearTask() {
        var latestAt = LocalDate.now().atStartOfDay()
        mor.tenant.tenantAuthUpdateDetail.delete()
            .where { it.createAt match_lte latestAt }.exec()


    }

    var apiOpen = 0;
    private fun getWorkingNumber(): Int {
        if (apiOpen == 0) return 1;
        return 0;
    }

    private fun switch() {
        if (rer.sys.apiOpenNumber.get() == 0L) {
            apiOpen = 1;
            rer.sys.apiOpenNumber.set(apiOpen.toLong())

            rer.sys.directApi(getWorkingNumber().toString()).deleteKey()
            rer.sys.regexApi(getWorkingNumber().toString()).deleteKey()
        } else {
            apiOpen = 0;
            rer.sys.apiOpenNumber.set(apiOpen.toLong())

            rer.sys.directApi(getWorkingNumber().toString()).deleteKey()
            rer.sys.regexApi(getWorkingNumber().toString()).deleteKey()
        }
    }

    @Scheduled(fixedDelay = 2000)
    fun syncAppApiResource() {
        switch()

        var directIndex = -1.0;
        var regexIndex = -1.0;
        BatchReader.init { skip, take ->
            mor.tenant.tenantResourceInfo.aggregate()
                .beginMatch()
                .where { it.type match ResourceTypeEnum.Api }
                .endMatch()
                .group("\$resource", JsonMap("sum" to JsonMap("\$sum" to 1)))
                .limit(skip, take)
                .toMapList()
        }
            .forEach { resource ->

                var isRegex = resource.contains("{")

                if (isRegex) {
                    regexIndex++;
                    rer.sys.regexApi(getWorkingNumber().toString()).add(resource.getString("id"), regexIndex);
                } else {
                    directIndex++;
                    rer.sys.directApi(getWorkingNumber().toString()).add(resource.getString("id"), directIndex);
                }
            }
    }
}