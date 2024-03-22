package com.openclassrooms.tourguide.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.model.User;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

@Service
public class GpsUtilService {
	private final UserService userService;
	private final GpsUtil gpsUtil;

	public GpsUtilService(GpsUtil gpsUtil,UserService userService) {
		this.gpsUtil = gpsUtil;
		this.userService=userService;
	}

	public VisitedLocation getUserVisitedLocation(User user) {
		return gpsUtil.getUserLocation(user.getUserId());
	}

	public List<Attraction> getAllAttractions() {
		return gpsUtil.getAttractions();
	}

	public Attraction getOneAttraction(String attractionName) {
		return gpsUtil.getAttractions().stream().filter(element -> element.attractionName.equals(attractionName))
				.findFirst().orElseThrow(() -> new NullPointerException("Attraction not found"));
	}

	public VisitedLocation trackUserLocation(User user) throws InterruptedException {
		VisitedLocation visitedLocation = this.getUserVisitedLocation(user);
		userService.addUserLocation(user, visitedLocation);

		return visitedLocation;
	}
}
