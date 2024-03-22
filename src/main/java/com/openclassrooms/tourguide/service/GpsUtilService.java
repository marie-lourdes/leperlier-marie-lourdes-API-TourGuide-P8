package com.openclassrooms.tourguide.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.model.User;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

@Service
public class GpsUtilService {
	private Logger logger = LoggerFactory.getLogger(UserService.class);
	private final GpsUtil gpsUtil;

	private ExecutorService executor = Executors.newFixedThreadPool(5000);
	public GpsUtilService(GpsUtil gpsUtil) {
		this.gpsUtil = gpsUtil;
	}

	public VisitedLocation trackUserLocation(User user, UserService userService) throws InterruptedException, ExecutionException {
		CompletableFuture<VisitedLocation> future =CompletableFuture.supplyAsync(() -> 
	     gpsUtil.getUserLocation(user.getUserId()),
	     executor 
	);
		
		future.thenAccept(visitedLocation -> { userService.addUserLocation(user, visitedLocation); })
		.thenRun(()-> logger.debug("Getting UserLocation completed"));
		return future.get();
		//return gpsUtil.getUserLocation(user.getUserId());
		
	}

	public List<Attraction> getAllAttractions() {
		return gpsUtil.getAttractions();
	}

	public Attraction getOneAttraction(String attractionName) {
		return gpsUtil.getAttractions().stream().filter(element -> element.attractionName.equals(attractionName))
				.findFirst().orElseThrow(() -> new NullPointerException("Attraction not found"));
	}

}
