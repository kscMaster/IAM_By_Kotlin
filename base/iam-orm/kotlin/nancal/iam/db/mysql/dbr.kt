package nancal.iam.db.sql

import nancal.iam.db.sql.table.*
import nbcp.comm.*
import nbcp.utils.*
import nbcp.db.*
import nbcp.db.sql.*
import nbcp.db.sql.*
import nbcp.db.sql.entity.*
import nbcp.db.sql.table.*


class dbr {
    companion object {
        val affectRowCount: Int
            get() = db.affectRowCount

        @JvmStatic
        val admin
            get() = AdminGroup();


        @JvmStatic
        val system
            get() = SystemGroup();

        @JvmStatic
        val base
            get() = db.sqlBase

        @JvmStatic
        val groups
            get() = db.sql.groups

        @JvmStatic
        fun getEntity(tableName: String): BaseMetaData? {
            var groups = groups

            var ret: BaseMetaData? = null
            groups.any { group ->
                ret = group.getEntities().firstOrNull { it.tableName == tableName }

                return@any ret != null
            }

            return ret;
        }

//        val event = SpringUtil.getBean<SqlEventConfig>();

//        private val execAffectRows: ThreadLocal<Int> = ThreadLocal<Int>()
//
//        fun setDbrExecAffectRows(n: Int) = execAffectRows.set(n)
//
//        @JvmStatic
//        val affectRowCount: Int
//            get() {
//                return execAffectRows.get() ?: -1
//            }
//
//
//        private val execLastAutoId: ThreadLocal<Int> = ThreadLocal<Int>()
//
//        fun setDbrExecLastAutoId(n: Int) = execLastAutoId.set(n)
//
//        @JvmStatic
//        val lastAutoId: Int
//            get() {
//                return execLastAutoId.get() ?: -1
//            }
//
//        @JvmStatic
//        internal val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
//
//        @JvmStatic
//        fun getQuoteName(oriValue:String):String = "`${oriValue}`"
//
//        val converter: LinkedHashMap<SqlColumnName, IConverter> = linkedMapOf()
    }
}