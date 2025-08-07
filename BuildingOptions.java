package capstoneProject;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/* File Name: BuildingOptions
 * Purpose: Provides a GUI for selecting building-specific options like accessing Minges Science or George Hall schedules.
 * Date Completed: 4/16/2025
 */

public class BuildingOptions {
    // Constructor to create the Building Options window
    public BuildingOptions(JFrame parent) {
        // Close the parent frame if it exists
        if (parent != null) parent.dispose();

        // Create a new JFrame for Building Options
        JFrame frame = new JFrame("Building Options");
        frame.setSize(500, 350); // Set window size
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit app on close
        frame.setLocationRelativeTo(null); // Center the window
        frame.setLayout(new BorderLayout()); // Use border layout

        // === MAIN CONTENT PANEL ===
        JPanel mainPanel = new JPanel(new GridBagLayout()); // Use GridBag for flexible layout
        mainPanel.setBackground(Color.WHITE); // Set background color

        GridBagConstraints gbc = new GridBagConstraints(); // Constraints for layout
        gbc.insets = new Insets(15, 15, 15, 15); // Padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Components should stretch horizontally

        // === TITLE ===
        JLabel title = new JLabel("Building Options", SwingConstants.CENTER); // Create title label
        title.setFont(new Font("Arial", Font.BOLD, 22)); // Set font style and size
        title.setForeground(new Color(139, 0, 0)); // Set text color (LR Maroon)
        gbc.gridx = 0; // Column 0
        gbc.gridy = 0; // Row 0
        gbc.gridwidth = 2; // Span across 2 columns
        mainPanel.add(title, gbc); // Add title to panel

        // === Minges Button ===
        JButton mingesButton = createStyledButton("Minges Science", new Color(139, 0, 0)); // Create button with LR Maroon color
        mingesButton.addActionListener(e -> new mingesPage(frame)); // Navigate to Minges page on click
        gbc.gridx = 0; // Column 0
        gbc.gridy = 1; // Row 1
        gbc.gridwidth = 1; // Span 1 column
        mainPanel.add(mingesButton, gbc); // Add button to panel

        // === George Button ===
        JButton georgeButton = createStyledButton("George Hall", new Color(70, 0, 0)); // Create button with darker maroon
        georgeButton.addActionListener(e -> new georgePage(frame)); // Navigate to George page on click
        gbc.gridx = 1; // Column 1
        gbc.gridy = 1; // Row 1
        mainPanel.add(georgeButton, gbc); // Add button to panel

        // === Close Button ===
        JButton closeButton = createStyledButton("Close", Color.BLACK); // Create Close button
        closeButton.addActionListener(e -> {
            new MainPage(); // Open the main page
            frame.dispose(); // Close current window
        });
        gbc.gridx = 0; // Start at column 0
        gbc.gridy = 2; // Row 2
        gbc.gridwidth = 2; // Span 2 columns
        mainPanel.add(closeButton, gbc); // Add close button

        // Add main panel to frame and make it visible
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Helper method to create a styled JButton
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Font styling
        button.setForeground(Color.WHITE); // White text
        button.setBackground(bgColor); // Background color
        button.setFocusPainted(false); // Remove focus border
        button.setPreferredSize(new Dimension(180, 40)); // Set button size
        // Add black border with padding
        button.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 2, true), // Rounded black border
                new EmptyBorder(10, 10, 10, 10) // Internal padding
        ));
        return button;
    }

    // Main method to launch the Building Options window
    public static void main(String[] args) {
        new BuildingOptions(null);
    }
}