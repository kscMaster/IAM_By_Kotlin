package nancal.iam.web.sys

import io.swagger.annotations.Api
import nancal.iam.db.sql.dbr
import org.springframework.web.bind.annotation.*
import nbcp.comm.*
import nbcp.utils.*

import nbcp.db.mysql.*
import nbcp.db.sql.*

/**
 * Created by yuxh on 2018/9/27
 */
@Api(tags = arrayOf("人员"))
@RestController
@RequestMapping("/user")
class UserController {
    @PostMapping("/list")
    fun list_user(name: String, mobile: String, skip: Int, take: Int): ListResult<JsonMap> {
        var data = dbr.system.s_user.query()

        if (name.HasValue) {
            data.where { it.name like "%${name}%" }
        }

        if (mobile.HasValue) {
            data.where { it.mobile like "%${mobile}%" }
        }

//        if (isChecker.HasValue) {
//            data.where { it.isChecker match_equal isChecker.AsBoolean() }
//        }
//
//        if (isManager.HasValue) {
//            data.where { it.isManager match_equal isManager.AsBoolean() }
//        }
        data.orderByDesc { it.id }
                .limit(skip, take)

        return data.toMapListResult()
    }

}