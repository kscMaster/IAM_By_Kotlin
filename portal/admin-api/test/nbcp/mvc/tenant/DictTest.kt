package nancal.iam.mvc.tenant

import nancal.iam.TestBase
import nancal.iam.config.BaseEnConfig
import org.junit.jupiter.api.Test
import java.util.*
import javax.annotation.Resource

/**
 *@Author shyf
 * @Date 2022/06/17
 **/
class DictTest  : TestBase() {

    @Resource
    lateinit var baseConfig: BaseEnConfig


    @Test
    fun test() {

        val keySet: MutableSet<String> = baseConfig.bundle.keySet()

        keySet.forEach {
//            println(it)
        }

        val cn = baseConfig.bundle.getString("建筑建材")
        val cn3 = baseConfig.bundle.getString("电子电工")
        val cn4 = baseConfig.bundle.getString("机械机电")
        val cn5 = baseConfig.bundle.getString("信息产业")
        baseConfig.bundle.getString("交通运输")
        baseConfig.bundle.getString("水利水电")
        baseConfig.bundle.getString("石油化工")
        baseConfig.bundle.getString("冶金矿产")
        println(cn)
        println(cn3)
        println(cn4)
        println(cn5)

        val en = baseConfig.getCn("Petrochemical industry")
        println(en)

    }
}