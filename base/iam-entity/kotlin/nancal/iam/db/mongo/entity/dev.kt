package nancal.iam.db.mongo.entity

import nbcp.db.*
import org.springframework.data.mongodb.core.mapping.Document


@Cn("数据连接")
@Document
@DbEntityGroup("dev")
@DbEntityIndexes(
    DbEntityIndex("updateAt",unique=false),
    DbEntityIndex("type",unique=false),
)
data class DbConnection(
    @Cn("名称")
    var name: String = "",
    @Cn("数据库类型")
    var type: DatabaseEnum? = null,
    @Cn("Ip")
    var host: String = "",
    @Cn("端口")
    var port: Int = 0,
    @Cn("用户名")
    var userName: String = "",
    @Cn("密码")
    var password: String = "",
    @Cn("数据库名")
    var dbName: String = "",
    @Cn("备注")
    var remark: String = ""
) : BaseEntity()