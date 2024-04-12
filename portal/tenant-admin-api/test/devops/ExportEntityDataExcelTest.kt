package devops

import cn.hutool.core.util.ClassUtil
import cn.hutool.core.util.IdUtil
import cn.hutool.core.util.ReflectUtil
import cn.hutool.poi.excel.ExcelUtil
import nbcp.db.Cn
import org.junit.jupiter.api.Test
import java.io.File
import javax.swing.filechooser.FileSystemView

/**
 * @Author wrk
 *
 * @Description
 * @Date 2022/4/29-9:51
 */
class ExportEntityDataExcelTest {
    class TableVo(
        var tableName: String = "",
        var field: String = "",
        var canDelete: String = "",
        var remark: String = ""
    )

    @Test
    fun metadata(){
        val entityPackage = "nancal.iam.db.mongo.entity"
        val entityClasses: Set<Class<*>> = ClassUtil.scanPackage(entityPackage)
        val data= mutableListOf<TableVo>()

        entityClasses
            .distinctBy { it.name }
            .sortedBy { it.name }

        var i = 0
        entityClasses.forEach { entity ->
            val annotation = entity.getAnnotation(Cn::class.java)

            if (annotation != null){
                i++
                println("${entity.simpleName}, ${annotation.value}")//类名  别名
            }else{
                println("${entity.simpleName}, ")
            }
            ReflectUtil.getFields(entity).forEach { field ->
                val vo=TableVo()
                if (annotation != null) {
                    vo.tableName = "${entity.simpleName} \n ${annotation.value}"
                }else{
                    vo.tableName = entity.simpleName
                }


                val annotation1 = field.getAnnotation(Cn::class.java)
                if (annotation1 != null){
                    vo.field=field.name
                    vo.remark=annotation1.value
                    println("${field.name}, ${annotation1.value}")//字段名  别名
                }else{
                    vo.field=field.name
                    println("${field.name}, ")
                }
                data.add(vo)
            }
        }
        exportData(data)
        println(entityClasses.size)
        println(i)
    }
    fun exportData(data:MutableList<TableVo>){
        val writer = ExcelUtil.getWriter("${getDesktopUrl()}/数据库字段说明-${IdUtil.fastUUID()}.xlsx")
        //自定义标题别名
        writer.addHeaderAlias("tableName", "表名称");
        writer.addHeaderAlias("field", "表字段");
        writer.addHeaderAlias("canDelete", "是否可以删除");
        writer.addHeaderAlias("remark", "备注");
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(data, true)
        writer.autoSizeColumnAll()
        writer.close()
    }
    fun getDesktopUrl():String{
        val fsv = FileSystemView.getFileSystemView()
        val com: File = fsv.homeDirectory
        //println(com.getPath().replace("\\","/"))
        return com.getPath().replace("\\","/")
    }
}