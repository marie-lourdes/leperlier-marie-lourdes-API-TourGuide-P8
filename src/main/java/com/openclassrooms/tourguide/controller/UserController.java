package com.openclassrooms.tourguide.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.tourguide.UserServiceFactory;
import com.openclassrooms.tourguide.UserServiceFactory.UserServiceMode;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.service.UserService;

@RestController
@RequestMapping("tourguide/user")
public class UserController {
	private UserService userService;

	public UserController() {
		this.userService = UserServiceFactory.create(UserServiceMode.TEST);
	}

	@PostMapping("/add")
	public String addUser() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		userService.addUser(user);
		return "user added";

	}
}
