package nancal.iam.util

import nbcp.comm.HasValue
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 *@Author syf
 * @Date 2022 06 01
 **/
class CodeUtils {

    companion object {
        /**
         * code 截取 ::a:::b::::c::::
         */
        fun codeHolder(code:String) :MutableList<String> {
            val codeList : MutableList<String> = mutableListOf()
            var codeStr = ""
            // :a:b::c
            code.split(":").toMutableList().forEach{
                if (it.HasValue) {
                    codeStr = if (codeStr.HasValue && codeStr.replace(":","").HasValue) {
                        "$codeStr:$it"
                    } else if (codeStr.HasValue && !codeStr.replace(":","").HasValue){
                        codeStr + it
                    } else {
                        it
                    }
                    codeList.add(codeStr)
                } else {
                    if (codeStr.replace(":","").HasValue) { // 不是第一个元素
                        codeStr += ":"
                        codeList.add(codeStr)
                    } else { // 第一个元素前有数据
                        codeStr += ":"
                    }
                }
            }
            for (index in codeList.size - 1 downTo 0){
                if ( index < codeList.size -1 &&
                    codeList[index].replace(":","") == codeList[index+1].replace(":",""))  {
                    codeList.removeAt(index)
                }
            }


            println(codeList.size)
            return codeList
        }
    }
}