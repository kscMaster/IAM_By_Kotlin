package nancal.iam.mvc.tenant


import nancal.iam.TestBase
import nancal.iam.db.mongo.TenantAdminTypeEnum
import nbcp.db.IdName
import nbcp.db.db
import nancal.iam.db.mongo.entity.*
import nbcp.db.mongo.match
import nancal.iam.db.mongo.mor
import nbcp.db.mongo.query
import nancal.iam.db.redis.rer
import nbcp.comm.*
import nbcp.db.LoginUserModel
import nbcp.db.redis.RedisRenewalDynamicService
import nbcp.utils.HttpUtil
import nbcp.utils.Md5Util
import org.bson.BsonWriter
import org.bson.Document
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import java.util.*

/**
 * @Classname AdminAutoControllerTest
 * @Description TODO
 * @Version 1.0.0
 * @Date 7/12/2021 上午 11:31
 * @Created by kowal
 */
internal class DFdfdfdfTest : TestBase() {

    @Test
    fun test1() {
        var appCode = "lz624-library";
        var parentCode = "a"

        mor.iam.sysResourceInfo.query()
            .where { it.appInfo.code match appCode }
            .apply {

                if (parentCode.HasValue == false) {
                    this.where { it.code match_pattern "^[^:]+\$" }
                } else {
                    this.where { it.code match_pattern "^${parentCode}:[^:]+\$" }
                }
            }
            .limit(0,9)
            .orderByDesc { it.createAt }
            .toListResult()
            .apply {
                println(this.ToJson())
            }
    }

    @Test
    fun test2() {
        var admin_tenant = Tenant();
        admin_tenant.secret = "open1111"
        admin_tenant.name = "互联网管理租户11111"
        mor.tenant.tenant.doInsert(admin_tenant)


        rer.sys.apiToken(admin_tenant.id + ":61934b2f551da57dd806f690").get()
            .apply {
                if (this == null) {
                    println("apiToken is null")
                } else {
                    println(this.ToJson())
                }
            }

        var admin_user = TenantUser();
        admin_user.loginName = "admin1111111"
        admin_user.adminType = TenantAdminTypeEnum.Super
        admin_user.tenant = IdName(admin_tenant.id, admin_tenant.name)
        mor.tenant.tenantUser.doInsert(admin_user)

        rer.sys.apiToken(admin_tenant.id + ":61934b2f551da57dd806f690").get()
            .apply {
                if (this == null) {
                    println("apiToken is null")
                } else {
                    println(this.ToJson())
                }
            }

        var admin_login_user = TenantLoginUser();
        admin_login_user.loginName = admin_user.loginName
        admin_login_user.password = Md5Util.getBase64Md5("1234")
        admin_login_user.userId = admin_user.id;
        mor.tenant.tenantLoginUser.doInsert(admin_login_user)


    }
}
