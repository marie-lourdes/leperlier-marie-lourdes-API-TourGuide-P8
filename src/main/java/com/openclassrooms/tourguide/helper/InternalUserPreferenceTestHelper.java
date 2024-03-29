package com.openclassrooms.tourguide.helper;

import java.util.stream.IntStream;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserPreferences;

public class InternalUserPreferenceTestHelper {

	public static void setUserPreference(User user) {
		IntStream.range(0, 3).forEach(i -> {
			UserPreferences userPreferences=new UserPreferences();
			userPreferences.setAttractionProximity(Integer.MAX_VALUE);
			userPreferences.setTripDuration(1);
			userPreferences.setTicketQuantity(1);
			userPreferences.setNumberOfAdults(1);
			userPreferences.setNumberOfChildren(0);
			user.setUserPreferences(userPreferences);
		});
	}

	
}
