package nancal.iam


import nbcp.comm.ConvertListJson
import nbcp.comm.JsonMap
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.tenant.*
import nancal.iam.mvc.tenant.*
import org.junit.jupiter.api.Test

class SyncMongoTest : TestBase() {

    /**
     * @Description 三权分立管理员权限 213
     *
     * @param
     * @return
     * @date 13:38 2022/3/10
     */
    @Test
    fun insertTenantAdminRole() {
        mor.tenant.tenantAdminRole.delete().exec()
        //---安全管理员
        var businessMenus: MutableList<String> = mutableListOf()
        //用户组管理
        businessMenus.add("61ad67eef8587d4696ead1fe")
        //角色管理
        businessMenus.add("61ad6847f8587d4696ead201")
        //授权管理
        businessMenus.add("61ad6861f8587d4696ead202")

        var businessAdminRole = TenantAdminRole()
        businessAdminRole.code = TenantAdminTypeEnum.Business
        businessAdminRole.name = "安全管理员"
        businessAdminRole.menus.addAll(businessMenus)
        mor.tenant.tenantAdminRole.doInsert(businessAdminRole)


        //---系统管理员
        var userMenus: MutableList<String> = mutableListOf()
        //成员与部门
        userMenus.add("61ad67d6f8587d4696ead1fd")
        //应用管理
        userMenus.add("61ad6805f8587d4696ead1ff")
        //资源管理
        userMenus.add("61ad6825f8587d4696ead200")
        //链接身份源
        userMenus.add("62023690983988018a84c5cd")
        //企业身份源
        userMenus.add("62023ea8983988018a84c5d0")
        //社会身份源
        userMenus.add("62134135dc68880d402e3c19")
        //同步中心
        userMenus.add("62032f63983988018a84c5e1")
        //系统设置
        userMenus.add("61ad6883f8587d4696ead204")
        //基础设置
        userMenus.add("61c96750f7199258e79adb63")
        //扩展字段
        userMenus.add("61c96793f7199258e79adb64")
        var userAdminRole = TenantAdminRole()
        userAdminRole.code = TenantAdminTypeEnum.User
        userAdminRole.name = "系统管理员"
        userAdminRole.menus.addAll(userMenus)
        mor.tenant.tenantAdminRole.doInsert(userAdminRole)

        //审计员 √
        var auditorMenus: MutableList<String> = mutableListOf()
        //审计日志
        auditorMenus.add("61ad6871f8587d4696ead203")
        //管理员操作日志
        auditorMenus.add("61c3e8abe5b1c57fd4ada807")
        //用户行为日志
        auditorMenus.add("61c3e8f3e5b1c57fd4ada80a")


        var auditorAdminRole = TenantAdminRole()
        auditorAdminRole.code = TenantAdminTypeEnum.Auditor
        auditorAdminRole.name = "安全审计员"
        auditorAdminRole.menus.addAll(auditorMenus)
        mor.tenant.tenantAdminRole.doInsert(auditorAdminRole)

        //超级管理员
        var superMenus: MutableList<String> = mutableListOf()
        //系统设置
        superMenus.add("61ad6883f8587d4696ead204")
        //基础设置
        superMenus.add("61c96750f7199258e79adb63")
        //扩展字段
        superMenus.add("61c96793f7199258e79adb64")

        superMenus.addAll(businessMenus)
        superMenus.addAll(userMenus)
        superMenus.addAll(auditorMenus)

        var superAdminRole = TenantAdminRole()
        superAdminRole.code = TenantAdminTypeEnum.Super
        superAdminRole.name = "超级管理员"
        superAdminRole.menus.addAll(superMenus)
        mor.tenant.tenantAdminRole.doInsert(superAdminRole)
    }

    @Test
    fun getDbDeptUserCount() {
        var tenantId = "61e4fe161388a44eb84250b8"
        mor.tenant.tenantUser.aggregate()
            .beginMatch()
            .where { it.tenant.id match tenantId }
            .endMatch()
            .addPipeLineRawString(PipeLineEnum.unwind, "\$depts")
            .group("\$dept._id", JsonMap("userCount" to JsonMap("\$sum" to 1)))
            .toMapList()
    }

    fun getDbDeptUserCount(tenantId: String): MutableList<DepartmentInfoAutoController.IdCount> {
        mor.tenant.tenantUser.aggregate()
            .beginMatch()
            .where { it.tenant.id match tenantId }
            .endMatch()
            .addPipeLineRawString(PipeLineEnum.unwind, "'\$depts'")
            .group("\$depts._id", JsonMap("currentDepPersonCount" to JsonMap("\$sum" to 1)))
            .toMapList()
            .ConvertListJson(DepartmentInfoAutoController.IdCount::class.java)
            .toMutableList()
            .apply {
                return this
            }
    }

}
