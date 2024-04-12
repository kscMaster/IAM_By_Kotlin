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
 * 企业私密信息
 */
@Document
@DbEntityGroup("tenant")
@RemoveToSysDustbin
@DbEntityFieldRefs(
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class),
)
@DbEntityIndexes(
    DbEntityIndex("tenant.id", unique = true),
)
@Cn("租户设置")
open class TenantSecretSet(
    var tenant: IdName = IdName(),

    @Cn("系统私钥")
    var sysPrivateSecret: String = "",

    @Cn("企业公钥")
    var publicSecret: String = "",

    @Cn("安全设置")
    var setting: TenantSetting = TenantSetting(),

    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted :Boolean? =false

    ) : BaseEntity()