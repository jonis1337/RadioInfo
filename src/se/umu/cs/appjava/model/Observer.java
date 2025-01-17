package se.umu.cs.appjava.model;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * This interface is used to implement the observer pattern.
 * It is used to notify the PageController when the SwingWorker is done parsing the api or when the
 * update timer has triggered. All methods are implemented in the PageController and the methods are
 * running on the EDT to keep it thread safe.
 * @author Jonatan Westling
 * @version 1.0
 * @date 2023-12-28
 */
public interface Observer {
    /**
     * This method is used to notify the PageController when the SwingWorker is done parsing the api.
     * @param channels A HashMap containing all the channels.
     */
     void channelsAvailable(HashMap<String, ArrayList<ChannelInfo>> channels);
    /**
     * This method is used to notify the PageController when the SwingWorker is done parsing the ap.
     * @param channelName the name of the channel where its schedule has been parsed.
     */
    void scheduleAvailable(String channelName);
    /**
     * This method is used to notify the PageController when the update timer has triggered.
     */
    void update();

    void errorOccurred(String errorMessage);

}
