package nancal.iam.client

import java.io.Serializable

/**
 * @Classname BaseResult
 * @Description TODO
 * @Version 1.0.0
 * @Date 4/12/2021 下午 4:02
 * @Created by kxp
 */
class BaseResult : Serializable {
    var code: Int? = null
    var msg: String? = null
    var data: Any? = null

    constructor() {}
    constructor(code: Int?, msg: String?, data: Any?) : super() {
        this.code = code
        this.msg = msg
        this.data = data
    }

    fun success(): Boolean {
        return 0 == code
    }

    companion object {
        private const val serialVersionUID = 310854062175746208L
        fun success(data: Any?): BaseResult {
            return BaseResult(0, "ok", data)
        }

        fun fail(code: Int?, msg: String?): BaseResult {
            return BaseResult(code, msg, null)
        }

        fun fail(code: Int?, msg: String?, data: Any?): BaseResult {
            return BaseResult(code, msg, data)
        }
    }
}
