package nancal.iam.db.redis

data class OAuthFreshTokenData(var token: String, var expriein: Long = 259200)
