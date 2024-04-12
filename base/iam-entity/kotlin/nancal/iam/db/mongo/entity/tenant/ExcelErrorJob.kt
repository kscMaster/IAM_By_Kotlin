package nancal.iam.db.mongo.entity

import nbcp.db.*
import nancal.iam.db.mongo.*
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.time.LocalDateTime

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/15-17:07
 */
//企业用户
@Document
@DbEntityGroup("tenant")
@RemoveToSysDustbin
@Cn("导入数据(失败数据)")
@VarDatabase("tenant.id")
open class ExcelErrorJob(

    @Cn("id")
    var id: String = "",
    @Cn("jobId")
    var jobId: String = "",

    @Cn("行号")
    var rowNumber : Int = 0,

    @Cn("姓名")
    var name: String = "",
    @Cn("部门")
    var depts: String = "",   //多部门使用
    @Cn("职务")
    var duty: String = "",
    @Cn("用户名")
    var loginName: String = "",
    @Cn("手机号")
    var mobile: String = "",
    @Cn("邮箱")
    var email: String = "",
    @Cn("失败原因")
    var reason: String = "",
) : Serializable
