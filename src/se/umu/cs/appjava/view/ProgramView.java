package se.umu.cs.appjava.view;

import se.umu.cs.appjava.model.Program;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * Class that will display a detailed view of a program as a JDialog.
 * The view will display the name, start and end time, description and image of the program.
 */

public class ProgramView extends JDialog {

    private Program program;
    private JFrame parentFrame;
    /**
     * Constructor for the ProgramView
     * @param parentFrame the parent frame
     * @param program the program to display
     */
    public ProgramView(JFrame parentFrame, Program program){
        super((parentFrame), "Program", true);
        this.parentFrame = parentFrame;
        this.program = program;
        buildProgramView();
        displayProgramView();

    }
    /**
     * Method that will build the view
     */
    private void buildProgramView(){
        setLayout(new BorderLayout());
        setSize(600, 300);

        JLabel nameLabel = new JLabel("    "+program.getProgramName());
        nameLabel.setFont(new Font("Microsoft JhengHei UI Light", Font.BOLD, 20));
        JLabel startTimeLabel = new JLabel("Start: " + program.getStartTime());
        startTimeLabel.setFont(new Font("Microsoft JhengHei UI Light", Font.BOLD, 12));
        JLabel endTimeLabel = new JLabel("Slut: " + program.getEndTime());
        endTimeLabel.setFont(new Font("Microsoft JhengHei UI Light", Font.BOLD, 12));
        JTextArea descriptionArea = new JTextArea(program.getDescription());
        //format the image
        String  image = program.getImage();
        URL url = null;
        try {
            url = new URL(image);
        } catch (MalformedURLException e) {
            //handle exception better...
            System.out.println("Error: creating image url for channel");
        }
        ImageIcon imageicon;
        if (url != null){
            imageicon = new ImageIcon(url);
        } else {
            imageicon = new ImageIcon();
        }
        Image image1 = imageicon.getImage();
        Image scaledImage = image1.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon newImageIcon = new ImageIcon(scaledImage);
        JLabel imageHolder = new JLabel(newImageIcon);

        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imageHolder, BorderLayout.WEST);
        topPanel.add(nameLabel, BorderLayout.CENTER);

        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.setBorder(BorderFactory.createEmptyBorder(5, 150, 10, 150));
        timePanel.add(startTimeLabel, BorderLayout.WEST);
        timePanel.add(endTimeLabel, BorderLayout.EAST);

        topPanel.add(timePanel, BorderLayout.SOUTH);
        // Add components to the frame
        add(topPanel, BorderLayout.NORTH);

        add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
    }

    private void displayProgramView(){
        setLocationRelativeTo(parentFrame);
        setResizable(false);
        setVisible(true);
    }


}


