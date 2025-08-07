package capstoneProject;

import javax.swing.*;

/* File Name: SystemReset
 * Purpose: Performs a reset operation to clear or reinitialize application data to a default state.
 * Date Completed: 4/16/2025
 */


public class SystemReset {
		public static void resetApplication(JFrame frame) {
	        int response = JOptionPane.showConfirmDialog(
	                frame,
	                "Are you sure you want to reset the system? This will restore default settings.",
	                "Confirm System Reset",
	                JOptionPane.YES_NO_OPTION,
	                JOptionPane.WARNING_MESSAGE
	        );

	        if (response == JOptionPane.YES_OPTION) {
	            // Perform reset actions
	            restoreDefaults();
	            JOptionPane.showMessageDialog(frame, "System has been reset to default settings.", "Reset Successful", JOptionPane.INFORMATION_MESSAGE);
	            
	            // Optionally, restart the application
	            restartApplication(frame);
	        }
	    }

	    private static void restoreDefaults() {
	        // Logic to reset settings (e.g., clearing config files, resetting variables)
	        System.out.println("Restoring default settings...");
	        // You can implement actual reset logic here
	    }

	    private static void restartApplication(JFrame frame) {
	        frame.dispose(); // Close current frame

	        SwingUtilities.invokeLater(() -> {
	            new Settings(); // Reopen the Settings window (assuming it's the main UI)
	        });
	    }
	}
