package se.umu.cs.appjava.model;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class that handles the update timer for the program. It is set to update the programs cached schedules every hour.
 *
 * @author Jonatan Westling
 * @version 1.0
 * @date 2024.01.05
 */
public class UpdateTimer {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    /**
     * Method that starts the timer and will message the observer when the timer has expired.
     */
    private Observer observer;
    public void startTimer(){
        scheduler.schedule(this::messageObserverUpdate, 1, TimeUnit.HOURS);
    }
    /**
     * Method that sets the observer for the timer.
     * @param observer the observer to be set.
     */
    public void setObserver(Observer observer){
        this.observer = observer;
    }
    /**
     * Method that will message the observer when the timer has expired.
     */
    private void messageObserverUpdate(){
        observer.update();
    }


}
