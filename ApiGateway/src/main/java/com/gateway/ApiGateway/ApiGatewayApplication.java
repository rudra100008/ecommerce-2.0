package com.gateway.ApiGateway;

import com.shared_library.Exceptions.GlobalException;
import com.shared_library.Exceptions.SecurityExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "com.gateway.ApiGateway",
                "com.shared_library"
        },
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = { GlobalException.class, SecurityExceptionHandler.class }
        )
)
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
