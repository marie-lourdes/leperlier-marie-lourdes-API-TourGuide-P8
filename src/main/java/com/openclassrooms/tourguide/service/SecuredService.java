package com.openclassrooms.tourguide.service;

import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.model.User;

@Service
public class SecuredService {
	private String tripPricerApiKey;
	
	public String generateTripPricerApiKey(User user) {
		if(null !=user.getUserId()) {
			tripPricerApiKey += user.getUserId().toString();
		}
		return  tripPricerApiKey;
	}
}
