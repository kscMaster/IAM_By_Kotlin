package nancal.iam.mvc.log

import cn.hutool.core.io.resource.ClassPathResource
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import nancal.iam.annotation.BizLog
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nbcp.comm.*
import nancal.iam.db.es.entity.BizLogData
import nancal.iam.db.mongo.BizLogActionEnum
import nancal.iam.db.mongo.BizLogResourceEnum
import nancal.iam.db.mongo.TenantAdminTypeEnum
import nancal.iam.util.ExcelUtil
import nancal.iam.utils.EnumMatchUtils
import nbcp.base.mvc.HttpContext
import nbcp.db.excel.ExcelComponent
import nbcp.model.DataTable
import org.apache.poi.ss.usermodel.Workbook
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.sort.SortOrder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URLEncoder
import java.time.LocalDateTime
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

@Api(description = "审计日志", tags = arrayOf("AuditLog"))
@RestController
@RequestMapping("/tenant/audit-log")
class AuditLogController {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    var encoding = "UTF-8"
    val exportCn = listOf(
        "时间", "用户", "UserID", "角色", "事件类型",
        "事件详情", "设备系统", "浏览器","客户端IP", "地点",
        "操作参数","操作结果", "原因"
    )

    val exportEn = listOf(
        "Time", "User", "UserID", "Role", "Event Type",
        "Event Details", "Equipment And System", "Browser","Clientip", "Place", "Operation Parameter","Operation Result", "Reasons"
    )

    @Value("\${spring.application.name}")
    var appName = ""

    @Resource
    lateinit var restHighLevelClient: RestHighLevelClient

    @Resource
    lateinit var enumMatchUtils: EnumMatchUtils

    fun esClientSearch( action: String,
                        resource: String,
                        ip: String,
                        name: String,
                        creatorName: String,
                        createdAtStart: LocalDateTime?,
                        createdAtEnd: LocalDateTime?) :BoolQueryBuilder{
        val query = BoolQueryBuilder()
        if (action.HasValue) {
            query.must(QueryBuilders.matchQuery("data.action", action))
        }
        if (resource.HasValue) {
            query.must(QueryBuilders.matchQuery("data.resource", resource))
        }
        if (name.HasValue) {
            query.should(QueryBuilders.matchQuery("data.ip", name))
            query.should(QueryBuilders.matchQuery("data.resource", name))
            query.should(QueryBuilders.wildcardQuery("creator.name.keyword", "*$name*"))
            query.minimumShouldMatch(1)
        }
        if (ip.HasValue) {
            query.must(QueryBuilders.matchQuery("data.ip", ip))
        }
        if (creatorName.HasValue) {
            query.must(QueryBuilders.wildcardQuery("creator.name.keyword", "*$creatorName*"))
        }
        if (createdAtStart != null && createdAtEnd != null) {
            query.must(QueryBuilders.rangeQuery("createAt").from(createdAtStart).to(createdAtEnd))
        }
        return query
    }

    @BizLog(BizLogActionEnum.Export, BizLogResourceEnum.Tenant, "管理员操作日志")
    @ApiOperation("导出")
    @GetMapping("/export")
    fun exportAdminLog(
        action: String,
        resource: String,
        ip: String,
        name: String,
        creatorName: String,
        createdAtStart: LocalDateTime?,
        createdAtEnd: LocalDateTime?,
        lang:String
    ) {
        HttpContext.request.logMsg = "管理员操作日志导出"
        val user = HttpContext.request.LoginTenantAdminUser
        val query: BoolQueryBuilder = esClientSearch(action, resource, ip, name, creatorName, createdAtStart, createdAtEnd)
        query.must(QueryBuilders.matchQuery("data.tenant.id", user.tenant.id))
        /*超级管理员看全部，其他管理员看自己*/
        if (user.adminType != TenantAdminTypeEnum.Super && user.adminType != TenantAdminTypeEnum.Auditor){
            query.must(QueryBuilders.matchQuery("creator.id", user.id))
        }
        val searchSourceBuilder = SearchSourceBuilder()
        searchSourceBuilder.query(query)
            .from(0)
            .size(5000)
            .sort("createAt", SortOrder.DESC)
        val searchRequest = SearchRequest(appName)
        searchRequest.source(searchSourceBuilder)
        val search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT)
        val list: List<BizLogData> = search.hits.hits.map { it.sourceAsMap.ConvertJson(BizLogData::class.java) }
        val exportData: Workbook?
        val encodedFileName: String
        if (lang == "en") {
            list.forEach {
                cnToEn(it)
            }
            exportData = ExcelUtil.exportData(list, exportEn)
            encodedFileName = URLEncoder.encode("TenantAdministratorLog.xlsx", "UTF8")
        } else {
            list.forEach {
                it.data.action = getRemark(it.data.action)
                it.data.remark = it.data.remark.replace("{", "[").replace("}", "]")
            }
            exportData = ExcelUtil.exportData(list, exportCn)
            encodedFileName = URLEncoder.encode("管理员操作日志.xlsx", "UTF8")
        }
        HttpContext.response.setHeader(/* name = */ "Content-Disposition", /* value = */ "attachment;filename=$encodedFileName")
        HttpContext.response.characterEncoding = encoding
        HttpContext.response.flushBuffer()
        exportData!!.write(HttpContext.response.outputStream)
    }

    @ApiOperation("管理员操作日志")
    @PostMapping("/list")
    fun findAdminLog(
        action: String,
        resource: String,
        ip: String,
        name: String,
        creatorName: String,
        createdAtStart: LocalDateTime?,
        createdAtEnd: LocalDateTime?,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<BizLogData>{
        println("管理员操作日志:appName= $appName")
        val user = request.LoginTenantAdminUser
        val query: BoolQueryBuilder = esClientSearch(action, resource, ip, name, creatorName, createdAtStart, createdAtEnd)
        query.must(QueryBuilders.matchQuery("data.tenant.id", user.tenant.id))
        /*超级管理员看全部，其他管理员看自己*/
        if (user.adminType != TenantAdminTypeEnum.Super && user.adminType != TenantAdminTypeEnum.Auditor){
            query.must(QueryBuilders.matchQuery("creator.id", user.id))
        }
        val searchSourceBuilder = SearchSourceBuilder()
        searchSourceBuilder.query(query)
            .from(skip)
            .size(take)
            .sort("createAt", SortOrder.DESC)

        val searchRequest = SearchRequest(appName)
        searchRequest.source(searchSourceBuilder)

        val search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT)
        val list = search.hits.hits.map { it.sourceAsMap.ConvertJson(BizLogData::class.java) }
        if (HttpContext.request.getHeader("lang") == "en") {
            list.forEach {
                cnToEn(it)
            }
        } else {
            list.forEach {
                it.data.action = getRemark(it.data.action)
                it.data.remark = it.data.remark.replace("{","[").replace("}","]")
                if (it.data.result == "失败") {
                    it.status = -1
                }
            }
        }
        return ListResult.of(list,search.hits.totalHits.value.toInt())
    }

    fun cnToEn(bizLogData: BizLogData): BizLogData {
        if (bizLogData.data.result == "失败") {
            bizLogData.status = -1
        }
        bizLogData.data.remark = enumMatchUtils.getMsgKey(bizLogData.data.remark)
        bizLogData.data.resource = enumMatchUtils.getMsgKey(bizLogData.data.resource).lowercase(Locale.getDefault())
        bizLogData.data.roles = enumMatchUtils.getRoleKey(bizLogData.data.roles)
        bizLogData.data.result = enumMatchUtils.getEnKey(bizLogData.data.result)
        bizLogData.msg = enumMatchUtils.getErrorKey(bizLogData.msg)
        bizLogData.data.appInfo.name =  enumMatchUtils.getEnKey(bizLogData.data.appInfo.name)
        return bizLogData
    }

    @BizLog(BizLogActionEnum.Export, BizLogResourceEnum.User, "用户行为日志")
    @ApiOperation("导出")
    @GetMapping("/user/export")
    fun exportTenantAdminLog(
        action: String,
        resource: String,
        ip: String,
        name: String,
        creatorName: String,
        createdAtStart: LocalDateTime?,
        createdAtEnd: LocalDateTime?,
        lang:String
    ) {
        HttpContext.request.logMsg = "用户行为日志导出"
        val user = HttpContext.request.LoginTenantAdminUser
        /*只有超管可以，其他管理员不可以*/
        val esIndexName = "mp-oauth-api"
        val query: BoolQueryBuilder = esClientSearch(action, resource, ip, name, creatorName, createdAtStart, createdAtEnd)
        query.must(QueryBuilders.matchQuery("data.tenant.id", user.tenant.id))
        val searchSourceBuilder = SearchSourceBuilder()
        searchSourceBuilder.query(query)
            .from(0)
            .size(5000)
            .sort("createAt", SortOrder.DESC)
        val searchRequest = SearchRequest(esIndexName)
        searchRequest.source(searchSourceBuilder)
        val search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT)
        val list: List<BizLogData> = search.hits.hits.map { it.sourceAsMap.ConvertJson(BizLogData::class.java) }

        val exportData: Workbook?
        val encodedFileName: String
        if (lang == "en") {
            list.forEach {
                cnToEn(it)
            }
            exportData = ExcelUtil.exportData(list, exportEn)
            encodedFileName = URLEncoder.encode("UserBehaviorLog.xlsx", "UTF8")
        } else {
            list.forEach {
                it.data.action = getRemark(it.data.action)
                it.data.remark = it.data.remark.replace("{", "[").replace("}", "]")
            }
            exportData = ExcelUtil.exportData(list, exportCn)
            encodedFileName = URLEncoder.encode("用户行为日志.xlsx", "UTF8")
        }
        HttpContext.response.setHeader(/* name = */ "Content-Disposition", /* value = */ "attachment;filename=$encodedFileName")
        HttpContext.response.characterEncoding = encoding
        HttpContext.response.flushBuffer()
        exportData!!.write(HttpContext.response.outputStream)
    }

    @ApiOperation("用户行为日志")
    @PostMapping("/user/loglist")
    fun findTenantAdminLog(
        action: String,
        resource: String,
        ip: String,
        name: String,
        creatorName: String,
        createdAtStart: LocalDateTime?,
        createdAtEnd: LocalDateTime?,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<BizLogData>{

        val user = request.LoginTenantAdminUser
        /*只有超管可以，其他管理员不可以*/
        if (user.adminType != TenantAdminTypeEnum.Super && user.adminType != TenantAdminTypeEnum.Auditor){
            return ListResult()
        }
        val esIndexName = "mp-oauth-api"

        val query: BoolQueryBuilder = esClientSearch(action, resource, ip, name, creatorName, createdAtStart, createdAtEnd)

        query.must(QueryBuilders.matchQuery("data.tenant.id", user.tenant.id))
        val searchSourceBuilder = SearchSourceBuilder()
        searchSourceBuilder.query(query)
            .from(skip)
            .size(take)
            .sort("createAt", SortOrder.DESC)

        val searchRequest = SearchRequest(esIndexName)
        searchRequest.source(searchSourceBuilder)

        val search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT)
        val list = search.hits.hits.map { it.sourceAsMap.ConvertJson(BizLogData::class.java) }

        if (HttpContext.request.getHeader("lang") == "en") {
            list.forEach {
                cnToEn(it)
            }
        } else {
            list.forEach {
                it.data.action = getRemark(it.data.action)
                it.data.remark = it.data.remark.replace("{","[").replace("}","]")
                if (it.data.result == "失败") {
                    it.status = -1
                }
            }
        }

        return ListResult.of(list,search.hits.totalHits.value.toInt())
    }

    fun getRemark(action : String) :String{

        if (action.HasValue) {
            var toMutableList: MutableList<BizLogActionEnum> =
                BizLogActionEnum.values().filter { it.name == action }.toMutableList()
            if (toMutableList.isNotEmpty()) {
                return toMutableList[0].remark
            }
        }
        return action
    }
}
