package nancal.iam.db.mongo.entity

import nbcp.db.*
import nancal.iam.db.mongo.entity.*
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/22-15:11
 */
@Document
@DbEntityGroup("iam")
@Cn("应用资源")
@DbEntityFieldRefs(
    DbEntityFieldRef("appInfo.code", "appInfo.name", SysApplication::class, "appCode"),
)
@DbEntityIndexes(
//    DbEntityIndex("appInfo.code", "code", unique = true),
)
open class SysResourceInfo @JvmOverloads constructor(
    @Cn("主键ID")
    var id: String = "",

    @Cn("应用")
    var appInfo: CodeName = CodeName(),

    @Cn("详情")
    var remark: String = "",

    @Cn("创建时间")
    var createAt: LocalDateTime = LocalDateTime.now(),

    @Cn("更新时间")
    var updateAt: LocalDateTime? = null
) : ResourceBaseInfo()
