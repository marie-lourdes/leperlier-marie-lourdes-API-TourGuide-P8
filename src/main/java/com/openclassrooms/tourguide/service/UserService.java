package com.openclassrooms.tourguide.service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.dao.IUserDao;
import com.openclassrooms.tourguide.dao.UserDaoImpl;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.utils.Tracker;

import gpsUtil.location.VisitedLocation;

@Service
public class UserService {
	private static final Logger logger = LogManager.getLogger(UserService.class);
	private ExecutorService executor = Executors.newFixedThreadPool(100000);
	public final Tracker tracker;
	private IUserDao userDaoImpltest;
	
	public UserService( ) {
		this.userDaoImpltest= new UserDaoImpl();
		tracker = new Tracker("Thread-1-UserService");
		tracker.addShutDownHook();
		logger.debug("Shutdown UserService");
	}

	public void addUser(User user) {
		userDaoImpltest.addUser(user);
	}

	public User getUser(String userName) {
		return userDaoImpltest.getUser(userName);
	}

	public List<User> getAllUsers() throws InterruptedException, ExecutionException {
		return userDaoImpltest.getAllUsers(executor);
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
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
}
