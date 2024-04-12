package nancal.iam.mvc.tenant

import io.swagger.annotations.*
import org.springframework.data.mongodb.core.query.*
import org.springframework.web.bind.annotation.*
import nancal.iam.base.extend.*
import nbcp.comm.*
import nbcp.db.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nbcp.base.mvc.*
import nbcp.web.*
import javax.servlet.http.*
import java.time.*


/**
 * Created by CodeGenerator at 2021-11-20 09:54:46
 */

@Api(description = "应用角色", tags = arrayOf("AppRole"))
@RestController
@RequestMapping("/tenant/excel-result")
class ExcelResultController {


    @ApiOperation("正确导入列表")
    @PostMapping("/right-list")
    fun list(
        @Require id: String,
        name: String,
        loginName: String,
        mobile: String,
        email: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<ExcelJob> {
        mor.tenant.excelJob.query()
            .apply {
                if (id.HasValue) {
                    this.where { it.jobId match id }
                }
                if (name.HasValue) {
                    this.where { it.name match_like name }
                }
                if (mobile.HasValue) {
                    this.where { it.mobile match_like mobile }
                }
                if (email.HasValue) {
                    this.where { it.email match_like email }
                }
            }
            .limit(skip, take)
            .toListResult()
            .apply {
                return this
            }
    }


    @ApiOperation("错误导入列表")
    @PostMapping("/error-list")
    fun errorlist(
        @Require id: String,
        name: String,
        loginName: String,
        mobile: String,
        email: String,
        reason: String,
        rowNumber: Int,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<ExcelErrorJob> {
        mor.tenant.excelErrorJob.query()
            .apply {
                if (id.HasValue) {
                    this.where { it.jobId match id }
                }
                if (name.HasValue) {
                    this.where { it.name match_like name }
                }
                if (mobile.HasValue) {
                    this.where { it.mobile match_like mobile }
                }
                if (email.HasValue) {
                    this.where { it.email match_like email }
                }
                if (email.HasValue) {
                    this.where { it.reason match_like reason }
                }
                if (rowNumber.HasValue) {
                    this.where { it.rowNumber match rowNumber }
                }
            }
            .limit(skip, take)
            .toListResult()
            .apply {
                return this
            }
    }


}
