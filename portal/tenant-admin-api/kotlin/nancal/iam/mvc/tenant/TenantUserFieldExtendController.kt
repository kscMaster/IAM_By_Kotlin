package nancal.iam.mvc.tenant

import io.swagger.annotations.*
import nancal.iam.annotation.BizLog
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import org.springframework.web.bind.annotation.*
import nbcp.comm.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.sql.dbr
import org.bson.Document
import javax.servlet.http.*

/**
 * Created by CodeGenerator at 2021-11-23 11:38:53
 */
@Api(description = "租户用户自定义字段", tags = arrayOf("TenantUserFieldExtend"))
@RestController
@RequestMapping("/tenant/tenant-user-field-extend")
class TenantUserFieldExtendAutoController {

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        code: String,
        name: String,
        keywords: String,
        fieldType: String,
        dataSource: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantUserFieldExtend> {
        var tenant = request.LoginTenantAdminUser.tenant
        mor.tenant.tenantUserFieldExtend.query()
            .apply {
                this.where { it.tenant.id match tenant.id }
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (keywords.HasValue) {
                    this.whereOr({ it.code match_like keywords },{ it.name match_like keywords } )
                }

                if (code.HasValue) {
                    this.where { it.code match_like code }
                }
                if (name.HasValue) {
                    this.where { it.name match_like name }
                }
                if (fieldType.HasValue) {
                    this.where { it.fieldType match fieldType }
                }
                if (dataSource.HasValue) {
                    this.where { it.dataSource match_like dataSource }
                }

            }.orderByDesc { it.createAt }
            .limit(skip, take)
            .toListResult()
            .apply {
                return this;
            }
    }

    @ApiOperation("电子签名")
    @PostMapping("/sign")
    fun signDetail(
        code: String,
        request: HttpServletRequest
    ): ApiResult<TenantUserFieldExtend> {
        val tenant = request.LoginTenantAdminUser.tenant
        mor.tenant.tenantUserFieldExtend.query()
            .apply {
                this.where { it.tenant.id match tenant.id }
                if (code.HasValue) {
                    this.where { it.code match_like code }
                }

            }.orderByDesc { it.createAt }
            .toEntity()
            .apply {
                return ApiResult.of(this);
            }
    }

    @ApiOperation("详情")
    @PostMapping("/detail/{id}")
    fun detail(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<TenantUserFieldExtend> {
        val tenantId = request.LoginTenantAdminUser.tenant.id
        mor.tenant.tenantUserFieldExtend.query()
            .where { it.id match id }
            .where { it.tenant.id match tenantId }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }
                return ApiResult.of(this)
            }
    }

    @BizLog(BizLogActionEnum.Save,BizLogResourceEnum.TenantUserFieldExtend,"用户扩展字段")
    @ApiOperation("更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: TenantUserFieldExtend,
        request: HttpServletRequest
    ): ApiResult<String> {
        if(entity.id.isEmpty()) request.logMsg="创建用户扩展字段{${entity.name}}"
        if(entity.id.isNotEmpty()) request.logMsg="修改用户扩展字段{${entity.name}}"
        var tenant = request.LoginTenantAdminUser.tenant
        entity.tenant = tenant
        entity.code = entity.code.trim()
        if(entity.id.HasValue){
            checkEditParameters(entity)
            mor.tenant.tenantUserFieldExtend.update()
                .where{it.tenant.id match tenant.id}
                .where{it.id match entity.id}
                .set { it.name to entity.name }
                .set { it.remark to entity.remark }
                .exec()
            if(dbr.affectRowCount==0) return ApiResult.error("更新失败")
            return ApiResult()
        }
        checkInsertParameters(entity)
        mor.tenant.tenantUserFieldExtend.doInsert(entity)
        if(dbr.affectRowCount==0) return ApiResult.error("新增失败")
        return ApiResult()
    }

    class Filed {
        var name: String = ""
        var code: String = ""
        var value: String = ""
    }

    @BizLog(BizLogActionEnum.Delete,BizLogResourceEnum.TenantUserFieldExtend,"用户扩展字段")
    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {

        var tenant = request.LoginTenantAdminUser.tenant
        var entity = mor.tenant.tenantUserFieldExtend.queryById(id)
            .where { it.tenant.id match tenant.id }
            .toEntity()
        if (entity == null) {
            return JsonResult.error("找不到数据")
        }
        request.logMsg="删除用户扩展字段{${entity.name}}"

        mor.tenant.tenantUserFieldExtend.deleteById(id)
            .where { it.tenant.id match tenant.id }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                } else {
                    // 删除租户用户的字段
                    mor.tenant.tenantUser.query().apply {
                        this.where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
                    }.toList(Document::class.java)
                        .apply {
                            this.forEach {
                                //删除字段
                                var finalFileds: MutableList<Any> = mutableListOf()
                                var fileds = it.get("fileds")?.ConvertListJson(Filed::class.java)?.toMutableList()
                                if (fileds != null) {
                                    fileds.forEach { filed ->
                                        if (!entity.code.equals(filed.code)) {
                                            finalFileds.add(filed)
                                        }

                                    }
                                }
                                it.set("fileds", finalFileds)
                                mor.tenant.tenantUser.updateWithEntity(it).execUpdate()
                            }
                        }
                }
                return JsonResult()
            }
    }

    fun checkInsertParameters(entity: TenantUserFieldExtend) {
        val english = "^[A-Za-z]+\$".toRegex()
        if(entity.code.isEmpty()) throw RuntimeException("字段Key不能为空")
        if(entity.code.length<2) throw RuntimeException("字段Key长度不能小于2")
        if(entity.code.length>32) throw RuntimeException("字段Key不能超过32位")
        if (!english.containsMatchIn(entity.code))  throw RuntimeException("字段Key只支持英文")
        if(entity.name.isEmpty()) throw RuntimeException("显示名称不能为空")
        if(entity.name.length<2) throw RuntimeException("显示名称长度不能小于2")
        if(entity.name.length>32) throw RuntimeException("显示名称不能超过32位")
        if(entity.remark.length>255) throw RuntimeException("备注不能超过255位")
        var hasField = mor.tenant.tenantUserFieldExtend.query().apply {
            this.where { it.tenant.id match entity.tenant.id }
            this.where { it.code match entity.code }
        }.exists()
        if (hasField) {
            throw RuntimeException("系统中已存在该字段")
        }
    }

    fun checkEditParameters(entity: TenantUserFieldExtend) {

        if(entity.name.isEmpty()) throw RuntimeException("显示名称不能为空")
        if(entity.name.length<2) throw RuntimeException("显示名称长度不能小于2")

        if(entity.name.length>32) throw RuntimeException("显示名称不能超过32位")
        if(entity.remark.length>255) throw RuntimeException("备注不能超过255位")
        val old=mor.tenant.tenantUserFieldExtend.queryById(entity.id).toEntity().must().elseThrow { "数据不存在" }
        if(entity.code!=old.code) throw RuntimeException("字段Key不可以变更")
    }


}