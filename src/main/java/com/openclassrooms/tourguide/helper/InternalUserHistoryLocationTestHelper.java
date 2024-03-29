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

	public static void setUserHistoryLocation(User user) {
		IntStream.range(0, 3).forEach(i -> {
			user.addToVisitedLocations(
					new VisitedLocation(user.getUserId(), new Location(randomLatitude, randomLongitude), randomTime));
		});
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
