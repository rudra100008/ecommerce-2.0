package com.inventory_service;

import com.shared_library.Exceptions.SecurityExceptionHandler;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(exclude = {
        UserDetailsServiceAutoConfiguration.class
})
@ComponentScan(
        basePackages = {
                "com.inventory_service",
                "com.shared_library"
        },
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityExceptionHandler.class }
        )
)
@EnableFeignClients(basePackages = {
        "com.inventory_service"
})
@EnableScheduling
@ConfigurationPropertiesScan
@EnableAspectJAutoProxy
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

}
