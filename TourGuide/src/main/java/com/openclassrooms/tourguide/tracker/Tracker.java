package com.openclassrooms.tourguide.tracker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;

/* Les test appel 4 fois la creation d user avec tours guide service , 4 threads
 * au 1er  l appel de la creation d user de tourGuideService service le tracker demarre 
 * puis mis en attente le 2eme et le 3ème appel le tracker rencontre des erreurs de threads  et de lecture des users apres la 2eme et 3 ème appel de creation d user
 * et stoppe et recommence au dernier appel du TourGuideService pour la creation user
 * Le tout avec un seul thread et pas de pool pour gerer la mise en attente de plusieurs thread
 */
public class Tracker extends Thread {
	private Logger logger = LoggerFactory.getLogger(Tracker.class);
	private static final long trackingPollingInterval = TimeUnit.MINUTES.toSeconds(5);//5 min (converti en secondes)trop long  la mise en attente du thread
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private final TourGuideService tourGuideService;
	private boolean stop = false;// !!!initilisez a true 

	public Tracker(TourGuideService tourGuideService) {
		this.tourGuideService = tourGuideService;

		executorService.submit(this);
	}

	/**
	 * Assures to shut down the Tracker thread
	 */
	public void stopTracking() {
		stop = true; //!!!reaffectez a false pour stopper le thread  et renommer la varible avec run= false car un while (!stop=false) ne demarre pas la boucle et les instructions mais une valeur true
		executorService.shutdownNow();
	}

	@Override
	public void run() {
		StopWatch stopWatch = new StopWatch();
		while (true) {//?? provoque boucle infinie si toujours a true*
			// passez en parametre de la boucle while la variable stop , et a la fin de la method run  pour stopper le thread reafectez la variable a false pour entrer a nouveau dans la boucle
			if (Thread.currentThread().isInterrupted() || stop) {
				logger.debug("Tracker stopping");
				break;
			}

			List<User> users = tourGuideService.getAllUsers();
			logger.debug("Begin Tracker. Tracking " + users.size() + " users.");
			stopWatch.start();
			users.forEach(u -> tourGuideService.trackUserLocation(u));
			stopWatch.stop();
			logger.debug("Tracker Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
			stopWatch.reset();
			try {
				logger.debug("Tracker sleeping");
				TimeUnit.SECONDS.sleep(trackingPollingInterval);//?? provoque des erreurs du 2 eme  du 2eme appel de tourGuideService
			} catch (InterruptedException e) {
				break;
			}
		}

	}
}
