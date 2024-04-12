package nancal.iam.db.redis

import nbcp.db.CodeName

data class LoginCodeData(
    var loginField: String = "",
    var loginName: String = "",
    var app: CodeName = CodeName(),
    var validTenantIds: List<String>
)