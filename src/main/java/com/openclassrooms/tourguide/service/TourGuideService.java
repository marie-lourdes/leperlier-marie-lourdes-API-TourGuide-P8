package com.openclassrooms.tourguide.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.model.RecommendedUserAttraction;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.utils.Constant;
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
		logger.debug("Getting TripDeals for: {} ", user.getUserName());
		List<Provider> providers = new ArrayList<>();

		try {
			providers = tripPricer.getPrice(generateTripPricerApiKey(user), user.getUserId(),
					user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
					user.getUserPreferences().getTripDuration(), rewardsService.calculateTotalRewardsPoints(user));
			if (null != providers) {
				user.setTripDeals(providers);
			}
			logger.debug(" TripDeals  {} for: {} ", providers, user.getUserName());
			return providers;
		} catch (Exception e) {
			logger.error("Trip deals not found for: {} ", user.getUserName());
			return new ArrayList<>();
		}
	}

	public List<RecommendedUserAttraction> getNearByAttractions(VisitedLocation visitedLocation, User user) {
		logger.debug("getting five closest RecommendedUserAttraction for: {} ", user.getUserName());
		List<RecommendedUserAttraction> closestRecommendedUserAttraction = new ArrayList<>();
		
		try {
			recommendedUserAttractionsSorted = getRecommendedUserAttractionsSortedByDistance(visitedLocation.location,
					user);
			closestRecommendedUserAttraction = selectFiveClosestRecommendedAttraction(recommendedUserAttractionsSorted);
			logger.debug("RecommendedUserAttractions: {}", closestRecommendedUserAttraction);
		} catch (Exception e) {
			logger.error("RecommendedUserAttractions not found");

		}
		return closestRecommendedUserAttraction;
	}

	private String generateTripPricerApiKey(User user) {
		if (null != user.getUserId()) {
			tripPricerApiKey = Constant.LONG_SECRET_STRING_ENCODE_API_KEY + user.getUserId().toString();
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
		return recommendedUserAttractionsSorted.stream().sorted().toList();
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

		return fiveAttractionsClosestUserLocationDistanceSelected;
	}
}
