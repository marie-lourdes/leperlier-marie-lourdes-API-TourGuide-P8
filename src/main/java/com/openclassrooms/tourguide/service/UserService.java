package com.openclassrooms.tourguide.service ;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.dao.IUserDao;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.utils.Tracker;

import gpsUtil.location.VisitedLocation;

@Service
public class UserService {
	private static final Logger logger = LogManager.getLogger(UserService.class);

	private ExecutorService executor = Executors.newFixedThreadPool(100000);
	public final Tracker tracker;
	private IUserDao userDaoImpl;

	public UserService(IUserDao userDaoImpl) {
		Locale.setDefault(Locale.US);
		this.userDaoImpl = userDaoImpl;
		tracker = new Tracker("Thread-1-UserService");
		tracker.addShutDownHook();
	}

	public void addUser(User user) {
		logger.debug("Adding user: {}", user);

		try {
			userDaoImpl.addUser(user);
			logger.debug("User created: {}  ", user);
		} catch (Exception e) {
			logger.error("Failed to add user{}", user);
		}
	}

	public void addUserLocation(User user, VisitedLocation visitedLocation) {
		logger.debug("Adding user  visited location for: {} ", visitedLocation, user.getUserName());

		try {
			userDaoImpl.addUserLocation(user, visitedLocation);
			tracker.finalizeTrackUser(user);
			logger.debug("User  visited location added: {}  ", visitedLocation);
		} catch (Exception e) {
			logger.error("Failed to add user visited location for: {} ", user.getUserName());
		}
	}

	public User getUser(String userName) {
		logger.debug("Getting user: {} ", userName);
		User userFound = null;

		try {
			userFound = userDaoImpl.getUser(userName);
			logger.debug("Use found by username: {}  ", userFound);
		} catch (NullPointerException e) {
			logger.error("User not found:{} ", userName);
		}

		return userFound;
	}

	public List<User> getAllUsers() {
		logger.debug("Getting All users");
		List<User> allUsers = new ArrayList<>();

		try {
			allUsers = userDaoImpl.getAllUsers(executor);
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			logger.error(e.getMessage());
		} catch (NullPointerException e) {
			logger.error("Users not found");
		}
		return allUsers;
	}

	public List<UserReward> getUserRewards(User user) {
		logger.debug("Getting user rewards for: {} ", user.getUserName());
		List<UserReward> allUserRewards = new ArrayList<>();
		try {
			allUserRewards = userDaoImpl.getUserRewards(user);
			logger.debug("All user rewards {} for: {} ", allUserRewards, user.getUserName());
		} catch (NullPointerException e) {
			logger.error("User Rewards not found");
		}

		return allUserRewards;
	}

	public VisitedLocation getUserLocation(User user) {
		logger.debug("Getting user location for: {} ", user.getUserName());
		VisitedLocation userLocation = null;

		try {
			userLocation = userDaoImpl.getUserLocation(user);
			logger.debug("User location {} ", userLocation, user.getUserName());
		} catch (NullPointerException e) {
			logger.error("User location not found");
		}

		return userLocation;
	}
}