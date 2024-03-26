package com.openclassrooms.tourguide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

public interface ICalculatorRewardPoint extends ICalculatorDistance {
	 boolean isNearAttraction(VisitedLocation visitedLocation, Attraction attraction);	
}
