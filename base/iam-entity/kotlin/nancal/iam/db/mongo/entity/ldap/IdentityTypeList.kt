package nancal.iam.db.mongo.entity

import nbcp.db.*
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
@DbEntityGroup("iam")
@DbEntityFieldRefs(
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class)
)
@DbEntityIndexes(
    DbEntityIndex("code", unique = false),
    DbEntityIndex("name", unique = false),
)
@Cn("企业身份源类型")
open class IdentityTypeList(

    @Cn("主键")
    var id: String = "",

    @Cn("所属租户")
    var tenant: IdName = IdName(),

    @Cn("ldap")
    var ldap: Boolean = false,

    @Cn("saml")
    var saml: Boolean = false,

    @Cn("cas")
    var cas: Boolean = false,

    @Cn("oidc")
    var oidc: Boolean = false,

    @Cn("oauth2.0")
    var oauth: Boolean = false,

    @Cn("windowsAD")
    var windowsAD: Boolean = false,

    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted: Boolean? = false

) :Serializable