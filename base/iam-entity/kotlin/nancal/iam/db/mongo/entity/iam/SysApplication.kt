package nancal.iam.db.mongo.entity

import nbcp.db.*
import nancal.iam.db.mongo.entity.*
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/22-15:10
 */
/**
 * 应用中心
 */
@Document
@DbEntityGroup("iam")
@Cn("应用")
@DbEntityFieldRefs(
    DbEntityFieldRef("industry.id", "industry.name", IndustryDict::class),
)
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("appCode", unique = true),
    DbEntityIndex("appSourceDbId", unique = false),
)
@RemoveToSysDustbin
open class SysApplication @JvmOverloads constructor(

    @Cn("数据同步用")
    var appSourceDbId: String = "",

    @Cn("版本")
    var version: String = "",
    @Cn("版本")
    var protal: String? = "",

    @Cn("应用私钥")
    var privateKey: String = "",
    @Cn("应用公钥")
    var publicKey: String = "",

    @Cn("启用true/禁用false")
    var enabled: Boolean = true

) : BaseApplication()
