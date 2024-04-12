package nancal.iam.web.open

//import nbcp.base.config.ActionDocBeanGather

import nbcp.comm.ApiResult
import nbcp.comm.OpenAction
import nancal.iam.db.mongo.BaseServiceEnum
import nancal.iam.model.DepItem
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by udi on 17-3-19.
 */
@RestController
@OpenAction
class Open_Dependency_Controller {


    //返回依赖信息接口
    @GetMapping("/dependency-services")
    fun getDependency(): ApiResult<DepItem> {

        val dependency = ApiResult<DepItem>()
        val depItem = DepItem()

        depItem.base = mutableListOf(BaseServiceEnum.mongo,BaseServiceEnum.redis)
        depItem.services = mutableListOf("integration","devops-pipeline-python")

        dependency.data = depItem

        return dependency
    }
}