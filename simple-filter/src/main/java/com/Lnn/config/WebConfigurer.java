package com.Lnn.config;

import com.Lnn.interceptors.MyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(
                new MyInterceptor()
        ).addPathPatterns("/**")  //需要拦截的路径
         .excludePathPatterns("/login"); //需要放行的路径
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
