package nancal.iam.weixinaes

/**
 * @Author wrk
 *
 * @Description
 * @Date 2022/2/25-15:50
 */
@SuppressWarnings("serial")
class AesException(code:Int) : Throwable() {
    private val code = code

    companion object{
        val OK = 0
        val ValidateSignatureError = -40001
        val ParseXmlError = -40002
        val ComputeSignatureError = -40003
        val IllegalAesKey = -40004
        val ValidateAppidError = -40005
        val EncryptAESError = -40006
        val DecryptAESError = -40007
        val IllegalBuffer = -40008
    }
    fun getMessage(code: Int): String {
        return when (code) {
            ValidateSignatureError -> "签名验证错误"
            ParseXmlError -> "xml解析失败"
            ComputeSignatureError -> "sha加密生成签名失败"
            IllegalAesKey -> "SymmetricKey非法"
            ValidateAppidError -> "appid校验失败"
            EncryptAESError -> "aes加密失败"
            DecryptAESError -> "aes解密失败"
            IllegalBuffer -> "解密后得到的buffer非法"
            else -> "" // cannot be
        }
    }

    fun getCode(): Int {
        return code
    }

}