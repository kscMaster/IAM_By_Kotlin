package nancal.iam.db.mongo.entity.iam

import nbcp.db.*
import nancal.iam.db.mongo.entity.*
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
@DbEntityGroup("iam")
@Cn("租户侧菜单")
open class TenantMenu @JvmOverloads constructor(



    @Cn("创建时间")
    var createAt: LocalDateTime = LocalDateTime.now(),
    @Cn("更新时间")
    var updateAt: LocalDateTime? = null,
) : MenuDefine()
