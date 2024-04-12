package nancal.iam.mvc.tenant;

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import nbcp.comm.ApiResult
import nbcp.comm.ConvertJson
import nbcp.comm.ToJson
import nbcp.db.IdName
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import org.bson.Document
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest


/**
 * @Author wrk
 * @Description
 * @Date 2021/12/28-14:30
 */
@Api(description = "tenant", tags = arrayOf("Tenant"))
@RestController
@RequestMapping("/admin/tenant")
public class DataTransferController {
    @ApiOperation("数据迁移")
    @PostMapping("/transferTenantSettingData")
    fun transferTenantSettingData(
        request: HttpServletRequest
    ): ApiResult<String> {
        var alreadHasIds = mor.tenant.tenantSecretSet.query().toList().map { it.tenant.id }
        var tenantList = mor.tenant.tenant.query().where{it.id match_notin alreadHasIds}.toList(Document::class.java)
        var tssList: MutableList<TenantSecretSet> = mutableListOf()
        tenantList.forEach { tenant ->
            var tenantSecretSet: TenantSecretSet = TenantSecretSet()
            var keyStore = com.nancal.cipher.RSARawUtil.create()
            tenantSecretSet.publicSecret = keyStore.publicKeyString
            tenantSecretSet.sysPrivateSecret = keyStore.privateKeyString
            var setting = tenant.get("setting")
            if (setting != null) {
                tenantSecretSet.setting = setting.ConvertJson(TenantSetting::class.java)
            }else{
                tenantSecretSet.setting =createSetting()
            }
            tenantSecretSet.tenant = IdName(tenant.getString("id"), tenant.getString("name"))
            tssList.add(tenantSecretSet)
        }
        var count=0
        var fail:MutableList<Map<String,Any>> = mutableListOf()
        tssList.forEach {
                tss->
            var has= mor.tenant.tenantSecretSet.query().where { it.tenant.id match tss.tenant.id }.exists()
            if(!has){
                //插入
                mor.tenant.tenantSecretSet.doInsert(tss)
                if(mor.affectRowCount>0){
                    count++
                }else{
                    fail.add(mapOf<String,Any>("tenant" to tss.tenant))
                }
            }
        }
        return ApiResult.of("{${tenantList.size}}个租户设置需要迁移，{${count}}个租户设置迁移成功 【失败信息】 ${fail.ToJson()}")

    }
    fun createSetting(): TenantSetting {
        val ts = TenantSetting()
        ts.selfSetting = SelfSetting()
        return ts
    }
}
