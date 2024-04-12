package nancal.iam.mvc.sys

import io.swagger.annotations.Api
import nbcp.bean.MongoFlywayBeanProcessor
import nbcp.comm.*
import nbcp.db.VarDatabase
import nbcp.db.db
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nbcp.utils.SpringUtil
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest


/**
 * Created by CodeGenerator at 2021-11-17 17:43:16
 */
@Api(description = "租户", tags = arrayOf("tenant"))
@RestController
@RequestMapping("/sys/tenant-db")
class TenantManageDbController {

    @Value("\${app.mongo-var-uri}")
    lateinit var mongoVarUri: String

    @Value("\${app.mongo.admin.ds.uri}")
    lateinit var adminMongo: String

    @Autowired
    lateinit var mongoTemplate: MongoTemplate;

    @PostMapping("/to-alone-db")
    fun toAloneDb(
            tenantId: String,
            request: HttpServletRequest
    ): JsonResult {

        createDb(tenantId);
//        var db =  mongoTemplate.mongoDatabaseFactory.getMongoDatabase("iam-${tenantId}");
        var tenant = mor.tenant.tenant.queryById(tenantId).toEntity().must().elseThrow { "找不到租户" }
        var tenantMongoTemplate = db.mongo.getMongoTemplateByUri(getConnectionString(tenantId))

        transformTenant(mongoTemplate, tenantMongoTemplate!!);

        var openTenentId = mor.tenant.tenant.query()
                .where { it.code match "open" }
                .toEntity()!!
                .id

        var openTenantMongoTemplate = db.mongo.getMongoTemplateByUri(getConnectionString(openTenentId))!!
        transformTenant(openTenantMongoTemplate, tenantMongoTemplate!!);

        mor.tenant.tenant.updateById(tenantId)
                .set { it.aloneDbConnection to getConnectionString(tenantId) }
                .exec();
        return JsonResult()
    }

    @PostMapping("/to-center-db")
    fun toCenterDb(
            tenantId: String,
            request: HttpServletRequest
    ): JsonResult {
        var tenant = mor.tenant.tenant.queryById(tenantId).toEntity().must().elseThrow { "找不到租户" }
        var tenantMongoTemplate = db.mongo.getMongoTemplateByUri(getConnectionString(tenantId))

        transformTenant(tenantMongoTemplate!!, mongoTemplate);

        var openTenentId = mor.tenant.tenant.query()
                .where { it.code match "open" }
                .toEntity()!!
                .id

        var openTenantMongoTemplate = db.mongo.getMongoTemplateByUri(getConnectionString(openTenentId))!!
        transformTenant(tenantMongoTemplate!!, openTenantMongoTemplate);

        mor.tenant.tenant.updateById(tenantId)
                .set { it.aloneDbConnection to "" }
                .exec();
        return JsonResult()
    }

    private fun getConnectionString(tenantId: String): String {
        return mongoVarUri.replace("@tenantId@", tenantId)
//        return """mongodb://iam:mp-iam-2021@mongo:27017/iam-@tenantId@""".replace("@tenantId@", tenantId)
    }

    private fun transformTenant(from: MongoTemplate, target: MongoTemplate) {

        mor.tenant.getEntities()
                .filter { it::class.java.getAnnotation(VarDatabase::class.java) != null }
                .map { it as MongoBaseMetaCollection<Any> }
                .forEach { metaEntity ->
                    var skip = 0;
                    var take = 50;
                    while (true) {
                        var list = usingScope(MongoTemplateScope(from)) {
                            metaEntity.query().limit(skip, take).toMapList()
                        }
                        if (list.size == 0) {
                            break;
                        }

                        skip += take;

                        usingScope(MongoTemplateScope(target)) {
                            list.forEach {
                                metaEntity.updateWithEntity(it).doubleExecSave()
                            }
                        }

                        if (list.size < 50) {
                            break;
                        }
                    }
                }
    }

    private fun createDb(tenantId: String) {
        var adminConn = db.mongo.getMongoTemplateByUri(adminMongo)!!


        usingScope(MongoTemplateScope(adminConn)) {
            mor.admin.sysMongoAdminUser.query().where { it.db match "iam-${tenantId}" }.exists()
                    .apply {
                        if (this == true) {
                            return;
                        }
                    }
        }

        var d = adminConn.mongoDatabaseFactory.getMongoDatabase("iam-${tenantId}");

        //https://docs.mongodb.com/manual/reference/command/createUser/#mongodb-dbcommand-dbcmd.createUser
        var doc = Document()
        doc["createUser"] = "iam"
        doc["pwd"] = "mp-iam-2021"
        doc["roles"] = listOf("readWrite")
        d.runCommand(doc);


        var tenantConn = db.mongo.getMongoTemplateByUri(getConnectionString(tenantId))!!
        usingScope(MongoTemplateScope(tenantConn)) {
            SpringUtil.getBean<MongoFlywayBeanProcessor>().playFlyVersion()
        }
    }
}

