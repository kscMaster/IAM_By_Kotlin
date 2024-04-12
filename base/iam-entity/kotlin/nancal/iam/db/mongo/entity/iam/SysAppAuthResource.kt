package nancal.iam.db.mongo.entity.iam

import nbcp.db.*
import nancal.iam.db.mongo.AuthTypeEnum
import nancal.iam.db.mongo.entity.*
import org.springframework.data.mongodb.core.mapping.Document

@Document
@DbEntityGroup("iam")
@Cn("应用授权")
@DbEntityFieldRefs(
    DbEntityFieldRef("appInfo.code", "appInfo.name", SysApplication::class, "appCode"),
//    DbEntityFieldRef("appInfo.code", "appInfo.name", SysApplication::class),
    DbEntityFieldRef("target.id", "target.name", SysAppRole::class),
    DbEntityFieldRef("auths.resourceId", "auths.$.code", SysResourceInfo::class, "id"),
    DbEntityFieldRef("auths.resourceId", "auths.$.name", SysResourceInfo::class, "id"),
    DbEntityFieldRef("auths.resourceId", "auths.$.type", SysResourceInfo::class, "id"),
    DbEntityFieldRef("auths.resourceId", "auths.$.resource", SysResourceInfo::class, "id"),
    DbEntityFieldRef("auths.resourceId", "auths.$.remark", SysResourceInfo::class, "id"),
)
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("appInfo.code", unique = false),
    DbEntityIndex("type", unique = false),
)
open class SysAppAuthResource @JvmOverloads constructor(
    @Cn("应用IdName")
    var appInfo: CodeName = CodeName(),

    @Cn("授权主体类型")
    var type: AuthTypeEnum = AuthTypeEnum.Role,

    @Cn("授权主体")
    var target: IdName = IdName(),

    @Cn("授权")
    var auths: MutableList<AuthResourceInfo> = mutableListOf(),
) : BaseEntity()
