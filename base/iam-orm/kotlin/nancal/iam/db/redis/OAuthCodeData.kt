package nancal.iam.db.redis

import nbcp.db.CodeName
import nancal.iam.db.mongo.*

data class OAuthCodeData(
    var type: UserSystemTypeEnum = UserSystemTypeEnum.TenantUser,
    var loginField: String = "",
    var loginName:String = "",
    var token: String = "",
    var userId: String,
    var app: CodeName = CodeName()
)