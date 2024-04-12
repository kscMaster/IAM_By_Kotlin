package nancal.iam.base.constant

import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * @Author wrk
 *
 * @Description
 * @Date 2022/2/9-17:16
 */
class SysConstants {
    companion object {

        /*-----------------------------------------------微信登录用--------------------------------------------------*/
        /*-----------------------------------------------微信登录用--------------------------------------------------*/
        /**
         * 消息类型
         */
        val MSG_TYPE = "MsgType"

        /**
         * 事件类型
         */
        val Event = "Event"

        /**
         * 用户唯一标识
         */
        val FROM_USER_NAME = "FromUserName"

        /**
         * 场景key
         */
        val EVENT_KEY = "EventKey"

        /**
         * 第一次关注前缀
         */
        val QRSCENE = "qrscene_"

        /**
         * 截取用
         */
        val CUT_OUT_EIGHT = 8

        /**
         * 搜索 searchValue
         */
        val CONTENT_SEARCH_VAL = 1

        val COURSE_SEARCH_VAL = 2

        val APP_SEARCH_VAL = 3

        /**
         * 专题合集里明细的类型:0文章，1课程，2附件
         */
        val DETAIL_CONTENT = 0

        val DETAIL_COURSE = 1

        val DETAIL_APPENDIX = 2

        /**
         * +1
         */
        val PLUS_ONE = 1

        /**
         * 来源前台
         */
        val SOURCE_FRONT = "front"

        /**
         * 是否关注和收藏
         */
        val IS_FOLLOW_OR_COLLECT = 1

        val NOT_FOLLOW_OR_COLLECT = 0

        /**
         * ADVERT副图类型
         */
        val ADVERT_TYPE_FIVE = 5

        /**
         * 小程序首页，专题类别
         */
        val APPLETS_SPECIAL_TOPIC = 1

        /**
         * 小程序首页，内容类别
         */
        val APPLETS_CONTENT = 2

        /**
         * 小程序首页，副图类别
         */
        val APPLETS_ADVERT = 3

        /**
         * 小程序首页，课程类型
         */
        val APPLETS_COURSE = 4

        /**
         * 小程序首页查询页
         */
        val APPLETS_PAGENO_ONE = 1

        /**
         * 小程序首页查询数量3
         */
        val APPLETS_PAGESIZE_THREE = 3

        /**
         * 小程序首页查询数量2
         */
        val APPLETS_PAGESIZE_TWO = 2

        /**
         * 短信返回数据
         */
        val SMS_2_BUSY = "发送短信太频繁，请稍后再试"

        val SMS_ERROR = "send:isv.BUSINESS_LIMIT_CONTROL,触发分钟级流控Permits"

        /**
         * 订单类型:普通订单
         */
        val COURSE_ORDER_TYPE_COMMON = "1"

        /**
         * 订单类型:VIP订单
         */
        val COURSE_ORDER_TYPE_VIP = "2"

        /**
         * 订单状态:正常
         */
        val COURSE_ORDER_STATUS_NORMAL = "1"

        /**
         * 订单状态:取消
         */
        val COURSE_ORDER_STATUS_CANCEL = "2"

        /**
         * 订单状态:完成
         */
        val COURSE_ORDER_STATUS_OK = "3"

        /**
         * 支付状态:待支付
         */
        val COURSE_PAY_STATUS_AWAIT = "1"

//    /**
//     * 支付状态:支付成功
//     */
//    public static final String COURSE_PAY_STATUS_OK = "2";

        //    /**
        //     * 支付状态:支付成功
        //     */
        //    public static final String COURSE_PAY_STATUS_OK = "2";
        /**
         * 支付状态:支付失败
         */
        val COURSE_PAY_STATUS_FAIL = "3"

        /**
         * 支付状态:取消支付
         */
        val COURSE_PAY_STATUS_CANCEL = "4"

        /**
         * 是否支持售后：是
         */
        val COURSE_AFTER_ABLE_Y = "1"

        /**
         * 是否支持售后：否
         */
        val COURSE_AFTER_ABLE_N = "2"

        /**
         * 评价状态：未评价
         */
        val COURSE_EVALUTION_STATUS_N = "1"

        /**
         * 评价状态：已评价
         */
        val COURSE_EVALUTION_STATUS_Y = "2"

        /**
         * 是否开发票：否
         */
        val COURSE_INVOICE_N = "1"

        /**
         * 是否开发票：是
         */
        val COURSE_INVOICE_Y = "2"

        /**
         * 退款状态：正常
         */
        val COURSE_REFUND_STATUS_NORMAL = "1"

        /**
         * 退款状态：申请退款
         */
        val COURSE_REFUND_STATUS_APPLY = "2"

        /**
         * 退款状态：退款中
         */
        val COURSE_REFUND_STATUS_ING = "3"

        /**
         * 退款状态：退款完成
         */
        val COURSE_REFUND_STATUS_OK = "4"

        /**
         * 用于关注的map返回值
         */
        val IS_FOLLOW = "isFollow"

        val IS_BOTH = "isBoth"

        /**成功 */
        val SUCCESS = "SUCCESS"

        /**失败 */
        val FAIL = "FAIL"

        /**
         * 查询热门文章的数量
         */
        val CONTENT_HOT_NUMBER = 5

        /**
         * url
         * 前缀 系统变量的enum
         */
        val CONTENT_URL_ENUM_VAL = 0

        val COURSE_URL_ENUM_VAL = 1

        val TOPIC_URL_ENUM_VAL = 2

        /**
         * 学习状态
         */
        val STUDY_NOT_YET = 0

        val STUDY_ING = 1

        val STUDY_ED = 2

        /**
         * 付费类型-收费
         */
        val UN_FREE = 0

        /**
         * 付费类型-会员免费
         */
        val VIP_FREE = 1

        /**
         * 付费类型-免费
         */
        val ALL_FREE = 2

        /**
         * 默认名称
         */
        val DEFAULT_NAME = "游客"

        /**
         * 作者最近文章列表查询数量
         */
        val CONTENT_AUTHOR_NUM = 3

        /**
         * 视频播放结束状态：0未结束，1结束
         */
        val VIDEO_UN_FINISH = 0

        val VIDEO_FINISH = 1

        /**
         * 数据不存在
         */
        val DATA_NOT_EXIST = 4004

        /**
         * 错误提示
         */
        val ERROR_MESSAGE = 6001

        /**
         * String 0
         */
        val STRING_ZERO = "0"

        /**
         * 课程试看
         */
        val COURSE_TRY = 1

        /**
         * 标签被选中
         */
        val SELECTED = 1

        /**
         * 标签未被选中
         */
        val UN_SELECTED = 0

        /**
         * 点击操作   0确认
         */
        val CLICK_CONFIRM = 0

        /**
         * 点击操作   1取消
         */
        val CLICK_CANCEL = 1
        //新增
        //新增
        /**
         * 区分订单类型
         */
        val DIFF_ORDER_TYPE_COURSE = "1"
        val DIFF_ORDER_TYPE_VIP = "2"
        val DIFF_ORDER_TYPE_PRODUCT = "3"
        val DIFF_ORDER_TYPE_KNOWLEDGE = "4"

        /**
         * 区分支付渠道
         */
        val PAY_CHANNEL_WX = "1"
        val PAY_CHANNEL_ZFB = "2"
        val PAY_CHANNEL_YL = "3"


        /**
         * 支付状态:待支付
         */
        val PAY_STATUS_AWAIT = "1"

        /**
         * 支付状态:支付超时
         */
        val PAY_STATUS_TIMEOUT = "2"

        /**
         * 支付状态:支付失败
         */
        val PAY_STATUS_FAIL = "3"

        /**
         * 支付状态:取消支付
         */
        val PAY_STATUS_CANCEL = "4"

        /**
         * 支付状态:支付成功
         */
        val PAY_STATUS_OK = "5"

        var PAY_STATUS_list = Stream.of(1, 2, 3, 4, 5).collect(Collectors.toList())


        /**
         * 交易类型：JSAPI -JSAPI支付（小程序支付），NATIVE -Native支付（二维码支付），APP -APP支付
         */
        val TRADE_TYPE_JSAPI = "JSAPI"
        val TRADE_TYPE_NATIVE = "NATIVE"
        val TRADE_TYPE_APP = "APP"

        /**
         * long类型-1
         */
        val LONG_MINUS_ONE = -1L

        /**
         * 加密方式
         */
        val PAY_AES = "1"

        /**
         * 订单来源
         */
        val ORDER_SOURCE_WEB = "web"
        val ORDER_SOURCE_APPLETS = "applets"
        val ORDER_SOURCE_H5 = "H5"


        /**
         * 会员来源
         */
        val VIP_BUY = 1
        val VIP_CARD = 2

        /**
         * url第一个路由
         */
        val URL_INDEX = "/zao"

        /**
         * 通知类型
         */
        val NOTIFY_DELETE = 3
    }
}