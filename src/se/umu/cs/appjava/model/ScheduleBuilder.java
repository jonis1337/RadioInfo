package se.umu.cs.appjava.model;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
/**
 * Class that will build the schedule for a channel. It will parse the api twice and filter out the programs that are not
 * within the 12 hour time frame. The worker can the get the final schedule with the method getPrograms().
 *
 * @author Jonatan Westling
 * @version 1.0
 * @date 2024-01-05
 */

public class ScheduleBuilder {
    private ArrayList<Program> twelveHourPast;
    private ArrayList<Program> twelveHourFuture;
    private ArrayList<Program> finalSchdule;
    private ApiParser apiParser;
    private ChannelInfo channelInfo;

    public ScheduleBuilder(ChannelInfo channelInfo, ApiParser apiParser) throws IOException, ParserConfigurationException, SAXException {
        this.channelInfo = channelInfo;
        this.apiParser = apiParser;
        finalSchdule = new ArrayList<>();
        buildSchedule();
    }

    /**
     * Method that will build the schedule for a channel
     */
    private void buildSchedule() throws IOException, ParserConfigurationException, SAXException {
        //get today's date
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime pastTime = timeNow.minusHours(12);
        LocalDateTime futureTime = timeNow.plusHours(12);
        //format the date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //get date for 12 hours in the past
        String pastDate = timeNow.minusHours(12).format(formatter);
        //to store all programs in
        twelveHourPast = new ArrayList<>();
        apiParser.parse("https://api.sr.se/api/v2/scheduledepisodes/?channelid=" + channelInfo.getChannelId() + "&date=" + pastDate + "&pagination=false");
        twelveHourPast = apiParser.getPrograms();

        filterPrograms(twelveHourPast, pastTime, futureTime);

        String futureDate = timeNow.plusHours(12).format(formatter);
        twelveHourFuture = new ArrayList<>();
        apiParser.parse("https://api.sr.se/api/v2/scheduledepisodes/?channelid=" + channelInfo.getChannelId() + "&date=" + futureDate + "&pagination=false");
        twelveHourFuture = apiParser.getPrograms();

        filterPrograms(twelveHourFuture, pastTime, futureTime);
    }

    /**
     * Method that will filter out the programs that have times outside the bounds
     * @param schedule the schedule of the day
     * @param pastTime the maximum time limit in past
     * @param futureTime the maximum time limit in future
     */
    private void filterPrograms(ArrayList<Program> schedule, LocalDateTime pastTime, LocalDateTime futureTime){
        //now filter the programs
        for (Program program : schedule){
            //get the start time for the program and convert it to local date time
            ZonedDateTime programStartZonedDate = program.getZonedLocalDateStartTime();
            LocalDateTime programsStartLocalDate = programStartZonedDate.toLocalDateTime();
            //check if the program is within the bounds
            if (programsStartLocalDate.isAfter(pastTime) && programsStartLocalDate.isBefore(futureTime)){
                finalSchdule.add(program);
            }
        }
    }

    /**
     * Getter for the final schedule
     * @return the final schedule
     */
    public ArrayList<Program> getPrograms(){
        return finalSchdule;
    }

}
