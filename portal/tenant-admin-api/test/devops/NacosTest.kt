//package nancal.iam
//
//import nancal.iam.TestBase
//import nbcp.base.mvc.*
//import nbcp.component.NacosService
//import nbcp.utils.SpringUtil
//import org.junit.jupiter.api.Test
//
//class NacosTest : TestBase() {
//    @Test
//    fun syncNacos() {
//        var nacosService = SpringUtil.getBean<NacosService>()
//        nacosService.listConfigs("192.168.5.213", "dev", "", "*.yml")
//            .data
//            .forEach { it ->
//                nacosService.setConfig(
//                    "192.168.5.211",
//                    "main",
//                    "",
//                    it.dataId.replace("-dev.yml", "-main.yml"),
//                    "",
//                    it.content
//                )
//            }
//    }
//}