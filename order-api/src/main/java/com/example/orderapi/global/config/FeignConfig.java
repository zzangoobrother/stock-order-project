package com.example.orderapi.global.config;

import com.example.orderapi.global.interceptor.FeignCookieInterceptor;
import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableFeignClients(basePackages = "com.example.orderapi")
@Import(FeignClientsConfiguration.class)
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(1000, 2000, 3);
    }

    @Bean
    public RequestInterceptor feignCookieInterceptor() {
        return new FeignCookieInterceptor();
    }
}
