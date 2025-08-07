package capstoneProject;

import capstoneProject.mingesPage;
import capstoneProject.georgePage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.RowFilter.Entry;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Vector;

/* File Name: RoomInfoScreen
 * Purpose: Displays room-specific details in a formatted layout, including capacity, room type, or schedule information.
 * Date Completed: 4/16/2025
 */


public class RoomInfoScreen {
	private JFrame frame;
	private JTable table;
	private TableRowSorter<DefaultTableModel> sorter;

	public RoomInfoScreen(String buildingCode, String roomId, JFrame parent) {
		if (parent != null) parent.dispose();

		Color lrRed = new Color(132, 0, 20);
		Color lrGold = new Color(255, 204, 0);
		Color darkGray = new Color(60, 60, 60);

		frame = new JFrame(buildingCode + " - Room " + roomId);
		frame.setSize(1100, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(Color.WHITE);
		frame.getRootPane().setBorder(new LineBorder(lrRed, 3));

		JPanel panel = new JPanel(null);
		panel.setBounds(20, 20, 1040, 520);
		panel.setBackground(Color.WHITE);
		panel.setBorder(new LineBorder(darkGray, 1));
		frame.getContentPane().add(panel);

		JButton clearFiltersButton = new JButton("Clear Filters");
		clearFiltersButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
		clearFiltersButton.setBackground(darkGray);
		clearFiltersButton.setForeground(Color.WHITE);
		clearFiltersButton.setBounds(890, 89, 130, 25);
		clearFiltersButton.setVisible(false);
		panel.add(clearFiltersButton);

		clearFiltersButton.addActionListener(e -> {
			sorter.setRowFilter(null);
			clearFiltersButton.setVisible(false);
		});

		String displayBuildingName = switch (buildingCode) {
			case "SCI" -> "Minges Science";
			case "GEORGE" -> "George Hall";
			default -> buildingCode;
		};

		frame.setTitle(displayBuildingName + " - Room " + roomId);

		JLabel lblTitle = new JLabel(displayBuildingName, SwingConstants.CENTER);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
		lblTitle.setForeground(lrRed);
		lblTitle.setBounds(325, 10, 400, 30);
		panel.add(lblTitle);

		JLabel lblSubtitle = new JLabel("Room " + roomId, SwingConstants.CENTER);
		lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblSubtitle.setForeground(darkGray);
		lblSubtitle.setBounds(325, 40, 400, 30);
		panel.add(lblSubtitle);

		String[] columnNames = {"Course", "§", "Course Name", "Day", "Time", "Professor"};
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);
		table = new JTable(model);
		sorter = new TableRowSorter<>(model);
		table.setRowSorter(sorter);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		table.setRowHeight(22);

		JTableHeader tableHeader = table.getTableHeader();
		tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
		tableHeader.setBackground(darkGray);
		tableHeader.setForeground(Color.WHITE);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(20, 130, 1000, 300);
		panel.add(scrollPane);

		ImageIcon sortIcon = new ImageIcon(getClass().getResource("/images/sortIcon.png"));
		ImageIcon resizedSortIcon = new ImageIcon(sortIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
		JLabel lblSortByIcon = new JLabel(resizedSortIcon);
		lblSortByIcon.setBounds(435, 51, 100, 100);
		lblSortByIcon.setToolTipText("Sort By: (Toggle Asc/Desc)");
		panel.add(lblSortByIcon);

		JLabel lblSortBy = new JLabel("Sort By:");
		lblSortBy.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblSortBy.setBounds(415, 75, 75, 50);
		panel.add(lblSortBy);

		lblSortByIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Object[] options = {"Ascending", "Descending"};
				int choice = JOptionPane.showOptionDialog(frame, "Choose sorting order",
						"Sort Order", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
						null, options, options[0]);

				if (choice == 0) {
					sorter.setSortKeys(java.util.List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
				} else if (choice == 1) {
					sorter.setSortKeys(java.util.List.of(new RowSorter.SortKey(0, SortOrder.DESCENDING)));
				}
			}
		});

		ImageIcon filterIcon = new ImageIcon(getClass().getResource("/images/filterIcon.png"));
		ImageIcon resizedFilterIcon = new ImageIcon(filterIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
		JLabel lblFilterIcon = new JLabel(resizedFilterIcon);
		lblFilterIcon.setBounds(571, 51, 100, 100);
		panel.add(lblFilterIcon);

		JLabel lblFilter = new JLabel("Filter:");
		lblFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblFilter.setBounds(561, 75, 75, 50);
		panel.add(lblFilter);

		lblFilterIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String courseFilter = JOptionPane.showInputDialog(frame, "Enter Course Code (Leave blank for all):", "Filter Courses", JOptionPane.QUESTION_MESSAGE);
				String professorFilter = JOptionPane.showInputDialog(frame, "Enter Professor Name (Leave blank for all):", "Filter Professor", JOptionPane.QUESTION_MESSAGE);
				String selectedDay = (String) JOptionPane.showInputDialog(frame, "Select Day:", "Filter Day", JOptionPane.QUESTION_MESSAGE, null, new String[]{"All", "MF", "MWF", "MON", "TR", "TUE", "WED", "THU", "FRI"}, "All");

				sorter.setRowFilter(new RowFilter<Object, Object>() {
					@Override
					public boolean include(Entry<?, ?> entry) {
						String course = entry.getStringValue(0);
						String professor = entry.getStringValue(5);
						String day = entry.getStringValue(3);

						boolean courseMatches = courseFilter == null || courseFilter.isEmpty() || course.toLowerCase().contains(courseFilter.toLowerCase());
						boolean professorMatches = professorFilter == null || professorFilter.isEmpty() || professor.toLowerCase().contains(professorFilter.toLowerCase());
						boolean dayMatches = selectedDay == null || selectedDay.equals("All") || day.equals(selectedDay);

						return courseMatches && professorMatches && dayMatches;
					}
				});

				clearFiltersButton.setVisible(true);
			}
		});

		// Back
		JButton backButton = new JButton("Back");
		backButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
		backButton.setBackground(new Color(70, 130, 180));
		backButton.setForeground(Color.WHITE);
		backButton.setBounds(258, 452, 120, 30);
		panel.add(backButton);
		backButton.addActionListener(e -> {
			if (buildingCode.equals("SCI")) {
				new mingesPage(frame);
			} else if (buildingCode.equals("GEORGE")) {
				new georgePage(frame);
			} else {
				new BuildingOptions(frame);
			}
		});

		// Save
		JButton saveTableButton = new JButton("Save Table");
		saveTableButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
		saveTableButton.setBackground(new Color(0, 128, 255));
		saveTableButton.setForeground(Color.BLACK);
		saveTableButton.setBounds(460, 452, 120, 30);
		panel.add(saveTableButton);
		saveTableButton.addActionListener(e -> saveTable(table));

		// Close
		JButton closeButton = new JButton("Close");
		closeButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
		closeButton.setBackground(new Color(255, 0, 0));
		closeButton.setForeground(Color.WHITE);
		closeButton.setBounds(683, 452, 120, 30);
		panel.add(closeButton);
		closeButton.addActionListener(e -> {
			new BuildingOptions(frame);
			frame.dispose();
		});

		loadRoomData(buildingCode, roomId, model);
		frame.setVisible(true);
	}

	/**
	 * Loads the course schedule data for a specific building and room from the database.
	 * Fills the provided table model with the retrieved data.
	 *
	 * @param buildingCode The code of the building (e.g., "SCI", "GEORGE").
	 * @param roomId The room identifier.
	 * @param model The table model to populate with room data.
	 */
	private void loadRoomData(String buildingCode, String roomId, DefaultTableModel model) {
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exceldatabase", "root", "##Toadsworth0130!")) {
			String query = "SELECT EVENT_ID, SECTION, EVENT_LONG_NAME, DAY, CONCAT(START_TIME, ' - ', END_TIME), LAST_NAME " +
					"FROM staging_course_schedule WHERE ROOM_ID = ? AND BUILDING_CODE = ?";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, roomId);
			stmt.setString(2, buildingCode);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Vector<String> row = new Vector<>();
				for (int i = 1; i <= 6; i++) row.add(rs.getString(i));
				model.addRow(row);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Opens a save dialog for the user to save the table as an image (JPEG/PNG) or a CSV file.
	 *
	 * @param table The JTable to save.
	 */
	public static void saveTable(JTable table) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save Table");
		FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("JPEG Image (*.jpg, *.jpeg)", "jpg", "jpeg");
		FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG Image (*.png)", "png");
		FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV File (*.csv)", "csv");
		fileChooser.addChoosableFileFilter(jpgFilter);
		fileChooser.addChoosableFileFilter(pngFilter);
		fileChooser.addChoosableFileFilter(csvFilter);
		fileChooser.setFileFilter(jpgFilter);
		int userSelection = fileChooser.showSaveDialog(null);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			String filePath = fileToSave.getAbsolutePath();
			String selectedFormat = fileChooser.getFileFilter().getDescription();
			if (selectedFormat.contains("JPEG")) {
				if (!filePath.toLowerCase().endsWith(".jpg") && !filePath.toLowerCase().endsWith(".jpeg")) filePath += ".jpg";
				saveTableAsImage(table, filePath, "jpg");
			} else if (selectedFormat.contains("PNG")) {
				if (!filePath.toLowerCase().endsWith(".png")) filePath += ".png";
				saveTableAsImage(table, filePath, "png");
			} else if (selectedFormat.contains("CSV")) {
				if (!filePath.toLowerCase().endsWith(".csv")) filePath += ".csv";
				saveTableAsCSV(table, filePath);
			} else {
				JOptionPane.showMessageDialog(null, "Error saving table. ");
			}
		}
	}

	/**
	 * Saves the table as an image file (JPEG or PNG).
	 *
	 * @param table The JTable to save.
	 * @param filePath The file path to save the image.
	 * @param format The image format ("jpg" or "png").
	 */
	private static void saveTableAsImage(JTable table, String filePath, String format) {
		JTableHeader header = table.getTableHeader();
		int headerHeight = header.getHeight();
		int tableHeight = table.getHeight();
		int totalWidth = table.getWidth();
		int totalHeight = headerHeight + tableHeight;
		BufferedImage image = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, totalWidth, totalHeight);
		header.setSize(totalWidth, headerHeight);
		header.printAll(g2d);
		g2d.translate(0, headerHeight);
		table.setSize(totalWidth, tableHeight);
		table.printAll(g2d);
		g2d.dispose();
		try {
			ImageIO.write(image, format, new File(filePath));
			JOptionPane.showMessageDialog(null, "Table saved as " + filePath);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error saving image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Saves the table content as a CSV file.
	 *
	 * @param table The JTable whose data should be saved.
	 * @param filePath The file path where the CSV will be saved.
	 */
	private static void saveTableAsCSV(JTable table, String filePath) {
	    try (PrintWriter writer = new PrintWriter(new File(filePath))) {
	        StringBuilder sb = new StringBuilder();

	        // Write column headers
	        for (int i = 0; i < table.getColumnCount(); i++) {
	            sb.append(table.getColumnName(i));
	            if (i < table.getColumnCount() - 1) sb.append(",");
	        }
	        sb.append("\n");

	        // Write row data
	        for (int i = 0; i < table.getRowCount(); i++) {
	            for (int j = 0; j < table.getColumnCount(); j++) {
	                Object value = table.getValueAt(i, j);
	                sb.append(value != null ? value.toString() : "");
	                if (j < table.getColumnCount() - 1) sb.append(",");
	            }
	            sb.append("\n");
	        }

	        writer.write(sb.toString());
	        JOptionPane.showMessageDialog(null, "Table saved as " + filePath);
	    } catch (IOException e) {
	        JOptionPane.showMessageDialog(null, "Error saving CSV: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}
}