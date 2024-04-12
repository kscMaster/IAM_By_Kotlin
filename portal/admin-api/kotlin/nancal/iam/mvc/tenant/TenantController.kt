package nancal.iam.mvc.tenant

import com.nancal.cipher.SHA256Util
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import nancal.iam.annotation.BizLog
import nancal.iam.client.MPClient
import nancal.iam.comm.*
import nancal.iam.config.BaseEnConfig
import nbcp.comm.*
import nbcp.db.IdName
import nbcp.db.db
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.mongo.entity.tenant.TenantGroupDict
import nancal.iam.db.sql.dbr
import nancal.iam.util.EmailMessage
import nancal.iam.util.MailUtil
import nbcp.db.CodeName
import nbcp.web.UserId
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

/**
 * Created by CodeGenerator at 2021-11-22 14:05:30
 */
@Api(description = "tenant", tags = arrayOf("Tenant"))
@RestController
@RequestMapping("/admin/tenant")
class AdminAutoController {

    @Resource
    lateinit var mpClient: MPClient

    @Resource
    lateinit var mailUtil: MailUtil

    @Resource
    lateinit var enConfig: BaseEnConfig

    @Value("\${spring.application.name}")
    var appName = "mp-iam-admin-api"

    @Value("\${mail.sender}")
    private val mailSender: String = ""

    @Value("\${mail.pwd}")
    private val mailPwd: String = ""

    @Value("\${mail.smtp}")
    private val mailSmtp: String = ""

    @Value("\${mail.pop}")
    private val mailPop: String = ""

    class TenantAndUserVO {
        var tenantAdminUser: TenantUser = TenantUser()
        var tenant: Tenant = Tenant()
        var publicSecret: String = ""

    }

    class TenantVo {
        var name = ""
        var id = ""
        var isLocked = false
        var appCount = 0
        var adminCount = 0
    }

    @ApiOperation("租户管理")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        name: String,
        enabled: Boolean?,
        concatName: String,
        concatPhone: String,
        email: String,
        address: String,
        keywords: String,
        @Require pageNumber: Int,
        @Require pageSize: Int,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<out Tenant> {
        if (pageNumber == 0 || pageSize == 0) return ListResult.error("页码和页条数未传或不正确")
        mor.tenant.tenant.query()
            .apply {
                if (keywords.HasValue) {
                    this.whereOr(
                        { it.id match keywords },
                        { it.name match_like keywords },
                        { it.concatName match_like keywords },
                        { it.concatPhone match_like keywords },
                        { it.email match_like keywords },
                        { it.address match_like keywords }
                    )
                }
            }
            .limit(skip, take).orderByDesc { it.createAt }.orderByAsc { it.id }
            .toListResult()
            .apply {
                this.total = mor.tenant.tenant.query().apply {
                    if (keywords.HasValue) {
                        this.whereOr(
                            { it.id match keywords },
                            { it.name match_like keywords },
                            { it.concatName match_like keywords },
                            { it.concatPhone match_like keywords },
                            { it.email match_like keywords }
                        )
                    }
                }.count()
                if (request.getHeader("lang") == "en") {
                    this.data.forEach {
                        it.industry.name = enConfig.getEn(it.industry.name)
                    }
                }
                return this;
            }

//        mor.tenant.tenant.aggregate()
//            .beginMatch()
//
//            .apply {
//                if (id.HasValue) {
//                    this.where { it.id match id }
//
//                }
//                if (name.HasValue) {
//                    this.where { it.name match_like name }
//                }
//                if (enabled != null) {
//                    this.where { it.isLocked match enabled }
//                }
//                if (concatName.HasValue) {
//                    this.where { it.concatName match_like concatName }
//                }
//                if (concatPhone.HasValue) {
//                    this.where { it.concatPhone match_like concatPhone }
//                }
//                if (email.HasValue) {
//                    this.where { it.email match_like email }
//                }
//                if (address.HasValue) {
//                    this.where { it.address match_like address }
//                }
//            }
//            .endMatch()
//            .addPipeLine(
//                PipeLineEnum.addFields,
//                db.mongo.op(
//                    PipeLineOperatorEnum.max,
//                    arrayOf(
//                        "$" + mor.tenant.tenant.createAt.toString()
////                        "$" + mor.tenant.tenant.updateAt.toString()
//                    )
//                ).As("u")
//            )
//            .orderBy("u" to false)
//            .limit(skip, take)
//            .toListResult()
//            .apply {
//                this.total = mor.tenant.tenant.query().apply {
//                    if (id.HasValue) {
//                        this.where { it.id match id }
//                    }
//                    if (name.HasValue) {
//                        this.where { it.name match_like name }
//                    }
//                    if (enabled != null) {
//                        this.where { it.isLocked match enabled }
//                    }
//                    if (concatName.HasValue) {
//                        this.where { it.concatName match_like concatName }
//                    }
//                    if (concatPhone.HasValue) {
//                        this.where { it.concatPhone match_like concatPhone }
//                    }
//                    if (email.HasValue) {
//                        this.where { it.email match_like email }
//                    }
//                    if (address.HasValue) {
//                        this.where { it.address match_like address }
//                    }
//                }.count()
//                if (request.getHeader("lang") == "en") {
//                    this.data.forEach {
//                        it.industry.name = enConfig.getEn(it.industry.name)
//                    }
//                }
//                return this;
//            }
    }

    @ApiOperation("详情")
    @PostMapping("/detail/{id}")
    fun detail(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<TenantAndUserVO> {
        val tenant = mor.tenant.tenant.queryById(id)
            .toEntity() ?: return ApiResult.error("找不到租户")

        if (request.getHeader("lang") == "en" && tenant.industry.name.HasValue) {
            tenant.industry.name = enConfig.getEn(tenant.industry.name)
        }
        val tenantAdminUser = mor.tenant.tenantUser.query()
            .where { it.tenant.id match id }
            .whereOr(
                { it.adminType match TenantAdminTypeEnum.Super },
                { it.adminType match TenantAdminTypeEnum.User },
                { it.adminType match TenantAdminTypeEnum.Business },
                { it.adminType match TenantAdminTypeEnum.Auditor }
            )
            .orderByAsc { it.createAt }
            .toEntity() ?: return ApiResult.error("找不到管理员")
        val result = TenantAndUserVO()
        result.tenant = tenant
        result.tenantAdminUser = tenantAdminUser
        var tss = mor.tenant.tenantSecretSet.queryByTenantId(id).toEntity()
        if (tss != null) {
            result.publicSecret = tss.publicSecret
        }

        return ApiResult.of(result)
    }

    @BizLog(BizLogActionEnum.Save, BizLogResourceEnum.Tenant, "租户管理")
    @ApiOperation("新增/更新")
    @PostMapping("/save")
    fun save(
        @Require tenant: Tenant,
        @Require tenantAdminUser: TenantUser?,
        password: String,
        request: HttpServletRequest
    ): ApiResult<Map<String, String>> {
        request.setAttribute("id", tenant.id)
        if (request.getHeader("lang") == "en" && tenant.industry.name.HasValue) {
            tenant.industry.name = enConfig.getCn(tenant.industry.name)
        }

        var resInfo: ApiResult<String> = ApiResult()
        if (tenant.id.HasValue) {
            request.logMsg = "修改租户{${tenant.name}}"
            val checkEditParams = checkEditParams(tenant, tenantAdminUser)
            if (checkEditParams.msg.HasValue) {
                return checkEditParams
            }
        } else {
            request.logMsg = "创建租户{${tenant.name}}"
            if (tenantAdminUser == null) {
                return ApiResult.error("租户管理员不能为空")
            }
            val checkInsertParams = checkInsertParams(tenant, tenantAdminUser)
            if (checkInsertParams.msg.HasValue) {
                return checkInsertParams
            }
        }

        var tempName = ""
        if (tenant.id.HasValue) {
            tempName = mor.tenant.tenant.query()
                .where { it.id match tenant.id }
                .select { it.name }
                .toEntity(String::class.java)!!
        }

        var isInsert = false
        mor.tenant.tenant.updateWithEntity(tenant)
            .withoutColumn { it.aloneDbConnection }
            .run {
                if (tenant.id.HasValue) {
                    this.withoutColumn { it.code }
                    return@run this.execUpdate()
                } else {
                    isInsert = true
                    return@run this.execInsert()
                }
            }
            .apply {
                if (this == 0) {
                    return ApiResult.error("保存失败")
                }
                if (!isInsert) {
                    //编辑
                    if (tenantAdminUser != null) {

                        tenantAdminUser.tenant = IdName(tenant.id, tenant.name)

                        //更新租户的管理员
                        mor.tenant.tenantUser.updateWithEntity(tenantAdminUser)
                            .execUpdate()
                            .apply {
                                if (this == 0) {
                                    return ApiResult.error("更新管理员失败")
                                }
                                if (!tempName.equals(tenant.name)) {
                                    mor.tenant.tenantDepartmentInfo.update()
                                        .where { it.tenant.id match tenant.id }
                                        .where { it.name match tempName }
                                        .where { it.parent.id match "" }
                                        .set { it.name to tenant.name }
                                        .exec()
                                }
                            }
                    }
                } else {
                    resInfo = afterInsert(tenant, tenantAdminUser!!, password, request)
                        .apply {
                            if (this.msg.HasValue) {
                                /*删除租户*/
                                mor.tenant.tenant.deleteById(tenant.id)
                                    .exec()
                                return ApiResult.error(this.msg)
                            }
                        }
                }
            }

        var tadminid = ""
        if (tenantAdminUser != null) {
            tadminid = tenantAdminUser.id
        }
        if (!tenant.id.HasValue) {
            // 初始化字典数据
            saveTenantDict(tenant.id)
        }
        return ApiResult.of(
            mapOf<String, String>(
                "tenantId" to tenant.id,
                "tenantAdminUserId" to tadminid,
                "publicSecret" to resInfo.data.toString()
            )
        )
    }

    fun saveTenantDict(id: String) {
        val tenant: Tenant = mor.tenant.tenant.queryById(id).toEntity().must().elseThrow { "找不到租户" }
        val data: MutableList<TenantGroupDict> = mutableListOf(
            TenantGroupDict(
                PersonClassifiedEnum.Core.name,
                PersonClassifiedEnum.Core.remark,
                "",
                TenantDictType.PersonClassified.type,
                1,
                IdName(tenant.id, tenant.name),
                false
            ),
            TenantGroupDict(
                PersonClassifiedEnum.Important.name,
                PersonClassifiedEnum.Important.remark,
                "",
                TenantDictType.PersonClassified.type,
                2,
                IdName(tenant.id, tenant.name),
                false
            ),
            TenantGroupDict(
                PersonClassifiedEnum.General.name,
                PersonClassifiedEnum.General.remark,
                "",
                TenantDictType.PersonClassified.type,
                3,
                IdName(tenant.id, tenant.name),
                false
            ),
            TenantGroupDict(
                PersonClassifiedEnum.NonConfidential.name,
                PersonClassifiedEnum.NonConfidential.remark,
                "",
                TenantDictType.PersonClassified.type,
                4,
                IdName(tenant.id, tenant.name),
                false
            ),
        )

        mor.tenant.tenantGroupDict.batchInsert()
            .apply {
                addEntities(data)
            }
            .exec()
    }

    private fun afterInsert(
        tenant: Tenant,
        tenantAdminUser: TenantUser,
        password: String,
        request: HttpServletRequest
    ): ApiResult<String> {
        //创建企业私密表。
        var tenantSecretSet = TenantSecretSet();
        var keyStore = com.nancal.cipher.RSARawUtil.create()
        tenantSecretSet.publicSecret = keyStore.publicKeyString
        tenantSecretSet.sysPrivateSecret = keyStore.privateKeyString
        tenantSecretSet.tenant = IdName(tenant.id, tenant.name)
        tenantSecretSet.setting = createSetting()

        mor.tenant.tenantSecretSet.doInsert(tenantSecretSet)

        //新建租户的管理员
        tenantAdminUser.tenant = IdName(tenant.id, tenant.name)

        if (tenantAdminUser.adminType != TenantAdminTypeEnum.User
            && tenantAdminUser.adminType != TenantAdminTypeEnum.Auditor
            && tenantAdminUser.adminType != TenantAdminTypeEnum.Business
            && tenantAdminUser.adminType != TenantAdminTypeEnum.Super
        ) {
            return ApiResult.error("角色选择错误")
        }
        //tenantAdminUser.adminType = TenantAdminTypeEnum.Super
        tenantAdminUser.id = "";

        mor.tenant.tenantUser.doInsert(tenantAdminUser)
        if (db.affectRowCount == 0) {
            return ApiResult.error("添加管理员失败")
        }


        val loginUser = TenantLoginUser()
        loginUser.userId = tenantAdminUser.id
        loginUser.loginName = tenantAdminUser.loginName
        loginUser.tenant.id = tenant.id
        loginUser.tenant.name = tenant.name
        loginUser.mobile = tenantAdminUser.mobile
        loginUser.email = tenantAdminUser.email
        // todo 密码要随机生成，不要包含特殊字符
        var pwd = UUID.randomUUID().toString().replace("-", "").substring(0, 6)
        var salt = UUID.randomUUID().toString().replace("-", "").substring(0, 4)
        loginUser.passwordSalt = salt
        if (tenantAdminUser.sendPasswordType == SendPasswordType.Defined) {
            if (password.trim().equals("")) {
                return ApiResult.error("密码不能为空")
            }
            pwd = password
            loginUser.password = SHA256Util.getSHA256StrJava(pwd + salt)
        } else {
            loginUser.password = SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(pwd) + salt)
        }
        mor.tenant.tenantLoginUser.doInsert(loginUser)
        if (db.affectRowCount == 0) {
            return ApiResult.error("添加管理员失败")
        }

        val userEntity = mor.tenant.tenantUser.queryById(loginUser.userId)
            .toEntity()

        if (userEntity == null) {
            return ApiResult.error("用户不存在")
        }

        var departmentInfo = TenantDepartmentInfo()
        departmentInfo.tenant.id = tenant.id
        departmentInfo.tenant.name = tenant.name
        departmentInfo.name = tenant.name

        mor.tenant.tenantDepartmentInfo.doInsert(departmentInfo)

        if (tenantAdminUser.isSendPassword) { // TODO EN 切换中英文
            if (userEntity.sendPasswordType == SendPasswordType.Mobile) {
                val sendSmsCode = mpClient.sendSmsPwd(MobileCodeModuleEnum.SendPassword, userEntity.mobile, pwd)
                val success = sendSmsCode.success()
            } else if (userEntity.sendPasswordType == SendPasswordType.Email) {
                val msg = EmailMessage()
                msg.sender = mailSender
                msg.password = mailPwd
                msg.addressee = listOf(userEntity.email)
                if (request.getHeader("lang") == "en") {
                    msg.topic = "[IAM] login password"
                    msg.content = "Hello，${userEntity.loginName}<br />" +
                            "<p style=\"text-indent:2em\">your login password is：$pwd 。Please do not share</p><br />" +
                            "<br>your password with anyone for the sake of your account security.</br>"
                } else {
                    msg.topic = "【统一身份认证系统】登录密码"
                    msg.content = "您好，${userEntity.loginName}<br />" +
                            "<p style=\"text-indent:2em\">您的登录密码为：$pwd 。为了您账户的</p><br />" +
                            "<br>安全请勿将密码告知他人。</br>"
                }

                msg.popService = mailPop
                msg.smtpService = mailSmtp
                mailUtil.sendEmail(msg)
            }
        }
        return ApiResult.of(tenantSecretSet.publicSecret)
    }


    @BizLog(BizLogActionEnum.Enable, BizLogResourceEnum.Tenant, "租户启用")
    @ApiOperation("租户启用")
    @PostMapping("/enable")
    fun enabled(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        val enabled = true
        //鉴权
        var userId = request.UserId
        mor.tenant.tenant.updateById(id)
            .set { it.isLocked to enabled }
            .exec()
        if (mor.affectRowCount > 0) {
            mor.tenant.tenant.queryById(id).toEntity()
                .apply {
                    if (this != null) {
                        request.logMsg = "租户{${this.name}}" + "启用"
                    }
                }
            return JsonResult();
        } else return JsonResult.error("操作失败")
    }

    @BizLog(BizLogActionEnum.Disable, BizLogResourceEnum.Tenant, "租户停用")
    @ApiOperation("租户停用")
    @PostMapping("/disable")
    fun disabled(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        val enabled = false
        //鉴权
        var userId = request.UserId
        mor.tenant.tenant.updateById(id)
            .set { it.isLocked to enabled }
            .exec()
        if (mor.affectRowCount > 0) {
            mor.tenant.tenant.queryById(id).toEntity()
                .apply {
                    if (this != null) {
                        request.logMsg = "租户{${this.name}}" + "禁用"
                    }
                }
            return JsonResult();
        } else return JsonResult.error("操作失败")
    }

    @BizLog(BizLogActionEnum.Authorize, BizLogResourceEnum.App, "应用授权")
    @ApiOperation("为租户分配应用")
    @PostMapping("/giveApp")
    fun giveApp(
        appCodes: List<String>,
        tenantId: String,
        request: HttpServletRequest
    ): ApiResult<String> {
        request.logMsg = "给租户{${tenantId}}授予应用{${appCodes.map { it }}}"
        if (appCodes.size == 0) {
            throw RuntimeException("请传应用code")
        }
        val tenant = mor.tenant.tenant.queryById(tenantId).toEntity().must().elseThrow { "找不到租户" }
        val tenantIdName = IdName(tenant.id, tenant.name)

        //先找出，租户没有的应用。 添加之
        val tenantApps = mor.tenant.tenantApplication.query()
            .where { it.tenant.id match tenantId }
            .toList()

        //查是否为租户重复添加应用
        val willAddAppCodes = appCodes.minus(tenantApps.map { it.appCode })
        if (willAddAppCodes.isEmpty()) {
            return ApiResult()
        }

        var successCount = 0

        // 拿到所有授权的应用
        var sysApps = mor.iam.sysApplication.query()
            .where { it.appCode match_in willAddAppCodes }
            .toList(TenantApplication::class.java)
        sysApps.forEach {
            it.tenant = tenantIdName
            it.enabled = true
            it.sysId = it.id
            it.id = ""
            it.isSysDefine = true
        }

        // 给租户添加应用
        mor.tenant.tenantApplication.batchInsert()
            .apply {
                addEntities(sysApps)
            }
            .exec()
        successCount = mor.affectRowCount

        if (successCount < willAddAppCodes.size) {
            throw RuntimeException("授权失败")
        }
        var data = mor.tenant.tenantApplication.query()
            .where { it.tenant.id match tenantId }
            .toList()

        //同步外接身份源登录
        pushAppToIdentitySource(tenantId, data)

        //同步应用资源
        mor.iam.sysResourceInfo.query()
            .where { it.appInfo.code match_in willAddAppCodes }
            .toList(TenantResourceInfo::class.java)
            .apply {
                if (this.isNotEmpty()) {
                    this.forEach {
                        it.sysId = it.id
                        it.id = ""
                        it.tenant = tenantIdName
                        it.isSysDefine = true
                    }

                    val data = this
                    mor.tenant.tenantResourceInfo.batchInsert()
                        .apply {
                            addEntities(data)
                        }
                        .exec()
                }
            }

        //同步应用角色
        mor.iam.sysAppRole.query()
            .where { it.appInfo.code match_in willAddAppCodes }
            .toList(TenantAppRole::class.java)
            .apply {
                if (this.isNotEmpty()) {
                    this.forEach {
                        it.sysId = it.id
                        it.id = ""
                        it.tenant = tenantIdName
                        it.isSysDefine = true
                    }

                    val data = this
                    mor.tenant.tenantAppRole.batchInsert()
                        .apply {
                            addEntities(data)
                        }
                        .exec()
                }
            }

        //同步授权
        mor.iam.sysAppAuthResource.query()
            .where { it.appInfo.code match_in willAddAppCodes }
            .toList(TenantAppAuthResourceInfo::class.java)
            .apply {
                if (this.isNotEmpty()) {
                    //租户应用角色
                    val tenantRoles = mor.tenant.tenantAppRole.query()
                        .select("id", "name")
                        .where { it.appInfo.code match_in willAddAppCodes }
                        .where { it.tenant.id match tenantId }
                        .toList()
                    //租户应用资源
                    val tenantResources = mor.tenant.tenantResourceInfo.query()
                        .select("id", "name")
                        .where { it.appInfo.code match_in willAddAppCodes }
                        .where { it.tenant.id match tenantId }
                        .toList()
                    val data = mutableListOf<TenantAppAuthResourceInfo>()
                    for (index in 0 until this.size) {
                        val authResource = this[index]
                        val tRole = tenantRoles.stream().filter { it.name == authResource.target.name }.findFirst()
                        if (!tRole.isPresent) {
                            // 找不到授权主体时删除并跳过

                            continue
                        }
                        authResource.target.id = tRole.get().id
                        val authsData = mutableListOf<AuthResourceInfo>()
                        for (authIndex in 0 until authResource.auths.size) {
                            val auth = authResource.auths[authIndex]
                            if (auth.code == "*") {  // TODO 验证
                                auth.resourceId = ""
                                authsData.add(auth)
                            } else {
                                val findFirst = tenantResources.stream().filter { it.name == auth.name }.findFirst()
                                if (!findFirst.isPresent) {
                                    // 找不到授权主体下的资源时删除并跳过
                                    continue
                                }
                                auth.resourceId = findFirst.get().id
                                authsData.add(auth)
                            }

                        }
                        authResource.auths = authsData
                        authResource.tenant = tenantIdName
                        authResource.isSysDefine = true
                        authResource.sysId = authResource.id
                        authResource.id = ""
                        data.add(authResource)
                    }


                    mor.tenant.tenantAppAuthResourceInfo.batchInsert()
                        .apply {
                            addEntities(data)
                        }
                        .exec()
                }
            }
        return ApiResult()
    }

    @BizLog(BizLogActionEnum.CancelAuthorize, BizLogResourceEnum.App, "应用授权")
    @ApiOperation("删除租户应用授权")
    @PostMapping("/removeApp")
    fun removeApp(
        appCode: String,
        tenantId: String,
        request: HttpServletRequest
    ): ApiResult<String> {
        request.logMsg = "移除租户{${tenantId}}的应用{${appCode}}"
        mor.tenant.tenantApplication.delete()
            .where { it.tenant.id match tenantId }
            .where { it.appCode match appCode }
            .exec()
        if (dbr.affectRowCount == 0) return ApiResult.error("移除失败，租户或应用不存在或应用不在租户下")

        val roles = mor.tenant.tenantAppRole.query()
            .where { it.tenant.id match tenantId }
            .where { it.appInfo.code match appCode }
            .toList()
        val roleIds = roles.map { it.id }


        //删除租户应用角色、资源、授权
        mor.tenant.tenantAppRole.delete()
            .where { it.tenant.id match tenantId }
            .where { it.appInfo.code match appCode }
            .exec()


        mor.tenant.tenantResourceInfo.delete()
            .where { it.tenant.id match tenantId }
            .where { it.appInfo.code match appCode }
            .exec()

        mor.tenant.tenantAppAuthResourceInfo.query()
            .where { it.tenant.id match tenantId }
            .where { it.appInfo.code match appCode }
            .select { it.id }
            .toList(String::class.java)
            .apply {
                if (this.size > 0) {
                    mor.tenant.tenantAppAuthResourceInfo.delete()
                        .where { it.id match_in this }
                        .exec()
                }
            }

        //删除授权过的角色
        //删除用户下的
        mor.tenant.tenantUser.update()
            .where { it.tenant.id match tenantId }
            .pull({ it.roles }, MongoColumnName("id") match_in roleIds)
            .exec()
        //删除部门下的
        mor.tenant.tenantDepartmentInfo.update()
            .where { it.tenant.id match tenantId }
            .pull({ it.roles }, MongoColumnName("id") match_in roleIds)
            .exec()
        //删除用户组下的
        mor.tenant.tenantUserGroup.update()
            .where { it.tenant.id match tenantId }
            .pull({ it.roles }, MongoColumnName("id") match_in roleIds)
            .exec()

        //删除应用登录授权
        //删除部门下的
        mor.tenant.tenantDepartmentInfo.update()
            .where { it.tenant.id match tenantId }
            .pull({ it.allowApps }, MongoColumnName("code") match appCode)
            .exec()
        //删除用户下的
        mor.tenant.tenantUser.update()
            .where { it.tenant.id match tenantId }
            .pull({ it.allowApps }, MongoColumnName("code") match appCode)
            .exec()
        //删除用户组下的
        mor.tenant.tenantUserGroup.update()
            .where { it.tenant.id match tenantId }
            .pull({ it.allowApps }, MongoColumnName("code") match appCode)
            .exec()


        //删除租户外接身份源登录的应用
        pullAppFromIdentitySource(tenantId, appCode)


        return ApiResult()
    }

    @ApiOperation("租户列表")
    @PostMapping("/tenantList")
    fun tenantList(
        id: String, //当列表列新一条后，刷新时使用
        name: String,
        isLocked: Boolean?,
        keywords: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantVo> {
        var reslist = mor.tenant.tenant.query()
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (keywords.HasValue) {
                    this.whereOr(
                        { it.id match keywords },
                        { it.name match_like keywords }
                    )
                }
                if (name.HasValue) {
                    this.where { it.name match_like name }
                }
                if (isLocked != null) {
                    this.where { it.isLocked match isLocked }
                }
            }.limit(skip, take)
            .orderByDesc { it.createAt }.orderByAsc { it.id }
            .toListResult(TenantVo::class.java)

        reslist.data.forEach { res ->
            res.adminCount = mor.tenant.tenantUser.query()
                .where { it.adminType match_not_equal TenantAdminTypeEnum.None }
                .where { it.tenant.id match res.id }
                .count()
            res.appCount = mor.tenant.tenantApplication.query()
                .where { it.tenant.id match res.id }
                .count()
        }

        return reslist
    }


    fun checkInsertParams(tenant: Tenant, tenantAdminUser: TenantUser): ApiResult<Map<String, String>> {
        val matchEmailParttern = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$"
        val matchPhoneParttern =
            "^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$"

        /******************** 租户信息校验开始      ********************/
        //租户信息必填
        if (!tenant.name.HasValue) {
            return ApiResult.error("请填写租户名称")
        }
        if (tenant.name.length < 2) {
            return ApiResult.error("租户名称至少2个字符")
        }
        if (tenant.name.length > 32) {
            return ApiResult.error("租户名称最多32个字符")
        }
        if (tenant.email.HasValue) {
            val isEmailMatch = Regex(matchEmailParttern).containsMatchIn(tenant.email)
            if (!isEmailMatch) {
                return ApiResult.error("租户email地址无效")
            }
        }
        if (tenant.concatPhone.HasValue) {
            val isPhoneMatch = Regex(matchPhoneParttern).containsMatchIn(tenant.concatPhone)
            if (!isPhoneMatch) {
                return ApiResult.error("租户手机号码格式不正确")
            }
        }

        if (tenant.concatName.HasValue && tenant.concatName.length > 32) {
            return ApiResult.error("联系人姓名最多32个字符")
        }
        if (tenant.address.HasValue && tenant.address.length > 255) {
            return ApiResult.error("联系地址不能超过255个字符")
        }
        if (tenant.remark.HasValue && tenant.remark.length > 255) {
            return ApiResult.error("备注不能超过255个字符")
        }


        val isPhoneMatch = Regex(matchPhoneParttern).containsMatchIn(tenantAdminUser.loginName)
        if (isPhoneMatch) {
            return ApiResult.error("loginName不能是手机号格式")
        }
        val isEmailMatch = Regex(matchEmailParttern).containsMatchIn(tenantAdminUser.loginName)
        if (isEmailMatch) {
            return ApiResult.error("loginName不能是邮箱格式")
        }

        //查相同租户
        val isSame = mor.tenant.tenant.query().apply {
            this.where { it.name match tenant.name }
        }.exists()
        if (isSame) {
            return ApiResult.error("租户重复，请重新填写租户名")
        }
        if (tenant.code.HasValue) {
            if (mor.tenant.tenant.query().where { it.code match tenant.code }.exists()) {
                return ApiResult.error("租户code已存在")
            }
        }
        //行业是否存在
        if (tenant.industry != null && tenant.industry.id.isNotEmpty()) {
            mor.iam.industryDict.query()
                .where { it.id match tenant.industry.id }
                .exists()
                .apply {
                    if (!this) {
                        return ApiResult.error("所属行业不存在")
                    }
                }
        }

        /******************** 租户信息校验结束      ********************/

        /******************** 租户管理员信息校验开始 ********************/
        if (!tenantAdminUser.loginName.HasValue) {
            return ApiResult.error("请填写管理员用户名")
        }
        if (tenantAdminUser.loginName.length < 3) {
            return ApiResult.error("管理员用户名至少3个字符")
        }
        if (tenantAdminUser.loginName.length > 32) {
            return ApiResult.error("管理员用户名不能超过32个字符")
        }
        if (!tenantAdminUser.name.HasValue) {
            return ApiResult.error("请填写管理员姓名")
        }
        if (tenantAdminUser.name.length < 2) {
            return ApiResult.error("管理员姓名至少2个字符")
        }
        if (tenantAdminUser.name.length > 32) {
            return ApiResult.error("管理员姓名不能超过32个字符")
        }

        if (tenantAdminUser.sendPasswordType == null) {
            return ApiResult.error("发送密码方式不能为空")
        }

//        //手机号或者邮箱
//        if(tenantAdminUser.sendPasswordType != SendPasswordType.Defined && !tenantAdminUser.id.HasValue){
//            if (!tenantAdminUser.email.HasValue && !tenantAdminUser.mobile.HasValue) {
//                return ApiResult.error("请填写邮箱或者电话")
//            }
//        }

        if (tenantAdminUser.email.isEmpty() && tenantAdminUser.sendPasswordType?.name.equals(SendPasswordType.Email.name)) {
            return ApiResult.error("发送密码请选择手机或者请填写邮箱")
        }
        if (tenantAdminUser.mobile.isEmpty() && tenantAdminUser.sendPasswordType?.name.equals(SendPasswordType.Mobile.name)) {
            return ApiResult.error("发送密码请选择邮箱或者请填写手机号")
        }
        //验证邮箱或者手机号格式
        if (tenantAdminUser.mobile.HasValue) {
            val isPhoneMatch = Regex(matchPhoneParttern).containsMatchIn(tenantAdminUser.mobile)
            if (!isPhoneMatch) {
                return ApiResult.error("管理员手机号码格式错误")
            }
        }
        if (tenantAdminUser.email.HasValue) {
            val isEmailMatch = Regex(matchEmailParttern).containsMatchIn(tenantAdminUser.email)
            if (!isEmailMatch) {
                return ApiResult.error("管理员email地址无效")
            }
        }
        //查相同的登录账号
        val isSameLogin = mor.tenant.tenantUser.query().apply {
            this.where { it.loginName match tenantAdminUser.loginName }
        }.exists()
        if (isSameLogin) {
            return ApiResult.error("用户名重复")
        }
        /******************** 租户管理员信息校验结束 *********************/
        return ApiResult()
    }

    fun checkEditParams(tenant: Tenant, tenantAdminUser: TenantUser?): ApiResult<Map<String, String>> {
        var matchEmailParttern = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$"
        var matchPhoneParttern =
            "^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$"

        val isPhoneMatch = tenantAdminUser?.let { Regex(matchPhoneParttern).containsMatchIn(it.loginName) }
        if (isPhoneMatch == true) {
            return ApiResult.error("loginName不能是手机号格式")
        }
        val isEmailMatch = tenantAdminUser?.let { Regex(matchEmailParttern).containsMatchIn(it.loginName) }
        if (isEmailMatch == true) {
            return ApiResult.error("loginName不能是邮箱格式")
        }

        //租户信息必填
        if (!tenant.name.HasValue) {
            return ApiResult.error("请填写租户名称")
        }
        if (tenant.name.length < 2) {
            return ApiResult.error("租户名称至少2个字符")
        }
        if (tenant.name.length > 20) {
            return ApiResult.error("租户名称最多20个字符")
        }
        if (tenant.email.HasValue) {
            val isEmailMatch = Regex(matchEmailParttern).containsMatchIn(tenant.email)
            if (!isEmailMatch) {
                return ApiResult.error("租户email地址无效")
            }
        }
        if (tenant.concatName.HasValue && tenant.concatName.length > 32) {
            return ApiResult.error("联系人姓名最多32个字符")
        }
        if (tenant.concatPhone.HasValue) {
            val isPhoneMatch = Regex(matchPhoneParttern).containsMatchIn(tenant.concatPhone)
            if (!isPhoneMatch) {
                return ApiResult.error("租户手机号码格式不正确")
            }
        }
        if (tenant.address.HasValue && tenant.address.length > 255) {
            return ApiResult.error("联系地址不能超过255个字符")
        }
        if (tenant.remark.HasValue && tenant.remark.length > 255) {
            return ApiResult.error("备注不能超过255个字符")
        }
        //行业是否存在
        if (tenant.industry != null && tenant.industry.id.isNotEmpty()) {
            mor.iam.industryDict.query()
                .where { it.id match tenant.industry.id }
                .exists()
                .apply {
                    if (!this) {
                        return ApiResult.error("所属行业不存在")
                    }
                }
        }
        //查租户是否存在
        mor.tenant.tenant.queryById(tenant.id)
            .exists()
            .apply {
                if (!this) return ApiResult.error("租户不存在")
            }
        //查相同租户
        val isSame = mor.tenant.tenant.query().apply {
            this.where { it.name match tenant.name }
            this.where { it.id match_not_equal tenant.id }
        }.exists()
        if (isSame) {
            return ApiResult.error("租户重复，请重新填写租户名")
        }

        if (tenantAdminUser != null) {
            if (!tenantAdminUser.loginName.HasValue) {
                return ApiResult.error("请填写管理员用户名")
            }
            if (tenantAdminUser.loginName.length < 3) {
                return ApiResult.error("管理员用户名至少3个字符")
            }
            if (tenantAdminUser.loginName.length > 32) {
                return ApiResult.error("管理员用户名不能超过32个字符")
            }
            if (!tenantAdminUser.name.HasValue) {
                return ApiResult.error("请填写管理员姓名")
            }
            if (tenantAdminUser.name.length < 2) {
                return ApiResult.error("管理员姓名至少2个字符")
            }
            if (tenantAdminUser.name.length > 32) {
                return ApiResult.error("管理员姓名不能超过32个字符")
            }
            if (!tenantAdminUser.email.HasValue && tenantAdminUser.sendPasswordType?.name.equals(SendPasswordType.Email.name)) {
                return ApiResult.error("请填写邮箱")
            }
            if (!tenantAdminUser.mobile.HasValue && tenantAdminUser.sendPasswordType?.name.equals(SendPasswordType.Mobile.name)) {
                return ApiResult.error("请填写手机号")
            }


            //TODO验证邮箱或者手机号格式
            if (tenantAdminUser.mobile.HasValue) {

                val isPhoneMatch = Regex(matchPhoneParttern).containsMatchIn(tenantAdminUser.mobile)
                if (!isPhoneMatch) {
                    return ApiResult.error("管理员手机号码格式不正确")
                }
            }
            if (tenantAdminUser.email.HasValue) {

                val isEmailMatch = Regex(matchEmailParttern).containsMatchIn(tenantAdminUser.email)
                if (!isEmailMatch) {
                    return ApiResult.error("管理员email地址无效")
                }
            }
            //查管理员是否在租户下
            if (tenantAdminUser.id.isEmpty()) return ApiResult.error("超级管理员id不能为空")
            val thisAdmin = mor.tenant.tenantUser.queryById(tenantAdminUser.id).toEntity()
            if (thisAdmin == null || thisAdmin.tenant.id != tenant.id) return ApiResult.error("找不到超级管理员")

            //查相同的登录账号
            val isSameLogin = mor.tenant.tenantUser.query().apply {
                this.where { it.loginName match tenantAdminUser.loginName }
                this.where { it.id match_not_equal tenantAdminUser.id }
            }.exists()
            if (isSameLogin) {
                return ApiResult.error("用户名重复")
            }
        }
        return ApiResult()
    }

    fun createSetting(): TenantSetting {
        val ts = TenantSetting()
        ts.selfSetting = SelfSetting()
        return ts
    }


    @PostMapping("/reset-pwd")
    fun resetPwd(userId: String, password: String): ApiResult<String> {
        var pwd = password.AsString("Nancal.1234")

        mor.tenant.tenantLoginUser.query().where { it.userId match userId }.toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("用户不存在")
                }
                mor.tenant.tenantLoginUser.updateByUserId(userId)
                    .set { it.password to SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(pwd) + this.passwordSalt) }
                    .set { it.lastUpdatePwdAt to LocalDateTime.now() }
                    .set { it.manualRemindPwdTimes to 0 }
                    .set { it.manualExpirePwdTimes to 0 }
                    .set { it.autoExpirePwdTimes to 0 }
                    .set { it.autoRemindPwdTimes to 0 }
                    .exec()
            }

        return ApiResult.of(pwd)
    }

    @PostMapping("/get-by-code/{code}")
    fun getByCode(@Require code: String): ApiResult<Tenant> {
        var tenants = mor.tenant.tenant.query()
            .where { it.code match code }
            .limit(0, 2)
            .toList();

        if (tenants.size > 1) {
            return ApiResult.error("code不唯一")
        }
        return ApiResult.of(tenants.first())
    }

    @PostMapping("/checkInsertTenantParams")
    fun checkInsertTenantParams(
        tenant: Tenant,
        tenantAdminUser: TenantUser?,
    ): ApiResult<String> {
        if (tenantAdminUser != null) {
            checkInsertParams(tenant, tenantAdminUser)
            return ApiResult()
        } else {
            return ApiResult.error("管理员不能为空")
        }
    }

    @PostMapping("/setting/ldap")
    fun ldap(tenantId: String, ldapSetting: LdapSetting): JsonResult {
        mor.tenant.tenantSecretSet.query()
            .where { it.tenant.id match tenantId }
            .toEntity()
            .apply {
                if (this == null || this.setting.protocol != ProtocolEnum.LDAP) {
                    mor.tenant.tenantUser.query()
                        .where { it.tenant.id match tenantId }
                        .count()
                        .apply {
                            if (this > 0) {
                                return JsonResult.error("租户已有成员数据，不可以修改为LDAP协议")
                            }
                        }
                }
            }

        mor.tenant.tenantSecretSet.query()
            .where { it.setting.ldapSetting.mailSuffix match ldapSetting.mailSuffix }
            .where { it.tenant.id match_not_equal tenantId }
            .toEntity()
            .apply {
                if (this != null) {
                    return JsonResult.error("租户邮箱后缀已被使用")
                }
            }

        mor.tenant.tenantSecretSet.update()
            .where { it.tenant.id match tenantId }
            .set { it.setting.protocol to ProtocolEnum.LDAP }
            .set { it.setting.ldapSetting to ldapSetting }
            .exec()
            .apply {
                return if (this > 0) {
                    JsonResult()
                } else {
                    JsonResult.error("租户设置更新失败")
                }
            }
    }

    @PostMapping("/setting/reset")
    fun reset(tenantId: String): JsonResult {
        mor.tenant.tenantSecretSet.query()
            .where { it.tenant.id match tenantId }
            .toEntity()
            .apply {
                if (this == null || this.setting.protocol != ProtocolEnum.Self) {
                    mor.tenant.tenantUser.query()
                        .where { it.tenant.id match tenantId }
                        .count()
                        .apply {
                            if (this > 0) {
                                return JsonResult.error("租户已有成员数据，不可以重置协议")
                            }
                        }
                }
            }

        mor.tenant.tenantSecretSet.updateByTenantId(tenantId)
            .set { it.setting to createSetting() }
            .exec()
            .apply {
                return if (this > 0) {
                    JsonResult()
                } else {
                    JsonResult.error("租户设置更新失败")
                }
            }
    }


    fun pushAppToIdentitySource(tenantId: String, appList: MutableList<TenantApplication>) {
        var willPushApps = mutableListOf<TenantIdentitySourceApp>()
        appList.forEach {
            var tenantApp = TenantIdentitySourceApp()
            tenantApp.sysAppId = it.sysId
            tenantApp.sysAppStatus = it.enabled
            tenantApp.codeName = CodeName(it.appCode, it.name)
            tenantApp.logo = it.logo
            tenantApp.id = it.id
            tenantApp.isSysDefine = it.isSysDefine
            tenantApp.tenantAppStatus = it.enabled
            willPushApps.add(tenantApp)
        }
        mor.tenant.socialIdentitySourceConfig.update()
            .where { it.tenant.id match tenantId }
            .set { it.tenantApps to willPushApps }
            .exec()
        mor.iam.identitySource.update()
            .where { it.tenant.id match tenantId }
            .set { it.tenantApps to willPushApps }
            .exec()
        /*willPushApps.forEach { will ->
            mor.tenant.socialIdentitySourceConfig.update()
                .where { it.tenant.id match tenantId }
                .push { it.tenantApps to will }
                .exec()
            mor.iam.identitySource.update()
                .where { it.tenant.id match tenantId }
                .push { it.tenantApps to will }
                .exec()

        }*/


    }

    fun pullAppFromIdentitySource(tenantId: String, appCode: String) {
        mor.tenant.socialIdentitySourceConfig.update()
            .where { it.tenant.id match tenantId }
            .pull({ it.tenantApps }, MongoColumnName("codeName.code") match appCode)
            .exec()
        mor.iam.identitySource.update()
            .where { it.tenant.id match tenantId }
            .pull({ it.tenantApps }, MongoColumnName("codeName.code") match appCode)
            .exec()
    }


    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.App, "租户管理")
    @PostMapping("/delete")
    fun delete(
        @Require tenantId: String,
        @Require tenantName: String,
        request: HttpServletRequest
    ): ApiResult<String> {
        request.logMsg = "删除租户{$tenantId - $tenantName}"

        //删除租户
        val tenant = mor.tenant.tenant.queryById(tenantId).toEntity() ?: return ApiResult.error("找不到数据")
        if (tenant.name != tenantName) {
            return ApiResult.error("租户名称不符")
        }
        mor.tenant.tenant.delete()
            .where { it.id match tenant.id }
            .exec()

        //删除租户应用
        mor.tenant.tenantApplication.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //删除租户应用资源
        mor.tenant.tenantResourceInfo.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //删除租户应用角色
        mor.tenant.tenantAppRole.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //删除租户应用资源授权
        mor.tenant.tenantAppAuthResourceInfo.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //资源组
        mor.tenant.tenantResourceGroup.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //资源组授权
        mor.tenant.tenantAuthResourceGroup.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //管理员用户
        mor.tenant.tenantAdminUser.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //管理员账号
        mor.tenant.tenantAdminLoginUser.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //成员
        mor.tenant.tenantUser.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //成员账号
        mor.tenant.tenantLoginUser.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //部门
        mor.tenant.tenantDepartmentInfo.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //部门自定义字段
        mor.tenant.tenantDepartmentInfoFieldExtend.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //用户组
        mor.tenant.tenantUserGroup.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //用户组扩展字段
        mor.tenant.tenantUserFieldExtend.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //企业私密信息
        mor.tenant.tenantSecretSet.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //部门导入数据(成功数据) (动态库)
        mor.tenant.excelDeportmentSuccessJob.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //部门导入数据(失败数据) (动态库)
        mor.tenant.excelDeportmentErrorJob.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //微信登录账号
        mor.tenant.tenantWeChatLoginUser.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //社会化身份源
        mor.tenant.socialIdentitySourceConfig.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //身份源同步风险数据
        mor.tenant.identitySyncRiskData.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //身份源同步任务记录
        mor.tenant.identitySyncJobLog.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //身份源同步任务
        mor.tenant.identitySyncJob.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //授权规则
        mor.tenant.tenantAuthRules.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //应用扩展字段
        mor.tenant.tenantApplicationFieldExtend.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //应用扩展字段数据源字典
        mor.tenant.tenantAppExtendFieldDataSourceDict.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //扩展字段数据源字典
        mor.tenant.tenantExtendFieldDataSourceDict.delete()
            .where { it.tenant.id match tenant.id }
            .exec()
        //岗位字典
        mor.tenant.tenantDutyDict.delete()
            .where { it.tenant.id match tenant.id }
            .exec()


        /*
        tenantAdminRole
        tenantStandardUserGroupAuthResource
        tenantStandardUserAuthResource
        tenantStandardRoleAuthResource
        tenantStandardDeptAuthResource
        identitySyncData
        excelJob
        excelErrorJob
        * */

        return ApiResult()
    }

}
