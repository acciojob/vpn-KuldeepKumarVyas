package com.driver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Main class for the VPN Spring Boot application.
 * This class initializes and runs the Spring Boot application.
 */
@EnableSwagger2 // Enables Swagger2 for API documentation
@SpringBootApplication // Marks this as a Spring Boot application
public class Vpn {

	/**
	 * Main method to run the Spring Boot application.
	 * @param args Command-line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(Vpn.class, args);
		System.out.println("VPN Application Started Successfully!");
	}
}
