package nancal.iam

import nbcp.comm.*
import nbcp.comm.*
import nbcp.utils.*
import nbcp.comm.JsonMap
import nbcp.comm.StringMap
import nbcp.db.IdCodeName
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.util.LinkedMultiValueMap
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FixData : TestBase() {

    data class LineData(
            var totalLines: Int = 0,
            var codeLines: Int = 0
    )

    @Test
    fun scan_code_line() {

    }

    private fun exec(path: File, exts: List<String>, fileFilter: ((String) -> Boolean)? = null): Map<String, LineData> {
        if (path.exists() == false) return mapOf()

        var ret = linkedMapOf<String, LineData>()
        path.listFiles().filter { f ->
            var d = f.isDirectory || exts.any { e -> f.name.endsWith("." + e) }
            if (d == false) return@filter false;

            return@filter fileFilter?.invoke(f.FullName) ?: true
        }.forEach { f ->
            if (f.isFile) {
                ret.put(f.FullName, getLine(f))
            } else {
                ret.putAll(exec(f, exts, fileFilter))
            }
        }

        return ret;
    }

    private fun getLine(file: File): LineData {
        var totalLines = 0;
        var codeLines = 0;

        var sectRemark = false;
        file.readLines(const.utf8).forEach {
            totalLines++;
            if (it.length == 0) {
                return@forEach
            }

            var value = it.trim();
            if (value.length == 0) {
                return@forEach
            }

            if (sectRemark && value.endsWith("*/")) {
                sectRemark = false;
                return@forEach
            }

            if (value.startsWith("/*")) {
                sectRemark = true;
                return@forEach
            }

            if (value.startsWith("//")) {
                return@forEach
            }

            codeLines++;
        }

        return LineData(totalLines, codeLines);
    }

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun test2() {
        val params = LinkedMultiValueMap<Any, Any>()
        params.add("grant_type", "client_credentials")
        val response = restTemplate.withBasicAuth("clientId", "clientSecret")
                .postForObject("http://localhost:8130/oauth/token", params, String::class.java)
        println(response)
    }

}