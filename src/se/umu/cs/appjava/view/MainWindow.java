package se.umu.cs.appjava.view;
import java.util.List;
import se.umu.cs.appjava.model.ChannelInfo;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * This class is the main window of the application. It contains the menu bar and the card panel that holds the different channel views.
 * It is building the meu bar and the home page and is also responsible for updating the channels menu when the api has been parsed.
 * It can also display different channel views in the card panel that the controller class decides.
 *
 * @author jonatanwestling
 * @version 1.0
 * @date 2023-12-28
 */

public class MainWindow extends JFrame{
    private JLabel welcomeText;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JMenuBar menuBar;
    private JMenu channels;
    private JMenu tools;
    private JMenuItem home;
    private JMenuItem help;
    private JMenuItem update;
    private  JMenuItem exit;
    private HashMap<String, ChannelView> channelViews;
    public MainWindow(){
        channelViews = new HashMap<>();
        initComponents();
    }
    /**
     * This method initializes the components of the main window
     * and also sets the title, size, layout and location of the window
     */
    private void initComponents(){
        setTitle("RadioInfo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setMinimumSize(new Dimension(400, 200));
        //set up the menu bar
        setJMenuBar(initMenu());

        //setup layout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        add(cardPanel, BorderLayout.CENTER);

        setUpHomePage();

        setLocationRelativeTo(null);
        setVisible(true);

    }
    /**
     * This method initializes the menu bar and adds the menu items
     * @return the created menu bar
     */
    private JMenuBar initMenu(){
        menuBar = new JMenuBar();
        channels = new JMenu("Kanaler");
        tools = new JMenu("Verktyg");
        //channels will be added as swingworker is done parsing the api
        JMenuItem loading = new JMenuItem("kanaler laddas...");
        loading.setEnabled(false);
        channels.add(loading);

        //create home menu item and add action listener
        home = new JMenuItem("Hem");
        home.setActionCommand("home");
        tools.add(home);

        help = new JMenuItem("Hjälp");
        help.setActionCommand("help");
        tools.add(help);

        //create update menu item and add action listener
        update = new JMenuItem("Uppdatera");
        update.setActionCommand("update");
        tools.add(update);
        //create exit menu item and add action listener
        exit = new JMenuItem("Avsluta");
        exit.setActionCommand("exit");
        tools.add(exit);
        //add menus to menu bar
        menuBar.add(channels);
        menuBar.add(tools);

        return menuBar;
    }

    /**
     * This method sets up the home page of the application and adds it to the card panel
     */
    private void setUpHomePage(){
        JPanel homePage = new JPanel(new BorderLayout());
        //create header text
        welcomeText = new JLabel("Välkommen till RadioInfo", SwingConstants.CENTER);
        //nice fonts: Microsoft JhengHei UI Light,
        welcomeText.setFont(new Font("Microsoft JhengHei UI Light", Font.PLAIN, 30));
        welcomeText.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        homePage.add(welcomeText, BorderLayout.CENTER);
        //create short info text
        JLabel infoText = new JLabel("Välj en kanal i menyn ovan för att visa information om kanalen", SwingConstants.CENTER);
        infoText.setFont(new Font("Microsoft JhengHei UI Light", Font.PLAIN, 15));
        infoText.setBorder(BorderFactory.createEmptyBorder(0, 0, 250, 0));
        homePage.add(infoText, BorderLayout.SOUTH);
        //create image
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("src/images/sr.jpeg")));
        Image image = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(image);
        JLabel imageHolder = new JLabel(imageIcon);
        imageHolder.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        homePage.add(imageHolder, BorderLayout.NORTH);
        //add home page to card panel
        cardPanel.add(homePage, "homePage");

    }

    /**
     * This method updates the channels menu
     * @param channelsMap: the map with the channels
     * NOTE: To avoid the EDT from freezing when updating the channels menu, the menu is updated in a swingworker
     */
    //testing this method for better performance
    public void updateChannelsMenu(HashMap<String, ArrayList<ChannelInfo>> channelsMap, ActionListener actionListener) {
        new SwingWorker<Void, JMenu>() {
            @Override
            protected Void doInBackground() {
                // Process data in the background
                channels.removeAll();
                channelsMap.forEach((key, channelList) -> {
                    JMenu submenu = new JMenu(key);
                    for (ChannelInfo channel : channelList) {
                        JMenuItem menuItem = new JMenuItem(channel.getName());
                        //add image to menu item
                        ImageIcon imageIcon = channel.getImage();
                        Image image = imageIcon.getImage();
                        Image newImage = image.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                        ImageIcon newImageIcon = new ImageIcon(newImage);
                        menuItem.setIcon(newImageIcon);
                        //set the action command as the channel name
                        menuItem.setActionCommand("is channel" + channel.getName());
                        menuItem.addActionListener(actionListener);
                        submenu.add(menuItem);
                    }
                    //publish the submenu to the process method
                    publish(submenu);
                });
                return null;
            }

            @Override
            protected void process(List<JMenu> chunks) {
                // update the GUI on the EDT
                for (JMenu submenu : chunks) {
                    channels.add(submenu);
                }
            }

        }.execute();
    }
    /**
     * Method for setting the action listener for the home menu item
     * @param actionListener the action listener
     */
    public void setHomeActionListener(ActionListener actionListener){
        home.addActionListener(actionListener);
    }

    /**
     * Method for setting the action listener for the help menu item
     * @param actionListener the action listener
     */
    public void setHelpActionListener(ActionListener actionListener){
        help.addActionListener(actionListener);
    }

    /**
     * Method for setting the action listener for the update menu item
     * @param actionListener the action listener
     */
    public void setUpdateActionListener(ActionListener actionListener){
        update.addActionListener(actionListener);
    }

    /**
     * Method for setting the action listener for the exit menu item
     * @param actionListener the action listener
     */
    public void setExitActionListener(ActionListener actionListener){
        exit.addActionListener(actionListener);
    }

    /**
     * Getter for the cardlayout so the controller can switch between the different views
     * @return the cardlayout
     */
    public CardLayout getCardLayout(){
        return cardLayout;
    }
    /**
     * Getter for the cardpanel so the controller can access the panels
     * @return the cardpandel
     */
    public JPanel getCardPanel(){
        return cardPanel;
    }

    /**
     * Method for controller to add a channel view to the cardpanel
     *
     * @param channelView the channel view to add
     * @param channelName the channel name for keeping track of the views
     */
    public void addChannelView(ChannelView channelView, String channelName){
        cardPanel.add(channelView, channelName);
        channelViews.put(channelName, channelView);
    }

    /**
     * Method for controller to get a channel view
     * @param channelName the name of the view
     * @return the channel view panel
     */
    public ChannelView getChannelView(String channelName){
        return channelViews.get(channelName);
    }
}
