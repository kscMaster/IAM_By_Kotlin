package nancal.iam.utils

import nancal.iam.base.config.AdminEnConfig
import nancal.iam.db.mongo.BizLogActionEnum
import nancal.iam.db.mongo.BizLogResourceEnum
import nancal.iam.util.ResultUtils
import nbcp.comm.HasValue
import nbcp.db.IdName
import org.springframework.stereotype.Component
import javax.annotation.Resource

/**
 *@Author shyf
 * @Date 2022/06/25
 **/
@Component("adminEnumMatchUtils")
class EnumMatchUtils{

    @Resource
    lateinit var adminEnConfig : AdminEnConfig


    /**
     * 事件类型处理
     */
    fun getCnKey(cn: String): String {
        val msg = cn.replace(":", "")

        try {
            var en: String = adminEnConfig.bundleBizLog.getString(msg)
            if (en.HasValue) {
                return en
            }
            BizLogActionEnum.values().forEach { a ->
                BizLogResourceEnum.values().forEach { b ->
                    if (("${a.remark}${b.remark}") == msg) {
                        return adminEnConfig.bundleBizLog.getString(a.remark) + " " + adminEnConfig.bundleBizLog.getString(b.remark)
                    }
                }
            }
        } catch (e: Exception) {
            e.message
            println(e.message)
        }
        return cn
    }

    /**
     * 事件详情处理
     */
    fun getMsgKey(cn: String): String {
        try {
            val msg = cn.replace(":", "")
            if (msg.contains("{")) {
                val clearBracket = ResultUtils.clearBracket(msg)
                val langRes: String = adminEnConfig.bundleBizLog.getString(clearBracket)
                return if (langRes == "") {
                    langRes
                } else {
                    ResultUtils.fillBracket(langRes, msg)
                }
            } else {
                val res: String = adminEnConfig.bundleBizLog.getString(msg)
                if (res.HasValue) {
                    return res
                }
            }
        } catch (e: Exception) {
            e.message
            println(e.message)
        }
        return cn
    }

    /**
     * 角色
     */
    fun getRoleKey(cns: MutableList<IdName>): MutableList<IdName> {
        try {
            cns.forEach {
                var en: String = adminEnConfig.bundleBizLog.getString(it.name)
                if (en.HasValue) {
                    it.name = en
                }
            }

        } catch (e: Exception) {
            e.message
            println(e.message)
        }
        return cns
    }


    fun getEnKey(cn: String): String {
        try {
            val msg = cn.replace(":", "")
            var en: String = adminEnConfig.bundleBizLog.getString(msg)
            if (en.HasValue) {
                return en
            }
        } catch (e: Exception) {
            e.message
            println(e.message)
        }

        return cn
    }

    /**
     * 失败原因
     */
    fun getErrorKey(cn: String): String {
        try {
            val msg = cn.replace(":", "")
            if (msg.contains("{")) {
                val clearBracket = ResultUtils.clearBracket(msg)
                val langRes: String = adminEnConfig.bundle.getString(clearBracket)
                return if (langRes == "") {
                    return langRes
                } else {
                    return ResultUtils.fillBracket(langRes, msg)
                }
            } else {
                val res: String = adminEnConfig.bundle.getString(msg)
                if (res.HasValue) {
                    return res
                }
            }
        } catch (e: Exception) {
            e.message
            println(e.message)
        }
        return cn
    }

}

fun main() {
    println("a" + " " + "b")
}