package com.openclassrooms.tourguide.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import jakarta.servlet.http.HttpServletResponse;
import tripPricer.Provider;

@RestController
@RequestMapping("tourguide")
public class TourGuideController {
	private static final Logger logger = LoggerFactory.getLogger(TourGuideController.class);

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
	public VisitedLocation getLocation(@RequestParam String userName, HttpServletResponse response)
			throws InterruptedException, IOException {

		VisitedLocation visitedLocation = null;
		try {
			User userFoundByName = userService.getUser(userName);
			if (0 == userFoundByName.getVisitedLocations().size()) {
				gpsUtilService.trackUserLocation(userFoundByName, userService);
			}
			visitedLocation = userService.getUserLocation(userFoundByName);

			logger.info("User location successfully retrieved for {}", userName);
		} catch (NullPointerException e) {
			response.sendError(404);
			logger.error("Failed to get user location {}", e.getMessage());
		}
		return visitedLocation;
	}

	@GetMapping("/getNearbyAttractions")
	public List<RecommendedUserAttraction> getNearbyAttractions(@RequestParam String userName,
			HttpServletResponse response) throws IOException {

		List<RecommendedUserAttraction> closestRecommendedUserAttractions = new ArrayList<>();

		try {
			User userFoundByName = userService.getUser(userName);
			if (null != userFoundByName.getLastVisitedLocation()) {
				VisitedLocation lastVisitedLocation = userService.getUserLocation(userFoundByName);
				closestRecommendedUserAttractions = tourGuideService.getNearByAttractions(lastVisitedLocation,
						userService.getUser(userName));
			}

			logger.info("closest recommended user attractions successfully retrieved for: {}", userName);
		} catch (NullPointerException e) {
			response.sendError(404);
			logger.error("Failed to get closest user attractions {}", e.getMessage());
		}

		return closestRecommendedUserAttractions;
	}

	@GetMapping("/getRewards")
	public List<UserReward> getRewards(@RequestParam String userName, HttpServletResponse response) throws IOException {
		List<UserReward> userRewards = new ArrayList<>();

		try {
			User userFoundByName = userService.getUser(userName);
			// define and modify default distance minimum (10) to get user rewards
			rewardsService.setDefaultProximityInMiles(Integer.MAX_VALUE);

			rewardsService.calculateRewards(userFoundByName);
			userRewards = userService.getUserRewards(userFoundByName);

			logger.info("User rewards successfully retrieved for: {}", userName);
		} catch (NullPointerException e) {
			response.sendError(404);
			logger.error("Failed to get user rewards  {}", e.getMessage());
		}

		return userRewards;
	}

	@GetMapping("/getTripDeals")
	public List<Provider> getTripDeals(@RequestParam String userName, HttpServletResponse response) throws IOException {
		List<Provider> providers = new ArrayList<>();

		try {
			User userFoundByName = userService.getUser(userName);
			providers = tourGuideService.getTripDeals(userFoundByName);

			logger.info("All providers successfully retrieved for: {}", userName);
		} catch (NullPointerException e) {
			response.sendError(404);
			logger.error("Failed to get all providers  {}", e.getMessage());
		}

		return providers;
	}

}