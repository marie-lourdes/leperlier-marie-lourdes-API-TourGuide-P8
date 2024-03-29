package com.openclassrooms.tourguide.service;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.utils.ICalculatorDistance;
import com.openclassrooms.tourguide.utils.Tracker;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.Data;
import rewardCentral.RewardCentral;

@Data
@Service
public class RewardsService implements ICalculatorDistance {
	private static final Logger logger = LogManager.getLogger(RewardsService.class);
	// proximity in miles
	private int defaultProximityBuffer = 10;
	private final GpsUtilService gpsUtilService;
	private final RewardCentral rewardCentral;
	public final Tracker tracker;
	private ExecutorService executor = Executors.newFixedThreadPool(100000);

	public RewardsService(GpsUtilService gpsUtilService, RewardCentral rewardCentral) {
		this.gpsUtilService = gpsUtilService;
		this.rewardCentral = rewardCentral;
		tracker = new Tracker("Thread-3-RewardsService");
		tracker.addShutDownHook();
		logger.debug("Shutdown RewardsService");

	}

	public void calculateRewards(User user) {
		try {
			List<VisitedLocation> userVisitedLocations = user.getVisitedLocations().stream()
					.collect(Collectors.toList());
			List<Attraction> attractions = gpsUtilService.getAllAttractions().stream().collect(Collectors.toList());

			for (VisitedLocation visitedLocation : userVisitedLocations) {
				for (Attraction attraction : attractions) {
					if (user.getUserRewards().stream().filter(
							userReward -> userReward.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
						this.calculateUserRewards(visitedLocation, attraction, user);
					}
				}
			}
		} catch (ConcurrentModificationException | InterruptedException | ExecutionException e) {
			System.err.print(e.getMessage());
		}
	}

	public void calculateUserRewards(VisitedLocation visitedLocation, Attraction attraction, User user)
			throws InterruptedException, ExecutionException {
		double distance = calculateDistance(visitedLocation.location, attraction);
		if (distance <= defaultProximityBuffer) {
			UserReward userReward = new UserReward(visitedLocation, attraction, 0);
			getUserRewardPoints(userReward, attraction, user);
			user.addUserReward(userReward);
		}
	}

	public int getUserRewardPoints(UserReward userReward, Attraction attraction, User user)
			throws InterruptedException, ExecutionException {
		CompletableFuture.supplyAsync(() -> {
			return rewardCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
		}, executor).thenAccept(points -> {
			userReward.setRewardPoints(points);
		});
		return userReward.getRewardPoints();
	}

	public int getAttractionRewardPoints(Attraction attraction, User user) {
		return rewardCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}
	
	/*private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Shutdown UserService");
				tracker.stopTracking();
			}
		});
	}*/
}