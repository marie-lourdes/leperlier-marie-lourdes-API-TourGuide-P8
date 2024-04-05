package com.openclassrooms.tourguide.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("tourguide/user")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/testing/add")
	public User addUser(@RequestBody User user, HttpServletResponse response) {
		logger.debug("adding user");
		try {
			userService.addUser(user);
			response.setStatus(201);
			return user;
		} catch (Exception e) {
			logger.error("Failed to creating user for test{}", e.getMessage());
			return new User();

		}
	}

	@GetMapping("/testing/getUser")
	public User getOneUser(@RequestParam String userName, HttpServletResponse response) throws IOException {
		logger.debug("testing add user");
		try {
			return userService.getUser(userName);
		} catch (NullPointerException e) {
			response.sendError(404);
			logger.error("Failed to creating user for test{}", e.getMessage());
			return new User();
		}
	}
}