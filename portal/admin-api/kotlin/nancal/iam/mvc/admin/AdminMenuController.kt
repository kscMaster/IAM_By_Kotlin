package nancal.iam.mvc.admin

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.*
import nancal.iam.base.extend.*
import nbcp.comm.*
import nbcp.db.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nbcp.utils.RecursionReturnEnum
import nbcp.utils.RecursionUtil
import nbcp.base.mvc.*
import nbcp.web.*
import org.bson.Document
import javax.servlet.http.HttpServletRequest
import java.time.*

/**
 * Created by CodeGenerator at 2021-03-14 15:58:11
 */
@Api(description = "菜单", tags = arrayOf("AdminMenu"))
@RestController
@RequestMapping("/admin/admin-menu")
class AdminMenuAutoController {

    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        name: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<Document> {

        var myMenus = listOf<String>();
        if (request.LoginUser.isAdmin == false) {
            myMenus = mor.admin.adminRole.query()
                .where { it.id match_in request.LoginUser.roles }
                .toList()
                .map { it.menus }
                .Unwind();
        }

        mor.admin.adminMenu.query()

            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (name.HasValue) {
                    this.where { it.name match_like name }
                }

                if (request.LoginUser.isAdmin == false) {
                    this.where { it.id match_in myMenus }
                }
            }
            .orderByAsc { it.sort }
            .toTreeJson({ it.id }, { it.parent.id }, "", "menus")
            .apply {
                return ListResult.of(this)
            }
    }

//    private fun sort_with(menus: MutableList<MenuDefine>) {
//        RecursionUtil.execute<MenuDefine>(menus, { it.menus }, { wbs, index ->
//            var it = wbs.last();
//            it.menus.sortBy { it.sort }
//            return@execute RecursionReturnEnum.Go
//        });
//    }

//    private fun filter_with_roles(menus: MutableList<MenuDefine>, request: HttpServletRequest) {
//        if (request.LoginUser.isAdmin) return;
//
//        var myRoles = mor.admin.adminRole.query()
//            .where { it.id match_in request.LoginUser.roles }
//            .toList()
//
//        var myMenuIds = myRoles.map { it.menus.map { it.id }  }.Unwind().toSet()
//
//        RecursionUtil.execute<MenuDefine>(menus, { it.menus }, { wbs, index ->
//            var it = wbs.last();
//            if (myMenuIds.contains(it.id) == false) {
//                return@execute RecursionReturnEnum.Remove
//            }
//
//            it.menus.sortBy { it.sort }
//            return@execute RecursionReturnEnum.Go
//        })
//    }

    @ApiOperation("详情")
    @PostMapping("/detail/{id}")
    fun detail(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<AdminMenu> {
        mor.admin.adminMenu.queryById(id)
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error<AdminMenu>("找不到数据")
                }

                return ApiResult.of(this)
            }
    }

    /**
     * 遍历所有根节点更新。
     */
    @ApiOperation("更新")
    @PostMapping("/save")
    fun save(
        pid: String,
        @JsonModel entity: AdminMenu,
        request: HttpServletRequest
    ): ApiResult<String> {
        //鉴权
        var userId = request.UserId;

        var actionName = if (entity.id.HasValue) "更新" else "添加"

        var parent = IdName();

        if (pid.HasValue) {
            parent = mor.admin.adminMenu.queryById(pid).toEntity(IdName::class.java)!!;
        }

        entity.parent = parent
        mor.admin.adminMenu.updateWithEntity(entity)
            .execUpdate().apply {
                if (this == 0) {
                    return ApiResult.error("${actionName}失败")
                }
            }

        return ApiResult.of(entity.id)
    }

    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String
    ): JsonResult {
        mor.admin.adminMenu.deleteById(id).exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
            }

        return JsonResult();
    }
}
