package nancal.iam.util

open class ResultUtils {
    companion object {
        fun fillBracket(enString: String, oldString: String): String {

            var list = mutableListOf<String>()
            // 修改原来的逻辑，防止右括号出现在左括号前面的位置
            var context = oldString
            var head = context.indexOf('{') // 标记第一个使用左括号的位置
            if (head == -1) {
                return context // 如果context中不存在括号，什么也不做，直接跑到函数底端返回初值str
            } else {
                var next = head + 1 // 从head+1起检查每个字符
                var count = 1 // 记录括号情况
                var currentIndex = 0
                do {
                    if (next > currentIndex && context[next] == '{') count++ else if (context[next] == '}') count--
                    next++ // 更新即将读取的下一个字符的位置
                    if (count == 0) // 已经找到匹配的括号
                    {
                        val temp = context.substring(head + 1, next - 1) // 将两括号之间的内容提取到temp中
                        list.add(temp)
                        head = context.indexOf('{', head + 1) // 找寻下一个左括号
                        next = head + 1 // 标记下一个左括号后的字符位置
                        currentIndex = next
                        count = 1 // count的值还原成1
                    }
                } while (head != -1) // 如果在该段落中找不到左括号了，就终止循环
            }

            context = enString
            head = context.indexOf('{') // 标记第一个使用左括号的位置
            if (head == -1) {
                return context // 如果context中不存在括号，什么也不做，直接跑到函数底端返回初值str
            } else {

                var replaceCount = 0

                var next = head + 1 // 从head+1起检查每个字符
                var count = 1 // 记录括号情况
                var currentIndex = 0

                var temp = 0
                do {
                    if (next > currentIndex && context[next] == '{') count++ else if (context[next] == '}') count--
//                if (next>currentIndex && context[next] == '{' ) count++ else if (next>currentIndex && context[next] == '}' ) count--
                    next++ // 更新即将读取的下一个字符的位置
                    if (count == 0) // 已经找到匹配的括号
                    {
                        if (replaceCount == 0) {
                            var stringStart = context.substring(0, head)
                            var stringEnd = context.substring(head + 2)
                            context = stringStart + list.get(replaceCount) + stringEnd
                        } else {
                            var stringStart = context.substring(0, next - 2)
                            var stringEnd = context.substring(next)
                            context = stringStart + list.get(replaceCount) + stringEnd
                        }

                        head = context.indexOf('{', head + 1) // 找寻下一个左括号
                        next = head + 1 // 标记下一个左括号后的字符位置
                        currentIndex = next
                        count = 1 // count的值还原成1
                        replaceCount++
                    }
                } while (head != -1) // 如果在该段落中找不到左括号了，就终止循环
            }


            return context // 返回更新后的context
        }


        open fun clearBracket(context: String): String {

            // 修改原来的逻辑，防止右括号出现在左括号前面的位置
            var context = context
            var head = context.indexOf('{') // 标记第一个使用左括号的位置
            if (head == -1) {
                return context // 如果context中不存在括号，什么也不做，直接跑到函数底端返回初值str
            } else {
                var next = head + 1 // 从head+1起检查每个字符
                var count = 1 // 记录括号情况
                var currentIndex = 0
                do {
                    if (next > currentIndex && context[next] == '{') count++ else if (context[next] == '}') count--
                    next++ // 更新即将读取的下一个字符的位置
                    if (count == 0) // 已经找到匹配的括号
                    {

                        var stringStart = context.substring(0, head + 1)
                        var endTemp = context.indexOf('}', head + 1)
                        var stringEnd = context.substring(endTemp)

                        context = stringStart + stringEnd
                        head = context.indexOf('{', head + 1) // 找寻下一个左括号
                        next = head + 1 // 标记下一个左括号后的字符位置
                        currentIndex = next
                        count = 1 // count的值还原成1
                    }
                } while (head != -1) // 如果在该段落中找不到左括号了，就终止循环
            }
            return context // 返回更新后的context
        }
    }
}