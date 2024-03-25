package com.openclassrooms.tourguide.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.model.RecommendedUserAttraction;
import com.openclassrooms.tourguide.model.User;

import gpsUtil.location.VisitedLocation;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {
	// private static final Logger logger = LogManager.getLogger( TourGuideService.class);
	
	private final RewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();

	public TourGuideService(RewardsService  rewardsService) {
		this.rewardsService = rewardsService;			
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

	public List<RecommendedUserAttraction>getNearByAttractions(VisitedLocation visitedLocation,User user) {
		List<RecommendedUserAttraction> attractionsClosestUserVisitedLocation =rewardsService.getClosestRecommendedUserAttractions(visitedLocation.location, user);
		return  attractionsClosestUserVisitedLocation;
	}

	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";

}

