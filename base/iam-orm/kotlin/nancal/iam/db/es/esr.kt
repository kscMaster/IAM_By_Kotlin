package nancal.iam.db.es

import nbcp.db.BaseMetaData
import nbcp.db.db
import nancal.iam.db.es.table.SystemGroup
import nbcp.db.es.EsEntityCollector
import org.slf4j.LoggerFactory

class esr {
    companion object {
        val logger = LoggerFactory.getLogger(this::class.java.declaringClass)

        @JvmStatic
        val affectRowCount = db.affectRowCount

        @JvmStatic
        val system = SystemGroup()

        @JvmStatic
        val groups = db.es.groups;

        fun getCollection(collectionName: String): BaseMetaData? {
            return EsEntityCollector.getCollection(collectionName)
        }
    }
}