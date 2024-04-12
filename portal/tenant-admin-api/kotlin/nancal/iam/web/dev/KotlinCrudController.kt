package nancal.iam.web.dev

import nbcp.comm.*
import nbcp.db.BaseMetaData
import nbcp.db.DatabaseEnum
import nbcp.db.IdName
import nancal.iam.db.mongo.entity.DbConnection
import nancal.iam.db.mongo.mor
import nbcp.db.mongo.queryById
import nbcp.db.mysql.tool.MysqlEntityGenerator
import nbcp.db.sql.DataSourceScope
import nancal.iam.service.*
import nbcp.base.mvc.*
import nbcp.web.*
import nbcp.base.mvc.*
import nbcp.web.*
import nbcp.tool.UserCodeGenerator
import nbcp.utils.CodeUtil
import nbcp.utils.MyUtil
import nbcp.utils.ZipUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.*
import java.io.File
import java.lang.RuntimeException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@RequestMapping("/dev")
@RestController
class KotlinCrudController {
    @Value("\${app.upload.local.path:}")
    var UPLOAD_LOCAL_PATH: String = ""

    @Autowired
    lateinit var mongoCrudService: MongoCrudService

    @Autowired
    lateinit var mysqlCrudService: MySqlCrudService


    fun getDataSource(db: DbConnection): DataSourceScope {
        var jdbcUrl =
            "jdbc:mariadb://${db.host}:${db.port}/${db.dbName}?serverTimezone=GMT%2B8&characterEncoding=const.utf8&useUnicode=true&useSSL=false"

        DataSourceBuilder
            .create()
            .type(com.zaxxer.hikari.HikariDataSource::class.java)
            .url(jdbcUrl)
            .username(db.userName)
            .password(db.password)
            .build().apply {
                return DataSourceScope(this)
            }
    }


    @PostMapping("/db-entity/tables/{id}")
    fun dbEntityList(id: String): ListResult<IdName> {
        var db = mor.dev.dbConnection.queryById(id).toEntity().must().elseThrow { "找不到数据库连接" };
        usingScope(getDataSource(db)) {
            return ListResult.of(
                MysqlEntityGenerator.db2Entity().getTablesData().map { IdName(it.name, it.comment) })
        }
    }

    @GetMapping("/db-entity/down_md/{id}")
    fun dbEntityDown_markdown(
        id: String,
        response: HttpServletResponse
    ) {
        var list = listOf<IdName>()
/*        usingScope(db.getDataSource()) {
            list = MysqlEntityGenerator
                .db2Entity(db.name)
                .toMarkdown();
        }
        list.forEach {
            if (it.id.isEmpty()) {
                it.id = "empty.md"
            } else {
                it.id = it.id + ".md"
            }
        }*/

        var file = File(UPLOAD_LOCAL_PATH + File.separator + "temp" + File.separator + CodeUtil.getCode() + ".zip");
        file.parentFile.mkdirs()

        var zipFile = ZipUtil.beginCompress(file);
        list.forEach { idName ->
            var fileName = idName.id;
            var text = idName.name
            zipFile.addFile(text.byteInputStream(const.utf8), fileName);
        }

        response.setDownloadFileName("entity-markdown.zip")
        response.outputStream.write(file.readBytes())
        file.delete()
    }

    @GetMapping("/db-entity/down/{id}")
    fun dbEntityDown(
        id: String,
        @Require style: String,
        pkgName: String,
        tables: Set<String>,
        response: HttpServletResponse
    ) {
        var db = mor.dev.dbConnection.queryById(id).toEntity().must().elseThrow { "找不到数据库连接" };
        var list = listOf<IdName>()
        usingScope(getDataSource(db)) {
            if (style == "MyOql") {
                list = getMyOqlStyleList(db.name, tables)
            } else if (style == "JPA") {
                list = getJpaStyleList(db.name, tables, pkgName)
            }
        }

        var file = File(UPLOAD_LOCAL_PATH + File.separator + "temp" + File.separator + CodeUtil.getCode() + ".zip");
        file.parentFile.mkdirs()

        var zipFile = ZipUtil.beginCompress(file);
        list.forEach { idName ->
            var fileName = idName.id;
            var text = idName.name
            zipFile.addFile(text.byteInputStream(const.utf8), fileName);
        }

        response.setDownloadFileName(style + "-entity-code.zip")
        response.outputStream.write(file.readBytes())
        file.delete()
    }

    private fun getJpaStyleList(db: String, tables: Set<String>, pkgName: String): List<IdName> {
        var list = MysqlEntityGenerator
            .db2Entity()
            .whereTable { tables.contains(it) }
            .toJpaCode(pkgName)

        list.forEach {
            if (it.id.isEmpty()) {
                it.id = "empty.java"
            } else {
                it.id = it.id + ".java"
            }
        }
        return list;
    }

    private fun getMyOqlStyleList(db: String, tables: Set<String>): List<IdName> {

        var list = MysqlEntityGenerator
            .db2Entity()
            .whereTable { tables.contains(it) }
            .toKotlinCode()


        list.forEach {
            if (it.id.isEmpty()) {
                it.id = "empty.kt"
            } else {
                it.id = it.id + ".kt"
            }
        }

        return list;
    }

    @PostMapping("/crud/groups")
    fun getGroup(dbType: DatabaseEnum?): ListResult<String> {
        if (dbType == null) {
            return ListResult();
        }

        if (dbType == DatabaseEnum.Mongo) {
            return mongoCrudService.getGroups()
        } else if (dbType == DatabaseEnum.Mysql) {
            return mysqlCrudService.getGroups()
        }

        throw RuntimeException("不识别的类型:${dbType}")
    }

    @PostMapping("/crud/entities")
    fun getEntities(dbType: DatabaseEnum?, group: String): ListResult<IdName> {
        if (dbType == null) {
            return ListResult()
        }

        if (dbType == DatabaseEnum.Mongo) {
            return mongoCrudService.getEntities(group)
        } else if (dbType == DatabaseEnum.Mysql) {
            return mysqlCrudService.getEntities(group)
        }

        throw RuntimeException("不识别的类型:${dbType}")
    }


    public enum class CrudTypeEnum {
        mvc,
        list,
        card,
        ref,
        route
    }

    @GetMapping("/crud/down")
    fun gen_crud_down(
        @Require type: String,
        @Require dbType: DatabaseEnum,
        @Require group: String,
        @Require entity: String,
        pkg: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        var server_path = "java/nancal/mvc"
        var web_path = "src/view"

        var prefix = "";
        var readme = mutableListOf<String>()
        var list: MutableList<IdName> = mutableListOf<IdName>();

        if (type == "all") {
            prefix = "all-code"

            getGroup(dbType).data.forEach { group ->
                readme.add(group + ":[")
                getEntities(dbType, group).data.forEach { entity ->
                    readme.add("\t'" + entity.id + "',")
                    list.addAll(
                        gen_crud(dbType, group, entity.id, pkg, request)
                            .data!!.values
                            .map { it as IdName }.map {
                                it.id = prefix + "/" + it.id;
                                it
                            }.toList()
                    )
                }
                readme.add("],")
            }

        } else if (type == "group") {
            if (group.isEmpty()) {
                response.parentAlert("找不到组名")
                return;
            }
            prefix = group + "-code";

            readme.add(group + ":[")
            getEntities(dbType, group).data.forEach { entity ->
                readme.add("\t'" + entity.id + "',")
                list.addAll(
                    gen_crud(dbType, group, entity.id, pkg, request)
                        .data!!.values
                        .map { it as IdName }.map {
                            it.id = prefix + "/" + it.id;
                            it
                        }.toList()
                )
            }
            readme.add("]")
        } else if (type == "entity") {
            if (group.isEmpty()) {
                response.parentAlert("找不到组名")
                return;
            }
            if (entity.isEmpty()) {
                response.parentAlert("找不到实体名")
                return;
            }
            prefix = group + "-" + entity + "-code"

            readme.add(group + ":[")
            readme.add("\t'" + entity + "',")
            readme.add("]")
            list.addAll(
                gen_crud(dbType, group, entity, pkg, request)
                    .data!!.values
                    .map { it as IdName }.map {
                        it.id = prefix + "/" + it.id;
                        it
                    }.toList()
            )
        }


        var file = File(UPLOAD_LOCAL_PATH + File.separator + "temp" + File.separator + CodeUtil.getCode() + ".zip");
        file.parentFile.mkdirs()

        var zipFile = ZipUtil.beginCompress(file);
        list.forEach { idName ->
            var fileName = idName.id;
            var text = idName.name
            zipFile.addFile(text.byteInputStream(const.utf8), fileName);
        }

        zipFile.addFile(readme.joinToString(const.line_break).byteInputStream(const.utf8), prefix + "/readme.md")

        response.setDownloadFileName(prefix + ".zip")
        response.outputStream.write(file.readBytes())
        file.delete()
    }


    @PostMapping("/crud/gen")
    fun gen_crud(
        @Require dbType: DatabaseEnum,
        @Require group: String,
        @Require entity: String,
        pkg: String,
        request: HttpServletRequest
    ): ApiResult<JsonMap> {
        var server_path = "java/nancal/mvc"
        var web_path = "src/view"

        var mongo_entity: BaseMetaData;
        if (dbType == DatabaseEnum.Mongo) {
            mongo_entity = mongoCrudService.getEntity(entity)
        } else if (dbType == DatabaseEnum.Mysql) {
            mongo_entity = mysqlCrudService.getEntity(entity)
        } else {
            throw RuntimeException("不识别的数据库类型:${dbType}")
        }

        if (mongo_entity == null) {
            return ApiResult.error("找不到${entity}实体")
        }

        var entity_upper = MyUtil.getBigCamelCase(mongo_entity.tableName)

        var jsonMap = JsonMap();
        if (dbType == DatabaseEnum.Mongo) {
            jsonMap[CrudTypeEnum.mvc.toString()] = IdName(
                "api/${server_path}/${MyUtil.getSmallCamelCase(group)}/${entity_upper}Controller.kt",
                UserCodeGenerator.genMongoMvcCrud(group, pkg, mongo_entity)
            )
        } else if (dbType == DatabaseEnum.Mysql) {
            jsonMap[CrudTypeEnum.mvc.toString()] = IdName(
                "api/${server_path}/${MyUtil.getSmallCamelCase(group)}/${entity_upper}Controller.kt",
                UserCodeGenerator.genMySqlMvcCrud(group, pkg, mongo_entity)
            )
        }
        jsonMap[CrudTypeEnum.list.toString()] = IdName(
            "web/${web_path}/${MyUtil.getKebabCase(group)}/${MyUtil.getKebabCase(entity)}/${MyUtil.getKebabCase(entity)}-list.vue",
            UserCodeGenerator.genVueList(group, mongo_entity)
        )
        jsonMap[CrudTypeEnum.card.toString()] = IdName(
            "web/${web_path}/${MyUtil.getKebabCase(group)}/${MyUtil.getKebabCase(entity)}/${MyUtil.getKebabCase(entity)}-card.vue",
            UserCodeGenerator.genVueCard(group, mongo_entity)
        )

        jsonMap[CrudTypeEnum.ref.toString()] = IdName(
            "web/${web_path}/${MyUtil.getKebabCase(group)}/${MyUtil.getKebabCase(entity)}/${MyUtil.getKebabCase(entity)}-ref.vue",
            UserCodeGenerator.genVueRef(group, mongo_entity)
        )

        return ApiResult.of(jsonMap)
    }
}