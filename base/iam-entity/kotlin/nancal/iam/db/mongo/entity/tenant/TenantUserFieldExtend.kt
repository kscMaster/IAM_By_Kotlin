package nancal.iam.db.mongo.entity

import nbcp.db.*
import nancal.iam.db.mongo.entity.*
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/15-17:20
 */
@Document
@DbEntityGroup("tenant")
@Cn("租户用户自定义字段")
@VarDatabase("tenant.id")
@RemoveToSysDustbin
open class TenantUserFieldExtend(
    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted :Boolean? =false
) : ExtendFieldDefine()