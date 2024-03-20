package com.openclassrooms.tourguide.tracker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.service.UserService;

/* Les test appel 4 fois la creation d user avec tours guide service , 4 threads
 * au 1er  l appel de la creation d user de userService service le tracker demarre 
 * puis mis en attente le 2eme et le 3ème appel le tracker rencontre des erreurs de threads  et de lecture des users apres la 2eme et 3 ème appel de creation d user
 * et stoppe et recommence au dernier appel du TourGuideService pour la creation user
 * Le tout avec un seul thread et pas de pool pour gerer la mise en attente de plusieurs thread
 */
public class Tracker extends Thread {
	private Logger logger = LoggerFactory.getLogger(Tracker.class);
	private static final long trackingPollingInterval = TimeUnit.SECONDS.toSeconds(5);//5 min (converti en secondes)trop long  la mise en attente du thread
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private final UserService userService;
	private final TourGuideService tourGuideService;
	private boolean stop = false;// !!!initilisez a true 

	public Tracker(UserService userService) {
		this.userService = userService;
		this.tourGuideService = null;
		executorService.submit(this);
	}
	public Tracker(TourGuideService tourGuideService) {
		this.userService = null;
		this.tourGuideService = tourGuideService;
		executorService.submit(this);
	}
	/**
	 * Assures to shut down the Tracker thread
	 */
	public void stopTracking() {
		stop = true; // !!!reaffectez a false pour stopper le thread et renommer la varible avec run=
						// false car un while (!stop=false) ne demarre pas la boucle et les instructions
						// mais une valeur true
		executorService.shutdownNow();
	}

	@Override
	public void run() {
		StopWatch stopWatch = new StopWatch();
		while (stop) {// ?? provoque boucle infinie si toujours a true*
			// passez en parametre de la boucle while la variable stop , et a la fin de la
			// method run pour stopper le thread reafectez la variable a false pour entrer a
			// nouveau dans la boucle
			try {
				if (Thread.currentThread().isInterrupted()) {
					// testez la condition sans la varinale stop et le placez dans lewhile pour
					// testerl exception ConccurrentModificationexception
					// le programme est interrompu si le thread est interrompu mais ne les thread
					// attribut "interrupted" =false dans le debug donc ne devrait pas stopper le
					// programme
					logger.debug("Tracker stopping");
					//stopTracking();
					 break;
				}

				List<User> users = userService.getAllUsers();
				logger.debug("Begin Tracker. Tracking " + users.size() + " users.");
				stopWatch.start();
				users.forEach(u -> userService.trackUserLocation(u));
				stopWatch.stop();
				logger.debug(
						"Tracker Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
				stopWatch.reset();

				logger.debug("Tracker sleeping");
				TimeUnit.SECONDS.sleep(trackingPollingInterval);// ?? provoque des erreurs du 2 eme du 2eme appel de
																// userService
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
				// stopTracking();
			}
			break;
		}
	}
}