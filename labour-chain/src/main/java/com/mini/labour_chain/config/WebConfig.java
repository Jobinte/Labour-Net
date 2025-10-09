package com.mini.labour_chain.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SessionSecurityInterceptor sessionSecurityInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(sessionSecurityInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/index", "/workers/login", "/workers/register", 
                                   "/agencies/login", "/agencies/register", "/admin/login",
                                   "/css/**", "/js/**", "/images/**", "/static/**", 
                                   "/contact", "/jobs", "/workers", "/agencies");
    }
}
