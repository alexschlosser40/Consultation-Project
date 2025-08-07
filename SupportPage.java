package capstoneProject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/* File Name: SupportPage
 * Purpose: Displays help or support information, such as documentation links or contact details.
 * Date Completed: 4/16/2025
 */



public class SupportPage {
	public SupportPage() {
        JFrame frame = new JFrame("Support");
        frame.setSize(700, 600); // Increase window size if needed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);

        // === ADD NAVIGATION BAR ===
        frame.add(new NavBar(frame), BorderLayout.NORTH);

        // === SUPPORT PANEL (Rounded Box) ===
        JPanel supportPanel = new JPanel(new GridBagLayout()); // Center content inside
        supportPanel.setPreferredSize(new Dimension(450, 350)); // Increased panel size
        supportPanel.setMinimumSize(new Dimension(450, 350));
        supportPanel.setMaximumSize(new Dimension(450, 350));
        supportPanel.setBackground(Color.LIGHT_GRAY);
        supportPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2), // Outer black border
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(50, 0, 30, 0), // Extra space below title
                        "Help and Support",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 18), // Large text
                        Color.BLACK
                )
        ));

        // === Create a Panel for Buttons (Ensures Equal Size) ===
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 15, 15)); // 3 rows, 1 column, even spacing
        buttonPanel.setOpaque(false); // Transparent background

        // === Create Rounded Buttons ===
        JButton devToolsButton = new RoundedButton("Developer Tools");
        JButton manualButton = new RoundedButton("User Manual");
        JButton aboutPageButton = new RoundedButton("About Page");

        // === Set Equal Button Sizes ===
        Dimension buttonSize = new Dimension(220, 55); // Slightly bigger buttons
        devToolsButton.setPreferredSize(buttonSize);
        manualButton.setPreferredSize(buttonSize);
        aboutPageButton.setPreferredSize(buttonSize);

        // === Center Buttons Horizontally ===
        devToolsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        manualButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        aboutPageButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // === Add Buttons to Panel ===
        buttonPanel.add(devToolsButton);
        buttonPanel.add(manualButton);
        buttonPanel.add(aboutPageButton);

        // === Center Button Panel Inside Support Panel ===
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // Center the button panel inside support panel
        supportPanel.add(buttonPanel, gbc);

        // === Center Support Panel in the Frame (Move it Lower) ===
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcWrapper = new GridBagConstraints();
        gbcWrapper.gridx = 0;
        gbcWrapper.gridy = 1; // Move lower
        gbcWrapper.anchor = GridBagConstraints.NORTH; // Align slightly downward
        gbcWrapper.insets = new Insets(20, 0, 0, 0); // Adds space at the top
        wrapperPanel.add(supportPanel, gbcWrapper);

        frame.add(wrapperPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        
        // === BUTTON EVENT LISTENER (No change to original logic) ===
        devToolsButton.addActionListener(e -> {
            frame.dispose();
            new DeveloperTools();
        });
        
        manualButton.addActionListener(e -> {
        	frame.dispose();
        	new UserManual();
        });

        aboutPageButton.addActionListener(e -> {
            frame.dispose();
            new AboutPage();
        });
    }
}