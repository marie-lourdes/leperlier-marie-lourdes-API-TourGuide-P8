package com.openclassrooms.tourguide.helper;

public class InternalUserTestHelper {
	//Default default internal user number to 100,000 for testing
	private static int internalUserNumber = 100000;
	
	private InternalUserTestHelper(){}
	
	public static void setInternalUserNumber(int internalUserNumber) {
		InternalUserTestHelper.internalUserNumber = internalUserNumber;
	}
	
	public static int getInternalUserNumber() {
		return internalUserNumber;
	}
}
