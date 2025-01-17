package se.umu.cs.appjava.controller;

import se.umu.cs.appjava.model.*;
import se.umu.cs.appjava.view.ChannelView;
import se.umu.cs.appjava.view.MainWindow;
import se.umu.cs.appjava.view.ProgramView;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is the controller class that is responsible for managing the pages in the GUI and the actions
 * that can be performed on them. It also communicates with the model classes to get the data that is needed
 * when the user performs an action. The class gets notified when the model classes are done parsing the api
 * or when the timer has expired via the observer pattern.
 *
 * @author Jonatan Westling
 * @version 1.0
 * @date 2024-01-05
 */
public class PageController implements ActionListener, Observer {

    MainWindow mainWindow;
    HashMap<String, ArrayList<ChannelInfo>> sortedChannels;
    private volatile boolean isParsing;
    private boolean networkErrorOccurred;
    public PageController(MainWindow mainWindow){
        this.mainWindow = mainWindow;
        this.mainWindow.setHomeActionListener(this);
        this.mainWindow.setHelpActionListener(this);
        this.mainWindow.setUpdateActionListener(this);
        this.mainWindow.setExitActionListener(this);
        this.isParsing = false;
        this.networkErrorOccurred = false;
        loadSortedChannels();
        setupTimer();
    }

    /**
     * Method that will be called when an action is performed, it will check what action was performed and
     * preform the appropriate action.
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().startsWith("is channel")){
            channelEvent(e);
        }
        switch (e.getActionCommand()){
            case "home":
                homeEvent();
                break;
            case "help":
                helpEvent();
                break;
            case "update":
                //if user had an network error att launch, try parsing again
                if (networkErrorOccurred){
                    networkErrorOccurred = false;
                    loadSortedChannels();
                } else {
                    updateEvent();
                }
                break;
            case "exit":
                exitEvent();
                break;
        }
    }

    /**
     * Method that will be called when the home button is pressed, it will display the home page
     */
    private void homeEvent(){
        mainWindow.getCardLayout().show(mainWindow.getCardPanel(), "homePage");
    }

    /**
     * Method that will be called when the update button is pressed, it will update the channels and the cashed schedules
     */
    private void updateEvent(){
        //check if channels are currently parsing
        if (isParsing){
            JOptionPane.showMessageDialog(mainWindow, "Kanaler laddas redan", "Fel", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //update all cashed schedules, let a worker parse each schedule
        for(String channelType : sortedChannels.keySet()){
            ArrayList<ChannelInfo> channelInfos = sortedChannels.get(channelType);
            for (ChannelInfo channelInfo : channelInfos){
                if (channelInfo.isScheduleCached()){
                    //create a worker to parse the schedule
                    Worker worker1 = new Worker(channelInfo);
                    worker1.setObserver(this);
                    worker1.execute();
                }
            }
        }
        //reset the timer
        setupTimer();
    }

    /**
     * Method that will be called when the help button is pressed, it will display a help message
     */
    private void helpEvent(){
        JOptionPane.showMessageDialog(mainWindow, "Dubbelklicka på en kanal för  detaljerad information angående sändningen",
                "Hjälp", JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * Method that will be called when the exit button is pressed, it will exit the program
     */
    private void exitEvent(){
        //exit the program
        int response = JOptionPane.showConfirmDialog(mainWindow, "Vill du avsluta programmet?", "RadioInfo", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION){
            System.exit(0);
        }
    }

    /**
     * Method that will be called when a channel is clicked, it will display the schedule for that channel.
     * It will also check if the schedule is already cached, if it is not it will let a worker parse it.
     * @param e the action event
     */
    private void channelEvent(ActionEvent e){
        //a channel has been clicked now display the channel info, ignore the "is channel" part of the string
        String channelName = e.getActionCommand().substring(10);

        for (String channelType:sortedChannels.keySet()){
            ArrayList<ChannelInfo> channelInfos = sortedChannels.get(channelType);
            for (ChannelInfo channelInfo:channelInfos){
                if (channelInfo.getChannelName().equals(channelName)){
                    if (!channelInfo.isScheduleCached()){
                        //if schedule not cached, let worker parse it
                        Worker parseWorker = new Worker(channelInfo);
                        parseWorker.setObserver(this);
                        parseWorker.execute();
                        //add the channel view to the main window
                        ChannelView channelView = new ChannelView(channelInfo, this);
                        mainWindow.addChannelView(channelView, channelName);
                        mainWindow.getCardLayout().show(mainWindow.getCardPanel(), channelName);
                    } else {
                        //schedule already cached so just display it
                        mainWindow.getCardLayout().show(mainWindow.getCardPanel(), channelName);
                    }
                }
            }
        }
    }
    /**
     * Method that will be called in the beginning of the program to load the channels at a separate thread
     */
    private void loadSortedChannels() {
        isParsing = true;
        Worker channelWorker = new Worker();
        channelWorker.setObserver(this);
        channelWorker.execute();
    }

    /**
     * Method that will set up a timer that will notify the controller after one hour has passed
     */
    private void setupTimer(){
        UpdateTimer timer = new UpdateTimer();
        timer.setObserver(this);
        timer.startTimer();
    }

    /**
     * Method that will be called when the channels are available, it will use swing utilities to make the update
     * on the edt thread.
     * @param channels the channels that are available
     */
    @Override
    public void channelsAvailable(HashMap<String, ArrayList<ChannelInfo>> channels){
        isParsing = false;
        //update the sorted channels
        sortedChannels = channels;
        //make the update on the edt thread
        SwingUtilities.invokeLater(() -> mainWindow.updateChannelsMenu(sortedChannels, this));
    }

    /**
     * Method that will be called when the schedule is available, it will use swing utilities to make the update
     * on the edt thread.
     * NOTE: that this method is synchronized since it can be called from multiple threads.
     * @param channelName the name of the channel that has a schedule available
     */
    @Override
    public synchronized void  scheduleAvailable(String channelName){
        //make the update on the edt thread
        SwingUtilities.invokeLater(() -> {
            ChannelView currentChannelView = mainWindow.getChannelView(channelName);
            if (currentChannelView != null ) {
                currentChannelView.updateSchedule();
            } else {
                JOptionPane.showMessageDialog(mainWindow, "Kunde inte uppdatera kanalen",
                        "Fel", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    /**
     * Method that will be called when the timer has expired and trigger an updateEvent on the edt thread
     */
    @Override
    public void update() {
        SwingUtilities.invokeLater(this::updateEvent);
    }
    /**
     * Method that will be called when an error has occurred, it will display an error message on the edt thread
     * NOTE: that this method is synchronized since it can be called from multiple threads.
     * @param errorMessage the error message to display
     */
    @Override public synchronized void errorOccurred(String errorMessage){
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainWindow, errorMessage, "fel", JOptionPane.ERROR_MESSAGE));
        if (errorMessage.contains("api.sr.se")){
            networkErrorOccurred = true;
        }
    }
    /**
     * Method that will be called when a program is double-clicked, it will display a detailed view of the program
     * @param program the program that was double-clicked
     */
    public void programDoubleClicked(Program program){
        new ProgramView(mainWindow, program);
    }
}
