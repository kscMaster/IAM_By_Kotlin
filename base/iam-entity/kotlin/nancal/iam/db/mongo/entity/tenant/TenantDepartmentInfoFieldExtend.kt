package nancal.iam.db.mongo.entity

import nbcp.db.*
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/15-17:21
 */
@Document
@DbEntityGroup("tenant")
@Cn("部门自定义字段")
@VarDatabase("tenant.id")
@RemoveToSysDustbin
open class TenantDepartmentInfoFieldExtend(

    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted :Boolean? =false
) : ExtendFieldDefine()