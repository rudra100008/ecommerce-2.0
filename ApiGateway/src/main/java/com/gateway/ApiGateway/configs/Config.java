package com.gateway.ApiGateway.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;


@Component
public class Config {
    @Bean
    public AntPathMatcher antPathMatcher(){
        return new AntPathMatcher();
    }
}
