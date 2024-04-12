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
import nbcp.base.mvc.*
import nbcp.web.*
import org.bson.Document
import javax.servlet.http.*
import java.time.*

/**
 * Created by CodeGenerator at 2021-11-23 11:38:27
 */
@Api(description = "组织机构自定义字段", tags = arrayOf("DepartmentInfoFieldExtend"))
@RestController
@RequestMapping("/tenant/department-info-field-extend")
class DepartmentInfoFieldExtendAutoController {

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
            id: String, //当列表列新一条后，刷新时使用
            code: String,
            name: String,
            dataSource: String,
            keywords: String,
            fieldType:String,
            @Require skip: Int,
            @Require take: Int,
            request: HttpServletRequest
    ): ListResult<TenantDepartmentInfoFieldExtend> {
        var tenant=request.LoginTenantAdminUser.tenant

        mor.tenant.tenantDepartmentInfoFieldExtend.query()
                .apply {
                    if (id.HasValue) {
                        this.where { it.id match id }
                    }
                    if (keywords.HasValue) {
                        this.whereOr({ it.code match_like keywords }, { it.name match_like keywords })
                    }
                    if (code.HasValue) {
                        this.where { it.code match_like  code }
                    }
                    if (name.HasValue) {
                        this.where { it.name match_like  name }
                    }
                    if (dataSource.HasValue) {
                        this.where { it.dataSource match dataSource }
                    }
                    if(fieldType.HasValue){
                        this.where { it.fieldType match fieldType }
                    }
                    this.where { it.tenant.id match tenant.id }
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
    ): ApiResult<TenantDepartmentInfoFieldExtend> {
        var tenant=request.LoginTenantAdminUser.tenant
        mor.tenant.tenantDepartmentInfoFieldExtend.query().apply {
            this.where { it.tenant.id match tenant.id }
            if (id.HasValue) {
                this.where { it.id match id }
            }
        }
                .toEntity()
                .apply {
                    if (this == null) {
                        return ApiResult.error<TenantDepartmentInfoFieldExtend>("找不到数据")
                    }

                    return ApiResult.of(this)
                }
    }

    @BizLog(BizLogActionEnum.Save,BizLogResourceEnum.DepartmentInfoFieldExtend,"部门扩展字段")
    @ApiOperation("更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: TenantDepartmentInfoFieldExtend,
        request: HttpServletRequest
    ): ApiResult<String> {
        if(entity.id.isEmpty()) request.logMsg="创建部门扩展字段{${entity.name}}"
        if(entity.id.isNotEmpty()) request.logMsg="修改部门扩展字段{${entity.name}}"
        var tenant=request.LoginTenantAdminUser.tenant
        entity.tenant=tenant
        entity.code= entity.code.trim()
        mor.tenant.tenantDepartmentInfoFieldExtend.updateWithEntity(entity)
                
                .run {

                    if (entity.id.HasValue) {
                        checkEditParameters(entity)
                        return@run this.execUpdate()
                    } else {
                        checkInsertParameters(entity)
                        entity.updateAt= LocalDateTime.now()
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
    class Filed{
        var name: String = ""
        var code: String = ""
        var value: String = ""
    }
    @BizLog(BizLogActionEnum.Delete,BizLogResourceEnum.DepartmentInfoFieldExtend,"部门扩展字段")
    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    fun delete(
            @Require id: String,
            request: HttpServletRequest
    ): JsonResult {
        var tenant=request.LoginTenantAdminUser.tenant

        var entity = mor.tenant.tenantDepartmentInfoFieldExtend.queryById(id).toEntity()
        if (entity == null) {
            return JsonResult.error("找不到数据")
        }
        request.logMsg="删除部门扩展字段{${entity.name}}"

        mor.tenant.tenantDepartmentInfoFieldExtend.deleteById(id)
            .where { it.tenant.id match tenant.id }
                .exec()
                .apply {
                    if (this == 0) {
                        return JsonResult.error("删除失败")
                    }else{
                        // 删除租户应用的字段
                        mor.tenant.tenantDepartmentInfo.query().apply {
                            this.where{it.tenant.id match request.LoginTenantAdminUser.tenant.id}
                        }.toList(Document::class.java)
                            .apply {
                                this.forEach {
                                    //删除字段
                                    var finalFileds:MutableList<Any> = mutableListOf()
                                    var fileds= it.get("fileds")?.ConvertListJson(Filed::class.java)?.toMutableList()
                                    if (fileds != null) {
                                        fileds.forEach { filed->
                                            if(!entity.code.equals(filed.code)){
                                                finalFileds.add(filed)
                                            }

                                        }
                                    }
                                    it.set("fileds",finalFileds)
                                    mor.tenant.tenantDepartmentInfo.updateWithEntity(it).execUpdate()
                                }
                            }
                    }
                    //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
                    return JsonResult()
                }
    }

    fun checkInsertParameters(entity: TenantDepartmentInfoFieldExtend) {
        var hasField = mor.tenant.tenantDepartmentInfoFieldExtend.query().apply {
            this.where { it.tenant.id match entity.tenant.id }
            this.where { it.code match entity.code }
        }.exists()
        if (hasField) {
            throw RuntimeException("系统中已存在该字段")
        }
        if(entity.code.isEmpty()) throw RuntimeException("字段Key不能为空")
        if(entity.code.length>32) throw RuntimeException("字段Key不能超过32位")
        if(entity.name.isEmpty()) throw RuntimeException("显示名称不能为空")
        if(entity.name.length>32) throw RuntimeException("显示名称不能超过32位")
        if(entity.fieldType==null) throw RuntimeException("字段类型不能为空")
        if(entity.remark.length>255) throw RuntimeException("备注不能超过255个字符")
    }
    fun checkEditParameters(entity: TenantDepartmentInfoFieldExtend) {
        if(entity.name.isEmpty()) throw RuntimeException("显示名称不能为空")
        if(entity.name.length>32) throw RuntimeException("显示名称不能超过32位")
        if(entity.remark.length>255) throw RuntimeException("备注不能超过255个字符")
        var hasField = mor.tenant.tenantDepartmentInfoFieldExtend.query().apply {
            this.where { it.tenant.id match entity.tenant.id }
            this.where { it.code match entity.code }
            this.where { it.id match_not_equal  entity.id }
        }.exists()
        if (hasField) {
            throw RuntimeException("系统中已存在该字段")
        }
    }

}