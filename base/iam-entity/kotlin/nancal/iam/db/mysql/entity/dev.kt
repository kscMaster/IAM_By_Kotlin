package nancal.iam.db.sql.entity

import nbcp.db.DbEntityGroup
import org.springframework.cglib.core.Local

import nbcp.db.mysql.*
import nbcp.db.sql.*
import nbcp.db.sql.*
import java.io.Serializable
import java.time.LocalDateTime
//
//
//@DbEntityGroup("dev")
////用户
//data class git_project(
//    @DbKey
//    @ConverterValueToDb(AutoIdConverter::class)
//    var id: Long = 0,
//    //姓名
//    var project_name: String = "",
//    //用户名
//    var url: String = ""
//) : Serializable
//
//@DbEntityGroup("dev")
////登录信息
//data class git_project_commit(
//    @DbKey
//    @ConverterValueToDb(AutoIdConverter::class)
//    var id: Long = 0,
//    var is_merge: Boolean = false,
//    var commit_message: String = "",
//    var project_id: Long = 0,
//    var commit_id: String = "",
//    var user_name: String = "",
//    var user_email: String = "",
//    var commit_at: LocalDateTime = LocalDateTime.now()
//) : Serializable
//
//@DbEntityGroup("dev")
////登录信息
//data class git_project_commit_log(
//    @DbKey
//    @ConverterValueToDb(AutoIdConverter::class)
//    var id: Long = 0,
//    var commit_id: String = "",
//    var file: String = "",
//    var action: GitFileActionEnum? = null,
//    var add_lines: Int = 0,
//    var delete_lines: Int = 0
//) : Serializable
//
