package com.openclassrooms.tourguide.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.model.RecommendedUserAttraction;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.tracker.Tracker;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tripPricer.Provider;

public class TourGuideServiceTest {
	private GpsUtil gpsUtil;
	private RewardsService rewardsService;

	@BeforeEach
	public void init() throws Exception {
		gpsUtil = new GpsUtil();
		rewardsService = new RewardsService(gpsUtil, new RewardCentral());
	}


	// @Disabled // Not yet implemented
	@Test
	public void testGetNearbyAttractions() throws Exception {
		InternalTestHelper.setInternalUserNumber(0);
		 UserService userService = new UserService(gpsUtil, rewardsService);
		 TourGuideService tourGuideService = new TourGuideService( rewardsService);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = userService.trackUserLocation(user);

		List<RecommendedUserAttraction> attractions = tourGuideService.getNearByAttractions(visitedLocation, user);
		Tracker trackerUser = new Tracker(userService);
		Tracker trackerTourguide = new Tracker(tourGuideService);
		 trackerUser.stopTracking();
		trackerTourguide.stopTracking();

		assertEquals(5, attractions.size());
	}

	public void testGetTripDeals() throws Exception {
		InternalTestHelper.setInternalUserNumber(0);
	 TourGuideService tourGuideService = new TourGuideService( rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideService.getTripDeals(user);
		
		Tracker trackerTourguide = new Tracker(tourGuideService);
		trackerTourguide.stopTracking();
		
		assertEquals(10, providers.size());
	}

}
