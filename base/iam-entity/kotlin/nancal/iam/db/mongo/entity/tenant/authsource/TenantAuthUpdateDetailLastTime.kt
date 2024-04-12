package nancal.iam.db.mongo.entity.tenant.authsource

import nancal.iam.db.mongo.AuthTypeEnum
import nancal.iam.db.mongo.DataActionEnum
import nancal.iam.db.mongo.entity.*
import nbcp.db.*
import nbcp.db.cache.RedisCacheDefine
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * @Author zhaopeng
 *
 * @Description
 * @Date 2022/5/16-17:18
 */
@Document
@DbEntityGroup("tenant")
@Cn("最后同步标准表时间")
@DbEntityIndexes(
    DbEntityIndex("createAt", unique = false),
)
open class TenantAuthUpdateDetailLastTime @JvmOverloads constructor(

    var id: String = "",

    @Cn("创建时间")
    var createAt: LocalDateTime = LocalDateTime.now(),

    @Cn("更新时间")
    var updateAt: LocalDateTime? = null

)