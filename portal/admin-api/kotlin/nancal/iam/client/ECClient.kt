package nancal.iam.client

import org.springframework.cloud.openfeign.FeignClient
import nancal.iam.client.ECClientFallbackFactory
import org.springframework.web.bind.annotation.PostMapping
import nancal.iam.client.BaseResult

/**
 * @Classname ECClient
 * @Description sync ec apis
 * @Version 1.0.0
 * @Date 4/12/2021 上午 11:40
 * @Created by kxp
 */
@FeignClient(value = "ec-levault", fallback = ECClientFallbackFactory::class)
interface ECClient {

    @PostMapping("applicaiton/getApplicationsByPlatform")
    fun getApplicationsByPlatform(): BaseResult

    @PostMapping("applicaiton/getApplicationsByPlatformV2")
    fun getApplicationsByPlatformV2(): BaseResult
}
