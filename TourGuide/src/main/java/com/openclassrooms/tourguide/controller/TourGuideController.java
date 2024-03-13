package com.openclassrooms.tourguide.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.tourguide.model.RecommendedUserAttraction;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.service.TourGuideService;

import gpsUtil.location.VisitedLocation;
import tripPricer.Provider;

@RestController
@RequestMapping("tourguide")
public class TourGuideController {
	private TourGuideService tourGuideService;
	
	public TourGuideController(TourGuideService tourGuideService) {
		this.tourGuideService= tourGuideService;
	}
		
    @GetMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @GetMapping("/getLocation") 
    public VisitedLocation getLocation(@RequestParam String userName) {
    	return tourGuideService.getUserLocation(tourGuideService.getUser(userName));
    }
    
    //  TODO: Change this method to no longer return a List of Attractions.
 	//  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
 	//  Return a new JSON object that contains:
    	// Name of Tourist attraction, 
        // Tourist attractions lat/long, 
        // The user's location lat/long, 
        // The distance in miles between the user's location and each of the attractions.
        // The reward points for visiting each Attraction.
        //    Note: Attraction reward points can be gathered from RewardsCentral
    @GetMapping("/getNearbyAttractions") 
    public List<RecommendedUserAttraction> getNearbyAttractions(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(tourGuideService.getUser(userName));	
    	return tourGuideService.getNearByAttractions(visitedLocation,tourGuideService.getUser(userName));
    }
    
    @GetMapping("/getRewards") 
    public List<UserReward> getRewards(@RequestParam String userName) {
    	return tourGuideService.getUserRewards(tourGuideService.getUser(userName));
    }
       
    @GetMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
    	return tourGuideService.getTripDeals(tourGuideService.getUser(userName));
    }
 
}