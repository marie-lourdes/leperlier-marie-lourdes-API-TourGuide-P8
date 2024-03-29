package com.openclassrooms.tourguide.dao;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import com.openclassrooms.tourguide.model.User;

public interface IUserDao {
	void initializeInternalUsers( );
	void addUser(User user);
	 User getUser(String userName);
	 List<User> getAllUsers(ExecutorService executor) throws InterruptedException, ExecutionException ;
	
}
