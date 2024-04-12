package nancal.iam.service

import nbcp.comm.AsString
import nbcp.comm.ListResult
import nbcp.comm.Slice
import nbcp.db.BaseMetaData
import nbcp.db.Cn
import nbcp.db.IdName
import nancal.iam.db.mongo.mor
import org.springframework.stereotype.Component


@Component
class MongoCrudService : ICrudService {
    override fun getGroups(): ListResult<String> {
        var ret = mor.groups.map {
            var name = it::class.java.simpleName
            return@map name.Slice(0, -5)
        }

        return ListResult.of(ret)
    }

    override fun getEntities(group: String): ListResult<IdName> {
        var group = group + "Group"
        var groupObj = mor.groups.firstOrNull { it::class.java.simpleName == group }
        if (groupObj == null) return ListResult.error("找不到group")

        groupObj.getEntities()
            .map {
                var remark = it::class.java.getAnnotationsByType(Cn::class.java).firstOrNull()?.value;

                var entName = it.tableName
                return@map IdName(entName, remark.AsString(entName))
            }.apply { return ListResult.of(this) }
    }

    override fun getEntity(name: String): BaseMetaData {
        return mor.getCollection( name) !!
    }
}

