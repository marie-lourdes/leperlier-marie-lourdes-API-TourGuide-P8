package com.openclassrooms.tourguide.model;

import lombok.Data;

@Data
public class RecommendedAttraction {
	private String attractionName;
	private double attractionLat;
	private double attractionLong;
	private double userLocationLat;
	private double userLocationLong;
	private double distance;

	public RecommendedAttraction(String attractionName, double attractionLat, double attractionLong, double userLocationLat,
			double userLocationLong, double distance) {
		this.attractionName = attractionName;
		this.attractionLat = attractionLat;
		this.attractionLong = attractionLong;
		this.userLocationLat=userLocationLat;
		this.distance=distance;
	}
}