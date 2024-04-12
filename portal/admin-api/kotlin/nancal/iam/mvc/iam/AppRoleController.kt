package nancal.iam.web.admin

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.*
import nancal.iam.base.extend.*
import nbcp.comm.*
import nbcp.db.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nbcp.base.mvc.*
import nbcp.web.*
import javax.servlet.http.HttpServletRequest
import java.time.*

/**
 * Created by CodeGenerator at 2021-03-14 16:00:34
 */
@Api(description = "后台角色", tags = arrayOf("AdminRole"))
@RestController
@RequestMapping("/admin/admin-role")
class AdminRoleAutoController {

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        name: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<AdminRole> {

        mor.admin.adminRole.query()
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (name.HasValue) {
                    this.where { it.name match_like name }
                }
            }
            .limit(skip, take).orderByDesc { it.createAt }
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
    ): ApiResult<AdminRole> {
        mor.admin.adminRole.queryById(id)
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error<AdminRole>("找不到数据")
                }

                return ApiResult.of(this)
            }
    }

    @ApiOperation("更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: AdminRole,
        request: HttpServletRequest
    ): ApiResult<String> {
        //鉴权
        var userId = request.UserId;

        mor.admin.adminRole.updateWithEntity(entity)

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

    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        //鉴权
        var userId = request.UserId

        var entity = mor.admin.adminRole.queryById(id).toEntity()
        if (entity == null) {
            return JsonResult.error("找不到数据")
        }

        mor.admin.adminRole.deleteById(id)
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
                //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
                return JsonResult()
            }
    }
}