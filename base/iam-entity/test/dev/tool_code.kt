package nancal.iam.dev

//import com.nancal.cipher.RSARawUtil
import nancal.iam.TestBase
import org.junit.jupiter.api.Test
import nbcp.comm.*
import nbcp.db.CodeValue
import nbcp.utils.MyUtil
import java.io.File
import java.io.FileWriter


class toolCode : TestBase() {
    @Test
    fun Kt2Java() {
        var basePath = Thread.currentThread().contextClassLoader.getResource("./").path.split("/target/")[0];

        var path = MyUtil.joinFilePath(basePath, "../iam-entity/kotlin")

        kt2Java(File(path), File(path));
    }

    private fun kt2Java(file: File, baseFile: File) {
        /**
         * 过程：
         * 1. 遍历
         * 2. 分析： 包，类，拆类。
         * 3. 写 Java 文件
         * 4. 使用已知形式 对Java文件进行替换。
         */

        if (file.exists() == false) return;

        if (file.isFile) {
            kt2Java_doWork(file, baseFile);
            return;
        }

        file.listFiles()
            .forEach {
                kt2Java(it, baseFile);
            }
    }

    private fun kt2Java_doWork(file: File, baseFile: File) {
        if (file.extension != "kt") return;

        var pack = getPackageAndClasses(file);
        if (pack.packageName.isEmpty()) {
            println("!找不到包,跳过: ${file.FullName} ")
            return;
        }

        writeFile(baseFile, pack);
        file.delete();
    }

    private fun writeFile(baseFile: File, pack: PackageClassInfo) {
        if (pack.packageName.isEmpty()) {
            println("找不到packageName,跳过.")
            return;
        }

        pack.classes.forEach { cls ->
            var fileName =
                MyUtil.joinFilePath(
                    baseFile.FullName,
                    pack.packageName.split(".").joinToString("/"),
                    cls.code + ".java"
                );
            var file = File(fileName);
            if (file.exists()) {
                println("目标文件存在，跳过，${file}")
                return;
            }

            if (file.parentFile.exists() == false) {
                file.parentFile.mkdirs();
            }

            println("生成：${fileName}")
            FileWriter(fileName, false).use { fw ->
                if (pack.packageRemark.HasValue) {
                    fw.write(pack.classRemark + "\n")
                }
                fw.write("package " + pack.packageName + ";" + "\n");

                if (pack.imports.HasValue) {
                    fw.write(pack.imports.split("\n").map {
                        if (it.endsWith(";", false)) {
                            return@map it;
                        } else {
                            return@map it + ";"
                        }
                    }.joinToString("\n") + "\n")
                }

                fw.write(cls.value);
            }
        }
    }

    private fun getPackageAndClasses(file: File): PackageClassInfo {
        var ret = PackageClassInfo();
        var lines = file.readLines(const.utf8);
        var pack_index = lines.indexOfFirst { line -> line.startsWith("package ") }
        if (pack_index < 0) return ret;

        ret.packageName = lines[pack_index].Slice("package ".length).trimEnd(';');
        ret.packageRemark = lines.Slice(0, pack_index).joinToString("\n")

        lines = lines.Slice(pack_index + 1);

        var import_end_index = lines.indexOfLast { line -> line.startsWith("import ") }
        if (import_end_index > 0) {
            ret.imports = lines
                .Slice(pack_index + 1, import_end_index + 1)
                .map {
                    if (it.endsWith(";")) return@map it;
                    return@map it + ";"
                }
                .joinToString("\n")

            lines = lines.Slice(import_end_index + 1);
        }

        var content = lines.joinToString("\n")

        //多类
        var reg = Regex(
            """^(public|private|open)*\s*(abstract|)*\s*(data|enum|annotation|)*\s*(class|interface|object)\s+(\w+)\b""",
            RegexOption.MULTILINE
        )

        var matches = reg.findAll(content).toList()
        if (matches.any() == false) {

            var item = CodeValue();
            item.code = file.name.replace(".kt", "Kt");
            item.value = lines.joinToString("\n")

            ret.classes.add(item);
            println("自动生成类: ${item.code} !找不到类: ${file.FullName}")
            return ret;
        }

        var clsEndPoints = mutableSetOf<Int>()

        matches.forEachIndexed { index, matchResult ->
            if (matchResult.value.startsWith(" ") || matchResult.value.startsWith("\t")) {
                return@forEachIndexed
            }

            var item = CodeValue();
            item.code = matchResult.groupValues.last();

            if (index > 0) {
                var sect = content.Slice(0, matchResult.range.start)
                var tailIndex = getTailIndex(sect);

                clsEndPoints.add(tailIndex);
            }

            ret.classes.add(item);
        }

        var prevIndex = 0;
        ret.classes.forEachIndexed { index, codeValue ->
            if (index < ret.classes.size - 1) {
                codeValue.value = content.Slice(prevIndex, clsEndPoints.elementAt(index));
                prevIndex = clsEndPoints.elementAt(index);
            } else {
                codeValue.value = content.Slice(clsEndPoints.lastOrNull() ?: 0);
            }

        }

        return ret;
    }

    fun getTailIndex(sect: String): Int {
        var lines = sect.split("\n");
        var index = lines.indexOfLast aa@{
            var v = it.trim();
            if (v.startsWith("//")) {
                return@aa false
            }

            if (v.startsWith("/*") || v.startsWith("*/") || v.startsWith('*')) {
                return@aa false
            }

            if (v.startsWith("@")) {
                return@aa false;
            }

            if (v.isEmpty()) {
                return@aa false;
            }


            if (v.endsWith("}", false)) {
                return@aa true;
            }

            if (v.endsWith(")", false)) {
                return@aa true;
            }

            return@aa false;
        }

        return lines.Slice(0, index + 1).map { it.length }.sum() + index + 1;
    }
}


data class PackageClassInfo(
    var packageRemark: String = "",
    var packageName: String = "",
    var imports: String = "",
    var classRemark: String = "",
    var classes: MutableList<CodeValue> = mutableListOf()
)
