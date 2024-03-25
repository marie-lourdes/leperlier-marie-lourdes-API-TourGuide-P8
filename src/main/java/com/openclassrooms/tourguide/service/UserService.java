package com.openclassrooms.tourguide.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.helper.InternalUserHistoryLocationTestHelper;
import com.openclassrooms.tourguide.helper.InternalUserTestHelper;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.tracker.Tracker;

import gpsUtil.location.VisitedLocation;

@Service
public class UserService {
	 private static final Logger logger = LogManager.getLogger(UserService.class);
	 
	private final RewardsService rewardsService;
	private ExecutorService executor = Executors.newFixedThreadPool(100);
	public final Tracker tracker;
	boolean testMode = true;

	public UserService(RewardsService rewardsService) {
		this.rewardsService = rewardsService;

		Locale.setDefault(Locale.US);
		if (testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker("Thread-1-UserService");
		tracker.startTracking();
		addShutDownHook();
	}

	public void addUser(User user) {
		if (!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}

	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}

	public List<User> getAllUsers() throws InterruptedException, ExecutionException {
		CompletableFuture<List<User>> future = new CompletableFuture<>();

		future = CompletableFuture.supplyAsync(() -> internalUserMap.values().stream().collect(Collectors.toList()),
				executor);
		return future.get();
	}

	public List<UserReward> getUserRewards(User user) {
		rewardsService.calculateRewards(user);
		return user.getUserRewards(); // ajouter rewardsService.calculateRewards(user) avant et creer user service;
	}

	public void addUserLocation(User user, VisitedLocation visitedLocation) {
		user.addToVisitedLocations(visitedLocation);
		user.setLastVisitedLocation();
		tracker.finalizeTrackUser(user);
	}

	public VisitedLocation getUserLocation(User user) {
		return user.getVisitedLocations().get(0);
	}

	public VisitedLocation getLastUserLocation(User user) {
		return user.getLastVisitedLocation();
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Shutdown UserService");
				tracker.stopTracking();
			}
		});
	}

	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	// Database connection will be used for external users, but for testing purposes
	// internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new ConcurrentHashMap<>();

	private void initializeInternalUsers() {
		IntStream.range(0, InternalUserTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			InternalUserHistoryLocationTestHelper.setUserHistoryLocation(user);

			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalUserTestHelper.getInternalUserNumber() + " internal test users.");
	}
}
