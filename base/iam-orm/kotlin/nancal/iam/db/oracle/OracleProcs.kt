//package nancal.iam.db.oracle
//
//import nbcp.comm.*
//import nbcp.db.sql.SingleSqlData
//import nbcp.db.sql.SqlBaseClip
//import nbcp.db.sql.SqlBaseExecuteClip
//import oracle.jdbc.OracleTypes
//import org.springframework.jdbc.core.CallableStatementCallback
//import org.springframework.jdbc.core.CallableStatementCreator
//import java.sql.CallableStatement
//import java.sql.Connection
//import java.sql.ResultSet
//
//class OracleProcs : SqlBaseClip("") {
//    override fun toSql(): SingleSqlData {
//        return SingleSqlData("");
//    }
//
//    private fun execWithCursor(dataSourceName: String, request: ((Connection) -> CallableStatement), response: ((CallableStatement) -> ResultSet)): List<JsonMap> {
//        return getJdbcTemplateByDatasrouce(dataSourceName).execute(
//                object : CallableStatementCreator {
//                    override fun createCallableStatement(con: Connection): CallableStatement {
//                        return request(con);
//                    }
//                }, object : CallableStatementCallback<List<JsonMap>> {
//
//            override fun doInCallableStatement(cs: CallableStatement): List<JsonMap> {
//                val resultsMap = mutableListOf<JsonMap>()
//                cs.execute()
//
//                var rs = response(cs);
//                while (rs.next()) {
//                    val row = JsonMap();
//                    for (i in 1..rs.metaData.columnCount) {
//                        var columnName = rs.metaData.getColumnName(i);
//                        row.set(columnName, rs.getObject(i));
//                    }
//                    resultsMap.add(row)
//                }
//                rs.close()
//                return resultsMap
//            }
//        }) as List<JsonMap>
//    }
//
//    fun test(orgId: String, orgName: String): List<JsonMap> {
//        return execWithCursor("", {
//            val storedProc = "{call SASE_SEARCH_LARGE_ESTIMATE_FEE(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}"// 调用的sql
//            val cs = it.prepareCall(storedProc)
//            cs.setString(1, "")// 设置输入参数的值
//            cs.setString(2, "")// 设置输入参数的值
//            cs.setString(3, "")// 设置输入参数的值
//            cs.setString(4, "")// 设置输入参数的值
//            cs.setString(5, "")// 设置输入参数的值
//            cs.setString(6, "")// 设置输入参数的值
//            cs.setString(7, "")// 设置输入参数的值
//            cs.setString(8, "")// 设置输入参数的值
//            cs.setString(9, "")// 设置输入参数的值
//            cs.setString(10, "")// 设置输入参数的值
//            cs.setInt(11, 1)// 设置输入参数的值
//            cs.setInt(12, 8)// 设置输入参数的值
//            cs.setString(13, "1143")// 设置输入参数的值
//
//            cs.registerOutParameter(14, OracleTypes.INTEGER)// 设置输入参数的值
//            cs.registerOutParameter(15, OracleTypes.CURSOR)// 注册输出参数的类型
//            return@execWithCursor cs
//        }, {
//            val r14 = it.getObject(14) as Int
//            println(r14)
//
//            return@execWithCursor it.getObject(15) as ResultSet
//        });
//    }
//}