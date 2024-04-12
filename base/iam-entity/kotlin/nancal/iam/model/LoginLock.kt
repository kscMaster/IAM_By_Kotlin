package nancal.iam.model

import java.time.LocalDateTime

/**
 * @ClassName LoginLock
 * @author xuebin
 * @version 1.0.0
 * @Description 密码输入错误时存入redis的对象
 * @createTime 2021年12月30日 14:16:00
 */
class LoginLock {
    /**
     * 密码错误次数
     */
    var count = 1

    /**
     * 最后一次锁定时间
     */
    var lockTime: LocalDateTime? = null

    var msg: String = ""

    var strongLock:Boolean=false
}