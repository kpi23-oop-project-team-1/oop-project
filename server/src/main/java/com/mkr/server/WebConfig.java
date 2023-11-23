package com.mkr.server;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.*;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(@NotNull CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedHeaders("*")
            .allowedOriginPatterns("*")
            .allowedMethods("POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS", "DELETE", "GET")
            .allowCredentials(true);
    }

    @Override
    public void addViewControllers(@NotNull ViewControllerRegistry registry) {
        registry.addStatusController("/api/**", HttpStatus.BAD_REQUEST);
        registry.addViewController("/**").setViewName("forward:/index.html");

        registry.setOrder(1000);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**")
            .addResourceLocations("/assets/");

        registry.addResourceHandler("/static/*.js", "/static/*.css")
            .addResourceLocations("classpath:/static/");

        registry.addResourceHandler("/index.html")
            .addResourceLocations("classpath:/public/index.html");

        registry.setOrder(-1000);
    }
}