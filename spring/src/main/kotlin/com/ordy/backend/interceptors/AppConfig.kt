package com.ordy.backend.interceptors

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.ordy.backend.database.View
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.*


@Configuration
class AppConfig: WebMvcConfigurer {

    @Autowired
    private lateinit var authInterceptor: AuthInterceptor

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