package com.openclassrooms.tourguide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

public interface ICalculatorRewardPoint  {
	 boolean isNearAttraction(VisitedLocation visitedLocation, Attraction attraction);	
}
