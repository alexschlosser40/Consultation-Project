package capstoneProject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/* File Name: DeveloperTools
 * Purpose: Displays developer-related tools or debug options for internal application testing or configuration.
 * Date Completed: 4/16/2025
 */


public class DeveloperTools {
	public DeveloperTools() {
        JFrame frame = new JFrame("Developer Tools");
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);

        frame.setLocationRelativeTo(null);

        // === ADD NAVIGATION BAR ===
        frame.add(new NavBar(frame), BorderLayout.NORTH);

        // === SUPPORT PANEL (Rounded Box) ===
        JPanel devToolsPanel = new JPanel(new GridBagLayout());
        devToolsPanel.setPreferredSize(new Dimension(450, 350));
        devToolsPanel.setBackground(Color.LIGHT_GRAY);
        devToolsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(50, 0, 30, 0),
                        "Developer Tools",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 18),
                        Color.BLACK
                )
        ));

        // === CREATE TABLE ===
        String[] columnNames = {"Tool", "Purpose", "Version", "Setup Details"};
        Object[][] data = {
                {"MySQL Workbench", "Database", "8.0.38", "Download from official site"},
                {"Eclipse", "Java IDE", "2023-06", "Requires JDK 11+"},
                {"IntelliJ IDEA", "Java IDE", "2023.1", "Popular among professionals"},
                {"OpenPDF", "PDF Utilization", "1.3.30", "Add external JAR file to project"}
        };

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table); // Add table to scroll pane

        // === ADD TABLE TO PANEL ===
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        devToolsPanel.add(scrollPane, gbc);
        
        // === ADD CLOSE BUTTON TO RETURN TO SUPPORT PAGE ===
        JButton closeButton = new CloseButton(frame, () -> new SupportPage());

        GridBagConstraints gbcClose = new GridBagConstraints();
        gbcClose.gridx = 0;
        gbcClose.gridy = 1; // Place below the text
        gbcClose.weighty = 1; // Push to bottom
        gbcClose.anchor = GridBagConstraints.SOUTH;
        gbcClose.insets = new Insets(20, 0, 10, 0);

        devToolsPanel.add(closeButton, gbcClose);


        // === ADD PANEL TO FRAME ===
        frame.add(devToolsPanel, BorderLayout.CENTER);

        frame.setVisible(true); // Ensure the window is displayed
    }
}