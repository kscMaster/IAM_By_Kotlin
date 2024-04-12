package nancal.iam.service.extra_auth

import nbcp.comm.usingScope
import nbcp.scope.IScopeData
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component


class CheckAuthResourceScope : IScopeData


@Aspect
@Component
class CheckAuthSourceAppAopService {
    @Around("@within(nancal.iam.annotation.CheckAuthSource)")
    fun check(joinPoint: ProceedingJoinPoint): Any? {
//        val signature = joinPoint.signature as MethodSignature;

        val args = joinPoint.args
        usingScope(CheckAuthResourceScope()) {
            return joinPoint.proceed(args)
        }
    }
}