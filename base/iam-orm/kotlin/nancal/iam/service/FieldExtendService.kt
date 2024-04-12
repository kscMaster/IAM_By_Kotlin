package nancal.iam.service

import nbcp.comm.HasValue
import nbcp.comm.ListResult
import nancal.iam.db.mongo.entity.*
import nbcp.db.mongo.match
import nancal.iam.db.mongo.mor
import nbcp.db.mongo.query
import org.springframework.stereotype.Service

@Service
class FieldExtendService {

    /**
     * 获取扩展字段数据源字典(下拉)
     */
    fun getDeportmentExtendFields(tenantId: String,dataSource:String): ListResult<TenantExtendFieldDataSourceDict> {
            mor.tenant.tenantExtendFieldDataSourceDict.query()
                .apply {

                    if (tenantId.HasValue) {
                        this.where { it.tenant.id match tenantId }
                    }

                    if (dataSource.HasValue) {
                        this.where { it.dataSource match dataSource }
                    }
                }
                .toListResult()
                .apply {
                    return this;
                }
        }

    }