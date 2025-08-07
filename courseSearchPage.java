package capstoneProject;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

/* File Name: courseSearchPage
 * Purpose: Allows users to search for courses based on filters, and displays the results in a structured table.
 * Date Completed: 4/16/2025
 */

public class courseSearchPage extends JFrame {
    private static final long serialVersionUID = 1L;

    // DB credentials
    // Database connection constants
    private static final String DB_URL = "jdbc:mysql://localhost:3306/exceldatabase";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "##Toadsworth0130!";

    // UI Components
    private JTextField txtCourseCode, txtCourseName, txtSection, txtProfFirstName, txtProfLastName, txtRoomNum;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    public courseSearchPage() {
        setTitle("Course Search");
        setSize(900, 600);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // === Title ===
        JLabel lblTitle = new JLabel("Course Search", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(139, 0, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        contentPanel.add(lblTitle, gbc);

        // === Labels & Inputs ===
        txtCourseCode = createInputField(contentPanel, "Course Code:", 1, 0, gbc);
        txtCourseName = createInputField(contentPanel, "Course Name:", 1, 2, gbc);
        txtSection = createInputField(contentPanel, "Section:", 2, 0, gbc);
        txtProfFirstName = createInputField(contentPanel, "Professor First Name:", 2, 2, gbc);
        txtProfLastName = createInputField(contentPanel, "Professor Last Name:", 3, 0, gbc);
        txtRoomNum = createInputField(contentPanel, "Room Number:", 3, 2, gbc);

        // === Buttons ===
        JButton searchButton = createStyledButton("Search", new Color(139, 0, 0));
        JButton closeButton = createStyledButton("Close", Color.BLACK);

        searchButton.addActionListener(this::performSearch);
        closeButton.addActionListener(e -> {
            new MainPage();
            dispose();
        });

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        contentPanel.add(searchButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 4;
        contentPanel.add(closeButton, gbc);

        // === Results Table ===
        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        resultTable.setFillsViewportHeight(true);
        resultTable.setFont(new Font("Arial", Font.PLAIN, 14));
        resultTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(139, 0, 0), 2, true), "Results", 0, 0, new Font("Arial", Font.BOLD, 14), new Color(139, 0, 0)));
        scrollPane.setPreferredSize(new Dimension(750, 200));

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        contentPanel.add(scrollPane, gbc);

        setContentPane(contentPanel);
        setVisible(true);
    }
    
    // Helper: creates a labeled input field and adds it to the panel using GridBagLayout
    /**
     * Creates labeled input field and adds it to panel using GridBagLayout
     * @param panel - declared to add all components to
     * @param labelText - label displayed to left of text field and 
     * set to font: "Arial", Bold, 14
     * @param row - adds label and text field on same row on top of page
     * @param col - splits labels and text fields into two columns on top
     * of page
     * @param gbc - labeled input fields are added to panel using GridBagLayout
     * @return
     */
    
    private JTextField createInputField(JPanel panel, String labelText, int row, int col, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        JTextField textField = new JTextField();
        gbc.gridx = col + 1;
        gbc.gridy = row;
        panel.add(textField, gbc);

        return textField;
    }

    // Helper: creates a custom-styled JButton
    /**
     * Creates a custom-styled button
     * @param text - button text is set to font: "Arial", Bold, 14
     * @param bgColor - button background is set to color
     * @return
     */
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 2, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return button;
    }
    
    // Executes a database query using the user's input and updates the result table
    /**
     * Executes a database query using the user's input and updates result table
     * @param e - action event to perform database queries when user clicks search button
     */
    
    private void performSearch(ActionEvent e) {
    	// Base query with joins across related tables
        String query = "SELECT DISTINCT c.Course_Number, c.Course_Name, s.Section_Number, " +
                       "p.First_Name, p.Last_Name, r.Room_Number " +
                       "FROM section s " +
                       "JOIN course c ON s.Course_ID = c.Course_ID " +
                       "JOIN professor p ON s.Professor_ID = p.Professor_ID " +
                       "JOIN room r ON s.Room_ID = r.Room_ID " +
                       "WHERE 1=1 ";

        // Appending filters based on input fields
        if (!txtCourseCode.getText().isEmpty()) query += " AND c.Course_Number LIKE '%" + txtCourseCode.getText() + "%'";
        if (!txtCourseName.getText().isEmpty()) query += " AND c.Course_Name LIKE '%" + txtCourseName.getText() + "%'";
        if (!txtSection.getText().isEmpty()) query += " AND s.Section_Number LIKE '%" + txtSection.getText() + "%'";
        if (!txtProfFirstName.getText().isEmpty()) query += " AND p.First_Name LIKE '%" + txtProfFirstName.getText() + "%'";
        if (!txtProfLastName.getText().isEmpty()) query += " AND p.Last_Name LIKE '%" + txtProfLastName.getText() + "%'";
        if (!txtRoomNum.getText().isEmpty()) query += " AND r.Room_Number LIKE '%" + txtRoomNum.getText() + "%'";
        
        // Execute query and populate table
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query); 

             // Set up column headers
            tableModel.setRowCount(0);
            tableModel.setColumnIdentifiers(new Object[]{"Course Code", "Course Name", "Section", "Professor First", "Professor Last", "Room Number"});

            // Fill table with results
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("Course_Number"),
                        rs.getString("Course_Name"),
                        rs.getString("Section_Number"),
                        rs.getString("First_Name"),
                        rs.getString("Last_Name"),
                        rs.getString("Room_Number")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ErrorLogger.log(ex.getMessage());
        }
    }

    // Launches the Course Search page
    public static void main(String[] args) {
        new courseSearchPage();
    }
}