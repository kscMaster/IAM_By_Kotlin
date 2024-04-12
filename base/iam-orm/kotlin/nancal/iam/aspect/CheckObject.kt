package nancal.iam.aspect

@Target(AnnotationTarget.FUNCTION)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class CheckObjects(vararg val value: CheckObject)

@Target(AnnotationTarget.FUNCTION)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@java.lang.annotation.Repeatable(CheckObjects::class)
@Repeatable
annotation class CheckObject(
    val path: String = "",
    val js: String = "",
    val msg: String = "",
    val reg: String = "",
    val require: Boolean = true
)