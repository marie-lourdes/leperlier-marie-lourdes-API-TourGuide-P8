package com.openclassrooms.tourguide.helper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Random;
import java.util.stream.IntStream;

import com.openclassrooms.tourguide.model.User;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

public class InternalUserHistoryLocationTestHelper {
	private static double randomLatitude = generateRandomLatitude();
	private static double randomLongitude = generateRandomLongitude();
	private static Date randomTime = getRandomTime();
	private static Random random = new Random();
	private static  int randomInt= generateRandomInt();
	private static  double randomDouble= generateRandomDouble();
	
	private InternalUserHistoryLocationTestHelper(){}

	public static void setUserHistoryLocation(User user) {
		IntStream.range(0, 3).forEach(i -> {
			VisitedLocation visitedLocation=new VisitedLocation(user.getUserId(), new Location(randomLatitude, randomLongitude), randomTime);
			user.addToVisitedLocations(
					visitedLocation);
			user.setLastVisitedLocation();
		});
	}

	private static double generateRandomLongitude() {
		double leftLimit = -180;
		double rightLimit = 180;
		return leftLimit + randomDouble * (rightLimit - leftLimit);
	}

	private static double generateRandomLatitude() {
		double leftLimit = -85.05112878;
		double rightLimit = 85.05112878;
		return leftLimit + randomDouble * (rightLimit - leftLimit);
	}

	private static Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays( randomInt);
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

	 public static int generateRandomInt() {
		 
			 int  randomIntValue = random.nextInt(30);
			 return randomIntValue;
		 }
	 
	 public static double generateRandomDouble() {
		 
		 double randomDoubleValue = random.nextDouble();
		 return randomDoubleValue;
	 }
     
	   
}
