package com.openclassrooms.tourguide.service;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.utils.Tracker;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;

@Service
public class GpsUtilService{
	private static final Logger logger = LogManager.getLogger(UserService.class);
	private final GpsUtil gpsUtil;
	private ExecutorService executor = Executors.newFixedThreadPool(100000);
	public final Tracker tracker;

	public GpsUtilService(GpsUtil gpsUtil) {
		this.gpsUtil = gpsUtil;
		tracker = new Tracker("Thread-2-GpsUtilService");
		tracker.addShutDownHook();
		logger.debug("Shutdown GpsUtilService");
	}

	public void trackUserLocation(User user, UserService userService) throws InterruptedException, ExecutionException {
		try {
			CompletableFuture.supplyAsync(() -> gpsUtil.getUserLocation(user.getUserId()), executor)
					.thenAccept(visitedLocation -> {
						userService.addUserLocation(user, visitedLocation);
					});
		} catch (ConcurrentModificationException e) {
			logger.error(e.getMessage());
		}
	}

	public List<Attraction> getAllAttractions() {
		return gpsUtil.getAttractions();
	}

	public Attraction getOneAttraction(String attractionName) {
		return gpsUtil.getAttractions().stream().filter(element -> element.attractionName.equals(attractionName))
				.findFirst().orElseThrow(() -> new NullPointerException("Attraction not found"));
	}
}
