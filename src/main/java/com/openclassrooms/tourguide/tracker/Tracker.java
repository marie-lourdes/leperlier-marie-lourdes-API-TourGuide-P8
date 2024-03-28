package com.openclassrooms.tourguide.tracker;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.service.GpsUtilService;
import com.openclassrooms.tourguide.service.UserService;

public class Tracker extends Thread {
	private static final Logger logger = LogManager.getLogger(Tracker.class);
	private static final long trackingPollingInterval = TimeUnit.SECONDS.toSeconds(5);
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private UserService userService;
	private GpsUtilService gpsUtilService;
	private String threadName;
	private boolean stop = false;
	private final Map<User, Boolean> completedTrackingUsersMap = new HashMap<>();

	public Tracker(String threadName) {
		this.threadName = threadName;
		startTracking(threadName);
	}

	@Override
	public void run() {
		StopWatch stopWatch = new StopWatch();
		while (true) {
			if (Thread.currentThread().isInterrupted() || stop) {
				logger.debug("Tracker stopping {}", threadName);
				this.stopTracking();
				break;
			}

			List<User> users = null;
			try {
				users = userService.getAllUsers();
			} catch (InterruptedException | ExecutionException e) {
				this.stopTracking();
				logger.error(e.getMessage());
				break;
			}

			users.forEach(user -> completedTrackingUsersMap.put(user, false));
			logger.debug("Begin Tracker. Tracking {}, with {}  users. ", threadName, users.size());
			stopWatch.start();
			users.forEach(user -> {
				try {
					gpsUtilService.trackUserLocation(user, userService);
				} catch (InterruptedException e) {
					logger.error("Tracker interrupted {}", threadName);
					this.stopTracking();
				} catch (ExecutionException e) {
					logger.error(e.getMessage());
				}
			});

			boolean notFinished = true;
			while (notFinished) {
				try {
					logger.debug("Waiting for tracking to finish...: {} ", threadName);
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					logger.error("Tracker interrupted, {} ", threadName);
					this.stopTracking();
					break;
				}

				if (!completedTrackingUsersMap.containsValue(false)) {
					notFinished = false;
				}
			}

			completedTrackingUsersMap.clear();

			stopWatch.stop();
			logger.debug("Tracker {} Time Elapsed: {}  seconds.", threadName,
					TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
			stopWatch.reset();

			try {
				logger.debug("Tracker sleeping {} ", threadName);
				TimeUnit.SECONDS.sleep(trackingPollingInterval);
			} catch (InterruptedException e) {
				this.stopTracking();
				break;
			}
		}
	}

	public synchronized void finalizeTrackUser(User user) {
		completedTrackingUsersMap.put(user, true);
	}

	public void startTracking(String threadName) {
		logger.debug("Starting {}", threadName);
		executorService.submit(this);
	}

	/**
	 * Assures to shut down the Tracker thread
	 */
	public void stopTracking() {
		stop = true;
		executorService.shutdownNow();
	}
}