package nancal.iam.annotation

import nbcp.db.Cn
import nancal.iam.db.mongo.*

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BizLog(
    @Cn("操作类型")
    val action: BizLogActionEnum = BizLogActionEnum.Define,
    @Cn("操作资源")
    val resource: BizLogResourceEnum = BizLogResourceEnum.Define,
    @Cn("业务模块")
    val module: String = ""
)


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CheckTenantAppStatus()


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CheckAuthSource()