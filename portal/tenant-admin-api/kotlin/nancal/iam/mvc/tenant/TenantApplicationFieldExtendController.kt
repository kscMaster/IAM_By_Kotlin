package nancal.iam.mvc.tenant

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import nancal.iam.annotation.BizLog
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nbcp.comm.*
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.TenantApplicationFieldExtend
import nancal.iam.db.sql.dbr
import nbcp.db.mongo.*
import org.bson.Document
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/13-16:41
 */
@Api(description = "应用", tags = arrayOf("SysApplication"))
@RestController
@RequestMapping("/tenant/sys-application-field-extend")
class TenantApplicationFieldExtendController {

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        code: String,
        name: String,
        dataSource: String,
        keywords: String,
        appCode: String,
        fieldType: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantApplicationFieldExtend> {
        val loginUser = request.LoginTenantAdminUser
        mor.tenant.tenantApplicationFieldExtend.query()
            .apply {
                this.where { it.tenant.id match loginUser.tenant.id }
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (keywords.HasValue) {
                    this.whereOr({ it.code match_like keywords }, { it.name match_like keywords })
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
                if (appCode.HasValue) {
                    this.where { it.appCode match appCode }
                }
                if (dataSource.HasValue) {
                    this.where { it.dataSource match dataSource }
                }
            }.orderByDesc { it.createAt }
            .limit(skip, take)
            .toListResult()
            .apply {
                return this;
            }
    }

    @ApiOperation("详情")
    @PostMapping("/detail/{id}")
    fun detail(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<TenantApplicationFieldExtend> {
        val loginUser = request.LoginTenantAdminUser
        mor.tenant.tenantApplicationFieldExtend.queryById(id)
            .where { it.tenant.id match loginUser.tenant.id }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }

                return ApiResult.of(this)
            }
    }

    @BizLog(BizLogActionEnum.Save,BizLogResourceEnum.TenantApplicationFieldExtend,"应用扩展字段")
    @ApiOperation("更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: TenantApplicationFieldExtend,
        request: HttpServletRequest
    ): ApiResult<String> {
        if(entity.id.isEmpty()) request.logMsg="创建应用扩展字段{${entity.name}}"
        if(entity.id.isNotEmpty()) request.logMsg="修改应用扩展字段{${entity.name}}"
        val tenant = request.LoginTenantAdminUser.tenant
        entity.tenant = tenant
        entity.code = entity.code.trim()
        if (entity.id.HasValue) {
            checkEditParameters(entity)
            mor.tenant.tenantApplicationFieldExtend.update()
                .where{it.tenant.id match tenant.id}
                .where{it.id match entity.id}
                .apply {
                    set { it.name to entity.name }
                    set { it.remark to entity.remark }
                }
                .exec()
                .apply {
                    if (this == 0) {
                        return ApiResult.error("更新失败")
                    }
                    return ApiResult.of(entity.id)
                }
        }
        checkInsertParameters(entity)
        mor.tenant.tenantApplicationFieldExtend.doInsert(entity)
        if (dbr.affectRowCount == 0) {
            return ApiResult.error("新增失败")
        }
        return ApiResult.of(entity.id)

    }

    class Filed {
        var name: String = ""
        var code: String = ""
        var value: String = ""
    }
    @BizLog(BizLogActionEnum.Delete,BizLogResourceEnum.TenantApplicationFieldExtend,"应用扩展字段")
    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {

        val tenant = request.LoginTenantAdminUser.tenant
        val entity = mor.tenant.tenantApplicationFieldExtend.queryById(id).toEntity()
        if (entity == null) {
            return JsonResult.error("找不到数据")
        }
        request.logMsg="删除应用扩展字段{${entity.name}}"
        mor.tenant.tenantApplicationFieldExtend.deleteById(id)
            .where { it.tenant.id match tenant.id }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                } else {
                    // 删除租户应用的字段
                    mor.tenant.tenantApplication.query().apply {
                        this.where { it.tenant.id match tenant.id }
                    }.toList(Document::class.java)
                        .apply {
                            this.forEach {
                                //删除字段
                                val finalFileds: MutableList<Any> = mutableListOf()
                                val fileds = it.get("fileds")?.ConvertListJson(Filed::class.java)?.toMutableList()
                                if (fileds != null) {
                                    fileds.forEach { filed ->
                                        if (!entity.code.equals(filed.code)) {
                                            finalFileds.add(filed)
                                        }

                                    }
                                }
                                it.set("fileds", finalFileds)
                                mor.tenant.tenantApplication.updateWithEntity(it).execUpdate()
                            }
                        }
                }
                return JsonResult()
            }
    }

    fun checkInsertParameters(entity: TenantApplicationFieldExtend) {
        if(entity.code.isEmpty()) throw RuntimeException("字段Key不能为空")
        if(entity.code.length>32) throw RuntimeException("字段Key不能超过32位")
        if(entity.name.isEmpty()) throw RuntimeException("显示名称不能为空")
        if(entity.name.length>32) throw RuntimeException("显示名称不能超过32位")
        if(entity.fieldType==null) throw RuntimeException("字段类型不能为空")
        if(entity.remark.length>255) throw RuntimeException("备注不能超过255个字符")
        val hasField = mor.tenant.tenantApplicationFieldExtend.query().apply {
            this.where { it.code match entity.code }
            this.where { it.tenant.id match entity.tenant.id }
        }.exists()
        if (hasField) {
            throw RuntimeException("系统中已存在该字段")
        }
    }

    fun checkEditParameters(entity: TenantApplicationFieldExtend) {
        if(entity.name.isEmpty()) throw RuntimeException("显示名称不能为空")
        if(entity.name.length>32) throw RuntimeException("显示名称不能超过32位")
        if(entity.remark.length>255) throw RuntimeException("备注不能超过255个字符")

    }
}