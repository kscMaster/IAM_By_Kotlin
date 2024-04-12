package nancal.iam.mvc.tenant

import cn.hutool.core.io.resource.ClassPathResource
import cn.hutool.poi.excel.ExcelUtil
import com.nancal.cipher.SHA256Util
import io.swagger.annotations.*
import nancal.iam.annotation.BizLog
import nancal.iam.client.MPClient
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.mongo.extend.queryByDeptFullName
import nancal.iam.util.EmailMessage
import nancal.iam.util.MailUtil
import nancal.iam.util.PwdVerifyStrategy
import nancal.iam.util.ValidateUtils
import nbcp.comm.*
import nbcp.db.*
import nbcp.db.excel.ExcelComponent
import nbcp.db.mongo.*
import nbcp.model.DataTable
import nbcp.utils.Md5Util
import org.bson.Document
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.time.LocalDateTime
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.*


/**
 * Created by CodeGenerator at 2021-11-17 15:48:36
 */
@Api(description = "租户用户", tags = arrayOf("TenantUser"))
@RestController
@RequestMapping("/tenant/tenant-user")
class TenantUserAutoController {
    @Value("\${openPrivatization}")
    private val openPrivatization: Boolean = true
    @Resource
    lateinit var mpClient: MPClient

    class IdNameRemark : IdName() {
        var remark: String = ""
    }

    class UserVO {
        var id : String = ""
        var name : String = ""
        var remark :String = ""
        var userID :String = ""
        var sendPasswordType :SendPasswordType? = null
        var loginName :String = ""
        var mobile :String = ""
        var tenant :IdName = IdName()
//        var allowApps: MutableList<CodeName> = mutableListOf()
        var roles :MutableList<IdName> = mutableListOf()
        var depts: MutableList<DeptDefine> = mutableListOf()
        var groups: MutableList<IdNameRemark> = mutableListOf()
        var enabled:Boolean = true
        var createAt :LocalDateTime = LocalDateTime.now()
        var updateAt :LocalDateTime = LocalDateTime.now()
    }

    @ApiOperation("用户批量查询")
    @PostMapping("/bathIds")
    fun bathIds(
        @Require ids: List<String>,
        request: HttpServletRequest
    ): ApiResult<List<UserVO>> {
        val res: MutableList<UserVO> = mutableListOf()
        if (ids.isEmpty()) {
            return ApiResult.error("参数不能为空")
        }
        val toList: MutableList<Document> = mor.tenant.tenantUser.query()
            .where { it.id match_in ids }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .apply {
                if (openPrivatization) {
                    this.where { it.adminType match TenantAdminTypeEnum.None }
                }
            }.toList(Document::class.java)
        if (toList.isNotEmpty()) {
            toList.forEach { item->
                val user = item.ConvertJson(TenantUser::class.java)
                val vo = UserVO()
                BeanUtils.copyProperties(user, vo)
                val dbAllGroups: MutableList<IdNameRemark> = mor.tenant.tenantUserGroup.query()
                    .where { it.id match_in user.groups.map { it.id } }
                    .toList(IdNameRemark::class.java)
                vo.depts = item.get("depts") as MutableList<DeptDefine>
                vo.groups = dbAllGroups
                vo.userID = item.get("userID").toString()
                vo.mobile = user.mobile
                vo.remark = user.remark
                res.add(vo)
            }
            return ApiResult.of(res)
        }
        return ApiResult.error("找不到数据")
    }

    @ApiOperation("详情")
    @PostMapping("/detail/{id}")
    fun detail(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<Document> {

        mor.tenant.tenantUser.query()
            .where { it.id match id }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .toEntity(Document::class.java)
            .apply {
                if (this == null) {
                    return ApiResult.error<Document>("找不到数据")
                }
                val user = this.ConvertJson(TenantUser::class.java)
                val dbAllGroups = mor.tenant.tenantUserGroup.query()
                    .where { it.id match_in user.groups.map { it.id } }
                    .toList(IdNameRemark::class.java)
                this.set("groups", dbAllGroups)
                return ApiResult.of(this)
            }
    }


    @ApiOperation("详情")
    @PostMapping("/detail/getByLoginName")
    fun getByLoginName(
        @Require loginName: String,
        request: HttpServletRequest
    ): ApiResult<Document> {

        mor.tenant.tenantUser.query().where { it.loginName match loginName }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .toEntity(Document::class.java)
            .apply {
                if (this == null) {
                    return ApiResult.error<Document>("找不到数据")
                }
                val user = this.ConvertJson(TenantUser::class.java)
                val dbAllGroups = mor.tenant.tenantUserGroup.query()
                    .where { it.id match_in user.groups.map { it.id } }
                    .toList(IdNameRemark::class.java)
                this.set("groups", dbAllGroups)
                return ApiResult.of(this)
            }
    }

    @ApiOperation("详情")
    @PostMapping("/detailById")
    fun detailById(
        @JsonModel entity: TenantUser
    ): ApiResult<Document> {
        mor.tenant.tenantUser.queryById(entity.id)
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .toEntity(Document::class.java)
            .apply {
                if (this == null) {
                    return ApiResult.error<Document>("找不到数据")
                }
                val user = this.ConvertJson(TenantUser::class.java)
                val dbAllGroups = mor.tenant.tenantUserGroup.query()
                    .where { it.id match_in user.groups.map { it.id } }
                    .toList(IdNameRemark::class.java)
                this.set("groups", dbAllGroups)
                return ApiResult.of(this)
            }
    }

    @ApiOperation("租户查询")
    @PostMapping("/detailByUserId")
    fun detailByUserId(
        id:String
    ): ApiResult<TenantUser> {
        var tenantUser: TenantUser = mor.tenant.tenantUser.query()
            .where { it.id match id }.toEntity().must().elseThrow { "找不到用户" }
        return ApiResult.of(tenantUser)
    }


    @BizLog(BizLogActionEnum.Save, BizLogResourceEnum.User, "成员管理")
    @ApiOperation("更新")
    @PostMapping("/save")
    fun save(
        @JsonModel tenantUserMap: Document,
        request: HttpServletRequest
    ): ApiResult<String> {

        val loginUser = request.LoginTenantAdminUser
        val tenantUser = tenantUserMap.ConvertJson(TenantUser::class.java)
        tenantUser.tenant = loginUser.tenant

        //修改
        if (tenantUser.id.HasValue) {
            //业务日志
            request.logMsg = "修改成员{${tenantUser.name}}"

            val toEntity = mor.tenant.tenantUser.queryById(tenantUser.id)
                .apply {
                    if(openPrivatization){
                        this.where { it.adminType match  TenantAdminTypeEnum.None}
                    }
                }
                .toEntity() ?: return ApiResult.error("用户不存在")

            tenantUser.sendPasswordType = toEntity.sendPasswordType

            if (toEntity.adminType != TenantAdminTypeEnum.None) {
                return ApiResult.error("不可以编辑租户管理员")
            }

            if (tenantUser.loginName.isEmpty()) {
                tenantUser.loginName = toEntity.loginName
            }
        } else {
            //业务日志
            request.logMsg = "创建成员{${tenantUser.name}}"


            //不传发送密码方式，默认 Mobile
            if (tenantUser.sendPasswordType == null) {
                tenantUser.sendPasswordType = SendPasswordType.Mobile
                tenantUserMap.put("sendPasswordType", "Mobile")
                if(!tenantUser.mobile.HasValue){
                    return ApiResult.error("发送方式默认为手机,请填写手机号")
                }
            }else if(tenantUser.sendPasswordType == SendPasswordType.Defined){
                if(tenantUserMap.get("password")==""){
                    return ApiResult.error("密码不能为空")
                }
            }

            tenantUser.adminType = TenantAdminTypeEnum.None
        }

        //新增不传loginName情况,默认给一串不重复的uuid
        if (!tenantUser.id.HasValue && !tenantUser.loginName.HasValue) {
            val value = checkInsertLoginName(loginUser.tenant.id)
            tenantUserMap.put("loginName", value)
            tenantUser.loginName = value
        }

        //参数验证
        val validParam = validParam(tenantUser)
        if (validParam.msg.HasValue) {
            return validParam
        }


        var mainCount = 0
        // 校验部门
        tenantUser.depts.forEach { dept ->
            mor.tenant.tenantDepartmentInfo.query()
                .where { it.id match dept.id }
                .where { it.name match dept.name  }
                .toEntity()
                .apply {
                    if(this == null){
                        return ApiResult.error("部门不存在")
                    }
                    if(dept.isMain ==true){
                        mainCount++
                    }
                }
        }

        if(mainCount >1){
            return ApiResult.error("每个人只能有且只能有一个主部门")
        }
        if(mainCount ==0&&  tenantUser.depts.size >0){
            tenantUser.depts.get(0).isMain =true
        }

        if(tenantUser.depts.size == 0){

            mor.tenant.tenantDepartmentInfo.query()
                .where { it.name match tenantUser.tenant.name }
                .where { it.tenant.id match tenantUser.tenant.id }
                .orderByAsc { it.createAt }
                .toList()
                .apply {
                    if(this.size>0){
                        var deptTemp = DeptDefine()
                        deptTemp.id = this.get(0).id
                        deptTemp.name = this.get(0).name
                        deptTemp.isMain = true
                        tenantUser.depts.add(deptTemp)
                    }
                }

        }

        tenantUserMap.put("tenant", tenantUser.tenant)
        tenantUserMap.put("depts", tenantUser.depts)
        tenantUserMap.put("roles", tenantUser.roles)
        tenantUserMap.put("groups", tenantUser.groups)
        tenantUserMap.put("leader", tenantUser.leader)
        tenantUserMap.put("groups", tenantUser.groups)
        tenantUserMap.put("enabled", tenantUser.enabled)
        tenantUserMap.put("personClassified", tenantUser.personClassified)
        tenantUserMap.put("adminType", "None")

        var isInsert = false
        mor.tenant.tenantUser.updateWithEntity(tenantUserMap)
            .run {
                if (tenantUser.id.HasValue) {
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
                if (isInsert) {

                    val loginUser = TenantLoginUser()
                    loginUser.userId = tenantUserMap.get("_id").AsString()
                    loginUser.email = tenantUser.email
                    loginUser.mobile = tenantUser.mobile
                    var pwd = UUID.randomUUID().toString().replace("-", "").substring(0, 6)
                    var salt = UUID.randomUUID().toString().replace("-", "").substring(0, 4)
                    loginUser.passwordSalt = salt
                    if(tenantUser.sendPasswordType == SendPasswordType.Defined){
                        pwd = tenantUserMap.get("password").AsString()
                        if(pwd.trim().equals("")){
                            return ApiResult.error("密码不能为空")
                        }
                        loginUser.password = SHA256Util.getSHA256StrJava(pwd + salt)
                    }else {
                        loginUser.password = SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(pwd) + salt)
                    }
                    loginUser.loginName = tenantUser.loginName
                    loginUser.isLocked = false
                    loginUser.tenant.id = tenantUser.tenant.id
                    loginUser.tenant.name = tenantUser.tenant.name
                    loginUser.isFirstLogin = true
                    mor.tenant.tenantLoginUser.doInsert(loginUser)

                    // 发送密码
                    if (tenantUser.isSendPassword) {
                        if (tenantUser.sendPasswordType == SendPasswordType.Mobile) {
                            val sendSmsCode =
                                mpClient.sendSmsPwd(MobileCodeModuleEnum.SendPassword, tenantUser.mobile, pwd)
                            val success = sendSmsCode.success()
                            println(success.toString())
                        } else if (tenantUser.sendPasswordType == SendPasswordType.Email) {
                            val msg = EmailMessage()
                            msg.sender = mailSender
                            msg.password = mailPwd
                            msg.addressee = listOf(tenantUser.email)
                            msg.topic = "【能科瑞元】登录密码"
                            msg.content = "您好，${tenantUser.loginName}\n" +
                                " <p style=\"text-indent:2em\">您的登录密码为：$pwd 。为了您账户的</br>" +
                                "安全请勿将密码告知他人。</br>"
                            msg.popService = mailPop
                            msg.smtpService = mailSmtp
                            mailUtil.sendEmail(msg)
                        }
                    }
                } else {
                    // 修改授权target
                    mor.tenant.tenantAppAuthResourceInfo.update()
                        .where { it.type match AuthTypeEnum.People }
                        .where { it.target.id match tenantUserMap.get("id").AsString() }
                        .set { it.target.name to tenantUserMap.get("name").AsString() }
                        .exec()
                }

                return ApiResult.of(tenantUserMap.get("_id").AsString())
            }
    }

    /* 验证参数 */
    fun validParam(tenantUser: TenantUser): ApiResult<String> {
        //loginName 不能是Mobile，不能是 email格式
        val matchPhoneParttern =
            "^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$"
        val matchEmailParttern = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$"

        if (tenantUser.loginName.HasValue) {
            val isPhoneMatch = Regex(matchPhoneParttern).containsMatchIn(tenantUser.loginName)
            if (isPhoneMatch) {
                return ApiResult.error("loginName不能是手机号格式")
            }
            val isEmailMatch = Regex(matchEmailParttern).containsMatchIn(tenantUser.loginName)
            if (isEmailMatch) {
                return ApiResult.error("loginName不能是邮箱格式")
            }
        }
        if (tenantUser.sendPasswordType != null) {
            if (tenantUser.sendPasswordType == SendPasswordType.Email) {

                if (tenantUser.email.HasValue) {
                    val isEmailMatch = Regex(matchEmailParttern).containsMatchIn(tenantUser.email)
                    if (!isEmailMatch) {
                        return ApiResult.error("邮箱格式不正确")
                    }
                } else {
                    return ApiResult.error("请填写邮箱")
                }
            }
            if (tenantUser.sendPasswordType == SendPasswordType.Mobile) {
                if (tenantUser.mobile.HasValue) {
                    val isPhoneMatch = Regex(matchPhoneParttern).containsMatchIn(tenantUser.mobile)
                    if (!isPhoneMatch) {
                        return ApiResult.error("手机格式不正确")
                    }
                }else {
                    return ApiResult.error("请填写手机号")
                }
            }
        } else {
            return ApiResult.error("请选择密码发送方式")
        }

        if (!tenantUser.loginName.HasValue) {
            return ApiResult.error("请填写用户名")
        }
        if (tenantUser.loginName.length < 6) {
            return ApiResult.error("用户名至少6个字符")
        }
        if (tenantUser.loginName.length > 32) {
            return ApiResult.error("用户名不能超过32个字符")
        }

        if (tenantUser.remark.HasValue && tenantUser.remark.length > 255) {
            return ApiResult.error("备注不能超过255个字符")
        }

        if (!tenantUser.name.HasValue) {
            return ApiResult.error("请填写管理员姓名")
        }
        if (tenantUser.name.length < 2) {
            return ApiResult.error("管理员姓名至少2个字符")
        }
        if (tenantUser.name.length > 32) {
            return ApiResult.error("管理员姓名不能超过32个字符")
        }

        // loginName不能重复
        if (tenantUser.id.HasValue) {
            mor.tenant.tenantUser.query()
                .where { it.loginName match tenantUser.loginName }
                .where { it.id match_not_equal tenantUser.id }
                .exists()
                .apply {
                    if (this == true) {
                        return ApiResult.error("用户名已存在")
                    }
                }
        } else {
            mor.tenant.tenantUser.query()
                .where { it.loginName match tenantUser.loginName }
                .exists()
                .apply {
                    if (this == true) {
                        return ApiResult.error("用户名已存在")
                    }
                }
        }
        // 同一租户下邮箱称不能重复
        if (tenantUser.email.HasValue) {
            val existEmail = mor.tenant.tenantUser.query()
                .where { it.tenant.id match tenantUser.tenant.id }
                .where { it.email match tenantUser.email }
                .where { it.id match_not_equal tenantUser.id }
                .exists()
            if (existEmail) {
                return ApiResult.error("邮箱已存在")
            }
        }
        // 同一租户下手机不能重复
        if (tenantUser.mobile.HasValue) {
            val existMobile = mor.tenant.tenantUser.query()
                .where { it.tenant.id match tenantUser.tenant.id }
                .where { it.mobile match tenantUser.mobile }
                .where { it.id match_not_equal tenantUser.id }
                .exists()
            if (existMobile) {
                return ApiResult.error("手机号已存在")
            }
        }
        return ApiResult()
    }


    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.User, "成员管理")
    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        val tenantUser = mor.tenant.tenantUser.queryById(id)
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .toEntity() ?: return JsonResult.error("找不到数据")

        request.logMsg = "删除成员{${tenantUser.name}}"

        if (tenantUser.adminType != TenantAdminTypeEnum.None) {
            return JsonResult.error("不可以删除租户管理员")
        }

        mor.tenant.tenantUser.query()
            .where { it.id match id }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .where { it.enabled match true }
            .toList()
            .apply {
                if(this.size >0){
                    return JsonResult.error("启用状态不允许删除")
                }
            }

        mor.tenant.tenantUser.delete()
            .where { it.id match id }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }

                mor.tenant.tenantLoginUser.delete().where { it.userId match id }.exec()
                mor.tenant.tenantDepartmentInfo.update()
                    .where { it.manager.id match id }
                    .pull({ it.manager }, MongoColumnName("id") match id)
                    .exec()


                mor.tenant.tenantAppAuthResourceInfo.query().where { it.type match AuthTypeEnum.People }
                    .where { it.target.id match id }.toList().apply {
                        this.forEach {
                            mor.tenant.tenantAppAuthResourceInfo.deleteById(it.id).exec()
                        }
                    }


                //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
                return JsonResult()
            }
    }

    private fun checkInsertLoginName(tenantId: String): String {
        var value = UUID.randomUUID().toString().replace("-", "").substring(0, 6)
        val exists = mor.tenant.tenantUser.query()
            .where { it.tenant.id match tenantId }
            .where { it.loginName match value }
            .exists()
        if (exists) {
            value = checkInsertLoginName(tenantId)
        }
        return value
    }


    private fun checkMainDept(depts: MutableList<DeptDefine>) {

        var mainCount = 0
        depts.forEach { depts ->
            if (depts.isMain) {
                mainCount++
            }
        }
        if (mainCount > 1) {
            throw RuntimeException("每个人只能有且只能有一个主部门")
        }
    }


    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.User, "成员管理")
    @ApiOperation("批量删除")
    @PostMapping("/delete/batch")
    fun deleteBatch(
        ids: List<String>,
        request: HttpServletRequest
    ): JsonResult {
        val names = mor.tenant.tenantUser.query()
            .select { it.id }
            .select { it.name }
            .where { it.id match_in ids }
            .where { it.adminType match TenantAdminTypeEnum.None }
            .toList(IdName::class.java)

        request.logMsg = "批量删除成员{${names.map { it.name }}}"

        val deleteIds = names.map { it.id }


        mor.tenant.tenantUser.query()
            .where { it.id match_in deleteIds }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .where { it.enabled match true }
            .toList()
            .apply {
                if(this.size >0){
                    return JsonResult.error("启用状态不允许删除")
                }
            }


        mor.tenant.tenantUser.delete()
            .where { it.id match_in deleteIds }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .exec().apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }

                mor.tenant.tenantLoginUser.delete()
                    .where { it.userId match_in deleteIds }
                    .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                    .exec()

                mor.tenant.tenantDepartmentInfo.update()
                    .where { it.manager.id match_in deleteIds }
                    .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                    .pull({ it.manager }, MongoColumnName("id") match_in  ids)
                    .exec()

                mor.tenant.tenantAppAuthResourceInfo.query().where { it.type match AuthTypeEnum.People }
                    .where { it.target.id match_in ids }.toList().apply {
                        this.forEach {
                            mor.tenant.tenantAppAuthResourceInfo.deleteById(it.id).exec()
                        }
                    }


            }
        //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
        return JsonResult()
    }


    private fun getAllDept(deptIds: MutableList<String>, ids: MutableList<String>) {
        mor.tenant.tenantDepartmentInfo.query().where {
            it.parent.id match_in deptIds
        }.toList().apply {
            var temp_ids: MutableList<String> = mutableListOf()
            if (this.size > 0) {
                this.forEach {
                    ids.add(it.id)
                    temp_ids.add(it.id)
                }
                getAllDept(temp_ids, ids)
            }
        }
    }

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list_temp(
        id: String, //当列表列新一条后，刷新时使用
        name: String,
        code: String,
        remark: String,
        address: String,
        deptId: String,
        enabled: Boolean?,
        loginNameStrict: String,// 用户名严格匹配用
        roleId: String,
        groupId: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantUser> {

        mor.tenant.tenantUser.query()
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (deptId.HasValue) {
                    var ids: MutableList<String> = mutableListOf()
                    ids.add(deptId)

                    var queryDeptIds: MutableList<String> = mutableListOf()
                    queryDeptIds.add(deptId)

                    getAllDept(queryDeptIds, ids)
                    this.where { it.depts.id match_in ids }
                }
                if(name.HasValue){
                    this.whereOr (
                        { it.name match_like  name },
                        { it.loginName match_like name },
                        { it.mobile match_like name },
                        { it.email match_like name }
                    )
                }
                if (remark.HasValue) {
                    this.where { it.remark match_like remark }
                }
                if (address.HasValue) {
                    this.where { it.address match_like address }
                }
                if (code.HasValue) {
                    this.where { it.code match_like code }
                }
                if (loginNameStrict.HasValue) {
                    this.where { it.loginName match loginNameStrict }
                }
                if (enabled != null) {
                    this.where { it.enabled match enabled }
                }
                if (groupId.HasValue) {
                    this.where { it.groups.id match groupId }
                }
                this.where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }

            }
            .limit(skip, take)
            .orderByAsc { it.sort }
            .toListResult()
            .apply {
                var group_ids = this.data.map { it.groups.map { it.id } }.Unwind().toSet()
                var dbAllGroups = mor.tenant.tenantUserGroup.query().where { it.id match_in group_ids }
                    .toList(IdNameRemark::class.java)
                this.data.forEach { user ->
                    user.groups = user.groups.map { group ->
                        var groupWithIdNameRemark = dbAllGroups.firstOrNull { it.id == group.id }
                        return@map groupWithIdNameRemark
                    }.filter { it != null }.map { it!! }.toMutableList()
                }
                return this
            }
    }

    @ApiOperation("穿梭框查人")
    @PostMapping("/listTransferUser")
    fun listTransferUser(
        name: String,
        @Require skip: Int,
        @Require take: Int,
        @Require pageSize: Int,
        @Require pageNumber: Int,
        request: HttpServletRequest
    ): ListResult<TenantUser> {
        val ids = mutableListOf<String>()
        mor.tenant.tenantUser.query()
            .apply {
                if (name.HasValue) {
                    this.where { it.name match_like name }
                }
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
                this.where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            }
            .toList()
            .apply {
                if(this.size>0) this.map { ids.add(it.id) }
            }
        mor.tenant.tenantUser.query()
            .apply {
                if (name.HasValue) {
                    this.where { it.email match_like name }
                }
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
                this.where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            }
            .toList()
            .apply {
                if(this.size>0) this.map { ids.add(it.id) }
            }
        mor.tenant.tenantUser.query()
            .apply {
                if (name.HasValue) {
                    this.where { it.mobile match_like name }
                }
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
                this.where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            }
            .toList()
            .apply {
                if(this.size>0) this.map { ids.add(it.id) }
            }
        var finalIds = mutableListOf<String>()
        if(ids.size>0) finalIds=ids.distinctBy { it }.toMutableList()
        val data = mor.tenant.tenantUser.query()
            .apply {
                if (name.HasValue) {
                    this.where { it.id match_in finalIds }
                }
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
                this.where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            }
            .orderByDesc { it.createAt }
            .limit(skip, take)
            .toListResult()
        return data
    }

    @Value("\${mail.sender}")
    val mailSender: String = ""

    @Value("\${mail.pwd}")
    val mailPwd: String = ""

    @Value("\${mail.smtp}")
    val mailSmtp: String = ""

    @Value("\${mail.pop}")
    val mailPop: String = ""

    @Resource
    lateinit var mailUtil: MailUtil

    @BizLog(BizLogActionEnum.ResetPassword, BizLogResourceEnum.User, "成员管理")
    @PostMapping("/reset-pwd")
    fun resetPwd(
        @Require userId: String,
        @Require sendPasswordType: SendPasswordType,
        password: String,
        request: HttpServletRequest
    ): JsonResult {
        //业务日志
        request.logMsg = "重置用户密码"

        //登录账号
        val user = mor.tenant.tenantLoginUser.query()
            .where { it.userId match userId }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity() ?: return JsonResult.error("用户不存在")

        //用户信息
        val userEntity = mor.tenant.tenantUser.queryById(userId)
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .toEntity() ?: return JsonResult.error("用户不存在")

        if (sendPasswordType == null) {
            return JsonResult.error("该用户未指定发送密码类型")
        }


        if (sendPasswordType == SendPasswordType.Email) {
            if(!userEntity.email.HasValue){
                return JsonResult.error("该用户未绑定邮箱")
            }
        }
        if (sendPasswordType == SendPasswordType.Mobile) {
            if(!userEntity.mobile.HasValue){
                return JsonResult.error("该用户未绑定手机")
            }
        }


        if (userEntity.adminType != TenantAdminTypeEnum.None) {
            return JsonResult.error("不可以重置租户管理员的密码")
        }

        var pwd = UUID.randomUUID().toString().replace("-", "").substring(0, 6)


        if(sendPasswordType == SendPasswordType.Defined){
            pwd = password
            if(pwd.trim().equals("")){
                return JsonResult.error("密码不能为空")
            }
            mor.tenant.tenantLoginUser.updateByUserId(userId)
                .set { it.password to SHA256Util.getSHA256StrJava(pwd + user.passwordSalt) }
                .set { it.lastUpdatePwdAt to LocalDateTime.now() }
                .set { it.manualRemindPwdTimes to 0 }
                .set { it.manualExpirePwdTimes to 0 }
                .set { it.autoExpirePwdTimes to 0 }
                .set { it.autoRemindPwdTimes to 0 }
                .exec()
        }else {
            mor.tenant.tenantLoginUser.updateByUserId(userId)
                .set { it.password to SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(pwd) + user.passwordSalt) }
                .set { it.lastUpdatePwdAt to LocalDateTime.now() }
                .set { it.manualRemindPwdTimes to 0 }
                .set { it.manualExpirePwdTimes to 0 }
                .set { it.autoExpirePwdTimes to 0 }
                .set { it.autoRemindPwdTimes to 0 }
                .exec()
        }

        if (sendPasswordType == SendPasswordType.Mobile) {
            // TODO 邮件中英文切换
            val sendSmsCode = mpClient.sendSmsPwd(MobileCodeModuleEnum.SendPassword, userEntity.mobile, pwd)
            val success = sendSmsCode.success()
            println(success.toString())
        } else if (sendPasswordType == SendPasswordType.Email) {
            val msg = EmailMessage()
            msg.sender = mailSender
            println("======Mail Sender======${mailSender}")
            println("======Mail SMTP======${mailSmtp}")
            println("======Mail POP======${mailPop}")
            msg.password = mailPwd
            msg.addressee = listOf(userEntity.email)
            // TODO 邮件中英文切换
            if (request.getHeader("lang") == "en") {
                msg.topic = "[Nancal Ruiyuan] Login password"
                msg.content = "Hello，${userEntity.loginName}<br />" +
                        "<p style=\"text-indent:2em\">your login password is：$pwd 。Please do not share</p><br />" +
                        "<br>your password with anyone for the sake of your account security.</br>"
            } else {
                msg.topic = "【能科瑞元】登录密码"
                msg.content = "您好，${userEntity.loginName}<br />" +
                        "<p style=\"text-indent:2em\">您的登录密码为：$pwd 。为了您账户的</p><br />" +
                        "<br>安全请勿将密码告知他人。</br>"
            }
            msg.popService = mailPop
            msg.smtpService = mailSmtp
            mailUtil.sendEmail(msg)
        }
        return JsonResult()
    }

    @BizLog(BizLogActionEnum.Enable, BizLogResourceEnum.User, "成员管理")
    @ApiOperation("用户启用")
    @PostMapping("/enable")
    fun enabled(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        val loginUser = mor.tenant.tenantLoginUser.query()
            .where { it.userId match id }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity() ?: return JsonResult.error("用户不存在!")
        val user=mor.tenant.tenantUser.queryById(id).toEntity()?:return JsonResult.error("用户不存在!")
        if (loginUser.enabled == true && user.enabled==true) {
            return JsonResult.error("用户已是启用状态!")
        }

        //用户信息
        val userEntity = mor.tenant.tenantUser.queryById(loginUser.userId)
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .toEntity() ?: return JsonResult.error("用户不存在!")

        if (userEntity.adminType != TenantAdminTypeEnum.None) {
            return JsonResult.error("不可以启用租户管理员")
        }

        mor.tenant.tenantLoginUser.update().where { it.userId match id }
            .set { it.enabled to true }
            .exec()
        mor.tenant.tenantUser.update().where { it.id match id }
            .set { it.enabled to true }
            .exec()



        mor.tenant.tenantUser.queryById(id)
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .toEntity()
            .apply {
                if (this != null) {
                    request.logMsg = "用户{${this.name}}" + "启用"
                }
            }

        return JsonResult()
    }
    @BizLog(BizLogActionEnum.Disable, BizLogResourceEnum.User, "成员管理")
    @ApiOperation("用户停用")
    @PostMapping("/disable")
    fun disabled(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {

        val loginUser = mor.tenant.tenantLoginUser.query()
            .where { it.userId match id }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity() ?: return JsonResult.error("用户不存在")
        val user=mor.tenant.tenantUser.queryById(id).toEntity()?:return JsonResult.error("用户不存在")


        if (loginUser.enabled == false && user.enabled==false) {
            return JsonResult.error("用户已是停用状态")
        }

        //用户信息
        val userEntity = mor.tenant.tenantUser.queryById(loginUser.userId)
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .toEntity() ?: return JsonResult.error("用户不存在")

        if (userEntity.adminType != TenantAdminTypeEnum.None) {
            return JsonResult.error("不可以停用租户管理员")
        }

        mor.tenant.tenantLoginUser.update().where { it.userId match id }
            .set { it.enabled to false }
            .exec()
        mor.tenant.tenantUser.update().where { it.id match id }
            .set { it.enabled to false }
            .exec()
        mor.tenant.tenantUser.queryById(id)
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .toEntity()
            .apply {
                if (this != null) {
                    request.logMsg = "用户{${this.name}}" + "停用"
                }
            }

        return JsonResult()
    }

    @BizLog(BizLogActionEnum.UpOrDownLocation, BizLogResourceEnum.User, "成员管理")
    @ApiOperation("用户上下移置顶")
    @PostMapping("/move")
    fun move(
        @Require ids: List<String>,
        @Require type: String,
        request: HttpServletRequest
    ): JsonResult {

        val users = mor.tenant.tenantUser.query()
            .where {
                it.id match_in ids
            }
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .toList(IdNameSort::class.java)
        if (users.size != 2) {
            JsonResult.error("未查询到移动对象")
        }


        if (type == MoveType.UpDown.toString()) {//上下移动
            mor.tenant.tenantUser.update().where {
                it.id match users[0].id
            }.set { it.sort to users[1].sort }.exec().apply {
                if (this == 0) {
                    return JsonResult.error("移动失败")
                }
                request.logMsg = "成员{${users.get(0).name}}上下移"
            }
            mor.tenant.tenantUser.update().where {
                it.id match users[1].id
            }.set { it.sort to users[0].sort }.exec().apply {
                if (this == 0) {
                    return JsonResult.error("移动失败")
                }
                request.logMsg = "成员{${users.get(1).name}}上下移"
            }
        } else if (type == MoveType.Top.toString()) {// 置顶
            if (users[0].sort >= users[1].sort) {
                mor.tenant.tenantUser.update().where {
                    it.id match users[0].id
                }.set { it.sort to users[1].sort - 0.1F }.exec().apply {
                    if (this == 0) {
                        return JsonResult.error("置顶失败")
                    }
                    request.logMsg = "成员{${users.get(0).name}}置顶"
                }
            } else {
                mor.tenant.tenantUser.update().where {
                    it.id match users[1].id
                }.set { it.sort to users[0].sort - 0.1F }.exec().apply {
                    if (this == 0) {
                        return JsonResult.error("置顶失败")
                    }
                    request.logMsg = "成员{${users.get(1).name}}置顶"
                }
            }
        } else {
            return JsonResult.error("移动类型不合法")
        }
        return JsonResult()
    }


    class IdNameSort {
        var id: String = ""
        var sort: Float = 0F
        var name = ""
    }

    @BizLog(BizLogActionEnum.UpdatePassword, BizLogResourceEnum.User, "成员管理")
    @ApiOperation("修改密码")
    @PostMapping("/updatePassword")
    fun updatePassword(
        @Require id: String,
        @Require oldPassword: String,
        @Require newPassword: String,
        request: HttpServletRequest
    ): JsonResult {
        request.logMsg = "成员修改密码"
        if (oldPassword == newPassword) {
            return JsonResult.error("新密码不能与旧密码一致")
        }
        val user = mor.tenant.tenantUser.query()
            .where { it.id match id }
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .toEntity() ?: return JsonResult.error("用户未找到")
        val userLogin = mor.tenant.tenantLoginUser.query().where { it.userId match id }.toEntity()
            ?: return JsonResult.error("用户未找到")
        // 校验密码格式是否正确
        if (ValidateUtils.containerSpace(newPassword) || ValidateUtils.isContainChinese(newPassword)) {
            return JsonResult.error("密码包含非法字符")
        }
        // 判断租户是否正常
        val tenant = mor.tenant.tenant.query()
            .where { it.id match user.tenant.id }
            .toEntity().must().elseThrow { "找不到该用户" }
            .apply {
                if (this.isLocked) {
                    return JsonResult.error("您的租户已被冻结")
                }
            }
        val tss = mor.tenant.tenantSecretSet.queryByTenantId(tenant.id).toEntity()
        val sp = tss?.setting?.selfSetting?.securityPolicy ?: SecurityPolicy()

        if (!PwdVerifyStrategy.pwdVerification(newPassword,  sp.leastLenght, sp.lowInput,sp.upInput,sp.specialInput,sp.numberInput)) {
            return JsonResult.error(PwdVerifyStrategy.getPwdVerificationPrompt(newPassword, sp.leastLenght, sp.lowInput,sp.upInput,sp.specialInput,sp.numberInput))
        }

        //鉴权
        mor.tenant.tenantLoginUser.query()
            .where { it.userId match id }
            .where { it.password match SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(oldPassword) + userLogin.passwordSalt) }
            .toEntity()
            .apply {
                if (this == null) {
                    return JsonResult.error("原密码不正确")
                }
            }
        mor.tenant.tenantLoginUser.update()
            .where { it.userId match id }
            .set { it.password to SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(newPassword) + userLogin.passwordSalt) }
            .set { it.lastUpdatePwdAt to LocalDateTime.now() }
            .set { it.manualRemindPwdTimes to 0 }
            .set { it.manualExpirePwdTimes to 0 }
            .set { it.autoExpirePwdTimes to 0 }
            .set { it.autoRemindPwdTimes to 0 }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("修改失败")
                }
                request.logMsg = "成员{${user.name}}修改密码"
            }
        return JsonResult()
        //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
    }


    @BizLog(BizLogActionEnum.Update, BizLogResourceEnum.User, "成员管理")
    @ApiOperation("修改手机")
    @PostMapping("/checkOldPassAndUpdatePhone")
    fun checkOldPassAndUpdatePhone(
        @Require id: String,
        @Require oldPassword: String,
        @Require phone: String,
        request: HttpServletRequest
    ): JsonResult {

        request.logMsg = "修改手机号码"

        val matchPhoneParttern =
            "^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$"
        val isPhoneMatch = Regex(matchPhoneParttern).containsMatchIn(phone)
        if (!isPhoneMatch) {
            return JsonResult.error("手机号码格式错误")
        }

        val user = mor.tenant.tenantUser.query()
            .where { it.id match id }
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .toEntity() ?: return JsonResult.error("用户未找到")
        // 判断租户是否正常
        val tenant = mor.tenant.tenant.query()
            .where { it.id match user.tenant.id }
            .toEntity().must().elseThrow { "找不到该用户" }
            .apply {
                if (this.isLocked) {
                    return JsonResult.error("您的租户已被冻结")
                }
            }
        //鉴权
        mor.tenant.tenantLoginUser.query()
            .where { it.userId match id }
            .where { it.password match Md5Util.getBase64Md5(oldPassword) }.toEntity()
            .apply {
                if (this == null) {
                    return JsonResult.error("登录密码不正确")
                }
            }

        mor.tenant.tenantUser.query()
            .where { it.mobile match phone }
            .where { it.id match_not_equal user.id }.exists()
            .apply {
                if (this == true) {
                    return JsonResult.error("手机号已存在")
                }
            }

        mor.tenant.tenantLoginUser.update()
            .where { it.userId match id }
            .set { it.mobile to phone }.exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("修改失败")
                }
            }
        mor.tenant.tenantUser.update()
            .where { it.id match id }
            .set { it.mobile to phone }.exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("修改失败")
                }
            }
        return JsonResult()
        //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
    }

    @BizLog(BizLogActionEnum.Update, BizLogResourceEnum.User, "成员管理")
    @ApiOperation("修改头像")
    @PostMapping("/updateHeadPicture")
    fun updateHeadPicture(
        @Require userId: String,
        pictureId: String,
        @Require url: String,
        request: HttpServletRequest
    ): JsonResult {

        val user = mor.tenant.tenantUser.query()
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .where { it.id match userId }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity()
        if (user == null) {
            return JsonResult.error("租户下用户未找到")
        }

        mor.tenant.tenantUser.updateById(userId)
            .apply {
                if(pictureId.HasValue){
                    this.set { it.logo.id to pictureId }
                }else {
                    this.set { it.logo.id to "" }
                }
            }
            .set { it.logo.url to url }
            .exec()
        if (mor.affectRowCount < 1) {
            return JsonResult.error("修改头像失败")
        }
        request.logMsg = "用户{${user.name}}修改头像"
        return JsonResult()
    }


    @ApiOperation("查询admind端是否存在该对应用户，乐造专用")
    @PostMapping("/queryAdminExsit")
    fun queryAdminExsit(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<TenantUser> {

        val tenantUser = mor.tenant.tenantUser.query()
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .where { it.id match id }
            .toEntity()
        if (tenantUser == null) {
            return ApiResult.error("非管理员用户不存在")
        }
        val exists = mor.tenant.tenantUser.query()
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .where { it.tenant.id match tenantUser.tenant.id }
            .where { it.loginName match tenantUser.loginName }
            .toEntity()
        return ApiResult.of(exists)
    }


    @ApiOperation("租户管理侧查询登录用户详情")
    @PostMapping("/personalCenter/{id}")
    fun personalCenter(
        @PathVariable @Require id: String,
        request: HttpServletRequest
    ): ApiResult<TenantUser> {
        mor.tenant.tenantUser.queryById(id)
            .toEntity(TenantUser::class.java)
            .apply {

                if (this == null) {
                    return ApiResult.error("找不到数据")
                }
                return ApiResult.of(this)
            }
    }

    @BizLog(BizLogActionEnum.UpdatePassword, BizLogResourceEnum.Admin, "成员管理")
    @ApiOperation("修改密码")
    @PostMapping("/updateTenantAdminPassword")
    fun updateTenantAdminPassword(
        @Require id: String,
        @Require oldPassword: String,
        @Require newPassword: String,
        request: HttpServletRequest
    ): JsonResult {

        request.logMsg = "管理员修改密码"
        if (oldPassword == newPassword) {
            return JsonResult.error("新密码不能与旧密码一致")
        }

        val user =
            mor.tenant.tenantUser.query()
                .where { it.id match id }
                .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }.toEntity()
                ?: return JsonResult.error("用户未找到")
        val userLogin =
            mor.tenant.tenantLoginUser.query().where { it.userId match id }
                .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }.toEntity()
                ?: return JsonResult.error("用户未找到")
        // 校验密码格式是否正确
        if (ValidateUtils.containerSpace(newPassword) || ValidateUtils.isContainChinese(newPassword)) {
            return JsonResult.error("密码包含非法字符")
        }
        // 判断租户是否正常
        val tenant = mor.tenant.tenant.query()
            .where { it.id match user.tenant.id }
            .toEntity().must().elseThrow { "找不到该租户" }
            .apply {
                if (this.isLocked) {
                    return JsonResult.error("您的租户已被冻结")
                }
            }
        val tss = mor.tenant.tenantSecretSet.queryByTenantId(tenant.id).toEntity()
        val sp = tss?.setting?.selfSetting?.securityPolicy ?: SecurityPolicy()
        if (!PwdVerifyStrategy.pwdVerification(newPassword, sp.leastLenght, sp.lowInput,sp.upInput,sp.specialInput,sp.numberInput)) {
            return JsonResult.error(PwdVerifyStrategy.getPwdVerificationPrompt(newPassword, sp.leastLenght, sp.lowInput,sp.upInput,sp.specialInput,sp.numberInput))
        }


        mor.tenant.tenantLoginUser.query()
            .where { it.userId match id }
            .where { it.password match SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(oldPassword) + userLogin.passwordSalt) }
            .toEntity()
            .apply {

                if (this == null) {
                    return JsonResult.error("原密码不正确")
                }
            }
        mor.tenant.tenantLoginUser.update()
            .where { it.userId match id }
            .set { it.password to SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(newPassword) + userLogin.passwordSalt) }
            .set { it.lastUpdatePwdAt to LocalDateTime.now() }
            .set { it.manualRemindPwdTimes to 0 }
            .set { it.manualExpirePwdTimes to 0 }
            .set { it.autoExpirePwdTimes to 0 }
            .set { it.autoRemindPwdTimes to 0 }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("修改失败")
                }
            }
        return JsonResult()
        //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
    }


    fun JsonMap.repaireDepts(tenantId: String, db_all_deptList: MutableList<TenantDepartmentInfo>): String {

        var dept = this.get(mor.tenant.tenantUser.depts.toString().trim()).AsString()

        if(dept == ""){
            return "部门不能为空"
        }

        var queryString = ""
        if (dept.contains("/")) {
            queryString = dept.split("/").get(dept.split("/").size - 1)
        }else {
            queryString = dept
        }

        if (!db_all_deptList.map { it.name }.contains(queryString)) {
            if (dept != "") {
                return "找不到部门"
            }

            var db_depts: MutableList<DeptDefine> = mutableListOf()
            this.set(mor.tenant.tenantUser.depts.toString(), db_depts)

        } else {

            if (dept.contains("/")) {
                val queryByDeptFullName =
                    mor.tenant.tenantDepartmentInfo.queryByDeptFullName(tenantId, *dept.split("/").toTypedArray())
                if (queryByDeptFullName.code == 0) {
                    var db_depts: MutableList<DeptDefine> = mutableListOf()
                    var deptInner = DeptDefine()
                    deptInner.isMain = true
                    deptInner.id = queryByDeptFullName.data?.id ?: ""
                    deptInner.name = queryByDeptFullName.data?.name ?: ""
                    db_depts.add(deptInner)
                    this.set(mor.tenant.tenantUser.depts.toString(), db_depts)
                } else {
                    this.set(mor.tenant.tenantUser.depts.toString(), dept)
                    return "无法确定部门"
                }
            } else {
                db_all_deptList.forEach {
                    var db_depts: MutableList<DeptDefine> = mutableListOf()
                    var deptInner = DeptDefine()
                    if (it.name == dept) {
                        deptInner.isMain = true
                        deptInner.id = it.id
                        deptInner.name = it.name
                        db_depts.add(deptInner)
                        this.set(mor.tenant.tenantUser.depts.toString(), db_depts)
                    }
                }
            }
        }


//        var excel_depts = this.get(mor.tenant.tenantUser.depts.toString()).AsString()
//            .split(",")
//            .map { it.trim() }
//
//        var db_depts: MutableList<DeptDefine> = mutableListOf()
//        if(excel_depts.size == 1){
//            if(excel_depts[0]==""){
//                this.set(mor.tenant.tenantUser.depts.toString(), db_depts.map { it });
//                return ""
//            }
//        }
//
//        excel_depts.forEach {
//
//            val queryByDeptFullName =
//                mor.tenant.tenantDepartmentInfo.queryByDeptFullName(tenantId, *it.split("/").toTypedArray())
//
//            if (queryByDeptFullName.code == 0) {
//                queryByDeptFullName.data?.let { it1 -> db_depts.add(it1) }
//            } else {
//                this.set(mor.tenant.tenantUser.depts.toString(),error_depts);
//                return queryByDeptFullName.msg
//            }
//
//        }
//
//        if(db_depts.size == 1){
//            db_depts[0].isMain = true
//        }
//
//        this.set(mor.tenant.tenantUser.depts.toString(), db_depts.map { it });
        return ""
    }

    fun JsonMap.queryByGroup(tenantId: String, tenantName: String): String {

        var excel_groups = this.get(mor.tenant.tenantUser.groups.toString()).AsString()
            .split(",")
            .map { it.trim() }

        var db_groups: MutableList<IdName> = mutableListOf()
        excel_groups.forEach {

            val group = mor.tenant.tenantUserGroup.query()
                .where { it.tenant.id match tenantId }
                .where { it.name match excel_groups }.toEntity()
            if (group != null) {
                db_groups.add(IdName(group.id, group.name))
            } else {

                var group: TenantUserGroup = TenantUserGroup()
                group.tenant.id = tenantId
                group.tenant.name = tenantName
                group.name = it

                mor.tenant.tenantUserGroup.updateWithEntity(group)

                    .run {
                        return@run this.execInsert()
                    }.apply {
                        db_groups.add(IdName(group.id, group.name))
                    }
            }
        }
        this.set(mor.tenant.tenantUser.groups.toString(), db_groups.map { IdName(it.id, it.name) })
        return ""
    }

    fun JsonMap.queryByDuty(
        tenantId: String,
        tenantName: String,
        db_all_dutyList: MutableList<TenantDutyDict>
    ): String {

        var excel_duty = this.get(mor.tenant.tenantUser.duty.toString()).AsString()

        var isContains = false
        if (excel_duty != "") {
            db_all_dutyList.forEach {
                if (it.name == excel_duty) {
                    this.set(mor.tenant.tenantUser.duty.toString(), IdName(it.id, excel_duty))
                    isContains = true
                }
            }

            if (!isContains) {
                var dutyObj = TenantDutyDict()
                dutyObj.tenant.id = tenantId
                dutyObj.tenant.name = tenantName
                dutyObj.name = excel_duty
                mor.tenant.tenantDutyDict.updateWithEntity(dutyObj)

                    .run {
                        return@run this.execInsert()
                    }
                this.set(mor.tenant.tenantUser.duty.toString(), IdName(dutyObj.id, dutyObj.name))
            }

        } else {
            this.set(mor.tenant.tenantUser.duty.toString(), IdName("", ""))
        }
        this.remove("duty.name")
        return ""
    }

    fun JsonMap.queryByMemberType(): String {
        var memberType = this.get(mor.tenant.tenantUser.employeeType.toString()).AsString()
        if (memberType != "") {
            if (memberType == "全职") {
                memberType = EmployeeTypeEnum.FullTime.name
            }
            if (memberType == "兼职") {
                memberType = EmployeeTypeEnum.PartTime.name
            }
            if (memberType == "外包") {
                memberType = EmployeeTypeEnum.Epiboly.name
            }
            if (memberType == "返聘") {
                memberType = EmployeeTypeEnum.ReturnHire.name
            }
        }
        this.set(mor.tenant.tenantUser.employeeType.toString(), memberType)
        return ""
    }

    fun JsonMap.queryByMemberStatus(): String {
        var memberStatus = this.get(mor.tenant.tenantUser.employeeStatus.toString()).AsString()
        if (memberStatus != "") {
            if (memberStatus == "正式") {
                memberStatus = EmployeeStatusEnum.Formal.name
            }
            if (memberStatus == "试用") {
                memberStatus = EmployeeStatusEnum.Try.name
            }
        }
        this.set(mor.tenant.tenantUser.employeeStatus.toString(), memberStatus)
        return ""
    }

    var columns = linkedMapOf<String, MongoColumnName>(
        "姓名" to mor.tenant.tenantUser.name,
//        "工号" to mor.tenant.tenantUser.code,
        "部门" to mor.tenant.tenantUser.depts,
//        "用户组" to mor.tenant.tenantUser.groups,
        "职务" to mor.tenant.tenantUser.duty,
//        "发送密码方式（手机/邮箱）" to mor.tenant.tenantUser.sendPasswordType,
//        "入职时间" to mor.tenant.tenantUser.goJobTime,
//        "员工类型" to mor.tenant.tenantUser.employeeType,
//        "员工状态" to mor.tenant.tenantUser.employeeStatus,
        "用户名" to mor.tenant.tenantUser.loginName,
        "手机号" to mor.tenant.tenantUser.mobile,
        "邮箱" to mor.tenant.tenantUser.email,
//        "备注" to mor.tenant.tenantUser.remark
    )
    var columnsEn = linkedMapOf<String, MongoColumnName>(
        "Full name" to mor.tenant.tenantUser.name,
        "Department" to mor.tenant.tenantUser.depts,
        "Post" to mor.tenant.tenantUser.duty,
        "User name" to mor.tenant.tenantUser.loginName,
        "Mobile phone" to mor.tenant.tenantUser.mobile,
        "E-mail" to mor.tenant.tenantUser.email,
    )

    private fun rowTransform_key2value(row: JsonMap, columnsDefine: Map<String, String>): JsonMap {
        var oriJson = JsonMap()
        var oriJsonKeys = row.keys
        columnsDefine.forEach { kv ->
            var title = kv.key
            var dbCoumnName = kv.value.toString()

            if (oriJsonKeys.contains(title)) {
                oriJson.put(dbCoumnName, row.getValue(title))
            }
        }

        return oriJson
    }

    class MobileReason {
        var loginName: String = ""
        var reason: String = ""
    }

    fun InputStream.ReadToMemeryStream(): ByteArrayInputStream {
        this.use {
            return ByteArrayInputStream(it.readBytes())
        }
    }

    @ApiOperation("错误导入")
    fun errorImport(
        oriJson: JsonMap,
        jobId: String,
        reasons: String,
        countRow: Int,
        errorList: MutableList<ExcelErrorJob>
    ) {
        var reason: ExcelErrorJob = ExcelErrorJob()
        reason.jobId = jobId
        reason.loginName = oriJson.get("loginName").toString()
        reason.email = oriJson.get("email").toString()
        if (oriJson.get("depts").toString() == "[]") {
            reason.depts = ""
        } else {
            reason.depts = oriJson.get("depts").toString()
        }
        if (oriJson.get("duty").toString() == ":") {
            reason.duty = ""
        } else {
            reason.duty = oriJson.get("duty").toString()
        }
        reason.name = oriJson.get("name").toString()
        reason.mobile = oriJson.get("mobile").toString()
        reason.rowNumber = countRow
        reason.reason = reasons

        errorList.add(reason)
    }


    class ImportResult {
        var jobId: String = ""
        var rightCount = 0
        var errorCount = 0
    }

    private fun checkFile(file: MultipartFile): String {
        //检查文件格式
        //过滤文件信息并记录不可导入的数据
        val fileName = file.originalFilename
        // 上传文件为空
        if (fileName.isEmpty()) {
            return "没有导入文件"
        }
        //上传文件大小为10000条数据
        // 上传文件名格式不正确
        if (fileName.lastIndexOf(".") != -1 && ".xlsx" != fileName.substring(fileName.lastIndexOf("."))
            && ".xls" != fileName.substring(fileName.lastIndexOf("."))
        ) {
            return "文件名格式不正确"
        }
        val reader = ExcelUtil.getReader(file.inputStream)
        reader.isIgnoreEmptyRow = true
        if (reader.rowCount > importCount + 2) {
            return "请不要超过{$importCount}条数据"
        }
        return ""
    }

    @Value("\${excel.importCount}")
    private val importCount: Int = 0

    @BizLog(BizLogActionEnum.Import, BizLogResourceEnum.User, "成员管理")
    @ApiOperation("导入")
    @PostMapping("/import")
    fun import(
        file: MultipartFile,
        request: HttpServletRequest
    ): ApiResult<ImportResult> {
        request.logMsg = "用户EXCEL导入"
        val loginTenantAdminUser = request.LoginTenantAdminUser
        val checkFile = checkFile(file)
        if (checkFile != "") {
            return ApiResult.error(checkFile)
        }
        var lang = request.getHeader("lang")
        var excel = ExcelComponent { file.inputStream }
        val reader = ExcelUtil.getReader(file.inputStream)
        val read: List<List<Any?>> = reader.read(1, 1)

        if ( "用户名" != read[0][0] && "User name" != read[0][0]) {
            throw RuntimeException("导入失败，请检查文件内容格式")
        }

        var sheet : ExcelComponent.ExcelSheetComponent = if ("User name" == read[0][0]) { // 英文
            excel.select(excel.sheetNames.first())
                .setStrictMode(false)
                .setRowOffset(1)
                .setColumns(*columnsEn.keys.toTypedArray())
        } else {
            excel.select(excel.sheetNames.first())
                .setStrictMode(false)
                .setRowOffset(1)
                .setColumns(*columns.keys.toTypedArray())
        }

        val readSheetColumns = sheet.readSheetColumns()
        val elements = if ("User name" == read[0][0]) {
            columnsEn.map { it.toString().split("=")[0] }
        } else {
            columns.map { it.toString().split("=")[0] }
        }

        if (!readSheetColumns.containsAll(elements)) {
            return ApiResult.error("导入失败，请检查文件内容格式")
        }

        var dt = sheet.getDataTable(JsonMap::class.java)

        val db_list = mor.tenant.tenantUser.query()
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .where { it.tenant.id match loginTenantAdminUser.tenant.id }.toList()
        val db_all_list = mor.tenant.tenantUser.query()
            .toList()
        val db_all_dutyList =
            mor.tenant.tenantDutyDict.query().where { it.tenant.id match loginTenantAdminUser.tenant.id }.toList()
        val db_all_deptList =
            mor.tenant.tenantDepartmentInfo.query().where { it.tenant.id match loginTenantAdminUser.tenant.id }.toList()

        var countRow = 2
        var rightCount = 0
        var errorCount = 0

        val jobId = UUID.randomUUID().toString().replace("-", "").toString()

        // 新增的tenantUser集合
        var tenantUserList: MutableList<JsonMap> = mutableListOf()
        var tenantLoginUserList: MutableList<TenantLoginUser> = mutableListOf()
        var reasonErrorList: MutableList<ExcelErrorJob> = mutableListOf()

        dt.rows.forEach { oriJson2 ->
            errorCount++
            countRow++
            // 中文列，转 ， 英文列。
            val oriJson :JsonMap = if ("User name" == read[0][0]) {
                rowTransform_key2value(oriJson2, columnsEn.map { it.key to it.value.toString() }.toMap())

            } else {
                rowTransform_key2value(oriJson2, columns.map { it.key to it.value.toString() }.toMap())

            }


            if (oriJson["name"].toString() == "") {
                var msg: String = if (lang == "cn") {
                    "姓名不能为空"
                }  else {
                    "Name cannot be null"
                }
                errorImport(oriJson, jobId, msg, countRow, reasonErrorList)
                return@forEach
            }

            if (oriJson["name"].toString().length < 2) {
                var msg: String = if (lang == "cn") {
                    "姓名长度不能小于2"
                }  else {
                    "The minimum length of name is 2"
                }
                errorImport(
                    oriJson, jobId, msg, countRow, reasonErrorList
                )
                return@forEach
            }

            if (oriJson["name"].toString().length > 32) {
                var msg: String = if (lang == "cn") {
                    "姓名长度不能超过32"
                }  else {
                    "The maximum length of name is 32"
                }
                errorImport(oriJson, jobId, msg, countRow, reasonErrorList)
                return@forEach
            }

            if(oriJson.get("mobile").toString() != ""){
                val matchPhoneParttern =
                    "^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$"

                val isPhoneMatch = Regex(matchPhoneParttern).containsMatchIn(oriJson.get("mobile").toString())
                if (!isPhoneMatch) {
                    var msg: String = if (lang == "cn") {
                        "手机号格式不正确"
                    }  else {
                        "Invalid phone number format"
                    }
                    errorImport(oriJson, jobId, msg, countRow, reasonErrorList)
                    return@forEach
                }
                if (db_list.map { it.mobile }.contains(oriJson["mobile"].toString())) {
                    var msg: String = if (lang == "cn") {
                        "手机号重复"
                    }  else {
                        "Duplicate phone number"
                    }
                    errorImport(oriJson, jobId, msg, countRow, reasonErrorList)
                    return@forEach
                }
            }


            if (oriJson.get("email").toString() != "") {
                val matchEmailParttern = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$"
                val isEmailMatch = Regex(matchEmailParttern).containsMatchIn(oriJson.get("email").toString())
                if (!isEmailMatch) {
                    var msg: String = if (lang == "cn") {
                        "邮箱格式不正确"
                    }  else {
                        "Invalid email format"
                    }
                    errorImport(oriJson, jobId, msg, countRow, reasonErrorList)
                    return@forEach
                }
                if (db_list.map { it.email }.contains(oriJson.get("email").toString())) {
                    var msg: String = if (lang == "cn") {
                        "邮箱重复"
                    }  else {
                        "Duplicate email"
                    }
                    errorImport(oriJson, jobId, msg, countRow, reasonErrorList)
                    return@forEach
                }
            }
            if (oriJson["email"].toString() != "") {
                oriJson[mor.tenant.tenantUser.sendPasswordType.toString()] = SendPasswordType.Email
            } else if(oriJson["mobile"].toString() != ""){
                oriJson[mor.tenant.tenantUser.sendPasswordType.toString()] = SendPasswordType.Mobile
            }else {
                oriJson[mor.tenant.tenantUser.sendPasswordType.toString()] = SendPasswordType.Defined
            }
            // 部门校验
            // 暂时需求为单极部门
            val repaireDepts = oriJson.repaireDepts(loginTenantAdminUser.tenant.id, db_all_deptList)
            if (repaireDepts != "") {
                errorImport(oriJson, jobId, repaireDepts, countRow, reasonErrorList)
                return@forEach
            }
            //组校验并处理
//            oriJson.queryByGroup(loginTenantAdminUser.tenant.id,loginTenantAdminUser.tenant.name)

            // 岗位处理
            oriJson.queryByDuty(loginTenantAdminUser.tenant.id, loginTenantAdminUser.tenant.name, db_all_dutyList)

            // 员工类型枚举处理
//            oriJson.queryByMemberType()

            // 员工状态枚举处理
//            oriJson.queryByMemberStatus()

            oriJson.keys.plus(mor.tenant.tenantUser.tenant.toString())
            oriJson.put(mor.tenant.tenantUser.tenant.toString(), loginTenantAdminUser.tenant)

            val goJobTime = oriJson["goJobTime"]
            if (goJobTime == "") {
                oriJson.set(mor.tenant.tenantUser.goJobTime.toString(), LocalDateTime.now())
            }

            var user = oriJson.ToJson().FromJson<TenantUser>().ToJson().FromJson<JsonMap>()!!
            oriJson.keys.minus(user.keys).forEach { key ->
                user.put(key, oriJson.get(key))
            }

            var insertOrUpdate = "insert"

            if (oriJson["loginName"] != "" && oriJson["loginName"] != null) {

                if (oriJson["loginName"].toString().length < 6 || oriJson["loginName"].toString().length > 32) {
                    var msg: String = if (lang == "cn") {
                        "用户名长度不正确"
                    }  else {
                        "Incorrect user name length"
                    }
                    errorImport(oriJson, jobId, msg, countRow, reasonErrorList)
                    return@forEach
                }

                if (db_all_list.map { it.loginName }.contains(oriJson.get("loginName").toString())) {

                    mor.tenant.tenantUser.query()
                        .where { it.tenant.id match loginTenantAdminUser.tenant.id }
                        .where { it.loginName match oriJson.get("loginName").toString() }
                        .apply {
                            if(openPrivatization){
                                this.where { it.adminType match  TenantAdminTypeEnum.None}
                            }
                        }
                        .toEntity()
                        .apply {
                            if (this == null) {
                                var msg: String = if (lang == "cn") {
                                    "用户名已被其他租户占用"
                                }  else {
                                    "The user name has been used by another tenant"
                                }
                                errorImport(oriJson, jobId, msg, countRow, reasonErrorList)
                                return@forEach
                            } else {
                                this.name = oriJson["name"].toString()
                                this.mobile = oriJson["mobile"].toString()
                                this.email = oriJson["email"].toString()
                                this.adminType = TenantAdminTypeEnum.None
                                this.depts = oriJson["depts"].ToJson().FromJson<MutableList<DeptDefine>>()!!
                                this.duty = oriJson["duty"].ToJson().FromJson<IdName>()!!
                                mor.tenant.tenantUser.updateWithEntity(this).execUpdate()
                            }
                        }
                    insertOrUpdate = "update"
                } else {
                    oriJson["enabled"] = true
                    oriJson["adminType"] = "None"
                    tenantUserList.add(oriJson)
                    oriJson.ToJson().FromJson<TenantUser>()?.let { db_list.add(it) }
                    oriJson.ToJson().FromJson<TenantUser>()?.let { db_all_list.add(it) }
                }
            } else {
                oriJson.set(
                    mor.tenant.tenantUser.loginName.toString(),
                    UUID.randomUUID().toString().replace("-", "").substring(0, 6)
                )

                oriJson["adminType"] = "None"
                oriJson["enabled"] = true
                tenantUserList.add(oriJson)
                oriJson.ToJson().FromJson<TenantUser>()?.let { db_list.add(it) }
                oriJson.ToJson().FromJson<TenantUser>()?.let { db_all_list.add(it) }
            }

            // 组装loginUser对象，默认密码1234
            var loginUser = TenantLoginUser()
            loginUser.tenant.id = loginTenantAdminUser.tenant.id
            loginUser.tenant.name = loginTenantAdminUser.tenant.name
            loginUser.loginName = oriJson["loginName"].toString()
            loginUser.mobile = oriJson["mobile"].toString()
            loginUser.email = oriJson["email"].toString()
            loginUser.userId = oriJson["_id"].AsString()
            loginUser.enabled = true

            if (insertOrUpdate == "insert") {
                val pwd = UUID.randomUUID().toString().replace("-", "").substring(0, 6)
                var salt = UUID.randomUUID().toString().replace("-", "").substring(0, 4)
                loginUser.passwordSalt = salt
                loginUser.password = SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava(pwd) + salt)
                tenantLoginUserList.add(loginUser)
                if (oriJson["email"].toString() != "") {
                    // 发送密码
                    val msg = EmailMessage()
                    msg.sender = mailSender
                    msg.password = mailPwd
                    msg.addressee = listOf(loginUser.email)
                    // TODO 中英文切换
                    if (request.getHeader("lang") == "en") {
                        msg.topic = "[Nancal Ruiyuan] Login password"
                        msg.content = "Hello，${loginUser.loginName}<br />" +
                                "<p style=\"text-indent:2em\">your login password is：$pwd 。Please do not share</p><br />" +
                                "<br>your password with anyone for the sake of your account security.</br>"
                    } else {
                        msg.topic = "【能科瑞元】登录密码"
                        msg.content = "您好，${loginUser.loginName}<br />" +
                                "<p style=\"text-indent:2em\">您的登录密码为：$pwd 。为了您账户的</p><br />" +
                                "<br>安全请勿将密码告知他人。</br>"
                    }
                    msg.popService = mailPop
                    msg.smtpService = mailSmtp
                    Thread {
                        mailUtil.sendEmail(msg)
                    }.start()

                } else if (oriJson["mobile"].toString() != "") {
                    // 共享主线程
                    RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(), true)
                    Thread {
                        // TODO 短信 中英文切换
                        val sendSmsCode =
                            mpClient.sendSmsPwd(MobileCodeModuleEnum.SendPassword, loginUser.mobile, pwd)
                        val success = sendSmsCode.success()
                        println(success.toString())
                    }.start()

                }
            } else {
                mor.tenant.tenantLoginUser.updateWithEntity(loginUser).execUpdate()
            }
            rightCount++
            errorCount--
        }

        mor.tenant.tenantUser.batchInsert()
            .apply {
                addEntities(tenantUserList)
            }.exec()

        mor.tenant.tenantUser.query()
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .where { it.loginName match_in tenantLoginUserList.map { it.loginName } }.toList()
            .apply {
                this.forEach { tenantUser ->
                    tenantLoginUserList.forEach { loginUser ->
                        if (tenantUser.loginName == loginUser.loginName) {
                            loginUser.userId = tenantUser.id
                            return@forEach
                        }
                    }
                }
            }

        mor.tenant.tenantLoginUser.batchInsert()
            .apply {
                addEntities(tenantLoginUserList)
            }.exec()
        mor.tenant.excelErrorJob.batchInsert()
            .apply {
                addEntities(reasonErrorList)
            }.exec()

        var result = ImportResult()
        result.jobId = jobId
        result.errorCount = errorCount
        result.rightCount = rightCount
        request.logMsg = "导入成功,成功{${rightCount}}条，失败{${errorCount}}条"
        return ApiResult.of(result)
    }

    @BizLog(BizLogActionEnum.Export, BizLogResourceEnum.User, "用户管理")
    @ApiOperation("导出")
    @GetMapping("/export")
    fun export(
        id: String, //当列表列新一条后，刷新时使用
        name: String,
        code: String,
        mobile: String,
        email: String,
        remark: String,
        address: String,
        deptId: String,
        enabled: Boolean?,
        loginName: String,
        roleId: String,
        groupId: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
        lang : String
    ) {

        request.logMsg = "用户导出"

        val list = mor.tenant.tenantUser.query()
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (deptId.HasValue) {
                    var ids: MutableList<String> = mutableListOf()
                    ids.add(deptId)

                    var queryDeptIds: MutableList<String> = mutableListOf()
                    queryDeptIds.add(deptId)

                    getAllDept(queryDeptIds, ids)
                    this.where { it.depts.id match_in ids }
                }
                if(name.HasValue){
                    this.whereOr (
                        { it.name match_like  name },
                        { it.loginName match_like name },
                        { it.mobile match_like name },
                        { it.email match_like name }
                    )
                }
                if (mobile.HasValue) {
                    this.where { it.mobile match_like mobile }
                }
                if (email.HasValue) {
                    this.where { it.email match_like email }
                }
                if (remark.HasValue) {
                    this.where { it.remark match_like remark }
                }
                if (address.HasValue) {
                    this.where { it.address match_like address }
                }
                if (code.HasValue) {
                    this.where { it.code match_like code }
                }
                if (loginName.HasValue) {
                    this.where { it.loginName match_like loginName }
                }
                if (enabled != null) {
                    this.where { it.enabled match enabled }
                }
                if (groupId.HasValue) {
                    this.where { it.groups.id match groupId }
                }
                this.where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }

            }
            .toList().apply {
                if (this.size > 0) {
                    this.forEach { outDept ->
                        var blankDept: MutableList<DeptDefine> = mutableListOf()
                        if (outDept.depts.size > 0) {
                            outDept.depts.forEach { define ->
                                if (define.isMain == true) {
                                    blankDept.add(define)
                                }
                            }
                        }
                        outDept.depts = blankDept
                    }
                }
            }

        val file: ClassPathResource = if (lang == "en") {
            ClassPathResource("userTemplage_en.xlsx")
        } else {
            ClassPathResource("userTemplage.xlsx")
        }

        var excel = ExcelComponent { file.stream }

        var sheet : ExcelComponent.ExcelSheetComponent = if (lang == "en") {
            excel.select("Sheet1")
                .setStrictMode(false)
                .setRowOffset(1)
                .setColumns(*columnsEn.keys.toTypedArray())
        } else {
            excel.select("Sheet1")
                .setStrictMode(false)
                .setRowOffset(1)
                .setColumns(*columns.keys.toTypedArray())
        }

        var dt = DataTable(JsonMap::class.java)
        dt.columns = columns.keys.toTypedArray()
        list.forEach { oriJson2 ->
            var oriJson  = rowTransform_key2value(
                 oriJson2.ToJson().FromJson<JsonMap>()!!,
                 columns.map { it.value.toString() to it.key }.toMap()
             )

            // 部门处理
            var oriJson_Dept = oriJson.get("部门")
            if (oriJson_Dept != null) {
                val fromJson = oriJson_Dept.ConvertListJson(DeptDefine::class.java)
                oriJson.set("部门", fromJson.map { it.name }.joinToString(","))
            }

            // 组处理
            var oriJson_groups = oriJson.get("用户组")
            if (oriJson_groups != null) {
                val fromJson = oriJson_groups.ConvertListJson(IdName::class.java)
                oriJson.set("用户组", fromJson.map { it.name }.joinToString(","))
            }

            // 岗位处理
            var oriJson_duty = oriJson.get("职务")
            if (oriJson_duty != null) {
                val fromJson = oriJson_duty.ToJson().FromJson<IdName>()
                if (fromJson != null) {
                    oriJson.set("职务", fromJson.name)
                }
            }

            // 员工类型处理
            var oriJson_MemberType = oriJson.get("员工类型")
            if (oriJson_MemberType != null) {
                var fromJson = oriJson_MemberType.ConvertJson(String::class.java)
                if (fromJson == EmployeeTypeEnum.FullTime.name) {
                    fromJson = EmployeeTypeEnum.FullTime.remark
                }
                if (fromJson == EmployeeTypeEnum.PartTime.name) {
                    fromJson = EmployeeTypeEnum.PartTime.remark
                }
                if (fromJson == EmployeeTypeEnum.ReturnHire.name) {
                    fromJson = EmployeeTypeEnum.ReturnHire.remark
                }
                if (fromJson == EmployeeTypeEnum.Epiboly.name) {
                    fromJson = EmployeeTypeEnum.Epiboly.remark
                }
                oriJson.set("员工类型", fromJson)
            }

            // 员工状态处理
            var oriJson_MemberStatus = oriJson.get("员工状态")
            if (oriJson_MemberStatus != null) {
                var fromJson = oriJson_MemberStatus.ConvertJson(String::class.java)
                if (fromJson == EmployeeStatusEnum.Formal.name) {
                    fromJson = EmployeeStatusEnum.Formal.remark
                }
                if (fromJson == EmployeeStatusEnum.Try.name) {
                    fromJson = EmployeeStatusEnum.Try.remark
                }
                oriJson.set("员工状态", fromJson)
            }


            // 发送密码方式处理
            var oriJson_sendPasswordType = oriJson.get("发送密码方式（手机/邮箱）")
            if (oriJson_sendPasswordType != null) {
                var fromJson = oriJson_sendPasswordType.ConvertJson(String::class.java)
                if (fromJson == SendPasswordType.Mobile.name) {
                    fromJson = SendPasswordType.Mobile.remark
                }
                if (fromJson == SendPasswordType.Email.name) {
                    fromJson = SendPasswordType.Email.remark
                }
                oriJson.set("发送密码方式（手机/邮箱）", fromJson)
            }
            val json = JsonMap()
            if (lang == "en") {
                json.run { set("Full name", oriJson["姓名"]) }
                json.run { set("Department", oriJson["部门"]) }
                json.run { set("Post", oriJson["职务"]) }
                json.run { set("User name", oriJson["用户名"]) }
                json.run { set("Mobile phone", oriJson["手机号"]) }
                json.run { set("E-mail", oriJson["邮箱"]) }
                dt.rows.add(json)
            } else {
                dt.rows.add(oriJson)
            }

        }

        response.setHeader("Content-Disposition", "attachment;filename=user.xlsx")

        response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"

        sheet.writeData(response.outputStream, dt)

    }

    @BizLog(BizLogActionEnum.Export, BizLogResourceEnum.User, "成员管理")
    @ApiOperation("导出")
    @GetMapping("/exportTemplete")
    fun exportTemplete(
        response: HttpServletResponse,
        request: HttpServletRequest,
        lang : String
    ) {
        request.logMsg = "用户导出模板"
        val file: ClassPathResource = if (lang == "en") {
            ClassPathResource("userTemplage_en.xlsx")
        } else {
            ClassPathResource("userTemplage.xlsx")
        }
        var excel = ExcelComponent { file.stream }
        var sheet = excel.select("Sheet1")
            .setRowOffset(0)
            .setColumns(*columns.keys.toTypedArray())
            .setStrictMode(false)

        var dt = DataTable(JsonMap::class.java)
        dt.columns = columns.keys.toTypedArray()
        response.setHeader("Content-Disposition", "attachment;filename=user-templete.xlsx")
        response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"
        sheet.writeData(response.outputStream, dt)
    }

    @ApiOperation("列表")
    @PostMapping("/list-auth-users")
    fun listAuthUser(
        namePhoneEmail: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantUser> {

        mor.tenant.tenantUser.query()
            .apply {
                if (namePhoneEmail.HasValue) {
                    this.whereOr(
                        { it.name match_like namePhoneEmail },
                        { it.mobile match_like namePhoneEmail },
                        { it.email match_like namePhoneEmail })
                }
            }
            .where { it.enabled match true }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .limit(skip, take)
            .orderByDesc { it.createAt }
            .toListResult()
            .apply {
                return this
            }
    }

    @ApiOperation("列表")
    @PostMapping("/listUsersByGroupIdAndDeptId")
    fun listUsersByDeptIdAndGroupId(
        @Require deptId: String,
        @Require groupId: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantUser> {

        mor.tenant.tenantUser.query()
            .where { it.groups.id match groupId }
            .where { it.depts.id match deptId }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }.orderByDesc { it.createAt }
            .toListResult()
            .apply {
                return this
            }
    }


    @ApiOperation("列表")
    @PostMapping("/listUsersByGroupIdAndUserId")
    fun listUsersByUserIdAndGroupId(
        userId: String,
        @Require groupId: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantUser> {

        val groups = mor.tenant.tenantUser.query()
            .apply {
                if (userId.HasValue) {
                    this.where { it.id match userId }
                }
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toList()
            .map { it.groups }
            .Unwind()

        if (groups.map { it.id }.contains(groupId)) {
            mor.tenant.tenantUser.query()
                .where { it.groups.id match groupId }
                .where { it.enabled match true }
                .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                .apply {
                    if(openPrivatization){
                        this.where { it.adminType match  TenantAdminTypeEnum.None}
                    }
                }.orderByDesc { it.createAt }
                .toListResult()
                .apply {
                    return this
                }
        } else {
            return ListResult.error("该成员不属于该组")
        }
    }

    @PostMapping("/authUserApp")
    fun authUserApp(
        @Require appCode: String,
        allows: MutableList<String>,
        denys: MutableList<String>,
        request: HttpServletRequest
    ): JsonResult {

        val app = mor.tenant.tenantApplication.query()
            .where { it.appCode match appCode }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity()
            .apply {
                if (this == null) {
                    return JsonResult.error("应用不存在")
                }
            }


        if (allows.size == 0 && denys.size == 0) {
            return JsonResult.error("请传入允许或拒绝的用户ID")
        }

        if (allows.size > 0) {
            allows.forEach { allow ->
                mor.tenant.tenantUser.query()
                    .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                    .where { it.id match allow }
                    .apply {
                        if(openPrivatization){
                            this.where { it.adminType match  TenantAdminTypeEnum.None}
                        }
                    }
                    .toEntity()
                    .apply {
                        if (this == null) {
                            return JsonResult.error(allow + "用户不存在与该租户")
                        }
                    }
            }

            allows.forEach { allow ->
                if (app != null) {
                    mor.tenant.tenantUser.query()
                        .where { it.id match allow }
                        .where { it.allowApps.code match app.appCode }
                        .apply {
                            if(openPrivatization){
                                this.where { it.adminType match  TenantAdminTypeEnum.None}
                            }
                        }
                        .toEntity()
                        .apply {
                            if (this == null) {
                                mor.tenant.tenantUser.update()
                                    .where { it.id match allow }
                                    .apply {
                                        if(openPrivatization){
                                            this.where { it.adminType match  TenantAdminTypeEnum.None}
                                        }
                                    }
                                    .push { it.allowApps to CodeName(app.appCode, app.name) }
                                    .exec()
                            }
                        }
                }
            }
        }

        if (denys.size > 0) {
            denys.forEach { denys ->
                mor.tenant.tenantUser.query()
                    .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                    .where { it.id match denys }
                    .apply {
                        if(openPrivatization){
                            this.where { it.adminType match  TenantAdminTypeEnum.None}
                        }
                    }
                    .toEntity()
                    .apply {
                        if (this == null) {
                            return JsonResult.error(denys + "用户不存在与该租户")
                        }
                    }
            }
            denys.forEach { denys ->
                if (app != null) {
                    mor.tenant.tenantUser.update()
                        .where { it.id match denys }
                        .apply {
                            if(openPrivatization){
                                this.where { it.adminType match  TenantAdminTypeEnum.None}
                            }
                        }
                        .pull({ it.allowApps }, MongoColumnName("code") match app.appCode)
                        .exec()
                }
            }
        }

        return JsonResult()
    }

    @BizLog(BizLogActionEnum.Update, BizLogResourceEnum.User, "成员部门管理")
    @ApiOperation("修改主部门")
    @PostMapping("/updateMainDept")
    fun updateMainDept(
        @Require id: String,
        depts: MutableList<DeptDefine>,
        request: HttpServletRequest
    ): JsonResult {

        request.logMsg = "修改部门"

        if(depts == null || depts.size ==0){
            return JsonResult.error("部门不能为空")
        }

        // 校验用户
        mor.tenant.tenantUser.queryById(id)
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity()
            .apply {
                if(this == null){
                    return JsonResult.error("用户不存在")
                }
            }

        var mainCount = 0
        // 校验部门
        depts.forEach { dept ->
            mor.tenant.tenantDepartmentInfo.query()
                .where { it.id match dept.id }
                .where { it.name match dept.name  }
                .toEntity()
                .apply {
                    if(this == null){
                        return JsonResult.error("部门不存在")
                    }
                    if(dept.isMain ==true){
                        mainCount++
                    }
                }
        }

        if(mainCount >1){
            return JsonResult.error("每个人只能有且只能有一个主部门")
        }
        if(mainCount ==0){
            depts.get(0).isMain =true
        }

        mor.tenant.tenantUser.updateById(id)
            .set { it.depts to depts }
            .exec()

        return JsonResult()
    }

    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.User, "成员管理")
    @ApiOperation("人员离职")
    @PostMapping("/leaveUser")
    fun leaveUser(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        mor.tenant.tenantUser.queryById(id)
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity()
            .apply {
                if(this == null){
                    return JsonResult.error("用户不存在")
                }
                var fromJson = this.ToJson().FromJson(TenantUserLeave::class.java)!!

                fromJson.createAt = LocalDateTime.now()

                mor.tenant.tenantUserLeave.updateWithEntity(fromJson).execInsert()
                 .apply {
                     if(this ==0){
                         return JsonResult.error("添加失败")
                     }
                     mor.tenant.tenantUser.deleteById(id).exec()
                     mor.tenant.tenantLoginUser.delete().where { it.userId match id }.exec()
                     mor.tenant.tenantDepartmentInfo.update()
                         .where { it.manager.id match id }
                         .pull({ it.manager }, MongoColumnName("id") match id)
                         .exec()


                     mor.tenant.tenantAppAuthResourceInfo.query().where { it.type match AuthTypeEnum.People }
                         .where { it.target.id match id }.toList().apply {
                             this.forEach {
                                 mor.tenant.tenantAppAuthResourceInfo.deleteById(it.id).exec()
                             }
                         }
                     return JsonResult()
                 }
            }
    }


    @PostMapping("/reset-def-pwd")
    fun resetDefaultPwd(
        @Require loginName: String,
        request: HttpServletRequest
    ): JsonResult {
        mor.tenant.tenantLoginUser.query()
            .apply {
                if (loginName.HasValue){
                    this.whereOr(
                        { it.loginName match loginName },
                        { it.mobile match loginName },
                        { it.email match loginName }
                    )
                }
            }
            .toList()
            .apply {
                if (this.any()){
                    this.forEach { user ->
                        mor.tenant.tenantLoginUser.updateById(user.id)
                            .set { it.password to SHA256Util.getSHA256StrJava(SHA256Util.getSHA256StrJava("1234") + user.passwordSalt) }
                            .set { it.lastUpdatePwdAt to LocalDateTime.now() }
                            .set { it.manualRemindPwdTimes to 0 }
                            .set { it.manualExpirePwdTimes to 0 }
                            .set { it.autoExpirePwdTimes to 0 }
                            .set { it.autoRemindPwdTimes to 0 }
                            .exec()
                    }
                }
            }

        return JsonResult()
    }



    @ApiOperation("列表")
    @PostMapping("/listUsersByUserId")
    fun listUsersByUserId(
        @Require userId: String,
        selfInclude : Boolean?,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantUser> {

        val groups = mor.tenant.tenantUser.query()
            .apply {
                this.where { it.id match userId }
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toList()
            .map { it.groups }
            .Unwind()

        if(groups.size>0){
            mor.tenant.tenantUser.query()
                .where { it.groups.id match_in  groups.map { it.id } }
                .where { it.enabled match true }
                .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                .apply {
                    if(openPrivatization){
                        this.where { it.adminType match  TenantAdminTypeEnum.None}
                    }
                    if(selfInclude ==null || selfInclude == false){
                        this.where { it.id match_not_equal userId }
                    }
                }
                .orderByDesc { it.createAt }
                .toList()
                .distinctBy { it.id }
                .apply {
                    return ListResult.of(this)
                }
        }
        return ListResult.of(emptyList(),0)
    }


}

