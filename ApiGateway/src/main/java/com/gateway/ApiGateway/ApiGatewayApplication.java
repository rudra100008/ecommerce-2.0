package com.gateway.ApiGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "com.gateway.ApiGateway",
                "com.shared_library.Utils"
        }
//        excludeFilters = @ComponentScan.Filter(
//                type = FilterType.ASSIGNABLE_TYPE,
//                classes = {
//                        GlobalException.class,
//                        SecurityExceptionHandler.class,
//                        InternalSecretFilter.class,
//                        FeignInternalSecretInterceptor.class
//                }
//        )
)
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
