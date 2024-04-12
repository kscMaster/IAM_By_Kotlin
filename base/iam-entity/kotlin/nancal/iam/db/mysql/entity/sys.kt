package nancal.iam.db.sql.entity

import nbcp.db.DbEntityGroup
import nbcp.db.DbEntityIndex
import nbcp.db.DbEntityIndexes
import org.springframework.cglib.core.Local

import nbcp.db.mysql.*
import nbcp.db.sql.*
import nbcp.db.sql.*
import java.io.Serializable
import java.time.LocalDateTime


//系统附件表
//
//@SqlEntityGroup("system")
//@DbUks("id")
//@SqlRks("corp_id")
//data class s_annex(
//        @SqlAutoIncrementKey
//        var id: Int = 0,
//        var name: String = "",          //显示的名字,友好的名称
////        var localPath: String = "",     //本地文件路径以及文件名。 用来删除。
//        var ext: String = "",           //后缀名。
//        var size: Int = 0,              //大小
//        var checkCode: String = "",     //Md5,Sha1
//        var imgWidth: Int = 0,          // 图像宽度值。
//        var imgHeight: Int = 0,         // 图像宽度值。
//        var url: String = "",           //下载的路径。没有 host
//
//        @SqlFk("s_user", "id")
//        var createby_id: Int = 0, //创建者
//        var createby_name: String = "", //创建者
//
//        var errorMsg: String = "",      //文件处理时的错误消息
//        var createAt: LocalDateTime = LocalDateTime.now()
//) : IBaseDbEntity() {
//}
//
//
//@SqlEntityGroup("system")
//@DbUks("id")
//data class s_log(
//        @SqlAutoIncrementKey
//        var id: Int = 0,
//        var msg: String = "",
//        var creatAt: LocalDateTime = LocalDateTime.now(),
//        var createBy_id: String = "",
//        var createBy_name: String = "",
//        var type: String = "",
//        var clientIp: String = "",
//        var module: String = "",
//        var remark: String = ""
//) : IBaseDbEntity()


@DbEntityGroup("system")
@DbEntityIndexes(
        DbEntityIndex("id", unique = false),
        DbEntityIndex("loginName", unique = false)
)
//用户
data class s_user(
        @SqlAutoIncrementKey
        var id: Int = 0,
        //姓名
        var name: String = "",
        //用户名
        var loginName: String = "",
        //员工号
        var code: String = "",
        //手机号
        var mobile: String = "",

        //是否是盘点员
        var isChecker: Boolean = false,

        //是否是管理员
        var isAdmin: Boolean = false,

        var isDisabled: Boolean = false,

        //用于Api交互
        var token: String = "",

        //最后更新时间
        var updateAt: LocalDateTime = LocalDateTime.now(),
        var createAt: LocalDateTime = LocalDateTime.now()
) : Serializable

@DbEntityGroup("system")
@DbEntityIndexes(
        DbEntityIndex("id", unique = true),
        DbEntityIndex("loginName", unique = true)
)
//登录信息
data class s_login_user(
        var id: Int = 0,
        var loginName: String = "",
        var password: String = "",

        var lastLoginAt: LocalDateTime = LocalDateTime.now(),
        var errorCount: Int = 0,
        var forget_password: Boolean = false,
        var updateAt: LocalDateTime = LocalDateTime.now(),
        var createAt: LocalDateTime = LocalDateTime.now()
) : Serializable
