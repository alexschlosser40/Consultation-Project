package capstoneProject;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/* File Name: MainPage
 * Purpose: Serves as the primary landing page or dashboard from which users can navigate to different sections.
 * Date Completed: 4/16/2025
 */


public class MainPage {
    public MainPage() {
    	JFrame frame = new JFrame("Main Page");
    	frame.setSize(1000, 700); // Default starting size
    	frame.setMinimumSize(new Dimension(800, 600)); // Still allows resizing
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setLayout(new BorderLayout());
    	frame.setLocationRelativeTo(null); // Centered on screen

        // === NAVIGATION BAR ===
        JPanel navBar = new JPanel();
        navBar.setPreferredSize(new Dimension(1000, 70));
        navBar.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 15));
        navBar.setBackground(new Color(139, 0, 0));
        navBar.setBorder(new MatteBorder(0, 0, 4, 0, Color.BLACK));

        JButton homeBtn = createNavButton("Home", frame, MainPage::new);
        JButton settingsBtn = createNavButton("Settings", frame, Settings::new);
        JButton supportBtn = createNavButton("Support", frame, SupportPage::new);

        navBar.add(homeBtn);
        navBar.add(settingsBtn);
        navBar.add(supportBtn);

        frame.add(navBar, BorderLayout.NORTH);

        // === MAIN CONTENT PANEL ===
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);

        // === SEARCH ROOM BUTTON + LOGO PANEL ===
        JButton searchButton = createRoundedButton("Search Room", new Color(139, 0, 0), Color.WHITE);
        searchButton.setPreferredSize(new Dimension(120, 120));
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.addActionListener(e -> {
            new BuildingOptions(frame);
            frame.dispose();
        });

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/bearlogo.png"));
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(searchButton);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(logoLabel);

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0;
        gbcLeft.gridy = 0;
        gbcLeft.weightx = 0.2;
        gbcLeft.weighty = 1.0;
        gbcLeft.insets = new Insets(65, 20, 20, 20);
        gbcLeft.anchor = GridBagConstraints.FIRST_LINE_START;
        gbcLeft.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(leftPanel, gbcLeft);

        // === TABLE PANEL ===
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setMinimumSize(new Dimension(500, 200));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(139, 0, 0), 3, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        String[] columnNames = {"Course", "§", "Course Name", "Day", "Time", "Professor"};
        Object[][] data;

        if (!ScheduleDataHolder.userSchedule.isEmpty()) {
            int rows = ScheduleDataHolder.userSchedule.size();
            data = new Object[rows][6];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < 6; j++) {
                    data[i][j] = ScheduleDataHolder.userSchedule.get(i).get(j);
                }
            }
        } else {
            data = new Object[10][6];
        }

        JTable table = new JTable(new DefaultTableModel(data, columnNames));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);
        
        String lastCSV = FilePathConfig.getLastCSVPath();
        
        if (!lastCSV.isEmpty()) {
        	loadCSVIntoTable(lastCSV, (DefaultTableModel) table.getModel());
        }

        int[] widths = {100, 30, 200, 60, 180, 150};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setForeground(Color.WHITE);
        header.setBackground(Color.BLACK);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setBackground(new Color(245, 245, 245));
        table.setGridColor(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        GridBagConstraints gbcTable = new GridBagConstraints();
        gbcTable.gridx = 1;
        gbcTable.gridy = 0;
        gbcTable.weightx = 0.8;
        gbcTable.weighty = 1.0;
        gbcTable.insets = new Insets(20, 20, 20, 20);
        gbcTable.fill = GridBagConstraints.BOTH;
        mainPanel.add(tablePanel, gbcTable);

        // === ACTION BUTTONS ===
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 2, true),
                new EmptyBorder(10, 20, 10, 20)
        ));

        JButton uploadFileButton = createRoundedButton("Upload File", Color.DARK_GRAY, Color.WHITE);
        JButton scheduleButton = createRoundedButton("Schedule", new Color(139, 0, 0), Color.WHITE);
        JButton courseSearchButton = createRoundedButton("Course Search", new Color(70, 0, 0), Color.WHITE);

        uploadFileButton.addActionListener(e -> {
            new FileUpload();
            frame.dispose();
        });

        scheduleButton.addActionListener(e -> {
            new UpdatedCourseSchedule();
            frame.dispose();
        });

        courseSearchButton.addActionListener(e -> {
            new courseSearchPage();
            frame.dispose();
        });

        buttonPanel.add(uploadFileButton);
        buttonPanel.add(scheduleButton);
        buttonPanel.add(courseSearchButton);

        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.gridx = 0;
        gbcButtons.gridy = 1;
        gbcButtons.gridwidth = 2;
        gbcButtons.weightx = 1.0;
        gbcButtons.weighty = 0.0;
        gbcButtons.insets = new Insets(20, 20, 20, 20);
        gbcButtons.fill = GridBagConstraints.HORIZONTAL;
        gbcButtons.anchor = GridBagConstraints.SOUTH;
        mainPanel.add(buttonPanel, gbcButtons);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 2, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        return button;
    }

    private JButton createNavButton(String text, JFrame frame, Runnable redirect) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(139, 0, 0));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addActionListener((ActionEvent e) -> {
            frame.dispose();
            redirect.run();
        });

        return button;
    }
    
    private void loadCSVIntoTable(String csvPath, DefaultTableModel model) {
    	try (Scanner scanner = new Scanner(new File(csvPath))) {
    		model.setRowCount(0);
    		
    		if (scanner.hasNextLine()) {
    			scanner.nextLine();
    		}
    		
    		while (scanner.hasNextLine()) {
    			String line = scanner.nextLine();
    			String[] values = line.split(",", -1);
    			if (values.length == model.getColumnCount()) {
    				model.addRow(values);
    			}
    		}
    	} catch (IOException e) {
    		JOptionPane.showMessageDialog(null, "Error loading schedule from file");
    		ErrorLogger.log("Failed to load CSV: " + e.getMessage());
    	}
    }
}