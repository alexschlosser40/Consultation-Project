package capstoneProject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/* File Name: DataBackupFrequency
 * Purpose: Manages user selection of how often course data should be backed up (e.g., daily, weekly).
 * Date Completed: 4/16/2025
 */

public class DataBackupFrequency {
    private final Properties config = new Properties();
    private final String CONFIG_FILE = "backup_config.properties";

    public DataBackupFrequency() {
        JFrame frame = new JFrame("Backup Frequency Settings");
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.WHITE);

        // === Navbar ===
        frame.add(new NavBar(frame), BorderLayout.NORTH);

        // === Main Panel ===
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(30, 10, 30, 10),
                        "Backup Frequency",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 18),
                        Color.BLACK
                )
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // === Frequency Options ===
        JLabel label = new JLabel("Select your preferred backup frequency:");
        String[] options = {"Never", "Daily", "Weekly", "Monthly"};
        JComboBox<String> frequencyBox = new JComboBox<>(options);

        // Load saved preference if available
        loadBackupPreference(frequencyBox);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(label, gbc);

        gbc.gridy = 1;
        panel.add(frequencyBox, gbc);

        // === Buttons ===
        JButton saveButton = new RoundedButton("Save");
        JButton backButton = new RoundedButton("Back to Settings");
        JButton backupNowButton = new RoundedButton("Backup Now");

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(saveButton, gbc);

        gbc.gridx = 1;
        panel.add(backButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(backupNowButton, gbc);

        // === Save Action ===
        saveButton.addActionListener((ActionEvent e) -> {
            String selected = (String) frequencyBox.getSelectedItem();
            config.setProperty("backup_frequency", selected);
            try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
                config.store(out, "Backup Frequency Settings");
                JOptionPane.showMessageDialog(frame, "Backup frequency saved: " + selected);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Failed to save backup frequency.");
            }
        });

        // === Manual Backup Now ===
        backupNowButton.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Data File to Backup");
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                performBackup(selectedFile, false);
            }
        });

        // === Back Action ===
        backButton.addActionListener(e -> {
            frame.dispose();
            new Settings();
        });

        // === Wrapper Panel for Centering ===
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

        frame.add(wrapperPanel, BorderLayout.CENTER);

        // === Initial Auto Backup Check ===
        autoBackupIfNeeded();

        // === Timer to Check for Auto Backups Every 30 Minutes ===
        new Timer(30 * 60 * 1000, e -> autoBackupIfNeeded()).start();

        frame.setVisible(true);
    }

    /**
     * Loads the saved backup frequency preference from the configuration file
     * and sets the selected item in the provided combo box.
     *
     * @param frequencyBox JComboBox to set the saved preference.
     */
    private void loadBackupPreference(JComboBox<String> frequencyBox) {
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            config.load(in);
            String saved = config.getProperty("backup_frequency", "Never");
            frequencyBox.setSelectedItem(saved);
        } catch (IOException e) {
            frequencyBox.setSelectedItem("Never");
        }
    }

    /**
     * Checks if an automatic backup is needed based on the saved frequency
     * (Daily, Weekly, Monthly) and the timestamp of the last backup.
     * If needed, it triggers the backup process and updates the last backup timestamp.
     */
    public void autoBackupIfNeeded() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        logToFile("[AutoBackup] Checked at: " + timestamp);

        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            config.load(in);
        } catch (IOException ignored) {}

        String frequency = config.getProperty("backup_frequency", "Never");
        String lastBackupStr = config.getProperty("last_backup_timestamp", "0");

        long lastBackup = Long.parseLong(lastBackupStr);
        long now = System.currentTimeMillis();
        long interval = switch (frequency) {
            case "Daily" -> 24L * 60 * 60 * 1000;
            case "Weekly" -> 7L * 24 * 60 * 60 * 1000;
            case "Monthly" -> 30L * 24 * 60 * 60 * 1000;
            default -> Long.MAX_VALUE;
        };

        if (now - lastBackup >= interval) {
            File defaultDataFile = new File("C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/CourseScheduleSpringStudentCSVCopy.csv");
            if (defaultDataFile.exists()) {
                performBackup(defaultDataFile, true);
                config.setProperty("last_backup_timestamp", String.valueOf(now));
                try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
                    config.store(out, "Updated auto-backup timestamp");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                logToFile("[AutoBackup] Backup performed at: " + timestamp);
            } else {
                logToFile("[AutoBackup] Skipped – file does not exist.");
            }
        } else {
            logToFile("[AutoBackup] No backup needed at this time.");
        }
    }

    /**
     * Performs the actual backup operation by copying the selected file
     * into a "backups" directory with a timestamped filename.
     * Optionally shows a success or failure message depending on silent mode.
     *
     * @param originalFile The file to backup.
     * @param silent If true, suppresses user notifications (used for auto-backups).
     */
    private void performBackup(File originalFile, boolean silent) {
        File backupDir = new File("backups");
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        File backupFile = new File(backupDir, originalFile.getName().replace(".csv", "") + "_backup_" + timestamp + ".csv");

        try (FileInputStream in = new FileInputStream(originalFile);
             FileOutputStream out = new FileOutputStream(backupFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            if (!silent) {
                JOptionPane.showMessageDialog(null, "Backup created: " + backupFile.getName());
            }

        } catch (IOException e) {
            if (!silent) {
                JOptionPane.showMessageDialog(null, "Backup failed: " + e.getMessage());
            } else {
                System.err.println("Auto-backup failed: " + e.getMessage());
            }
        }
    }
    
    /**
     * Logs backup-related messages (like success or errors)
     * into a "backup_log.txt" file for recordkeeping.
     *
     * @param message The message to log.
     */
    private void logToFile(String message) {
        try (FileWriter writer = new FileWriter("backup_log.txt", true)) {
            writer.write(message + "\n");
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }

}