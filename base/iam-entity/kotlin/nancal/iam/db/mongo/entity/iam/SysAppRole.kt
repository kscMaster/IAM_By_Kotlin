package nancal.iam.db.mongo.entity

import nbcp.db.*
import nancal.iam.db.mongo.entity.*
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/22-15:11
 */
@Document
@DbEntityGroup("iam")
@Cn("应用角色")
@DbEntityFieldRefs(
    DbEntityFieldRef("appInfo.code", "appInfo.name", SysApplication::class, "appCode"),
)
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("appInfo.code", unique = false),
    DbEntityIndex("appInfo.code", "name", unique = true),
)
open class SysAppRole @JvmOverloads constructor(
    @Cn("应用IdName")
    var appInfo: CodeName = CodeName(),

    @Cn("角色名称")
    var name: String = "",

    @Cn("备注")
    var remark: String = ""
) : BaseEntity()