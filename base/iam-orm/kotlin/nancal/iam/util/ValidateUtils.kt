package nancal.iam.util

import java.util.regex.Pattern

/**
 * @Classname UserUtils
 * @Description TODO
 * @Version 1.0.0
 * @Date 14/12/2021 上午 11:57
 * @Created by kxp
 */
class ValidateUtils {

    companion object {

        /**
         * 是否包含空格
         *
         * @param str
         * @return
         */
        fun containerSpace(str: String): Boolean {
            return str.indexOf(" ") != -1
        }

        /**
         * 中文过滤
         *
         * @param str
         * @return
         */
        fun isContainChinese(str: String?): Boolean {
            val p = Pattern.compile("[\u4e00-\u9fa5]")
            val m = p.matcher(str)
            return m.find()
        }

        /**
         * 获取字符串中字符类型的数量
         *
         * @param str
         */
        fun getCharKindByRegex(str: String): Int {
            val letterPattern = Pattern.compile("[a-zA-Z]")
            val numberPattern = Pattern.compile("[0-9]")
            val blandPattern = Pattern.compile("[\\s|\r|\n|\t]") //不全
            val letterMatcher = letterPattern.matcher(str)
            val numberMatcher = numberPattern.matcher(str)
            val blandMatcher = blandPattern.matcher(str)
            val stringBufferLetter = StringBuffer()
            val stringBufferNumber = StringBuffer()
            val stringBufferBlank = StringBuffer()
            while (letterMatcher.find()) {
                stringBufferLetter.append(letterMatcher.group(0))
            }
            while (numberMatcher.find()) {
                stringBufferNumber.append(numberMatcher.group(0))
            }
            while (blandMatcher.find()) {
                stringBufferBlank.append(blandMatcher.group(0))
            }
            val otherNumber =
                str.length - stringBufferLetter.length - stringBufferNumber.length - stringBufferBlank.length
            println("**********")
            println("字母类型：" + stringBufferLetter.length + "个")
            println("数字类型：" + stringBufferNumber.length + "个")
            println("空格类型：" + stringBufferBlank.length + "个")
            println("其它类型：" + otherNumber + "个")
            val list: MutableList<Int> = ArrayList<Int>(4)
            list.add(stringBufferLetter.length)
            list.add(stringBufferNumber.length)
            list.add(stringBufferBlank.length)
            list.add(otherNumber)
            var result = 0
            for (i in list.indices) {
                if (list[i] as Int > 0) {
                    result++
                }
            }
            return result
        }

        fun checkPhoneNumber(phoneNumber: String?): Boolean {
            if (phoneNumber == null || "" == phoneNumber) {
                return false
            }
            // [3-9]    第二位可以为3/4/5/6/7/8/9的任意一个
            // \\d{9}   加上后面的9位数字，总共为11位数字
            val regex = "^1[3-9]\\d{9}$".toRegex()
            return regex.containsMatchIn(phoneNumber)
        }

        fun checkEmail(email: String?): Boolean {
            if (email == null || "" == email) {
                return false
            }
            val regularExpression =
                "^([a-z0-9A-Z]+[-|_.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$".toRegex()
            return try {
                regularExpression.matches(email)
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun transfom(time: Int): String? {
            val hh = time / 3600
            val mm = time % 3600 / 60
            val ss = time % 3600 % 60
            return (if (hh < 10) "0$hh" else hh).toString() + "小时" + (if (mm < 10) "0$mm" else mm) + "分钟" + if (ss < 10) "0$ss" else ss
        }

    }



}
