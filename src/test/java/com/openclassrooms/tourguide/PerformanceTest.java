package com.openclassrooms.tourguide;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.openclassrooms.tourguide.dao.UserDaoImpl;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.service.GpsUtilService;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.UserService;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;

/*
 * A note on performance improvements:
 * 
 * The number of users generated for the high volume tests can be easily
 * adjusted via this method:
 * 
 * InternalTestHelper.setInternalUserNumber(100000);
 * 
 * These are performance metrics that we are trying to hit:
 * 
 * highVolumeTrackLocation: 100,000 users within 15 minutes:
 * assertTrue(TimeUnit.MINUTES.toSeconds(15) >=
 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
 *
 * highVolumeGetRewards: 100,000 users within 20 minutes:
 * assertTrue(TimeUnit.MINUTES.toSeconds(20) >=
 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
 */
//@TestMethodOrder(OrderAnnotation.class)
class PerformanceTest {

	private GpsUtilService gpsUtilService;
	private RewardsService rewardsService;
	private UserService userService;
	private List<User> allUsers;

	@BeforeEach
	void init() throws Exception {
		GpsUtil gpsUtil = new GpsUtil();
		gpsUtilService = new GpsUtilService(gpsUtil);
		userService = new UserService(new UserDaoImpl());
		allUsers = userService.getAllUsers();
	}

	// disable test because run code with 100 000 user , use test when it's needed to improve performance
	@Disabled
	@Test
	@DisplayName("With 100,000 users , test should be finish within 15 minutes")
	void testHighVolumeTrackLocation() throws Exception {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		allUsers.parallelStream().forEach(user -> {
			gpsUtilService.trackUserLocation(user, userService);
		});

		allUsers.forEach(user -> {
			CompletableFuture.supplyAsync(() -> user.getVisitedLocations().get(3))
					.thenAccept(visitedLocation -> assertNotNull(visitedLocation));
		});

		stopWatch.stop();
		userService.tracker.stopTracking();
		gpsUtilService.tracker.stopTracking();

		System.out.println("highVolumeTrackLocation: Time Elapsed: "
				+ TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + "seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

	// disable test because run code with 100 000 user , use test when it's needed to improve performance
	@Disabled
	@Test
	@DisplayName("With 100,000 users , test should be finish within 20 minutes")
	void testHighVolumeGetRewards() throws Exception {
		rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
		Attraction attraction = gpsUtilService.getAllAttractions().get(0);
		StopWatch stopWatch = new StopWatch();

		stopWatch.start();
		allUsers.forEach(user -> {
			user.clearVisitedLocations();
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		});

		allUsers.parallelStream().forEach(user -> rewardsService.calculateRewards(user));

		allUsers.forEach(user -> {
			assertTrue(user.getUserRewards().size() > 0);
		});

		stopWatch.stop();
		userService.tracker.stopTracking();
		gpsUtilService.tracker.stopTracking();
		rewardsService.tracker.stopTracking();

		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
				+ "seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
}