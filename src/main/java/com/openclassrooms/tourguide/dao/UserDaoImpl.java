package com.openclassrooms.tourguide.dao;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.openclassrooms.tourguide.helper.InternalUserHistoryLocationTestHelper;
import com.openclassrooms.tourguide.helper.InternalUserPreferenceTestHelper;
import com.openclassrooms.tourguide.helper.InternalUserTestHelper;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.service.RewardsService;

import gpsUtil.location.VisitedLocation;

/**********************************************************************************
 * 
 * Class and Methods Below: For Internal Testing
 * 
 **********************************************************************************/
// Database connection will be used for external users, but for testing purposes
// internal users are provided and stored in memory

@Component
public class UserDaoImpl implements IUserDao{
	private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);
	private final Map<String, User> internalUserMap = new ConcurrentHashMap<>();
	//private boolean testMode=false;
	
	public UserDaoImpl( ) {
//this.testMode = isTestMode;
		//if(isTestMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		//}
			
	}

//	public UserDaoImpl(){}
	
	@Override
	public void addUser(User user) {
		if (!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}

	@Override
	public User getUser(String userName) {
		return internalUserMap.values().stream().filter(elem-> elem.getUserName().equals(userName)).findFirst().orElseThrow(()-> new NullPointerException("User not found"));
	}

	@Override
	public List<User> getAllUsers(ExecutorService executor) throws InterruptedException, ExecutionException {
		CompletableFuture<List<User>> future = new CompletableFuture<>();
		try {
			future = CompletableFuture.supplyAsync(() -> internalUserMap.values().stream().collect(Collectors.toList()),
					executor);
		} catch (ConcurrentModificationException e) {
			logger.error(e.getMessage());
		}
		return future.get();
	}

	@Override
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	@Override
	public void addUserLocation(User user, VisitedLocation visitedLocation) {
		user.addToVisitedLocations(visitedLocation);
		user.setLastVisitedLocation();

	}

	@Override
	public VisitedLocation getUserLocation(User user) {
		return  user.getLastVisitedLocation();
	}

	/*@Override
	public VisitedLocation getLastUserLocation(User user) {
		return user.getLastVisitedLocation();
	}*/

	@Override
	public void initializeInternalUsers() {
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
