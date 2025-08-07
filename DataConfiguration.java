package capstoneProject;

import java.awt.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.ActionEvent;
import java.io.*;

/* File Name: DataConfiguration
 * Purpose: Handles reading, writing, and managing configuration settings for the application's data and CSV file paths.
 * Date Completed: 4/16/2025
 */

public class DataConfiguration {
	public DataConfiguration() {
		JFrame frame = new JFrame("Data Configuration");
		frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setLocationRelativeTo(null);
        
        // === NavBar ===
        frame.add(new NavBar(frame), BorderLayout.NORTH);
        // === Main Panel ===
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea previewArea = new JTextArea();
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        previewArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(previewArea);

        JButton uploadButton = new RoundedButton("Preview Data File (CSV)");
        JButton backButton = new RoundedButton("Back to Settings");
        JButton clearButton = new RoundedButton("Clear Preview");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(uploadButton);
        buttonPanel.add(backButton);
        buttonPanel.add(clearButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        
        clearButton.addActionListener(e -> {
        	previewArea.setText("");
        });

        // === Upload Button Logic ===
        uploadButton.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();

            // Only allow CSV files
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                // Validate file extension manually as well
                if (!file.getName().toLowerCase().endsWith(".csv")) {
                    JOptionPane.showMessageDialog(frame,
                            "Invalid file type. Please select a CSV file.",
                            "Invalid File",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                previewArea.setText("Previewing: " + file.getName() + "\n\n");
                previewArea.append(loadCsvPreview(file));
            }
        });


        // === Back Button Logic ===
        backButton.addActionListener(e -> {
            frame.dispose();
            new Settings();
        });

        frame.setVisible(true);
    }

    private String loadCsvPreview(File file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int rowCount = 0;
            while ((line = reader.readLine()) != null && rowCount < 10) {
                sb.append(line).append("\n");
                rowCount++;
            }
        } catch (IOException e) {
            sb.append("Failed to load file: ").append(e.getMessage());
            ErrorLogger.log(e.getMessage());
        }
        return sb.toString();
	}
}