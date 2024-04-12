package nancal.iam.base.config

import nbcp.comm.*
import nancal.iam.db.mongo.VarDatabaseTenantIdScope
import nbcp.base.mvc.*
import nbcp.web.*
import nbcp.base.mvc.*
import nbcp.web.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.servlet.*
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest

@WebFilter(urlPatterns = ["/*", "/**"])
open class VarDatabaseScopeFilter : Filter {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request is HttpServletRequest == false) {
            chain.doFilter(request, response)
            return;
        }

        var webRequest = request as HttpServletRequest
        var appCode = webRequest.findParameterStringValue("tenant-id");
        if (appCode.HasValue) {
            usingScope(VarDatabaseTenantIdScope(appCode)) {
                chain.doFilter(webRequest, response)
            }
            return;
        }
        chain.doFilter(webRequest, response)
    }
}