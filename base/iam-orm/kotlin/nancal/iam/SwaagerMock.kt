package io.swagger.annotations

annotation class Api(val value: String = "", val description: String = "", vararg val tags: String)
annotation class ApiOperation(val value: String = "", val description: String = "", vararg val tags: String)
