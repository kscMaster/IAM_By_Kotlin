package nancal.iam.db.mongo.entity

import nbcp.db.*
import nbcp.db.cache.RedisCacheDefine
import nbcp.db.mongo.entity.SysOrganization
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.time.LocalDateTime

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/15-17:05
 */
/**
 * 企业信息
 */
@Document
@DbEntityGroup("tenant")
@RemoveToSysDustbin
@DbEntityFieldRefs(
    DbEntityFieldRef("industry.id", "industry.name", IndustryDict::class),
    DbEntityFieldRef("apps.id", "apps.$.name", SysApplication::class),
)
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("name", unique = false),
)
@RedisCacheDefine("id")
@Cn("租户")
open class Tenant(
    var code: String = "",
    @Cn("成立日期")
    var buildAt: LocalDateTime? = null,
    @Cn("行业")
    var industry: IdName = IdName(),
    @Cn("联系人")
    var concatName: String = "",
    @Cn("联系电话")
    var concatPhone: String = "",
    @Cn("邮箱")
    var email: String = "",
    @Cn("企业地址")
    var address: String = "",
    @Cn("企业简介")
    var remark: String = "",

    @Cn("企业密钥")
    var secret: String = "",

    @Cn("动态数据库地址")
    var aloneDbConnection: String = "",

    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted: Boolean? = false

) : SysOrganization(), Serializable