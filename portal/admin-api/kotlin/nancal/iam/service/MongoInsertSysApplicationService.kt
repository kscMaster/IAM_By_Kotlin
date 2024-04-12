package nancal.iam.service

import nancal.iam.ApiTokenService
import nbcp.comm.AsString
import nbcp.comm.ConvertJson
import nbcp.db.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nbcp.db.mongo.event.IMongoEntityInsert
import nbcp.utils.MyUtil
import nbcp.utils.SpringUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class MongoInsertSysApplicationService : IMongoEntityInsert {
    override fun beforeInsert(insert: MongoBaseInsertClip): EventResult {
        return EventResult(true)
    }

    @Autowired
    lateinit var applicationPreparedService: ApiTokenService

    override fun insert(insert: MongoBaseInsertClip, eventData: EventResult) {
        if (insert.defEntityName != mor.iam.sysApplication.tableName) {
            return;
        }

        var apiTokenService = SpringUtil.getBean<ApiTokenService>()

        insert.entities.forEach {
            var sysApplication = it.ConvertJson(SysApplication::class.java)

            apiTokenService.loadAppToRedis(sysApplication)
        }
    }

}

