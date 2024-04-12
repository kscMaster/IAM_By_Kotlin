package nancal.iam.flyway

import nancal.iam.db.mongo.entity.SysApplication
import nancal.iam.db.mongo.entity.TenantApplication
import nancal.iam.db.mongo.mor
import nbcp.comm.HasValue
import nbcp.db.CodeName
import nbcp.db.FlywayVersionBaseService
import nbcp.db.excel.ExcelComponent
import nbcp.db.mongo.*
import nbcp.model.DataTable
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

/**
 * @Author zhaopeng
 *
 * @Description
 * @Date 2022/7/6
 */
@Component
class `19-InitDb` : FlywayVersionBaseService(19) {

    var map: Map<String, AppData> = mutableMapOf()


    override fun exec() {
        initData214()
        asyncData()
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

    fun asyncData() {
        val adminData: List<SysApplication> =
            mor.iam.sysApplication.query().toList(SysApplication::class.java)
        adminData.forEach {
            if ( it.lable.isEmpty()) { // lable 处理
                val data = compareToDataLable(it)
                if (!it.ename.HasValue) {
                    data.ename = ""
                }
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
                if (!it.ename.HasValue) {
                    data.ename = ""
                }
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

    /**
     * admin url 处理
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

    /**
     * 租户 url 处理
     */
    fun compareToDataTenant(entity: TenantApplication): TenantApplication {
        if (map[entity.appCode] == null) {
            entity.url = "https://"
            return entity
        }
        entity.url = map[entity.appCode]?.url
        return entity

    }

    /**
     * lable
     */
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


    /**
     * admin lable 处理
     */
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

    data class AppData(var name: String = "", var url: String = "", var code: String = "", var type: String = "")


}