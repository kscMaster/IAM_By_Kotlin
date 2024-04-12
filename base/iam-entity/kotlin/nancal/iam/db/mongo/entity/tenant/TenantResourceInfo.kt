package nancal.iam.db.mongo.entity

import nbcp.db.*
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.tenant.TenantResourceGroup
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/15-17:19
 */
@Document
@DbEntityGroup("tenant")
@Cn("租户应用资源")
@DbEntityFieldRefs(
    DbEntityFieldRef("appInfo.code", "appInfo.name", SysApplication::class, "appCode"),
    DbEntityFieldRef("appInfo.code", "appInfo.name", TenantApplication::class, "appCode","name"),
//    DbEntityFieldRef("appInfo.code", "appInfo.name", SysApplication::class),
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class),
    DbEntityFieldRef("id", "type", SysResourceInfo::class),
    DbEntityFieldRef("id", "name", SysResourceInfo::class),
    DbEntityFieldRef("id", "code", SysResourceInfo::class),
    DbEntityFieldRef("id", "resource", SysResourceInfo::class),
    DbEntityFieldRef("id", "action", SysResourceInfo::class),
    DbEntityFieldRef("id", "remark", SysResourceInfo::class),
    DbEntityFieldRef("groups.id", "groups.$.name", TenantResourceGroup::class),
)
@VarDatabase("tenant.id")
@RemoveToSysDustbin
@DbEntityIndexes(
//    DbEntityIndex("tenant.id", "appInfo.code", "code", unique = true),
)
open class TenantResourceInfo @JvmOverloads constructor(
    var id: String = "",
    var tenant: IdName = IdName(),

    @Cn("应用")
    var appInfo: CodeName = CodeName(),

    @Cn("详情")
    var remark: String = "",

    @Cn("是否系统默认")
    var isSysDefine: Boolean = false,

    @Cn("系统默认ID")
    var sysId: String = "",

    @Cn("资源组")
    var groups: MutableList<IdName> = mutableListOf(),

    @Cn("创建时间")
    var createAt: LocalDateTime = LocalDateTime.now(),

    @Cn("更新时间")
    var updateAt: LocalDateTime? = null,

    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted: Boolean? = false
) : ResourceBaseInfo()