package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.model.User;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

public interface ICalculatorRewardPoint  {
	 boolean isNearAttraction(VisitedLocation visitedLocation, Attraction attraction);	
	void calculateUserRewardsPoints (VisitedLocation visitedLocation, Attraction attraction,User user, int rewardPoints);
	public int getRewardPoints(Attraction attraction, User user) ;
}
