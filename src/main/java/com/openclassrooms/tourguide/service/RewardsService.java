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
	private double defaultProximityBuffer = 1000.00;
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
		logger.debug("Calculating user rewards for: {} ", user.getUserName());
		try {
			List<VisitedLocation> userVisitedLocations = user.getVisitedLocations().stream()
					.collect(Collectors.toList());
			List<Attraction> attractions = gpsUtilService.getAllAttractions().stream().collect(Collectors.toList());

			for (VisitedLocation visitedLocation : userVisitedLocations) {
				for (Attraction attraction : attractions) {
					if (user.getUserRewards().stream().filter(
							userReward -> userReward.attraction.attractionName.equals(attraction.attractionName))
							.count()==0) {
						this.calculateUserRewardsPoints(visitedLocation, attraction, user);
					}
				}
			}
			logger.debug("Rewards of user: {} succesfully calculated: {} ", user.getUserName(), user.getUserRewards());
		} catch (ConcurrentModificationException | InterruptedException | ExecutionException e) {
			logger.error("Failed to calculate user rewards for: {}, {}", user.getUserName(), e.getMessage());
		}
	}

	public void calculateUserRewardsPoints(VisitedLocation visitedLocation, Attraction attraction, User user)
			throws InterruptedException, ExecutionException {
		double distance = calculateDistance(visitedLocation.location, attraction);
		if (distance <= defaultProximityBuffer) {
			UserReward userReward = new UserReward(visitedLocation, attraction);
			getUserRewardPoints(userReward, attraction, user);
			user.addUserReward(userReward);
		}else if(distance <=5000.00){
			UserReward userReward = new UserReward(visitedLocation, attraction,10);
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

	public int calculateTotalRewardsPoints(User user) {
		logger.debug("Calculating total Rewards Points for: {} ", user.getUserName());
		return user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
	}
}