package com.openclassrooms.tourguide;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.model.RecommendedUserAttractions;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;

@SpringBootApplication
public class TourGuideApplication{

	public static void main(String[] args) {
		SpringApplication.run(TourGuideApplication.class, args);
	}
}
