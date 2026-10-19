package com.user.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = {
        "com.user.user_service",
        "com.shared_library"
})
@EnableAspectJAutoProxy
@EnableFeignClients
public class UserServiceApplication {

	public static void main(String[] args) {
		System.out.println("INTERNAL SECRET KEY exitst:" + (System.getenv("INTERNAL_SECRET_KEY") != null));
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
