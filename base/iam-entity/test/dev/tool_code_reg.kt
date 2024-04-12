package nancal.iam.dev

//import com.nancal.cipher.RSARawUtil
import nancal.iam.TestBase
import org.junit.jupiter.api.Test
import nbcp.comm.*
import nbcp.db.CodeValue
import nbcp.utils.MyUtil
import java.io.File
import java.io.FileWriter


class toolCodeReg : TestBase() {

    @Test
    fun Kt2Java_reg() {
        var basePath = Thread.currentThread().contextClassLoader.getResource("./").path.split("/target/")[0];

        var path = MyUtil.joinFilePath(basePath, "../iam-orm/kotlin")

        reg_replace(File(path));
    }

    private fun reg_replace(file: File) {
        if (file.exists() == false) return;

        if (file.isFile) {
            if (file.extension != "java") return;

            txt_doWork(file, """import nbcp.comm.HasValue;""", """""");
            txt_doWork(file, """import nbcp.comm.AsString;""", """""");
            txt_doWork(file, """import nbcp.comm.minus;""", """""");
            txt_doWork(file, """import nancal.iam.db.redis.rer;""", """""");

            txt_doWork(file, """@JvmStatic""", """""");


            reg_doWork(file, """\bAny\b""", {
                return@reg_doWork """Object""";
            });

            /**
             * 函数定义转换
             */
            reg_doWork(file, """^\s+(private|)*\s+fun([^(]+)\(([^)]+)\)\s*\:\s*([\w<>]+)\??\s*\{""", {
                var pGroup = it.groups.elementAt(3)!!;
                var str = pGroup.value;
                if (str.contains("<")) {
                } else {
                    str = str.split(",").map { it.split(":").asReversed().joinToString(" ") }.joinToString(",")
                }

                var prefix = "public";
                if (it.groups.elementAt(1)!!.value == "private") {
                    prefix = "private";
                }
                return@reg_doWork "${prefix} ${it.groups[4]!!.value} ${it.groups[2]!!.value}(${str}) {";
            });

            /**
            private val userSystemRedis
            get() = RedisStringProxy()
             */
            reg_doWork(file, """^\s*val\s+(\w+)\s+get\s*\(\s*\)\s*\=\s*(\S+)$""", {
                var pGroup = it.groups.elementAt(2)!!;
                var str = pGroup.value;

                if (str.trim().endsWith(";") == false) {
                    str += ";"
                }

                return@reg_doWork """public boolean get${MyUtil.getBigCamelCase(it.groups[1]!!.value)}() {
                    return ${str}
            } """;
            });


            reg_doWork(file, """^\s*val\s+(\w+)\s*\:\s*(\w+)\s+get\s*\(\s*\)\s*\=\s*(\S+)$""", {
                var pGroup = it.groups.elementAt(3)!!;
                var str = pGroup.value;

                if (str.trim().endsWith(";") == false) {
                    str += ";"
                }

                return@reg_doWork """public ${it.groups[2]!!.value} get${MyUtil.getBigCamelCase(it.groups[1]!!.value)}() {
                    return ${str}
            } """;
            });


            //.where { it.loginName match loginName }
            //转换为：
            //.where(it -> {
            //    return it.loginName.match(loginName);
            //})
            reg_doWork(file, """^\s*\.where\s*\{\s*(\S+)\s+match\s+(\S+)\s*}\s*$""", {
                return@reg_doWork """.where( it-> {
    return ${it.groups[1]!!.value}.match( ${it.groups[2]!!.value} );
})""";
            });


            reg_doWork(file, """(\w+)\.ToJson\(\)""", {
                return@reg_doWork """MyJsonUtil.toJson(${it.groups[1]!!.value})""";
            });

            reg_doWork(file, """override\s+fun\s+""", {
                return@reg_doWork """@Override\n fun """;
            });


//            reg_doWork(file, """^\s*annotation\s+class\s+(\w+)\s*\(([^)]+)\)\s*$""", {
//                var pGroup = it.groups.elementAt(2)!!;
//                var str = pGroup.value;
//                if (str.contains("<") || str.contains("@")) {
//                } else {
//                    str = str.split(",")
//                        .map {
//                            // String value() default "";
//                            var type = it.split(":").last().split("=").first().trim();
//                            var isArray = it.trim().startsWith("vararg");
//                            var p = it.split(":").first().trim().split(" ").last();
//                            var defAry = it.split(":").last().split("=");
//                            var def = "";
//                            if (defAry.size > 1) {
//                                def = """ default ${defAry.last()}"""
//                            }
//
//                            return@map """ ${type}${if (isArray) "[]" else ""} ${p}() ${def}; """
//                        }.joinToString("\n")
//                }
//                return@reg_doWork """public @interface ${it.groups[1]!!.value} {
//    ${str}
//}
//""";
//            });

            return;
        }

        file.listFiles()
            .forEach {
                reg_replace(it);
            }
    }

    private fun txt_doWork(file: File, findText: String, replaceText: String) {
        var content = file.readText(const.utf8);

        var txt = content.replace(findText, replaceText);
        if (content == txt) {
            return;
        }

        file.writeText(txt, const.utf8);
    }

    private fun reg_doWork(file: File, regText: String, transform: (MatchResult) -> CharSequence) {
        var content = file.readText(const.utf8);

        var reg = Regex(
            regText,
            RegexOption.MULTILINE
        )

        var txt = reg.replace(content, transform);
        if (content == txt) {
            return;
        }

        file.writeText(txt, const.utf8);
    }


    @Test
    fun a() {
        var ff = Regex("""^\s*val\s*(\w+)\s+get\s*\(\s*\)\s*\=\s*(\S+)""", RegexOption.MULTILINE).replace("""
val admin
    get() = AdminGroup();
""", {
            var pGroup = it.groups.elementAt(2)!!;
            var str = pGroup.value;
            if (str.contains("<")) {
            } else {
                str = str.split(",").map { it.split(":").asReversed().joinToString(" ") }.joinToString(",")
            }
            return@replace """public ${it.groups[2]!!.value} get${MyUtil.getBigCamelCase(it.groups[1]!!.value)} {
                    return ${str} ; 
            } """;
        })
        println(ff)
    }
}
