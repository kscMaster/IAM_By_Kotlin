package nancal.iam.db.mongo.entity.ldap

import nancal.iam.db.mongo.*
import nbcp.comm.JsonMap
import nbcp.db.*
import nbcp.db.mongo.entity.SysOrganization
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

@Cn("身份源同步任务")
@Document
@RemoveToSysDustbin
@DbEntityGroup("tenant")
data class IdentitySyncJob(
    @Cn("租户")
    var tenant: IdName = IdName(),
    @Cn("身份源")
    var identitySource: ProtocolEnum = ProtocolEnum.LDAP,
    @Cn("同步模式")
    var schema: SyncJobSchemaEnum = SyncJobSchemaEnum.Manually,
    @Cn("任务状态")
    var status: SyncJobStatusEnum = SyncJobStatusEnum.Ready,
) : BaseEntity()




