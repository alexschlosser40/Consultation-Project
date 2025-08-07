package capstoneProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/* File Name: RoundedButton
 * Purpose: Custom JButton class that renders buttons with rounded corners and custom background styling.
 * Date Completed: 4/16/2025
 */


public class RoundedButton extends JButton {
	private Color originalColor = Color.BLACK; // Default button color
    private Color hoverColor = Color.WHITE;   // Hover color

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false); // Remove default button background
        setFocusPainted(false); // Remove focus border
        setBorderPainted(false); // Remove default border
        setOpaque(false); // Transparent background
        setForeground(Color.WHITE); // Default text color
        setFont(new Font("Arial", Font.BOLD, 14));

        // Add hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                originalColor = Color.BLACK; // Save original background
                setForeground(Color.BLACK);  // Change text to black
                repaint(); // Redraw button
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setForeground(Color.WHITE);  // Restore original text color
                repaint(); // Redraw button
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set hover effect
        g2.setColor(getModel().isRollover() ? hoverColor : originalColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Adjust corner radius

        // Draw text on top of button
        FontMetrics fm = g2.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = (getHeight() + fm.getAscent()) / 2 - 2;
        g2.setColor(getForeground()); // Use updated text color
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }

}