package capstoneProject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

/* File Name: UserManual
 * Purpose: Displays a built-in user manual or guide for navigating and using the application.
 * Date Completed: 4/16/2025
 */


public class UserManual {
    public UserManual() {
        JFrame frame = new JFrame("User Manual");
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);
        
        frame.setLocationRelativeTo(null);

        // === ADD NAVIGATION BAR ===
        frame.add(new NavBar(frame), BorderLayout.NORTH);

     // === Container Panel ===
        JPanel manualPanel = new JPanel(new GridBagLayout());
        frame.add(manualPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // === 1. TOP GLUE ===
        gbc.gridy = 0;
        gbc.weighty = 1;  // flexible space
        manualPanel.add(Box.createVerticalGlue(), gbc);

        // === 2. TITLE ===
        gbc.gridy = 1;
        gbc.weighty = 0;
        JLabel title = new JLabel("User Manual");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        manualPanel.add(title, gbc);

        // === 3. MANUAL TEXT ===
        gbc.gridy = 2;
        JLabel manualText = new JLabel("<html><div style='text-align: center'>"
                + "This user manual provides instructions on how to use the application.<br>"
                + "Please click the link below to view the full manual."
                + "</div></html>");
        manualText.setFont(new Font("Arial", Font.PLAIN, 14));
        manualPanel.add(manualText, gbc);

        // === 4. LINK LABEL ===
        gbc.gridy = 3;
        JLabel linkLabel = new JLabel("<html><u>User Manual.docx</u></html>");
        linkLabel.setForeground(Color.BLUE);
        linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        manualPanel.add(linkLabel, gbc);

        // === 5. BOTTOM GLUE ===
        gbc.gridy = 4;
        gbc.weighty = 1; // flexible space below the content
        manualPanel.add(Box.createVerticalGlue(), gbc);

        // === 6. CLOSE BUTTON ===
        gbc.gridy = 5;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.PAGE_END;
        RoundedButton closeButton = new RoundedButton("Close");
        closeButton.setPreferredSize(new Dimension(120, 40));
        manualPanel.add(closeButton, gbc);

        // === LINK ACTION ===
        linkLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                    File manual = new File("documents/User Manual.docx");
                    if (manual.exists()) {
                        Desktop.getDesktop().open(manual);
                    } else {
                        JOptionPane.showMessageDialog(frame, "User Manual not found");
                    }

                } catch (IOException ex) {
                    ErrorLogger.log("Error opening User Manual: " + ex.getMessage());
                    JOptionPane.showMessageDialog(frame, "Error opening the document.");
                }
            }
        });

        // === CLOSE BUTTON ACTION ===
        closeButton.addActionListener(e -> {
            frame.dispose();
            new SupportPage();
        });

        frame.setVisible(true);
    }
}