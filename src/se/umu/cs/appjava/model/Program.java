package se.umu.cs.appjava.model;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Class that will hold the information about a program, such as name, description, start and end time.
 * It will also convert the utc time to the local time of the user and format it to a more readable format.
 * The class have getters for all the information so it can get accessed when building the view.
 *
 * @author Jonatan Westling
 * @version 1.0
 * @date 2024-01-05
 */
public class Program {
    private String episodeId;
    private String programName;
    private String description;
    private String startTimeUtc;
    private String endTimeUtc;
    private String image;
    private String formattedStartTime;
    private String formattedEndTime;
    private ZonedDateTime zonedLocalStartTime;
    private ZonedDateTime zonedLocalEndTime;

    public Program(String programName, String episodeId, String description, String startTimeUtc, String endTimeUtc, String image){

        this.programName = programName;
        this.episodeId = episodeId;
        this.description = description;
        this.startTimeUtc = startTimeUtc;
        this.endTimeUtc = endTimeUtc;
        this.image = image;
        convertTimeToLocalTime();

    }

    /**
     * Will convert the utc time to the local time of the users current time zone.
     */
    private void convertTimeToLocalTime(){
        ZonedDateTime utcInstantStart = ZonedDateTime.parse(startTimeUtc);
        ZonedDateTime utcInstantEnd = ZonedDateTime.parse(endTimeUtc);
        //convert to zoned date time in the local time
        zonedLocalStartTime = utcInstantStart.withZoneSameInstant(ZoneId.systemDefault());
        zonedLocalEndTime = utcInstantEnd.withZoneSameInstant(ZoneId.systemDefault());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM", Locale.ENGLISH);
        this.formattedStartTime= zonedLocalStartTime.format(formatter);
        this.formattedEndTime = zonedLocalEndTime.format(formatter);
    }

    /**
     * Getter for the converted time
     * @return the converted start time
     */
    public ZonedDateTime getZonedLocalDateEndTIme(){
        return zonedLocalEndTime;
    }

    /**
     * Getter for the converted time
     * @return the converted start end
     */
    public ZonedDateTime getZonedLocalDateStartTime(){
        return zonedLocalStartTime;
    }
    /**
     * Getter for the program name
     * @return the program name
     */
    public String getProgramName() {
        return programName;
    }

    /**
     * Getter for the start time
     * @return the formatted start time used for the users view
     */
    public String getStartTime(){
        return formattedStartTime;
    }

    /**
     * Getter for the end time
     * @return the formatted end time used for the users view
     */
    public String getEndTime(){
        return formattedEndTime;
    }

    /**
     * Getter for the url to the image
     * @return the url to the image as a string
     */
    public String getImage(){
        return image;
    }

    /**
     * Getter for the description of the program
     * @return the description
     */
    public String getDescription(){
        return description;
    }
}
