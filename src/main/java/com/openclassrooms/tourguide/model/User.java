package com.openclassrooms.tourguide.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import gpsUtil.location.VisitedLocation;
import lombok.Data;
import tripPricer.Provider;

@Data
public class User {
	private UUID userId;
	private String userName;
	private String phoneNumber;
	private String emailAddress;
	private Date latestLocationTimestamp;
	private VisitedLocation lastVisitedLocation;
	private List<VisitedLocation> visitedLocations = new ArrayList<>();
	private List<UserReward> userRewards = new ArrayList<>();
	private UserPreference userPreferences = new UserPreference();
	private List<Provider> tripDeals = new ArrayList<>();

	public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
		this.userId = userId;
		this.userName = userName;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
	}

	public User() {
	}

	public UUID getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setLatestLocationTimestamp(Date latestLocationTimestamp) {
		this.latestLocationTimestamp = latestLocationTimestamp;
	}

	public Date getLatestLocationTimestamp() {
		return latestLocationTimestamp;
	}

	public void addToVisitedLocations(VisitedLocation visitedLocation) {
		visitedLocations.add(visitedLocation);
	}

	public List<VisitedLocation> getVisitedLocations() {
		return visitedLocations;
	}

	public void clearVisitedLocations() {
		visitedLocations.clear();
	}

	public void addUserReward(UserReward userReward) {
		userRewards.add(userReward);
	}

	public List<UserReward> getUserRewards() {
		return userRewards;
	}

	public UserPreference getUserPreferences() {
		return userPreferences;
	}

	public void setUserPreferences(UserPreference userPreferences) {
		this.userPreferences = userPreferences;
	}

	public void setLastVisitedLocation() {
		this.lastVisitedLocation =visitedLocations.get(visitedLocations.size() - 1) ;
	}

	public VisitedLocation getLastVisitedLocation() {
		return lastVisitedLocation;
	}

	public void setTripDeals(List<Provider> tripDeals) {
		this.tripDeals = tripDeals;
	}

	public List<Provider> getTripDeals() {
		return tripDeals;
	}

}
