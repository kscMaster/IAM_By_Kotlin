package nancal.iam.util

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * @ClassName DateUtils
 * @author xuebin
 * @version 1.0.0
 * @Description TODO
 * @createTime 2021年12月30日 19:13:00
 */
class DateUtils {
    companion object {
        /**
         * 计算两个日期之间相差多少分钟
         *
         * @param start 开始时间
         * @param end 结束时间
         * @return long
         */
        fun getDaysBetweenMinute(start: LocalDateTime, end: LocalDateTime): Long {
            val minutes = ChronoUnit.MINUTES.between(start, end)
            val hours = ChronoUnit.HOURS.between(start, end)
            return minutes + hours * 60
        }
    }
}