package nancal.iam.flyway

import nancal.iam.db.mongo.PersonClassifiedEnum
import nancal.iam.db.mongo.entity.TenantUser
import nancal.iam.db.mongo.entity.tenant.TenantGroupDict
import nancal.iam.db.mongo.mor
import nbcp.comm.HasValue
import nbcp.db.FlywayVersionBaseService
import nbcp.db.IdCodeName
import nbcp.db.mongo.match
import nbcp.db.mongo.query
import nbcp.db.mongo.updateWithEntity
import org.springframework.stereotype.Component


@Component
class `22-InitDb` : FlywayVersionBaseService(22) {

    override fun exec() {
        initPersonClassified()
    }


    fun initPersonClassified() {
        println("flyway22 执行 =================================================")
        val toList: MutableList<TenantUser> = mor.tenant.tenantUser.query().toList(TenantUser::class.java)
        // 同步数据到字典表
        val groupBy: Map<String, List<TenantUser>> = toList.groupBy { it.tenant.id }
        groupBy.forEach{
            val tenantUser: TenantUser = it.value[0]
            val data: List<PersonClassifiedEnum> = it.value.map { it.personClassified }.toList().distinct()
            data.forEach { name->
                if (name.name != "None") {
                    mor.tenant.tenantGroupDict.query()
                        .where { it.tenant.id match tenantUser.tenant.id }
                        .where { it.code match name.name }.toEntity().apply {
                            if (this == null) {
                                val  entity = TenantGroupDict()
                                entity.code = name.name
                                entity.name = name.remark
                                entity.tenant.id = tenantUser.tenant.id
                                entity.tenant.name = tenantUser.tenant.name
                                mor.tenant.tenantGroupDict.doInsert(entity)
                            }
                        }
                }
            }

        }
        // 字典数据同步用户
        toList.forEach {tenantUser->
            if (tenantUser.personClassified.name == "None") {
                tenantUser.personnelSecret = IdCodeName()
                mor.tenant.tenantUser.updateWithEntity(tenantUser).execUpdate()
            }
            if (tenantUser.personClassified.name.HasValue && tenantUser.personClassified.name != "None") {
                val tenantDict: TenantGroupDict? = mor.tenant.tenantGroupDict.query()
                    .where { it.tenant.id match tenantUser.tenant.id }
                    .where { it.code match tenantUser.personClassified.name }.toEntity()
                if (tenantDict != null){
                    tenantUser.personnelSecret.id = tenantDict.id
                    tenantUser.personnelSecret.name = tenantDict.name
                    tenantUser.personnelSecret.code = tenantDict.code
                    mor.tenant.tenantUser.updateWithEntity(tenantUser).execUpdate()
                }
            }
        }
        println("flyway22 执行结束 =================================================")
    }
}