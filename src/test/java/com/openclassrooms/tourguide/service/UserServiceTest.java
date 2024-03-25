package com.openclassrooms.tourguide.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.openclassrooms.tourguide.model.User;

import gpsUtil.GpsUtil;

class UserServiceTest {
	private GpsUtilService gpsUtilService;
	private UserService userService;

	@BeforeEach
	public void init() throws Exception {
		GpsUtil gpsUtil = new GpsUtil();
		gpsUtilService = new GpsUtilService(gpsUtil);
		userService = new UserService();
	}

	@Test
	public void testAddUser() throws Exception {
		//UserService userService = new UserService(rewardsService,gpsUtilService);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userService.addUser(user);
		userService.addUser(user2);

		User retrivedUser = userService.getUser(user.getUserName());
		User retrivedUser2 = userService.getUser(user2.getUserName());
	
		userService.tracker.stopTracking();

		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}

	@Test
	public void testGetAllUsers() throws Exception {
		//UserService userService = new UserService(rewardsService,gpsUtilService);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
		userService.addUser(user);
		userService.addUser(user2);

		List<User> allUsers = userService.getAllUsers();
		userService.tracker.stopTracking();

		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}

	@Test
	public void testGetUserLocation() throws Exception {
		//UserService userService = new UserService(rewardsService,gpsUtilService);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		
		gpsUtilService.trackUserLocation(user, userService);
		while(user.getVisitedLocations().isEmpty()) {
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
			}
		}
		userService.tracker.stopTracking();
		
		assertTrue(user.getVisitedLocations().get(0).userId.equals(user.getUserId()));
	}

	/*@Test
	public void testTrackUserLocation() throws Exception {
		//UserService userService = new UserService(rewardsService,gpsUtilService);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		
	    gpsUtilService.trackUserLocation(user,userService);
		userService.tracker.stopTracking();
		VisitedLocation visitedLocation=userService.getUserLocation(user);
		assertEquals(user.getUserId(), visitedLocation.userId);
	}*/
}
