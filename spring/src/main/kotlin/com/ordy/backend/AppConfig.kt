package com.ordy.backend

import com.ordy.backend.interceptors.AuthInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableAsync
@EnableScheduling
class AppConfig(
        var authInterceptor: AuthInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(listOf(
                        "/groups/**",
                        "/user/**",
                        "/locations/**",
                        "/orders/**"
                ))
                .excludePathPatterns("/auth/**")
    }
}