package capstoneProject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/* File Name: mingesPage
 * Purpose: Displays the interface for Minges Science Building, including schedule or room-specific options.
 * Date Completed: 4/16/2025
 */


public class mingesPage {
    private JFrame frame2;

    public mingesPage(JFrame parent) {
        if (parent != null) parent.dispose();

        Color lrRed = new Color(132, 0, 20);
        Color lrGold = new Color(34, 37, 212);
        Color darkGray = new Color(60, 60, 60);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);

        frame2 = new JFrame("Minges Science - Room List");
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setSize(600, 400);
        frame2.setLocationRelativeTo(null);
        frame2.setLayout(new BorderLayout(10, 10));
        frame2.getContentPane().setBackground(Color.WHITE);
        frame2.getRootPane().setBorder(new LineBorder(lrRed, 3));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblTitle = new JLabel("Minges Science");
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(lrRed);

        JLabel lblSubtitle = new JLabel("Select a Room from the List");
        lblSubtitle.setFont(labelFont);
        lblSubtitle.setForeground(darkGray);

        String[] rooms = {"104", "105", "107", "109", "111", "118", "214", "222", "300", "301", "302", "316"};
        JComboBox<String> roomDropdown = new JComboBox<>(rooms);
        roomDropdown.setPreferredSize(new Dimension(200, 25));

        JButton openRoomButton = new JButton("Open Room Info");
        openRoomButton.setBackground(lrGold);
        openRoomButton.setForeground(Color.WHITE);
        openRoomButton.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel selectedLabel = new JLabel("Selected: Room " + rooms[0]);
        selectedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        selectedLabel.setForeground(Color.DARK_GRAY);

        roomDropdown.addActionListener(e -> {
            String selected = (String) roomDropdown.getSelectedItem();
            selectedLabel.setText("Selected: Room " + selected);
        });

        openRoomButton.addActionListener(e -> {
            String selected = (String) roomDropdown.getSelectedItem();
            new RoomInfoScreen("SCI", selected, frame2);
        });

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(lblTitle, gbc);
        gbc.gridy++;
        mainPanel.add(lblSubtitle, gbc);
        gbc.gridy++;
        mainPanel.add(roomDropdown, gbc);
        gbc.gridy++;
        mainPanel.add(selectedLabel, gbc);
        gbc.gridy++;
        mainPanel.add(openRoomButton, gbc);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);
        JButton backButton = new JButton("Back");
        JButton closeButton = new JButton("Close");

        backButton.setBackground(lrRed);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 12));

        closeButton.setBackground(Color.DARK_GRAY);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 12));

        backButton.addActionListener(e -> new BuildingOptions(frame2));
        closeButton.addActionListener(e -> {
        	frame2.dispose();
        	new MainPage();
        });

        bottomPanel.add(backButton);
        bottomPanel.add(closeButton);

        frame2.add(mainPanel, BorderLayout.CENTER);
        frame2.add(bottomPanel, BorderLayout.SOUTH);

        frame2.setVisible(true);
    }
}