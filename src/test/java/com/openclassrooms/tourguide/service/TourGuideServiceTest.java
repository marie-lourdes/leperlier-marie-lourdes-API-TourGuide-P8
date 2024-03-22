package com.openclassrooms.tourguide.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.model.RecommendedUserAttraction;
import com.openclassrooms.tourguide.model.User;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tripPricer.Provider;

public class TourGuideServiceTest {
	private GpsUtilService gpsUtilService;
	private RewardsService rewardsService;

	@BeforeEach
	public void init() throws Exception {
		UserService userService = new UserService(rewardsService, gpsUtilService);
		GpsUtil gpsUtil = new GpsUtil();
		gpsUtilService = new GpsUtilService(gpsUtil);
		rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
	}

	// @Disabled // Not yet implemented
	@Test
	public void testGetNearbyAttractions() throws Exception {
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(rewardsService, gpsUtilService);
		TourGuideService tourGuideService = new TourGuideService(rewardsService);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = gpsUtilService.trackUserLocation(user,userService );

		List<RecommendedUserAttraction> attractions = tourGuideService.getNearByAttractions(visitedLocation, user);

		userService.tracker.stopTracking();
		tourGuideService.tracker.stopTracking();

		assertEquals(5, attractions.size());
	}

	public void testGetTripDeals() throws Exception {
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideService.getTripDeals(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(10, providers.size());
	}

}
