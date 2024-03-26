package com.openclassrooms.tourguide.service;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import lombok.Data;
import rewardCentral.RewardCentral;

@Data
@Service
public class RewardsService {
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
	private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	//private int attractionProximityRange = 200;
	private final  GpsUtilService  gpsUtilService ;
	private final RewardCentral rewardsCentral;
	private final  UserService userService ;
	

	public RewardsService(GpsUtilService gpsUtilService, RewardCentral rewardCentral, UserService userService) {
		this.gpsUtilService= gpsUtilService;
		this.rewardsCentral = rewardCentral;
		this.userService= userService;
	}

	//optimiser boucle avec fonction  native java ou stream
	public void calculateRewards(User user) {// erreur de ConcurrentModificationException lors de l appel de la methode
		try {
			List<VisitedLocation> userVisitedLocations = user.getVisitedLocations();
			List<Attraction> attractions = gpsUtilService.getAllAttractions();
			// boucles imbriquée lance erreur de ConcurrentModificationException (iteration
			// et modification lors de l iteration) et userRewards vide
			for (VisitedLocation visitedLocation : userVisitedLocations) {
				for (Attraction attraction : attractions) {// a debugger avec les point d arrêts conditionnel et getrewards()

					Stream<UserReward>listUserRewards = userService.getUserRewards(user).stream().filter(
							userReward -> userReward.attraction.attractionName.equals(attraction.attractionName));

					if (listUserRewards.count() == 0 && isNearAttraction(visitedLocation, attraction)) {
						user.addUserReward(
								new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));

					}
				}
			}
		} catch (ConcurrentModificationException e) {
			System.err.print("Error ConcurrentModificationException calculateRewards " + e.getMessage());
		}

	}
	
	/*
	// a implementer dans le tour guideService avec les 5 premier attraction proche 
	 du dernier lieu visité par l user, peur importe la distance et une methode getDistance d interface
	
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}*/

	private boolean isNearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		// ? methode propre au reward service avec une methode getDistance dinterface à
		// creer
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}

	public int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}

	// ?Utiliser une interface commune DistanceCalculator pour rewardService et
	// tourguide service avec une methode getDistance()
	//
	public double getDistance(Location loc1, Location loc2) {// ? à implementer dans un service GpsUtilService
		double lat1 = Math.toRadians(loc1.latitude);
		double lon1 = Math.toRadians(loc1.longitude);
		double lat2 = Math.toRadians(loc2.latitude);
		double lon2 = Math.toRadians(loc2.longitude);

		double angle = Math
				.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

		double nauticalMiles = 60 * Math.toDegrees(angle);
		double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
		return statuteMiles;
	}

}