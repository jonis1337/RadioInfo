package se.umu.cs.appjava.view;

import se.umu.cs.appjava.controller.PageController;
import se.umu.cs.appjava.model.ChannelInfo;
import se.umu.cs.appjava.model.Program;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.ZonedDateTime;
import java.util.ArrayList;
/**
 * Class that will display a detailed view of a channel as a JPanel.
 * The view will display the name, description and image of the channel.
 * It will also display the schedule for the channel.
 *
 * @author Jonatan Westling
 * @version 1.0
 * @date 2024-01-05
 */
public class ChannelView extends JPanel {
    private JTable table;
    ChannelInfo channelInfo;
    private DefaultTableModel model;
    private ArrayList<Program> schedule;
    private final PageController pageController;

    public ChannelView(ChannelInfo channelInfo, PageController pageController){
        this.channelInfo = channelInfo;
        this.pageController = pageController;
        setLayout(new BorderLayout());
        //create the top panel
        ImageIcon image = channelInfo.getImage();
        Image image1 = image.getImage();
        Image newImage = image1.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        ImageIcon newImageIcon = new ImageIcon(newImage);
        JLabel imageHolder = new JLabel(newImageIcon);

        JLabel channelName = new JLabel("    " + channelInfo.getChannelName());
        channelName.setFont(new Font("Microsoft JhengHei UI Light", Font.BOLD, 20));

        JTextArea descriptionText = new JTextArea(channelInfo.getTagline());
        descriptionText.setFont(new Font("Microsoft JhengHei UI Light", Font.PLAIN, 15));
        descriptionText.setEditable(false);
        descriptionText.setLineWrap(true);
        descriptionText.setWrapStyleWord(true);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(imageHolder, BorderLayout.WEST);
        infoPanel.add(channelName, BorderLayout.CENTER);
        infoPanel.add(descriptionText, BorderLayout.SOUTH);
        infoPanel.setPreferredSize(new Dimension(150, 150));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(infoPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = buildScheduleTable();
        add(scrollPane, BorderLayout.CENTER);


    }

    /**
     * Method that will build an empty schedule table at the initialization of the view. And also implement a mouse listener
     * that will listen for double clicks on the table and call the controller to let it know that a program was double clicked.
     * @return the JScrollPane that contains the table.
     */
    private JScrollPane buildScheduleTable(){
        //now we need to build the schedule
        //create
        String[] columnNames = {"Program", "Starttid", "Sluttid"};
        //init empty table
        model = new DefaultTableModel(columnNames, 0){
            //we need to override this method to make the table not editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        //create the table to show the schedule
        table = new JTable(model);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(25);
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    e.consume();
                    // Get the row index that was double-clicked
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        pageController.programDoubleClicked(schedule.get(row));
                    }
                }
            }
        });
        return new JScrollPane(table);
    }
    /**
     * This method updates the schedule table when the worker has parsed the schedule and notified the controller.
     * It will also scroll to the current running program if there is one and select it so the user easily can see
     * which program is currently running.
     */
    public void updateSchedule(){
        schedule = channelInfo.getSchedule();
        ZonedDateTime now = ZonedDateTime.now();
        int rowCurrentRunningProgram = -1;
        //clear the table
        model.setRowCount(0);
        //add all the programs to the table
        for (int i = 0; i < schedule.size(); i++){
            Program program = schedule.get(i);
            //create the row and add it
            Object[] object = new Object[]{program.getProgramName(), program.getStartTime(), program.getEndTime()};
            model.addRow(object);
            //check if the program is running now
            if (!now.isBefore(program.getZonedLocalDateStartTime()) && !now.isAfter(program.getZonedLocalDateEndTIme())){
                //collect the row index of the current running program
                rowCurrentRunningProgram = i;
            }
        }
        if (rowCurrentRunningProgram != -1){
            //if there is a current running program, select it and scroll to it
            table.setRowSelectionInterval(rowCurrentRunningProgram, rowCurrentRunningProgram);
            // +7 to put the current running program in the middle of the screen for better visibility
            table.scrollRectToVisible(table.getCellRect(rowCurrentRunningProgram + 7, 0, true));
        }

    }
}
