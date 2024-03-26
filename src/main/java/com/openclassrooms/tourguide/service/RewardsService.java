package com.openclassrooms.tourguide.service;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.utils.ICalculatorDistance;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import lombok.Data;
import rewardCentral.RewardCentral;

@Data
@Service
public class RewardsService implements ICalculatorDistance {

	// proximity in miles
	private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private final GpsUtilService gpsUtilService;
	private final RewardCentral rewardCentral;
	private UserService userService;
	private double distance;
	private int rewardPoints;

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
						this.calculateUserRewardsPoints(visitedLocation, attraction, user);
					}
				}
			}
		} catch (ConcurrentModificationException e) {
			System.err.print("Error ConcurrentModificationException calculateRewards " + e.getMessage());
		}

	}

	public void calculateUserRewardsPoints(VisitedLocation visitedLocation, Attraction attraction, User user) {
		rewardPoints = 0;
		if (isNearAttraction(visitedLocation, attraction)) {
			user.addUserReward(new UserReward(visitedLocation, attraction, rewardPoints));
		}
	}

	public boolean isNearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		this.setDistanceAttractionAndVisitedLocation(visitedLocation, attraction);
		return distance > proximityBuffer ? false : true;
	}

	public double setDistanceAttractionAndVisitedLocation(VisitedLocation visitedLocation, Attraction attraction) {
		return this.distance = calculateDistance(visitedLocation.location, attraction);

	}

	public int getRewardPoints(Attraction attraction, User user) {
		return rewardCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}

	/*
	 * // a implementer dans le tour guideService avec les 5 premier attraction
	 * proche du dernier lieu visité par l user, peur importe la distance et une
	 * methode getDistance d interface
	 * 
	 * public boolean isWithinAttractionProximity(Attraction attraction, Location
	 * location) { return getDistance(attraction, location) >
	 * attractionProximityRange ? false : true; }
	 */

}