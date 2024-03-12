package com.openclassrooms.tourguide.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.openclassrooms.tourguide.config.UserDataLoader;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.model.RecommendedUserAttractions;
import com.openclassrooms.tourguide.model.User;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tripPricer.Provider;

public class TourGuideServiceTest {
	private GpsUtil gpsUtil;
	private RewardsService rewardsService;
	private UserDataLoader userDataLoader;

	@BeforeEach
	public void init() throws Exception {
		gpsUtil = new GpsUtil();
		rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		userDataLoader = new UserDataLoader();
	}

	@Test
	public void testGetUserLocation() throws Exception {
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, userDataLoader);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}

	@Test
	public void testTrackUserLocation() throws Exception {
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, userDataLoader);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	// @Disabled // Not yet implemented
	@Test
	public void testGetNearbyAttractions() throws Exception {
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, userDataLoader);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		List<RecommendedUserAttractions> attractions = tourGuideService.getNearByAttractions(visitedLocation, user);

		tourGuideService.tracker.stopTracking();

		assertEquals(5, attractions.size());
	}

	public void testGetTripDeals() throws Exception {
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, userDataLoader);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideService.getTripDeals(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(10, providers.size());
	}

}
