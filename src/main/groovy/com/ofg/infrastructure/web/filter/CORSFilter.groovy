package com.ofg.infrastructure.web.filter

import groovy.transform.TypeChecked

import javax.servlet.*
import javax.servlet.http.HttpServletResponse

/**
 * This filter allows Cross-Origin Resource Sharing, so that rest endpoints can be called in browsers from different domains
 */
@TypeChecked
class CORSFilter implements Filter {
    @Override
    void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res
        response.setHeader("Access-Control-Allow-Origin", "*")
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
        response.setHeader("Access-Control-Max-Age", "3600")
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with")
        chain.doFilter(req, res)
    }

    @Override
    void init(FilterConfig filterConfig) {}

    @Override
    void destroy() {}
}
