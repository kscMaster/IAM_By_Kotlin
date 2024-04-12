package nbcp.mvc.admin

import nancal.iam.mvc.admin.SysAppResourceController
import nbcp.comm.HasValue
import nbcp.comm.remove

/**
 *@Author syf
 * @Date 2022 05 30
 **/
class MyTest {
    companion object {
        fun <T> page(list: List<T>, page: Int, size: Int): List<T> {
            return when {
                list.size < page * size -> emptyList()
                list.size >= page * size && list.size <= (page + 1) * size -> list.subList(
                    page * size,
                    list.size
                )
                else -> list.subList(page * size, (page + 1) * size)
            }
        }
    }
}

fun main() {


    var mutableList = mutableListOf("Java", "Kotlin", "aa", "Go")
    mutableList.removeAt(mutableList.size - 1)

    val list = arrayListOf("A", "B", "C", "D", "E")

//    var codeStr = ""
//    list.forEach { s ->
//        codeStr += s
//    }

    val code = ":a:b::c"  // null a b null c
    val code1 = "::a:b::c:"  // null a b null c
    val code2 = "a:b:c:"  // null a b null c
    val code3 = "a:b:c::d"  // null a b null c
    val code4 = ":::a::::b::::c:::::d::::"  // null a b null c
//    println(codeHolder(code))
//    println(codeHolder(code1)) // 处理末尾
//    println(codeHolder(code2))
    println(codeHolder(code1))


}

/**
 * 开头为null 拼接：
 * 中间为null 拼接:
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


