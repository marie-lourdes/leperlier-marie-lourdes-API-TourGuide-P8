package com.openclassrooms.tourguide.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.tourguide.model.RecommendedUserAttraction;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.service.GpsUtilService;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.service.UserService;

import gpsUtil.location.VisitedLocation;
import tripPricer.Provider;

@RestController
@RequestMapping("tourguide")
public class TourGuideController {
	private Logger logger = LoggerFactory.getLogger(TourGuideController .class);
	
	private TourGuideService tourGuideService;
	private UserService userService;
	private final GpsUtilService gpsUtilService;
	
	public TourGuideController(TourGuideService tourGuideService, UserService userService,GpsUtilService gpsUtilService) {
		this.tourGuideService= tourGuideService;
		this.userService=  userService;
		this.gpsUtilService= gpsUtilService;
	}
		
    @GetMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @GetMapping("/getLocation") 
    public VisitedLocation getLocation(@RequestParam String userName) {
    	User userFoundByName = userService.getUser(userName);
    	try {
			if(userFoundByName.getVisitedLocations().size() == 0){
				gpsUtilService.trackUserLocation(userFoundByName,userService);
			}
					
		} catch (InterruptedException | ExecutionException e) {
			logger.error(e.getMessage());
		}
    	return  userService.getUserLocation(userFoundByName);
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
      	User userFoundByName = userService.getUser(userName);
    	try {
			if(null ==userFoundByName.getLastVisitedLocation()){
				gpsUtilService.trackUserLocation(userFoundByName,userService);
			}
					
		} catch (InterruptedException | ExecutionException e) {
			logger.error(e.getMessage());
		}
    	VisitedLocation visitedLocation = userService.getLastUserLocation(userFoundByName);	
    	return tourGuideService.getNearByAttractions(visitedLocation,userService.getUser(userName));
    }
    
    @GetMapping("/getRewards") 
    public List<UserReward> getRewards(@RequestParam String userName) {
    	return userService.getUserRewards(userService.getUser(userName)); 
    }
       
    @GetMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
    	return tourGuideService.getTripDeals(userService.getUser(userName));
    }
 
}