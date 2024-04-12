package nancal.iam.mvc.admin

import io.swagger.annotations.*
import nancal.iam.annotation.BizLog
import org.springframework.web.bind.annotation.*
import nancal.iam.comm.logMsg
import nancal.iam.config.BaseEnConfig
import nbcp.comm.*
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import javax.annotation.Resource
import javax.servlet.http.*

/**
 * Created by CodeGenerator at 2021-11-17 17:33:25
 */
@Api(description = "行业字典", tags = arrayOf("IndustryDict"))
@RestController
@RequestMapping("/admin/industry-dict")
class IndustryDictAutoController {

    @Resource
    lateinit var baseEnConfig: BaseEnConfig


    @ApiOperation("列表")
    @PostMapping("/list")
    fun list(
        id: String, //当列表列新一条后，刷新时使用
        name: String,
        code: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<IndustryDict> {
        if (request.getHeader("lang") == "en") {
            val res :MutableList<IndustryDict> = mutableListOf()
            mor.iam.industryDict.query()
                .apply {
                    if (id.HasValue) {
                        this.where { it.id match id }
                    }
                    if (name.HasValue) {
                        this.where { it.name match_like name }
                    }
                    if (code.HasValue) {
                        this.where { it.code match_like code }
                    }
                }.limit(skip, take).orderByDesc { it.createAt }
                .toList().apply {
                    this.forEach {
                        it.name = baseEnConfig.bundle.getString(it.name)
                    }
                    return  ListResult.of(this)
                }
        }


        mor.iam.industryDict.query()
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (name.HasValue) {
                    this.where { it.name match_like name }
                }
                if (code.HasValue) {
                    this.where { it.code match_like code }
                }
            }.limit(skip, take).orderByDesc { it.createAt }
            .toListResult()
            .apply {
                return this;
            }
    }

    @ApiOperation("详情")
    @PostMapping("/detail/{id}")
    fun detail(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<IndustryDict> {
        mor.iam.industryDict.queryById(id)
            .toEntity()
            .apply {
                if (this == null) {
                    return ApiResult.error<IndustryDict>("找不到数据")
                }

                return ApiResult.of(this)
            }
    }

    @BizLog(BizLogActionEnum.Save, BizLogResourceEnum.IndustryDict, "行业字典")
    @ApiOperation("更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: IndustryDict,
        request: HttpServletRequest
    ): ApiResult<String> {
        if(entity.id.isEmpty())  request.logMsg="创建行业字典{${entity.name}}"
        if(entity.id.isNotEmpty())  request.logMsg="更新行业字典{${entity.name}}"
        val msg=checkParams(entity)
        if(msg.isNotEmpty()) return ApiResult.error(msg)


        mor.iam.industryDict.updateWithEntity(entity)

            .run {
                if (entity.id.HasValue) {
                    return@run this.execUpdate()
                } else {
                    return@run this.execInsert()
                }
            }
            .apply {
                if (this == 0) {
                    return ApiResult.error("更新失败")
                }

                return ApiResult.of(entity.id)
            }
    }
    @BizLog(BizLogActionEnum.Delete, BizLogResourceEnum.IndustryDict, "行业字典")
    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {

        var entity: IndustryDict? = mor.iam.industryDict.queryById(id).toEntity() ?: return JsonResult.error("找不到数据")
        request.logMsg="删除行业字典{${id}}"

        mor.iam.industryDict.deleteById(id)
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }
                //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
                return JsonResult()
            }
    }
    fun checkParams(entity:IndustryDict):String{
        if(entity.name.isEmpty()) return "行业名称不能为空"
        if(entity.name.length>32) return "行业名称不能超过32个字符"
        if(entity.code.isEmpty()) return "行业名称code不能为空"
        if(entity.code.length>32) return "行业名称code不能超过32个字符"
        if(entity.id.isNotEmpty()){
            mor.iam.industryDict.query()
                .where { it.id match_not_equal entity.id }
                .where { it.code match entity.code }
                .exists()
                .apply {
                    if(this) return "行业code已存在，请重新填写"
                }
        }else{
            mor.iam.industryDict.query()
                .where { it.code match entity.code }
                .exists()
                .apply {
                    if(this) return "行业code已存在，请重新填写"
                }
        }
        return ""

    }
}