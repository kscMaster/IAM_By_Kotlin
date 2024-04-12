package nancal.iam.db.mongo.entity.ldap

import nancal.iam.db.mongo.ProtocolEnum
import nbcp.db.*
import org.springframework.data.mongodb.core.mapping.Document

@Document
@RemoveToSysDustbin
@DbEntityGroup("tenant")
@Cn("同步任务记录")
data class IdentitySyncJobLog(
    @Cn("租户")
    var tenant: IdName = IdName(),
    @Cn("身份源")
    var identitySource: ProtocolEnum = ProtocolEnum.LDAP,
    @Cn("任务ID")
    var jobId: String = "",
    @Cn("成功条数")
    var successNumber: Int = 0,
    @Cn("错误条数")
    var errorNumber: Int = 0,
    @Cn("错误信息")
    var msg: String = "",
) : BaseEntity()
