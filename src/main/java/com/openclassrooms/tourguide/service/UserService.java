package com.openclassrooms.tourguide.service;

import java.util.ConcurrentModificationException;
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

import com.openclassrooms.tourguide.dao.IUserDao;
import com.openclassrooms.tourguide.dao.UserDaoImpl;
import com.openclassrooms.tourguide.helper.InternalUserHistoryLocationTestHelper;
import com.openclassrooms.tourguide.helper.InternalUserPreferenceTestHelper;
import com.openclassrooms.tourguide.helper.InternalUserTestHelper;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.utils.Tracker;

import gpsUtil.location.VisitedLocation;

@Service
public class UserService {
	private static final Logger logger = LogManager.getLogger(UserService.class);
	private ExecutorService executor = Executors.newFixedThreadPool(100000);
	public final Tracker tracker;
	private IUserDao userDaoImplTest ;
	boolean testMode = true;

	public UserService(UserDaoImpl  userDaoImpl ) {
		Locale.setDefault(Locale.US);
	    this.userDaoImplTest=userDaoImpl ;
		tracker = new Tracker("Thread-1-UserService");
		tracker.addShutDownHook();
	}

	public void addUser(User user) {
		 userDaoImplTest.addUser(user);
	}

	public User getUser(String userName) {
		return  userDaoImplTest.getUser(userName);
	}

	public List<User> getAllUsers() throws InterruptedException, ExecutionException {
		return userDaoImplTest.getAllUsers(executor);
	}

	public List<UserReward> getUserRewards(User user) {
		return  userDaoImplTest.getUserRewards(user);
	}

	public void addUserLocation(User user, VisitedLocation visitedLocation) {
		 userDaoImplTest.addUserLocation(user, visitedLocation);
		tracker.finalizeTrackUser(user);
	}

	public VisitedLocation getUserLocation(User user) {
		return  userDaoImplTest.getUserLocation(user);
	}

	public VisitedLocation getLastUserLocation(User user) {
		return  userDaoImplTest.getLastUserLocation(user);
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
			 InternalUserPreferenceTestHelper.setUserPreference(user);
			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalUserTestHelper.getInternalUserNumber() + " internal test users.");
	}
}