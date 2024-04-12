package nancal.iam.web.sys

import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.*
import nbcp.comm.*

import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nbcp.utils.HttpUtil
import nbcp.web.UserId
import org.springframework.web.bind.annotation.PostMapping

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by udi on 17-5-23.
 */
@RestController
@RequestMapping("/sys")
class LoginInfo_Controller {

    @ApiOperation(value = "获取登录者信息")
    @PostMapping("/getMine")
    fun getMine(
            session: HttpServletRequest,
            response: HttpServletResponse
    ): ApiResult<TenantUser> {
        val usr = mor.tenant.tenantUser.query().where { it.id match session.UserId }.toEntity()
        if (usr == null) {
            response.status = 401;
            return ApiResult.error("找不到用户信息")
        }
        return ApiResult.of(usr);
    }


    data class EsMappingDataModel(
        var url: String = "",
        var name: String = "",
        var content: String = ""
    )

    @PostMapping("/es/index")
    fun createEsIndex(
        @JsonModel mapping: EsMappingDataModel,
        response: HttpServletResponse
    ): ApiResult<String> {
        //创建空的 index: curl -X PUT /{index}
        val http = HttpUtil("${mapping.url}/${mapping.name}");
        http.request.contentType = "application/json"
        http.request.requestMethod = "PUT"
        val ret = http.doNet()
        if (http.status != 200) {
            return ApiResult.error(ret.AsString(http.status.toString() + ":" + http.msg))
        }
        return ApiResult()
    }

    @PostMapping("/es/mapping")
    fun createEsMapping(
        @JsonModel mapping: EsMappingDataModel,
        response: HttpServletResponse
    ): ApiResult<String> {

        //curl -X PUT -H 'Content-Type: application/json' '/{index}/_mapping'  -d 'json'
        val http = HttpUtil("${mapping.url}/${mapping.name}/_mapping");
        http.request.contentType = "application/json"
        http.request.requestMethod = "PUT"
        http.request.postBody = mapping.content
        val ret = http.doNet()
        if (http.status != 200) {
            return ApiResult.error(ret.AsString(http.status.toString() + ":" + http.msg))
        }
        return ApiResult();
    }

    @PostMapping("/get-ans")
    fun getAns(request: HttpServletRequest): MutableMap<String, Set<String>> {
        val ret = mutableMapOf<String, Set<String>>();
        return ret
    }

}

