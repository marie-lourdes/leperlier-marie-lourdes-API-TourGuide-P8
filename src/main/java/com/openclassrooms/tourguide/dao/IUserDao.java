package com.openclassrooms.tourguide.dao;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

import gpsUtil.location.VisitedLocation;

public interface IUserDao{
	void initializeInternalUsers();

	void addUser(User user);

	User getUser(String userName);

	List<User> getAllUsers(ExecutorService executor) throws InterruptedException, ExecutionException;

	void addUserLocation(User user, VisitedLocation visitedLocation);

	VisitedLocation getUserLocation(User user);

	List<UserReward> getUserRewards(User user);

	//VisitedLocation getLastUserLocation(User user);

}
