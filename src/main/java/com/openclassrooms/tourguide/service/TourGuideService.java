package com.openclassrooms.tourguide.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.dao.UserDaoImpl;
import com.openclassrooms.tourguide.model.RecommendedUserAttraction;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.utils.ConstantTest;
import com.openclassrooms.tourguide.utils.ICalculatorDistance;
import com.openclassrooms.tourguide.utils.Tracker;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService implements ICalculatorDistance {
	 private static final Logger logger = LogManager.getLogger(TourGuideService.class);

	private final RewardsService rewardsService;
	private final GpsUtilService gpsUtilService;
	private final TripPricer tripPricer = new TripPricer();
	private String tripPricerApiKey;
	public final Tracker tracker;
	private List<RecommendedUserAttraction> recommendedUserAttractionsSorted = new ArrayList<>();
	private List<RecommendedUserAttraction> fiveAttractionsClosestUserLocationDistanceSelected = new ArrayList<>();

	public TourGuideService(RewardsService rewardsService, GpsUtilService gpsUtilService) {
		Locale.setDefault(Locale.US);
		this.rewardsService = rewardsService;
		this.gpsUtilService = gpsUtilService;
		
		tracker = new Tracker("Thread-4-TourGuideService");
		tracker.addShutDownHook();
		logger.debug("Shutdown RewardsService");
	}

	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(generateTripPricerApiKey(user), user.getUserId(),
				user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
				user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	public List<RecommendedUserAttraction> getNearByAttractions(VisitedLocation visitedLocation, User user) {
		recommendedUserAttractionsSorted = getRecommendedUserAttractionsSortedByDistance(visitedLocation.location,
				user);
		return selectFiveClosestRecommendedAttraction(recommendedUserAttractionsSorted);

	}

	private String generateTripPricerApiKey(User user) {
	
		if (null != user.getUserId()) {
			tripPricerApiKey = ConstantTest.LONG_SECRET_STRING_ENCODE_API_KEY + user.getUserId().toString();
		}

		return tripPricerApiKey;
	}

	private List<RecommendedUserAttraction> getRecommendedUserAttractionsSortedByDistance(Location userLocation,
			User user) {
		List<Attraction> attractions = gpsUtilService.getAllAttractions();

		for (Attraction attraction : attractions) {
			double dist = calculateDistance(attraction, userLocation);
			int rewardPoint = rewardsService.getAttractionRewardPoints(attraction, user);

			RecommendedUserAttraction recommendedUserAttraction = new RecommendedUserAttraction(
					attraction.attractionName, attraction.latitude, attraction.longitude, userLocation.latitude,
					userLocation.longitude, dist, rewardPoint);
			recommendedUserAttractionsSorted.add(recommendedUserAttraction);
		}
		return recommendedUserAttractionsSorted.stream().sorted().collect(Collectors.toList());
	}

	private List<RecommendedUserAttraction> selectFiveClosestRecommendedAttraction(
			List<RecommendedUserAttraction> recommendedUserAttractionsSorted) {
		int i = 0;
		for (RecommendedUserAttraction attraction : recommendedUserAttractionsSorted) {
			i++;
			if (i <= 5) {
				fiveAttractionsClosestUserLocationDistanceSelected.add(attraction);
			}

		}
		 logger.debug("5 recommended attractionUser: {}", fiveAttractionsClosestUserLocationDistanceSelected);
		return fiveAttractionsClosestUserLocationDistanceSelected;
	}

}
