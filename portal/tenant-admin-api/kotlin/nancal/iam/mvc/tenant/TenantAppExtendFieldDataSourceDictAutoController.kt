package nancal.iam.mvc.tenant

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import nancal.iam.annotation.BizLog
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nbcp.comm.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.TenantAppExtendFieldDataSourceDict
import nancal.iam.db.sql.dbr
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

/**
 * @Author wrk
 *
 * @Description 应用扩展字段数据源表
 * @Date 2021/12/13-18:12
 */
@Api(description = "应用扩展字段数据源字典", tags = arrayOf("SysApplication"))
@RestController
@RequestMapping("/tenant/sys-app-extend-field-data-source-dict")
class TenantAppExtendFieldDataSourceDictAutoController {
    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        dataSource: String,
        name: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantAppExtendFieldDataSourceDict> {

        val tenant = request.LoginTenantAdminUser.tenant
        mor.tenant.tenantAppExtendFieldDataSourceDict.query()
            .apply {
                this.where { it.tenant.id match tenant.id }
                if (id.HasValue) {
                    this.where { it.id match id }
                }
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
    ): ApiResult<TenantAppExtendFieldDataSourceDict> {
        val tenant = request.LoginTenantAdminUser.tenant
        mor.tenant.tenantAppExtendFieldDataSourceDict.query()
            .where { it.id match id }
            .where { it.tenant.id match tenant.id }
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error("找不到数据")
                }
                return ApiResult.of(this)
            }
    }

    @BizLog(BizLogActionEnum.Save,BizLogResourceEnum.TenantApplicationFieldExtend,"数据源")
    @ApiOperation("更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: TenantAppExtendFieldDataSourceDict,
        request: HttpServletRequest
    ): ApiResult<String> {
        if(entity.id.isEmpty()) request.logMsg="创建应用扩展字段数据源{${entity.name}}"
        if(entity.id.isNotEmpty()) request.logMsg="修改应用扩展字段数据源{${entity.name}}"
        val tenant = request.LoginTenantAdminUser.tenant
        entity.tenant = tenant
        if(entity.id.HasValue){
            checkEditParameters(entity)
            mor.tenant.tenantAppExtendFieldDataSourceDict.update()
                .set { it.code to entity.code }
                .set { it.name to entity.name }
                .exec()
            if(dbr.affectRowCount==0) return ApiResult.error("更新失败")
            return ApiResult()

        }
        checkInsertParameters(entity)
        mor.tenant.tenantAppExtendFieldDataSourceDict.doInsert(entity)
        if(dbr.affectRowCount==0) return ApiResult.error("新增失败")
        return ApiResult()
    }

    @BizLog(BizLogActionEnum.Delete,BizLogResourceEnum.TenantApplicationFieldExtend,"数据源")
    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        val tenant = request.LoginTenantAdminUser.tenant
        val entity = mor.tenant.tenantAppExtendFieldDataSourceDict.queryById(id).toEntity()
        if (entity == null) {
            return JsonResult.error("找不到数据")
        }
        request.logMsg="删除应用扩展字段数据源[{${entity.name}}]"
        mor.tenant.tenantAppExtendFieldDataSourceDict.deleteById(id)
            .where { it.tenant.id match tenant.id }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
                return JsonResult()
            }
    }

    fun checkEditParameters(entity: TenantAppExtendFieldDataSourceDict) {
        if(entity.code.isEmpty()) throw RuntimeException("code不能为空")
        if(entity.code.length>32) throw RuntimeException("code不能超过32位")
        if(entity.name.isEmpty()) throw RuntimeException("name不能为空")
        if(entity.name.length>32) throw RuntimeException("name不能超过32位")
        if (entity.dataSource.HasValue)  throw RuntimeException("不可携带dataSource参数")
        val isHas = mor.tenant.tenantAppExtendFieldDataSourceDict.query().apply {
            this.where { it.code match entity.code }
        }.exists();
        if (isHas) {
            throw RuntimeException("code重复，请重新填写")
        }


    }

    fun checkInsertParameters(entity: TenantAppExtendFieldDataSourceDict) {
        if(entity.code.isEmpty()) throw RuntimeException("code不能为空")
        if(entity.code.length>32) throw RuntimeException("code不能超过32位")
        if(entity.name.isEmpty()) throw RuntimeException("name不能为空")
        if(entity.name.length>32) throw RuntimeException("name不能超过32位")
        if(entity.dataSource.isEmpty()) throw RuntimeException("dataSource不能为空")
        //查是否重复
        val isHas = mor.tenant.tenantAppExtendFieldDataSourceDict.query().apply {
            this.where { it.code match entity.code }
        }.exists();
        if (isHas) {
            throw RuntimeException("code重复，请重新填写")
        }
    }
}