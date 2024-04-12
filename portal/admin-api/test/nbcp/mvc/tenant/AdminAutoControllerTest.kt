package nancal.iam.mvc.tenant


import nancal.iam.TestBase
import nancal.iam.db.mongo.AuthTypeEnum
import nbcp.db.IdName
import nbcp.db.db
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.mongo.mor
import nbcp.comm.*
import nbcp.db.mongo.*
import nbcp.utils.HttpUtil
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
internal class AdminAutoControllerTest : TestBase() {

    @Test
    fun test2() {
        var http = HttpUtil("http://192.168.5.213/api/mp-iam-admin-api/admin/tenant/save")
        http.request.headers.put("api-token", "61934b2f551da57dd806f690")
        var tenantId = http.doPost(
            """{
  "tenant": {
    "concatName": "yangtz11",
    "concatPhone": "18600560901",
    "email": "yangtz11@nancal.com",
    "enable": true,
    "name": "yangtz11_18600560901"
  },
  "tenantAdminUser": {
    "email": "yangtz11@nancal.com",
    "isUserAdminType": "Super",
    "loginName": "yangt11z_18600560901",
    "mobile": "18600560901",
    "name": "yangtz11",
    "sendPasswordType": "Mobile"
  }
}"""
        ).FromJson<JsonMap>()!!.getStringValue("data");


        var http2 = HttpUtil("http://192.168.5.213/api/mp-tenant-admin-api/tenant/sys-setting/detail/{id}")
        http2.request.headers.put("api-token", "${tenantId}:61934b2f551da57dd806f690")
        var msg = http2.doGet()
        println(msg)
    }


    @Test
    fun abc() {

        var auths = mor.tenant.tenantAppAuthResourceInfo.aggregate()
            .beginMatch()
            .where { it.type match AuthTypeEnum.Role }
            .where { it.auths.type match "Menu" }
            .endMatch()
            .addPipeLine(
                PipeLineEnum.project, JsonMap(
                    "auths" to db.mongo.filter(
                        "\$auths", "item",
                        (MongoColumnName("\$item.type") match "Menu").toExpression()
                    )
                )
            )
            .limit(0, 5)
            .toList()
            .apply {
                println(this.ToJson())
            }

    }
}
