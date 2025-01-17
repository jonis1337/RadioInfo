package se.umu.cs.appjava.model;

import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * This class is used to parse the api in a background thread. Depending on which constructor is used it will either
 * parse the channels or the schedule for a channel. It will then notify the observer when it is done with the parsing.
 *
 * @author jonatanwestling
 * @version 1.0
 * @date 2024-01-05
 */
public class Worker extends SwingWorker<Void, Void> {
    private final ApiParser apiParser;
    private final ChannelInfo channelInfo;
    private Observer observer;
    //constructor for channel parser
    public Worker(){
        this.apiParser = new ApiParser();
        this.channelInfo = null;
    }
    //constructor for schedule parser
    public Worker(ChannelInfo channelInfo){
        this.apiParser = new ApiParser();
        this.channelInfo = channelInfo;
    }
    /**
     * Method that will parse the api in a background thread.
     *
     * @return null
     */
    @Override
    protected Void doInBackground()  {
        if (channelInfo == null){
            //no channel info passed, parse channels
            try {
                apiParser.parse("https://api.sr.se/api/v2/channels/?pagination=false");
            } catch (IOException e) {
                messageObserverWithError("Could not connect to: " + e.getMessage() + ", please check network connection");
            } catch (ParserConfigurationException e) {
                messageObserverWithError("An internal error with the parser occurred");
            } catch (SAXException e) {
                messageObserverWithError("Format from api seems unsupported");
            } catch (RuntimeException e){
                messageObserverWithError("Invalid response code from server : " + e.getMessage());
            }
            messageObserverWithChannels(apiParser.getHasMapOfSortedChannels());
        } else {
            //channel info passed, parse schedule
            ScheduleBuilder scheduleBuilder = null;
            try {
                scheduleBuilder = new ScheduleBuilder(channelInfo, apiParser);
            } catch (IOException e) {
                messageObserverWithError("Could not connect to:"+ e.getMessage() +", please check network connection");
            } catch (ParserConfigurationException e) {
                messageObserverWithError("An internal error with the parser occurred");
            } catch (SAXException e) {
                messageObserverWithError("Format from api seems unsupported");
            } catch (RuntimeException e){
                messageObserverWithError("Invalid response code from server : " + e.getMessage());
            }
            channelInfo.setSchedule(scheduleBuilder.getPrograms());
            messageObserverWithSchedule(channelInfo.getChannelName());
        }
        return null;
    }
    /**
     * Method that will set the observer for this class (the page controller)
     * @param observer the observer to set
     */
    public void setObserver(Observer observer){
        this.observer = observer;
    }
    /**
     * Method that will notify the observer that the parsing is done and send the channels.
     */
    private void messageObserverWithChannels(HashMap<String, ArrayList<ChannelInfo>> channels){
        observer.channelsAvailable(channels);
    }
    /**
     * Method that will notify the observer that the parsing for a channels schedule is done.
     * it will send the name of the channel since the schedule is stored in the channel object.
     * @param channelName the name of the channel
     */
    private void messageObserverWithSchedule(String channelName){
        observer.scheduleAvailable(channelName);
    }
     private void messageObserverWithError(String message){
        observer.errorOccurred(message);
     }
}
