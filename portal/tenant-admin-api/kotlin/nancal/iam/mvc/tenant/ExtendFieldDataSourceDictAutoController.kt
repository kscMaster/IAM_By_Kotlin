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
import javax.servlet.http.*

/**
 * Created by CodeGenerator at 2021-11-23 11:36:44
 */
@Api(description = "扩展字段数据源字典", tags = arrayOf("ExtendFieldDataSourceDict"))
@RestController
@RequestMapping("/tenant/extend-field-data-source-dict")
class ExtendFieldDataSourceDictAutoController {

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        dataSource: String,
        name: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantExtendFieldDataSourceDict> {
        val tenant = request.LoginTenantAdminUser.tenant
        mor.tenant.tenantExtendFieldDataSourceDict.query()
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                this.where { it.tenant.id match tenant.id }
                if (dataSource.HasValue) {
                    this.where { it.dataSource match dataSource }
                }
                if (name.HasValue) {
                    this.where { it.name match_like name }
                }
            }.limit(skip, take).orderByDesc { it.createAt }
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
    ): ApiResult<TenantExtendFieldDataSourceDict> {
        val tenant = request.LoginTenantAdminUser.tenant
        mor.tenant.tenantExtendFieldDataSourceDict.queryById(id).apply {
            this.where { it.tenant.id match tenant.id }
        }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }

                return ApiResult.of(this)
            }
    }

    @BizLog(BizLogActionEnum.Save,BizLogResourceEnum.ExtendFieldDataSourceDict,"扩展字段数据源字典")
    @ApiOperation("更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: TenantExtendFieldDataSourceDict,
        request: HttpServletRequest
    ): ApiResult<String> {
        if(entity.id.isEmpty()) request.logMsg="创建扩展字段数据源字典{${entity.name}}"
        if(entity.id.isNotEmpty()) request.logMsg="修改扩展字段数据源字典{${entity.name}}"
        var tenant = request.LoginTenantAdminUser.tenant
        entity.tenant = tenant
        mor.tenant.tenantExtendFieldDataSourceDict.updateWithEntity(entity)
            
            .run {
                if (entity.id.HasValue) {
                    checkEditParameters(entity)
                    return@run this.execUpdate()
                } else {
                    checkInsertParameters(entity);
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
    @BizLog(BizLogActionEnum.Delete,BizLogResourceEnum.ExtendFieldDataSourceDict,"扩展字段数据源字典")
    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {

        val tenantId = request.LoginTenantAdminUser.tenant.id
        val entity = mor.tenant.tenantExtendFieldDataSourceDict.queryById(id)
            .where { it.tenant.id match tenantId }
            .toEntity()
        if (entity == null) {
            return JsonResult.error("找不到数据")
        }
        request.logMsg="删除扩展字段数据源字典{${entity.name}}"
        mor.tenant.tenantExtendFieldDataSourceDict.deleteById(id)
            .where { it.tenant.id match tenantId }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
                return JsonResult()
            }
    }

    fun checkEditParameters(entity: TenantExtendFieldDataSourceDict) {
        if(entity.code.isEmpty()) throw RuntimeException("code不能为空")
        if(entity.code.length>32) throw RuntimeException("code不能超过32位")
        if (entity.dataSource.HasValue) {
            throw RuntimeException("不可携带dataSource参数")
        }
        //查是否重复
        var isHas = mor.tenant.tenantExtendFieldDataSourceDict.query().apply {
            this.where { it.code match entity.code }
        }.exists();
        if (isHas) {
            throw RuntimeException("code重复，请重新填写")
        }


    }

    fun checkInsertParameters(entity: TenantExtendFieldDataSourceDict) {
        if(entity.code.isEmpty()) throw RuntimeException("code不能为空")
        if(entity.code.length>32) throw RuntimeException("code不能超过32位")
        if(entity.name.isEmpty()) throw RuntimeException("name不能为空")
        if(entity.name.length>32) throw RuntimeException("name不能超过32位")
        if(entity.dataSource.isEmpty()) throw RuntimeException("dataSource不能为空")
        if(entity.dataSource.length>32) throw RuntimeException("dataSource不能超过32位")
        //查是否重复
        var isHas = mor.tenant.tenantExtendFieldDataSourceDict.query().apply {
            this.where { it.code match entity.code }
        }.exists();
        if (isHas) {
            throw RuntimeException("code重复，请重新填写")
        }
    }
}