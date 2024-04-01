package com.openclassrooms.tourguide.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.openclassrooms.tourguide.dao.UserDaoImpl;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;

public class RewardsServiceTest {
	private GpsUtilService gpsUtilService;
	private RewardsService rewardsService;

	@BeforeEach
	public void init() throws Exception {
		GpsUtil gpsUtil = new GpsUtil();
		gpsUtilService = new GpsUtilService(gpsUtil);
		rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
	}

	@Test // A ajouter dans un test de UserService
	public void testUserGetRewards() throws InterruptedException, ExecutionException{
		UserService userService = new UserService(new UserDaoImpl());
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtilService.getAllAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		rewardsService.calculateRewards(user);
		List<UserReward> userRewards = userService.getUserRewards(user);
		
		rewardsService.tracker.stopTracking();
		assertTrue(userRewards.size() == 1);
	}
	
	@Test
	public void testIsNearAttraction_WithAllAttractionsAndUserRewardsCalculated() throws InterruptedException, ExecutionException { // ajouter
																												// try/catch																										// ConcurrentModificationException
		rewardsService.setDefaultProximityBuffer(Integer.MAX_VALUE);

		UserService userService = new UserService(new UserDaoImpl());
		rewardsService.calculateRewards(userService.getAllUsers().get(0));
		List<UserReward> userRewards = userService.getUserRewards(userService.getAllUsers().get(0));
		userService.tracker.stopTracking();

		assertEquals(gpsUtilService.getAllAttractions().size(), userRewards.size());
		
	}
}