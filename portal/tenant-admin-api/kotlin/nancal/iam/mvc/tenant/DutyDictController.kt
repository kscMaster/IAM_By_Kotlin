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
 * Created by CodeGenerator at 2021-11-17 17:01:20
 */
@Api(description = "岗位字典", tags = arrayOf("DutyDict"))
@RestController
@RequestMapping("/tenant/duty-dict")
class DutyDictAutoController {

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        id:String,
        name:String,
        code:String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TenantDutyDict> {
        var tenant=request.LoginTenantAdminUser.tenant

        mor.tenant.tenantDutyDict.query()
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (name.HasValue) {
                    this.where { it.name match_like name }
                }
                if (code.HasValue) {
                    this.where { it.code match_like code }
                }
                    this.where { it.tenant.id match  tenant.id }
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
    ): ApiResult<TenantDutyDict> {
        var tenant=request.LoginTenantAdminUser.tenant
        mor.tenant.tenantDutyDict.query().apply {
            this.where { it.id match id }
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

    @BizLog(BizLogActionEnum.Save,BizLogResourceEnum.DutyDict,"岗位字典")
    @ApiOperation("新增/更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: TenantDutyDict,
        request: HttpServletRequest
    ): ApiResult<String> {
        if(entity.id.isEmpty()) request.logMsg="创建岗位{${entity.name}}"
        if(entity.id.isNotEmpty()) request.logMsg="修改岗位{${entity.name}}"
        var msg=checkParmters(entity)
        if(msg.isNotEmpty()) return ApiResult.error(msg)
        //鉴权
        var tenant = request.LoginTenantAdminUser.tenant
        entity.tenant=tenant
        mor.tenant.tenantDutyDict.updateWithEntity(entity)
            
            .run {
                if (entity.id.HasValue) {
                    return@run this.execUpdate()
                } else {
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

    @BizLog(BizLogActionEnum.Delete,BizLogResourceEnum.DutyDict,"岗位字典")
    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        var tenant=request.LoginTenantAdminUser.tenant
        var entity = mor.tenant.tenantDutyDict.queryById(id).toEntity()
        if (entity == null) {
            return JsonResult.error("找不到数据")
        }
        request.logMsg="删除岗位{${entity.name}}"

        mor.tenant.tenantDutyDict.deleteById(id)
            .where { it.tenant.id match tenant.id }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
                //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
                return JsonResult()
            }
    }
    fun checkParmters(entity: TenantDutyDict):String{
        if(entity.name.isEmpty()) return "岗位名称不能为空"
        if(entity.name.length>32) return "岗位名称不能超过32个字符"
        if(entity.code.isNotEmpty() && entity.code.length>32) return  "岗位code不能超过32个字符"
        return ""
    }
}