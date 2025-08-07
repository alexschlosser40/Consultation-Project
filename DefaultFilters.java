package capstoneProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.sql.*;

/* File Name: DefaultFilters
 * Purpose: Provides a GUI screen for users to define and save default filtering preferences for course searches.
 * Date Completed: 4/16/2025
 */

public class DefaultFilters {
	private final Properties filterConfig = new Properties();
    private final String CONFIG_FILE = "default_filters.properties";
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/exceldatabase";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "##Toadsworth0130!";
    
    private Connection getConnection() throws SQLException {
    	return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

	public DefaultFilters() {
		JFrame frame = new JFrame("Default Filters");
		frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setLocationRelativeTo(null);
        
        // === NavBar ===
        frame.add(new NavBar(frame), BorderLayout.NORTH);
        
        // === Main Form Panel ===
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        formPanel.setBackground(Color.LIGHT_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // === Input Fields ===
        JLabel termLabel = new JLabel("Default Term:");
        JComboBox<String> termField = new JComboBox<>(new String[]{"Fall", "Spring", "Summer"});
        JLabel sectionLabel = new JLabel("Default Section:");
        JComboBox<String> sectionField = new JComboBox<>();
        JLabel instructorLabel = new JLabel("Default Instructor:");
        JTextField instructorField = new JTextField();

        loadSections(sectionField);
        loadFilters(termField, sectionField, instructorField);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(termLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(termField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(sectionLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(sectionField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(instructorLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(instructorField, gbc);

        // === Buttons ===
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new RoundedButton("Save");
        JButton resetButton = new RoundedButton("Reset");
        JButton backButton = new RoundedButton("Back");

        buttonPanel.add(saveButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(backButton);

        // === Save Action ===
        saveButton.addActionListener((ActionEvent e) -> {
        	String instructor = instructorField.getText().trim();
        	if (!isValidInstructor(instructor)) {
        		JOptionPane.showMessageDialog(frame, "Instructor not found in database. Please try again");
        		return;
        	}
        	
            filterConfig.setProperty("term", (String) termField.getSelectedItem());
            filterConfig.setProperty("section", (String) sectionField.getSelectedItem());
            filterConfig.setProperty("instructor", instructor);
            
            try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
                filterConfig.store(out, "Default Filter Settings");
                JOptionPane.showMessageDialog(frame, "Filters saved.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error saving filters.");
                ErrorLogger.log(ex.getMessage());
            }
        });

        // === Reset Action ===
        resetButton.addActionListener(e -> {
            termField.setSelectedIndex(0);
            sectionField.setSelectedIndex(1);
            instructorField.setText("Staff");
        });

        // === Back Action ===
        backButton.addActionListener(e -> {
            frame.dispose();
            new Settings();
        });

        frame.add(formPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
	
	private void loadSections(JComboBox<String> sectionField) {
		try (Connection conn = getConnection()) { 
			 	Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT DISTINCT Section_Number FROM section ORDER BY Section_Number ASC");
				
				while (rs.next()) {
					sectionField.addItem(rs.getString("Section_Number"));
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error loading sections: " + e.getMessage());
				ErrorLogger.log(e.getMessage());
			}
		}
	
	private boolean isValidInstructor(String instructorLastName) {
		try (Connection conn = getConnection();
			 PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM staging_course_schedule WHERE LAST_NAME = ?")) {
			
			stmt.setString(1, instructorLastName);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return rs.getInt(1) > 0;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error validating instructor: " + e.getMessage());
			ErrorLogger.log(e.getMessage());
			return false;
		}
	}

    private void loadFilters(JComboBox<String> termField, JComboBox<String> sectionField, JTextField instructorField) {
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            filterConfig.load(in);
            termField.setSelectedItem(filterConfig.getProperty("term", "Spring"));
            sectionField.setSelectedItem(filterConfig.getProperty("section"));
            instructorField.setText(filterConfig.getProperty("instructor", ""));
        } catch (IOException e) {
            // File not found or unreadable — use defaults
        	ErrorLogger.log(e.getMessage());
        }

	}
}