package nancal.iam.db.mongo.entity.tenant

import nbcp.db.*
import nancal.iam.db.mongo.entity.*
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable

/**
 * @Author wrk
 *
 * @Description
 * @Date 2022/1/7-15:34
 */
//企业用户
@Document
@DbEntityGroup("tenant")
@RemoveToSysDustbin
@Cn("部门导入数据(成功数据)")
@VarDatabase("tenant.id")
@DbEntityFieldRefs(
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class),
)
open class ExcelDeportmentSuccessJob(

    @Cn("id")
    var id: String = "",
    @Cn("tenant")
    var tenant: IdName = IdName(),
    @Cn("jobId")
    var jobId: String = "",
    @Cn("部门路径")
    var path: String = ""
) : Serializable