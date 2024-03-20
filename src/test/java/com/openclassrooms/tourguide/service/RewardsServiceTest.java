package com.openclassrooms.tourguide.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;

public class RewardsServiceTest {
	private GpsUtil gpsUtil;
	private RewardsService rewardsService;
	
	@BeforeEach
	public void init() throws Exception {
		gpsUtil = new GpsUtil();
		rewardsService = new RewardsService(gpsUtil, new RewardCentral());
	}

	@Test // A ajouter dans un test de UserService
	public void testUserGetRewards() {
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		userService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		userService.tracker.stopTracking();
		assertTrue(userRewards.size() == 1);
	}

	@Test
	public void testIsWithinAttractionProximity() {
		Attraction attraction = gpsUtil.getAttractions().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));//? deuxieme parametre doit etre un type Location et non attraction
	}

	//@Disabled // Needs fixed - can throw ConcurrentModificationException
	@Test
	public void testIsNearAttraction_WithAllAttractionsAndUserRewardsCalculated() throws InterruptedException{ //ajouter try/catch ConcurrentModificationException
	
		//try {
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);
		
		InternalTestHelper.setInternalUserNumber(1);
		UserService userService = new UserService(gpsUtil, rewardsService);

		rewardsService.calculateRewards(userService.getAllUsers().get(0));
		List<UserReward> userRewards = userService.getUserRewards(userService.getAllUsers().get(0));
		userService.tracker.stopTracking();

		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
		/*} catch (ConcurrentModificationException e) {
			System.err.print("Error ConcurrentModificationException " + e.getMessage());
		}*/
	}
}