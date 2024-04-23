package com.epam.resourceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ResourceserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResourceserviceApplication.class, args);
	}

}
