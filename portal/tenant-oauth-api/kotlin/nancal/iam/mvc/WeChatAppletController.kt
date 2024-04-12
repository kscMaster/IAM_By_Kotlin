package nancal.iam.mvc

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @Author wrk 微信小程序授权登录sass
 *
 * @Description
 * @Date 2022/2/15-12:47
 */
@RestController
@RequestMapping("/weChatApplet")
class WeChatAppletController {
    @PostMapping("/tempQRcodeUrl")
    fun getTempQRcodeUrl(): Any? {

        return "成功"
    }
}