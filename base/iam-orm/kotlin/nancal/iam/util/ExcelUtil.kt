package nancal.iam.util

import nancal.iam.db.es.entity.BizLogData
import nbcp.comm.HasValue
import org.apache.poi.hssf.usermodel.HSSFDataFormat
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.time.format.DateTimeFormatter
import kotlin.math.log


class ExcelUtil {

    companion object {
        /**
         * 生成Excel并写入数据信息
         * @param dataList 数据列表
         * @return 写入数据后的工作簿对象
         */
        fun exportData(
            dataList: List<BizLogData>, titles : List<String>
        ): Workbook? {
//            val workbook: Workbook = HSSFWorkbook() xls
            val workbook: Workbook = XSSFWorkbook() // xlsx
            return try {
                writeDataRaito(workbook, dataList, titles)
            } catch (e: Exception) {
                workbook
            }
        }

        /**
         * 配置趋势
         * @param workbook
         * @param dataList
         * @param headList
         * @return
         */
        private fun writeDataRaito(
            workbook: Workbook,
            dataList: List<BizLogData>,
            headList: List<String?>
        ): Workbook {
            val sheet: Sheet = buildDataSheet(workbook, headList)
            sheet.horizontallyCenter = true
            var rowNum = 0
            dataList.forEach { v ->
                try {
                    rowNum += 1
                    // 时间、用户、useriD、角色、事件类型、事件详情、设备系统、浏览器、客户端IP、地点、操作参数、操作结果、原因
                    val rowS: Row = sheet.createRow(rowNum)
                    val df: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val localTime = df.format(v.createAt)
                    rowS.createCell(0).setCellValue(localTime)
                    if (v.creator.name.HasValue) {
                        rowS.createCell(1).setCellValue(v.creator.name)
                    }
                    if (v.creator.id.HasValue) {
                        rowS.createCell(2).setCellValue(v.creator.id)
                    }
                    if (v.data.roles.isNotEmpty()) {
                        val map: List<String> = v.data.roles.map { it.name }.distinct()
                        var a = ""
                        map.forEach {
                            a = "$a$it,"
                        }
                        val take: String = a.take(a.length - 1)
                        rowS.createCell(3).setCellValue(take)
                    }
                    if (v.data.resource.HasValue) {
                        rowS.createCell(4).setCellValue(v.data.action + " " + v.data.resource)
                    } else {
                        rowS.createCell(4).setCellValue(v.data.action)
                    }
                    rowS.createCell(5).setCellValue(v.data.remark)
                    rowS.createCell(6).setCellValue(v.data.os)
                    rowS.createCell(7).setCellValue(v.data.browser)
                    rowS.createCell(8).setCellValue(v.data.ip)
                    rowS.createCell(9).setCellValue(v.data.city)
                    if (v.request.body.length < 32767) {
                        rowS.createCell(10).setCellValue(v.request.body)
                    } else {
                        rowS.createCell(10).setCellValue(v.request.body.substring(0, 32766))
                    }
                    rowS.createCell(11).setCellValue(v.data.result)
                    rowS.createCell(12).setCellValue(v.msg)
                }catch (e:Exception) {
                    println(e.message)
                }
            }
            return workbook
        }


        /**
         * 生成sheet表，并写入第一行数据（列头）
         * @param workbook 工作簿对象
         * @return 已经写入列头的Sheet
         */
        private fun buildDataSheet(workbook: Workbook, headList: List<String?>): Sheet {
            val sheet: Sheet = workbook.createSheet()
            sheet.defaultRowHeight = 400.toShort()
            val cellStyle = buildHeadCellStyle(sheet.workbook)

            // 写入第一行各列的数据
            val head: Row = sheet.createRow(0)
            for (i in headList.indices) {
                val cell: Cell = head.createCell(i)
                cell.setCellValue(headList[i])
                cell.cellStyle = cellStyle
            }
            val textCellStyle = workbook.createCellStyle()
            textCellStyle.alignment = HorizontalAlignment.CENTER
            textCellStyle.dataFormat = HSSFDataFormat.getBuiltinFormat("@")
            for (i in headList.indices) {
                sheet.setColumnWidth(i, 6000)
                sheet.setDefaultColumnStyle(i, textCellStyle)
            }
            return sheet
        }

        /**
         * 设置第一行列头的样式
         * @param workbook 工作簿对象
         * @return 单元格样式对象
         */
        private fun buildHeadCellStyle(workbook: Workbook): CellStyle {
//            val titleStyle =  workbook.createCellStyle()
            val titleStyle =  workbook.createCellStyle() as XSSFCellStyle
            // 水平居中
            titleStyle.alignment = HorizontalAlignment.LEFT
            // 垂直居中
            titleStyle.verticalAlignment = VerticalAlignment.CENTER

            titleStyle.borderBottom = BorderStyle.THIN
            titleStyle.borderLeft = BorderStyle.THIN
            titleStyle.borderTop = BorderStyle.THIN
            titleStyle.borderRight = BorderStyle.THIN
            //单元格背景色 setFillForegroundColor R: 52 G: 127 B: 192
            val rgb = byteArrayOf(53.toByte(),127.toByte(), 191.toByte())
            titleStyle.setFillForegroundColor(XSSFColor(rgb, DefaultIndexedColorMap()))
            //单元格填充效果
            titleStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
            val font = workbook.createFont()
            //设置字体颜色
            font.color = 1
            //设置字体加粗
            font.bold = true
            titleStyle.setFont(font)
            return titleStyle
        }

    }

}