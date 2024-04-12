package nancal.iam

import nbcp.comm.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import org.junit.jupiter.api.Test
import java.io.File

class InitDataTest : TestBase() {
    @Test
    fun initData0() {
        var path = Thread.currentThread().contextClassLoader.getResource("").path.split("/target/")[0] + "/resources"

        path += "/flyway-v0"


        var list = listOf(
            mor.tenant.tenant.query().where { it.code match "open" },
            mor.admin.adminUser.query().where { it.loginName match "admin" },
            mor.admin.adminLoginUser.query().where { it.loginName match "admin" },
            mor.iam.industryDict.query(),
            mor.base.sysCity.query().orderByAsc { it.code }
        )

        list.forEach { query ->
            var content = query
                .toList()
                .map {
                    var map = it.ConvertJson(JsonMap::class.java);
                    map.remove("createAt")
                    map.remove("updateAt")
                    return@map map.ToJson()
                }
                .joinToString(const.line_break)
            File("${path}/${query.actualTableName}.dat").writeText(content, const.utf8)
        }
    }


    @Test
    fun initData2() {
        var path = Thread.currentThread().contextClassLoader.getResource("").path.split("/target/")[0] + "/resources"

        path += "/flyway-v2"

        var list = listOf(
            mor.tenant.tenantAdminRole.query(),
            mor.iam.sysApplication.query().where {
                it.appCode match_in arrayOf(
                    "saas",
                    "model",
                    "it-mse",
                    "code-design",
                    "ec-levault",
                    "ec-levault-admin",
                    "workflow",
                    "lzpesdpu",
                    "lzappwb",
                )
            },
        )

        list.forEach { query ->
            var content = query
                .toList()
                .map {
                    var map = it.ConvertJson(JsonMap::class.java);
                    map.remove("createAt")
                    map.remove("updateAt")
                    return@map map.ToJson()
                }
                .joinToString(const.line_break)
            File("${path}/${query.actualTableName}.dat").writeText(content, const.utf8)
        }
    }
}