package capstoneProject;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import javax.swing.border.EtchedBorder;

/* File Name: FileUpload
 * Purpose: Handles uploading CSV or other supported files into the system, typically importing course data.
 * Date Completed: 4/16/2025
 */

public class FileUpload extends JFrame {
    private static final String MYSQL_UPLOADS_FOLDER = "C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/";
    
    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/exceldatabase";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "##Toadsworth0130!";

    public FileUpload() {
        getContentPane().setForeground(new Color(128, 0, 64));
        getContentPane().setBackground(SystemColor.activeCaption);
        setTitle("CSV Uploader");
        setSize(210, 198);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        // === Upload Panel ===
        JPanel panel = new JPanel();
        panel.setBorder(new EtchedBorder(EtchedBorder.RAISED, new Color(128, 0, 0), null));
        panel.setBackground(new Color(255, 255, 255));
        panel.setLayout(null);

        JLabel lblInstruction = new JLabel("Select a CSV file to upload:");
        lblInstruction.setBounds(10, 11, 198, 44);
        lblInstruction.setFont(new Font("Tahoma", Font.PLAIN, 15));
        panel.add(lblInstruction);

        JButton btnBrowse = new JButton("Browse");
        btnBrowse.setBounds(10, 49, 171, 44);
        btnBrowse.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btnBrowse.setBackground(new Color(255, 0, 0));
        btnBrowse.setForeground(new Color(255, 255, 255));
        btnBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectAndUploadFile();
            }
        });
        panel.add(btnBrowse);

        getContentPane().add(panel, BorderLayout.CENTER);
        
                JButton backButton = new JButton("Back to Main Page");
                backButton.setBounds(10, 104, 171, 44);
                panel.add(backButton);
                backButton.setFont(new Font("Arial", Font.PLAIN, 15));
                backButton.setBackground(new Color(139, 0, 0));
                backButton.setForeground(Color.WHITE);
                backButton.setFocusPainted(false);
                backButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new MainPage();  // Go back to Main Page
                        dispose();       // Close current window
                    }
                });

        setVisible(true);
    }

    private void selectAndUploadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose CSV File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            File destination = new File(MYSQL_UPLOADS_FOLDER + selectedFile.getName());

            try {
                Files.copy(selectedFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(this, "File uploaded successfully!");
                importCSVToDatabase(destination.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error uploading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void importCSVToDatabase(String fileName) {
        String filePath = MYSQL_UPLOADS_FOLDER.replace("\\", "/") + fileName;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
        	
        	try (CallableStatement resetStmt = conn.prepareCall("{CALL ResetDatabase()}")) {
        		resetStmt.execute();
        	}

            // Load CSV data into staging table
            String loadQuery = "LOAD DATA INFILE '" + filePath + "' " +
                               "INTO TABLE staging_course_schedule " +
                               "FIELDS TERMINATED BY ',' ENCLOSED BY '\"' " +
                               "LINES TERMINATED BY '\\r\\n' IGNORE 1 ROWS " +
                               "(EVENT_ID, SECTION, EVENT_TYPE, EVENT_LONG_NAME, ADDS, MAX_PARTICIPANT, DAY, START_TIME, END_TIME, " +
                               " FIRST_NAME, LAST_NAME, POSITION, CREDITS, BUILDING_CODE, ROOM_ID, COLLEGE, DEPARTMENT, START_DATE, END_DATE, @ignore)";

            stmt.executeUpdate(loadQuery);

            // Process the data
            try (CallableStatement callStmt = conn.prepareCall("{CALL ImportCSV()}")) {
                callStmt.execute();
            }

            JOptionPane.showMessageDialog(this, "Database updated successfully!");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new FileUpload();
    }
}