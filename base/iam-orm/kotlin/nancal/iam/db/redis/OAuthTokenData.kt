package nancal.iam.db.redis

import nancal.iam.db.mongo.UserSystemTypeEnum
import java.io.Serializable

data class OAuthTokenData(
    var type: UserSystemTypeEnum = UserSystemTypeEnum.TenantUser,
    var token: String = "",
    var userId: String
) : Serializable