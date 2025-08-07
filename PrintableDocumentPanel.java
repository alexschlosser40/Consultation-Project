package capstoneProject;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/* File Name: PrintableDocumentPanel
 * Purpose: Creates a printable JPanel that visually represents the course schedule with headers, logo, and formatting.
 * Date Completed: 4/16/2025
 */


public class PrintableDocumentPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTable table;
    private String term, building, room;
    private Image logoImage;

    private final Font headerFont = new Font("Aptos", Font.PLAIN, 14);
    private final Font contentFont = new Font("Aptos", Font.PLAIN, 14);

    public PrintableDocumentPanel(JTable originalTable, String term, String building, String room, Image logoImage) {
        this.term = term;
        this.building = building;
        this.room = room;
        this.logoImage = logoImage;

        this.table = new JTable(originalTable.getModel());
        this.table.setFont(contentFont);
        this.table.setRowHeight(25);
        this.table.getTableHeader().setFont(headerFont);
        this.setPreferredSize(new Dimension(800, 1100));
        this.setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Draw watermark logo (centered on panel, not based on table)
        if (logoImage != null) {
            int logoWidth = logoImage.getWidth(null);
            int logoHeight = logoImage.getHeight(null);
            int centerX = (getWidth() - logoWidth) / 2;
            int centerY = (getHeight() - logoHeight) / 2;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.30f));
            g2d.drawImage(logoImage, centerX, centerY, this);
            g2d.setComposite(AlphaComposite.SrcOver);
        }

        // Set fonts and metrics
        g2d.setFont(headerFont);
        FontMetrics headerFm = g2d.getFontMetrics(headerFont);
        FontMetrics contentFm = g2d.getFontMetrics(contentFont);

        // Centered Header Text
        int titleY = 50;
        String[] titles = {term, building, "Room " + room};
        for (String title : titles) {
            int titleWidth = headerFm.stringWidth(title);
            int centerX = (getWidth() - titleWidth) / 2;
            g2d.drawString(title, centerX, titleY);
            titleY += 20;
        }

        // Draw table manually
        int startY = 130;
        int startX = 80;
        int rowHeight = 25;
        int headerHeight = 35;
        int[] colWidths = new int[]{90, 40, 160, 60, 160, 120};

        // Dynamically adjust Course Name column width based on longest name
        int maxNameWidth = contentFm.stringWidth("Course Name"); // Start with header width
        for (int row = 0; row < table.getRowCount(); row++) {
            String name = String.valueOf(table.getValueAt(row, 2)); // Column index 2 = Course Name
            int width = contentFm.stringWidth(name);
            if (width > maxNameWidth) {
                maxNameWidth = width;
            }
        }
        colWidths[2] = Math.max(160, maxNameWidth + 20); // Give padding

        TableModel model = table.getModel();
        String[] headers = {"Course", "§", "Course Name", "Day", "Time", "Professor"};

        // Draw header row
        g2d.setFont(headerFont);
        int x = startX;
        for (int i = 0; i < headers.length; i++) {
            int w = colWidths[i];
            g2d.drawRect(x, startY, w, headerHeight);
            String text = headers[i];
            int textWidth = headerFm.stringWidth(text);
            int textHeight = headerFm.getHeight();
            int textX = x + (w - textWidth) / 2;
            int textY = startY + (headerHeight + textHeight) / 2 - 6;
            g2d.drawString(text, textX, textY);
            x += w;
        }

        // Draw content rows
        g2d.setFont(contentFont);
        int y = startY + headerHeight;
        for (int row = 0; row < model.getRowCount(); row++) {
            x = startX;
            for (int col = 0; col < model.getColumnCount(); col++) {
                int w = colWidths[col];
                String text = String.valueOf(model.getValueAt(row, col));
                int textWidth = contentFm.stringWidth(text);
                int textX = (col == 1 || col == 3) ? x + (w - textWidth) / 2 : x + 5; // Center for Section and Day
                int textY = y + rowHeight - 5; // Bottom align

                g2d.drawRect(x, y, w, rowHeight);
                g2d.drawString(text, textX, textY);
                x += w;
            }
            y += rowHeight;
        }

        g2d.dispose();
    }
}