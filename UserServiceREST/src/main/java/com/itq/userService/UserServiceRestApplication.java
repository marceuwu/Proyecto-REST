package com.itq.userService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class UserServiceRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceRestApplication.class, args);
	}

}
