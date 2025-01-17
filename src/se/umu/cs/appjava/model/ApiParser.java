package se.umu.cs.appjava.model;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * A class that will parse the api and return the information in a more usable format.
 * The class can both parse channels and scheduledepisodes, depending on what is passed in as a parameter.
 * * @author Jonatan Westling
 * * @version 1.0
 * * @date 2024-01-05
 */

public class ApiParser extends DefaultHandler {
    private String channelName;
    private String channelId;
    private String imageURL;
    private String link;
    private String channelType;
    private String tagline;
    private String episodeId;
    private String title;
    private String description;
    private String startTime;
    private String endTime;
    private boolean parsingChannels;
    private boolean isImage;
    private boolean isTagline;
    private boolean isChannelType;
    private boolean isEpisodeId;
    private boolean isTitle;
    private boolean isDescription;
    private boolean isStartTime;
    private boolean isEndTime;
    private ImageIcon image;
    //a list of all channels with necessary information
    private ArrayList<ChannelInfo> channels;
    private ArrayList<Program> programs;

    /**
     * Constructor for the ApiParser
     */
    public ApiParser() {
        channelId = "";
        channelName = "";
        isImage = false;
        isTagline = false;
        isChannelType = false;
        link = "";
        channels = new ArrayList<>();
        parsingChannels = true;
        programs = new ArrayList<>();
    }

    /**
     * Method that will parse the api and return a list of channels
     *
     * @param link the link to the api
     */
    public void parse(String link) throws IOException, ParserConfigurationException, SAXException {
        //get the link to visit
        this.link = link;
        //check if we are parsing channels or scheduledepisodes
        parsingChannels = !link.contains("scheduledepisodes");
        HttpURLConnection connection = null;

            URL url = new URL(link);
            connection = (HttpURLConnection) url.openConnection();
            //obtain response code
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse(connection.getInputStream(), this);

        connection.disconnect();
    }

    /**
     * Method that will run before parsing first element and will determine what we are parsing
     */
    @Override
    public void startDocument() {
        if (parsingChannels) {
            channels = new ArrayList<>();
        } else {
            programs = new ArrayList<>();
        }
    }

    /**
     * Method that will run when the parser reaches a new element
     * @param uri the uri of the element
     * @param localName the local name of the element
     * @param qName the qName of the element
     * @param attributes the attributes of the element
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        //maybe need error handling if there are channels that miss subsections
        if (parsingChannels) {
            startElementParsingChannels(qName, attributes);
        } else {
            startElementParsingSchedule(qName, attributes);
        }
    }

    /**
     * Method that will run when the parser reaches a new element that is a channel
     *
     * @param qName      the qName of the element
     * @param attributes the attributes of the element
     */
    private void startElementParsingChannels(String qName, Attributes attributes) {
        switch (qName) {
            case "channel":
                //we in beginning of a new channel, gather info about the channel
                channelName = attributes.getValue("name");
                channelId = attributes.getValue("id");
                break;
            case "image":
                isImage = true;
                break;
            case "tagline":
                isTagline = true;
                break;
            case "channeltype":
                isChannelType = true;
                break;

        }
    }

    /**
     * Method that will run when the parser reaches a new element that is a scheduledepisode
     *
     * @param qName      the qName of the element
     * @param attributes the attributes of the element
     */
    private void startElementParsingSchedule(String qName, Attributes attributes) {
        switch (qName) {
            case "episodeid":
                isEpisodeId = true;
                break;
            case "title":
                isTitle = true;
                break;
            case "description":
                isDescription = true;
                break;
            case "starttimeutc":
                isStartTime = true;
                break;
            case "endtimeutc":
                isEndTime = true;
                break;
            case "imageurl":
                isImage = true;
                break;
        }
    }

    /**
     * Method that will catch the characters between the start and end node
     *
     * @param ch     the characters of the element
     * @param start  the start index of the characters
     * @param length the length of the characters
     */
    @Override
    public void characters(char[] ch, int start, int length) {
        if (parsingChannels) {
            charactersParsingChannels(ch, start, length);
        } else {
            charactersParsingSchedule(ch, start, length);
        }
    }

    /**
     * Method that will catch the characters between the start and end node if it is parsing channels
     *
     * @param ch     the characters of the element
     * @param start  the start index of the characters
     * @param length the length of the characters
     */
    private void charactersParsingChannels(char[] ch, int start, int length) {
        if (isImage) {
            imageURL = new String(ch, start, length);
        } else if (isTagline) {
            tagline = new String(ch, start, length);
        } else if (isChannelType) {
            channelType = new String(ch, start, length);
        }
    }

    /**
     * Method that will catch the characters between the start and end node if it is parsing schedule
     *
     * @param ch     the characters of the element
     * @param start  the start index of the characters
     * @param length the length of the characters
     */
    private void charactersParsingSchedule(char[] ch, int start, int length) {
        if (isEpisodeId) {
            episodeId = new String(ch, start, length);
        } else if (isTitle) {
            title = new String(ch, start, length);
        } else if (isDescription) {
            description = new String(ch, start, length);
        } else if (isStartTime) {
            startTime = new String(ch, start, length);
        } else if (isEndTime) {
            endTime = new String(ch, start, length);
        } else if (isImage) {
            imageURL = new String(ch, start, length);
        }
    }

    /**
     * Method that will run when the parser reaches the end of an element. The method will reset the flags
     *
     * @param uri       the uri of the element
     * @param localName the local name of the element
     * @param qName     the qName of the element
     */
    @Override
    public void endElement(String uri, String localName, String qName) {
        //reset flag save information
        if (parsingChannels) {
            endElementParsingChannels(qName);
        } else {
            endElementParsingSchedule(qName);
        }
    }

    /**
     * Method that will reset flags and save information if it is parsing channels depending on the qName.
     *
     * @param qName the qName of the element
     */
    private void endElementParsingChannels(String qName) {
        switch (qName) {
            case "channel":
                URL url = null;
                try {
                    url = new URL(imageURL);
                } catch (MalformedURLException e) {
                    //handle exception better...
                    System.out.println("Error: creating image url for channel");
                }
                if (url != null) {
                    image = new ImageIcon(url);
                }
                //add the current channel to the list of channels
                ChannelInfo currentChannel = new ChannelInfo(channelName, channelId, image, channelType, tagline);
                channels.add(currentChannel);
                break;

            case "image":
                isImage = false;
                break;
            case "tagline":
                isTagline = false;
                break;
            case "channeltype":
                isChannelType = false;
                break;

        }
    }

    /**
     * Method that will reset flags and save information if it is parsing schedule depending on the qName.
     *
     * @param qName the qName of the element
     */
    private void endElementParsingSchedule(String qName) {
        switch (qName) {
            case "scheduledepisode":
                Program newProgram = new Program(title, episodeId, description, startTime, endTime, imageURL);
                programs.add(newProgram);
                break;
            case "episodeid":
                isEpisodeId = false;
                break;
            case "title":
                isTitle = false;
                break;
            case "description":
                isDescription = false;
                break;
            case "starttimeutc":
                isStartTime = false;
                break;
            case "endtimeutc":
                isEndTime = false;
                break;
            case "imageurl":
                isImage = false;
                break;
        }
    }

    /**
     * Method that will return a list of all channels
     *
     * @return a list of all channels
     */
    public ArrayList<Program> getPrograms() {
        return programs;
    }

    /**
     * Method that will return a map of all channels sorted by channel type
     *
     * @return a map of all channels sorted by channel type where the key is the channel type
     * and the value is a list of the channels.
     */
    public LinkedHashMap<String, ArrayList<ChannelInfo>> getHasMapOfSortedChannels() {
        //create the map
        LinkedHashMap<String, ArrayList<ChannelInfo>> channelTypeMap = new LinkedHashMap<>();
        for (ChannelInfo channel : channels) {
            String type = channel.getChannelType();
            //if map does not contain the key add it and create a new list for those channels
            if (!channelTypeMap.containsKey(type)) {
                channelTypeMap.put(type, new ArrayList<>());
            }
            //add the channel to the list of channels
            channelTypeMap.get(type).add(channel);
        }
        return channelTypeMap;
    }
}
