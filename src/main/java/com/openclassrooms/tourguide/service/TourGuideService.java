package com.openclassrooms.tourguide.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.model.RecommendedUserAttraction;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.utils.ICalculatorDistance;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService implements ICalculatorDistance{
	// private static final Logger logger = LogManager.getLogger(
	// TourGuideService.class);

	private final RewardsService rewardsService;
	private final GpsUtilService gpsUtilService;
	private final TripPricer tripPricer = new TripPricer();
	private List<RecommendedUserAttraction> attractionsUserLocationDistance = new ArrayList<>();
	private List<RecommendedUserAttraction> attractionsClosestUserLocationDistanceSorted = new ArrayList<>();

	public TourGuideService(RewardsService rewardsService, GpsUtilService gpsUtilService) {
		this.rewardsService = rewardsService;
		this.gpsUtilService = gpsUtilService;
		Locale.setDefault(Locale.US);
	}

	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(),
				user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
				user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	public List<RecommendedUserAttraction> getNearByAttractions(VisitedLocation visitedLocation, User user) {
		List<Attraction> attractions = gpsUtilService.getAllAttractions();
		int i = 0;
		Location userLocation = visitedLocation.location;
		for (Attraction attraction : attractions) {
			double dist = calculateDistance(attraction, userLocation);
			int rewardPoint = rewardsService.getAttractionRewardPoints(attraction, user);

			RecommendedUserAttraction closestAttraction = new RecommendedUserAttraction(attraction.attractionName,
					attraction.latitude, attraction.longitude, userLocation.latitude, userLocation.longitude, dist,
					rewardPoint);
			attractionsUserLocationDistance.add(closestAttraction);
		}
		Collections.sort(attractionsUserLocationDistance);

		System.out.println("all recommended attractionUser" + attractionsUserLocationDistance);
		for (RecommendedUserAttraction attraction : attractionsUserLocationDistance) {
			i++;
			if (i <= 5) {
				attractionsClosestUserLocationDistanceSorted.add(attraction);
			}

		}
		return attractionsClosestUserLocationDistanceSorted;
	}

	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";

}
