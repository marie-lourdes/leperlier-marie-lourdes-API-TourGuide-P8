package com.openclassrooms.tourguide.controller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.tourguide.helper.InternalUserHistoryLocationTestHelper;
import com.openclassrooms.tourguide.helper.InternalUserPreferenceTestHelper;
import com.openclassrooms.tourguide.helper.InternalUserRewardsTestHelper;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.service.UserService;

//For testing Tourguidecontroller, add default user without internalUserMap
@RestController
@RequestMapping("tourguide/user")
public class UserController {
	private final static Logger logger = LoggerFactory.getLogger(UserController.class);
	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/testing/add")
	public User addUser(User user) {
		logger.debug("adding user");
		try {
			userService.addUser(user);
			return user;
		} catch (Exception e) {
			logger.error("Failed to creating user for test{}", e.getMessage());
			return new User();

		}
	}

	@GetMapping("/testing/getUser")
	public User getOneUser(@RequestParam String userName) throws InterruptedException, ExecutionException {
		logger.debug("testing add user");
		try {
			return userService.getUser(userName);
		} catch (Exception e) {
			logger.error("Failed to creating user for test{}", e.getMessage());
			return new User();
		}
	}
}