package org.example.backend.tokener;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InterceptorAll implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(InterceptorAll.class);
    private final AuthInterceptor authinterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {

        log.info("Request URI: {}", registry);
        registry.addInterceptor(authinterceptor)
                .addPathPatterns("/**")// 拦截所有请求
                .excludePathPatterns("/api/register", "/api/login", "/api",
                                     "/swagger-ui/**",                        // Swagger UI 资源
                                     "/v3/api-docs/**",                       // OpenAPI 文档
                                     "/swagger-resources/**",// Swagger 资源
                                     "/avatar/**", "/post/**",
                                     "/webjars/**");                            // Swagger 依赖的 webjars
    }

}
