package nancal.iam.web.open

import nbcp.comm.*
import nbcp.db.*
import nancal.iam.db.es.entity.AppLogIndex
import nancal.iam.db.es.entity.NginxLogIndex
import nancal.iam.db.es.esr
import nbcp.db.es.query
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nbcp.base.mvc.*
import nbcp.web.*
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest


/**
 * Created by udi on 17-5-23.
 */

@OpenAction
@RestController
@RequestMapping("")
class Open_Test_Controller {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

//    @Value("\${spring.profiles.active}")
//    var profiles: String = "";

    @RequestMapping("/hi2", method = arrayOf(RequestMethod.GET, RequestMethod.POST))
    fun hi(): ApiResult<JsonMap> {
        var ret = ApiResult<JsonMap>()
        ret.msg = "";

        var ret2 = db.mongo.dynamicEntity("sysAnnex").query()
            .where(MongoColumnName("id") match "5ffbf52a3ab4096e4c80a129")
            .toListResult();

        var ent = MongoDynamicEntity();
        ent.put("name", "kkk")
        ent.put("remark","ffff")

        db.mongo.dynamicEntity("abc").doInsert(ent)

        db.mongo.dynamicEntity("abc").update()
            .set{MongoColumnName("remark") to "f2"}
            .where(MongoColumnName("name") match "kkk")
            .exec();



        var list = db.mongo.dynamicEntity("abc").query()
            .where(MongoColumnName("name") match "kkk")
            .toList()


        db.mongo.dynamicEntity("abc").delete()
            .where(MongoColumnName("name") match "kkk")
            .exec()

        return ret;
    }

    @RequestMapping("/test/es", method = arrayOf(RequestMethod.GET, RequestMethod.POST))
    fun test_es(act: String, request: HttpServletRequest): ApiResult<AppLogIndex> {

        if (act == "add") {
            var e1 = NginxLogIndex()
            e1.visitAt = LocalDateTime.now()
            e1.url = "abc"
            e1.method = "GET"
            e1.ip = request.ClientIp
            e1.status = 200
            e1.referer = "def"
            e1.agent = request.getHeader("User-Agent")

            esr.system.nginxLogIndex.doInsert(e1)

            if (esr.affectRowCount == 0) {
                return ApiResult.error("插入失败")
            }
        } else if (act == "get") {
            var nginx = esr.system.appLogIndex.query()
                .orderByDesc { it.createAt }
                .toEntity();

            if (nginx == null) {
                return ApiResult.error("no data")
            }
            return ApiResult.of(nginx)
        }
        return ApiResult()
    }
}
