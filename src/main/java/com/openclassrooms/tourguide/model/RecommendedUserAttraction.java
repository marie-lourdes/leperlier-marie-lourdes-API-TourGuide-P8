package com.openclassrooms.tourguide.model;

import lombok.Data;

@Data
public class RecommendedUserAttraction implements Comparable<RecommendedUserAttraction> {
	private String attractionName;
	private double attractionLat;
	private double attractionLong;
	private double userLocationLat;
	private double userLocationLong;
	private Double distance;
	private int rewardPoints;

	public RecommendedUserAttraction(String attractionName, double attractionLat, double attractionLong,
			double userLocationLat, double userLocationLong, Double distance,int rewardPoints) {
		this.attractionName = attractionName;
		this.attractionLat = attractionLat;
		this.attractionLong = attractionLong;
		this.userLocationLat = userLocationLat;
		this.distance = distance;
		this.rewardPoints =rewardPoints;
		
	}

	@Override
	public int compareTo(RecommendedUserAttraction recommendedAttraction) {
		return (int) (this.distance - recommendedAttraction.distance);
	}
}