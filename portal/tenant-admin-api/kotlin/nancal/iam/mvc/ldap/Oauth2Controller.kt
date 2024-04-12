package nancal.iam.mvc.ldap

import cn.hutool.core.lang.Assert
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import nancal.iam.annotation.BizLog
import nancal.iam.annotation.CheckTenantAppStatus
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nancal.iam.db.mongo.BizLogActionEnum
import nancal.iam.db.mongo.BizLogResourceEnum
import nancal.iam.db.mongo.entity.IdentitySource
import nancal.iam.db.mongo.entity.IdentityTypeList
import nancal.iam.db.mongo.entity.TenantIdentitySourceApp
import nancal.iam.db.mongo.entity.ldap.IdentityOauthSource
import nancal.iam.db.mongo.mor
import nbcp.comm.*
import nbcp.db.mongo.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest


@CheckTenantAppStatus
@Api(description = "oauth2.0")
@RestController
@RequestMapping("/iam/oauth")
class Oauth2Controller {


    @BizLog(BizLogActionEnum.Save, BizLogResourceEnum.OrganizationIdentity, "企业身份源新增、修改")
    @ApiOperation("新增、更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: IdentityOauthSource, request: HttpServletRequest
    ): ApiResult<String> {

        if (entity.id.HasValue) {
            request.logMsg = "企业身份源{${entity.name}}修改"
        } else {
            request.logMsg = "企业身份源{${entity.name}}新增"
        }

        //鉴权
        val loginTenantAdminUser = request.LoginTenantAdminUser
        entity.tenant = loginTenantAdminUser.tenant

        // 参数校验
        checkParam(entity)
        var isInsert = false

        mor.iam.identityOauthSource.updateWithEntity(entity).run {
            if (entity.id.HasValue) {
                return@run this.execUpdate()
            } else {
                isInsert = true
                return@run this.execInsert()
            }
        }.apply {
            if (this == 0) {
                return ApiResult.error("更新失败")
            }
            // 新增时，修改列表ldap类型状态
            if (isInsert) {

                mor.iam.identityTypeList.query()
                    .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                    .toEntity()
                    .apply {
                        if (this == null) {
                            val identityTypeList = IdentityTypeList()
                            // 不存在，就新增
                            identityTypeList.tenant = request.LoginTenantAdminUser.tenant
                            identityTypeList.oauth = true
                            mor.iam.identityTypeList.doInsert(identityTypeList)
                        } else {
                            mor.iam.identityTypeList.update()
                                .where { it.tenant.id match loginTenantAdminUser.tenant.id }
                                .set { it.oauth to true }
                                .exec()
                        }
                    }

                val appList: MutableList<TenantIdentitySourceApp> = mutableListOf()
                // 查询租户的所有应用，关联ldap
                mor.tenant.tenantApplication.query()
                    .where { it.tenant.id match loginTenantAdminUser.tenant.id }
                    .toList()
                    .apply {
                        this.forEach {
                            val obj = TenantIdentitySourceApp()
                            obj.id = it.id
                            obj.codeName.code = it.appCode
                            obj.codeName.name = it.name
                            obj.logo = it.logo
                            obj.status = false
                            obj.tenantAppStatus = it.enabled
                            obj.isSysDefine = false
                            // 总应用开关控制
                            mor.iam.sysApplication.query()
                                .where { it.appCode match obj.codeName.code }.toEntity()
                                .apply {
                                    if (this != null) {
                                        obj.sysAppStatus = this.enabled
                                        obj.sysAppId = this.id
                                        obj.isSysDefine = true
                                    }
                                }

                            appList.add(obj)
                        }
                    }
                if (appList.isNotEmpty()) {
                    entity.tenantApps = appList
                    mor.iam.identityOauthSource.updateWithEntity(entity).execUpdate()
                }

            }
            return ApiResult.of(entity.id)
        }
    }

    fun checkParam(entity: IdentityOauthSource) {
        Assert.isFalse(!entity.code.HasValue, "唯一标识符不能为空")
        Assert.isFalse(!entity.name.HasValue, "显示名称不能为空")
        Assert.isFalse(!entity.url.HasValue, "授权url不能为空")
        Assert.isFalse(!entity.tokenUrl.HasValue, "tokenUrl不能为空")
        Assert.isFalse(!entity.clientSecurity.HasValue, "clientSecurity不能为空")
        Assert.isFalse(!entity.clientId.HasValue, "clientId不能为空")
        Assert.isFalse(!entity.loginType.name.HasValue, "登录模式不能为空")

        Assert.isFalse(entity.code.length > 32, "唯一标识符长度为1~32")
        Assert.isFalse(entity.name.length < 2 || entity.code.length > 20, "显示名称长度为2-20个")
        Assert.isFalse(entity.url.length > 250, "授权url最大长度为250")
        Assert.isFalse(entity.clientId.length > 250, "clientId最大长度为250")
        Assert.isFalse(entity.clientSecurity.length > 250, "clientSecurity最大长度为250")

        val toEntity = mor.iam.identityOauthSource.query()
            .where { it.code match entity.code }
            .where { it.tenant.id match entity.tenant.id }
            .toEntity()
        if (entity.id == "") { // 新增
            // 判断唯一标识符是否存在
            Assert.isFalse(toEntity != null, "唯一标识符已存在,无法新增")
            val exist = mor.iam.identityOauthSource.query().where { it.tenant.id match entity.tenant.id }.toEntity()
            Assert.isFalse(exist != null, "oauth2.0身份源已存在，无法新增")
        } else { // 修改
            val updateEntity = mor.iam.identityOauthSource.query().where { it.id match entity.id }.toEntity()
            Assert.isFalse(updateEntity != null && entity.code != updateEntity.code, "oauth2.0身份源已存在，无法新增")
            Assert.isFalse(updateEntity == null, "修改数据不存在")
            if (entity.tenantApps.size > 0) {
                entity.tenantApps.forEach { app ->
                    mor.tenant.tenantApplication.query()
                        .where { it.appCode match app.codeName.code }
                        .where { it.tenant.id match entity.tenant.id }
                        .toEntity()
                        .apply {
                            Assert.isTrue(this == null, "有应用不属于该租户，请核对")
                        }
                }

            }
        }
        if (toEntity != null) {
            Assert.isFalse(toEntity.code == entity.code && entity.id.isEmpty(), "唯一标识符已存在,无法新增")
            Assert.isFalse(toEntity.code == entity.code && toEntity.id != entity.id, "唯一标识符已存在,无法新增")
        }
    }


    @ApiOperation("详情")
    @PostMapping("/detail")
    fun detail(
        request: HttpServletRequest
    ): ApiResult<IdentityOauthSource> {
        mor.iam.identityOauthSource.query()
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }.toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }
                return ApiResult.of(this)
            }
    }


    @BizLog(BizLogActionEnum.Enable, BizLogResourceEnum.OrganizationIdentity, "企业身份源启用")
    @ApiOperation("身份源应用的启用")
    @PostMapping("/enable")
    fun enabled(
        @Require id: String, @Require appCode: String, request: HttpServletRequest
    ): JsonResult {
        val tenantId=request.LoginTenantAdminUser.tenant.id
        val msg = "启用"
        request.logMsg = "企业身份源应用{${appCode}}"+msg

        mor.iam.identityOauthSource.query()
            .where { it.id match id }
            .where { it.tenantApps.codeName.code match appCode }
            .where { it.tenant.id match tenantId }
            .toEntity().apply {
                if (this == null) {
                    return JsonResult.error("找不到数据")
                }
                this.tenantApps.forEach {
                    if (it.codeName.code == appCode) {
                        if(it.tenantAppStatus){
                            it.status = true
                        }else{
                            return JsonResult.error("应用已被禁用")
                        }

                    }
                }
                mor.iam.identityOauthSource.updateWithEntity(this).execUpdate()
            }
        return JsonResult()
    }
    @BizLog(BizLogActionEnum.Disable, BizLogResourceEnum.OrganizationIdentity, "企业身份源停用")
    @ApiOperation("身份源应用的停用")
    @PostMapping("/disable")
    fun disabled(
        @Require id: String, @Require appCode: String, request: HttpServletRequest
    ): JsonResult {
        val tenantId=request.LoginTenantAdminUser.tenant.id
        val  msg = "停用"
        request.logMsg = "企业身份源应用{${appCode}}"+msg

        mor.iam.identityOauthSource.query()
            .where { it.id match id }
            .where { it.tenantApps.codeName.code match appCode }
            .where { it.tenant.id match tenantId }
            .toEntity()
            .apply {
                if (this == null) {
                    return JsonResult.error("找不到数据")
                }
                this.tenantApps.forEach {
                    if (it.codeName.code == appCode) {
                        if(it.tenantAppStatus){
                            it.status = false
                        }else{
                            return JsonResult.error("应用已被禁用")
                        }

                    }
                }
                mor.iam.identityOauthSource.updateWithEntity(this).execUpdate()

            }
        return JsonResult()
    }


    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.OrganizationIdentity, "企业身份源删除")
    @ApiOperation("身份源删除")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String, request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "企业身份源删除"
        mor.iam.identityOauthSource.queryById(id).toEntity() ?: return JsonResult.error("找不到数据")

        // 修改协议列表的ldap状态
        mor.iam.identityTypeList.update()
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .set { it.oauth to false }.exec().apply {
                if (this == 0) {
                    return JsonResult.error("修改协议状态失败")
                }
            }

        // 执行删除
        mor.iam.identityOauthSource.delete()
            .where{ it.id match id}
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id}.exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
            }

        return JsonResult()
    }

}