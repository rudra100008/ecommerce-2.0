package com.shared_library.Config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignInternalSecretInterceptor implements RequestInterceptor {
    @Value("${internal.secret-key}")
    private String internalSecret;
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("X-Internal-Secret",internalSecret);

        ServletRequestAttributes attributes =(ServletRequestAttributes)  RequestContextHolder.getRequestAttributes();

        if(attributes != null){
            HttpServletRequest request = attributes.getRequest();
            String userId = request.getHeader("X-User-Id");
            String userRole = request.getHeader("X-User-Role");
            String userEmail = request.getHeader("X-User-Email");

            if(userId != null) requestTemplate.header("X-User-Id",userId);
            if(userRole != null) requestTemplate.header("X-User-Role");
            if (userEmail != null) requestTemplate.header("X-User-Email");
        }
    }
}
