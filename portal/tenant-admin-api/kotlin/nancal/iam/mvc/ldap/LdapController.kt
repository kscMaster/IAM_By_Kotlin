package nancal.iam.mvc.ldap

import io.swagger.annotations.*
import nancal.iam.annotation.BizLog
import nancal.iam.annotation.CheckTenantAppStatus
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nbcp.comm.*
import nbcp.db.mongo.*
import nbcp.base.mvc.*
import nbcp.web.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ldap.core.LdapTemplate
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.naming.Context
import javax.naming.NamingException
import javax.naming.directory.DirContext
import javax.naming.directory.InitialDirContext
import javax.servlet.http.*


/**
 * Created by CodeGenerator at 2021-11-26 11:42:27
 */
@CheckTenantAppStatus
@Api(description = "ldap")
@RestController
@RequestMapping("/iam/ldap")
class LdapController {


    @Autowired
    lateinit var ldapTemplate: LdapTemplate

    @ApiOperation("详情")
    @PostMapping("/detail")
    fun detail(
        request: HttpServletRequest
    ): ApiResult<IdentitySource> {
        mor.iam.identitySource.query().where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }.toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }
                return ApiResult.of(this)
            }
    }

    @BizLog(BizLogActionEnum.Save, BizLogResourceEnum.OrganizationIdentity, "企业身份源新增、修改")
    @ApiOperation("新增、更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: IdentitySource, request: HttpServletRequest
    ): ApiResult<String> {

        if (entity.id.HasValue) {
            request.logMsg = "企业身份源{${entity.name}}修改"
        } else {
            request.logMsg = "企业身份源{${entity.name}}新增"
        }

        //鉴权
        val loginTenantAdminUser = request.LoginTenantAdminUser
        entity.tenant = loginTenantAdminUser.tenant

        val checkParam = checkParam(entity)
        if (checkParam != "") {
            return ApiResult.error(checkParam)
        }

        var isInsert = false

        mor.iam.identitySource.updateWithEntity(entity).run {
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


                    mor.iam.identityTypeList.query().where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }.toEntity()
                        .apply {
                            if (this == null) {
                                var identityTypeList = IdentityTypeList()
                                // 不存在，就新增
                                identityTypeList.tenant = request.LoginTenantAdminUser.tenant
                                identityTypeList.ldap = true
                                mor.iam.identityTypeList.doInsert(identityTypeList)
                            }else{
                                mor.iam.identityTypeList.update().where { it.tenant.id match loginTenantAdminUser.tenant.id }
                                    .set { it.ldap to true }.exec()
                            }
                        }

                    var appList: MutableList<TenantIdentitySourceApp> = mutableListOf()
                    // 查询租户的所有应用，关联ldap
                    mor.tenant.tenantApplication.query().where { it.tenant.id match loginTenantAdminUser.tenant.id }
                        .toList().apply {
                            this.forEach {
                                var obj = TenantIdentitySourceApp()
                                obj.id = it.id
                                obj.codeName.code = it.appCode
                                obj.codeName.name = it.name
                                obj.logo = it.logo
                                obj.status = false
                                obj.tenantAppStatus = it.enabled
                                obj.isSysDefine = false
                                // 总应用开关控制
                                mor.iam.sysApplication.query().where { it.appCode match obj.codeName.code }.toEntity()
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
                        mor.iam.identitySource.updateWithEntity(entity).execUpdate()
                    }

                }
                return ApiResult.of(entity.id)
            }
    }

    fun checkParam(entity: IdentitySource): String {
        if (entity.code.isEmpty()) {
            return "唯一标识符不能为空"
        } else if (entity.code.length < 1 || entity.code.length > 32) {
            return "唯一标识符长度为1~32"
        }

        if (entity.name.isEmpty()) {
            return "显示名称不能为空"
        } else if (entity.name.length < 2 || entity.name.length > 20) {
            return "显示名称长度为2-20个"
        }

        if (entity.url.isEmpty()) {
            return "LDAP链接不能为空"
        } else if (entity.url.length > 256) {
            return "LDAP链接最大长度为256"
        }

        if (entity.bindDN.isEmpty()) {
            return "BindDN不能为空"
        } else if (entity.bindDN.length > 256) {
            return "BindDN最大长度为256"
        }

        if (entity.bindDNPassword.isEmpty()) {
            return "BindDN密码不能为空"
        } else if (entity.bindDNPassword.length > 120) {
            return "BindDN密码最大长度为120"
        }

        if (entity.baseDN.isEmpty()) {
            return "BaseDN不能为空"
        } else if (entity.baseDN.length > 256) {
            return "BaseDN最大长度为256"
        }

        val toEntity = mor.iam.identitySource.query().where { it.code match entity.code }
            .where { it.tenant.id match entity.tenant.id }.toEntity()

        if (entity.id == "") { // 新增
            // 判断唯一标识符是否存在
            if (toEntity != null) {
                return "唯一标识符已存在,无法新增"
            }
            val exist = mor.iam.identitySource.query().where { it.tenant.id match entity.tenant.id }.toEntity()
            if (exist != null) {
                return "ldap身份源已存在，无法新增"
            }
        } else { // 修改
            val updateEntity = mor.iam.identitySource.query().where { it.id match entity.id }.toEntity()
            if (updateEntity != null) {
                if (entity.code != updateEntity.code) {
                    return "唯一标识符不允许修改"
                }
            } else {
                return "修改数据不存在"
            }

            if(entity.tenantApps.size >0){

                entity.tenantApps.forEach { app ->
                    mor.tenant.tenantApplication.query()
                        .where { it.appCode match app.codeName.code }
                        .where { it.tenant.id match entity.tenant.id }
                        .toEntity()
                        .apply {
                            if(this == null){
                                return "有应用不属于该租户，请核对"
                            }
                        }
                }

            }

        }
        if (toEntity != null) {
            if (toEntity.code == entity.code) {
                if (entity.id.isEmpty()) {
                    return "唯一标识符已存在,无法新增"
                } else {
                    if (toEntity.id != entity.id) {
                        return "唯一标识符已存在，无法修改"
                    }
                }
            }
        }
        return ""
    }


    @ApiOperation("协议列表")
    @PostMapping("/protocol-list")
    fun list(
        @Require skip: Int, @Require take: Int, request: HttpServletRequest
    ): ApiResult<IdentityTypeList> {
        mor.iam.identityTypeList.query().where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }.toEntity()
            .apply {
                if (this == null) {
                    var identityTypeList = IdentityTypeList()
                    // 不存在，就新增
                    identityTypeList.tenant = request.LoginTenantAdminUser.tenant
                    mor.iam.identityTypeList.doInsert(identityTypeList)
                    return ApiResult.of(identityTypeList)
                }
                return ApiResult.of(this)
            }
    }

    @ApiOperation("测试连接")
    @PostMapping("/testConnect")
    fun testConnect(
        code: String, bindDN: String, bindDNPassword: String, @Require url: String, request: HttpServletRequest
    ): ApiResult<String> {
        val env = Properties()

        env[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.ldap.LdapCtxFactory" //java.naming.factory.initial

        env[Context.PROVIDER_URL] = url //java.naming.provider.url

        env[Context.SECURITY_AUTHENTICATION] = "simple" //java.naming.security.authentication

        env[Context.SECURITY_PRINCIPAL] = bindDN //java.naming.security.principal

        env[Context.SECURITY_CREDENTIALS] = bindDNPassword //java.naming.security.credentials

        env[Context.REFERRAL] = "throw"

        try {
            InitialDirContext(env)
        } catch (e: NamingException) {
            return ApiResult.error("服务器连接失败")
        }

        return ApiResult()
    }

    @BizLog(BizLogActionEnum.Enable, BizLogResourceEnum.OrganizationIdentity, "企业身份源启用")
    @ApiOperation("身份源应用的启用")
    @PostMapping("/enable")
    fun enabled(
        @Require id: String, @Require appCode: String,  request: HttpServletRequest
    ): JsonResult {
        val tenantId=request.LoginTenantAdminUser.tenant.id
        val msg = "启用"
        request.logMsg = "企业身份源应用{${appCode}}"+msg

        mor.iam.identitySource.query().where { it.id match id }
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
                mor.iam.identitySource.updateWithEntity(this).execUpdate()
            }
        return JsonResult()
    }
    @BizLog(BizLogActionEnum.Disable, BizLogResourceEnum.OrganizationIdentity, "企业身份源停用")
    @ApiOperation("身份源应用的停用")
    @PostMapping("/disable")
    fun disabled(
        @Require id: String, @Require appCode: String,  request: HttpServletRequest
    ): JsonResult {
        val tenantId=request.LoginTenantAdminUser.tenant.id
        val  msg = "停用"
        request.logMsg = "企业身份源应用{${appCode}}"+msg

        mor.iam.identitySource.query().where { it.id match id }
            .where { it.tenantApps.codeName.code match appCode }
            .where { it.tenant.id match tenantId }
            .toEntity().apply {
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
                mor.iam.identitySource.updateWithEntity(this).execUpdate()

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
        val toEntity = mor.iam.identitySource.queryById(id).toEntity()
        toEntity ?: return JsonResult.error("找不到数据")

        // 修改协议列表的ldap状态
        mor.iam.identityTypeList.update().where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .set { it.ldap to false }.exec().apply {
                if (this == 0) {
                    return JsonResult.error("修改协议状态失败")
                }
            }

        // 执行删除
        mor.iam.identitySource.delete()
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