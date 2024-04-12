package nancal.iam.enums

import nancal.iam.client.MPClient
import javax.annotation.Resource

/**
 *@Author shyf
 * @Date 2022/06/13
 **/
class LangResResult {

    companion object {
        @Resource
        lateinit var mpClient: MPClient
        fun getResLang(lang: String,  key: String) :String{
            try {
                return mpClient.res(lang, "mp", key)
            } catch (e: Exception) {
                return key
            }
        }

    }
}