package nancal.iam

import org.junit.jupiter.api.Test
import nbcp.comm.*
import nbcp.db.BaseMetaData
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nbcp.db.mysql.tool.MysqlEntityGenerator
import nbcp.tool.UserCodeGenerator
import nbcp.utils.MyUtil
import nbcp.utils.SpringUtil
import org.junit.jupiter.api.BeforeEach
import java.io.File
import java.io.FileWriter
import java.lang.RuntimeException
import java.time.LocalDateTime


class tool : TestBase() {

    var java_mvc_path = "D:\\code\\shop\\server\\admin\\kotlin\\nbcp\\web";
    var vue_view_path = "D:\\code\\dev8-web\\admin\\src\\view"
    var ents = JsonMap(
        "system" to setOf(mor.admin.adminUser)
    )

    companion object {
        @BeforeEach
        fun before() {
            println("redis:" + SpringUtil.context.environment.getProperty("spring.data.redis.host"))
            println("mongo:" + SpringUtil.context.environment.getProperty("spring.data.mongo.uri"))
        }
    }


    @Test
    fun gen_mongo_mvc() {
        var lastSyncAt = LocalDateTime.now().minusDays(1).AsDate().AsLocalDateTime()!!;
        println(lastSyncAt.AsDate())

        mor.tenant.tenant.query()
            .where { it.isLocked match false }

            .whereOr({ it.updateAt match_gte lastSyncAt }, { it.createAt match_gte lastSyncAt })

            .toList()
            .apply {
                println(this.size)
            }


    }


    @Test
    fun gen_vue_all() {
        var lastSyncAt = LocalDateTime.now();
        lastSyncAt =  lastSyncAt.AsDate().AsLocalDateTime()
        lastSyncAt =  lastSyncAt.AsDate().AsLocalDateTime()
        lastSyncAt =  lastSyncAt.AsDate().AsLocalDateTime()
        lastSyncAt =  lastSyncAt.AsDate().AsLocalDateTime()
        lastSyncAt =  lastSyncAt.AsDate().AsLocalDateTime()
        lastSyncAt =  lastSyncAt.AsDate().AsLocalDateTime()
        lastSyncAt =  lastSyncAt.AsDate().AsLocalDateTime()
        lastSyncAt =  lastSyncAt.AsDate().AsLocalDateTime()
        lastSyncAt =  lastSyncAt.AsDate().AsLocalDateTime()
        lastSyncAt =  lastSyncAt.AsDate().AsLocalDateTime()
        lastSyncAt =  lastSyncAt.AsDate().AsLocalDateTime()
        lastSyncAt =  lastSyncAt.AsDate().AsLocalDateTime()
        lastSyncAt =  lastSyncAt.AsDate().AsLocalDateTime()
        lastSyncAt =  lastSyncAt.AsDate().AsLocalDateTime()
        lastSyncAt =  lastSyncAt.AsDate().AsLocalDateTime()
        lastSyncAt =  lastSyncAt.AsDate().AsLocalDateTime()
        println(lastSyncAt.AsDate().AsLocalDateTime().AsDate().AsLocalDateTime().AsString())
        println(lastSyncAt.AsDate().AsLocalDateTime().AsDate().AsLocalDateTime().AsDate().AsLocalDateTime().AsString())

    }

    @Test
    fun gen_vue_card() {
        ents.forEach {
            var group = it.key;
            (it.value as Collection<BaseMetaData>).forEach {
                var entity = it;
                var entity_upper = MyUtil.getBigCamelCase(it.tableName)


                run {
                    var code = UserCodeGenerator.genVueCard(group, entity);

                    write2File(
                        vue_view_path, File.separator + group + File.separator + entity_upper + "Card.vue",
                        code
                    )
                }
            }
        }
    }

    @Test
    fun gen_vue_list() {
        ents.forEach {
            var group = it.key;
            (it.value as Collection<BaseMetaData>).forEach {
                var entity = it;
                var entity_upper = MyUtil.getBigCamelCase(it.tableName)

                run {
                    var code = UserCodeGenerator.genVueList(group, entity);

                    write2File(
                        vue_view_path, File.separator + group + File.separator + entity_upper + "List.vue",
                        code
                    )
                }

            }
        }
    }

    @Test
    fun gen_vue_ref() {
        ents.forEach {
            var group = it.key;
            (it.value as Collection<BaseMetaData>).forEach {
                var entity = it;
                var entity_upper = MyUtil.getBigCamelCase(it.tableName)

                run {
                    var code = UserCodeGenerator.genVueRef(group, entity);

                    write2File(
                        vue_view_path, File.separator + group + File.separator + entity_upper + "Ref.vue",
                        code
                    )
                }
            }
        }
    }

    @Test
    fun gen_vue_router() {
        var list = mutableListOf<String>()

        ents.forEach {
            var group = it.key;


            (it.value as Collection<BaseMetaData>).forEach {
                var entity = it;
                var entity_upper = MyUtil.getBigCamelCase(entity.tableName)
                var url_name = MyUtil.getKebabCase(entity.tableName);

                if (url_name.startsWith(group + "-")) {
                    url_name = url_name.substring((group + "-").length);
                }


                list.add(
                    """
{
    path: '/${group}/${url_name}/list',
    component: resolve => require(['./view/${group}/${entity_upper}List.vue'], resolve)
},
{
    path: '/${group}/${url_name}/(add|edit)/:id',
    props:true,
    component: resolve => require(['./view/${group}/${entity_upper}Card.vue'], resolve)
},
{
    path: '/${group}/${url_name}/ref',
    component: resolve => require(['./view/${group}/${entity_upper}Ref.vue'], resolve)
},
"""
                )

            }
        }

        println("")
        println(list.joinToString(","))
    }


    private fun write2File(path: String, fileName: String, code: String) {
        var targetFile = File(path + fileName);
        if (path.isEmpty() || targetFile.exists()) {
            println("===================================================")
            println(fileName)
            println("----------------")
            println(code)
            println("===================================================")
            return;
        }

        if (targetFile.parentFile.exists() == false) {
            targetFile.parentFile.mkdirs();
        }

        var writer = FileWriter(targetFile.FullName)
        writer.write(code);
        writer.close();

        println(targetFile.FullName)
    }


    @Test
    fun mysql2Entity() {
        var list = MysqlEntityGenerator.db2Entity()
            .whereTable { it == "user_income_day" }
            .toKotlinCode();

        list.forEach {
            println(it);
        }
    }
}