package com.openclassrooms.tourguide.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.openclassrooms.tourguide.dao.UserDaoImpl;
import com.openclassrooms.tourguide.helper.InternalUserTestHelper;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;

class RewardsServiceTest {
	private GpsUtilService gpsUtilService;
	private RewardsService rewardsService;

	@BeforeEach
	void init() throws Exception {
		GpsUtil gpsUtil = new GpsUtil();
		gpsUtilService = new GpsUtilService(gpsUtil);
		rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
	}

	@Test
	void testGetUserRewards() throws InterruptedException, ExecutionException {
		UserService userService = new UserService(new UserDaoImpl());
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtilService.getAllAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));

		rewardsService.calculateRewards(user);
		List<UserReward> userRewards = userService.getUserRewards(user);

		rewardsService.tracker.stopTracking();
		assertEquals(1, userRewards.size());
	}

	@Test
	public void testGetUserRewards_WithAllAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		UserService userService = new UserService(new UserDaoImpl());
		rewardsService.setDefaultProximityInMiles(Integer.MAX_VALUE);

		InternalUserTestHelper.setInternalUserNumber(1);

		rewardsService.calculateRewards(userService.getAllUsers().get(0));
		List<UserReward> userRewards = userService.getUserRewards(userService.getAllUsers().get(0));
		userService.tracker.stopTracking();

		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
	}
}