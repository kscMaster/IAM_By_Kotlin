package nancal.iam.weixinaes

import java.security.MessageDigest
import java.util.*
import kotlin.experimental.and

/**
 * @Author wrk
 *
 * @Description
 * @Date 2022/2/28-16:31
 */
class SHA1 {

    companion object {


        /**
         * 用SHA1算法生成安全签名
         * @param token 票据
         * @param timestamp 时间戳
         * @param nonce 随机字符串
         * @param encrypt 密文
         * @return 安全签名
         * @throws AesException
         */
        @Throws(AesException::class)
        fun getSHA1(token: String, timestamp: String, nonce: String, encrypt: String): String {
            try {
                val array = arrayOf(token, timestamp, nonce, encrypt)
                val sb = StringBuffer()
                // 字符串排序
                Arrays.sort(array)
                for (i in 0..3) {
                    sb.append(array[i])
                }
                val str = sb.toString()
                // SHA1签名生成
                val md = MessageDigest.getInstance("SHA-1")
                md.update(str.toByteArray())
                val digest = md.digest()
                val hexstr = StringBuffer()
                var shaHex = ""
                for (i in digest.indices) {
                    shaHex = Integer.toHexString((digest[i].toInt() and 0xFF))
                    if (shaHex.length < 2) {
                        hexstr.append(0)
                    }
                    hexstr.append(shaHex)
                }
                return hexstr.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                throw AesException(AesException.ComputeSignatureError)
            }
        }
    }
}