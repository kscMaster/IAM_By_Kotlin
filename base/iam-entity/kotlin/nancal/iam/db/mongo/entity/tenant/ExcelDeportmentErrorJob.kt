package nancal.iam.db.mongo.entity.tenant

import nbcp.db.*
import nancal.iam.db.mongo.entity.*
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable

/**
 * @Author wrk
 *
 * @Description
 * @Date 2022/1/7-15:33
 */

//企业用户
@Document
@DbEntityGroup("tenant")
@RemoveToSysDustbin
@Cn("部门导入数据(失败数据)")
@VarDatabase("tenant.id")
@DbEntityFieldRefs(
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class),
)
open class ExcelDeportmentErrorJob(
    @Cn("id")
    var id: String = "",
    @Cn("tenant")
    var tenant: IdName = IdName(),
    @Cn("jobId")
    var jobId: String = "",
    @Cn("行号")
    var rowNumber: Int = 0,
    @Cn("部门路径")
    var path: String = "",
    @Cn("失败原因")
    var reason: String = "",
    @Cn("失败部分")
    var failDep: String = ""

) : Serializable