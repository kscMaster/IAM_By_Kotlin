package nancal.iam.mvc.tenant

import nancal.iam.TestBase
import nancal.iam.db.mongo.ResourceTypeEnum
import nancal.iam.db.mongo.entity.SysApplication
import nancal.iam.db.mongo.entity.SysResourceInfo
import nancal.iam.db.mongo.entity.TenantApplication
import nancal.iam.db.mongo.entity.TenantResourceInfo
import nancal.iam.db.mongo.mor
import nancal.iam.util.CodeUtils
import nancal.iam.util.IPUtils
import nancal.iam.utils.EnumMatchUtils
import nbcp.comm.FromJson
import nbcp.comm.HasValue
import nbcp.comm.ToJson
import nbcp.comm.simpleFieldToJson
import nbcp.db.CodeName
import nbcp.db.excel.ExcelComponent
import nbcp.db.mongo.*
import nbcp.model.DataTable
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import org.springframework.util.CollectionUtils
import java.time.LocalDateTime
import java.util.function.Function
import java.util.stream.Collectors
import javax.annotation.Resource

class mytest : TestBase() {


    @Resource
    lateinit var enumMatchUtils: EnumMatchUtils

    var map: Map<String, AppData> = mutableMapOf()



    @Test
    fun exec() {
        initPersonClassified()
    }

    fun initPersonClassified() {
        val dataList: List<SysResourceInfo> = mor.iam.sysResourceInfo.query().toListResult().data.toList()
        if (CollectionUtils.isEmpty(dataList)) {
            return
        }
        dataList.forEach{
            asyncResource(it)

        }
    }

    fun asyncResource(entity: SysResourceInfo) : Boolean {
        // code处理
        entity.code.split(":").toMutableList().forEach {
            if (!it.HasValue) {
                return false
            }
        }
        val codeList: MutableList<String> = CodeUtils.codeHolder(entity.code)
        if (codeList.size == 1) {
            return true
        }

        codeList.removeAt(codeList.size - 1) // 当前资源不处理
        codeList.forEach { item ->

            entity.id = ""
            entity.code = item
            entity.type = ResourceTypeEnum.Data
            entity.createAt = LocalDateTime.now()
            entity.updateAt = LocalDateTime.now()
            mor.iam.sysResourceInfo.query()
                .where { it.appInfo.code match entity.appInfo.code }
                .where { it.code match item }
                .toEntity()
                .apply {
                    if (this == null) { // 父资源不存在
                        asyncTenantResource(entity).apply {
                            if (!this) {
                                return false
                            }
                        }
                    }
                }
        }
        return true
    }

    /**
     * 同步到租户侧数据
     */
    fun asyncTenantResource(entity: SysResourceInfo):Boolean {
        var isInsert = false
        mor.iam.sysResourceInfo.updateWithEntity(entity)
            .run {
                if (entity.id.HasValue) {
                    return@run this.execUpdate()
                } else {
                    isInsert = true
                    return@run this.execInsert()
                }
            }
            .apply {
                if (this == 0) {
                    return false
                }
                if (isInsert) {
                    mor.tenant.tenantApplication.query()
                        .where { it.appCode match entity.appInfo.code }
                        .toList()
                        .apply {

                            if (this.isNotEmpty()) {
                                var tenantResource: TenantResourceInfo
                                val list = mutableListOf<TenantResourceInfo>()

                                this.forEach {
                                    tenantResource = entity.ToJson().FromJson(TenantResourceInfo::class.java)!!
                                    tenantResource.id = ""
                                    tenantResource.tenant = it.tenant
                                    tenantResource.isSysDefine = true
                                    tenantResource.sysId = entity.id
                                    list.add(tenantResource)
                                }

                                mor.tenant.tenantResourceInfo.batchInsert()
                                    .apply {
                                        addEntities(list)
                                    }
                                    .exec().apply {
                                        if (this == 0) {
                                            return false
                                        }
                                    }
                            }
                        }
                } else {
                    mor.tenant.tenantResourceInfo.query()
                        .where { it.appInfo.code match entity.appInfo.code }
                        .where { it.sysId match entity.id }
                        .toList()
                        .apply {
                            if (this.isNotEmpty()) {
                                this.forEach {
                                    it.remark = entity.remark
                                    it.action = entity.action
                                    it.name = entity.name
                                    it.code = entity.code
                                    it.type = entity.type
                                    it.resource = entity.resource
                                    it.dataAccessLevel = entity.dataAccessLevel

                                    mor.tenant.tenantResourceInfo.updateWithEntity(it).execUpdate().apply {
                                        if (this == 0) {
                                            return false
                                        }
                                    }
                                }
                            }
                        }
                }
                return true
            }
    }

    @Test
    fun test01() {
        val cnKey = enumMatchUtils.getCnKey("登录")
        println(cnKey)
    }

    @Test
    fun city() {
        println(IPUtils.getCityInfo("127.0.0.1"))
        println(IPUtils.getCityInfo("61.217.179.255"))
        println(IPUtils.getCityInfo("194.117.103.55"))
        println(IPUtils.getCityInfo("61.117.255.255"))
        println(IPUtils.getCityInfo("203.234.255.255"))
        println(IPUtils.getCityInfo("202.96.128.166"))
    }

    fun initData213() {
        val d = ExcelComponent { ClassPathResource("企业容器应用同步_213.xlsx").inputStream }
        val sheet = d.select(0)
        sheet.setColumns("name", "url", "code", "type")
        sheet.setStrictMode(false)
        val dataTable: DataTable<AppData> = sheet.getDataTable(AppData::class.java)
        // 上架平台 0  SaaS 市场 ；1 乐仓生产力中台  ；2 乐造OS

        val res: List<AppData> = dataTable.rows

        val toMap: Map<String, AppData> = res.associateBy { it.code }

        map = toMap
    }

    fun initData214() {
        val d = ExcelComponent { ClassPathResource("企业容器应用同步_214.xlsx").inputStream }
        val sheet = d.select(0)
        sheet.setColumns("name", "url", "code", "type")
        sheet.setStrictMode(false)
        val dataTable: DataTable<AppData> = sheet.getDataTable(AppData::class.java)


        val res: List<AppData> = dataTable.rows

        val toMap: Map<String, AppData> = res.associateBy { it.code }

        map = toMap
    }

    /**
     * admin url 处理  分开处理
     */
    fun compareToData(entity: SysApplication): SysApplication {
        if (map[entity.appCode] == null) {
            entity.url = "https://"
            return entity
        }
        /**
         *  LPC("生产力中台"), LOS("乐造OS应用管理平台"),  RFZ("后厂造商城")
         * 上架平台 0  SaaS 市场 ；1 乐仓生产力中台  ；2 乐造OS
         */
        entity.url = map[entity.appCode]?.url
        return entity

    }

    fun compareToDataLable(entity: SysApplication): SysApplication {
        if (map[entity.appCode] == null) {
            entity.lable = mutableListOf(CodeName("LOS", "乐造OS应用管理平台"))
            return entity
        }
        if (map[entity.appCode]?.type.HasValue) {
            val lable = mutableListOf<CodeName>()
            val split: List<String> = map[entity.appCode]?.type!!.split(",")
            split.forEach {
                when (it) {
                    "0" -> lable.add(CodeName("RFZ", "后厂造商城"))
                    "1" -> lable.add(CodeName("LPC", "生产力中台"))
                    "2" -> lable.add(CodeName("LOS", "乐造OS"))
                }

            }
            entity.lable = lable
        }
        return entity

    }

    /**
     * tenant
     */
    fun compareToDataTenant(entity: TenantApplication): TenantApplication {
        if (map[entity.appCode] == null) {
            entity.url = "https://"
            return entity
        }
        entity.url = map[entity.appCode]?.url
        return entity

    }

    fun compareToDataTenantLable(entity: TenantApplication): TenantApplication {
        if (map[entity.appCode] == null) {
            entity.lable = mutableListOf(CodeName("LOS", "乐造OS应用管理平台"))
            return entity
        }
        if (map[entity.appCode]?.type.HasValue) {
            val lable = mutableListOf<CodeName>()
            val split: List<String> = map[entity.appCode]?.type!!.split(",")
            split.forEach {
                when (it) {
                    "0" -> lable.add(CodeName("RFZ", "后厂造商城"))
                    "1" -> lable.add(CodeName("LPC", "生产力中台"))
                    "2" -> lable.add(CodeName("LOS", "乐造OS"))
                }

            }
            entity.lable = lable
        }
        return entity

    }


    fun asyncData() {
        val adminData: List<SysApplication> =
            mor.iam.sysApplication.query().toList(SysApplication::class.java)
        adminData.forEach {
            if ( it.lable.isEmpty()) { // lable 处理
                val data = compareToDataLable(it)
                try {
                    mor.iam.sysApplication.updateWithEntity(data)
                        .run {
                            return@run this.execUpdate()
                        }.apply {
                            if (this == 0) {
                                println("admin 同步失败")
                            }
                            println("admin 成功")
                        }
                } catch (e: Exception) {
                    println("失败记录id====== " + it.id)
                }
            }
            if (!it.url.HasValue) { // url 处理
                val data = compareToData(it)
                try {
                    mor.iam.sysApplication.updateWithEntity(data)
                        .run {
                            return@run this.execUpdate()
                        }.apply {
                            if (this == 0) {
                                println("admin 同步失败")
                            }
                            println("admin 成功")
                        }
                } catch(e:Exception) {
                    println("失败记录id====== " + it.id)
                }
            }
        }

        // 租户侧应用
        val tenantData: MutableList<TenantApplication> =
            mor.tenant.tenantApplication.query().toList(TenantApplication::class.java)
        tenantData.forEach {
            if (it.lable.isEmpty()) { // lable 处理
                val data: TenantApplication = compareToDataTenantLable(it)
                try {
                    mor.tenant.tenantApplication.updateWithEntity(data)
                        .run {
                            return@run this.execUpdate()
                        }.apply {
                            if (this == 0) {
                                println("tenant 同步失败")
                            }
                            println("tenant 成功")
                        }

                } catch (e: Exception) {
                    println("失败记录id====== " + it.id)
                }
            }
            if (!it.url.HasValue) {
                val dataTenant = compareToDataTenant(it)
                try {
                    mor.tenant.tenantApplication.updateWithEntity(dataTenant)
                        .run {
                            return@run this.execUpdate()
                        }.apply {
                            if (this == 0) {
                                println("tenant 同步失败")
                            }
                            println("tenant 成功")
                        }

                } catch (e: Exception) {
                    println("失败记录id====== " + it.id)
                }
            }
        }

    }
    @Test
    fun test213() {
        initData213()
//        initData214() // TODO 一定要修改数据库配置
        asyncData()
    }

    data class AppData(var name: String = "", var url: String = "", var code: String = "", var type: String = "")


}