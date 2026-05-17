package com.gateway.ApiGateway;

import com.shared_library.Config.FeignInternalSecretInterceptor;
import com.shared_library.Exceptions.GlobalException;
import com.shared_library.Exceptions.SecurityExceptionHandler;
import com.shared_library.Security.InternalSecretFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

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
