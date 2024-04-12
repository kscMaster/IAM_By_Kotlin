//package nancal.iam.utils
//
//import nbcp.comm.AsString
//import nbcp.db.excel.ExcelComponent
//import org.springframework.beans.factory.InitializingBean
//import org.springframework.core.io.ClassPathResource
//import org.springframework.stereotype.Component
//
//
//data class LangResData(var key: String = "", var cn: String = "", var en: String = "")
//
//
//@Component
//class LangResUtil : InitializingBean {
//    companion object {
//        private lateinit var list: List<LangResData>
//        fun initData(list: List<LangResData>) {
//            this.list = list;
//        }
//
//        fun getCn(key: String): String {
//            list.firstOrNull { it.key == key }
//                .apply {
//                    if (this == null) {
//                        return key;
//                    }
//
//                    return this.cn.AsString(this.en);
//                }
//        }
//
//        fun getEn(key: String): String {
//            list.firstOrNull { it.key == key }
//                .apply {
//                    if (this == null) {
//                        return key;
//                    }
//
//                    return this.en.AsString(this.cn);
//                }
//        }
//
//
//        fun getRes(key: String, lang: String): String {
//            if (lang == "en") return getEn(key);
//            return getCn(key);
//        }
//    }
//
//    override fun afterPropertiesSet() {
//        var d = ExcelComponent { ClassPathResource("多语言资源.xlsx").inputStream }
//        var sheet = d.select(0)
//        sheet.setColumns("key", "en", "cn")
//        sheet.setStrictMode(false)
//        var res = sheet.getDataTable(LangResData::class.java)
//
//        initData(res.rows);
//    }
//
//}