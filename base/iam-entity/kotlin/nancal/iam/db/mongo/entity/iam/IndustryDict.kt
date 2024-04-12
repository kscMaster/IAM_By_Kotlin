package nancal.iam.db.mongo.entity

import nbcp.db.*
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/22-15:12
 */
@Document
@DbEntityGroup("iam")
@Cn("行业字典")
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("code", unique = true),
)
open class IndustryDict @JvmOverloads constructor(
    @Cn("编码")
    var code: String = "",
    @Cn("名称")
    var name: String = "",
) : BaseEntity()