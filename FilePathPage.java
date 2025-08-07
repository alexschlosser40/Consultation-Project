package capstoneProject;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Properties;

/* File Name: FilePathPage
 * Purpose: Provides a GUI interface to view and edit file path configurations for data input/output.
 * Date Completed: 4/16/2025
 */

public class FilePathPage {
	private final Properties config = new Properties();
    private final String CONFIG_FILE = "default_paths.properties";
    
	public FilePathPage() {
		JFrame frame = new JFrame("Default File Paths");
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);

        // === NavBar ===
        frame.add(new NavBar(frame), BorderLayout.NORTH);

        // === Main Panel ===
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(30, 10, 30, 10),
                        "Default File Paths",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 18),
                        Color.BLACK
                )
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // === Fields & Buttons ===
        JLabel importLabel = new JLabel("Default Import Folder:");
        JTextField importField = new JTextField(30);
        JButton importBrowse = new RoundedButton("Browse");

        JLabel exportLabel = new JLabel("Default Backup Folder:");
        JTextField exportField = new JTextField(30);
        JButton exportBrowse = new RoundedButton("Browse");

        loadPaths(importField, exportField);

        // === Add Components ===
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(importLabel, gbc);
        gbc.gridx = 1;
        panel.add(importField, gbc);
        gbc.gridx = 2;
        panel.add(importBrowse, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(exportLabel, gbc);
        gbc.gridx = 1;
        panel.add(exportField, gbc);
        gbc.gridx = 2;
        panel.add(exportBrowse, gbc);

        // === Save / Back Buttons ===
        JButton saveButton = new RoundedButton("Save");
        JButton backButton = new RoundedButton("Back to Settings");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);

        // === Browse Button Actions ===
        importBrowse.addActionListener(e -> {
            String selected = chooseFolder(frame);
            if (selected != null) importField.setText(selected);
        });

        exportBrowse.addActionListener(e -> {
            String selected = chooseFolder(frame);
            if (selected != null) exportField.setText(selected);
        });

        // === Save Action ===
        saveButton.addActionListener((ActionEvent e) -> {
            config.setProperty("import_path", importField.getText());
            config.setProperty("export_path", exportField.getText());
            try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
                config.store(out, "Default File Paths");
                JOptionPane.showMessageDialog(frame, "Paths saved successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Failed to save paths.");
            }
        });

        // === Back Action ===
        backButton.addActionListener(e -> {
            frame.dispose();
            new Settings();
        });
        
     // === WRAPPER PANEL TO CENTER THE MAIN PANEL ===
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcWrapper = new GridBagConstraints();
        gbcWrapper.gridx = 0;
        gbcWrapper.gridy = 0;
        gbcWrapper.anchor = GridBagConstraints.CENTER;
        gbcWrapper.fill = GridBagConstraints.BOTH;
        gbcWrapper.weightx = 1;
        gbcWrapper.weighty = 1;
        gbcWrapper.insets = new Insets(30, 0, 30, 0);
        wrapperPanel.add(panel, gbcWrapper);

        // === ADD WRAPPER TO FRAME ===
        frame.add(wrapperPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void loadPaths(JTextField importField, JTextField exportField) {
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            config.load(in);
            importField.setText(config.getProperty("import_path", ""));
            exportField.setText(config.getProperty("export_path", ""));
        } catch (IOException e) {
            // File doesn't exist yet — use empty defaults
        }
    }

    private String chooseFolder(Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
	}
}