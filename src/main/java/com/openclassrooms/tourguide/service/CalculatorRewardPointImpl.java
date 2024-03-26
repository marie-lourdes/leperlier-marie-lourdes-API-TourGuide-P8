package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.Data;
import rewardCentral.RewardCentral;

@Data
public class CalculatorRewardPointImpl implements ICalculatorRewardPoint {

	private final int defaultProximityBuffer = 10;
	private final int proximityBuffer = defaultProximityBuffer;
	private double distance;
	private final GpsUtilService gpsUtilService;
	private final RewardCentral rewardCentral;

	public CalculatorRewardPointImpl(GpsUtilService gpsUtilService, RewardCentral rewardCentral) {
		this.gpsUtilService = gpsUtilService;
		this.rewardCentral = rewardCentral;
	}

	@Override
	public void calculateUserRewardsPoints(VisitedLocation visitedLocation, Attraction attraction, User user,
			int rewardPoints) {
		
		if (isNearAttraction(visitedLocation, attraction)) {
			user.addUserReward(new UserReward(visitedLocation, attraction, rewardPoints));
		}
	}

	@Override
	public boolean isNearAttraction(VisitedLocation visitedLocation, Attraction attraction) {

		return this.getDistance() > proximityBuffer ? false : true;
	}

	public double getDistanceAttractionAndVisitedLocation(VisitedLocation visitedLocation, Attraction attraction) {
		return this.distance = gpsUtilService.calculateDistance(visitedLocation.location, attraction);
	}
	
	@Override
	public int getRewardPoints(Attraction attraction, User user) {
		return rewardCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}
}
