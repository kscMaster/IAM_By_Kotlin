package nancal.iam.db.sql


enum class CheckResultEnum constructor(val value: Int, val remark: String) {
    Downloaded(0, "已下载"),
    OK(1, "账实一致"),
    Over(2, "盘盈"),
    Lose(3, "盘亏"),
    Diff(4, "账实不符"),
    NotSure(5, "待确认")
}

enum class BatchStatusEnum constructor(val value: String, val remark: String) {
    Created("01", "创建"),
    Edit("02", "修改"),
    Freeze("03", "冻结"),
    Close("04", "关闭"),
    Open("05", "打开")
}
