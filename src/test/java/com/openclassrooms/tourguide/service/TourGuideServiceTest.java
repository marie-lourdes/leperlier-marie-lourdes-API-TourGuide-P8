package com.openclassrooms.tourguide.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.openclassrooms.tourguide.dao.UserDaoImpl;
import com.openclassrooms.tourguide.helper.InternalUserPreferenceTestHelper;
import com.openclassrooms.tourguide.model.RecommendedUserAttraction;
import com.openclassrooms.tourguide.model.User;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tripPricer.Provider;

public class TourGuideServiceTest {
	private GpsUtilService gpsUtilService;
	private RewardsService rewardsService;
	private TourGuideService tourGuideService;

	@BeforeEach
	public void init() throws Exception {
		GpsUtil gpsUtil = new GpsUtil();
		gpsUtilService = new GpsUtilService(gpsUtil);
		rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
	}

	// @Disabled // Not yet implemented
	@Test
	public void testGetNearbyAttractions() throws Exception {
		UserService userService =new UserService(new UserDaoImpl());
		tourGuideService = new TourGuideService(rewardsService,gpsUtilService);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		
		gpsUtilService.trackUserLocation(user, userService);
		
		while(null== user.getLastVisitedLocation()) {
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
			}
		}
		
		VisitedLocation lastVisitedLocation = user.getLastVisitedLocation();

		List<RecommendedUserAttraction> attractions = tourGuideService.getNearByAttractions(lastVisitedLocation, user);
	
		assertEquals(5, attractions.size());
		gpsUtilService.tracker.stopTracking();
		tourGuideService.tracker.stopTracking();
	}

	@Test
	public void testGetTripDeals() throws Exception {
		tourGuideService = new TourGuideService(rewardsService, gpsUtilService);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		InternalUserPreferenceTestHelper.setUserPreference(user);

		List<Provider> providers = tourGuideService.getTripDeals(user);
	
		assertEquals(5, providers.size());
		tourGuideService.tracker.stopTracking();
	}

}
