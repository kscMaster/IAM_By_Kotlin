//package nancal.iam.utils
//
//import java.util.regex.Pattern
//
///**
// * @Classname UserUtils
// * @Description TODO
// * @Version 1.0.0
// * @Date 14/12/2021 上午 11:57
// * @Created by kxp
// */
//class ValidateUtils {
//
//    companion object {
//        /**
//         * 是否包含空格
//         *
//         * @param str
//         * @return
//         */
//        fun containerSpace(str: String): Boolean {
//            return if (str.indexOf(" ") != -1) {
//                true
//            } else false
//        }
//
//        /**
//         * 中文过滤
//         *
//         * @param str
//         * @return
//         */
//        fun isContainChinese(str: String?): Boolean {
//            val p = Pattern.compile("[\u4e00-\u9fa5]")
//            val m = p.matcher(str)
//            return if (m.find()) {
//                true
//            } else false
//        }
//
//        /**
//         * 获取字符串中字符类型的数量
//         *
//         * @param str
//         */
//        fun getCharKindByRegex(str: String): Int {
//            val letter1Pattern = Pattern.compile("[a-z]")
//            val letter2Pattern = Pattern.compile("[A-Z]")
//            val numberPattern = Pattern.compile("[0-9]")
//            val blandPattern = Pattern.compile("[\\s|\r|\n|\t]") //不全
//
//            val letter1Matcher = letter1Pattern.matcher(str)
//            val letter2Matcher = letter2Pattern.matcher(str)
//            val numberMatcher = numberPattern.matcher(str)
//            val blandMatcher = blandPattern.matcher(str)
//
//            val stringBufferLetter1 = StringBuffer()
//            val stringBufferLetter2 = StringBuffer()
//            val stringBufferNumber = StringBuffer()
//            val stringBufferBlank = StringBuffer()
//            while (letter1Matcher.find()) {
//                stringBufferLetter1.append(letter1Matcher.group(0))
//            }
//            while (letter2Matcher.find()) {
//                stringBufferLetter2.append(letter2Matcher.group(0))
//            }
//            while (numberMatcher.find()) {
//                stringBufferNumber.append(numberMatcher.group(0))
//            }
//            while (blandMatcher.find()) {
//                stringBufferBlank.append(blandMatcher.group(0))
//            }
//            val otherNumber =
//                str.length - stringBufferLetter1.length - stringBufferLetter2.length - stringBufferNumber.length - stringBufferBlank.length
//            println("**********")
//            println("小写字母类型：" + stringBufferLetter1.length + "个")
//            println("大写字母类型：" + stringBufferLetter2.length + "个")
//            println("数字类型：" + stringBufferNumber.length + "个")
//            println("空格类型：" + stringBufferBlank.length + "个")
//            println("其它类型：" + otherNumber + "个")
//            val list: MutableList<Int> = ArrayList<Int>(4)
//            list.add(stringBufferLetter1.length)
//            list.add(stringBufferLetter2.length)
//            list.add(stringBufferNumber.length)
//            list.add(stringBufferBlank.length)
//            list.add(otherNumber)
//            var result = 0
//            for (i in list.indices) {
//                if (list[i] as Int > 0) {
//                    result++
//                }
//            }
//            return result
//        }
//
//        fun checkPhoneNumber(phoneNumber: String?): Boolean {
//            if (phoneNumber == null || "" == phoneNumber) {
//                return false
//            }
//            // [3-9]    第二位可以为3/4/5/6/7/8/9的任意一个
//            // \\d{9}   加上后面的9位数字，总共为11位数字
//            val regex = "^1[3-9]\\d{9}$".toRegex()
//            return regex.containsMatchIn(phoneNumber)
//        }
//
//        fun checkEmail(email: String?): Boolean {
//            if (email == null || "" == email) {
//                return false
//            }
//            val regularExpression =
//                "^([a-z0-9A-Z]+[-|_.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$".toRegex()
//            return try {
//                regularExpression.matches(email)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                false
//            }
//        }
//
//        @JvmStatic
//        fun main(args: Array<String>) {
//            getCharKindByRegex("1!aA ")
//        }
//    }
//
//}
