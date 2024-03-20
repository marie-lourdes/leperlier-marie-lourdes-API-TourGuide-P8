package com.openclassrooms.tourguide.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.tracker.Tracker;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;

class UserServiceTest {
	private GpsUtilService  gpsUtilService;
	private RewardsService rewardsService;
	
	@BeforeEach
	public void init() throws Exception {
		GpsUtil gpsUtil = new GpsUtil();
		gpsUtilService = new GpsUtilService(gpsUtil);
		rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
		
	}
	
	@Test
	public void testAddUser() throws Exception {
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(rewardsService,gpsUtilService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		 userService.addUser(user);
		 userService.addUser(user2);

		User retrivedUser =  userService.getUser(user.getUserName());
		User retrivedUser2 =  userService.getUser(user2.getUserName());
		Tracker tracker= new Tracker(userService);
		tracker.stopTracking();

		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}

	@Test
	public void testGetAllUsers() throws Exception {		
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(rewardsService,gpsUtilService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userService.addUser(user);
		userService.addUser(user2);

		List<User> allUsers = userService.getAllUsers();

		Tracker tracker= new Tracker( userService);
		tracker.stopTracking();

		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}
	
	@Test
	public void addUser() {
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(rewardsService,gpsUtilService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userService .addUser(user);
		userService .addUser(user2);

		User retrivedUser = userService.getUser(user.getUserName());
		User retrivedUser2 = userService.getUser(user2.getUserName());

		Tracker tracker= new Tracker( userService);
		tracker.stopTracking();

		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}

	@Test
	public void getAllUsers() {	
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService =new UserService(rewardsService,gpsUtilService);

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
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(rewardsService,gpsUtilService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = userService.getUserLocation(user);
		userService.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}

	@Test
	public void testTrackUserLocation() throws Exception {
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(rewardsService,gpsUtilService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = userService.trackUserLocation(user);

		userService.tracker.stopTracking();

		assertEquals(user.getUserId(), visitedLocation.userId);
	}
}
