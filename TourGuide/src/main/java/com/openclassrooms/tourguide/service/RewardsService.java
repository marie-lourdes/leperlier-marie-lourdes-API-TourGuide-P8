package com.openclassrooms.tourguide.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;

@Service
public class RewardsService {
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
	private int defaultProximityBuffer = 10;// ?type Buffer pour la manipulation des données au lieu de int
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;

	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}

	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	public void calculateRewards(User user) {// erreur de ConcurrentModificationException lors de l appel de la methode
												
			List<VisitedLocation> userLocations = user.getVisitedLocations();
			List<Attraction> attractions = gpsUtil.getAttractions();
			List<String> listUserRewards= new ArrayList<String>();
			
		// boucles imbriquée lance  erreur de ConcurrentModificationException (iteration et modification lors de l iteration) et userRewards vide
			for (VisitedLocation visitedLocation : userLocations) {
				for (Attraction attraction : attractions) {
					
				 listUserRewards=user.getUserRewards().stream()
								.filter(r -> r.attraction.attractionName.equals(attraction.attractionName))
								  .map(Object::toString)
								.toList();
					 
					 if( listUserRewards.isEmpty()) {
						 if (isNearAttraction(visitedLocation, attraction)) {
							 user.addUserReward(
										new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
						 }
							
					 }
			
			/*	if (user.getUserRewards().stream()
							.filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
						if (isNearAttraction(visitedLocation, attraction)) {
							user.addUserReward(
									new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
						}
					}*/
				}
			}
			
		//	System.out.println("userRewards"+user.getUserRewards());//? userRewards vide
		/*} catch (ConcurrentModificationException e) {
			System.out.println("erreur ConcurrentException" + e.getMessage());
		}*/

	}

	// ? a implementer dans le tour guideService avec les 5 premier attraction
	// proche du dernier lieu visité par l user, peur importe la distance et une
	// methode getDistance d interface
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {

		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}

	private boolean isNearAttraction(VisitedLocation visitedLocation, Attraction attraction) {// ? methode propre au
																								// reward service avec
																								// une methode
																								// getDistance d
																								// interface à creer
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}

	private int getRewardPoints(Attraction attraction, User user) {
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
