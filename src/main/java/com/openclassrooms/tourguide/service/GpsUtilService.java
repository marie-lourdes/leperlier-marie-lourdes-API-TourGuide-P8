package com.openclassrooms.tourguide.service;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.tracker.Tracker;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

@Service
public class GpsUtilService {
	//private Logger logger = LoggerFactory.getLogger(GpsUtilService.class);
	private final GpsUtil gpsUtil;
	public final Tracker tracker;
	private ExecutorService executor = Executors.newFixedThreadPool(100000);
	public GpsUtilService(GpsUtil gpsUtil) {
		this.gpsUtil = gpsUtil;
		tracker = new Tracker(this);
		if(executor.isTerminated()) {
			addShutDownHook();
		}
	}

	public VisitedLocation trackUserLocation(User user, UserService userService) throws ConcurrentModificationException,InterruptedException, ExecutionException {
		CompletableFuture<VisitedLocation> future =CompletableFuture.supplyAsync(() -> 
	     gpsUtil.getUserLocation(user.getUserId()),
	     executor 
	);
		
		future.thenAccept(visitedLocation -> { userService.addUserLocation(user, visitedLocation); });
		return future.get();	
	}

	public List<Attraction> getAllAttractions() {
		return gpsUtil.getAttractions();
	}

	public Attraction getOneAttraction(String attractionName) {
		return gpsUtil.getAttractions().stream().filter(element -> element.attractionName.equals(attractionName))
				.findFirst().orElseThrow(() -> new NullPointerException("Attraction not found"));
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() { 
		      public void run() {
		        System.out.println("Shutdown GpsUtilService");
		        tracker.stopTracking();
		      } 
		    }); 
	}
}
