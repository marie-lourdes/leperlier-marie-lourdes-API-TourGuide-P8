package com.openclassrooms.tourguide.helper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

public class InternalUserRewardsTestHelper {
	private static double randomLatitude = generateRandomLatitude();
	private static double randomLongitude = generateRandomLongitude();
	private static Date randomTime = getRandomTime();

	public static void setUserRewards(User user) {
		InternalUserHistoryLocationTestHelper.setUserHistoryLocation(user);
		List<VisitedLocation >VisitedLocation=user.getVisitedLocations();
		
			UserReward userReward=new UserReward(VisitedLocation.get(VisitedLocation.size()-1), new Attraction("Disneyland","Anaheim","CA",randomLatitude,randomLongitude));
			user.addUserReward(userReward);
			
		
	}

	private static double generateRandomLongitude() {
		double leftLimit = -180;
		double rightLimit = 180;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private static double generateRandomLatitude() {
		double leftLimit = -85.05112878;
		double rightLimit = 85.05112878;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private static Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}
}
