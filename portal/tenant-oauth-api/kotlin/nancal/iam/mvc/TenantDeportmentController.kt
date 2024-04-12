package nancal.iam.mvc

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import nancal.iam.db.mongo.TenantAdminTypeEnum
import nancal.iam.db.mongo.entity.TenantDepartmentInfo
import nancal.iam.db.mongo.entity.TenantUser
import nancal.iam.db.mongo.mor
import nbcp.comm.ApiResult
import nbcp.comm.ConvertJson
import nbcp.comm.ConvertListJson
import nbcp.db.IdName
import nbcp.db.IdUrl
import nbcp.db.mongo.match
import nbcp.db.mongo.query
import nbcp.web.LoginUser
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

/**
 * @Author wrk
 *
 * @Description
 * @Date 2022/5/17-14:49
 */
@Api(description = "部门", tags = arrayOf("Deportment"))
@RestController
@RequestMapping("/tenant/department")
class TenantDeportmentController {
    @Value("\${openPrivatization}")
    private val openPrivatization: Boolean = true


    open class TenantUserPath : TenantUser(){
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
        var logo= IdUrl()
        var duty= IdName()
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
    @ApiOperation("查组织树与部门(树形结构，人列表在前，部门树内嵌人)")
    @PostMapping("/listDeptAndUser")
    fun listDeportmentAndUser(
        name: String,
        request: HttpServletRequest
    ): ApiResult<DeptUserVo> {
        val tenantId = request.LoginUser.organization.id
        //所有人
        val dbUsers= mor.tenant.tenantUser.query()
            .where{it.tenant.id match tenantId}
            .apply {
                if(openPrivatization){
                    this.where { it.adminType match  TenantAdminTypeEnum.None}
                }
            }
            .toList(TenantUserPath::class.java)
        //所有部门
        val dbDepts= mor.tenant.tenantDepartmentInfo.query()
            .where{it.tenant.id match tenantId}
            .toList()
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
        vo.tenantInfo.id=request.LoginUser.organization.id
        vo.tenantInfo.name=request.LoginUser.organization.name
        vo.tenantInfo.userCount= mor.tenant.tenantUser.query()
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
    }
    fun spellPath(users:MutableList<TenantUserPath>,depts:List<TenantDepartmentInfo>){
        users.forEach {
                user->
            if(user.depts.size>0){
                user.depts.forEach {
                    val deptId =it.id
                    val paths= mutableListOf<String>()
                    findFatherForSpell(deptId,depts,paths)
//                    val path= Joiner.on("/").join(paths.reversed().toList())
                    val path= paths.reversed().joinToString("/")
                    val  pa = DepPath()
                    pa.path=user.tenant.name+"/"+path
                    pa.isMain=it.isMain
                    user.path.add(pa)
                }

            }
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
}