package com.Lnn.filters;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * filterName 拦截器名称，urlPatterns拦截相关的请求
 */
@WebFilter(filterName = "myFilter", urlPatterns = "/*")
public class MyFilter implements Filter {


    /**
     *
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //这里可以做点事情
        filterChain.doFilter(servletRequest,servletResponse);//传到下一个拦截器
    }

    /**
     *  拦截器的初始化方法
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }
}
