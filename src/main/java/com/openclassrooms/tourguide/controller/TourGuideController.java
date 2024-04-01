package com.openclassrooms.tourguide.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.tourguide.model.RecommendedUserAttraction;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.service.GpsUtilService;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.service.UserService;

import gpsUtil.location.VisitedLocation;
import tripPricer.Provider;

@RestController
@RequestMapping("tourguide")
public class TourGuideController {
	private final static Logger logger = LoggerFactory.getLogger(TourGuideController.class);

	private TourGuideService tourGuideService;
	private UserService userService;
	private GpsUtilService gpsUtilService;
	private RewardsService rewardsService;

	public TourGuideController(TourGuideService tourGuideService, UserService userService,
			GpsUtilService gpsUtilService, RewardsService rewardsService) {
		this.tourGuideService = tourGuideService;
		this.userService = userService;// for testing controller with internalUser
		this.gpsUtilService = gpsUtilService;
		this.rewardsService = rewardsService;
	}

	@GetMapping("/")
	public String index() {
		return "Greetings from TourGuide!";
	}

	@GetMapping("/getLocation")
	public VisitedLocation getLocation(@RequestParam String userName) {
		User userFoundByName = userService.getUser(userName);

		try {
			if (0 == userFoundByName.getVisitedLocations().size()) {
				gpsUtilService.trackUserLocation(userFoundByName, userService);
			}

		} catch (InterruptedException | ExecutionException e) {
			logger.error("Failed to get user location {}", e.getMessage());
		}

		VisitedLocation visitedLocation = userService.getUserLocation(userFoundByName);
		logger.info("User location successfully retrieved {}", visitedLocation);
		return visitedLocation;
	}

	@GetMapping("/getNearbyAttractions")
	public List<RecommendedUserAttraction> getNearbyAttractions(@RequestParam String userName) {
		User userFoundByName = userService.getUser(userName);
		List<RecommendedUserAttraction> closestRecommendedUserAttractions = new ArrayList<>();

		try {
			if (null != userFoundByName.getLastVisitedLocation()) {
				VisitedLocation lastVisitedLocation = userService.getUserLocation(userFoundByName);
				closestRecommendedUserAttractions = tourGuideService.getNearByAttractions(lastVisitedLocation,
						userService.getUser(userName));
			}

		} catch (Exception e) {
			logger.error("Failed to get closest user attractions {}", e.getMessage());
		}

		logger.info("closest recommended user attractions successfully retrieved {} for: {}",
				closestRecommendedUserAttractions, userName);
		logger.info("user location for: {}", userService.getUserLocation(userFoundByName));

		return closestRecommendedUserAttractions;
	}

	@GetMapping("/getRewards")
	public List<UserReward> getRewards(@RequestParam String userName) {
		List<UserReward> userRewards = new ArrayList<>();

		try {
			rewardsService.calculateRewards(userService.getUser(userName));
			userRewards = userService.getUserRewards(userService.getUser(userName));
		} catch (Exception e) {
			logger.error("Failed to get user rewards  {}", e.getMessage());
		}

		logger.info("User rewards successfully retrieved {} for: {}", userRewards, userName);
		return userRewards;
	}

	@GetMapping("/getTripDeals")
	public List<Provider> getTripDeals(@RequestParam String userName) {
		List<Provider> providers = new ArrayList<>();

		try {
			providers = tourGuideService.getTripDeals(userService.getUser(userName));
		} catch (Exception e) {
			logger.error("Failed to get all providers  {}", e.getMessage());
		}

		logger.info("All providers successfully retrieved {} for: {}", providers, userName);
		return providers;
	}

}