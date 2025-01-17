package se.umu.cs.appjava.model;
import javax.swing.*;
import java.util.ArrayList;

/**
 * A class that will hold the information about a channel. It will hold the name, id, image, type and tagline and the
 * schedule for the channel if it is cached. The class have getters for all the information, so it can get accessed when
 * needed. It also has a setter for the schedule, so it can be cached when a user have visited the channel.
 *
 * @author Jonatan Westling
 * @version 2.0
 * @date 2024-01-05
 */
public class ChannelInfo {
    private final String channelName;
    private final String channelId;
    private final ImageIcon image;
    private final String channelType;
    private final String tagline;
    private boolean scheduleIsCached;
    private ArrayList<Program> schedule;
    public ChannelInfo(String channelName, String channelId, ImageIcon image, String channelType, String tagline){
        this.channelName = channelName;
        this.channelId = channelId;
        this.image = image;
        this.channelType = channelType;
        this.tagline = tagline;
        this.scheduleIsCached = false;
    }

    /**
     * Getters for the information about the channel
     * @return the information about the channel
     */
    public String getChannelName(){
        return channelName;
    }
    public String getChannelType(){
        return channelType;
    }
    public String getChannelId(){
        return channelId;
    }
    public String getName(){
        return channelName;
    }
    public ImageIcon getImage(){
        return image;
    }
    public String getTagline(){
        return tagline;
    }

    /**
     * A setter for the schedule, so it can be cashed when a user have visited the channel
     * @param schedule the parsed schedule to set
     */
    public synchronized void  setSchedule(ArrayList<Program> schedule){
        this.schedule = schedule;
        this.scheduleIsCached = true;
    }

    /**
     * A getter for the schedule of the channel
     * @return the schedule of the channel
     */
    public synchronized ArrayList<Program> getSchedule(){
        return schedule;
    }

    /**
     * A method to check if the schedule is cashed or not
     * @return true or false
     */
    public synchronized boolean isScheduleCached(){
        return scheduleIsCached;
    }
}
