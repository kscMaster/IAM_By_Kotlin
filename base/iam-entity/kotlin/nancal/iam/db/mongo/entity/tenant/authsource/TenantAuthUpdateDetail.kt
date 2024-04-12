package nancal.iam.db.mongo.entity.tenant.authsource

import nancal.iam.db.mongo.AuthTypeEnum
import nancal.iam.db.mongo.DataActionEnum
import nancal.iam.db.mongo.entity.*
import nbcp.db.*
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @Author wrk
 *
 * @Description
 * @Date 2021/12/15-17:18
 */
@Document
@DbEntityGroup("tenant")
@Cn("应用授权同步详情记录表")
@DbEntityFieldRefs(
    DbEntityFieldRef("appInfo.code", "appInfo.name", SysApplication::class),
    DbEntityFieldRef("tenant.id", "tenant.name", Tenant::class),
    DbEntityFieldRef("target.id", "target.name", TenantAppRole::class),
    DbEntityFieldRef("auths.resourceId", "auths.$.name", TenantResourceInfo::class, "id"),
    DbEntityFieldRef("auths.resourceId", "auths.$.code", TenantResourceInfo::class, "id"),
    DbEntityFieldRef("auths.resourceId", "auths.$.type", TenantResourceInfo::class, "id"),
    DbEntityFieldRef("auths.resourceId", "auths.$.resource", TenantResourceInfo::class, "id"),
    DbEntityFieldRef("auths.resourceId", "auths.$.action", TenantResourceInfo::class, "id"),
    DbEntityFieldRef("auths.resourceId", "auths.$.remark", TenantResourceInfo::class, "id"),
)
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("createAt", unique = false),
    DbEntityIndex("tenant.id", unique = false),
    DbEntityIndex("appInfo.code", unique = false),
    DbEntityIndex("type", unique = false),
)
@VarDatabase("tenant.id")
open class TenantAuthUpdateDetail @JvmOverloads constructor(

    @Cn("授权数据ID")
    var authId : String = "",

    @Cn("授权主体类型")
    var actionType: DataActionEnum? = null,

    @Cn("应用IdName")
    var appInfo: CodeName = CodeName(),

    @Cn("租户")
    var tenant: IdName = IdName(),

    @Cn("授权主体类型")
    var type: AuthTypeEnum? = null,

    @Cn("授权主体")
    var target: IdName = IdName(),

    @Cn("授权")
    var auths: MutableList<AuthResourceInfo> = mutableListOf(),

    @Cn("子部门是否授权，只在type=Dept时用")
    var childDeptsAll : Boolean = false,

    /**
     * 从 SysAppRole 同步时， isSysDefine = true
     * 同步角色资源授权关系时，同步资源，再同步角色，最后同步关系。
     * 更新时：
     * 循环更新关系表。
     *  SysAppRole.appInfo.code + SysAppRole.name
     *        ==> 从 TenantAppRole 表中确定  id ,标记 tenantRoleId
     *        ==> 从 TenantAppAuthResourceInfo 中查询: type == Role and target.id == tenantRoleId and isSysDefine == true
     *        更新auths ： SysAppRole.auths 在 TenantResouceInfo 中的值。
     */
    @Cn("是否系统默认")
    var isSysDefine: Boolean = false,

    @Cn("系统默认ID")
    var sysId: String = "",

    @Cn("备注")
    var remark: String = "",

    @Cn("是否逻辑删除(false:不删除、true:删除)")
    var isDeleted :Boolean? =false
) : BaseEntity() {
}