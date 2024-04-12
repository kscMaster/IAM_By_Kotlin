//package nancal.iam.db
//
//import org.slf4j.LoggerFactory
//import nbcp.comm.*
//import nbcp.comm.*
//import nbcp.comm.*
//import nbcp.comm.*
//import nbcp.utils.*
//import nbcp.utils.*
//import nbcp.db.mysql.SingleSqlData
//import nbcp.db.mysql.SqlBaseTable
//import java.io.StringReader
//
//class BaseDataCacheOfSql   : IDataCacheService4Sql {
//
//    companion object {
//        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
//    }
//
//    fun getFromTables(sql: String): Set<String> {
//        return """\bfrom\b\s*([^\s]+\s*\.\s*)?([^\s]+)"""
//                .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
//                .findAll(sql, 0)
//                .map {
//                    //最后一个有值的
//                    for (i in (it.groupValues.size - 1) downTo 1) {
//                        var item = it.groupValues[i];
//                        if (item.isEmpty()) {
//                            continue
//                        }
//
//                        return@map item.toLowerCase();
//                    }
//                    return@map ""
//                }
//                .map {
//                    //可能是   `table`
//                    var quoted = (it.startsWith("`") && it.endsWith("`")) ||
//                            (it.startsWith("\"") && it.endsWith("\"")) ||
//                            (it.startsWith("[") && it.endsWith("]"))
//
//                    if (quoted) {
//                        return@map it.slice(1, -1)
//                    }
//
//                    return@map it
//                }
//                .toSortedSet()
//    }
//
//    fun getJoinTables(sql: String): Set<String> {
//        return """\bjoin\s+([^\s]+\s*\.\s*)?([^\s]+)"""
//                .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
//                .findAll(sql, 0)
//                .map {
//                    //最后一个有值的
//                    for (i in (it.groupValues.size - 1) downTo 1) {
//                        var item = it.groupValues[i];
//                        if (item.isEmpty()) {
//                            continue
//                        }
//
//                        return@map item.toLowerCase();
//                    }
//                    return@map ""
//                }
//                .map {
//                    return@map getUnquoteName(it)
//                }
//                .toSortedSet()
//    }
//
//    fun getUnquoteName(it: String): String {
//        if ((it.startsWith("`") && it.endsWith("`")) ||
//                (it.startsWith("\"") && it.endsWith("\"")) ||
//                (it.startsWith("[") && it.endsWith("]"))) {
//            return it.slice(1, -1)
//        }
//        return it;
//    }
//
//    fun getFromJoinTables(sql: String): Set<String> {
//        var set = hashSetOf<String>()
//        set.addAll(getFromTables(sql))
//        set.addAll(getJoinTables(sql))
//        return set;
//    }
//
//
//    /**
//     * @param sql 带 ? 参数
//     */
//    override fun getCacheKey(sql: SingleSqlData): CacheKey {
//        var select = (CCJSqlParserManager().parse(StringReader(sql.expression)) as Select).selectBody as PlainSelect
//        var tableName = getUnquoteName((select.fromItem as Table).name)
//
//        var set = getFromJoinTables(sql.expression)
//        var md5 = Md5Util.getBase64Md5(sql.expression + "\n" + sql.values.ToJson())
//
//        var ret = CacheKey(CacheKeyTypeEnum.Normal, md5, set)
//
//        //from 子查询 将忽略掉主键,隔离键 .
//        if (select.fromItem is Table == false) {
//            return ret
//        }
//
//
//        var dbEntity = dbr.getEntity(tableName)
//        if (dbEntity == null) {
//            return ret
//        }
//
//        var dbTable = (dbEntity as SqlBaseTable<*>)
//
//        var rks = dbTable.getRks()
//        var uks = dbTable.getUks()
//
//        var tableAlias = getUnquoteName(select.fromItem.alias?.name.AsString((select.fromItem as Table).name))
//
//        var rksValue = getWhereMap(select.where, rks, tableAlias, sql.values).data!!
//        var uksValue = getWhereMap(select.where, uks, tableAlias, sql.values).data!!
//
//        //如果键是全的.
//        var rksValid = rks.any { it.intersect(rksValue.keys).size == it.size }
//        var uksValid = uks.any { it.intersect(uksValue.keys).size == it.size }
//
//        if (rksValid && uksValid) {
//            ret.key = CacheKeyTypeEnum.UnionReginKey
//            ret.whereJson.putAll(uksValue)
//            ret.whereJson.putAll(rksValue)
//
//        } else if (rksValid) {
//            ret.key = CacheKeyTypeEnum.RegionKey
//            ret.whereJson.putAll(rksValue)
//        } else if (uksValid) {
//            ret.key = CacheKeyTypeEnum.UnionKey
//            ret.whereJson.putAll(uksValue)
//
//        }
//
//        return ret
//    }
//
//
//    fun getWhereMap(where: Expression, rks: Array<Array<String>>, tableAlias: String, parameters: JsonMap): ApiResult<StringMap> {
//        var ret = ApiResult.of(StringMap())
//
//        //如果有 or 则, or 前后都不可用. 用 ret.msg = "or" 表示.
//        if (where is OrExpression) {
//            ret.msg = "or"
//            return ret
//        }
//
//        if (where is Parenthesis) {
//            if (where.isNot) return ret
//
//            return getWhereMap(where.expression, rks, tableAlias, parameters)
//        }
//
//        if (where is EqualsTo) {
//            var left = where.leftExpression
//            var right = where.rightExpression
//
//            var flatrks = rks.toList().Unwind()
//            var columnName = ""
//            var value: Expression? = null
//            if (left is Column && getUnquoteName(left.table.name) == tableAlias) {
//                columnName = getUnquoteName(left.columnName)
//                if (flatrks.contains(columnName) == false) {
//                    return ret
//                }
//
//                value = right;
//            } else if (right is Column && getUnquoteName(right.table.name) == tableAlias) {
//                columnName = getUnquoteName(right.columnName)
//                if (flatrks.contains(columnName) == false) {
//                    return ret
//                }
//                value = left;
//            }
//
//
//            if (columnName.HasValue && value != null) {
//                if (value is StringValue) {
//                    ret.data = StringMap(columnName to value.value)
//                } else if (value is DoubleValue) {
//                    ret.data = StringMap(columnName to value.value.toString())
//                } else if (value is LongValue) {
//                    ret.data = StringMap(columnName to value.value.toString())
//                } else if (value is Column &&
//                        value.columnName.startsWith("#") &&
//                        value.columnName.endsWith("@")) {
//                    ret.data = StringMap(columnName to parameters.get(value.columnName.slice(1, -1)).AsString())
//                }
//                return ret
//            }
//        }
//        if (where is AndExpression) {
//            var leftResult = getWhereMap(where.leftExpression, rks, tableAlias, parameters)
//            if (leftResult.msg == "or") {
//                return ret
//            }
//
//            var rightResult = getWhereMap(where.rightExpression, rks, tableAlias, parameters)
//            if (rightResult.msg == "or") {
//                return ret
//            }
//
//            ret.data!!.putAll(leftResult.data!!)
//            ret.data!!.putAll(rightResult.data!!)
//
//            return ret
//        }
//        return ret
//    }
//
//    override fun getCacheJson(cacheKey: CacheKey ): String {
//        if (enable == false) return ""
//        if (cacheKey.key == CacheKeyTypeEnum.None) return ""
//
//        //如果正在删除依赖表中的任何一个,都不要再返回了.
//        if (cacheKey.dependencies.any { rer.sys.brokeKeys(it).scard() > 0 }) {
//            return "";
//        }
//        var ret = rer.sys.cacheSqlData.get(cacheKey.getExpression());
//
//        if (ret.isNotEmpty()) {
//            logger.info("命中缓存数据: ${cacheKey}")
//        }
//
//        return ret;
//    }
//
//    override fun setCacheJson(cacheKey: CacheKey, cacheJson: String,cacheSeconds:Int ) {
//        if (enable == false) return;
//        if ( cacheSeconds <= 0) return
//        if (cacheKey.key == CacheKeyTypeEnum.None) return
//
//        //如果正在删除依赖表中的任何一个,都不要再添加了.
//        var brokingTable = rer.sys.brokingTable.get()
//
//        if (cacheKey.dependencies.any { brokingTable == it }) {
//            return;
//        }
//
//        rer.sys.cacheSqlData.set(cacheKey.getExpression(), cacheJson,  cacheSeconds)
//    }
//
//
////    private fun getSetTable(sql: String): String {
////        var ret = """\bdelete\s+from\s+([^\s]+\s*\.\s*)?([^\s]+)|\bupdate\s+([^\s]+\s*\.\s*)?([^\s]+)|\binsert\s+into\s+([^\s]+\s*\.\s*)?([^\s]+)"""
////                .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
////                .findAll(sql, 0)
////                .map {
////                    //最后一个有值的
////                    for (i in (it.groupValues.size - 1) downTo 1) {
////                        var item = it.groupValues[i];
////                        if (item.isEmpty()) {
////                            continue
////                        }
////
////                        return@map item.toLowerCase();
////                    }
////                    return@map ""
////                }
////                .map {
////                    //可能是   `table`
////                    var quoted = (it.startsWith("`") && it.endsWith("`")) ||
////                            (it.startsWith("\"") && it.endsWith("\"")) ||
////                            (it.startsWith("[") && it.endsWith("]"))
////
////                    if (quoted) {
////                        return@map it.slice(1, -1)
////                    }
////
////                    return@map it
////                }
////                .firstOrNull() ?: ""
////
////        return ret
////    }
//
////    fun getBrokePatterns(sql: String, parameters: Array<Any?>): BrokePattern {
////        var ret = BrokePattern()
////        ret.mainEntity = getSetTable(sql)
//////        ret.where
////        return ret;
////    }
//
////    private fun getSqlKey(sqlKeys: Array<String>, value: String): String {
////        var list = sqlKeys.filter { it.equals(value, true) };
////        if (list.size == 0) return ""
////        if (list.size == 1) return list.first()
////        else {
////            throw Exception("getSqlKey 找到多个相同的Key: ${list.joinToString(",")}")
////        }
////    }
////
////    fun analyseSql(sql: String): List<SqlSectItem> {
////        var list = mutableListOf<SqlSectItem>()
////
////
////        var sqlKeys = SqlKeyEnum.values().filter { it != SqlKeyEnum.ExtraTable }.map { it.name }.toTypedArray()
////
////        var prevKey: String = ""
////        var prevIndex = 0
////        var index = -1;
////        var data = sql.Tokenizer();
////
////        while (true) {
////            index++;
////
////            if (index >= data.size) {
////                break;
////            }
////
////            var value = data[index]
////
////            var firstChar = value[0];
////            if (firstChar == '\t' || firstChar == '\n' || firstChar == ' ') {
////                continue
////            }
////
////            var sqlKey_StringValue = getSqlKey(sqlKeys, value);
////
////            if (sqlKey_StringValue.isEmpty()) {
////                continue
////            }
////
////            //添加上一个.
////            if (index > 0) {
////                var sqlKey = SqlKeyEnum.valueOf(prevKey)
////                var sqlValue = data.slice(prevIndex + 1, index).joinToString("")
////                list.add(SqlSectItem(sqlKey, sqlValue))
////            }
////
////            prevIndex = index;
////            prevKey = sqlKey_StringValue
////        }
////
////        if (prevIndex < data.size) {
////            var sqlKey = SqlKeyEnum.valueOf(prevKey)
////            var sqlValue = data.slice(prevIndex + 1, index).joinToString("")
////            list.add(SqlSectItem(sqlKey, sqlValue))
////        }
////
////        return list;
////    }
//
//    //封装 update , delete 自动化操作.
////    fun update_BrokeCache(tableName: String, whereJson:StringMap){
////
////    }
//
//    override fun updated4BrokeCache(sql: SingleSqlData) {
//        if (enable == false) return;
//        var update = (CCJSqlParserManager().parse(StringReader(sql.expression)) as Update)
//        var tableName = ""
//        var tableAlias = ""
//        if (update.fromItem != null) {
//            tableName = getUnquoteName((update.fromItem as Table).name)
//            tableAlias = getUnquoteName((update.fromItem as Table).alias?.name ?: "")
//        } else {
//            tableName = getUnquoteName(update.tables.first().name)
//            tableAlias = getUnquoteName(update.tables.first().alias?.name ?: "")
//        }
//
//        var cacheSeconds = this.cacheDefine.getOrDefault(tableName, "").AsInt()
//        if (cacheSeconds <= 0) return
//
//        var dbEntity = dbr.getEntity(tableName)
//        if (dbEntity == null) {
//            return
//        }
//
//        var urkInfo = getUrkInfo(
//                dbEntity as SqlBaseTable<*>,
//                getUnquoteName(tableAlias.AsString(tableName)),
//                update.where,
//                sql.values)
//
//
//        var set = setOf<String>()
//        if (urkInfo.rksValid && urkInfo.uksValid) {
//            set = getUpdateDeleteByReginUnion_BrokeCache(tableName, urkInfo.rks, urkInfo.uks)
//        } else if (urkInfo.rksValid) {
//            set = getUpdateByRegion_BrokeCache(tableName, urkInfo.rks)
//        } else if (urkInfo.uksValid) {
//            set = getUpdateDeleteById_BrokeCache(tableName, urkInfo.uks)
//        } else {
//            set = getUpdateDelete_BrokeCache(tableName)
//        }
//
//        rer.sys.brokeKeys(tableName).sadd(*set.toTypedArray())
//        rer.sys.borkeKeysChangedVersion.incr()
//    }
//
//    override fun delete4BrokeCache(sql: SingleSqlData) {
//        if (enable == false) return;
//        var delete = (CCJSqlParserManager().parse(StringReader(sql.expression)) as Delete)
//
//        var tableName = getUnquoteName(delete.table.name)
//
//        var cacheSeconds = this.cacheDefine.getOrDefault(tableName, "").AsInt()
//        if (cacheSeconds <= 0) return
//
//
//        var dbEntity = dbr.getEntity(tableName)
//        if (dbEntity == null) {
//            return
//        }
//
//        var urkInfo = getUrkInfo(
//                dbEntity as SqlBaseTable<*>,
//                getUnquoteName(delete.table.alias?.name.AsString(delete.table.name)),
//                delete.where,
//                sql.values)
//
//        var set = setOf<String>()
//        if (urkInfo.rksValid && urkInfo.uksValid) {
//            set = getUpdateDeleteByReginUnion_BrokeCache(tableName, urkInfo.rks, urkInfo.uks)
//        } else if (urkInfo.rksValid) {
//            set = getDeleteByRegion_BrokeCache(tableName, urkInfo.rks)
//        } else if (urkInfo.uksValid) {
//            set = getUpdateDeleteById_BrokeCache(tableName, urkInfo.uks)
//        } else {
//            set = getUpdateDelete_BrokeCache(tableName)
//        }
//
//        rer.sys.brokeKeys(tableName).sadd(*set.toTypedArray())
//        rer.sys.borkeKeysChangedVersion.incr()
//    }
//
//
//
//    fun getUrkInfo(dbTable: SqlBaseTable<*>, tableAlias: String, where: Expression, parameters: JsonMap): UrkInfo {
//        var rks = dbTable.getRks()
//        var uks = dbTable.getUks()
//
//        var rksValue = getWhereMap(where, rks, tableAlias, parameters).data!!
//        var uksValue = getWhereMap(where, uks, tableAlias, parameters).data!!
//
//        //如果键是全的.
//        var rksValid = rks.any { it.intersect(rksValue.keys).size == it.size }
//        var uksValid = uks.any { it.intersect(uksValue.keys).size == it.size }
//
//        return UrkInfo(rksValue, uksValue, rksValid, uksValid)
//    }
//
//    /** 1
//     * 移除缓存,
//     * 再启一个服务,遍历 brokeTables 清除缓存
//     */
//    fun getUpdateDeleteById_BrokeCache(tableName: String, idJson: StringMap): Set<String> {
////        rer.publish2brokeTables(tableName)
//
//        var valuePattern = idJson.toSortedMap().map { "${it.key}=${it.value}" }.joinToString("&")
//        if (valuePattern.HasValue) {
//            valuePattern = "&" + valuePattern + "&*"
//        }
//
//        var ret = hashSetOf<String>()
//        ret.add("rk*-${tableName}-*")
//        ret.add("uk*-${tableName}-*" + valuePattern)
//        ret.add("urk*-${tableName}-*" + valuePattern)
//        ret.add("sql*-${tableName}-*")
//        return ret;
//    }
//
//    //2
//    fun getUpdateByRegion_BrokeCache(tableName: String, regionValue: StringMap): Set<String> {
//        var valuePattern = regionValue.toSortedMap().map { "${it.key}=${it.value}" }.joinToString("&")
//        if (valuePattern.HasValue) {
//            valuePattern = "&" + valuePattern + "&*"
//        }
//
//        var ret = hashSetOf<String>()
//        ret.add("uk*-${tableName}-*")
//        ret.add("rk*-${tableName}-*" + valuePattern)
//        ret.add("urk*-${tableName}-*" + valuePattern)
//        ret.add("sql*-${tableName}-*")
//        return ret;
//    }
//
//    //3
//    fun getDeleteByRegion_BrokeCache(tableName: String, regionValue: StringMap): Set<String> {
//        var valuePattern = regionValue.toSortedMap().map { "${it.key}=${it.value}" }.joinToString("&")
//
//        if (valuePattern.HasValue) {
//            valuePattern = "&" + valuePattern + "&*"
//        }
//
//        var ret = hashSetOf<String>()
//        ret.add("uk*-${tableName}-*")
//        ret.add("rk*-${tableName}-*" + valuePattern)
//        ret.add("urk*-${tableName}-*" + valuePattern)
//        ret.add("sql*-${tableName}-*")
//        return ret;
//    }
//
//    //4
//    fun getUpdateDeleteByReginUnion_BrokeCache(tableName: String, regionValue: StringMap, idValue: StringMap): Set<String> {
//        var regionPattern = regionValue.toSortedMap().map { "${it.key}=${it.value}" }.joinToString("&")
//        var idPattern = idValue.toSortedMap().map { "${it.key}=${it.value}" }.joinToString("&")
//
//        if (regionPattern.HasValue) {
//            regionPattern = "&" + regionPattern + "&*"
//        }
//
//        if (idPattern.HasValue) {
//            idPattern = "&" + idPattern + "&*"
//        }
//
//        var ret = hashSetOf<String>()
//        ret.add("rk*-${tableName}-*" + regionPattern)
//        ret.add("uk*-${tableName}-*" + idPattern)
//        ret.add("urk*-${tableName}-*" + idPattern)
//        ret.add("sql*-${tableName}-*")
//        return ret
//    }
//
//    //5
//    fun getUpdateDelete_BrokeCache(tableName: String): Set<String> {
//        var ret = hashSetOf<String>()
//        ret.add("uk*-${tableName}-*")
//        ret.add("rk*-${tableName}-*")
//        ret.add("urk*-${tableName}-*")
//        ret.add("sql*-${tableName}-*")
//        return ret;
//    }
//
//    //7
//    override fun insert4BrokeCache(sql: SingleSqlData) {
//        if (enable == false) return;
//        var insert = (CCJSqlParserManager().parse(StringReader(sql.expression)) as Insert)
//
//        var tableName = getUnquoteName(insert.table.name)
//        var cacheSeconds = this.cacheDefine.getOrDefault(tableName, "").AsInt()
//        if (cacheSeconds <= 0) return
//
//        var dbEntity = dbr.getEntity(tableName)
//        if (dbEntity == null) {
//            return
//        }
//
//        var dbTable = dbEntity as SqlBaseTable<*>
//        var tableAlias = getUnquoteName(insert.table.alias?.name.AsString(insert.table.name))
//
//
//        var rks = dbTable.getRks()
//
//        var rksValue = JsonMap(* rks.toList().Unwind().map { it to sql.values.getOrDefault(it, null) }.filter { it.second != null }.toTypedArray())
//
//        var set = hashSetOf<String>()
//        rks.forEach { group ->
//            var regionValue = rksValue.filterKeys { group.contains(it) }
//            if (group.size == regionValue.size) {
//                //该组合法.
//                var valuePattern = regionValue.toSortedMap().map { "${it.key}=${it.value}" }.joinToString("&")
//
//                if (valuePattern.HasValue) {
//                    valuePattern = "&" + valuePattern + "&*"
//                }
//                set.add("rk*-${tableName}-*${valuePattern}")
//            }
//        }
//        set.add("sql*-${tableName}-*")
//        rer.sys.brokeKeys(tableName).sadd(*set.toTypedArray())
//        rer.sys.borkeKeysChangedVersion.incr()
//    }
//
//    override fun insertMany4BrokeCache(tableName: String) {
//        if (enable == false) return;
//        var set = mutableSetOf<String>()
//        set.add("rk*-${tableName}-*")
//        set.add("sql*-${tableName}-*")
//        rer.sys.brokeKeys(tableName).sadd(*set.toTypedArray())
//        rer.sys.borkeKeysChangedVersion.incr()
//    }
//
//    //8
//    override fun insertSelect4BrokeCache(tableName: String) {
//        if (enable == false) return;
//        var cacheSeconds = this.cacheDefine.getOrDefault(tableName, "").AsInt()
//        if (cacheSeconds <= 0) return
//
//        rer.sys.brokeKeys(tableName).sadd("rk*-${tableName}-*", "sql*-${tableName}-*")
//        rer.sys.borkeKeysChangedVersion.incr()
//    }
//}