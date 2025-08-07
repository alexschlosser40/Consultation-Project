package capstoneProject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/* File Name: Settings
 * Purpose: GUI screen for user-defined settings including paths, filters, and system preferences.
 * Date Completed: 4/16/2025
 */


public class Settings {
	public Settings() {
		
		JFrame frame = new JFrame("Settings");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setLocationRelativeTo(null);
        // === NAVBAR ===
        frame.add(new NavBar(frame), BorderLayout.NORTH);
        
        //Integration Panel
        JPanel integrationPanel = new JPanel(new GridBagLayout());
        integrationPanel.setPreferredSize(new Dimension(230, 200)); // Increased panel size
        integrationPanel.setMinimumSize(new Dimension(230, 200));
        integrationPanel.setMaximumSize(new Dimension(230, 200));
        integrationPanel.setBackground(Color.LIGHT_GRAY);
        integrationPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2), // Outer black border
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(50, 0, 30, 0), // Extra space below title
                        "Integration Settings",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 18), // Large text
                        Color.BLACK
                )
        ));
        
        //Data Panel
        JPanel dataPanel = new JPanel(new GridBagLayout());
        dataPanel.setPreferredSize(new Dimension(230, 200)); // Increased panel size
        dataPanel.setMinimumSize(new Dimension(230, 200));
        dataPanel.setMaximumSize(new Dimension(230, 200));
        dataPanel.setBackground(Color.LIGHT_GRAY);
        dataPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2), // Outer black border
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(50, 0, 30, 0), // Extra space below title
                        "Data Settings",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 18), // Large text
                        Color.BLACK
                )
        ));
        
      //Advanced Panel
        JPanel advancedPanel = new JPanel(new GridBagLayout());
        advancedPanel.setPreferredSize(new Dimension(230, 200)); // Increased panel size
        advancedPanel.setMinimumSize(new Dimension(230, 200));
        advancedPanel.setMaximumSize(new Dimension(230, 200));
        advancedPanel.setBackground(Color.LIGHT_GRAY);
        advancedPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2), // Outer black border
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(50, 0, 30, 0), // Extra space below title
                        "Advanced Settings",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 18), // Large text
                        Color.BLACK
                )
        ));
        
        JPanel wrapperPanel1 = new JPanel(new GridBagLayout());
        GridBagConstraints gbcWrapper1 = new GridBagConstraints();
        gbcWrapper1.gridx = 0;
        gbcWrapper1.gridy = 1; // Move lower
        gbcWrapper1.anchor = GridBagConstraints.NORTH; // Align slightly downward
        gbcWrapper1.insets = new Insets(20, 15, 0, 0); // Adds space at the top
        wrapperPanel1.add(integrationPanel, gbcWrapper1);
        
        JPanel wrapperPanel2 = new JPanel(new GridBagLayout());
        GridBagConstraints gbcWrapper2 = new GridBagConstraints();
        gbcWrapper2.gridx = 0;
        gbcWrapper2.gridy = 1; // Move lower
        gbcWrapper2.anchor = GridBagConstraints.NORTH; // Align slightly downward
        gbcWrapper2.insets = new Insets(20, 0, 0, 0); // Adds space at the top
        wrapperPanel2.add(dataPanel, gbcWrapper2);
        
        JPanel wrapperPanel3 = new JPanel(new GridBagLayout());
        GridBagConstraints gbcWrapper3 = new GridBagConstraints();
        gbcWrapper3.gridx = 0;
        gbcWrapper3.gridy = 1; // Move lower
        gbcWrapper3.anchor = GridBagConstraints.NORTH; // Align slightly downward
        gbcWrapper3.insets = new Insets(20, 0, 0, 15); // Adds space at the top
        wrapperPanel3.add(advancedPanel, gbcWrapper3);
        
        //Add panels to frame
        frame.add(wrapperPanel1, BorderLayout.WEST);
        frame.add(wrapperPanel2, BorderLayout.CENTER);
        frame.add(wrapperPanel3, BorderLayout.EAST);
        
        //Create buttons
        frame.setVisible(true);
        
        //Create Buttons
        JButton dataConfigButton = new RoundedButton("Data Configuration");
        JButton filePathButton = new RoundedButton("Default Filepath");
        JButton defaultFiltersButton = new RoundedButton("Default Filters");
        JButton dataBackupButton = new RoundedButton("Data Backup Frequency");
        JButton errorLoggingButton = new RoundedButton("Error Logging");
        JButton systemResetButton = new RoundedButton("System Reset");
        
        // Stacking buttons
        GridBagConstraints gbcStack = new GridBagConstraints();
        gbcStack.gridx = 0;
        gbcStack.fill = GridBagConstraints.HORIZONTAL;
        gbcStack.insets = new Insets(5, 5, 5, 5); // Spacing between buttons

        //Buttons stacked based on position
        gbcStack.gridy = 0;
        integrationPanel.add(dataConfigButton, gbcStack);
        dataPanel.add(defaultFiltersButton, gbcStack);
        advancedPanel.add(errorLoggingButton, gbcStack);
        
        gbcStack.gridy = 1;
        integrationPanel.add(filePathButton, gbcStack);
        dataPanel.add(dataBackupButton, gbcStack);
        advancedPanel.add(systemResetButton, gbcStack);        
        
        systemResetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SystemReset.resetApplication(frame);
            }
        });
        
        filePathButton.addActionListener(e -> {
            frame.dispose();
            new FilePathPage();
        });
        
        dataConfigButton.addActionListener(e -> {
            frame.dispose();
            new DataConfiguration();
        });
        
        defaultFiltersButton.addActionListener(e -> {
            frame.dispose();
            new DefaultFilters();
        });
        
        errorLoggingButton.addActionListener(e -> {
            frame.dispose();
            new ErrorLogging();
        });
        
        dataBackupButton.addActionListener(e -> {
        	frame.dispose();
        	new DataBackupFrequency();
        });
	}
}