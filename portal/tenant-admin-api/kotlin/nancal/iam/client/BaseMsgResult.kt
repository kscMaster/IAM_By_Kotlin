package nancal.iam.client

import java.io.Serializable

class BaseMsgResult<T> : Serializable {

    var code: Int? = null
    var msg: String? = null
    var data: T? = null

    constructor() {}
    constructor(code: Int?, msg: String?, data: T?) : super() {
        this.code = code
        this.msg = msg
        this.data = data
    }

    fun success(): Boolean {
        return 0 == code
    }
}