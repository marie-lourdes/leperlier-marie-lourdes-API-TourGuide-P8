package com.openclassrooms.tourguide.service;

import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.model.User;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;

@Service
public class GpsUtilService {
	private final GpsUtil gpsUtil;
	public GpsUtilService(GpsUtil gpsUtil) {
		this.gpsUtil = gpsUtil;
	}
	
	public VisitedLocation  getUserVisitedLocation(User user) {
		return gpsUtil.getUserLocation(user.getUserId());
	}
	
}
