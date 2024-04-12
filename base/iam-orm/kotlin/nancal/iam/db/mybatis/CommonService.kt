package nancal.iam.db.mybatis

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import nbcp.comm.*
import nancal.iam.db.mybatis.mapper.CommonMapper

/**
 * Created by yuxh on 2018/6/26
 */

@Service
open abstract class CommonService {
    @Autowired
    protected lateinit var commonMapper: CommonMapper


    fun getCountSql(sql: String): String {
        //对比两个的性能. 正则可能会慢.
        //select\b((?!\bfrom\b).)*\bfrom\b(.*)
        var fromSql = """^\s*\bselect\b((?!\bfrom\b).)*\bfrom\b(.*)"""
                .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
                .find(sql)
                ?.groupValues
                ?.lastOrNull()

        if (fromSql == null) {
            throw Exception("解析出错")
        }

        //去除最后的 limit
        fromSql = """(.*)\blimit\b\s*\d(\s*,\s*\d)?\s*$"""
                .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
                .find(fromSql)
                ?.groupValues?.get(1) ?: fromSql

        //去除最后的 order by
        fromSql = """(.*)\border\b\s*by\b\s*\S+\s*\b\w*\b\s*${'$'}"""
                .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
                .find(fromSql)
                ?.groupValues?.get(1) ?: fromSql

        return "select count(1) cou from " + fromSql;
    }


    fun <T> findList(sql: String, skip: Int, take: Int): ListResult<T> {
        var ret = ListResult<T>();

        ret.data = commonMapper.select(sql + " limit ${skip},${take}")


        //查条数
        if (skip == 0) {
            if (ret.data.size != take) {
                ret.total = ret.data.size;
            } else {
                var countSql = getCountSql(sql);
                ret.total = commonMapper.select<Int>(countSql).firstOrNull() ?: 0
            }
        }

        return ret;
    }
}