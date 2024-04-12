package nancal.iam.mvc.ldap

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.*
import nancal.iam.annotation.BizLog
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.mongo.entity.ldap.*
import nancal.iam.service.WebSocketServer
import org.springframework.data.mongodb.core.query.*
import org.springframework.web.bind.annotation.*
import nbcp.comm.*
import nbcp.db.*
import nbcp.db.mongo.*
import nbcp.db.mongo.entity.*
import nbcp.base.mvc.*
import nbcp.web.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.core.support.LdapContextSource
import org.springframework.ldap.odm.annotations.Attribute
import org.springframework.ldap.odm.annotations.Entry
import org.springframework.ldap.odm.annotations.Id
import javax.servlet.http.*
import java.time.*
import javax.naming.Name
import org.springframework.ldap.query.LdapQueryBuilder
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.naming.NamingException
import org.springframework.ldap.core.AttributesMapper
import java.io.IOException
import java.math.BigInteger
import javax.naming.directory.Attributes


/**
 * Created by CodeGenerator at 2022-02-11 17:16:26
 */
@Api(description = "identitySyncJob", tags = arrayOf("IdentitySyncJob"))
@RestController
@RequestMapping("/tenant/identity-sync-job")
class IdentitySyncJobController {

    class IdentitySyncJobVO(
        @Cn("同步任务")
        var job: IdentitySyncJob? = null,
        @Cn("同步任务日志")
        var jobLog: IdentitySyncJobLog? = null,
    )

    class IdentityStatusVO(
        @Cn("ldap")
        var ldap: Boolean = false,

        @Cn("saml")
        var saml: Boolean = false,

        @Cn("cas")
        var cas: Boolean = false,

        @Cn("oidc")
        var oidc: Boolean = false,

        @Cn("oauth2.0")
        var oauth: Boolean = false,

        @Cn("windowsAD")
        var windowsAD: Boolean = false,
    )

    @Entry(objectClasses = ["user"])
    class LdapUser(
        @Id
        @JsonIgnore
        var distinguishedName: Name? = null,

        /* 唯一标识 */
//        @Attribute(name = "objectSid")
//        var objectSid: String,

//        @Attribute(name = "objectGUID")
//        var objectGUID: String,

//        @Attribute(name = "uSNCreated")
//        var uSNCreated: String,

        /* 登录账号 */
        @Attribute(name = "sAMAccountName")
        var loginName: String = "",

        /* 用户姓名 */
        @Attribute(name = "name")
        var name: String = "",

        /* 邮箱 */
        @Attribute(name = "mail")
        var email: String = "",

        /* 电话 */
        @Attribute(name = "telephoneNumber")
        var mobile: String = "",

        /* 职务 */
        @Attribute(name = "title")
        var title: String = "",

        /* 描述 */
        @Attribute(name = "description")
        var remark: String = "",
    )

    @Entry(objectClasses = ["organizationalUnit"])
    class LdapDepartment(
        @Id
        @JsonIgnore
        var distinguishedName: Name? = null,

        /* 唯一标识 */
//        @Attribute(name = "objectSid")
//        var objectSid: String,

//        @Attribute(name = "objectGUID")
//        var objectGUID: String,

//        @Attribute(name = "uSNCreated")
//        var uSNCreated: Any,

        /* 部门名称 */
        @Attribute(name = "name")
        var name: String = "",

        /* 描述 */
        @Attribute(name = "description")
        var remark: String = "",
    )

    @Autowired
    lateinit var ldapTemplate: LdapTemplate

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        identitySource: ProtocolEnum,
        request: HttpServletRequest
    ): ApiResult<IdentitySyncJobVO> {
        val tenant = request.LoginTenantAdminUser.tenant

        mor.tenant.identitySyncJob.query()
            .where { it.tenant.id match tenant.id }
            .where { it.identitySource match identitySource }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult()
                }
                val result = IdentitySyncJobVO()
                result.job = this

                mor.tenant.identitySyncJobLog.query()
                    .where { it.jobId match this.id }
                    .where { it.tenant.id match tenant.id }
                    .where { it.identitySource match identitySource }
                    .orderByDesc { it.createAt }
                    .orderByAsc { it.id }
                    .limit(0, 1)
                    .toEntity()
                    .apply {
                        result.jobLog = this
                    }

                return ApiResult.of(result)
            }
    }

    @ApiOperation("详情")
    @PostMapping("/detail/{id}")
    fun detail(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<IdentitySyncJob> {
        val tenant = request.LoginTenantAdminUser.tenant

        mor.tenant.identitySyncJob.queryById(id)
            .where { it.tenant.id match tenant.id }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }

                return ApiResult.of(this)
            }
    }


    @BizLog(action = BizLogActionEnum.Save, resource = BizLogResourceEnum.SyncJob, module = "同步任务")
    @ApiOperation("更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: IdentitySyncJob,
        request: HttpServletRequest
    ): ApiResult<String> {
        request.logMsg = "保存同步任务"

        val tenant = request.LoginTenantAdminUser.tenant
        mor.tenant.identitySyncJob.query()
            .where { it.tenant.id match tenant.id }
            .where { it.identitySource match entity.identitySource }
            .where { it.id match_not_equal entity.id }
            .toEntity()
            .apply {
                if (this != null) {
                    return ApiResult.error("同步任务只能有一个")
                }
            }

        mor.tenant.identitySyncJob.updateWithEntity(entity)
            
            .run {
                if (entity.id.HasValue) {
                    return@run this.execUpdate()
                } else {
                    entity.tenant = tenant
                    return@run this.execInsert()
                }
            }
            .apply {
                if (this == 0) {
                    return ApiResult.error("更新失败")
                }

                return ApiResult.of(entity.id)
            }
    }

    @BizLog(action = BizLogActionEnum.Delete, resource = BizLogResourceEnum.SyncJob, module = "同步任务")
    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "删除同步任务"

        val tenant = request.LoginTenantAdminUser.tenant

        mor.tenant.identitySyncJob.queryById(id)
            .where { it.tenant.id match tenant.id }
            .toEntity() ?: return JsonResult.error("找不到数据")

        mor.tenant.identitySyncJob.deleteById(id)
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
                //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
                return JsonResult()
            }
    }

    @ApiOperation("身份源状态")
    @PostMapping("/status")
    fun identityStatus(
        request: HttpServletRequest
    ): ApiResult<IdentityStatusVO> {
        val result = IdentityStatusVO()
        val tenant = request.LoginTenantAdminUser.tenant
        mor.tenant.identitySyncJob.query()
            .select { it.identitySource }
            .where { it.tenant.id match tenant.id }
            .toList(ProtocolEnum::class.java)
            .apply {
                if (this.isNotEmpty()) {
                    val distinct = this.distinct()
                    if (distinct.contains(ProtocolEnum.LDAP)) {
                        result.ldap = true
                    }
                }
            }
        return ApiResult.of(result)
    }

    @BizLog(action = BizLogActionEnum.Sync, resource = BizLogResourceEnum.SyncJob, module = "同步任务")
    @ApiOperation("手动同步")
    @PostMapping("/run")
    fun run(
        jobId: String,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "执行同步任务"

        val tenant = request.LoginTenantAdminUser.tenant

        //记录同步任务日志
        val jobLog = IdentitySyncJobLog()
        jobLog.tenant = tenant

        //获取LDAP配置
        val ldapSetting = mor.iam.identitySource.query()
            .where { it.tenant.id match tenant.id }
            .toEntity()

        if (ldapSetting == null) {
            jobLog.msg = "找不LDAP认证信息"
            mor.tenant.identitySyncJobLog.doInsert(jobLog)
            return JsonResult.error("请先填写LDAP认证信息")
        }

        //根据配置初始化 ldapTemplate
        val apiResult = getLdapTemplate(ldapSetting)
        if (apiResult.msg.HasValue) {
            return apiResult
        }
        ldapTemplate = apiResult.data!!

        //测试连接
        try {
            ldapTemplate.authenticate(
                LdapQueryBuilder.query().where("distinguishedName").`is`(ldapSetting.bindDN),
                ldapSetting.bindDNPassword
            )
        } catch (ex: Exception) {
            jobLog.msg = "LDAP认证信息连接失败"
            mor.tenant.identitySyncJobLog.doInsert(jobLog)
            return JsonResult.error("LDAP认证信息连接失败")
        }

        //获取同步数据规则
        mor.tenant.identitySyncJob.query()
            .where { it.tenant.id match tenant.id }
            .where { it.id match jobId }
            .toEntity()
            .apply {
                if (this == null) {
                    jobLog.msg = "找不到同步任务：$jobId"
                    mor.tenant.identitySyncJobLog.doInsert(jobLog)
                    return JsonResult.error("请先添加LDAP同步任务")
                }

                jobLog.identitySource = this.identitySource
                jobLog.jobId = this.id

                //更新任务状态
                this.status = SyncJobStatusEnum.Runing
                mor.tenant.identitySyncJob.updateWithEntity(this).execUpdate()
            }

        //启线程
        Executors.newSingleThreadExecutor().submit {
            try {
                TimeUnit.SECONDS.sleep(1)
                //执行同步
                execSync(jobLog, tenant)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        return JsonResult()
    }

    /** 执行同步 **/
    fun execSync(jobLog: IdentitySyncJobLog, tenant: IdName) {
        val errorDatas = mutableListOf<IdentitySyncData>()
        //同步部门数据
        syncDept(jobLog, tenant, errorDatas)
        //同步用户数据
        syncUser(jobLog, tenant, errorDatas)
        //记录同步任务日志
        val jobLogId = mor.tenant.identitySyncJobLog.doInsert(jobLog)
        errorDatas.forEach {
            it.jobId = jobLog.jobId
            it.jobLogId = jobLogId
        }
        mor.tenant.identitySyncData.batchInsert()
            .apply {
                addEntities(errorDatas)
            }
            .exec()
        //更新任务状态
        mor.tenant.identitySyncJob.updateById(jobLog.jobId)
            .set { it.status to SyncJobStatusEnum.Completed }
            .exec()
        //发送消息到前端
        WebSocketServer.sendMessageToTenant("Completed", tenant.id)
    }

    /** 同步部门数据 **/
    fun syncDept(jobLog: IdentitySyncJobLog, tenant: IdName, errorDatas: MutableList<IdentitySyncData>) {
        //获取部门数据，根据配置的baseDN
        val ldapDepartmentList = ldapTemplate.findAll(LdapDepartment::class.java)

        //删除的数据，保存到风险操作
        //AD的部门标识
        val dns = ldapDepartmentList.map { it.name }
        //已存在的删除部门风险操作
        val ids = mor.tenant.identitySyncRiskData.query()
            .select { it.objectData.id }
            .where { it.objectType match SyncJobDataObjectTypeEnum.Dept }
            .where { it.identitySource match ProtocolEnum.LDAP }
            .where { it.tenant.id match tenant.id }
            .toList(String::class.java)

        mor.tenant.tenantDepartmentInfo.query()
            .where { it.tenant.id match tenant.id }
            .where { it.identitySource match ProtocolEnum.LDAP }
            .where { it.name match_notin dns }
            .where { it.id match_notin ids }
            .toList()
            .apply {
                var riskData: IdentitySyncRiskData
                val riskDataList = mutableListOf<IdentitySyncRiskData>()
                this.forEach {
                    riskData = IdentitySyncRiskData()
                    riskData.tenant = tenant
                    riskData.objectType = SyncJobDataObjectTypeEnum.Dept
                    riskData.objectData = IdName(it.id, it.name)
                    riskData.syncType = "删除部门"
                    riskDataList.add(riskData)
                }

                mor.tenant.identitySyncRiskData.batchInsert()
                    .apply {
                        addEntities(riskDataList)
                    }
                    .exec()
            }

        //新增和更新的数据
        var dn: String
        var parentDN: String
        var parent = IdName()
        var errorData: IdentitySyncData

        var duplicateDept: TenantDepartmentInfo?
        var tenantDept: TenantDepartmentInfo
        ldapDepartmentList.forEach { ldapDept ->
            dn = ldapDept.distinguishedName.toString()

            errorData = IdentitySyncData()
            errorData.objectType = SyncJobDataObjectTypeEnum.Dept
            errorData.objectData = IdName(dn, ldapDept.name)
            errorData.result = "成功"

            //重名验证
            duplicateDept = mor.tenant.tenantDepartmentInfo.query()
                .where { it.tenant.id match tenant.id }
                .where { it.parent.id match parent.id }
                .where { it.name match ldapDept.name }
                .toEntity()

            //查询数据库是否存在
            mor.tenant.tenantDepartmentInfo.query()
                .where { it.tenant.id match tenant.id }
                .where { it.distinguishedName match dn }
                .toEntity()
                .apply {
                    if (this != null) {
                        errorData.syncType = "更新部门"

                        if (duplicateDept != null && duplicateDept?.id != this.id) {
                            jobLog.errorNumber += 1
                            errorData.result = "失败"
                            errorData.msg = "部门名称重复"
                        } else {
                            this.name = ldapDept.name
                            this.remark = ldapDept.remark

                            mor.tenant.tenantDepartmentInfo.updateWithEntity(this).execUpdate()
                            jobLog.successNumber += 1
                        }
                    } else {
                        errorData.syncType = "创建部门"

                        if (duplicateDept != null) {
                            jobLog.errorNumber += 1
                            errorData.result = "失败"
                            errorData.msg = "部门已存在"
                        } else {
                            //查找上级部门，如果库里没有就从集合中查找并保存
                            if (dn.contains(",")) {
                                parentDN = dn.substringAfter(",")
                                mor.tenant.tenantDepartmentInfo.query()
                                    .where { it.tenant.id match tenant.id }
                                    .where { it.distinguishedName match parentDN }
                                    .toEntity()
                                    .apply {
                                        if (this != null) {
                                            parent = IdName(this.id, this.name)
                                        }
                                    }
                            }

                            tenantDept = TenantDepartmentInfo()
                            tenantDept.tenant = tenant
                            tenantDept.name = ldapDept.name
                            tenantDept.parent = parent
                            tenantDept.remark = ldapDept.remark
                            tenantDept.distinguishedName = dn
                            tenantDept.identitySource = ProtocolEnum.LDAP

                            mor.tenant.tenantDepartmentInfo.doInsert(tenantDept)
                            jobLog.successNumber += 1
                        }
                    }
                }

            errorDatas.add(errorData)
        }
    }

    /** 同步用户数据 **/
    fun syncUser(jobLog: IdentitySyncJobLog, tenant: IdName, errorDatas: MutableList<IdentitySyncData>) {

        //TODO 用户的启用停用状态同步

        //获取用户数据，根据配置的baseDN
        val ldapUserList = ldapTemplate.findAll(LdapUser::class.java)

        //删除的数据，保存到风险操作
        //AD的用户标识
        val loginNames = ldapUserList.map { it.loginName }

        //已存在的删除成员风险操作
        val ids = mor.tenant.identitySyncRiskData.query()
            .select { it.objectData.id }
            .where { it.objectType match SyncJobDataObjectTypeEnum.User }
            .where { it.identitySource match ProtocolEnum.LDAP }
            .where { it.tenant.id match tenant.id }
            .toList(String::class.java)

        mor.tenant.tenantUser.query()
            .where { it.tenant.id match tenant.id }
            .where { it.identitySource match ProtocolEnum.LDAP }
            .where { it.loginName match_notin loginNames }
            .where { it.id match_notin ids }
            .toList()
            .apply {
                var riskData: IdentitySyncRiskData
                val riskDataList = mutableListOf<IdentitySyncRiskData>()
                this.forEach {
                    riskData = IdentitySyncRiskData()
                    riskData.tenant = tenant
                    riskData.objectType = SyncJobDataObjectTypeEnum.User
                    riskData.objectData = IdName(it.id, it.name)
                    riskData.syncType = "删除成员"
                    riskDataList.add(riskData)
                }

                mor.tenant.identitySyncRiskData.batchInsert()
                    .apply {
                        addEntities(riskDataList)
                    }
                    .exec()
            }

        //新增和更新的数据
        var dn: String
        var parentDN: String
        var errorData: IdentitySyncData

        var duplicateMobile: TenantUser? = null
        var duplicateEmail: TenantUser? = null
        var duplicateLoginName: TenantUser?
        var tenantUser: TenantUser
        var loginUser: TenantLoginUser
        var dept: DeptDefine? = null
        ldapUserList.forEach { ldapUser ->
            dn = ldapUser.distinguishedName.toString()

            errorData = IdentitySyncData()
            errorData.objectType = SyncJobDataObjectTypeEnum.User
            errorData.objectData = IdName(dn, ldapUser.name)
            errorData.result = "成功"

            //重名验证
            if (ldapUser.mobile.HasValue) {
                duplicateMobile = mor.tenant.tenantUser.query()
                    .where { it.tenant.id match tenant.id }
                    .where { it.mobile match ldapUser.mobile }
                    .toEntity()
            }
            if (ldapUser.email.HasValue) {
                duplicateEmail = mor.tenant.tenantUser.query()
                    .where { it.tenant.id match tenant.id }
                    .where { it.email match ldapUser.email }
                    .toEntity()
            }
            duplicateLoginName =
                mor.tenant.tenantUser.query().where { it.loginName match ldapUser.loginName }.toEntity()

            //查询数据库是否存在
            mor.tenant.tenantUser.query()
                .where { it.tenant.id match tenant.id }
                .where { it.distinguishedName match dn }
                .toEntity()
                .apply {
                    if (this != null) {
                        errorData.syncType = "更新成员"

                        if (duplicateLoginName != null && duplicateLoginName?.id != this.id) {
                            jobLog.errorNumber += 1
                            errorData.result = "失败"
                            errorData.msg = "成员 ${ldapUser.name}，用户名重复：${ldapUser.loginName}"
                        } else if (duplicateMobile != null && duplicateMobile?.id != this.id) {
                            jobLog.errorNumber += 1
                            errorData.result = "失败"
                            errorData.msg = "成员 ${ldapUser.name}，手机号重复：${ldapUser.mobile}"
                        } else if (duplicateEmail != null && duplicateEmail?.id != this.id) {
                            jobLog.errorNumber += 1
                            errorData.result = "失败"
                            errorData.msg = "成员 ${ldapUser.name}，邮箱重复：${ldapUser.email}"
                        } else {
                            this.loginName = ldapUser.loginName
                            this.email = ldapUser.email
                            this.mobile = ldapUser.mobile
                            this.name = ldapUser.name
                            this.duty.name = ldapUser.title
                            this.remark = ldapUser.remark

                            mor.tenant.tenantUser.updateWithEntity(this).execUpdate()
                            jobLog.successNumber += 1
                        }
                    } else {
                        errorData.syncType = "创建成员"

                        if (duplicateLoginName != null) {
                            jobLog.errorNumber += 1
                            errorData.result = "失败"
                            errorData.msg = "成员 ${ldapUser.name}，用户名重复：${ldapUser.loginName}"
                        } else if (duplicateMobile != null) {
                            jobLog.errorNumber += 1
                            errorData.result = "失败"
                            errorData.msg = "成员 ${ldapUser.name}，手机号重复：${ldapUser.mobile}"
                        } else if (duplicateEmail != null) {
                            jobLog.errorNumber += 1
                            errorData.result = "失败"
                            errorData.msg = "成员 ${ldapUser.name}，邮箱重复：${ldapUser.email}"
                        } else {
                            //获取部门
                            parentDN = dn.substringAfter(",")
                            mor.tenant.tenantDepartmentInfo.query()
                                .where { it.tenant.id match tenant.id }
                                .where { it.distinguishedName match parentDN }
                                .toEntity()
                                .apply {
                                    if (this != null) {
                                        dept = DeptDefine(true)
                                        dept?.id = this.id
                                        dept?.name = this.name
                                    }
                                }

                            tenantUser = TenantUser()
                            tenantUser.tenant = tenant
                            tenantUser.loginName = ldapUser.loginName
                            tenantUser.email = ldapUser.email
                            tenantUser.mobile = ldapUser.mobile
                            tenantUser.name = ldapUser.name
                            tenantUser.duty.name = ldapUser.title
                            tenantUser.remark = ldapUser.remark
                            tenantUser.identitySource = ProtocolEnum.LDAP
                            tenantUser.distinguishedName = dn
                            if (dept != null) {
                                tenantUser.depts.add(dept!!)
                            }
                            mor.tenant.tenantUser.doInsert(tenantUser)

                            loginUser = TenantLoginUser()
                            loginUser.tenant = tenant
                            loginUser.loginName = ldapUser.loginName
                            loginUser.email = ldapUser.email
                            loginUser.mobile = ldapUser.mobile
                            loginUser.userId = tenantUser.id
                            mor.tenant.tenantLoginUser.doInsert(loginUser)
                            jobLog.successNumber += 1
                        }
                    }
                }
            duplicateEmail = null
            duplicateMobile = null
            dept = null
            errorDatas.add(errorData)
        }
    }

    /** 初始化 ldapTemplate **/
    fun getLdapTemplate(ldapSetting: IdentitySource): ApiResult<LdapTemplate> {
        try {
            System.setProperty("com.sun.jndi.ldap.object.disableEndpointIdentification", "true")
            System.setProperty("java.naming.ldap.attributes.binary", "objectSid")

            val contextSource = LdapContextSource()
            contextSource.setUrl(ldapSetting.url)
            contextSource.setBase(ldapSetting.baseDN)
            contextSource.userDn = ldapSetting.bindDN
            contextSource.password = ldapSetting.bindDNPassword
            contextSource.isPooled = false
            contextSource.afterPropertiesSet() // important

            val baseEnvMaps = Hashtable<String, Any>()
            baseEnvMaps["java.naming.ldap.attributes.binary"] = "objectSid"
            contextSource.setBaseEnvironmentProperties(baseEnvMaps)

            val template = LdapTemplate(contextSource)
            template.setIgnorePartialResultException(true)

            return ApiResult.of(template)
        } catch (ex: Exception) {
            return ApiResult.error("LDAP认证信息连接失败")
        }
    }

    /** 解析 ObjectSid **/
    fun getObjectSid(SID: ByteArray): String {
        val strSID = StringBuilder("S-")
        strSID.append(SID[0]).append('-')
        val tmpBuff = StringBuilder()
        for (t in 2..7) {
            val hexString = Integer.toHexString(SID[t].toInt() and (0xFF))
            tmpBuff.append(hexString)
        }
        strSID.append(tmpBuff.toString().toLong(16))
        val count = SID[1].toInt()
        for (i in 0 until count) {
            val currSubAuthOffset = i * 4
            tmpBuff.setLength(0)
            tmpBuff.append(
                java.lang.String.format(
                    "%02X%02X%02X%02X",
                    SID[11 + currSubAuthOffset].toInt() and (0xFF),
                    SID[10 + currSubAuthOffset].toInt() and (0xFF),
                    SID[9 + currSubAuthOffset].toInt() and (0xFF),
                    SID[8 + currSubAuthOffset].toInt() and (0xFF)
                )
            )
            strSID.append('-').append(tmpBuff.toString().toLong(16))
        }
        return strSID.toString()
    }

    /** 查询 ObjectSid **/
    @PostMapping("/getSid")
    fun getSid(
        name: String,
        request: HttpServletRequest
    ): ApiResult<String> {
        val tenant = request.LoginTenantAdminUser.tenant
        var objectSidStr = ""

        //获取LDAP配置
        val ldapSetting = mor.iam.identitySource.query()
            .where { it.tenant.id match tenant.id }
            .toEntity() ?: return ApiResult.error("请先填写LDAP认证信息")

        //根据配置初始化 ldapTemplate
        val apiResult = getLdapTemplate(ldapSetting)
        if (apiResult.msg.HasValue) {
            return ApiResult.error(apiResult.msg)
        }

        ldapTemplate = apiResult.data!!

        ldapTemplate.search(
            LdapQueryBuilder.query()
                .where("objectclass").`is`("user")
                .and("sAMAccountName").`is`(name)
        ) { attributes: Attributes ->
            try {
                val objectGUID = attributes["objectGUID"].get()
                val uSNCreated: String = attributes["uSNCreated"].get().toString()
                val objectSid = attributes["objectSid"].get()

                val bytes = objectSid.AsString().toByteArray()
                objectSidStr = getObjectSid(bytes)
            } catch (e: NamingException) {
                e.printStackTrace()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return ApiResult.of(objectSidStr)
    }

}