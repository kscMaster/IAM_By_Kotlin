package nancal.iam.db.sql.entity

import nbcp.db.*
import nbcp.db.mysql.*
import nbcp.db.sql.*
import java.io.Serializable
import java.time.LocalDateTime

/**
 * Created by yuxh on 2018/9/20
 */

@DbEntityGroup("admin")
@DbEntityIndexes(
        DbEntityIndex("id", unique = true),
        DbEntityIndex("loginName", unique = true),
)
//后台管理员
open class admin_user(
        var updateAt: LocalDateTime = LocalDateTime.now(),
        @Cn("昵称")
        var name: String = "",      //这里的名称=自定义昵称
        @Cn("登录名")
        var loginName: String = "",
        @Cn("手机号")
        var mobile: String = "",
        @Cn("电子邮件")
        var email: String = "",
        @Cn("备注")
        var remark: String = "",
        var isAdmin: Boolean = false, //是否是超级管理员
        var address: String = ""
) : AutoIdSqlDbEntity()


@DbEntityGroup("admin")
@DbEntityIndexes(
        DbEntityIndex("id", unique = true),
        DbEntityIndex("userId", unique = true),
        DbEntityIndex("loginName", unique = true),
)
//管理员登录信息
open class admin_login_user(
        var updateAt: LocalDateTime = LocalDateTime.now(),

        @Cn("用户唯一Id")
        var userId: String = "",    //用户Id,唯一
        @Cn("登录名")
        var loginName: String = "",
        @Cn("登录手机")
        var mobile: String = "",    //认证后更新
        @Cn("登录邮箱")
        var email: String = "",     //认证后更新

        @Cn("密码")
        var password: String = "",  // Md5Util.getBase64Md5(pwd)
        @Cn("最后登录时间")
        var lastLoginAt: LocalDateTime = LocalDateTime.now(),

        @Cn("授权码")
        var authorizeCode: String = "", //授权码
        @Cn("令牌")
        var token: String = "",         //常用数据，也会放到主表。
        @Cn("刷新令牌")
        var freshToken: String = "",
        @Cn("授权码创建时间")
        var authorizeCodeCreateAt: LocalDateTime = LocalDateTime.now(),
        @Cn("是否已锁定")
        var isLocked: Boolean = false,
        @Cn("锁定详情")
        var lockedRemark: String = "",
        var errorLoginTimes: Byte = 0
) : AutoIdSqlDbEntity()





