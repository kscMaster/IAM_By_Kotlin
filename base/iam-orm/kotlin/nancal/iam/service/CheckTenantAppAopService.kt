package nancal.iam.service

import nbcp.comm.ListResult
import nbcp.comm.Require
import nbcp.comm.usingScope
import nbcp.db.cache.getTableName
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nbcp.scope.IScopeData
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service


class CheckTenantAppStatusScope : IScopeData


@Aspect
@Component
class CheckTenantAppAopService {
    @Around("@within(nancal.iam.annotation.CheckTenantAppStatus)")
    fun check(joinPoint: ProceedingJoinPoint): Any? {
//        val signature = joinPoint.signature as MethodSignature;

        val args = joinPoint.args
        usingScope(CheckTenantAppStatusScope()) {
            return joinPoint.proceed(args)
        }
    }
}