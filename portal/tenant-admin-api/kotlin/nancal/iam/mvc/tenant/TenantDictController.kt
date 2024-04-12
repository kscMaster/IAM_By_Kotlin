package nancal.iam.mvc.tenant

import cn.hutool.core.lang.Assert
import io.swagger.annotations.ApiOperation
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.db.mongo.TenantDictType
import nancal.iam.db.mongo.entity.TenantUser
import nancal.iam.db.mongo.entity.tenant.TenantGroupDict
import nancal.iam.db.mongo.mor
import nbcp.comm.*
import nbcp.db.IdCodeName
import nbcp.db.mongo.*
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

@OpenAction
@RestController
@RequestMapping("/tenant/dict")
class TenantDictController {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    @ApiOperation("数据字典-添加")
    @PostMapping("/save")
    fun save(@JsonModel entity: TenantGroupDict, request: HttpServletRequest): ApiResult<String> {
        entity.group = TenantDictType.PersonClassified.type // 数据字典
        checkParam(entity)
        if (entity.number == 0) {
            val toList: MutableList<TenantGroupDict> = mor.tenant.tenantGroupDict.query()
                .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                .toList()
                .stream()
                .sorted(Comparator.comparing(TenantGroupDict::number).reversed())
                .collect(Collectors.toList())
            if (toList.isNotEmpty()) {
                entity.number = toList.first().number + 1
            } else {
                entity.number = 1
            }
        }
        entity.tenant.id = request.LoginTenantAdminUser.tenant.id
        entity.tenant.name = request.LoginTenantAdminUser.tenant.name
        // 验证租户
        mor.tenant.tenant.query()
            .where { it.id match request.LoginTenantAdminUser.tenant.id }.toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("租户不存在")
                }
            }
        // save 校验
        if (!entity.id.HasValue) {
            // 验证字典数据code
            mor.tenant.tenantGroupDict.query()
                .where { it.code match entity.code }
                .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                .toEntity().apply {
                    if (this != null) {
                        return ApiResult.error("字典code已存在")
                    }
                }
        }
        // 编辑校验
        if (entity.id.HasValue) {
            mor.tenant.tenantGroupDict.query()
                .where { it.id match entity.id }
                .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                .toEntity()
                .apply {
                    if (this == null) {
                        return ApiResult.error("字典数据不存在")
                    }
                    if (this.code != entity.code) {
                        return ApiResult.error("code不可编辑")
                    }
                }
        }
        mor.tenant.tenantGroupDict.updateWithEntity(entity)
            .run {
                if (entity.id.HasValue) {
                    return@run this.execUpdate()
                } else {
                    return@run this.execInsert()
                }
            }.apply {
                if (this == 0) {
                    return ApiResult.error("保存失败")
                }
            }
        return ApiResult.of(entity.id)
    }

    fun checkParam(entity: TenantGroupDict) {
        // 名称：必填项；字符限制20
        Assert.isFalse(!entity.name.HasValue, "名称不能为空")
        Assert.isFalse(entity.name.length > 20, "名称长度不能超过20")
        //  编码：必填项；字符限制32.可输入大小写字母、数字、下划线。 ^[0-9a-zA-Z_]{1,}$
        Assert.isFalse(!entity.code.HasValue, "编码不能为空")
        Assert.isFalse(entity.code.length > 32, "编码长度不能大于32")
        val codePattern = "^[0-9a-zA-Z_]{1,}\$"
        Assert.isFalse(!Regex(codePattern).containsMatchIn(entity.code), "编码只能是大小写字母、数字、下划线")
        //  排序：非必填；只能输入数字。 ^[0-9]*$
        if (entity.number.HasValue) {
            val numPattern = "^[0-9]*\$"
            Assert.isFalse(!Regex(numPattern).containsMatchIn(entity.number.toString()), "排序只能是数字")
        }
        // 说明：非必填；字符限制120.
        if (entity.remark.HasValue) {
            Assert.isFalse(entity.remark.length > 120, "说明内容长度不能大于120")
        }
    }

    @ApiOperation("数据字典-列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        name: String,
        remark: String,
        code: String,
        number: Int,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantGroupDict> {
        val loginTenantAdminUser = request.LoginTenantAdminUser
        mor.tenant.tenantGroupDict.query()
            .apply {

                this.where { it.tenant.id match loginTenantAdminUser.tenant.id }

                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (remark.HasValue) {
                    this.where { it.remark match_like remark }
                }
                if (name.HasValue) {
                    this.where { it.name match_like name }
                }
                if (code.HasValue) {
                    this.where { it.code match_like code }
                }
                this.where { it.group match TenantDictType.PersonClassified.type }
            }
            .limit(skip, take).orderByAsc { it.number }.orderByDesc { it.createAt }
            .toListResult()
            .apply {
                return this
            }
    }

    @ApiOperation("数据字典-列表")
    @PostMapping("/list_dict")
    fun listDict(
        request: HttpServletRequest
    ): ListResult<TenantGroupDict> {
        val loginTenantAdminUser = request.LoginTenantAdminUser
        mor.tenant.tenantGroupDict.query()
            .apply {

                this.where { it.tenant.id match loginTenantAdminUser.tenant.id }

                this.where { it.group match TenantDictType.PersonClassified.type }
            }
            .limit(0, Int.MAX_VALUE).orderByAsc { it.number }.orderByDesc { it.createAt }
            .toListResult()
            .apply {
                return this
            }
    }

    @ApiOperation("数据字典-删除")
    @PostMapping("/delete")
    fun delete(@Require id: String, request: HttpServletRequest): JsonResult {

        val toEntity = mor.tenant.tenantGroupDict.queryById(id).toEntity()
        toEntity ?: return JsonResult.error("找不到数据")
        mor.tenant.tenantGroupDict.delete()
            .where { it.id match id }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
            }
        // 删除租户下用户的字典数据
        val toList: MutableList<TenantUser> = mor.tenant.tenantUser.query()
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .where { it.personnelSecret.id match id }.toList()
        if (toList.isNotEmpty()) {
            toList.forEach {
                it.personnelSecret = IdCodeName()
                mor.tenant.tenantUser.updateWithEntity(it).execUpdate()
            }
        }
        return JsonResult()
    }

    @ApiOperation("数据字典-删除")
    @PostMapping("/deleteBath")
    fun delete(@Require ids: List<String>, request: HttpServletRequest): JsonResult {
        mor.tenant.tenantGroupDict.query()
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .where { it.id match_in ids }.toList().apply {
                if (this.size != ids.size) {
                    return JsonResult.error("找不到数据")
                }
            }
        mor.tenant.tenantGroupDict.delete()
            .where { it.id match_in ids }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
            }
        return JsonResult()
    }


    @ApiOperation("数据字典-详情")
    @PostMapping("/detail")
    fun detail(@Require id: String, request: HttpServletRequest): ApiResult<TenantGroupDict> {

        mor.tenant.tenantGroupDict.query()
            .where { it.id match id }
            .where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }
                return ApiResult.of(this)
            }
    }


}