package capstoneProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/* File Name: NavBar
 * Purpose: Implements a reusable navigation bar for switching between different GUI pages in the application.
 * Date Completed: 4/16/2025
 */


public class NavBar extends JPanel {
    public NavBar(JFrame frame) {
        // Set layout and appearance
        setPreferredSize(new Dimension(1000, 70));
        setLayout(new FlowLayout(FlowLayout.CENTER, 40, 15)); // Even spacing
        setBackground(new Color(139, 0, 0)); // Lenoir-Rhyne maroon
        setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.BLACK)); // Bottom border

        // Create nav buttons
        JButton homeBtn = createNavButton("Home", frame, MainPage::new);
        JButton settingsBtn = createNavButton("Settings", frame, Settings::new);
        JButton supportBtn = createNavButton("Support", frame, SupportPage::new);

        // Add to layout
        add(homeBtn);
        add(settingsBtn);
        add(supportBtn);
    }

    private JButton createNavButton(String text, JFrame currentFrame, Runnable redirect) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(139, 0, 0));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false); // Flat look
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding only

        // Navigate to page
        button.addActionListener((ActionEvent e) -> {
            currentFrame.dispose();
            redirect.run();
        });

        return button;
    }
}