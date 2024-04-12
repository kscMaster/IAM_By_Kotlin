package nancal.iam.base.config

import nancal.iam.util.ResultUtils
import nbcp.base.mvc.IsOctetContent
import nbcp.base.mvc.WriteJsonRawValue
import nbcp.comm.*
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class ResultMsgInterceptor : HandlerInterceptor {


    @Resource
    lateinit var enConfig: AdminEnConfig

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        if (handler !is HandlerMethod) {
            return
        }
        if (response.IsOctetContent) {
            return;
        }

        if(request.getHeader("lang") ==null){
            return
        }
        var status = response.status
        var responseBody = (response as ContentCachingResponseWrapper).contentAsByteArray.toString(const.utf8)

        val responseJson = responseBody.FromJson<JsonMap>()!!


        var msg = ""
        var paramMsg = ""

        if (response.contentType.AsString().contains("json", true)) {

            if (request.getHeader("lang").toString().equals("cn")) {
                if (responseJson.get("code") != 0) {
                    msg = responseJson.get("msg").AsString().replace("{","").replace("}","")
                    responseJson.set("msg",msg)
                    response.reset()
                    response.status = status
                    response.WriteJsonRawValue(responseJson.ToJson())
                }
                return
            }

            // 英文处理
            var bundle =  enConfig.bundle

            if(responseJson.get("code") != 0){
                msg = responseJson.get("msg").AsString()

                if(!msg.contains("{")){

                    val langRes  = bundle.getString(msg)
                    if(langRes.equals("")){
                        responseJson.set("msg",msg)
                    }else {
                        responseJson.set("msg",langRes)
                    }

                } else{
                    paramMsg = ResultUtils.clearBracket(msg)
                    val langRes  = bundle.getString(paramMsg)

                    if(langRes.equals("")){
                        responseJson.set("msg",msg)
                    }else {
                        val fillBracket = ResultUtils.fillBracket(langRes, msg)
                        responseJson.set("msg",fillBracket)
                    }

                }
                response.reset()
                response.status = status
                response.WriteJsonRawValue(responseJson.ToJson())
            }
        }
        super.afterCompletion(request, response, handler, ex)
    }

}