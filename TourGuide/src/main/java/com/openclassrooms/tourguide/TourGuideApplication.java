package com.openclassrooms.tourguide;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.openclassrooms.tourguide.config.UserDataLoader;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.model.RecommendedAttraction;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;

@SpringBootApplication
public class TourGuideApplication implements CommandLineRunner {
	@Autowired
	private RewardsService rewardsService;

	public static void main(String[] args) {
		SpringApplication.run(TourGuideApplication.class, args);
	}

	@Override
	public void run(String... args) {
		GpsUtil  gpsUtil = new GpsUtil();
		UserDataLoader userDataLoader = new UserDataLoader();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, userDataLoader);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
	
		List<	RecommendedAttraction>  attracUserLocationDistanceSortedCalculated =rewardsService.has5ClosestRecommendedAttractionsProximity(visitedLocation.location);
		System.out.println(attracUserLocationDistanceSortedCalculated );
		
	}

}
