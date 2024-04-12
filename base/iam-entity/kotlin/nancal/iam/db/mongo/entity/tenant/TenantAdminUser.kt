package nancal.iam.db.mongo.entity

import nbcp.db.*
import nbcp.db.cache.RedisCacheDefine
import nancal.iam.db.mongo.*
import nbcp.db.mongo.entity.BasicUser
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/15-17:11
 */
/**
 * 租户管理员
 */
@Document
@DbEntityGroup("tenant")
@VarDatabase("tenant.id")
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("code", unique = false),
    DbEntityIndex("tenant.id", unique = false),
    DbEntityIndex("loginName", unique = true),
    DbEntityIndex("mobile", unique = false),
    DbEntityIndex("email", unique = false),
)
@DbEntityFieldRefs(
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class)
)
@SortNumber("sort","",10)
@RedisCacheDefine("id")
@RemoveToSysDustbin
@Cn("租户管理员用户（废弃）")
open class TenantAdminUser(
    @Cn("工号")
    var code: String = "",
    @Cn("租户")
    var tenant: IdName = IdName(),

    @Cn("发送密码方式")
    var sendPasswordType: SendPasswordType? = null,

    @Cn("是否发送密码")
    var isSendPassword : Boolean =true,

    var sort: Float = 0F,
    @Cn("管理员类型")
    var userAdminType: TenantAdminTypeEnum = TenantAdminTypeEnum.Super,

    var enabled :Boolean =true,

//    @Cn("是否逻辑删除(false:不删除、true:删除)")
//    var isDeleted :Boolean? =false

) : BasicUser()