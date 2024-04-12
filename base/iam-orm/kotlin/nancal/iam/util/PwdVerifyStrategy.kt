package nancal.iam.util

import org.apache.commons.lang3.StringUtils
import java.util.*
import java.util.regex.Pattern

/**
 * @author xuebin
 * @version 1.0.0
 * @ClassName PwdVerifyStrategy.java
 * @Description PwdVerifyStrategy
 * @createTime 2021年10月15日 10:57:00
 */
object PwdVerifyStrategy {
    fun pwdVerification(pwd: String, pwdLength: Int,lowInput:Boolean,upInput:Boolean,specialInput:Boolean,numberInput:Boolean): Boolean {
        return getPwdRegular(pwdLength, pwd,lowInput,upInput,specialInput,numberInput)
    }

    fun getPwdVerificationPrompt(pwd:String,pwdLength: Int,lowInput:Boolean,upInput:Boolean,specialInput:Boolean,numberInput:Boolean): String {
        val endWith = "{$pwdLength}位"

        var prompt = ""
        if(lowInput && !isLowerCaseLetters(pwd) ){
            prompt =  "密码需要包含小写字母,长度不小于"
        }else if(upInput && !isUppercaseLetter(pwd) ){
            prompt =  "密码需要包含大写字母,长度不小于"
        }else if(specialInput && !isSpecialChar(pwd) ){
            prompt =  "密码需要包含特殊字符,长度不小于"
        }else if(numberInput && !isContainsNumbers(pwd) ){
            prompt =  "密码需要包含数字,长度不小于"
        }

//        val prompt: String = when (kind) {
//            4 -> "密码必须包含大小写字母、数字和特殊字符中的4种类型，长度不小于"
//            2 -> "密码必须包含大小写字母、数字和特殊字符中的2种类型，长度不小于"
//            1 -> "密码长度不小于"
//            else -> "密码必须包含大小写字母、数字和特殊字符中的3种类型，长度不小于"
//        }
        return prompt + endWith
    }

    private fun getPwdRegular( pwdLength: Int, pwd: String,lowInput:Boolean,upInput:Boolean,specialInput:Boolean,numberInput:Boolean): Boolean {
        var pwdLength = pwdLength
        if (StringUtils.isBlank(pwd)) {
            return java.lang.Boolean.FALSE
        }
        if (Objects.isNull(pwdLength)) {
            pwdLength = 6
        }

        if(lowInput && !isLowerCaseLetters(pwd) ){
            return false
        }
        if(upInput && !isUppercaseLetter(pwd) ){
            return false
        }
        if(specialInput && !isSpecialChar(pwd) ){
            return false
        }
        if(numberInput && !isContainsNumbers(pwd) ){
            return false
        }

        return true
//        return when (kind) {
//            4 -> {
//                (isLength(
//                    pwd,
//                    pwdLength
//                ) && isUppercaseLetter(pwd) && isLowerCaseLetters(pwd)
//                        && isContainsNumbers(pwd) && isSpecialChar(pwd))
//            }
//            2 -> {
//                isLength(pwd, pwdLength) &&
//                        (
//                                isUppercaseLetter(pwd) && isLowerCaseLetters(pwd)
//                                || isUppercaseLetter(pwd) && isContainsNumbers(pwd)
//                                || isUppercaseLetter(pwd) && isSpecialChar(pwd)
//                                || isLowerCaseLetters(pwd) && isContainsNumbers(pwd)
//                                || isLowerCaseLetters(pwd) && isSpecialChar(pwd)
//                                || isContainsNumbers(pwd) && isSpecialChar(pwd)
//                        )
//            }
//            1 -> {
//                isLength(pwd, pwdLength)
//            }
//            else -> {
//                isLength(pwd, pwdLength) &&
//                        (isUppercaseLetter(pwd) && isLowerCaseLetters(pwd) && isContainsNumbers(
//                            pwd
//                        )
//                                || isUppercaseLetter(pwd) && isLowerCaseLetters(pwd) && isSpecialChar(
//                            pwd
//                        )
//                                || isUppercaseLetter(pwd) && isSpecialChar(pwd) && isContainsNumbers(
//                            pwd
//                        )
//                                || isSpecialChar(pwd) && isLowerCaseLetters(pwd) && isContainsNumbers(
//                            pwd
//                        ))
//            }
//        }
    }

    /**
     * 判断是否含有特殊字符
     *
     * @param str 要判断的字符串
     * @return true为包含，false为不包含
     */
    private fun isSpecialChar(str: String?): Boolean {
        val regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"
        val p = Pattern.compile(regEx)
        val m = p.matcher(str)
        return m.find()
    }

    /**
     * 判断是否含有大写字母
     *
     * @param str 要判断的字符串
     * @return true为包含，false为不包含
     */
    private fun isUppercaseLetter(str: String?): Boolean {
        val regEx = "[A-Z]"
        val p = Pattern.compile(regEx)
        val m = p.matcher(str)
        return m.find()
    }

    /**
     * 判断是否含有小写字母
     *
     * @param str 要判断的字符串
     * @return true为包含，false为不包含
     */
    private fun isLowerCaseLetters(str: String?): Boolean {
        val regEx = "[a-z]"
        val p = Pattern.compile(regEx)
        val m = p.matcher(str)
        return m.find()
    }

    /**
     * 判断是否含有数字
     *
     * @param str 要判断的字符串
     * @return true为包含，false为不包含
     */
    private fun isContainsNumbers(str: String?): Boolean {
        val regEx = "[\\d]"
        val p = Pattern.compile(regEx)
        val m = p.matcher(str)
        return m.find()
    }

    /**
     * 判断密码长度
     *
     * @param str 要判断的字符串
     * @return true为包含，false为不包含
     */
    private fun isLength(str: String, length: Int): Boolean {
        return if (Objects.isNull(str) || Objects.isNull(length)) {
            java.lang.Boolean.FALSE
        } else str.trim { it <= ' ' }.length >= length
    }

//    @JvmStatic
//    fun main(args: Array<String>) {
//        val str = "QWERTY"
//        val str1 = "qwerty"
//        val str2 = "123456"
//        val str3 = "qwe123"
//        val str4 = "QWE123"
//        val str5 = "QWEqwe"
//        val str6 = "qwe!@#"
//        val str7 = "QWE!@#"
//        val str8 = "!@#123"
//        val str9 = "!@#$%^"
//        val str10 = "Qq12!@"
//        val str11 = "qq12!@"
//        val str12 = "QQ12!@"
//        val str13 = "Qqqq!@"
//        val str14 = "Qqqq12"
//        println("大写字母：" + getPwdRegular(4, 6, str))
//        println("小写字母：" + getPwdRegular(4, 6, str1))
//        println("数字：" + getPwdRegular(4, 6, str2))
//        println("小写字母+数字：" + getPwdRegular(4, 6, str3))
//        println("大写字母+数字：" + getPwdRegular(4, 6, str4))
//        println("大写字母+小写字母：" + getPwdRegular(4, 6, str5))
//        println("小写字母+特殊字符：" + getPwdRegular(4, 6, str6))
//        println("大写字母+特殊字符：" + getPwdRegular(4, 6, str7))
//        println("数字+特殊字符：" + getPwdRegular(4, 6, str8))
//        println("特殊字符：" + getPwdRegular(4, 6, str9))
//        println("大写字母+小写字母+数字+特殊字符：" + getPwdRegular(4, 6, str10))
//        println("小写字母+数字+特殊字符：" + getPwdRegular(4, 6, str11))
//        println("大写字母+数字+特殊字符：" + getPwdRegular(4, 6, str12))
//        println("大写字母+小写字母+特殊字符：" + getPwdRegular(4, 6, str13))
//        println("大写字母+小写字母+数字：" + getPwdRegular(4, 6, str14))
//    }
}