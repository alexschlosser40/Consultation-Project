package capstoneProject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;
import java.sql.*;
import java.time.LocalDate;

/* File Name: UpdatedCourseSchedule
 * Purpose: Main view and control panel for editing and managing course schedules with table interactions.
 * Date Completed: 4/16/2025
 */


public class UpdatedCourseSchedule extends JFrame {
    private static final long serialVersionUID = 1L;
    private JComboBox<String> categoryDropdown;
    private JComboBox<String> roomDropdown;
    private static final int MAX_ROWS = 10;
    private Properties defaultFilters = new Properties();

    private String selectedTerm, selectedBuilding, selectedRoom;
    private HashMap<String, String[]> dataMap = new HashMap<>();
    private DefaultTableModel tableModel;

    public UpdatedCourseSchedule() {
        setTitle("Course Schedule Creation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 980);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        
        try (FileInputStream in = new FileInputStream("default_filters.properties")) {
        	defaultFilters.load(in);
        } catch (IOException e) {
        	ErrorLogger.log(e.getMessage());
        }

        Color lrRed = new Color(132, 0, 20);
        Color lrGold = new Color(255, 204, 0);
        Color darkGrey = new Color(50, 50, 50);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 26);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

        JPanel container = new JPanel();
        container.setLayout(null);
        container.setBackground(Color.WHITE);
        container.setBounds(40, 20, 750,  880);
        container.setBorder(new LineBorder(lrRed, 2, true));
        getContentPane().add(container);

        JLabel lblTitle = new JLabel("Course Schedule Creation", SwingConstants.CENTER);
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(lrRed);
        lblTitle.setBounds(175, 20, 400, 40);
        container.add(lblTitle);

        int xLabel = 100, xField = 280, y = 80, spacingY = 40;

        JLabel lblTerm = new JLabel("* Term:");
        lblTerm.setFont(labelFont);
        lblTerm.setBounds(xLabel, y, 120, 25);
        container.add(lblTerm);

        // Split term into dropdown and text input
        JComboBox<String> cmbTermSeason = new JComboBox<>(new String[]{"Spring", "Summer", "Fall"});
        cmbTermSeason.setBounds(xField, y, 100, 25);
        container.add(cmbTermSeason);

        JTextField txtYear = new JTextField();
        txtYear.setBounds(xField + 110, y, 90, 25);
        container.add(txtYear);

        y += spacingY;

        JLabel lblBuilding = new JLabel("* Building:");
        lblBuilding.setFont(labelFont);
        lblBuilding.setBounds(xLabel, y, 120, 25);
        container.add(lblBuilding);

        categoryDropdown = new JComboBox<>();
        categoryDropdown.setBounds(xField, y, 200, 25);
        container.add(categoryDropdown);
        y += spacingY;

        JLabel lblRoom = new JLabel("* Room Number:");
        lblRoom.setFont(labelFont);
        lblRoom.setBounds(xLabel, y, 120, 25);
        container.add(lblRoom);

        roomDropdown = new JComboBox<>();
        roomDropdown.setBounds(xField, y, 200, 25);
        container.add(roomDropdown);
        y += spacingY;

        JButton btnSetProperties = new JButton("Set Properties");
        int btnSetPropsWidth = 240;
        btnSetProperties.setBounds((750 - btnSetPropsWidth) / 2, y, btnSetPropsWidth, 35);
        btnSetProperties.setBackground(lrRed);
        btnSetProperties.setForeground(Color.WHITE);
        btnSetProperties.setFont(buttonFont);
        btnSetProperties.setFocusPainted(false);
        btnSetProperties.setBorder(new LineBorder(Color.BLACK, 1));
        container.add(btnSetProperties);
        y += spacingY + 10;

        String[][] fields = {
            {"* Course Code:", "txtCourseCode"},
            {"* Course Section:", "cmbSection"},
            {"* Course Name:", "txtCourseName"},
            {"* Professor Last Name:", "txtProfessor"}
        };

        JTextField txtCourseCode = new JTextField();
        JComboBox<String> cmbSection = new JComboBox<>();
        loadSectionsFromDatabase(cmbSection);
        
        JTextField txtCourseName = new JTextField();
        JTextField txtProfessor = new JTextField();

        for (String[] field : fields) {
            JLabel lbl = new JLabel(field[0]);
            lbl.setFont(labelFont);
            lbl.setBounds(xLabel, y, 160, 25);
            container.add(lbl);

            JComponent input = switch (field[1]) {
                case "txtCourseCode" -> txtCourseCode;
                case "cmbSection" -> cmbSection;
                case "txtCourseName" -> txtCourseName;
                case "txtProfessor" -> txtProfessor;
                default -> new JTextField();
            };
            input.setBounds(xField, y, 200, 25);
            container.add(input);
            y += spacingY;
        }
        
        String defaultTerm = defaultFilters.getProperty("term");
        if (defaultTerm != null && defaultTerm.matches("(Spring|Summer|Fall)")) {
        	String[] parts = defaultTerm.split(" ");
        	cmbTermSeason.setSelectedItem(parts[0]);
        	txtYear.setText(String.valueOf(LocalDate.now().getYear()));
        }
        
        String defaultSection = defaultFilters.getProperty("section");
        if (defaultSection != null) {
            cmbSection.setSelectedItem(defaultSection.length() == 1 ? "0" + defaultSection : defaultSection);
        }
        txtProfessor.setText(defaultFilters.getProperty("instructor"));

        JLabel lblDays = new JLabel("* Instruction Day(s):");
        lblDays.setFont(labelFont);
        lblDays.setBounds(xLabel, y, 160, 25);
        container.add(lblDays);

        JCheckBox cbMonday = new JCheckBox("M");
        JCheckBox cbTuesday = new JCheckBox("T");
        JCheckBox cbWednesday = new JCheckBox("W");
        JCheckBox cbThursday = new JCheckBox("R");
        JCheckBox cbFriday = new JCheckBox("F");
        JCheckBox[] days = {cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday};

        int dayX = xField;
        for (JCheckBox cb : days) {
            cb.setBounds(dayX, y, 40, 25);
            cb.setBackground(Color.WHITE);
            container.add(cb);
            dayX += 45;
        }
        y += spacingY;

        JLabel lblTime = new JLabel("* Instruction Time(s):");
        lblTime.setFont(labelFont);
        lblTime.setBounds(xLabel, y, 160, 25);
        container.add(lblTime);

        JTextField txtStartTime = new JTextField();
        txtStartTime.setBounds(xField, y, 50, 25);
        container.add(txtStartTime);

        JComboBox<String> cmbStartAMPM = new JComboBox<>(new String[]{"AM", "PM"});
        cmbStartAMPM.setBounds(xField + 60, y, 60, 25);
        container.add(cmbStartAMPM);

        JLabel lblTo = new JLabel("-");
        lblTo.setBounds(xField + 130, y, 10, 25);
        container.add(lblTo);

        JTextField txtEndTime = new JTextField();
        txtEndTime.setBounds(xField + 150, y, 50, 25);
        container.add(txtEndTime);

        JComboBox<String> cmbEndAMPM = new JComboBox<>(new String[]{"AM", "PM"});
        cmbEndAMPM.setBounds(xField + 210, y, 60, 25);
        container.add(cmbEndAMPM);
        y += spacingY + 10;

        int tripleButtonWidth = 160;
        int tripleSpacing = 30;
        int tripleTotalWidth = (3 * tripleButtonWidth) + (2 * tripleSpacing);
        int tripleStartX = (750 - tripleTotalWidth) / 2;

        JButton btnAddRow = new JButton("Add Row");
        btnAddRow.setBounds(tripleStartX, y, tripleButtonWidth, 40);
        btnAddRow.setBackground(new Color(0, 128, 255));
        btnAddRow.setForeground(Color.BLACK);
        btnAddRow.setFont(buttonFont);
        btnAddRow.setFocusPainted(false);
        btnAddRow.setBorder(new LineBorder(Color.BLACK, 1));
        container.add(btnAddRow);

        JButton btnResetData = new JButton("Reset Data Entry");
        btnResetData.setBounds(tripleStartX + tripleButtonWidth + tripleSpacing, y, tripleButtonWidth, 40);
        btnResetData.setBackground(Color.LIGHT_GRAY);
        btnResetData.setFont(buttonFont);
        btnResetData.setFocusPainted(false);
        container.add(btnResetData);

        JButton btnCreateSchedule = new JButton("Create Schedule");
        btnCreateSchedule.setBounds(tripleStartX + 2 * (tripleButtonWidth + tripleSpacing), y, tripleButtonWidth, 40);
        btnCreateSchedule.setBackground(lrRed);
        btnCreateSchedule.setForeground(Color.WHITE);
        btnCreateSchedule.setFont(buttonFont);
        btnCreateSchedule.setFocusPainted(false);
        container.add(btnCreateSchedule);

        y += spacingY + 40;

        int btnWidth = 180;
        int spacing = 40;
        int totalWidth = (btnWidth * 2) + spacing;
        int startX = (750 - totalWidth) / 2;

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(startX, y, btnWidth, 40);
        btnCancel.setBackground(new Color(153, 0, 0));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(buttonFont);
        btnCancel.setFocusPainted(false);
        container.add(btnCancel);

        JButton btnResetSchedule = new JButton("Reset Schedule");
        btnResetSchedule.setBounds(startX + btnWidth + spacing, y, btnWidth, 40);
        btnResetSchedule.setBackground(new Color(90, 90, 90));
        btnResetSchedule.setForeground(Color.WHITE);
        btnResetSchedule.setFont(buttonFont);
        btnResetSchedule.setFocusPainted(false);
        container.add(btnResetSchedule);

        y += spacingY + 50;

        String[] columnNames = {"Course", "§", "Course Name", "Day", "Time", "Professor"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(darkGrey);
        header.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(60, y, 620, 160);
        container.add(scrollPane);

        dataMap.put("Minges Science", new String[]{"104", "105", "107", "109", "111", "118", "214", "222", "300", "301", "302", "316"});
        dataMap.put("George Hall", new String[]{"122", "126", "128", "129", "132", "137", "139", "315", "319", "324", "325", "330"});
        dataMap.put("Library", new String[]{"100", "101", "201", "210"});
        dataMap.put("Admin Building", new String[]{"100", "101", "200", "201"});

        categoryDropdown.setModel(new DefaultComboBoxModel<>(dataMap.keySet().toArray(new String[0])));
        roomDropdown.setModel(new DefaultComboBoxModel<>(dataMap.get(categoryDropdown.getSelectedItem())));

        categoryDropdown.addActionListener(e -> roomDropdown.setModel(new DefaultComboBoxModel<>(dataMap.get(categoryDropdown.getSelectedItem()))));

        btnSetProperties.addActionListener(e -> {
        	String season = (String) cmbTermSeason.getSelectedItem();
        	String yearText = txtYear.getText().trim();
        	if (!yearText.matches("\\d{4}")) {
        	    JOptionPane.showMessageDialog(this, "Year must be a valid 4-digit number.");
        	    return;
        	}
        	
        	int enteredYear = Integer.parseInt(yearText);
        	int currentYear = LocalDate.now().getYear();
        	
        	if (enteredYear < currentYear) {
        		JOptionPane.showMessageDialog(this, "Year must be the current year or later (≥" + currentYear + ").");
        		return;
        	}
        	
        	selectedTerm = season + " " + enteredYear;
            selectedBuilding = (String) categoryDropdown.getSelectedItem();
            selectedRoom = (String) roomDropdown.getSelectedItem();

            JOptionPane.showMessageDialog(this,
                "Properties Set:\nTerm: " + selectedTerm +
                "\nBuilding: " + selectedBuilding +
                "\nRoom: " + selectedRoom,
                "Confirmation", JOptionPane.INFORMATION_MESSAGE);
        });

        btnResetData.addActionListener(e -> {
            txtCourseCode.setText("");
            cmbSection.setSelectedIndex(0);
            txtCourseName.setText("");
            txtProfessor.setText("");
            for (JCheckBox cb : days) cb.setSelected(false);
            txtStartTime.setText("");
            cmbStartAMPM.setSelectedIndex(0);
            txtEndTime.setText("");
            cmbEndAMPM.setSelectedIndex(0);
        });

        btnCancel.addActionListener(e -> {
            new MainPage();
            dispose();
        });

        btnResetSchedule.addActionListener(e -> {
            cmbTermSeason.setSelectedIndex(0);
            categoryDropdown.setSelectedIndex(0);
            roomDropdown.setSelectedIndex(0);
            tableModel.setRowCount(0);
            selectedTerm = null;
            selectedBuilding = null;
            selectedRoom = null;
        });

        btnAddRow.addActionListener(e -> {
            if (tableModel.getRowCount() >= MAX_ROWS) {
            	JOptionPane.showMessageDialog(this, "You can only add up to 10 rows");
            	return;
            }
            
            String courseCode = txtCourseCode.getText().trim().toUpperCase();
            String courseName = txtCourseName.getText().trim();
            String professor = txtProfessor.getText().trim();
            String startTime = txtStartTime.getText().trim();
            String endTime = txtEndTime.getText().trim();
            String section = (String) cmbSection.getSelectedItem();
            String startPeriod = (String) cmbStartAMPM.getSelectedItem();
            String endPeriod = (String) cmbEndAMPM.getSelectedItem();
            
            boolean startEmpty = startPeriod.isEmpty();
            boolean endEmpty = endPeriod.isEmpty();
            	
            // Validate course code format: AAA 123 or AAA 123G/H or both
            if (!courseCode.matches("[A-Z]{3} \\d{3}(G|H|GH)?")) {
                JOptionPane.showMessageDialog(this, "Course code must be in format: AAA 123 or AAA 123G/H");
                return;
            }

            if (courseCode.isEmpty() || courseName.isEmpty() || professor.isEmpty()) {
    			JOptionPane.showMessageDialog(this, "Please fill in all required fields");
    			return;
    		}
            
            if (!courseName.matches("[a-zA-Z\\s\\-&.():',]+")) {
    			JOptionPane.showMessageDialog(this, "Course name cannot contain numbers or invalid characters");
    			return;
    		}
            
            if (!professor.matches("[a-zA-Z\\s]+")) {
    			JOptionPane.showMessageDialog(this, "Professor last name cannot contain numbers or invalid characters");
    			return;
    		}
            
            if ((startEmpty && !endEmpty) || (!startEmpty && endEmpty)) {
    			JOptionPane.showMessageDialog(this, "Please enter both start and end times");
    			return;
    		}
    		
    		if (!startEmpty && !endEmpty) {
    			if (!startTime.matches("\\d{1,2}:\\d{2}") || !endTime.matches("\\d{1,2}:\\d{2}")) {
    				JOptionPane.showMessageDialog(this, "Time format must be HH:MM");
    				return;
    			}
    			
    			try {
    				String fullStart = startTime + " " + startPeriod;
    				String fullEnd = endTime + " " + endPeriod;
    				
    				SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
    				
    				Date sTime = sdf.parse(fullStart);
    				Date eTime = sdf.parse(fullEnd);
    				
    				long diff = eTime.getTime() - sTime.getTime();
    				if (diff < 30 * 60 * 1000) {
    					JOptionPane.showMessageDialog(this, "Start and end times must be at least 30 minutes apart.");
    					return;
    				}
    			} catch (Exception ex) {
    				JOptionPane.showMessageDialog(this, "Invalid time input.");
    				ErrorLogger.log("Time parsing error: " + ex.getMessage());
    				ex.printStackTrace();
    				return;
    			}

            // Validate selected days
            StringBuilder dayText = new StringBuilder();
            for (JCheckBox cb : days) if (cb.isSelected()) dayText.append(cb.getText());
            if (dayText.length() == 0) {
                JOptionPane.showMessageDialog(this, "Please select at least one instruction day.");
                return;
            }

            // Everything valid — add row
            String formattedTime = startTime + " " + startPeriod + " - " + endTime + " " + endPeriod;
            
            Vector<String> row = new Vector<>();
            row.add(courseCode);
            row.add(section);
            row.add(courseName);
            row.add(dayText.toString());
            row.add(formattedTime);
            row.add(professor);
            
            for (int i = 0; i < tableModel.getRowCount(); i++) {
            	boolean isDuplicate = true;
            	for (int j = 0; j < tableModel.getColumnCount(); j++) {
            		String existingValue = String.valueOf(tableModel.getValueAt(i, j)).trim();
            		String newValue = row.get(j).trim();
            		if (!existingValue.equalsIgnoreCase(newValue)) {
            			isDuplicate = false;
            			break;
            		}
            	}
            	
            	if (isDuplicate) {
            		JOptionPane.showMessageDialog(this, "Duplicate entry. That exact course already exists in the table.");
            		return;
            	}
            }
            
            tableModel.addRow(row);
    		}
        });
        
        btnCreateSchedule.addActionListener(e -> {
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No courses added! Please add at least one course.");
                return;
            }
            if (selectedTerm == null || selectedBuilding == null || selectedRoom == null) {
                JOptionPane.showMessageDialog(this, "Please set properties");
                return;
            }
            Vector<Vector<String>> scheduleData = new Vector<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Vector<String> row = new Vector<>();
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    row.add((String) tableModel.getValueAt(i, j));
                }
                scheduleData.add(row);
            }
            UpdatedProjectFrame frame = new UpdatedProjectFrame(scheduleData, selectedTerm, selectedBuilding, selectedRoom);
            frame.setVisible(true);
            dispose();
        });

        setVisible(true);
    }
    
    /**
     * Pulls all section numbers from database and adds 0's to section
     * numbers that are single digit for increased user-friendliness
     * @param comboBox - Section numbers from database are loaded into
     * dropdown menu and filled
     */
    
    private void loadSectionsFromDatabase(JComboBox<String> comboBox) {
        String url = "jdbc:mysql://localhost:3306/exceldatabase";
        String user = "root";
        String pass = "##Toadsworth0130!";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT Section_Number FROM section ORDER BY Section_Number ASC")) {

            while (rs.next()) {
                String section = rs.getString("Section_Number").trim();
                // Add leading 0 if needed
                if (section.matches("\\d")) {
                    section = "0" + section;
                }
                comboBox.addItem(section);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load sections from database.");
            ErrorLogger.log("Section load error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UpdatedCourseSchedule::new);
    }
}