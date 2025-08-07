package capstoneProject;

import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import capstoneProject.UpdatedProjectFrame.editingPage;

import java.util.Date;
import java.util.Vector;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

/* File Name: InsertRow1
 * Purpose: Allows users to insert a new course row with validated input fields into the schedule table.
 * Date Completed: 4/16/2025
 */


public class InsertRow1 extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultTableModel tableModel;
	private editingPage parent;

	/**
	 * Launch the application.
	 */
	public InsertRow1(DefaultTableModel tableModel, editingPage parent) {
		this.tableModel = tableModel;
		this.parent = parent;
        setTitle("Insert Row Screen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(null);
        setLocationRelativeTo(null);
        
        Color lrRed = new Color(132, 0, 20);
        Color lrGold = new Color(255, 204, 0);
        Color darkGrey = new Color(50, 50, 50);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font dropdownFont = new Font("Segoe UI", Font.PLAIN, 12);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 26);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        
        JLabel lblTitle = new JLabel("Insert Row Screen", SwingConstants.CENTER);
        lblTitle.setFont(titleFont);
        lblTitle.setBounds(150, 10, 300, 30);
        add(lblTitle);
        
        JLabel lblCourseCode = new JLabel("* Course Code:");
        lblCourseCode.setBounds(80, 60, 150, 25);
        lblCourseCode.setFont(labelFont);
        add(lblCourseCode);
        
        JTextField txtCourseCode = new JTextField();
        txtCourseCode.setBounds(250, 60, 200, 25);
        add(txtCourseCode);
        
        JLabel lblCourseSection = new JLabel("* Course Section:");
        lblCourseSection.setBounds(80, 100, 150, 25);
        lblCourseSection.setFont(labelFont);
        add(lblCourseSection);
        
        JComboBox<String> cmbCourseSection = new JComboBox<>();
        cmbCourseSection.setBounds(250, 100, 80, 25);
        cmbCourseSection.setBackground(Color.GRAY);
        cmbCourseSection.setForeground(Color.WHITE);
        cmbCourseSection.setFont(dropdownFont);
        loadSectionsFromDatabase(cmbCourseSection);
        add(cmbCourseSection);
        
        JLabel lblCourseName = new JLabel("* Course Name:");
        lblCourseName.setBounds(80, 140, 150, 25);
        lblCourseName.setFont(labelFont);
        add(lblCourseName);
        
        JTextField txtCourseName = new JTextField();
        txtCourseName.setBounds(250, 140, 200, 25);
        add(txtCourseName);
        
        JLabel lblInstructionDays = new JLabel("* Instruction Day(s):");
        lblInstructionDays.setBounds(80, 180, 150, 25);
        lblInstructionDays.setFont(labelFont);
        add(lblInstructionDays);
        
        JComboBox<String> cmbInstructionDays = new JComboBox<>(new String[]{"M", "T", "W", "R", "F", "MW", "MF", "MWF", "MTWR", "MTRF", "TR", "WF", "ONLINE", "TBD"});
        cmbInstructionDays.setBounds(250, 180, 100, 25);
        cmbInstructionDays.setBackground(Color.GRAY);
        cmbInstructionDays.setForeground(Color.WHITE);
        cmbInstructionDays.setFont(dropdownFont);
        add(cmbInstructionDays);
        
        JLabel lblInstructionTime = new JLabel("* Instruction Time:");
        lblInstructionTime.setBounds(80, 220, 150, 25);
        lblInstructionTime.setFont(labelFont);
        add(lblInstructionTime);
        
        JTextField txtStartTime = new JTextField(5);
        txtStartTime.setBounds(250, 220, 50, 25);
        add(txtStartTime);
        
        JLabel lblTo = new JLabel("-");
        lblTo.setBounds(365, 220, 10, 25);
        lblTo.setFont(labelFont);
        add(lblTo);
        
        JTextField txtEndTime = new JTextField(5);
        txtEndTime.setBounds(375, 220, 50, 25);
        add(txtEndTime);
        
        JComboBox<String> cmbStartAMPM = new JComboBox<>(new String[]{"AM", "PM"});
        cmbStartAMPM.setBounds(310, 220, 50, 25);
        cmbStartAMPM.setBackground(Color.GRAY);
        cmbStartAMPM.setForeground(Color.WHITE);
        cmbStartAMPM.setFont(dropdownFont);
        add(cmbStartAMPM);
        
        JComboBox<String> cmbEndAMPM = new JComboBox<>(new String[]{"AM", "PM"});
        cmbEndAMPM.setBounds(430, 220, 50, 25);
        cmbEndAMPM.setBackground(Color.GRAY);
        cmbEndAMPM.setForeground(Color.WHITE);
        cmbEndAMPM.setFont(dropdownFont);
        add(cmbEndAMPM);
        
        JLabel lblProfessor = new JLabel("* Professor");
        lblProfessor.setBounds(80, 260, 150, 25);
        lblProfessor.setFont(labelFont);
        add(lblProfessor);
        
        JTextField txtProfessor = new JTextField();
        txtProfessor.setBounds(250, 260, 200, 25);
        add(txtProfessor);
        
        JButton btnInsert = new JButton("Insert Row");
        btnInsert.setBounds(180, 310, 120, 40);
        btnInsert.setBackground(new Color(0, 128, 255));
        btnInsert.setForeground(Color.BLACK);
        btnInsert.setFont(buttonFont);
        add(btnInsert);
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(320, 310, 120, 40);
        btnCancel.setBackground(lrRed);
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(buttonFont);
        add(btnCancel);
        
        btnInsert.addActionListener(e -> {
            if (tableModel.getRowCount() >= 10) {
                JOptionPane.showMessageDialog(null, "You can only have up to 10 courses.");
                return;
            }

            String course = txtCourseCode.getText().trim();
            String section = (String) cmbCourseSection.getSelectedItem();
            String courseName = txtCourseName.getText().trim();
            String days = ((String) cmbInstructionDays.getSelectedItem()).trim();
            String start = txtStartTime.getText().trim();
            String end = txtEndTime.getText().trim();
            String startAMPM = (String) cmbStartAMPM.getSelectedItem();
            String endAMPM = (String) cmbEndAMPM.getSelectedItem();
            String professor = txtProfessor.getText().trim();

            // Basic field validation
            if (course.isEmpty() || courseName.isEmpty() || professor.isEmpty() || days.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please complete all fields.");
                return;
            }

            if (!course.matches("[A-Z]{3}\\s\\d{3}(G|H|GH)?")) {
                JOptionPane.showMessageDialog(null, "Course code must be in format: AAA 123 or AAA 123G/H");
                return;
            }

            if (!courseName.matches("[a-zA-Z\\s\\-&.():',]+")) {
                JOptionPane.showMessageDialog(null, "Course name cannot contain numbers.");
                return;
            }

            if (!professor.matches("[a-zA-Z\\s]+")) {
                JOptionPane.showMessageDialog(null, "Professor name cannot contain numbers.");
                return;
            }

            // Handle ONLINE special case
            if (days.equalsIgnoreCase("ONLINE")) {
                start = end = "12:00";
                startAMPM = endAMPM = "AM";
            } else {
                if ((start.isEmpty() && !end.isEmpty()) || (!start.isEmpty() && end.isEmpty())) {
                    JOptionPane.showMessageDialog(null, "Enter both start and end times or leave both empty.");
                    return;
                }

                if (!start.matches("\\d{1,2}:\\d{2}") || !end.matches("\\d{1,2}:\\d{2}")) {
                    JOptionPane.showMessageDialog(null, "Time must be in HH:MM format.");
                    return;
                }

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                    Date startTime = sdf.parse(start + " " + startAMPM);
                    Date endTime = sdf.parse(end + " " + endAMPM);
                    long diff = endTime.getTime() - startTime.getTime();
                    if (diff < 30 * 60 * 1000) {
                        JOptionPane.showMessageDialog(null, "Start and end time must be at least 30 minutes apart.");
                        return;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Invalid time entered.");
                    ErrorLogger.log("Time parse error: " + ex.getMessage());
                    return;
                }
            }

            // Final formatted row
            String formattedTime = start + " " + startAMPM + " - " + end + " " + endAMPM;
            Vector<String> newRow = new Vector<>();
            newRow.add(course);
            newRow.add(section);
            newRow.add(courseName);
            newRow.add(days);
            newRow.add(formattedTime);
            newRow.add(professor);
            
            for (int i = 0; i < tableModel.getRowCount(); i++) {
            	boolean isDuplicate = true;
            	for (int j = 0; j < tableModel.getColumnCount(); j++) {
            		Object cellVal = tableModel.getValueAt(i, j);
            		if (!newRow.get(j).equals(cellVal != null ? cellVal.toString() : "")) {
            			isDuplicate = false;
            			break;
            		}
            	}
            	
            	if (isDuplicate) {
            		JOptionPane.showMessageDialog(this, "This course already exists in the schedule.");
            		return;
            	}
            }

            tableModel.addRow(newRow);
            parent.queueInsert(newRow);
            dispose();
            parent.setVisible(true);
        });
        
        btnCancel.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		dispose();
        		parent.setVisible(true);
        	}
        });
            
        setVisible(true);
	}
	
	/**
	 * Pulls all section numbers from DB and adds 0's to single-digit section
	 * numbers
	 * @param comboBox - Section numbers are loaded into dropdown menu and filled
	 * from DB
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
}