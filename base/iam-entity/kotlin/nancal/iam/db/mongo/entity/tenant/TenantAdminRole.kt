package nancal.iam.db.mongo.entity.tenant

import nbcp.db.*
import nancal.iam.db.mongo.*
import org.springframework.data.mongodb.core.mapping.Document

@Cn("租户管理员角色")
@Document
@DbEntityGroup("tenant")
@DbEntityIndexes(
    DbEntityIndex("code", unique = true)
)
data class TenantAdminRole(
    var code: TenantAdminTypeEnum = TenantAdminTypeEnum.None,
    @Cn("角色名称")
    var name: String = "",

//    @Cn("允许的Api")
//    var permissionApis: MutableList<IdNamePath> = mutableListOf(), //关联 AdminPermissionApi
    @Cn("授权的菜单")
    var menus: MutableList<String> = mutableListOf(),

//    @Cn("允许的页面按钮权限")
//    var pageActions: MutableList<AdminPermissionPageAction> = mutableListOf() // key=页面path,value=ans
) : BaseEntity()