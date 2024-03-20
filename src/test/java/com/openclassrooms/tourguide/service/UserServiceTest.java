package com.openclassrooms.tourguide.service;

/*class UserServiceTest {
	private GpsUtil gpsUtil;
	private RewardsService rewardsService;
	//private UserDataLoader userDataLoader;
	
	/*@BeforeEach
	public void init() throws Exception {
		gpsUtil = new GpsUtil();
		rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		userDataLoader = new UserDataLoader();
	}
	
	@Test
	public void testAddUser() throws Exception {
		UserService userService = new UserService();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService,userDataLoader);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		 userService.addUser(user);
		 userService.addUser(user2);

		User retrivedUser =  userService.getUser(user.getUserName());
		User retrivedUser2 =  userService.getUser(user2.getUserName());
		Tracker tracker= new Tracker(tourGuideService, userService);
		tracker.stopTracking();

		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}

	@Test
	public void testGetAllUsers() throws Exception {	
		UserService userService = new UserService();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService,userDataLoader);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userService.addUser(user);
		userService.addUser(user2);

		List<User> allUsers = userService.getAllUsers();

		Tracker tracker= new Tracker(tourGuideService, userService);
		tracker.stopTracking();

		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}
}*/
