package nancal.iam.utils

import nancal.iam.mvc.admin.SysAppResourceController
import kotlin.streams.toList

/**
 * 资源树转换工具类测试性能
 */
class List2TreeUtils {

    companion object {

        fun list2Tree(list: MutableList<SysAppResourceController.SysResourceTree>) : List<SysAppResourceController.SysResourceTree> {
            if (list.isEmpty()) {
                return mutableListOf()
            }
            val toList: List<SysAppResourceController.SysResourceTree> =
                list.stream().filter { a -> !a.code.contains(":") }.toList()
            list.removeAll(toList)
            val maxTreeSize: Int = maxTreeSize(list)
            toList.forEach {
                setValue(it, list, 0, maxTreeSize, "")
            }
            return toList

        }

        fun setValue(vo : SysAppResourceController.SysResourceTree,
                     list: List<SysAppResourceController.SysResourceTree>,
                     count:Int,
                     maxTreeSize: Int, parentCode:String ) : SysAppResourceController.SysResourceTree {
            if (count == maxTreeSize) {
                return vo
            }
            val toList: List<SysAppResourceController.SysResourceTree> =
                list.stream().filter { a -> a.code.startsWith(vo.code + ":") && countStr(a.code, ":") == count + 1 }.toList()
            vo.parentCode = parentCode
            if (toList.isEmpty()) {
                vo.children = null
            } else {
                vo.children = toList.toMutableList()
            }

            toList.forEach {
                setValue(it, list, count + 1, maxTreeSize, vo.code)
            }
            return vo
        }

        private fun maxTreeSize(list: List<SysAppResourceController.SysResourceTree>): Int {
            var max = 0
            list.stream().filter{a-> countStr(a.code, ":") == 2}.toList()
            list.forEach {
                val countStr: Int = countStr(it.code, ":")
                if (countStr> max) {
                    max = countStr
                }
            }
            return max
        }


        private fun countStr(longStr: String, mixStr: String): Int {
            var count = 0
            var index = 0
            while (longStr.indexOf(mixStr, index).also { index = it } != -1) {
                index += mixStr.length
                count++
            }
            return count
        }
    }
}