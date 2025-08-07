package capstoneProject;

import javax.swing.*;

/* File Name: CloseButton
 * Purpose: Implements a reusable close button for disposing of windows and returning to the main page.
 * Date Completed: 4/16/2025
 */

public class CloseButton extends RoundedButton {
    public CloseButton(JFrame currentFrame, Runnable mainPageOpener) {
        super("Close");

        addActionListener(e -> {
            currentFrame.dispose(); // Close the current window
            mainPageOpener.run();   // Run the provided redirect logic
        });
    }
}