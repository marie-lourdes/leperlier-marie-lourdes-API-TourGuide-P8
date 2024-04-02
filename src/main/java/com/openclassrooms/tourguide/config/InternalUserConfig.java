package com.openclassrooms.tourguide.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.openclassrooms.tourguide.dao.UserDaoImpl;
import com.openclassrooms.tourguide.service.UserService;

@Configuration
public class InternalUserConfig {

	@Bean
	UserService userServiceTest() {
		return new UserService(new UserDaoImpl());
	}
}
