package nancal.iam.mvc.tenant

import nbcp.comm.FromJson
import nbcp.comm.JsonMap
import nbcp.comm.getStringValue
import nbcp.utils.HttpUtil
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc

@ExtendWith(SpringExtension::class)
@WebAppConfiguration
@AutoConfigureMockMvc
@SpringBootTest()
@ComponentScan(basePackages = arrayOf("nancal.iam"))
@ActiveProfiles("yuxh")
internal class SysAppChildResourceControllerTest{

    @Autowired
    lateinit var mvc: MockMvc

    init {
        System.setProperty("app.upload.host", "dev8.cn:9503");
        System.setProperty("app.scheduler", "false");
    }


//    @Test
    private fun save() {

        var http = HttpUtil("http://localhost:8081/admin/sys-child-resource-info/save")
        http.request.headers.put("token", "st!5mq6sogckmps")

        var data = http.doPost(
            """
 {
    "name": "主资源-子资源",
    "action": [
        "read",
        "create",
        "edit",
        "delete"
    ],
    "dataAccessLevel": "None",
    "remark": "",
    "type": "Data",
    "resource": "",
    "appInfo": {
        "code": "IIJJ",
        "name": "testID"
    },
    "code": "00oop:ppuu"
}
"""
        ).FromJson<JsonMap>()!!.getStringValue("data");

//        Assert.hasText(data)
        print(data)
    }
}