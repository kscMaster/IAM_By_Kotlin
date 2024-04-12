package nancal.iam.mvc.tenant

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nbcp.comm.*
import org.bson.Document
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

/**
 * Created by CodeGenerator at 2021-11-17 17:43:16
 */
@Api(description = "租户相关接口", tags = ["Tenant"])
@RestController
@RequestMapping("/tenant")
class TenantController {

    @ApiOperation("获取租户详情")
    @GetMapping("/detail")
    fun detail(
        request: HttpServletRequest
    ): ApiResult<Document> {

        val tenant = mor.tenant.tenant.query()
            .where { it.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity() ?: Tenant()
        val ts=mor.tenant.tenantSecretSet.queryByTenantId(request.LoginTenantAdminUser.tenant.id ).toEntity()
       val setting= ts?.setting
        if (setting?.selfSetting == null) {
            setting?.selfSetting = SelfSetting()
        }
        val res=tenant.ConvertJson(Document::class.java)
        res["setting"] = setting
        return ApiResult.of(res)
    }

    @PostMapping("/destroy-tenant")
    fun deleteTenant(
        request: HttpServletRequest
    ): JsonResult {
        val loginTenantAdminUser = request.LoginTenantAdminUser ?: return JsonResult.error("登录人信息不存在")
        val list = mor.tenant.getEntities().map { it as MongoBaseMetaCollection<*> }
            .filterNot { it.tableName == mor.tenant.tenant.tableName }
            .filterNot { it.tableName == mor.tenant.tenantLoginUser.tableName }
        list.forEach {
            it.delete()
                .where(MongoColumnName("tenant.id") match loginTenantAdminUser.tenant.id)
                .exec()
        }
        mor.tenant.tenantLoginUser.delete().where { it.tenant.id match loginTenantAdminUser.tenant.id }.exec()
        mor.tenant.tenant.deleteById(loginTenantAdminUser.tenant.id).exec()
        return JsonResult()
    }





}
