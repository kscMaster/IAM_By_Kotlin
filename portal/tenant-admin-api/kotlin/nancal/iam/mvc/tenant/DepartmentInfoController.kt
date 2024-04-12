package nancal.iam.mvc.tenant

import cn.hutool.core.io.resource.ClassPathResource
import cn.hutool.core.util.IdUtil
import cn.hutool.poi.excel.ExcelUtil
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import nancal.iam.annotation.BizLog
import nancal.iam.comm.LoginTenantAdminUser
import nancal.iam.comm.logMsg
import nbcp.base.mvc.*
import nbcp.comm.*
import nbcp.db.IdName
import nbcp.db.excel.ExcelComponent
import nancal.iam.db.mongo.*
import nancal.iam.db.mongo.entity.DeptDefine
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.TenantDepartmentInfo
import nancal.iam.db.mongo.entity.TenantUser
import nancal.iam.db.mongo.entity.tenant.*
import nbcp.base.mvc.HttpContext.response
import nbcp.db.IdUrl
import nbcp.db.db
import nbcp.model.DataTable
import org.bson.Document
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.*
import javax.servlet.http.HttpServletRequest


/**
 * Created by CodeGenerator at 2021-11-17 16:47:33
 */
@Api(description = "公司部门", tags = arrayOf("DepartmentInfo"))
@RestController
@RequestMapping("/tenant/department-info")
class DepartmentInfoAutoController {
    @Value("\${deportment.maxImportSize}")
    var maxImportSize: Int = 999;
    @Value("\${openPrivatization}")
    private val openPrivatization: Boolean = true


    //部门层级限制 999
    val deportmentLevel = 999

    class DeportmentVo {
        var parentId: String = ""
        var parentName: String = ""
        var name: String = ""
        var id: String = ""
        var count: Number = 0
        var currentDepPersonCount: Number = 0
        var manager: MutableList<IdName> = mutableListOf()
        var remark: String = ""
        var level: Int = 1
        var hasChildren: Boolean = false
        var children: MutableList<DeportmentVo> = mutableListOf()
        var sort: Float = 0F

    }

    class DeportmentInfoTree(
        var children: MutableList<TenantDepartmentInfo> = mutableListOf()
    ) : TenantDepartmentInfo()

    class DeportmentInfoVo(
        var currentCount: Number = 0
    ) : TenantDepartmentInfo()

    /**
     * @Description 穿梭框组织部门
     * @date 11:51 2021/12/18
     */
    class TransferDeportmentVo {
        var parent: IdName = IdName()
        var name: String = ""
        var id: String = ""
        var level: Int = 1
        var hasChildren = true
        var count = 0
        var isSearch = false

    }


    @ApiOperation("部门平级列表")
    @PostMapping("/list")
    fun list(
        id: String,
        parentId: String,
        name: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<DeportmentInfoVo> {
        val tenantId = request.LoginTenantAdminUser.tenant.id
        mor.tenant.tenantDepartmentInfo.query()
            .apply {
                if (id.HasValue) {
                    this.where { it.id match id }
                }
                if (tenantId.HasValue) {
                    this.where { it.tenant.id match tenantId }
                }
                if (parentId.HasValue) {
                    this.where { it.parent.id match parentId }
                }
                if (name.HasValue) {
                    this.where { it.name match_like name }
                }
                this.where { it.tenant.id match request.LoginTenantAdminUser.tenant.id }
            }
            .limit(skip, take).orderByAsc { it.createAt }
            .toListResult(DeportmentInfoVo::class.java).apply {
                this.data.map { dep ->
                    dep.currentCount =
                        mor.tenant.tenantUser.query().apply {
                            this.where { it.depts.id match dep.id }
                            if(openPrivatization){
                                this.where { it.adminType match  TenantAdminTypeEnum.None}
                            }
                        }.count()
                }
            }
            .apply {
                return this
            }
    }

    class TenantInfo(
        var userCount: Int = 0,
        var currentDepPersonCount: Int = 0
    ) : IdName()

    open class DeptReturnModel(
        var tenantInfo: TenantInfo = TenantInfo(),
        var DeptModelList: MutableList<DeptModel> = mutableListOf()
    )

    open class DeptModel : IdName() {
        var parentId: String = ""
        var parentName: String = ""
        var userCount = 0
        var currentDepPersonCount = 0
        var manager: MutableList<IdName> = mutableListOf()
        var remark: String = ""
        var level: Int = 1
        var sort: Float = 0F
        var hasChildren = false
        var identitySource:ProtocolEnum? = ProtocolEnum.Self

        //前端是否展示下拉箭头 false时展示
        var leaf = true
        var children = mutableListOf<DeptModel>()


    }

    open class IdCount {
        var id = ""
        var currentDepPersonCount = 0
    }

    /**
     * @Description 获取组织树 整棵树(可能暂时未用)
     *
     * @param  name
     * @param  deportmentId
     * @return ApiResult<List<DeportmentVo>>
     * @date 11:03 2021/12/13
     */
    @ApiOperation("获取组织树 整棵树")
    @PostMapping("/getDeportmentTree")
    fun getDeportmentTrees(
        name: String,
        request: HttpServletRequest
    ): ApiResult<MutableList<DeptModel>> {
        val resList: MutableList<DeptModel> = mutableListOf()
        val tenant = request.LoginTenantAdminUser.tenant
        mor.tenant.tenant.queryById(tenant.id).toEntity()
            .must().elseThrow { "找不到租户" }
            .apply {
                tenant.name = this.name
            }
        val root = DeptModel()
        //所有部门
        var ps = queryFiterDb(tenant, name)
        val depUserCountList = getDbDeptUserCount(tenant.id)
        getSubTree(ps, root, depUserCountList, false, tenant.id, name)
        root.children.forEach {
            it.parentId = tenant.id
            it.parentName = tenant.name
        }
        if (root.children.isNotEmpty()) {
            root.hasChildren = true
            root.leaf = false
            root.children.sortBy { it.sort }
        }
        root.id = tenant.id
        root.name = tenant.name
        root.level = 0
        resList.add(root)
        return ApiResult.of(resList)
    }

    private fun getDbDeptUserCount(tenantId: String): MutableList<IdCount> {
        mor.tenant.tenantUser.aggregate()
            .beginMatch()
            .where { it.tenant.id match tenantId }
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .endMatch()
            .addPipeLineRawString(PipeLineEnum.unwind, "'\$depts'")
            .group("\$depts._id", JsonMap("currentDepPersonCount" to JsonMap("\$sum" to 1)))
            .toMapList()
            .ConvertListJson(IdCount::class.java)
            .toMutableList()
            .apply {
                return this
            }
    }


    /**
     * @Description 找子部门
     *
     * @param
     * @return
     * @date 10:05 2022/1/24
     */
    private fun getSubDepts(
        db: List<TenantDepartmentInfo>,
        deptId: String

    ): Set<TenantDepartmentInfo> {
        val subs = db.filter { it.parent.id == deptId }
        if (subs.any() == false) return setOf()
        val ret = mutableSetOf<TenantDepartmentInfo>()
        ret.addAll(subs)
        subs.forEach {
            ret.addAll(getSubDepts(db, it.id))
        }
        return ret
    }

    /**
     * @Description 找父部门
     *
     * @param
     * @return
     * @date 10:05 2022/1/24
     */
    private fun getPWbsDepts(
        db: List<TenantDepartmentInfo>,
        dept: TenantDepartmentInfo
    ): Set<TenantDepartmentInfo> {
        val parent = db.firstOrNull { it.id == dept.parent.id }
        if (parent == null) return setOf()
        return listOf(parent).union(getPWbsDepts(db, parent))
    }

    /**
     * @Description 根据父Id，拉子树
     *
     * @param  db 所有数据
     * @param  model 部门
     * @param  idCountList 当前部门人数集合
     * @param  calculate 是否计算总人数
     * @param  tenantId 租户id
     * @param  name 部门名称检索条件
     * @return
     * @date 16:05 2022/1/24
     */
    private fun getSubTree(
        db: List<TenantDepartmentInfo>,
        model: DeptModel,
        idCountList: MutableList<IdCount>,
        calculate: Boolean,
        tenantId: String,
        name: String
    ) {
        val subs = db.filter { it.parent.id == model.id }
        if (subs.isNotEmpty() && name.isEmpty()) {
            model.hasChildren = true
            model.leaf = false
        }
        subs.map { current ->
            val ret = current.ConvertJson(DeptModel::class.java)
            ret.identitySource =current.identitySource
            ret.parentId = current.parent.id
            ret.parentName = current.parent.name
            val idcount = idCountList.filter { it.id == ret.id }
            ret.currentDepPersonCount = if (idcount.isEmpty()) 0 else idcount.first().currentDepPersonCount
            if (calculate && db != null) {
                ret.userCount=current.userCount
            }
            getSubTree(db, ret, idCountList, calculate, tenantId, name)
            if (ret.children.size > 0) {
                if (name.HasValue) {
                    ret.leaf = false
                }
                ret.children.sortBy { it.sort }
            }
            model.children.add(ret)
        }

    }


    /**
     * @Description
     *
     * @param calculate  false:不计算人数   true:计算人数
     * @return
     * @date 10:44 2022/1/24
     */

    @ApiOperation("获取组织树 懒加载")
    @PostMapping("/getChildDeportment")
    fun getChildDeportment(
        name: String,
        deportmentId: String,
        calculatePersonCount: Boolean?,
        request: HttpServletRequest
    ): ApiResult<DeptReturnModel> {
        var deptReturnModel: DeptReturnModel = DeptReturnModel()
        val tenantInfo = TenantInfo()
        var calculate = false
        if (calculatePersonCount == true) {
            calculate = true
        }
        val tenant = request.LoginTenantAdminUser.tenant
        mor.tenant.tenant.queryById(tenant.id).toEntity()
            .must().elseThrow { "找不到租户" }
            .apply {
                tenant.name = this.name
            }
        tenantInfo.id = tenant.id
        tenantInfo.name = tenant.name
        deptReturnModel.tenantInfo = tenantInfo
        //查符合要求的数据
        val db = queryFiterDb(tenant, name)
        val depUserCountList = getDbDeptUserCount(tenant.id)
        if (name.HasValue) {
            if (db.isEmpty()) {
                if(calculate) {
                    deptReturnModel.tenantInfo.userCount = mor.tenant.tenantUser.query()
                        .apply {
                            if(openPrivatization){
                                this.where { it.adminType match  TenantAdminTypeEnum.None}
                            }
                        }
                        .where { it.tenant.id match tenant.id }
                        .count()
                    deptReturnModel.tenantInfo.currentDepPersonCount = mor.tenant.tenantUser.query()
                        .apply {
                            if(openPrivatization){
                                this.where { it.adminType match  TenantAdminTypeEnum.None}
                            }
                        }
                        .where { it.tenant.id match tenant.id }
                        .where { it.depts.id match_exists false }
                        .count()
                }
                return ApiResult.of(deptReturnModel)

            }
            //name有值返回整棵树 hasChildren应该为false 因为不是懒加载 而是返回的整颗模糊查询的树
            val root = DeptModel()
            //组装树
            getSubTree(db, root, depUserCountList, calculate, tenant.id, name)
            if (calculate) {
                deptReturnModel.tenantInfo.userCount = mor.tenant.tenantUser.query()
                    .apply {
                        if(openPrivatization){
                            this.where { it.adminType match  TenantAdminTypeEnum.None}
                        }
                    }
                    .where { it.tenant.id match tenant.id }
                    .count()

                deptReturnModel.tenantInfo.currentDepPersonCount = mor.tenant.tenantUser.query()
                    .apply {
                        if(openPrivatization){
                            this.where { it.adminType match  TenantAdminTypeEnum.None}
                        }
                    }
                    .where { it.tenant.id match tenant.id }
                    .where { it.depts.id match_exists false }
                    .count()
            }
            if (root.children.isNotEmpty()) {
                root.children.sortBy { it.sort }
            }
            deptReturnModel.DeptModelList = root.children
            return ApiResult.of(deptReturnModel)
        }
        //name没有值返回逐层加载
        if (calculate) {
            deptReturnModel.tenantInfo.userCount = mor.tenant.tenantUser.query()
                .apply {
                    if(openPrivatization){
                        this.where { it.adminType match  TenantAdminTypeEnum.None}
                    }
                }
                .where { it.tenant.id match tenant.id }
                .count()
            deptReturnModel.tenantInfo.currentDepPersonCount = mor.tenant.tenantUser.query()
                .apply {
                    if(openPrivatization){
                        this.where { it.adminType match  TenantAdminTypeEnum.None}
                    }
                }
                .where { it.tenant.id match tenant.id }
                .where { it.depts.id match_exists false }
                .count()
        }

        deptReturnModel.DeptModelList = getDeportmentById(db, tenant, deportmentId, depUserCountList, calculate)
        return ApiResult.of(deptReturnModel)
    }

    /**
     * @Description 查符合要求的数据
     *
     * @param
     * @return
     * @date 10:58 2022/1/24
     */
    fun queryFiterDb(tenant: IdName, name: String): MutableList<TenantDepartmentInfo> {
        //所有部门
        val db = mor.tenant.tenantDepartmentInfo.query()
            .where { it.tenant.id match tenant.id }
            .orderByAsc { it.sort }
            .toList()
        var ps = mutableListOf<TenantDepartmentInfo>()
        if (name.HasValue) {
            val list = db.filter { it.name.contains(name) }
            list.forEach {
                ps.addAll(getPWbsDepts(db, it))
                ps.addAll(getSubDepts(db, it.id))
            }
            ps.addAll(list)
        } else {
            ps = db
        }
        return ps.distinctBy { it.id }.toList().toMutableList()
    }

    /**
     * @Description 返回下一级部门
     *
     * @param
     * @return
     * @date 10:56 2022/1/24
     */
    fun getDeportmentById(
        db: List<TenantDepartmentInfo>,
        tenant: IdName,
        deportmentId: String,
        depUserCountList: MutableList<IdCount>,
        calculate: Boolean
    ): MutableList<DeptModel> {
        //下一级别所有部门（平级）
        val resList: MutableList<DeptModel> = mutableListOf()
        var depId = deportmentId
        val children = db.filter { it.parent.id == depId }.toMutableList()
        children.forEach { dep ->
            val model = dep.ConvertJson(DeptModel::class.java)
            val idCount = depUserCountList.filter { it.id == model.id }
            model.currentDepPersonCount = if (idCount.isEmpty()) 0 else idCount.first().currentDepPersonCount
            model.identitySource=dep.identitySource
            if (calculate) {
                model.userCount =dep.userCount
            }

            if (dep.id.isEmpty()) {
                model.parentId = tenant.id
                model.parentName = tenant.name
            } else {
                model.parentId = dep.parent.id
                model.parentName = dep.parent.name
            }
            val childChild = db.filter { it.parent.id == model.id }.toList()
            if (childChild.isNotEmpty()) {
                model.hasChildren = true
                model.leaf = false
            }
            resList.add(model)
        }
        return resList

    }


    class DeportmentIdVo {
        var id: String = ""
        var parent: IdName = IdName()
    }


    /**
     * @Description 新增/更新
     *
     * @param  entity Document
     * @return ApiResult<String>
     * @date 11:03 2021/12/13
     */
    @BizLog(BizLogActionEnum.Save,BizLogResourceEnum.Dept,"部门")
    @ApiOperation("新增/更新")
    @PostMapping("/save")
    fun save(
        @JsonModel entity: Document,
        request: HttpServletRequest
    ): ApiResult<String> {

        val loginUser = request.LoginTenantAdminUser
        entity.put("tenant", loginUser.tenant)
        val par = entity.get("parent")
        if (par == null || par.equals("")) {
            return ApiResult.error("父部门参数有误")
        } else {
            val idname = par.ConvertJson(IdName::class.java)
            if (idname.id.isEmpty()) {
                entity.put("parent", IdName("", ""))
            }
        }
        val msg=checkSave(entity)
        if(msg.isNotEmpty()) return ApiResult.error(msg)
        val ent = entity.ConvertJson(TenantDepartmentInfo::class.java)
        if(ent.id.isEmpty()) request.logMsg="创建部门{${ent.name}}"
        if(ent.id.isNotEmpty()) request.logMsg="修改部门{${ent.name}}"
        ent.tenant.id = loginUser.tenant.id
        ent.tenant.name = loginUser.tenant.name
        //查父亲的层级  父部门id是租户id
        var depFather = TenantDepartmentInfo()
        depFather.level = 0
        val parent = ent.parent

        if(ent.parent.id.HasValue && ent.parent.name.HasValue){
            var hasTheFather  = mor.tenant.tenantDepartmentInfo.query()
                .where { it.tenant.id match loginUser.tenant.id }
                .where { it.id match parent.id }
                .exists()
            if (!hasTheFather ) {
                return ApiResult.error("父部门不存在")
            }
        }else {
            return ApiResult.error("父部门参数有误")
        }


        if (ent.parent.id.isNotEmpty() ) {
            depFather = mor.tenant.tenantDepartmentInfo.queryById(ent.parent.id).toEntity()!!
            if (depFather.level >= deportmentLevel) {
                throw  RuntimeException("部门层级必须小于等于{${deportmentLevel}}层")
            }
        }

        //自己的层级

        if (ent.id.HasValue) {
            //编辑
            val depLevel = mor.tenant.tenantDepartmentInfo.queryById(ent.id).toEntity()?.level
            checkEditParameters(ent)
            if (ent.parent.id.equals(ent.id)) {
                throw RuntimeException("自己不能是自己的上级")

            }


            //限制层级
            //当前租户下所有部门
            val list = mor.tenant.tenantDepartmentInfo.query()
                .apply {
                    this.where { it.tenant.id match loginUser.tenant.id }
                }
                .toList(DeportmentInfoTree::class.java)
            val reslist = list
            val idArr: MutableList<String> = mutableListOf()
            //查子部门ids
            val s = DepService()
            s.getChildrenIds(reslist, idArr, ent.id)
            if (idArr.contains(ent.parent.id)) {
                throw RuntimeException("上下级不能循环")
            }
            //查到所有子部门
            val allList = mor.tenant.tenantDepartmentInfo.query().apply {
                this.where { it.id match_in idArr }
            }.toList(DeportmentInfoTree::class.java)

            //类转换
            val deplist: MutableList<DeportmentVo> = mutableListOf()
            allList.forEach { dep ->
                val vo = DeportmentVo()
                vo.parentName = dep.parent.name
                vo.id = dep.id
                vo.parentId = dep.parent.id
                vo.name = dep.name
                vo.remark = dep.remark
                vo.level = dep.level
                deplist.add(vo)
            }
            buildDeportmentVoTreeForCheck(deplist, ent.id, depFather.level + 1)
            entity.put("level", depFather.level + 1)
            if (loginUser.tenant.id.equals(ent.parent.id) || ent.parent.id.isEmpty()) {
                ent.parent.id = ""
                ent.parent.name = ""
                entity.put("parent", ent.parent)
            } else {
                ent.parent.name =
                    mor.tenant.tenantDepartmentInfo.query().where { it.id match ent.parent.id }
                        .toEntity()?.name.toString()
                entity.put("parent", ent.parent)
            }
            val manager = entity.get("manager")
            if (manager != null) {
                val idname = manager.ConvertJson(mutableListOf<IdName>() ::class.java)
                if (idname.isEmpty()) {
                    entity.put("manager", mutableListOf<IdName>())
                }
            }
            mor.tenant.tenantDepartmentInfo.updateWithEntity(entity)
                .run {
                    return@run execUpdate()
                }

            mor.tenant.tenantAppAuthResourceInfo.update()
                .where { it.type match AuthTypeEnum.Dept }
                .where { it.target.id match  ent.id }
                .set { it.target.name to ent.name  }
                .exec()

            //修改子部门的层级    4---->9   value=-(4-(9+1))    6--->3   value=-(6-(3+1))  depLevel--->depFather.level   value=-(depLevel-(depFather.level + 1))
            val value = depFather.level - depLevel!! + 1
            idArr.forEach { id ->
                val de = mor.tenant.tenantDepartmentInfo.queryById(id).toEntity()
                if (de != null) {
                    mor.tenant.tenantDepartmentInfo.update()
                        .where { it.id match id }
                        .set { it.level to (de.level + value) }
                        .exec()
                }

            }
            return ApiResult()
        } else {
            //插入
            checkInsertParameters(ent)
            entity.put("level", depFather.level + 1)
            //传过来的上级id是租户的id 则新增一级部门
            if (ent.parent.id.isEmpty() || loginUser.tenant.id.equals(ent.parent.id)) {
                //新建一级部门
                ent.parent.id = ""
                ent.parent.name = ""
                entity.put("parent", IdName(ent.parent.id, ent.parent.name))

            } else {
                //是否存在父级部门
                val depFather: TenantDepartmentInfo =
                    mor.tenant.tenantDepartmentInfo.query().where { it.id match ent.parent.id }.toEntity()
                        ?: throw  RuntimeException("父部门不存在")
                ent.parent.name = depFather.name
                entity.put("parent", IdName(ent.parent.id, ent.parent.name))

            }
            val manager = entity.get("manager")
            if (manager != null) {
                val idname = manager.ConvertJson(mutableListOf<IdName>() ::class.java)
                if (idname.isEmpty()) {
                    entity.put("manager", mutableListOf<IdName>())
                }
            }
            mor.tenant.tenantDepartmentInfo.updateWithEntity(entity)
                .run {
                    return@run execInsert()
                }
        }
        val resEnt = entity.getObjectId("_id").toString()
        val res: ApiResult<String> = ApiResult()
        res.data = resEnt
        return res
    }


    /**
     * @Description 删除
     *
     * @param  id 部门id
     * @return
     * @date 11:04 2021/12/13
     */
    @BizLog(BizLogActionEnum.Delete,BizLogResourceEnum.Dept,"部门")
    @ApiOperation("删除单个部门")
    @PostMapping("/delete/{id}")
    fun delete(
        @Require id: String,
        request: HttpServletRequest
    ): JsonResult {
        val tenant = request.LoginTenantAdminUser.tenant
        val entity = mor.tenant.tenantDepartmentInfo.queryById(id).toEntity()
        if (entity == null) {
            return JsonResult.error("找不到数据")
        }
        request.logMsg="删除部门{${entity.name}}"

        mor.tenant.tenantDepartmentInfo.query()
            .where { it.tenant.name match entity.name }
            .orderByAsc { it.createAt }
            .toList()
            .apply {
                if(this.size>0 ){
                    if(this.get(0).id.equals(id)){
                        return JsonResult.error("根部门不允许删除")
                    }
                }
            }

        //部门下有部门不能删除
        val nextGradeDeps: List<TenantDepartmentInfo> = mor.tenant.tenantDepartmentInfo.query()
            .apply {
                this.where { it.parent.id match id }
            }
            .toList()
        if (nextGradeDeps.isNotEmpty()) {
            return JsonResult.error("该部门下存在子部门，无法删除")
        }
        //部门下有用户不能删除
        val exists: Boolean = mor.tenant.tenantUser.query()
            .where { it.depts.id match id }
            .exists()
        if (exists) {
            return JsonResult.error("该部门下存在用户，无法删除")
        }
        mor.tenant.tenantDepartmentInfo.deleteById(id)
            .where { it.tenant.id match tenant.id }
            .exec()
            .apply {
                if (this == 0) {
                    return JsonResult.error("删除失败")
                }

                mor.tenant.tenantAppAuthResourceInfo.query().where { it.type match AuthTypeEnum.Dept }
                    .where { it.target.id match id }.toList().apply {
                        this.forEach {
                            mor.tenant.tenantAppAuthResourceInfo.deleteById(it.id).exec()
                        }
                    }

                //实体上配置垃圾箱功能，可物理删除，会自动移到垃圾箱。
                return JsonResult()
            }
    }

    /**
     * @Description 获取组织树（其他项目组调用）
     *
     * @param  name 部门名称
     * @param  deportmentId 根据部门id获取当前部门以及子部门集合数据
     * @return ApiResult<List<DeportmentVo>>
     * @date 11:05 2021/12/13
     */
    @ApiOperation("组织树对外")
    @PostMapping("/deportmentTree")
    fun depTree(
        name: String,
        deportmentId: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ApiResult<List<DeportmentVo>> {
        val res: ApiResult<List<DeportmentVo>> = ApiResult()
        val tenant = IdName()
        tenant.id = request.LoginTenantAdminUser.tenant.id
        mor.tenant.tenant.queryById(tenant.id).toEntity()?.apply {
            tenant.name = this.name
        }
        if (name.HasValue) {
            val allList = mor.tenant.tenantDepartmentInfo.query()
                .apply {
                    this.where { it.tenant.id match tenant.id }
                }
                .toList(DeportmentInfoTree::class.java)
            val personCountDepList = mor.tenant.tenantDepartmentInfo.query().apply {
                this.where { it.tenant.id match tenant.id }
                this.where { it.name match_like name }
                if (deportmentId.HasValue) {
                    this.where { it.parent.id match deportmentId }
                }
            }.limit(skip, take)
                .toList()
            // 根据name查询 返回列表
                val returnRes: MutableList<DeportmentVo> = mutableListOf()
                personCountDepList.forEach {
                    val vo = DeportmentVo()
                    vo.id = it.id
                    vo.parentId = it.parent.id
                    vo.name = it.name
                    vo.manager = it.manager
                    vo.parentName = it.parent.name
                    //查本级部门以及子集部门所有总人数
                    vo.count = it.userCount
                    //查当前部门人数
                    vo.currentDepPersonCount=getCurrentDepPersonCount(it.id)
                    returnRes.add(vo)
                }
                res.data = returnRes
                res.code = 0
                return res

        }

        if ((deportmentId.HasValue && tenant.id.equals(deportmentId)) || deportmentId.isEmpty()) {
            val rootDep = DeportmentVo()
            rootDep.id = tenant.id
            rootDep.name = tenant.name
            //从跟部门查
            //一级部门List
            val rootList = mor.tenant.tenantDepartmentInfo.query().apply {
                this.where { it.tenant.id match tenant.id }
                this.where { it.parent.id match "" }
            }.toList(DeportmentInfoTree::class.java)
            rootList.forEach {
                it.parent.id = rootDep.id
                it.parent.name = rootDep.name
            }
            //除一级部门外所有list
            val allList = mor.tenant.tenantDepartmentInfo.query()
                .apply {
                    this.where { it.tenant.id match tenant.id }
                    this.where { it.parent.id match_not_equal "" }
                }
                .toList(DeportmentInfoTree::class.java)
            allList.addAll(rootList)
            val deplist: MutableList<DeportmentVo> = mutableListOf()
            allList.forEach { dep ->
                val vo = DeportmentVo()
                vo.parentName = dep.parent.name
                vo.id = dep.id
                vo.parentId = dep.parent.id
                vo.name = dep.name
                vo.level = dep.level
                //查本级部门与子部门ids
                val idss: MutableList<String> = mutableListOf()
                val s = DepService()
                s.getChildrenIds(allList, idss, vo.id)
                vo.manager = dep.manager
                vo.count = dep.userCount
                vo.currentDepPersonCount = getCurrentDepPersonCount(vo.id)
                deplist.add(vo)
            }
            val returnList: MutableList<DeportmentVo> = mutableListOf()
            rootDep.children = buildDeportmentVoTree(deplist, rootDep.id).toMutableList()
            rootDep.count = mor.tenant.tenantUser.query().apply {
                this.where { it.tenant.id match tenant.id }
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }.count()
            rootDep.level = 0
            rootDep.currentDepPersonCount = mor.tenant.tenantUser.query()
                .apply {
                    this.where { it.tenant.id match tenant.id }
                    this.where { it.depts.id match_exists false }
                    if(openPrivatization){
                        this.where { it.adminType match  TenantAdminTypeEnum.None}
                    }
                }.count()
            returnList.add(rootDep)
            res.code = 0
            res.data = returnList
            return res
        } else {
            val theDep: TenantDepartmentInfo =
                mor.tenant.tenantDepartmentInfo.query().where { it.id match deportmentId }.toEntity()
                    ?: throw RuntimeException("该部门不存在")

            //所有部门list
            val allList = mor.tenant.tenantDepartmentInfo.query()
                .apply {
                    this.where { it.tenant.id match tenant.id }
                }
                .toList(DeportmentInfoTree::class.java)
            val ids: MutableList<String> = mutableListOf()
            val s = DepService()
            s.getChildrenIds(allList, ids, deportmentId)
            ids.add(deportmentId)
            val finalList = mor.tenant.tenantDepartmentInfo.query().apply {
                this.where { it.tenant.id match tenant.id }
                this.where { it.id match_in ids }
            }.toList(DeportmentInfoTree::class.java)


            val deplist: MutableList<DeportmentVo> = mutableListOf()
            finalList.forEach { dep ->
                val vo = DeportmentVo()
                vo.id = dep.id
                vo.parentId = dep.parent.id
                if (dep.level == 1) {
                    vo.parentName = tenant.name
                } else {
                    if (dep.parent.name.HasValue) {
                        vo.parentName = dep.parent.name
                    } else {
                        val father = mor.tenant.tenantDepartmentInfo.query().apply {
                            this.where { it.tenant match tenant.id }
                            this.where { it.id match vo.parentId }
                        }.toEntity()
                        if (father != null) {
                            vo.parentName = father.name
                        }
                    }
                }

                vo.name = dep.name
                vo.level = dep.level
                //查本级部门与子部门ids
                val idss: MutableList<String> = mutableListOf()
                val s = DepService()
                s.getChildrenIds(allList, idss, vo.id)
                vo.manager = dep.manager
                vo.count = dep.userCount
                vo.currentDepPersonCount = getCurrentDepPersonCount(vo.id)
                deplist.add(vo)
            }
            val returnList: MutableList<DeportmentVo> = buildDeportmentVoTree(deplist, theDep.parent.id).toMutableList()
            returnList.forEach {
                if (it.level == 1) {
                    it.parentId = tenant.id
                }
            }
            res.code = 0
            res.data = returnList
            return res
        }
    }

    /**
     * @Description 部门在同级上下移置顶
     *
     * @param  moveId 要和谁对换
     * @param  id 当前部门id
     * @param  moveDirection 上移:Up 下移:Down 置顶:Top
     * @return ApiResult<String>
     * @date 2021/12/16
     */
    @BizLog(BizLogActionEnum.UpOrDownLocation,BizLogResourceEnum.Dept,"部门")
    @ApiOperation("部门在同级上下移置顶")
    @PostMapping("/move")
    fun move(
        moveId: String,
        @Require id: String,
        @Require moveDirection: String,
        request: HttpServletRequest
    ): ApiResult<String> {
        request.logMsg="移动部门"
        if(moveId.isEmpty()){
            if(moveDirection==DeportmentMoveType.Up.name){
                throw  RuntimeException("当前部门已经是最顶层")
            }
            if(moveDirection==DeportmentMoveType.Down.name){
                throw  RuntimeException("当前部门已经是最底层")
            }
            if(moveDirection==DeportmentMoveType.Top.name){
                throw  RuntimeException("当前部门已经是最顶层")
            }
        }
        val tenantId = request.LoginTenantAdminUser.tenant.id
        val dep = mor.tenant.tenantDepartmentInfo.queryById(id)
            .where { it.tenant.id match tenantId }
            .toEntity() ?: throw RuntimeException("部门已不存在")
        val moveDep = mor.tenant.tenantDepartmentInfo.queryById(moveId)
            .where { it.tenant.id match tenantId }
            .toEntity() ?: throw RuntimeException("部门已不存在")
        when (moveDirection) {
            DeportmentMoveType.Up.name -> {
                request.logMsg="上移部门{${dep.name}}"
            }
            DeportmentMoveType.Down.name -> {
                request.logMsg="下移部门{${dep.name}}"
            }
            DeportmentMoveType.Top.name -> {
                request.logMsg="置顶部门{${dep.name}}"
            }
            else -> return throw RuntimeException("移动方向有误")
        }


        if(moveDep.level!=dep.level){
            //不同层级
            throw  RuntimeException("不同层级部门无法移动")
        }
        //置顶
        if(moveDirection==DeportmentMoveType.Top.name){
            //需要移动的节点
            val deps=mor.tenant.tenantDepartmentInfo.query()
                .where{it.tenant.id match tenantId}
                .where{it.parent.id match dep.parent.id}
                .where{it.id match_not_equal dep.id}
                .where{it.sort match_lte   dep.sort}
                .toList()
            //置顶当前部门
            mor.tenant.tenantDepartmentInfo.updateById(dep.id).set {
                it.sort to moveDep.sort
            }.exec()
            //移动节点
            deps.forEach {
                deppp->
                mor.tenant.tenantDepartmentInfo.updateById(deppp.id)
                    .set { it.sort to deppp.sort+10F}
                    .exec()
            }
            return ApiResult()
        }

        //上移下移
        mor.tenant.tenantDepartmentInfo.updateById(dep.id).set {
            it.sort to moveDep.sort
        }.exec()
        if (mor.affectRowCount <= 0) {
            throw  RuntimeException("操作失败")
        }
        mor.tenant.tenantDepartmentInfo.updateById(moveDep.id).set {
            it.sort to dep.sort
        }.exec()
        if (mor.affectRowCount <= 0) {
            throw  RuntimeException("操作失败")
        }
        return ApiResult()
    }



    /**
     * @Description 查子部门
     *
     * @param  id 部门id
     * @return ListResult<TransferDeportmentVo>
     * @date 2021/12/18
     */
    @ApiOperation("穿梭框查子部门")
    @PostMapping("/getTransferDeportmentChildren")
    fun getTransferDeportmentChildren(
        id: String,
        name: String,
        @Require skip: Int,
        @Require take: Int,
        request: HttpServletRequest
    ): ListResult<TransferDeportmentVo> {
        val loginUser = request.LoginTenantAdminUser
        mor.tenant.tenantDepartmentInfo.query().apply {
            this.where { it.tenant.id match loginUser.tenant.id }
            if (name.HasValue) {
                this.where { it.name match_like name }
            } else {
                this.where { it.parent.id match id }
            }
            if (id.isEmpty() && name.isEmpty()) {
                this.where { it.level match 1 }
            }
        }.limit(skip, take)
            .toListResult(TransferDeportmentVo::class.java).apply {
                this.data.forEach { dep ->
                    val has = mor.tenant.tenantDepartmentInfo.query().apply {
                        this.where { it.parent.id match dep.id }
                        this.where { it.tenant.id match loginUser.tenant.id }
                    }.exists()
                    dep.hasChildren = has
                    if (name.HasValue) {
                        dep.isSearch = true
                    }
                    dep.count = mor.tenant.tenantDepartmentInfo.query()
                        .where { it.id match dep.id }
                        .select { it.userCount }
                        .toEntity(Int::class.java)!!
                }
                return this
            }
    }

    class ImportDepVo {
        var tenant: IdName = IdName()
        var name: String = ""
        var parent: IdName = IdName()
        var level: Int = 1
        var remark: String = ""
        var id: String = ""
        var path: String = ""
        var rowNumber: Int = 0
        var allPath: String = ""
        var pathList: MutableList<PathDep> = mutableListOf()
        var dbHas: Boolean = false
    }

    class PathDep constructor(
        var name: String = "",
        var level: Int = 1,
        var parent: IdName = IdName()
    )

    @BizLog(BizLogActionEnum.Import,BizLogResourceEnum.Dept,"部门")
    @ApiOperation("部门导入")
    @PostMapping("/departmentImport")
    fun departmentImport(
        @JsonModel file: MultipartFile,
        request: HttpServletRequest
    ): ApiResult<Any> {
        val loginUser = request.LoginTenantAdminUser
        var lang = request.getHeader("lang")

        //检查文档格式
        checkFile(file)
        val reader = ExcelUtil.getReader(file.inputStream)
        val read: List<List<Any?>> = reader.read(1, 1)
        val columns  = if ("部门" == read[0][0]) {
            mutableListOf("部门")
        } else {
            mutableListOf("Department")
        }

        var rootDept = mor.tenant.tenantDepartmentInfo.query()
            .where { it.parent.id match "" }
            .where { it.tenant.id match loginUser.tenant.id }
            .toList().get(0)


        //失败信息
        val failList: MutableList<ExcelDeportmentErrorJob> = mutableListOf()
        //成功数据
        val successList: MutableList<ExcelDeportmentSuccessJob> = mutableListOf()
        //jobId
        val jobId = UUID.randomUUID().toString().replace("-", "")
        val bigList: MutableList<ImportDepVo> = mutableListOf()
        //读取数据
        readDepData(loginUser.tenant, file, columns, bigList,rootDept)
        //要导入的数据
        val will_add_deps: MutableList<ImportDepVo> = mutableListOf()
        //总条数
        val allCount = bigList
            .filter { it.path.isNotEmpty() }
            .distinctBy { it.rowNumber }
            .count()
        //检查部门参数信息并导入
        val selectList = bigList
            .filter { it.path.isNotEmpty() }
            .distinctBy { listOf(it.name, it.path) }
            .toMutableList()
        for (index in 1..selectList.size) {
            var vo = selectList.get(index - 1)
            //判断部门名称长度不超32字符
            if (vo.name.length > 32) {
                var msg: String = if (lang == "cn") {
                    "部门名称不能超过32字符"
                }  else {
                    "Department name cannot exceed 32 characters."
                }
                failList.add(
                    ExcelDeportmentErrorJob(
                        "",
                        loginUser.tenant,
                        jobId,
                        vo.rowNumber,
                        vo.allPath,
                        msg,
                        vo.name
                    )
                )
                continue
            }
            //部门名称不为空
            if (vo.name.isBlank()) {
                var msg: String = if (lang == "cn") {
                    "部门名称不能为空"
                }  else {
                    "Department name cannot be null"
                }
                failList.add(
                    ExcelDeportmentErrorJob(
                        "",
                        loginUser.tenant,
                        jobId,
                        vo.rowNumber,
                        vo.allPath,
                        msg,
                        vo.name
                    )
                )
                continue
            }
            //判断层级
            if (vo.level > 9) {
                val failDep = vo.allPath.split("/").toMutableList()
                val ss = failDep.reversed().take(failDep.size - 9).reversed().joinToString("/")
                var msg: String = if (lang == "cn") {
                    "部门层级不能大于9层"
                }  else {
                    "The maximum layer of departments is 9."
                }
                failList.add(
                    ExcelDeportmentErrorJob(
                        "",
                        loginUser.tenant,
                        jobId,
                        vo.rowNumber,
                        vo.allPath,
                        msg,
                        ss
                    )
                )
                continue
            }
            //判断子部门与父部门重名
            if (vo.name.equals(vo.parent.name)) {
                var msg: String = if (lang == "cn") {
                    "子部门与父部门名称不能相同"
                }  else {
                    "Subdepartment and parent department cannot share the same name."
                }
                failList.add(
                    ExcelDeportmentErrorJob(
                        "",
                        loginUser.tenant,
                        jobId,
                        vo.rowNumber,
                        vo.allPath,
                        msg,
                        vo.parent.name + "/" + vo.name
                    )
                )
                continue
            }

            vo = hasSameDep(loginUser.tenant.id, vo)

            will_add_deps.add(vo)
        }
        var quChongList = will_add_deps.distinctBy { listOf(it.name, it.path, it.level) }.toMutableList()
        val finalList: MutableList<ImportDepVo> = mutableListOf()
        finalList.addAll(quChongList)
        //数据处理
        val level = 1
        val resList: MutableList<ImportDepVo> = mutableListOf()
        val oneList =
            quChongList.filter { it.level == level }.filter { it.name.HasValue }.distinctBy { listOf(it.name, it.path) }
        oneList.forEach {
            if (it.id.isEmpty()) {
                it.id = IdUtil.objectId()
            }
            resList.add(it)
            createChildDep(resList, quChongList, level, it.name, it.id, it.path)
            quChongList = finalList
        }
        //失败条数
        val failCount = failList.distinctBy { it.rowNumber }.count()
        //失败的allPath
        val failAllPaths: MutableList<String> = mutableListOf()
        failList.distinctBy { it.rowNumber }.forEach {
            failAllPaths.add(it.path)
        }

        //成功条数
        val count = allCount - failCount
        resList.forEach {
            if (!failAllPaths.contains(it.allPath)) {
                successList.add(ExcelDeportmentSuccessJob("", loginUser.tenant, jobId, it.allPath))
            }
        }
        resList.filter { !it.dbHas }.toMutableList().forEach {
            val dep = it.ConvertJson(TenantDepartmentInfo::class.java)
            mor.tenant.tenantDepartmentInfo.doInsert(dep)
        }
        //导入成功与失败数据
        importFailData(failList.distinctBy { listOf(it.path, it.reason) })
        //导入成功数据
        importSuccessData(successList.distinctBy { it.path })
        if (failList.size > 0) {
            var msg: String = if (lang == "cn") {
                "导入部门[一共{${allCount}}条数据，导入成功{${count}}条数据，失败{${failCount}}条]"
            }  else {
                "Import department $allCount pieces of data in total, $count imported successfully,  $failCount failed"
            }
            request.logMsg="导入部门一共{${allCount}}条数据，导入成功{${count}}条数据，失败{${failCount}}条"
            return ApiResult.of(
                mapOf(
                    "info" to msg,
                    "jobId" to jobId,
                    "rightCount" to count,
                    "errorCount" to failCount

                )
            )
        } else {
            var msg: String = if (lang == "cn") {
                "导入成功{${count}}条数据"
            }  else {
                "Import $count pieces of data successfully"
            }
            request.logMsg="导入部门导入成功{${count}}条数据"
            return ApiResult.of(
                mapOf(
                    "info" to msg,
                    "jobId" to jobId,
                    "rightCount" to count,
                    "errorCount" to failCount
                )
            )
        }
    }

    fun importFailData(failList: List<ExcelDeportmentErrorJob>) {
        mor.tenant.excelDeportmentErrorJob.batchInsert().apply {
            addEntities(failList)
        }.exec()
    }

    fun importSuccessData(successList: List<ExcelDeportmentSuccessJob>) {
        mor.tenant.excelDeportmentSuccessJob.batchInsert().apply {
            addEntities(successList)
        }.exec()
    }

    /**
     * @Description 读取文件数据
     *
     * @param
     * @return
     * @date 13:30 2022/3/10
     */
    fun readDepData(
        tenant: IdName,
        file: MultipartFile,
        columns: MutableList<String>,
        bigList: MutableList<ImportDepVo>,
        rootDept :  TenantDepartmentInfo
    ) {
        val depNameList: MutableList<String> = mutableListOf()
        val reader = ExcelUtil.getReader(file.inputStream)
        val read: List<List<Any?>> = reader.read(2, reader.getRowCount())
        for (obj in read) {
            depNameList.add(obj[0].toString())
        }


        //解析depNameList部门名称列表
        var row = 3
        depNameList.forEach { names ->

            var tempName = names

            var namesList = tempName.split("/").toList()

            if(!namesList.get(0).equals(rootDept.name)){
                tempName = rootDept.name + "/" + tempName
                namesList = tempName.split("/").toList()
            }
            var level = 1
            for (index in 1..namesList.size) {
                val name = namesList.get(index - 1)
                val smallDep = ImportDepVo()
                smallDep.name = name
                smallDep.tenant = tenant
                smallDep.level = level
                smallDep.allPath = tempName
                smallDep.rowNumber = row
                if (level >= 2) {
                    smallDep.parent.name = namesList[level - 2]
                    //拼本部门所属的路径
                }
                var i = 0
                while (i < smallDep.level) {
                    smallDep.path = smallDep.path + namesList[i]
                    smallDep.pathList.add(PathDep(namesList[i], i + 1))
                    i++
                }
                bigList.add(smallDep)
                level++
            }
            row += 1
        }
    }

    fun hasSameDep(tenantId: String, dep: ImportDepVo): ImportDepVo {
        dep.pathList.sortBy { it -> it.level }
        var id = ""
        var returnDep = ImportDepVo()
        returnDep = dep
        for (index in 1..dep.pathList.size) {
            val pathDep = dep.pathList[index - 1]
            pathDep.parent.id = id
            mor.tenant.tenantDepartmentInfo.query().apply {
                this.where { it.tenant.id match tenantId }
                this.where { it.name match pathDep.name }
                this.where { it.parent.id match pathDep.parent.id }
            }.toEntity().apply {
                if (this != null) {
                    id = this.id
                }
                if (this != null && index == dep.pathList.size) {
                    returnDep.id = this.id
                    returnDep.parent = this.parent
                    returnDep.dbHas = true
                }
            }
        }
        return returnDep
    }

    private fun checkFile(file: MultipartFile) {
        //检查文件格式
        //过滤文件信息并记录不可导入的数据
        val fileName = file.originalFilename
        // 上传文件为空
        if (!fileName.HasValue) {
            throw RuntimeException("没有导入文件")
        }
        //上传文件大小为999条数据
        // 上传文件名格式不正确
        if (fileName.lastIndexOf(".") != -1 && (".xlsx" != fileName.substring(fileName.lastIndexOf(".")) && ".xls" != fileName.substring(
                fileName.lastIndexOf(".")
            ))
        ) {
            throw RuntimeException("文件名格式不正确,请使用后缀名为.XLSX或者.XLS的文件")
        }
        val reader = ExcelUtil.getReader(file.inputStream)
        reader.isIgnoreEmptyRow = true
        val rowCount = reader.read().size
        if (rowCount > maxImportSize + 2) {
            throw RuntimeException("请不要超过{${maxImportSize}}条数据")
        } else if (rowCount == 2) {
            throw RuntimeException("文件内容为空")
        }
        //文件行列
        val read: List<List<Any?>> = reader.read(1, 1)
        if (read.size == 0) {
            throw RuntimeException("导入失败，请检查文件内容格式")
        }
        if ( "部门" != read[0][0] && "Department" != read[0][0]) {
            throw RuntimeException("导入失败，请检查文件内容格式")
        }
        if (read[0].size > 1) {
            throw RuntimeException("导入失败，请检查文件内容格式")
        }
    }

    fun createChildDep(
        resList: MutableList<ImportDepVo>,
        quChongList: MutableList<ImportDepVo>,
        level: Int,
        fatherName: String,
        fatherId: String,
        path: String
    ) {
        val theLevel = level + 1
        val childlist = quChongList
            .filter { it.level == theLevel }
            .filter { it.parent.name.equals(fatherName) }
            .filter { !it.name.equals(fatherName) }
            .filter { it.path.indexOf(path) == 0 }.distinctBy { listOf(it.name, it.path) }
        childlist.forEach { cc ->
            if (cc.id.isEmpty()) {
                cc.id = IdUtil.objectId()
            }

            cc.parent.id = fatherId
            cc.parent.name = fatherName
            resList.add(cc)
            createChildDep(resList, quChongList, theLevel, cc.name, cc.id, cc.path)
        }

    }

    class ExportDepVo {
        var tenant: IdName = IdName()
        var name: String = ""
        var parent: IdName = IdName()
        var level: Int = 1
        var remark: String = ""
        var id: String = ""
        var path: String = ""
    }

    @ApiOperation("部门导出") // 不使用 不修改
    @GetMapping("/departmentExport")
    fun departmentExport(
        request: HttpServletRequest,
        lang : String
    ): ApiResult<String> {
        val columns = linkedMapOf<String, String>(
            "部门" to "path"
        )
        val loginUser = request.LoginTenantAdminUser
        val allList = mor.tenant.tenantDepartmentInfo.query().apply {
            this.where { it.tenant.id match loginUser.tenant.id }
        }.toList(ExportDepVo::class.java).toMutableList()
        val exportList: MutableList<ExportDepVo> = mutableListOf()
        val rootList = allList.filter { it.parent.id.equals("") }.filter { it.level == 1 }
        rootList.forEach { root ->
            root.path = root.name
            exportList.add(root)
            getChilds(root, exportList, root.name, allList)
        }
        val file: ClassPathResource = if (lang == "en") {
            ClassPathResource("deportment_en.xlsx")
        } else {
            ClassPathResource("deportment.xlsx")
        }

        val excel = ExcelComponent { file.stream }

        val sheet = excel.select("Sheet1")
            .setStrictMode(false)
            .setRowOffset(1)
            .setColumns(*columns.keys.toTypedArray())
        val dt = DataTable(JsonMap::class.java)
        dt.columns = columns.keys.toTypedArray()
        exportList.forEach { oriJson2 ->
            val oriJson = rowTransform_key2value(
                oriJson2.ToJson().FromJson<JsonMap>()!!,
                columns.map { it.value to it.key }.toMap()
            )
            /**
             * TODO 部门导出需要处理英文转换 参考
             * @see nancal.iam.mvc.tenant.TenantUserAutoController  export
             */
            oriJson.set("部门", oriJson2.path)
            dt.rows.add(oriJson)
        }

        response.setHeader("Content-Disposition", "attachment;filename=deportment.xlsx")
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8")
        sheet.writeData(response.outputStream, dt)
        return ApiResult()
    }

    fun getChilds(
        dep: ExportDepVo,
        exportList: MutableList<ExportDepVo>,
        fatherNames: String,
        allList: MutableList<ExportDepVo>
    ) {
        val childList = allList.filter { it.parent.id.equals(dep.id) }
        var fName = fatherNames
        childList.forEach { child ->
            fName = fName + "/" + child.name
            child.path = fName
            exportList.add(child)
            getChilds(child, exportList, fName, allList)
            fName = fatherNames
        }

    }

    private fun rowTransform_key2value(row: JsonMap, columnsDefine: Map<String, String>): JsonMap {
        val oriJson = JsonMap()
        val oriJsonKeys = row.keys
        columnsDefine.forEach { kv ->
            val title = kv.key
            val dbCoumnName = kv.value

            if (oriJsonKeys.contains(title)) {
                oriJson.put(dbCoumnName, row.getValue(title))
            }
        }

        return oriJson
    }

    @BizLog(BizLogActionEnum.Export,BizLogResourceEnum.Dept,"部门")
    @ApiOperation("部门模板导出")
    @GetMapping("/exportTemplete")
    fun exportTemplete(
        request: HttpServletRequest,
        lang : String
    ) {
        request.logMsg="导出部门模板"
        val columns = linkedMapOf<String, String>(
            "部门" to "path"
        )
        val file: ClassPathResource = if (lang == "en") {
            ClassPathResource("deportment_en.xlsx")
        } else {
            ClassPathResource("deportment.xlsx")
        }
        val excel = ExcelComponent { file.stream }
        val sheet = excel.select("Sheet1")
            .setRowOffset(1)
            .setColumns(*columns.keys.toTypedArray())
            .setStrictMode(false)

        val dt = DataTable(JsonMap::class.java)
        dt.columns = columns.keys.toTypedArray()
        response.setHeader("Content-Disposition", "attachment;filename=deportment-templete.xlsx")
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8")
        sheet.writeData(response.outputStream, dt)
    }


    @BizLog(BizLogActionEnum.Delete,BizLogResourceEnum.Dept,"部门")
    @ApiOperation("部门批量删除")
    @PostMapping("/departmentBatchDelete")
    fun departmentBatchDelete(
        @JsonModel depIds: List<String>,
        request: HttpServletRequest
    ): ApiResult<Any> {
        request.logMsg="批量删除部门"
        if (depIds.isEmpty()) {
            return ApiResult.error("请选择要删除的部门")
        }
        val loginUser = request.LoginTenantAdminUser
        val depSetIds = depIds.toSet().toList()
        var depsSize = depSetIds.size
        if (depSetIds.contains(loginUser.tenant.id)) {
            depsSize -= 1
        }
        var hasDep = false
        var hasPerson = false
        //查出来不可以删除的
        var allList = mor.tenant.tenantUser.query()
            .where { it.tenant.id match loginUser.tenant.id }
            .toList()
        //有人的
        var conNotDedlete = allList.filter { it.depts.isNotEmpty() }.toMutableList()
        var canNotDeleteIds: MutableList<String> = mutableListOf()
        conNotDedlete.forEach {
            it.depts.forEach { dep ->
                canNotDeleteIds.add(dep.id)
            }
        }
        if (conNotDedlete.size > 0) {
            hasPerson = true
        }
        //有子部门的
        mor.tenant.tenantDepartmentInfo.query().apply {
            this.where { it.tenant.id match loginUser.tenant.id }
            this.where { it.parent.id match_in depSetIds }
        }.toList()
            .apply {
                if (this.size > 0) hasDep = true
                this.forEach {
                    canNotDeleteIds.add(it.parent.id)
                }
            }

        var canDeleteIds = depSetIds.subtract(canNotDeleteIds.toSet().toList())

        request.logMsg="一共{${depIds.size}}条数据，删除成功{${canDeleteIds.size}}条数据，失败{${depIds.size - canDeleteIds.size}}条"
        //执行删除操作
        mor.tenant.tenantDepartmentInfo.delete().apply {
            this.where { it.id match_in canDeleteIds }
            this.where { it.tenant.id match loginUser.tenant.id }
        }.exec()
        if(db.affectRowCount>0){
            mor.tenant.tenantAppAuthResourceInfo.query()
                .where { it.tenant.id match loginUser.tenant.id }
                .where { it.type match AuthTypeEnum.Dept }
                .where { it.target.id match_in  canDeleteIds }
                .toList()
                .apply {
                    this.forEach {
                        mor.tenant.tenantAppAuthResourceInfo.deleteById(it.id).exec()
                    }
                }
        }
        if (canDeleteIds.size == depSetIds.size) {
            return ApiResult()
        }


        if (canDeleteIds.size == 0) {
            if (hasDep) return ApiResult.error("请先删除所有子部门再进行删除部门操作。")
            if (hasPerson) return ApiResult.error("请先删除所有成员再进行删除部门操作。")
            return ApiResult.error("请先删除所有子部门再进行删除部门操作。")
        }
        return ApiResult.of(
            mapOf(
                "info" to "一共{${depIds.size}}条数据，删除成功{${canDeleteIds.size}}条数据，失败{${depIds.size - canDeleteIds.size}}条",
                "rightCount" to canDeleteIds.size,
                "errorCount" to depIds.size - canDeleteIds.size

            )
        )

    }

    @ApiOperation("详情")
    @PostMapping("/detail/{id}")
    fun detail(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<Document> {
        val tenant = request.LoginTenantAdminUser.tenant
        mor.tenant.tenant.queryById(tenant.id).toEntity()
            .must().elseThrow { "找不到租户" }
            .apply {
                tenant.name = this.name
            }
        mor.tenant.tenantDepartmentInfo.queryById(id)
            .where { it.tenant.id match tenant.id }
            .toEntity(Document::class.java)
            .apply {
                if (this == null) {
                    return ApiResult.error<Document>("找不到数据")
                }
                var parent = this.get("parent")?.ConvertJson(IdName::class.java)
                if (parent == null || parent.id.isEmpty()) {
                    this.put("parent", tenant)
                }
                return ApiResult.of(this)
            }
    }

    open class PersonUpDeportmentVo {
        var manager= mutableListOf<IdName>()
        var name: String = ""
        var id: String = ""
    }

    @ApiOperation("获取某个用户所在部门的直接上级（主部门）")
    @PostMapping("/getPersonUpDeportment/{id}")
    fun getPersonUpDeportment(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<PersonUpDeportmentVo> {
        val tenant = request.LoginTenantAdminUser.tenant
        val user = mor.tenant.tenantUser.queryById(id)
            .where { it.tenant.id match tenant.id }
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .select { it.depts }
            .toEntity()
        if (user == null) {
            return ApiResult.error("找不到用户")
        }
        var deps = user.depts.toList()
        val dep = deps.filter { it.isMain == true }.firstOrNull()
        if (dep == null) return ApiResult.error("用户没有所属部门")
        val currentDep = mor.tenant.tenantDepartmentInfo.query()
            .where { it.tenant.id match tenant.id }
            .where { it.id match dep.id }
            .select { it.parent.id }
            .toEntity().must().elseThrow { "部门不存在" }
        if (currentDep.parent.id.isEmpty()) {
            return ApiResult.error("用户没有上级部门")
        }
        val upDep = mor.tenant.tenantDepartmentInfo.queryById(currentDep.parent.id)
            .where { it.tenant.id match tenant.id }
            .toEntity(PersonUpDeportmentVo::class.java).must().elseThrow { "部门不存在" }
        return ApiResult.of(upDep)
    }

    class PersonUpUpDeportmentVo(
        var hasUp: Boolean = false
    ) : PersonUpDeportmentVo()

    @ApiOperation("获取某个用户所在部门的直接上级的上级（主部门）")
    @PostMapping("/getPersonUpUpDeportment/{id}")
    fun getPersonUpUpDeportment(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<PersonUpUpDeportmentVo> {
        val tenant = request.LoginTenantAdminUser.tenant
        val user = mor.tenant.tenantUser.queryById(id)
            .where { it.tenant.id match tenant.id }
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .select { it.depts }
            .toEntity()
        if (user == null) {
            return ApiResult.error("找不到用户")
        }
        var deps = user.depts.toList()
        val dep = deps.filter { it.isMain == true }.firstOrNull()
        if (dep == null) return ApiResult.error("用户没有所属部门")
        val currentDep = mor.tenant.tenantDepartmentInfo.query()
            .where { it.tenant.id match tenant.id }
            .where { it.id match dep.id }
            .select { it.parent.id }
            .toEntity().must().elseThrow { "部门不存在" }
        if (currentDep.parent.id.isEmpty()) {
            return ApiResult.error("用户没有上级部门")
        }
        val upDep = mor.tenant.tenantDepartmentInfo.queryById(currentDep.parent.id)
            .where { it.tenant.id match tenant.id }
            .toEntity().must().elseThrow { "上级部门不存在" }
        if (upDep.parent.id.isEmpty()) {
            return ApiResult.error("用户没有上上级部门")
        }
        val upUpDep = mor.tenant.tenantDepartmentInfo.queryById(upDep.parent.id)
            .where { it.tenant.id match tenant.id }
            .toEntity().must().elseThrow { "上上级部门不存在" }
        val upUpDepVo = upUpDep.ConvertJson(PersonUpUpDeportmentVo::class.java)
        if (upUpDep.parent.id.isNotEmpty()) {
            upUpDepVo.hasUp = true
        }
        return ApiResult.of(upUpDepVo)
    }

    @ApiOperation("获取树形结构的当前部门")
    @PostMapping("/getCurrentDep/{id}")
    fun getCurrentDep(
        @Require id: String,
        request: HttpServletRequest
    ): ApiResult<DeptModel> {
        val tenantId = request.LoginTenantAdminUser.tenant.id
        val dep = mor.tenant.tenantDepartmentInfo.query()
            .where { it.tenant.id match tenantId }
            .where { it.id match id }
            .toEntity()
        if (dep == null) {
            return ApiResult.error("部门信息不存在")
        }
        val vo=dep.ConvertJson(DeptModel::class.java)
        vo.parentId=dep.parent.id
        vo.parentName=dep.parent.name
        mor.tenant.tenantUser.query()
            .where { it.tenant.id match tenantId }
            .where { it.depts.id match_in mutableListOf(dep.id)  }
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .count()
            .apply {
                vo.currentDepPersonCount=this
            }
        mor.tenant.tenantDepartmentInfo.query()
            .where { it.tenant.id match tenantId }
            .where { it.parent.id match dep.id }
            .exists()
            .apply {
                if(this)  vo.hasChildren = true
            }
        vo. children = mutableListOf()
        return ApiResult.of(vo)
    }

/*    @ApiOperation("查组织树与部门(树形结构，人列表在前，部门树内嵌人)")
    @PostMapping("/listDeptAndUser")
    fun listDeportmentAndUser(
        name: String,
        request: HttpServletRequest
    ): ApiResult<DeptUserVo> {
        val tenantId = request.LoginTenantAdminUser.tenant.id
        //所有人
        val dbUsers=mor.tenant.tenantUser.query()
            .where{it.tenant.id match tenantId}
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .toList(TenantUserPath::class.java)
        //所有部门
        val dbDepts=mor.tenant.tenantDepartmentInfo.query()
            .where{it.tenant.id match tenantId}
            .toList()
        //处理多部门的用户 无需处理
       //val dealUsers= dealWithUser(dbUsers)
        val dealUsers=dbUsers
        //拼路径
        spellPath(dealUsers,dbDepts)

        //检索人
        var users= mutableListOf<TenantUserPath>()
        val noDeptUsers=dealUsers.filter { it.depts.isEmpty() || it.depts == null}.toMutableList()
        noDeptUsers.map {
            val depPath=DepPath()
            depPath.isMain=true
            depPath.path=it.tenant.name
            it.path.add(depPath)
        }
        if(name.isNotEmpty())  users=dealUsers.filter { it.name.contains(name)}.toMutableList()
        //检索部门
       val depts=dbDepts.filter { it.name.contains(name) }.toMutableSet()
        //补充部门的子部门
        val deptss=depts.ConvertListJson(TenantDepartmentInfo::class.java).toMutableList()
        depts.forEach {
            val childs=getSubDepts(dbDepts,it.id)
            if(childs.size>0) deptss.addAll(childs)
        }
        //去重部门
        val distinctDepts=deptss.distinctBy { it.id }.toList().toMutableList()
        //找第一层部门
        val firstDepts=findFirstDepList(distinctDepts)
        //部门组装树并挂上人
        val vo =DeptUserVo()
        vo.tenantInfo.id=request.LoginTenantAdminUser.tenant.id
        vo.tenantInfo.name=request.LoginTenantAdminUser.tenant.name
        vo.tenantInfo.userCount=mor.tenant.tenantUser.query()
            .where{it.tenant.id match tenantId}
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .count()
        if(name.isEmpty()) vo.tenantInfo.users=noDeptUsers.ConvertListJson(OnlyUser::class.java).toMutableList()
        if(users.size>0) vo.users=users.ConvertListJson(OnlyUser::class.java).toMutableList()
        vo.deptUsers=getTreeAndUser(dealUsers, distinctDepts, firstDepts)
        return  ApiResult.of(vo)
    }*/






    /**
     * @Description 拼装树
     *
     * @param  dbUsers 本租户的所有用户
     * @param  distinctDepts  去重后的部门
     * @param  firstDepts 最外层部门集合
     * @return
     * @date 10:46 2022/4/18
     */
    fun getTreeAndUser(dbUsers:List<TenantUserPath>,distinctDepts:List<TenantDepartmentInfo>,firstDepts:List<TenantDepartmentInfo>):MutableList<DeptUser>{
        val voList = mutableListOf<DeptUser>()
        firstDepts.forEach {
            val deptUser=it.ConvertJson(DeptUser::class.java)
            //外层的人
            dbUsers.forEach{
                user->
                val has=user.depts.map { it.id }.toList().contains(deptUser.id)
                if(has){
                    deptUser.users.add(user.ConvertJson(OnlyUser::class.java))
                }
            }
            //找儿子
            findSon(dbUsers, deptUser, distinctDepts)
            if(deptUser.children.size>0) deptUser.hasChildren=true
            voList.add(deptUser)

        }
        return voList
    }
    //找儿子
    fun findSon(dbUsers:List<TenantUserPath>,deptUser:DeptUser,distinctDepts:List<TenantDepartmentInfo>){

        val childs=distinctDepts.filter { it.parent.id == deptUser.id }.toMutableList()
        if(childs.size>0){
            val deptUsers = mutableListOf<DeptUser>()
            childs.forEach {
                val deptUser=it.ConvertJson(DeptUser::class.java)
                dbUsers.forEach{
                        user->
                    val has=user.depts.map { it.id }.toList().contains(deptUser.id)
                    if(has){
                        deptUser.users.add(user.ConvertJson(OnlyUser::class.java))
                    }
                }
                findSon(dbUsers, deptUser, distinctDepts)
                if(deptUser.children.size>0) deptUser.hasChildren=true
                deptUsers.add(deptUser)
            }
            deptUser.children=deptUsers
        }

    }
    fun findFirstDepList(distinctDepts:List<TenantDepartmentInfo>):MutableList<TenantDepartmentInfo>{
        val depts= mutableListOf<TenantDepartmentInfo>()
        //找集合中不存在其父部门的部门
        distinctDepts.forEach {
            dept->
            //找父亲
            val depps=distinctDepts.filter { it.id == dept.parent.id }
            if(depps.size==0){
                depts.add(dept)
            }else{
                depts.add(findFather(distinctDepts,depps.get(0)))
            }
        }
        return depts.distinctBy { it.id }.toMutableList()
    }
    //找父亲直到找不到为止
    fun findFather(distinctDepts:List<TenantDepartmentInfo>,dept:TenantDepartmentInfo):TenantDepartmentInfo{
        val fa=distinctDepts.filter { it.id == dept.parent.id }
        if(fa.size==0){
            return dept
        } else{
          return  findFather(distinctDepts,fa.get(0))
        }
    }
    fun spellPath(users:MutableList<TenantUserPath>,depts:List<TenantDepartmentInfo>){
        /*users.forEach {
            user->
            if(user.depts.size>0){
                val deptId =user.depts.get(0).id
                val paths= mutableListOf<String>()
                findFatherForSpell(deptId,depts,paths)
                user.path= Joiner.on("/").join(paths.reversed().toList())
            }
        }*/
        users.forEach {
                user->
            if(user.depts.size>0){
                user.depts.forEach {
                    val deptId =it.id
                    val paths= mutableListOf<String>()
                    findFatherForSpell(deptId,depts,paths)
//                    val path = Joiner.on("/").join(paths.reversed().toList())
                    val path = paths.reversed().joinToString("/")
                    val  pa = DepPath()
                    pa.path=user.tenant.name+"/"+path
                    pa.isMain=it.isMain
                    user.path.add(pa)
                }

            }
        }

    }
    fun findFatherForSpell(deptId:String,depts:List<TenantDepartmentInfo>,paths:MutableList<String>){
        if(deptId.isNotEmpty()){
            val currs=depts.filter { it.id == deptId }
            if(currs.size>0){
                var curr=currs.get(0)
                paths.add(curr.name)
                findFatherForSpell(curr.parent.id,depts,paths)
            }
        }

    }
/*    open class TenantUserPath :TenantUser() {
        var path= mutableListOf<DeptDefine>()
    }*/
    open class TenantUserPath :TenantUser(){
        var path:MutableList<DepPath> = mutableListOf()
    }
    open class DepPath{
        var isMain=false
        var path=""
    }

    open class DeptUser : IdName() {
        var parent: IdName = IdName()
        var level: Int = 1
        var sort: Float = 0F
        var userCount=0
        val type="deportment"


        var users=mutableListOf<OnlyUser>()
        var hasChildren = false
        var children = mutableListOf<DeptUser>()
    }
    open class OnlyUser : IdName() {
        val type="user"
        var mobile=""
        var email=""
        var logo=IdUrl()
        var duty=IdName()
        var path= mutableListOf<DepPath>()
    }
    open class DepTenantInfo : IdName() {
        val type="tenant"
        var userCount=0
        var users = mutableListOf<OnlyUser>()
    }
    open class DeptUserVo{
        var tenantInfo = DepTenantInfo()
        var users= mutableListOf<OnlyUser>()
        var deptUsers= mutableListOf<DeptUser>()
    }



    /**
     * @Description 校验编辑操作参数
     *
     * @param  entity DepartmentInfo
     * @return
     * @date 11:07 2021/12/13
     */
    fun checkEditParameters(entity: TenantDepartmentInfo) {
        if (entity.name.length > 32) {
            throw RuntimeException("部门名称不能超过32个字符")
        }
        if(entity.remark.isNotEmpty() && entity.remark.length>255 )  throw RuntimeException("备注长度不能超过255个字符")
        //电话
        if (entity.phone.HasValue) {
            val matchPhoneParttern = "^1[0-9]{10}\$"
            val isPhoneMatch = Regex(matchPhoneParttern).containsMatchIn(entity.phone)
            if (!isPhoneMatch) {
                throw RuntimeException("电话号码格式错误,请输入手机号")
            }
        }
        if (entity.manager.size>0) {
            entity.manager.forEach { obj ->
                val hasMen = mor.tenant.tenantUser.query().apply {
                    this.where { it.tenant.id match entity.tenant.id }
                    if (obj.id.HasValue) {
                        this.where { it.id match obj.id }
                    }
                }.exists()
                if (!hasMen) {
                    throw RuntimeException("系统中不存在该负责人")
                }
            }
        }
        val hasDepNameInSameGrade = mor.tenant.tenantDepartmentInfo.query().apply {
            if (entity.tenant.id.equals(entity.parent.id)) {
                entity.parent.id = ""
            }
            this.where { it.tenant.id match entity.tenant.id }
            this.where { it.name match entity.name }
            this.where { it.parent.id match entity.parent.id }
            this.where { it.id match_not_equal entity.id }
        }.exists()
        if (hasDepNameInSameGrade) {
            throw RuntimeException("同一级别不允许有相同的名称")
        }
        //子部门不允许与父部门相同、
        val hasSameNameWithFa = mor.tenant.tenantDepartmentInfo.query().apply {
            this.where { it.id match entity.parent.id }
            this.where { it.name match entity.name }
        }.exists()
        if (hasSameNameWithFa) {
            throw RuntimeException("子部门不能与父部门名称相同")
        }



    }

    /**
     * @Description 校验新增操作参数
     *
     * @param  entity DepartmentInfo
     * @return
     * @date 11:07 2021/12/13
     */

    fun checkInsertParameters(entity: TenantDepartmentInfo) {
        if (!entity.name.HasValue) {
            throw RuntimeException("部门名称不能为空")
        }
        if (entity.name.length > 32) {
            throw RuntimeException("部门名称不能超过32个字符")
        }
        if(entity.remark.isNotEmpty() && entity.remark.length>255 )  throw RuntimeException("备注长度不能超过255个字符")
        //电话
        if (entity.phone.HasValue) {
            val matchPhoneParttern = "^1[0-9]{10}\$"
            val isPhoneMatch = Regex(matchPhoneParttern).containsMatchIn(entity.phone)
            if (!isPhoneMatch) {
                throw RuntimeException("电话号码格式错误,请输入手机号")
            }
        }

        //查是否存在该租户
        val hasTenant = mor.tenant.tenant.query().apply {
            this.where { it.id match entity.tenant.id }
        }.exists()
        if (!hasTenant) {
            throw RuntimeException("系统中不存在该租户")
        }
        //系统中是否存在同一级别下部门相同名称的情况
        val hasDepNameInSameGrade = mor.tenant.tenantDepartmentInfo.query().apply {
            val parent = entity.parent
            this.where { it.tenant.id match entity.tenant.id }
            this.where { it.name match entity.name }
            if (entity.tenant.id.equals(parent.id)) {
                parent.id = ""
            }
            this.where { it.parent.id match parent.id }
        }.exists()
        if (hasDepNameInSameGrade) {
            throw RuntimeException("同一级别不允许有相同的名称")
        }
        //子部门不允许与父部门相同、
        val hasSameNameWithFa = mor.tenant.tenantDepartmentInfo.query().apply {
            this.where { it.id match entity.parent.id }
            this.where { it.name match entity.name }
        }.exists()
        if (hasSameNameWithFa) {
            throw RuntimeException("子部门不能与父部门名称相同")
        }


        //系统中是否存在该负责人
        if (entity.manager.size>0) {
            entity.manager.forEach { obj ->
                val hasMen = mor.tenant.tenantUser.query().apply {
                    this.where { it.tenant.id match entity.tenant.id }
                    if (obj.id.HasValue) {
                        this.where { it.id match obj.id }
                    }
                }.exists()
                if (!hasMen) {
                    throw RuntimeException("系统中不存在该负责人")
                }
            }
        }
    }


    /**
     * 根据节点列表,递归构建一棵树VO
     * @param nodes 节点列表
     */
    fun buildDeportmentVoTree(nodes: List<DeportmentVo>, deportmentId: String): List<DeportmentVo> {
        val treeNodes = mutableListOf<DeportmentVo>()
        val rootNodes = nodes.filter { it.parentId == deportmentId }
        rootNodes.forEach {
            buildDeportmentVoChildren(it, nodes)
            if (it.children.isNotEmpty()) {
                it.hasChildren = true
            }
            treeNodes.add(it)
        }

        return treeNodes
    }

    /**
     * 递归构建当前节点的的孩子列表VO
     * @param node 当前节点
     * @param nodes 节点列表
     */
    private fun buildDeportmentVoChildren(node: DeportmentVo, nodes: List<DeportmentVo>) {
        val nodeId = node.id
        val children = nodes.filter { it.parentId == nodeId }
        children.forEach {
            buildDeportmentVoChildren(it, nodes)
        }
        if (children.isNotEmpty()) {
            node.hasChildren = true
        }
        node.children = children as MutableList<DeportmentVo>
    }


    /**
     * 根据节点列表,递归构建一棵树VO 计算是否超出层级限制
     * @param nodes 节点列表
     */
    fun buildDeportmentVoTreeForCheck(
        nodes: List<DeportmentVo>,
        deportmentId: String,
        fatherLevel: Int
    ): List<DeportmentVo> {

        val treeNodes = mutableListOf<DeportmentVo>()
        val rootNodes = nodes.filter { it.parentId == deportmentId }
        //当前部门的子
        rootNodes.forEach {
            buildDeportmentVoChildrenForCheck(it, nodes, fatherLevel + 1)
            treeNodes.add(it)
        }

        return treeNodes
    }

    /**
     * 递归构建当前节点的的孩子列表VO 计算是否超出层级限制
     * @param node 当前节点
     * @param nodes 节点列表
     */
    private fun buildDeportmentVoChildrenForCheck(node: DeportmentVo, nodes: List<DeportmentVo>, level: Int) {
        if (level > deportmentLevel) {
            throw  RuntimeException("部门层级必须小于等于${deportmentLevel}层")
        }
        val nodeId = node.id
        val children = nodes.filter { it.parentId == nodeId }
        children.forEach {
            buildDeportmentVoChildrenForCheck(it, nodes, level + 1)
        }
        node.children = children as MutableList<DeportmentVo>
    }

    /**
     * @Description 查当前部门以及子集部门人数
     *
     * @param  depId 当前部门id
     * @param  ids 子部门ids
     * @return NUmber
     * @date 11:53 2021/12/15
     */
    fun getAllDepPersonCount(depId: String, ids: MutableList<String>): Number {
        ids.add(depId)
        return mor.tenant.tenantUser.query().apply {
            this.where { it.depts.id match_in ids }
            if(openPrivatization){
                this.where { it.adminType match  TenantAdminTypeEnum.None}
            }
        }.count()
    }

    /**
     * @Description 查当前部门人数
     *
     * @param  depId 当前部门id
     * @return NUmber
     * @date 11:53 2021/12/15
     */
    fun getCurrentDepPersonCount(depId: String): Number {
        return mor.tenant.tenantUser.query().apply {
            this.where { it.depts.id match depId }
            if(openPrivatization){
                this.where { it.adminType match  TenantAdminTypeEnum.None}
            }
        }.count()
    }
    fun checkSave(entity: Document):String{
        val ent = entity.ConvertJson(TenantDepartmentInfo::class.java)
        if(ent.name.isEmpty() ) return "部门名称不能为空"
        if(ent.name.length>32 ) return "部门名称长度不能超过32个字符"
        if(ent.remark.isNotEmpty() && ent.remark.length>255 ) return "备注长度不能超过255个字符"

        val fileds =entity.get("fileds")
        if(fileds!=null){
           val fis = fileds.ConvertListJson(Filed::class.java)
            fis.forEach {
                if(it.value.isNotEmpty() && it.value.length>32){
                    throw RuntimeException(it.name+"长度不能超过32个字符")
                }
            }
        }
        return ""
    }

    class Filed {
        var code = ""
        var fieldType = ""
        var name = ""
        var value = ""
    }
}


interface DepInterface {
    fun getChildrenIds(
        list: List<DepartmentInfoAutoController.DeportmentInfoTree>,
        ids: MutableList<String>,
        id: String
    )

    fun buildChildrenIds(
        id: String,
        ids: MutableList<String>,
        nodes: List<DepartmentInfoAutoController.DeportmentInfoTree>
    )
}

class DepService : DepInterface {
    /**
     * 查id组织树的子集部门id
     * @param list 部门集合
     */
    override fun getChildrenIds(
        list: List<DepartmentInfoAutoController.DeportmentInfoTree>,
        ids: MutableList<String>,
        id: String
    ) {

        val nodes = list.filter { it.parent.id == id }
        nodes.forEach { node ->
            ids.add(node.id)
            buildChildrenIds(node.id, ids, list)
        }
    }

    /**
     * 递归构建当前节点的的孩子列表的ids
     * @param id 当前节点id
     * @param nodes 节点列表
     */
    override fun buildChildrenIds(
        id: String,
        ids: MutableList<String>,
        nodes: List<DepartmentInfoAutoController.DeportmentInfoTree>
    ) {
        val children = nodes.filter { it.parent.id == id }
        children.forEach { it ->
            ids.add(it.id)
            buildChildrenIds(it.id, ids, nodes)

        }
    }
}
