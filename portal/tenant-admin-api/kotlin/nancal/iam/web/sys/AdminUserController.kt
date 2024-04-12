package nancal.iam.web.sys

import io.swagger.annotations.Api
import org.springframework.web.bind.annotation.*
import nbcp.comm.*
import nbcp.utils.*

import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.AdminUser
import org.bson.Document

/**
 * Created by yuxh on 2018/9/27
 */
@Api(tags = arrayOf("人员"))
@RestController
@RequestMapping("/admin-user")
class AdminUserController {
    @PostMapping("/list")
    fun list_user(name: String, mobile: String, skip: Int, take: Int): ListResult<Document> {
        var data = mor.admin.adminUser.query()
                .where { it.loginName match_not_equal "admin" }

        if (name.HasValue) {
            data.where { it.name match_like "%${name}%" }
        }

        if (mobile.HasValue) {
            data.where { it.mobile match_like "%${mobile}%" }
        }

//        if (isChecker.HasValue) {
//            data.where { it.isChecker match_equal isChecker.AsBoolean() }
//        }
//
//        if (isManager.HasValue) {
//            data.where { it.isManager match_equal isManager.AsBoolean() }
//        }
        data.orderByDesc { it.createAt }
                .limit(skip, take)

        return data.toMapListResult()
    }

    @PostMapping("/detail/{id}")
    fun detail(@Require id: String): ApiResult<AdminUser> {
        mor.admin.adminUser.queryById(id).toEntity()
                .apply {
                    return ApiResult.of(this)
                }
    }


    @PostMapping("/resetPassword")
    fun forgetPassword(userId: String): JsonResult {
        var defaultPassword = Md5Util.getBase64Md5("12345678");
        mor.admin.adminLoginUser.updateById(userId)
                .set { it.password to defaultPassword }
                .exec()

        if (mor.affectRowCount > 0) {
            return JsonResult();
        } else return JsonResult.error("重置用户密码失败")
    }
}