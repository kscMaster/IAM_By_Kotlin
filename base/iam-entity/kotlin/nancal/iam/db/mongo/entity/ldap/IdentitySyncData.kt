package nancal.iam.db.mongo.entity.ldap

import nancal.iam.db.mongo.SyncJobDataObjectTypeEnum
import nbcp.db.*
import org.springframework.data.mongodb.core.mapping.Document

@Document
@RemoveToSysDustbin
@DbEntityGroup("tenant")
@Cn("同步任务数据")
data class IdentitySyncData(
    @Cn("任务ID")
    var jobId: String = "",
    @Cn("任务日志ID")
    var jobLogId: String = "",
    @Cn("主体类型")
    var objectType: SyncJobDataObjectTypeEnum = SyncJobDataObjectTypeEnum.Define,
    @Cn("主体数据标识")
    var objectData: IdName = IdName(),
    @Cn("同步类型")
    var syncType: String = "",
    @Cn("执行结果")
    var result: String = "",
    @Cn("错误信息")
    var msg: String = "",
): BaseEntity()