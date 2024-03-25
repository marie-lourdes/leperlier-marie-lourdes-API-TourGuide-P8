package com.openclassrooms.tourguide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

public interface ICalculatorRewardPoint {
	 boolean isNearAttraction(VisitedLocation visitedLocation, Attraction attraction);
	 public double getDistance(Location loc1, Location loc2);
}
