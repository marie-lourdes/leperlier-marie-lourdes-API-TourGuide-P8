package com.openclassrooms.tourguide.helper;

public class InternalUserTestHelper {
	
	private  InternalUserTestHelper () {
		
	}
	
	// Set this default up to 100,000 for testing
	private static int internalUserNumber = 100;
	
	public static void setInternalUserNumber(int internalUserNumber) {
		InternalUserTestHelper.internalUserNumber = internalUserNumber;
	}
	
	public static int getInternalUserNumber() {
		return internalUserNumber;
	}
}
