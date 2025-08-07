package capstoneProject;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/* File Name: AboutPage
 * Purpose: Displays information about the project, such as version and authorship, typically accessed from the main menu.
 * Date Completed: 4/16/2025
 */

public class AboutPage {
    // Define theme colors (example Lenoir-Rhyne inspired palette)
    private static final Color MAROON = new Color(128, 0, 0);
    private static final Color GOLD = new Color(204, 153, 0);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color BACKGROUND = Color.WHITE;
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font BODY_FONT = new Font("Arial", Font.PLAIN, 16);

    //About page constructor
    public AboutPage() {
        JFrame frame = new JFrame("About Page");
        frame.setSize(1016, 797);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BACKGROUND);
        frame.setLocationRelativeTo(null);

        // === NAVIGATION BAR (using a custom themed navbar) ===
        frame.getContentPane().add(new NavBar(frame), BorderLayout.NORTH);

        // === ABOUT PAGE TITLE PANEL ===
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(BACKGROUND);
        JLabel titleLabel = new JLabel("About ExcelSoftware");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(MAROON);
        titlePanel.add(titleLabel);

        // === ABOUT PAGE CONTENT PANEL (inside a styled wrapper) ===
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND);
        contentPanel.setBorder(new CompoundBorder(
                new LineBorder(MAROON, 2),
                new EmptyBorder(30, 30, 30, 30)
        ));

        // === ABOUT TEXT CONTENT ===
        String aboutPageInfo = "<html><body style='width: 600px; font-family:Arial; font-size:16px; color:#333333;'>"
                + "<h2 style='color: rgb(" + MAROON.getRed() + "," + MAROON.getGreen() + "," + MAROON.getBlue() + ");'>Welcome to ExcelSoftware</h2>"
                + "<p><strong>ExcelSoftware</strong> is a cutting-edge solution developed for the College of Natural Sciences and Mathematics at Lenoir-Rhyne University. "
                + "Our system streamlines course management by replacing cumbersome Excel spreadsheets with an intuitive digital platform that is both efficient and easy-to-use.</p>"
                + "<p>The application is designed to:</p>"
                + "<ul>"
                + "<li>Seamlessly import and manage course data</li>"
                + "<li>Allow powerful searching and filtering by instructor, term, and department</li>"
                + "<li>Generate comprehensive scheduling reports automatically</li>"
                + "<li>Offer robust system backup and reset functionalities</li>"
                + "<li>Provide integrated developer tools for ongoing system enhancements</li>"
                + "</ul>"
                + "<p>Our goal is to save time, reduce errors, and empower academic administrators by offering a reliable, modern approach to course scheduling.</p>"
                + "</body></html>";

        // === CREATE A TEXT PANE FOR THE HTML CONTENT ===
        JTextPane aboutTextPane = new JTextPane();
        aboutTextPane.setContentType("text/html");
        aboutTextPane.setText(aboutPageInfo);
        aboutTextPane.setEditable(false);
        aboutTextPane.setOpaque(false);
        aboutTextPane.setFont(BODY_FONT);
        aboutTextPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        // === WRAP THE TEXT PANE IN A SCROLL PANE ===
        JScrollPane scrollPane = new JScrollPane(aboutTextPane);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // === CLOSE BUTTON AT THE BOTTOM ===
        JButton closeButton = new CloseButton(frame, () -> new SupportPage());
        closeButton.setFont(BODY_FONT);
        closeButton.setBackground(MAROON);
        closeButton.setForeground(BACKGROUND);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(new LineBorder(GOLD, 2));
        // Add padding around the button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND);
        buttonPanel.add(closeButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // === WRAPPER PANEL TO CENTER THE CONTENT PANEL ===
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        wrapperPanel.add(contentPanel, gbc);

        // === ADD COMPONENTS TO THE FRAME ===
        // Placing the title panel at the top, then the content below
        frame.getContentPane().add(titlePanel, BorderLayout.SOUTH);
        frame.getContentPane().add(wrapperPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}