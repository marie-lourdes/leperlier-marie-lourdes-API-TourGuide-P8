package com.openclassrooms.tourguide.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.service.TourGuideService;

@RestController
@RequestMapping("tourguide/user")
public class UserController {


	@Autowired
	TourGuideService tourGuideService;

	@PostMapping("/add")
	public void addUser() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourGuideService.addUser(user);

	}
}
