package com.openclassrooms.tourguide.service;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.utils.ICalculatorDistance;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.Data;
import rewardCentral.RewardCentral;

@Data
@Service
public class RewardsService implements ICalculatorDistance {

	// proximity in miles
	private int defaultProximityBuffer = 10;
	private final GpsUtilService gpsUtilService;
	private final RewardCentral rewardCentral;
	private UserService userService;
	private double distance;

	private ExecutorService executor = Executors.newFixedThreadPool(100000);

	public RewardsService(GpsUtilService gpsUtilService, RewardCentral rewardCentral) {
		this.gpsUtilService = gpsUtilService;
		this.rewardCentral = rewardCentral;

	}

	// optimiser boucle avec fonction native java ou stream
	public void calculateRewards(User user) {// erreur de ConcurrentModificationException lors de l appel de la methode
		try {
			List<VisitedLocation> userVisitedLocations = user.getVisitedLocations();
			List<Attraction> attractions = gpsUtilService.getAllAttractions();

			// boucles imbriquée lance erreur de ConcurrentModificationException (iteration
			// et modification lors de l iteration) et userRewards vide
			for (VisitedLocation visitedLocation : userVisitedLocations) {
				for (Attraction attraction : attractions) {// a debugger avec les point d arrêts conditionnel et
															// getrewards()

					Stream<UserReward> listUserRewards = user.getUserRewards().stream().filter(
							userReward -> userReward.attraction.attractionName.equals(attraction.attractionName));

					if (listUserRewards.count() == 0) {
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

		if (isNearAttraction(visitedLocation, attraction)) {
			UserReward userReward = new UserReward(visitedLocation, attraction, 0);
			getUserRewardPoints(userReward, attraction, user);
			user.addUserReward(userReward);
		}
	}

	public boolean isNearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		this.setDistanceAttractionAndVisitedLocation(visitedLocation, attraction);
		return distance > defaultProximityBuffer ? false : true;
	}

	public double setDistanceAttractionAndVisitedLocation(VisitedLocation visitedLocation, Attraction attraction) {
		return this.distance = calculateDistance(visitedLocation.location, attraction);

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
}