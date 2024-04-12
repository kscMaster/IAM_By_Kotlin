package nancal.iam

import nancal.iam.db.mongo.entity.TenantResourceInfo
import nancal.iam.mvc.iam.ResourceInfoAutoController
import nbcp.db.CodeName
import nbcp.db.IdName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher.matchAll
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime
import java.util.UUID

/**
 *@Author syf
 * @Date 2022 06 07
 **/
class ResourceInfoAutoControllerTest :TestBase() {

//    var controller = ResourceInfoAutoController()

    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var controller: ResourceInfoAutoController

    var toke = "5n0vl2c5g1dt"


    @Test
    fun listResource() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .build()
        // {"pageNumber":1,"pageSize":10,"appInfoId":"qms-unq","type":"","name":"","code":""}
//        controller.listResource(
//                "", "", "", "", "", "", "", "", "", null, null, 1, 10,
//            ).apply {
//                org.springframework.util.Assert.isTrue(this.code == 200)
//            }

        mockMvc.perform(
            MockMvcRequestBuilders.post("/tenant/resource-info/listResource")
//                .content("""
//                {"pageNumber":1,"pageSize":10,"appInfoId":"qms-unq","parentCode":""}
//            """)
                .param("id","")
                .param("parentCode","")
                .param("name","")
                .param("code","")
                .param("remark","")
                .param("resource","")
                .param("action","")
                .param("action","")
                .param("type","")
                .param("appInfoId","qms-unq")
                .param("createAt","")
                .param("endAt","")
                .param("skip","1")
                .param("take","10")
                .header("token",toke)
        )
            .andExpect (matchAll(status().isOk))
            .andReturn()
    }

    @Test
    fun batchInsert() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        val entitys : MutableList<TenantResourceInfo> = mutableListOf()
        var groups : MutableList<IdName> = mutableListOf()
        var tenantResourceInfo: TenantResourceInfo = TenantResourceInfo(
            "", IdName("", ""), CodeName("C-A-0-a17-4211443", "a17"),
            "",
            false,
            "", groups, LocalDateTime.now(), LocalDateTime.now(), false
        )
        tenantResourceInfo.code = "${UUID.randomUUID().toString().substring(1,5)}:${UUID.randomUUID().toString().substring(1,5)}:${UUID.randomUUID().toString().substring(1,5)}"
        tenantResourceInfo.name = "test000001"
        var tenantResourceInfo2: TenantResourceInfo = TenantResourceInfo(
            "", IdName("", ""), CodeName("C-A-0-a17-4211443", "a17"),
            "",
            false,
            "", groups, LocalDateTime.now(), LocalDateTime.now(), false
        )
        tenantResourceInfo2.code = "${UUID.randomUUID().toString().substring(1,5)}:${UUID.randomUUID().toString().substring(1,5)}:${UUID.randomUUID().toString().substring(1,5)}"
        tenantResourceInfo2.name = "test000001"
        var tenantResourceInfo3: TenantResourceInfo = TenantResourceInfo(
            "", IdName("", ""), CodeName("C-A-0-a17-4211443", "a17"),
            "",
            false,
            "", groups, LocalDateTime.now(), LocalDateTime.now(), false
        )
        tenantResourceInfo3.code = "${UUID.randomUUID().toString().substring(1,5)}:${UUID.randomUUID().toString().substring(1,5)}:${UUID.randomUUID().toString().substring(1,5)}"
        tenantResourceInfo3.name = "test000001"
        entitys.add(tenantResourceInfo)
        entitys.add(tenantResourceInfo2)
        entitys.add(tenantResourceInfo3)
//        val jsonObject = JSONObject()
//        jsonObject.put("entitys", entitys)
//        mockMvc.perform(
//            MockMvcRequestBuilders.post("/tenant/resource-info/batchInsert")
//                .content(entitys.toString())
//                .header("token",toke)
//        )
//            .andExpect (matchAll(status().isOk))
//            .andReturn()
    }

}