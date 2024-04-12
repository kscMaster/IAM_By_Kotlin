package nancal.iam.service

import nbcp.comm.ListResult
import nbcp.db.BaseMetaData
import nbcp.db.IdName

interface ICrudService{
    fun getGroups():ListResult<String>
    fun getEntities(group:String): ListResult<IdName>

    fun getEntity(name:String): BaseMetaData
}
