package com.honeypot.common.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ReadableRequestWrapperFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // Do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ReadableRequestWrapper wrapper = new ReadableRequestWrapper((HttpServletRequest) request);
        chain.doFilter(wrapper, response);
    }

    @Override
    public void destroy() {
        // Do nothing
    }
}