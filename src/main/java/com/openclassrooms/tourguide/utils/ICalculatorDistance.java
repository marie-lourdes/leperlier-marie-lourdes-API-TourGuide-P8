package com.openclassrooms.tourguide.utils;

import gpsUtil.location.Location;

public interface ICalculatorDistance {
	public default double calculateDistance(Location loc1, Location loc2) {
		double lat1 = Math.toRadians(loc1.latitude);
		double lon1 = Math.toRadians(loc1.longitude);
		double lat2 = Math.toRadians(loc2.latitude);
		double lon2 = Math.toRadians(loc2.longitude);

		double angle = Math
				.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

		double nauticalMiles = 60 * Math.toDegrees(angle);
		return Constant.STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;

	}
}
