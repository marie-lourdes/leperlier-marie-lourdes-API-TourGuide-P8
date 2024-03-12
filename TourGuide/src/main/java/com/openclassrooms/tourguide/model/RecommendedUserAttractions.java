package com.openclassrooms.tourguide.model;

import lombok.Data;

@Data
public class RecommendedUserAttractions implements Comparable<RecommendedUserAttractions> {
	private String attractionName;
	private double attractionLat;
	private double attractionLong;
	private double userLocationLat;
	private double userLocationLong;
	private Double distance;
	private int rewardPoints;

	public RecommendedUserAttractions(String attractionName, double attractionLat, double attractionLong,
			double userLocationLat, double userLocationLong, Double distance) {
		this.attractionName = attractionName;
		this.attractionLat = attractionLat;
		this.attractionLong = attractionLong;
		this.userLocationLat = userLocationLat;
		this.distance = distance;
		
	}

	@Override
	public int compareTo(RecommendedUserAttractions recommendedAttraction) {
		return (int) (this.distance - recommendedAttraction.distance);
	}
}