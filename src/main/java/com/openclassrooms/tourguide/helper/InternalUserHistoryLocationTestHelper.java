package com.openclassrooms.tourguide.helper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.openclassrooms.tourguide.model.User;

import gpsUtil.location.VisitedLocation;

public class InternalUserHistoryLocationTestHelper {
	// Set this default up to 100,000 for testing
	private static List<VisitedLocation> internalUserLocationHistory = new ArrayList<>();
	
	private InternalUserHistoryLocationTestHelper() {}
	
	public void setUserHistoryLocation(VisitedLocation visitedLocation,User user) {
		boolean existingUserVisitedLocation =internalUserLocationHistory.removeIf(userLocation -> user.getUserId().equals(visitedLocation.userId));
		internalUserLocationHistory.stream().filter(element -> !existingUserVisitedLocation  )
		.map(elem->internalUserLocationHistory.add(elem));
		
	}
	
	public List<VisitedLocation> getUserHistoryLocation(VisitedLocation visitedLocation,User user) {
		return (internalUserLocationHistory.isEmpty()) ? new ArrayList<>() : internalUserLocationHistory;
	}
	
	/*private double generateRandomLongitude() {
		double leftLimit = -180;
		double rightLimit = 180;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
		double rightLimit = 85.05112878;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}*/
}
