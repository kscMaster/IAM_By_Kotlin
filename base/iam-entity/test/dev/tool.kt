package nancal.iam.dev

//import com.nancal.cipher.RSARawUtil
import nancal.iam.TestBase
import org.junit.jupiter.api.Test
import nbcp.comm.*
import nbcp.db.CodeValue
import nbcp.utils.MyUtil
import java.io.File
import java.io.FileWriter


class tool : TestBase() {

    @Test
    fun gen_mor() {
        var path = Thread.currentThread().contextClassLoader.getResource("").path.split("/target/")[0]

        MorGenerator4Java().work(
            File(path).parentFile.path + "/iam-orm/kotlin",
            "nancal.iam.db.mongo.entity.",
            "nancal.iam.db.mongo.table",
            arrayOf("nancal.iam.db.mongo.*")
        )
    }

    @Test
    fun GenEnum_Generator() {
        nbcp.tool.enumer.work("nbcp.db.mongo.")
    }


    @Test
    fun GenEntity_Sql_Entity() {
        nbcp.db.mysql.tool.MysqlEntityGenerator.db2Entity()
            .toKotlinCode()
            .forEach {
                println()
                println("/** ${it.id}.kt **/")
                println(it.name)
                println()
            }
    }
}
