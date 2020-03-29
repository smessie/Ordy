package com.ordy.backend.interceptors

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class AppConfig: WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(AuthInterceptor())
                .addPathPatterns(listOf(
                        "/groups/**",
                        "/user/**",
                        "/locations/**",
                        "/orders/**"
                ))
                .excludePathPatterns("/auth/**")
    }
}