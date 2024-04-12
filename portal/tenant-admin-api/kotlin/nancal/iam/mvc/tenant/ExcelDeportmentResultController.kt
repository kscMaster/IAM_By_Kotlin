package nancal.iam.mvc.tenant

import io.swagger.annotations.*
import org.springframework.data.mongodb.core.query.*
import org.springframework.web.bind.annotation.*
import nancal.iam.base.extend.*
import nancal.iam.comm.LoginTenantAdminUser
import nbcp.comm.*
import nbcp.db.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.mongo.entity.tenant.ExcelDeportmentErrorJob
import nancal.iam.db.mongo.entity.tenant.ExcelDeportmentSuccessJob
import nbcp.base.mvc.*
import nbcp.web.*
import javax.servlet.http.*
import java.time.*


/**
 * Created by CodeGenerator at 2021-11-20 09:54:46
 */

@Api(description = "部门", tags = arrayOf("DepartmentInfo"))
@RestController
@RequestMapping("/tenant/excel-deportment-result")
class ExcelDeportmentResultController {


    @ApiOperation("正确导入列表")
    @PostMapping("/right-list")
    fun list(
        id: String,
        name: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<ExcelDeportmentSuccessJob> {
        var tenant=request.LoginTenantAdminUser.tenant
        mor.tenant.excelDeportmentSuccessJob.query()
            .apply {
                this.where { it.tenant.id match tenant.id }
                if (id.HasValue) {
                    this.where { it.jobId match id }
                }
                if (name.HasValue) {
                    this.where { it.path match_like name }
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
        id: String,
        name: String,
        reason: String,
        rowNumber: Int,
        failDep: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<ExcelDeportmentErrorJob> {
        var tenant=request.LoginTenantAdminUser.tenant
        mor.tenant.excelDeportmentErrorJob.query()
            .apply {
                this.where { it.tenant.id match  tenant.id }
                if (id.HasValue) {
                    this.where { it.jobId match id }
                }
                if (name.HasValue) {
                    this.where { it.path match_like name }
                }
                if (reason.HasValue) {
                    this.where { it.reason match_like reason }
                }
                if (rowNumber.HasValue) {
                    this.where { it.rowNumber match rowNumber }
                }
                if(failDep.HasValue){
                    this.where { it.failDep match_like  failDep }
                }
            }
            .limit(skip, take)
            .toListResult()
            .apply {
                return this
            }
    }


}
