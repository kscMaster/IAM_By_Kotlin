package nancal.iam.db.mongo.entity

import nbcp.db.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nbcp.db.mongo.entity.*
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document
@DbEntityGroup("admin")
@DbName("system.users")
open class SysMongoAdminUser(
    var id: String = "",
    var userId: UUID? = null,
    var user:String = "",
    var db:String = "",
    var credentials: MongoCredentialsData = MongoCredentialsData(),
    var roles :MutableList<MongoRoleData> = mutableListOf()
    /*
{
"_id" : "cms-3.dev",
"userId" : UUID("8fb31dae-2bff-4928-a4c8-261b71842c5c"),
"user" : "dev",
"db" : "cms-3",
"credentials" : {
    "SCRAM-SHA-1" : {
        "iterationCount" : 10000,
        "salt" : "RAuLnqffAl5erHQcRnwXmg==",
        "storedKey" : "ADXifKB/otBzX3eIi/M47oRt2eQ=",
        "serverKey" : "OpcAhX70B4qxuGlnOp93evQBdog="
    },
    "SCRAM-SHA-256" : {
        "iterationCount" : 15000,
        "salt" : "6vFtj3WAL+K1jNajPw1HTfUTUgkysz5Z04KTQA==",
        "storedKey" : "NtOOtGqeLDNr36yOtGwp/Am+vBo8PBcncxrEh9Tt/6M=",
        "serverKey" : "h0Sy+xc8xzQwgh66EWKvdtRKuV6ZdjU2DsY0Rvd491A="
    }
},
"roles" : [
    {
        "role" : "readWrite",
        "db" : "cms-3"
    }
]
}
     */
)

/**
 * 平台管理员
 */
@Document
@DbEntityGroup("admin")
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("loginName", unique = true),
    DbEntityIndex("mobile", unique = false),
    DbEntityIndex("email", unique = false),
)
open class AdminUser(

    @Cn("是否为超级管理员")
    var isAdmin: Boolean = false, //是否是超级管理员
    @Cn("地址")
    var address: String = "",
    @Cn("角色")
    var roles: MutableList<IdName> = mutableListOf(),
    @Cn("发送密码方式")
    var sendPasswordType: SendPasswordType? = null,

) : BasicUser()

/**
 * 平台员工登录用户
 */
@Document
@DbEntityGroup("admin")
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("loginName", unique = true),
    DbEntityIndex("mobile", unique = false),
    DbEntityIndex("email", unique = false),
)
data class AdminLoginUser(
    @Cn("登录出错次数")
    var errorLoginTimes: Byte = 0,

    @Cn("密码的盐")
    var passwordSalt : String = "",
) : BasicUserLoginInfo()


@Cn("后台角色")
@Document
@DbEntityGroup("admin")
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false)
)
data class AdminRole(
    var code:String = "",
    @Cn("角色名称")
    var name: String = "",

//    @Cn("允许的Api")
//    var permissionApis: MutableList<IdNamePath> = mutableListOf(), //关联 AdminPermissionApi
    @Cn("授权的菜单")
    var menus: MutableList<String> = mutableListOf(),

//    @Cn("允许的页面按钮权限")
//    var pageActions: MutableList<AdminPermissionPageAction> = mutableListOf() // key=页面path,value=ans
) : BaseEntity()

@Cn("菜单")
@Document
@DbEntityGroup("admin")
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false)
)
data class AdminMenu(
    @Cn("创建时间")
    var createAt: LocalDateTime = LocalDateTime.now(),
    @Cn("更新时间")
    var updateAt: LocalDateTime? = null,

    ) : MenuDefine()


@Cn("权限接口")
@Document
@DbEntityGroup("admin")
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false)
)
data class AdminPermissionApi(
    @Cn("接口名称")
    var name: String = "",
    @Cn("地址,格式[方法]@/url")
    var url: String = ""
) : BaseEntity()


@Cn("权限页面定义")
@Document
@DbEntityGroup("admin")
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("path", unique = false),
)
data class AdminPermissionPage(
    @Cn("页面名称")
    var name: String = "",
    @Cn("vue路由")
    var path: String = ""
) : BaseEntity()


@Cn("权限页面按钮")
@Document
@DbEntityGroup("admin")
@DbEntityIndexes(
    DbEntityIndex("updateAt", unique = false),
    DbEntityIndex("page.path", unique = false),
    DbEntityIndex("action", unique = false),
)
data class AdminPermissionPageAction(
    @Cn("按钮名称")
    var name: String = "",
    @Cn("动作定义")
    var action: String = "",

    @Cn("页面")
    var page: IdNamePath = IdNamePath()
) : BaseEntity()

