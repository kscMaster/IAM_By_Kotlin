package nancal.iam

import nancal.iam.mvc.tenant.AppRoleAutoController
import nancal.iam.mvc.tenant.TenantUserAutoController
import nancal.iam.mvc.tenant.TenantUserGroupAutoController
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

/**
 *@Author shyf
 * @Date 2022/06/14
 * @see nancal.iam.base.config.WebSocketConfig 注释一下
 **/
class TenantUserGroupAutoControllerTest :TestBase() {

    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var controller: TenantUserGroupAutoController
    @Autowired
    private lateinit var appRoleAutoController: AppRoleAutoController
    @Autowired
    private lateinit var tenantController: TenantUserAutoController

    var toke = "5n77cye677k1"

    /**
     * 用户组批量查询
     */
    @Test
    fun bathIds() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()

        val list :MutableList<String> = mutableListOf()
        list.add("62a81882f0b3d61f57a84ba7")
        list.add("62a8187df0b3d61f57a84ba6")
        list.add("62a81878f0b3d61f57a84ba5")
        val params = mutableListOf("62a81882f0b3d61f57a84ba7", "62a8187df0b3d61f57a84ba6", "62a81878f0b3d61f57a84ba5")
        mockMvc.perform(MockMvcRequestBuilders.post("/tenant/tenant-user-group/bathIds/")
                .contentType(MediaType.APPLICATION_JSON)
                // TODO 测试Require 传参格式
//                .param("ids", "62a81882f0b3d61f57a84ba7", "62a8187df0b3d61f57a84ba6", "62a81878f0b3d61f57a84ba5")
                .header("token", toke)
        )
            .andDo { MockMvcResultHandlers.print() }
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .apply {
                this.response.characterEncoding = "UTF-8";
                println( this.response.contentAsString)
            }
    }


    /**
     * 角色批量查询
     */
    @Test
    fun usersBathIds() {
        mockMvc = MockMvcBuilders.standaloneSetup(appRoleAutoController).build()
        mockMvc.perform(MockMvcRequestBuilders.post("/tenant/app-role/bathIds/")
            .contentType(MediaType.APPLICATION_JSON)
//            .param("ids", "62a83505f0b3d61f57a84c96", "62a83500f0b3d61f57a84c95", "62a834faf0b3d61f57a84c94")
            .header("token", toke)
        )
            .andDo { MockMvcResultHandlers.print() }
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .apply {
                this.response.characterEncoding = "UTF-8";
                println( this.response.contentAsString)
            }
    }

    @Test
    fun userBathIds() {
        mockMvc = MockMvcBuilders.standaloneSetup(tenantController).build()
        mockMvc.perform(MockMvcRequestBuilders.post("/tenant/tenant-user/bathIds/")
            .contentType(MediaType.APPLICATION_JSON)
            .param("ids", "6281c7a9eb2535609ea12df9", "62a818a8f0b3d61f57a84ba8", "62a818bbf0b3d61f57a84baa"
            ,"62a818cbf0b3d61f57a84bac","62a818ddf0b3d61f57a84bae","62a818edf0b3d61f57a84bb0","62a818fef0b3d61f57a84bb2")
            .header("token", toke)
        )
            .andDo { MockMvcResultHandlers.print() }
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .apply {
                this.response.characterEncoding = "UTF-8";
                println( this.response.contentAsString)
            }
    }
}

//fun main() {
//    val list :MutableList<String> = mutableListOf()
//    list.add("62a81882f0b3d61f57a84ba7")
//    list.add("62a8187df0b3d61f57a84ba6")
//    list.add("62a81878f0b3d61f57a84ba5")
//    val toJSONString = JSON.toJSONString(list)
//    val requestBody = "{\"ids\":$toJSONString}"
//    println(requestBody)
//}