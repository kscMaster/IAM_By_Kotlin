package nancal.iam.flyway

import com.mongodb.client.model.IndexOptions
import nbcp.comm.AsString
import nbcp.comm.BatchReader
import nbcp.comm.JsonMap
import nbcp.db.DbEntityIndex
import nbcp.db.FlywayVersionBaseService
import nbcp.db.IdName
import nbcp.db.db
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nbcp.utils.Md5Util
import org.bson.Document
import org.springframework.stereotype.Component
import java.io.Serializable


@Component
class `3-InitDb` : FlywayVersionBaseService(3) {

    override fun exec() {
        fixData()
    }


    fun fixData() {
        BatchReader.init { skip, take ->
            mor.tenant.tenantAppAuthResourceInfo.query().limit(skip, take).toList()
        }.forEach { ent ->
            if (ent.auths.size > 0) {
                ent.auths.forEach {
                    if (it.type != ResourceTypeEnum.Api) {
                        it.resource = it.name
                    }
                }
                mor.tenant.tenantAppAuthResourceInfo.updateById(ent.id).set { it.auths to ent.auths }.exec()
            }
        }

        BatchReader.init { skip, take ->
            mor.iam.sysApplication.query().limit(skip, take).toMapList()
        }.forEach { ent ->
            if (!ent.containsKey("enabled")) {
                ent.put("enabled", true)
                mor.iam.sysApplication.updateWithEntity(ent).execUpdate()
            }
        }

        BatchReader.init { skip, take ->
            mor.tenant.tenantLoginUser.query().limit(skip, take).toMapList()
        }.forEach { ent ->
            if (!ent.containsKey("isFirstLogin")) {
                ent.put("isFirstLogin", false)
                mor.tenant.tenantLoginUser.updateWithEntity(ent).execUpdate()
            }
        }

        BatchReader.init { skip, take ->
            mor.tenant.tenantLoginUser.query().limit(skip, take).toMapList()
        }.forEach { ent ->
            if (!ent.containsKey("isFirstLogin")) {
                ent.put("isFirstLogin", false)
                mor.tenant.tenantLoginUser.updateWithEntity(ent).execUpdate()
            }
        }

        BatchReader.init { skip, take ->
            mor.iam.sysResourceInfo.query().limit(skip, take).toMapList()
        }.forEach { ent ->
            if (!ent.containsKey("code")) {
                ent.put("code", ent.get("name").toString())
                mor.iam.sysResourceInfo.updateWithEntity(ent).execUpdate()
            }
        }

        BatchReader.init { skip, take ->
            mor.tenant.tenantResourceInfo.query().limit(skip, take).toMapList()
        }.forEach { ent ->
            if (!ent.containsKey("code")) {
                ent.put("code", ent.get("name").toString())
                mor.tenant.tenantResourceInfo.updateWithEntity(ent).execUpdate()
            }
        }


    }
}
