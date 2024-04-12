package nancal.iam.db.mongo.entity.ldap

import nancal.iam.db.mongo.ProtocolEnum
import nancal.iam.db.mongo.SyncJobDataObjectTypeEnum
import nancal.iam.db.mongo.SyncJobRiskDataStatusEnum
import nbcp.db.*
import org.springframework.data.mongodb.core.mapping.Document

@Cn("风险数据")
@Document
@RemoveToSysDustbin
@DbEntityGroup("tenant")
data class IdentitySyncRiskData(
    @Cn("租户")
    var tenant: IdName = IdName(),
    @Cn("身份源")
    var identitySource: ProtocolEnum = ProtocolEnum.LDAP,
    @Cn("主体类型")
    var objectType: SyncJobDataObjectTypeEnum = SyncJobDataObjectTypeEnum.Define,
    @Cn("主体数据标识")
    var objectData: IdName = IdName(),
    @Cn("同步类型")
    var syncType: String = "",
    @Cn("执行状态")
    var status: SyncJobRiskDataStatusEnum = SyncJobRiskDataStatusEnum.Unenforced,
): BaseEntity()