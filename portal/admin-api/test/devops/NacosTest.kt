package nancal.iam

import com.nancal.cipher.ApiTokenUtil
import nancal.iam.TestBase
import nbcp.utils.SpringUtil
import org.junit.jupiter.api.Test

class NacosTest : TestBase() {
    @Test
    fun syncNacos() {
        println(ApiTokenUtil.encryptWithPublicSecret("",""))
    }
}