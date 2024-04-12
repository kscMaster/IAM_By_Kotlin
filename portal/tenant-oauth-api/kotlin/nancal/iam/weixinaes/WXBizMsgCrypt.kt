package nancal.iam.weixinaes

import org.apache.commons.codec.binary.Base64
import java.nio.charset.Charset
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and

/**
 * @Author wrk
 *
 * @Description
 * @Date 2022/2/25-17:41
 */
/**
 * @Description 微信加解密需要的构造函数
 *
 * @param token 公众平台上，开发者设置的token
 * @param encodingAesKey 公众平台上，开发者设置的EncodingAESKey
 * @param appId 公众平台appid
 * @return
 * @date 17:32 2022/2/24
 */
class WXBizMsgCrypt {
    companion object {

        var CHARSET = Charset.forName("utf-8")




        /**
         * 对密文进行解密.
         *
         * @param text 需要解密的密文
         * @return 解密得到的明文
         * @throws AesException aes解密失败
         */
//        @Throws(AesException::class)
        fun decrypt(text: String, key: String, appId: String): String {
            val aesKey=Base64.decodeBase64(key + "=")
            val original: ByteArray
            original = try {
                // 设置解密模式为AES的CBC模式
                var cipher = Cipher.getInstance("AES/CBC/NoPadding")
                var key_spec = SecretKeySpec(aesKey, "AES")
                var iv = IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16))
                cipher.init(Cipher.DECRYPT_MODE, key_spec, iv)
                // 使用BASE64对密文进行解码

                // 使用BASE64对密文进行解码
                val encrypted = Base64.decodeBase64(text)
                // 解密
                cipher.doFinal(encrypted)
            } catch (e: Exception) {
                e.printStackTrace()
                throw AesException(AesException.DecryptAESError)
            }
            val xmlContent: String
            val from_appid: String
            try {
                // 去除补位字符
                val bytes = PKCS7Encoder.decode(original)

                // 分离16位随机字符串,网络字节序和AppId
                val networkOrder = Arrays.copyOfRange(bytes, 16, 20)
                val xmlLength = recoverNetworkBytesOrder(networkOrder)
                xmlContent = String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET)
                from_appid = String(
                    Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.size),
                    CHARSET
                )
            } catch (e: Exception) {
                e.printStackTrace()
                throw AesException(AesException.IllegalBuffer)
            }

            // appid不相同的情况
            if (from_appid != appId) {
                throw AesException(AesException.ValidateAppidError)
            }
            return xmlContent
        }

        // 生成4个字节的网络字节序
        fun getNetworkBytesOrder(sourceNumber: Int): ByteArray? {
            val orderBytes = ByteArray(4)
            orderBytes[3] = (sourceNumber and 0xFF).toByte()
            orderBytes[2] = (sourceNumber shr 8 and 0xFF).toByte()
            orderBytes[1] = (sourceNumber shr 16 and 0xFF).toByte()
            orderBytes[0] = (sourceNumber shr 24 and 0xFF).toByte()
            return orderBytes
        }

        // 还原4个字节的网络字节序
        fun recoverNetworkBytesOrder(orderBytes: ByteArray): Int {
            var sourceNumber = 0
            for (i in 0..3) {
                sourceNumber = sourceNumber shl 8
                sourceNumber = sourceNumber or ((orderBytes[i].toInt() and 0xff))
            }
            return sourceNumber
        }

        /**
         * 检验消息的真实性，并且获取解密后的明文.
         *
         *  1. 利用收到的密文生成安全签名，进行签名验证
         *  1. 若验证通过，则提取xml中的加密消息
         *  1. 对消息进行解密
         *
         *
         * @param msgSignature 签名串，对应URL参数的msg_signature
         * @param timeStamp 时间戳，对应URL参数的timestamp
         * @param nonce 随机串，对应URL参数的nonce
         * @param fromXML 密文，对应POST请求的数据
         *
         * @return 解密后的原文
         * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
         */
        @Throws(AesException::class)
        fun decryptMsg(
            msgSignature: String,
            timeStamp: String,
            nonce: String,
            fromXML: String,
            key: String,
            appId: String,
            token: String,
        ): String {
            // 密钥，公众账号的app secret
            // 提取密文
            val encrypt = XMLParse.extract(fromXML)

            // 验证安全签名
            val signature: String = SHA1.getSHA1(token, timeStamp, nonce, encrypt[1].toString())

            // 和URL中的签名比较是否相等
            // System.out.println("第三方收到URL中的签名：" + msg_sign);
            // System.out.println("第三方校验签名：" + signature);
            if (signature != msgSignature) {
                throw AesException(AesException.ValidateSignatureError)
            }

            // 解密
            return decrypt(encrypt[1].toString(), key, appId)
        }
    }


}