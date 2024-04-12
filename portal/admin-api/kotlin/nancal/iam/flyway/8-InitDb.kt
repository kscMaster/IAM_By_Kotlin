package nancal.iam.flyway

import nancal.iam.db.mongo.TenantAdminTypeEnum
import nancal.iam.db.mongo.entity.TenantLoginUser
import nancal.iam.db.mongo.entity.TenantUser
import nancal.iam.db.mongo.mor
import nbcp.db.FlywayVersionBaseService
import nbcp.db.mongo.*
import org.springframework.stereotype.Component

/**
 * @Author liyx
 *
 * @Description 合并租户管理员数据到租户用户表
 * @Date 2022/4/12 20:04
 */
@Component
class `8-InitDb` : FlywayVersionBaseService(8) {

    override fun exec() {
        var result = ""
        mor.tenant.tenantAdminUser.query()
            .toList(TenantUser::class.java)
            .apply {
                result += "查询到租户管理员用户信息 " + this.size + "条，"
                var execSave = 0
                this.forEach { user ->
                    user.adminType = TenantAdminTypeEnum.Super
                    user.id = ""

                    execSave += mor.tenant.tenantUser.updateWithEntity(user)
                        .whereColumn { it.loginName }
                        .withoutColumns(
                            "depts",
                            "roles",
                            "groups",
                            "allowApps",
                            "denyApps",
                            "identitySource",
                            "distinguishedName"
                        )
                        .doubleExecSave()
                }
                result += "执行保存租户管理员用户信息 " + execSave + "条，"
            }


        mor.tenant.tenantAdminLoginUser.query()
            .toList(TenantLoginUser::class.java)
            .apply {
                result += "查询到租户管理员账号 " + this.size + "条，"
                var execSave = 0

                this.forEach { loginUser ->
                    loginUser.id = ""

                    execSave += mor.tenant.tenantLoginUser.updateWithEntity(loginUser)
                        .whereColumn { it.loginName }
                        .withoutColumn { it.userId }
                        .doubleExecSave()
                }

                result += "执行保存租户管理员账号 " + execSave + "条，"
            }


        val userList = mor.tenant.tenantUser.query().toList()

        var exec = mor.tenant.tenantLoginUser.delete()
            .where { it.tenant.id match_notin userList.map { it.tenant.id } }
            .exec()

        result += "删除租户ID不存在的账号 " + exec + "条，"

        exec = mor.tenant.tenantLoginUser.delete()
            .where { it.loginName match_notin userList.map { it.loginName } }
            .exec()

        result += "删除loginname不存在的账号 " + exec + "条，"

        mor.tenant.tenantLoginUser.query()
            .toList()
            .apply {
                val map = userList.map { it.loginName to it.id }.toMap()

                var execUpdate = 0
                this.forEach { loginUser ->
                    if (map.get(loginUser.loginName) != loginUser.userId) {
                        loginUser.userId = map.get(loginUser.loginName).toString()
                        execUpdate += mor.tenant.tenantLoginUser.updateWithEntity(loginUser).execUpdate()
                    }
                }
                result += "更新账号的userid " + execUpdate + "条，"
            }

        mor.tenant.tenantUser.query()
            .where { it.adminType match_exists false }
            .toList()
            .apply {
                this.forEach { user ->
                    mor.tenant.tenantUser.updateWithEntity(user).execUpdate()
                }
            }

        println(result)
    }

}