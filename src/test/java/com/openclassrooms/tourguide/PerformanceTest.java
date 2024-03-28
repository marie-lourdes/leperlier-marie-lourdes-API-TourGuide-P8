package com.openclassrooms.tourguide;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.service.GpsUtilService;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.UserService;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;

public class PerformanceTest {
	private GpsUtilService gpsUtilService;
	private RewardsService rewardsService;
	private UserService userService;

	@BeforeEach
	public void init() throws Exception {
		GpsUtil gpsUtil = new GpsUtil();
		gpsUtilService = new GpsUtilService(gpsUtil);
		userService = new UserService();
		
	}

	/*
	 * A note on performance improvements:
	 * 
	 * The number of users generated for the high volume tests can be easily
	 * adjusted via this method:
	 * 
	 * InternalTestHelper.setInternalUserNumber(100000);
	 * 
	 * 
	 * These tests can be modified to suit new solutions, just as long as the
	 * performance metrics at the end of the tests remains consistent.
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

	// @Disabled
	@Test
	public void testHighVolumeTrackLocation() throws Exception {
		// Users should be incremented up to 100,000, and test finishes within 15
		// minutes
		List<User> allUsers= userService.getAllUsers();
		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		allUsers.parallelStream().forEach(user -> {
			try {
				gpsUtilService.trackUserLocation(user, userService);
			} catch (ConcurrentModificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		for (User user : allUsers) {

			while (user.getVisitedLocations().size() < 4) {
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					gpsUtilService.tracker.stopTracking();
					break;
				}
			}
			VisitedLocation visitedLocation = user.getVisitedLocations().get(3);
			assertTrue(visitedLocation != null);
		}

		/*
		 * for (User user : allUsers) { VisitedLocation visitedLocation =
		 * user.getVisitedLocations().get(3); assertTrue(visitedLocation != null); }
		 */
		userService.tracker.stopTracking();
		gpsUtilService.tracker.stopTracking();
		stopWatch.stop();

		System.out.println("highVolumeTrackLocation: Time Elapsed: "
				+ TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

	//@Disabled
	@Test
	public void testHighVolumeGetRewards() throws Exception {
		// Users should be incremented up to 100,000, and test finishes within 20 minutes
		List<User> allUsers= userService.getAllUsers();
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
			while (user.getUserRewards().isEmpty()) {
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					rewardsService.tracker.stopTracking();
					break;
				}
			}
		});
		
		allUsers.forEach(user -> assertTrue(user.getUserRewards().size() >= 1));
		
		userService.tracker.stopTracking();
		gpsUtilService.tracker.stopTracking();
		rewardsService.tracker.stopTracking();
		stopWatch.stop();	
		
		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
				+ " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));

	/*	// Users should be incremented up to 100,000, and test finishes within 20
		// minutes
		rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
		StopWatch stopWatch = new StopWatch();
		Attraction attraction = gpsUtilService.getAllAttractions().get(0);
		
		stopWatch.start();

		allUsers.forEach(user -> {
			user.clearVisitedLocations();
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
			rewardsService.calculateRewards(user);

			while (user.getUserRewards().isEmpty()) {
				try {
					TimeUnit.MILLISECONDS.sleep(200);
				} catch (InterruptedException e) {
					gpsUtilService.tracker.stopTracking();
					break;
				}
			}
		});

		userService.tracker.stopTracking();
		rewardsService.tracker.stopTracking();
		stopWatch.stop();
	
	   allUsers.forEach(user -> {assertTrue(user.getUserRewards().size() >= 1);});
		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
				+ " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(30) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));*/
	}
}