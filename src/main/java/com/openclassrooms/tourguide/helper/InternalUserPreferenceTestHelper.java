package com.openclassrooms.tourguide.helper;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserPreference;

public class InternalUserPreferenceTestHelper {
	private static UserPreference userPreference=new UserPreference();
	
	private InternalUserPreferenceTestHelper (){}
	
	public static void setUserPreference(User user) {	
			userPreference.setAttractionProximity(Integer.MAX_VALUE);
			userPreference.setTripDuration(1);
			userPreference.setTicketQuantity(1);
			userPreference.setNumberOfAdults(1);
			userPreference.setNumberOfChildren(0);
			user.setUserPreferences(userPreference);
	
	}
	
}
