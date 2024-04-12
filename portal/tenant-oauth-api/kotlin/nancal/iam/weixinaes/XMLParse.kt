package nancal.iam.weixinaes

import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

/**
 * @Author wrk
 *
 * @Description
 * @Date 2022/2/28-16:21
 */
class XMLParse {
    companion object {


        /**
         * 提取出xml数据包中的加密消息
         * @param xmltext 待提取的xml字符串
         * @return 提取出的加密消息字符串
         * @throws AesException
         */
        @Throws(AesException::class)
        fun extract(xmltext: String): Array<Any> {
            val result:MutableList<Any> = mutableListOf()
             try {
                 val dbf = DocumentBuilderFactory.newInstance()
                 dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
                 dbf.setFeature("http://xml.org/sax/features/external-general-entities", false)
                 dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
                 dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
                 dbf.isXIncludeAware = false
                 dbf.isExpandEntityReferences = false
                 val db = dbf.newDocumentBuilder()
                 var xmltexts =
                     "<xml>    <ToUserName><![CDATA[wx1878653592e78b44]]></ToUserName>    <Encrypt><![CDATA[Tfprs8qdo7yJIBWqGAInrjc+biooUaPiGYESdgaUiWanddakOJxHMBpoQ0ebNqZeod9Qnfr89X2oF5AFYM5aKWCDnEtBt8h7WkpJps1A/8i4TQd4dwym0T3dBq3WpfeBL08/mNNepW4XNKuN1CS2rcJQhxdQG6MOYXcrECjgoLuCERAIIi9wG4N4zju9pO9OaVQywSScjd5MYkEHZ+IjQp2DyA0vAQwETrcUTzg31V/Gtm/7EAYaiz7c+VsLTNyE1HJMAKshr3pKYEMFOV+ob9d8P5+yJ025Te48Eb6R8ev5RqBG8FrTOhazFNKhsUSdfz8H0M0jYWuKCcslWrdfv8q8JIilP/V+uUmrJxSpB8Kpqr4ScLb9RVHOGF8Kunnkq2B1G75UIiUG4mRf1w4xh2SVTkKGcy6QS645QpvjRmMYrN1aB/415F49QAcf6ZwwHJIFXpBerSNJTNSoqbMszagQpZlr8WEg6IzpB8mQ5DJJS0gQkT/e85/cdB17Ma8CQ78hbGY63ItCxbHS6EaF5MK69XjVtk4UifIOO72+vkAOnJxKxCDzXsyrQXuE/J2/Rvec/trOe2dtmD3EyWd7NKPRkwF2hl+ZtWi7/Q2aBnkkcZISwU7BtgzbOry74mtV]]></Encrypt></xml>"
                 val sr = StringReader(xmltext)
                 val iss = InputSource(sr)
                 val document = db.parse(iss)

                 val root = document.documentElement
                 val nodelist1 = root.getElementsByTagName("Encrypt")
                 val nodelist2 = root.getElementsByTagName("ToUserName")
                 result.add(0)
                 result.add(nodelist1.item(0).textContent)
                 result.add(nodelist2.item(0).textContent)
                 return result.toTypedArray()
            } catch (e: Exception) {
                e.printStackTrace()
                throw AesException(AesException.ParseXmlError)
            }
        }

        /**
         * 生成xml消息
         * @param encrypt 加密后的消息密文
         * @param signature 安全签名
         * @param timestamp 时间戳
         * @param nonce 随机字符串
         * @return 生成的xml字符串
         */
        fun generate(encrypt: String?, signature: String?, timestamp: String?, nonce: String?): String? {
            val format = """
             <xml>
             <Encrypt><![CDATA[%1${"$"}s]]></Encrypt>
             <MsgSignature><![CDATA[%2${"$"}s]]></MsgSignature>
             <TimeStamp>%3${"$"}s</TimeStamp>
             <Nonce><![CDATA[%4${"$"}s]]></Nonce>
             </xml>
             """.trimIndent()
            return String.format(format, encrypt, signature, timestamp, nonce)
        }
    }
}