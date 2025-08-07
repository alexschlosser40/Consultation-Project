package capstoneProject;

import capstoneProject.ScheduleDataHolder;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.List;
import com.lowagie.text.*;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.*;
import java.awt.Font;
import com.lowagie.text.Image;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

/* File Name: UpdatedProjectFrame
 * Purpose: Central GUI frame that integrates various project screens like schedule, settings, and data tools.
 * Date Completed: 4/16/2025
 */


//The following class handles the main schedule GUI for viewing, editing, saving, and exporting course schedules
//It is built using Java Swing and integrates with a MySQL database
public class UpdatedProjectFrame extends JFrame {
    // Class fields for main components and schedule metadata
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    JTable table;
    private DefaultTableModel tableModel;
    private String term, building, room;

 // Constructor: builds the full UI for the schedule display, loads table data, sets up buttons for saving, printing, etc.
    public UpdatedProjectFrame(Vector<Vector<String>> scheduleData, String term, String building, String room) {
    	// Initialization and layout setup
    	this.term = term;
        this.building = building;
        this.room = room;

        setTitle("Lenoir-Rhyne Schedule");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 25, 1055, 849);
        
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Color lrRed = new Color(132, 0, 20);
        Color lrGold = new Color(255, 204, 0);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 255, 255));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);
        setLocationRelativeTo(null);

        JLabel lblTitle = new JLabel(term, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(269, 10, 500, 30);
        contentPane.add(lblTitle);

        JLabel lblSubtitle = new JLabel(building, SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblSubtitle.setBounds(269, 31, 500, 30);
        contentPane.add(lblSubtitle);

        JLabel lblExtra = new JLabel("Room " + room, SwingConstants.CENTER);
        lblExtra.setFont(new Font("Arial", Font.BOLD, 18));
        lblExtra.setBounds(269, 52, 500, 30);
        contentPane.add(lblExtra);
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/logo.png"));
        JLabel lblLogo = new JLabel(logoIcon);
        lblLogo.setBounds(367, 429, 309, 274);
        contentPane.add(lblLogo);

        String[] columnNames = {"Course", "§", "Course Name", "Day", "Time", "Professor"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFont(new Font("Arial", Font.BOLD, 12));
        table.setDragEnabled(true);
        table.setDropMode(DropMode.INSERT_ROWS);
        table.setTransferHandler(new TableRowTransferHandler(table));

        for (Vector<String> rowData : scheduleData) {
            tableModel.addRow(rowData);
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(120, 105, 800, 313);
        contentPane.add(scrollPane);

        autoResizeColumns(table, scrollPane);
        
        JButton btnPrint = new JButton("Print");
        btnPrint.setBounds(200, 734, 120, 50);
        btnPrint.setBackground(Color.LIGHT_GRAY);
        btnPrint.setForeground(Color.BLACK);
        btnPrint.setFont(buttonFont);
        contentPane.add(btnPrint);
        btnPrint.addActionListener(e -> exportPanelAsPDF(contentPane));

        String[] saveOptions = {"Save as PNG", "Save as JPEG", "Save as CSV"};
        JComboBox<String> saveFormatCombo = new JComboBox<>(saveOptions);
        saveFormatCombo.setBounds(350, 704, 141, 25);
        saveFormatCombo.setFont(buttonFont);
        contentPane.add(saveFormatCombo);

        JButton btnSave = new JButton("Save");
        btnSave.setBounds(350, 734, 141, 50);
        btnSave.setBackground(new Color(0, 128, 255));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFont(buttonFont);
        contentPane.add(btnSave);
        
        btnSave.addActionListener(e -> {
        	String exportPath = FilePathConfig.getExportPath();
        	
        	if (exportPath.isEmpty()) {
        		JFileChooser folderChooser = new JFileChooser();
        		folderChooser.setDialogTitle("Select Folder to Save Schedule");
        		folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        		
        		int choice = folderChooser.showSaveDialog(null);
        		if (choice == JFileChooser.APPROVE_OPTION) {
        			File selectedDir = folderChooser.getSelectedFile();
        			exportPath = selectedDir.getAbsolutePath();
        			
        			int setDefault = JOptionPane.showConfirmDialog(
        					null,
        					"Would you like to set this folder as your default backup"
        					+ " path?", "Set as Default?", JOptionPane.YES_NO_OPTION
        			);
        			if (setDefault == JOptionPane.YES_OPTION) {
        				FilePathConfig.setExportPath(exportPath);
        			}
        		} else {
        			JOptionPane.showMessageDialog(null, "Schedule not saved. No folder selected.");
        			return;
        		}
        	}
        	
            String selectedFormat = (String) saveFormatCombo.getSelectedItem();
            String fileName = "schedule_" + System.currentTimeMillis() + "." + 
            				  (selectedFormat.equals("Save as CSV") ? "csv" :
            				   selectedFormat.equals("Save as PNG") ? "png" 
            				   : selectedFormat.equals("Save as JPEG") ? "jpg" :
            		           "csv");
            
            File outputFile = new File(exportPath, fileName);
            
            switch (selectedFormat) {
                case "Save as PNG": 
                	saveTableAsImage(table, outputFile.getAbsolutePath(), "png"); 
                	break;
                case "Save as JPEG": 
                	saveTableAsImage(table, outputFile.getAbsolutePath(), "jpg"); 
                	break;
                case "Save as CSV":
                	saveTableAsCSV(table, outputFile.getAbsolutePath());
                	FilePathConfig.setLastCSVPath(outputFile.getAbsolutePath());
                	break;
            }
            
            ScheduleDataHolder.userSchedule = getTableData();
        });
        
        JButton btnEdit = new JButton("Edit");
        btnEdit.setBounds(552, 734, 120, 50);
        btnEdit.setBackground(new Color(90, 90, 90));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFont(buttonFont);
        contentPane.add(btnEdit);
        btnEdit.addActionListener(e -> {
            new editingPage(tableModel, this);
            dispose();
        });

        JButton btnClose = new JButton("Close");
        btnClose.setBounds(697, 734, 120, 50);
        btnClose.setBackground(lrRed);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(buttonFont);
        btnClose.addActionListener(e -> {
        	new MainPage();
        	dispose();
        });
        contentPane.add(btnClose);
        
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 0, 0));
        panel.setBounds(519, 704, 5, 95);
        contentPane.add(panel);

        setVisible(true);
    }
    
    // Returns current table data as a 2D vector (used when saving/exporting or passing to the edit screen)
    public Vector<Vector<String>> getTableData() {
    	// Extracts data from tableModel
        Vector<Vector<String>> data = new Vector<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Vector<String> row = new Vector<>();
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                row.add((String) tableModel.getValueAt(i, j));
            }
            data.add(row);
        }
        return data;
    }

    // Simple getter methods for schedule properties
    public String getTerm() { return term; }
    public String getBuilding() { return building; }
    public String getRoom() { return room; }

    // Opens a file chooser and saves the JTable in a selected format (CSV, PNG, JPEG)
    public static void saveTable(JTable table, String format) {
    	// Handles saving using helper methods below
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Table");
        String ext = "." + format;
        FileNameExtensionFilter filter = new FileNameExtensionFilter
        (format.toUpperCase() + " Files (*" + ext + ")", format);
        fileChooser.setFileFilter(filter);
        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(ext)) filePath += ext;
            if (format.equals("csv")) saveTableAsCSV(table, filePath);
            else saveTableAsImage(table, filePath, format);
        }
    }
    
    // Renders the table and header as an image and saves it in the specified format (png or jpg)
    private static void saveTableAsImage(JTable table, String filePath, String format) {
    	// Paints table and header into a BufferedImage and writes to disk
        JTableHeader tableHeader = table.getTableHeader();
        int headerHeight = tableHeader.getHeight();
        int tableHeight = table.getHeight();
        int totalWidth = table.getWidth();
        int totalHeight = headerHeight + tableHeight;

        BufferedImage image = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, totalWidth, totalHeight);
        tableHeader.setSize(totalWidth, headerHeight);
        tableHeader.printAll(g2d);
        g2d.translate(0, tableHeader.getHeight());
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

    // Converts the table contents to CSV format and writes it to the selected file path
    private static void saveTableAsCSV(JTable table, String filePath) {
    	// Uses PrintWriter to save rows and headers as CSV text
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < table.getColumnCount(); i++) {
                sb.append(table.getColumnName(i));
                if (i < table.getColumnCount() - 1) sb.append(",");
            }
            sb.append("\n");

            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColumnCount(); j++) {
                    sb.append(table.getValueAt(i, j));
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
    
    // Dynamically adjusts column widths based on header and content widths
    private void autoResizeColumns(JTable table, JScrollPane scrollPane) {
    	// Loops through each column, measures width, scales to fit viewport
        final TableModel model = table.getModel();
        final TableColumnModel columnModel = table.getColumnModel();
        int totalContentWidth = 0;
        int[] columnWidths = new int[table.getColumnCount()];

        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50;
            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(
                    table, columnModel.getColumn(column).getHeaderValue(), false, false, 0, column);
            width = Math.max(width, headerComp.getPreferredSize().width + 10);

            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(cellRenderer, row, column);
                width = Math.max(width, comp.getPreferredSize().width + 10);
            }

            columnWidths[column] = width;
            totalContentWidth += width;
        }

        int availableWidth = scrollPane.getViewport().getWidth();
        if (availableWidth <= 0) availableWidth = scrollPane.getWidth();
        double scale = (double) availableWidth / totalContentWidth;

        for (int column = 0; column < columnWidths.length; column++) {
            int newWidth = (int) (columnWidths[column] * scale);
            columnModel.getColumn(column).setPreferredWidth(newWidth);
        }
    }
    
    // Exports the current schedule panel as a printable PDF using iText
    public static void exportPanelAsPDF(JPanel originalPanel) {
    	// Copies components, prints to BufferedImage, embeds image into PDF
    	JFileChooser fileChooser = new JFileChooser();
    	fileChooser.setDialogTitle("Save as PDF");
    	fileChooser.setSelectedFile(new File("schedule.pdf"));
    	
    	int result = fileChooser.showSaveDialog(null);
    	if (result != JFileChooser.APPROVE_OPTION) return;
    	
    	File fileToSave = fileChooser.getSelectedFile();
    	if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
    		fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
    	}
    	
    	try {
    		// 1. Copy content to a new printable panel
    		JPanel printablePanel = new JPanel(null);
    		printablePanel.setPreferredSize(originalPanel.getSize());
    		printablePanel.setBackground(Color.WHITE); //Make sure background is white
    	
    		for (Component comp : originalPanel.getComponents()) {
    			if (comp instanceof JButton && comp.getY() >= 600) continue;
    			if (comp instanceof JComboBox) continue;
    			
    			Component copy = copyComponent(comp);
    			if (copy != null) {
    				copy.setBounds(comp.getBounds());
    				printablePanel.add(copy);
    			}
    		}
    		
    		// 2. Add to a dummy visible frame to trigger layout
    		// 2. Add to a dummy visible frame to trigger layout
    		JFrame dummy = new JFrame();
    		dummy.setUndecorated(true);
    		dummy.getContentPane().add(printablePanel);
    		dummy.pack();
    		dummy.setVisible(true);

    		// ⬇️ NEW: Force layout + trigger column width expansion
    		for (Component comp : printablePanel.getComponents()) {
    		    if (comp instanceof JScrollPane scroll) {
    		        Component view = scroll.getViewport().getView();
    		        if (view instanceof JTable tableCopy) {
    		            tableCopy.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    		            tableCopy.doLayout(); // Apply sizing
    		        }
    		    }
    		}

    		// 3. Layout & validate panel
    		printablePanel.doLayout();
    		printablePanel.validate();

    		
    		// 3. Layout, print to image
    		printablePanel.doLayout();
    		printablePanel.validate();
    		
    		BufferedImage image = new BufferedImage(
    				printablePanel.getWidth(),
    				printablePanel.getHeight(),
    				BufferedImage.TYPE_INT_RGB
    		);
    		
    		Graphics2D g2 = image.createGraphics();
    		g2.setColor(Color.WHITE);
    		g2.fillRect(0, 0, image.getWidth(), image.getHeight());
    		printablePanel.paint(g2);
    		g2.dispose();
    		
    		dummy.dispose();
    		
    		// 4. Convert image to PDF
    		Document document = new Document();
    		PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
    		document.open();
    		
    		byte[] imageBytes = imageToBytes(image);
    		
    		Image pdfImage = Image.getInstance(imageToBytes(image));
    		
    		//Calculate scale factor based on image and page size
    		float maxWidth = PageSize.LETTER.getWidth() - 60f; //Leave some margin
    		float maxHeight = PageSize.LETTER.getHeight() - 60f;
    		
    		float scale = Math.min(
    			maxWidth / pdfImage.getWidth(),
    			maxHeight / pdfImage.getHeight()
    		);
    		
    		pdfImage.scaleAbsolute(
    			pdfImage.getWidth() * scale,
    			pdfImage.getHeight() * scale
    		);
    		pdfImage.setAlignment(com.lowagie.text.Image.ALIGN_CENTER);
    		document.add(pdfImage);
    		document.close();
    		
    		JOptionPane.showMessageDialog(null, "PDF saved to:\n" + fileToSave.
    		getAbsolutePath());
    		Desktop.getDesktop().open(fileToSave);
    	
    	} catch (Exception e) {
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(null, "Failed to export PDF: " +
    		e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    	}
    }
    
    // Converts a BufferedImage to a byte array for PDF embedding
    private static byte[] imageToBytes(BufferedImage image) throws IOException {
    	// Uses ImageIO and ByteArrayOutputStream
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	ImageIO.write(image, "png", baos);
    	return baos.toByteArray();
    }

    // Creates a copy of JLabel or JScrollPane (with JTable) for exporting visuals (PDF/image)
    private static Component copyComponent(Component original) {
    	// Duplicates style and content of visual components
    	if (original instanceof JLabel label) {
    		JLabel copy = new JLabel(label.getText(), label.getHorizontalAlignment());
    		copy.setFont(label.getFont());
    		copy.setIcon(label.getIcon());
    		copy.setForeground(label.getForeground());
    		copy.setBackground(label.getBackground());
            copy.setOpaque(label.isOpaque());
            return copy;
        } else if (original instanceof JScrollPane scroll) {
            Component view = scroll.getViewport().getView();
            if (view instanceof JTable table) {
                boolean hasData = false;
                for (int row = 0; row < table.getRowCount(); row++) {
                    for (int col = 0; col < table.getColumnCount(); col++) {
                        Object val = table.getValueAt(row, col);
                        if (val != null && !val.toString().trim().isEmpty()) {
                            hasData = true;
                            break;
                        }
                    }
                    if (hasData) break;
                }

                if (!hasData) return null;

                JTable copyTable = new JTable(table.getModel());
                copyTable.setFont(new Font("Arial", Font.PLAIN, 16));
                copyTable.setRowHeight(28);
                copyTable.setShowGrid(true);
                copyTable.setGridColor(Color.BLACK);
                copyTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                copyTable.setBackground(Color.WHITE);
                copyTable.setOpaque(true);
                copyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
                copyTable.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.BLACK));
                copyTable.getTableHeader().setBackground(Color.WHITE);
                copyTable.getTableHeader().setOpaque(true);

                // ✅ Auto-resize logic for columns based on content
                TableColumnModel colModel = copyTable.getColumnModel();
                for (int col = 0; col < copyTable.getColumnCount(); col++) {
                    int maxWidth = 50;

                    TableCellRenderer headerRenderer = copyTable.getTableHeader().getDefaultRenderer();
                    Component headerComp = headerRenderer.getTableCellRendererComponent(
                        copyTable, colModel.getColumn(col).getHeaderValue(), false, false, 0, col);
                    maxWidth = Math.max(maxWidth, headerComp.getPreferredSize().width + 10);

                    for (int row = 0; row < copyTable.getRowCount(); row++) {
                        TableCellRenderer cellRenderer = copyTable.getCellRenderer(row, col);
                        Component cellComp = copyTable.prepareRenderer(cellRenderer, row, col);
                        maxWidth = Math.max(maxWidth, cellComp.getPreferredSize().width + 10);
                    }

                    colModel.getColumn(col).setPreferredWidth(maxWidth);
                }

                // ⬇️ Add to scroll and return
                JScrollPane copyScroll = new JScrollPane(copyTable);
                copyScroll.setBounds(scroll.getBounds());
                copyScroll.setBackground(Color.WHITE);
                copyScroll.getViewport().setBackground(Color.WHITE);
                copyScroll.setOpaque(true);
                copyScroll.getViewport().setOpaque(true);
                return copyScroll;
            }

        }
        return null;
    }
    
    // Nested class representing the full editing interface for modifying the schedule
    public static class editingPage extends JFrame {
    	// Fields for editing logic, state tracking, and references
        private TableRowSorter<TableModel> sorter;
        private DefaultTableModel tableModel;
        private UpdatedProjectFrame parentFrame;
        private String term, building, room;
        private JLabel lblTerm;
        private JLabel lblBuilding;
        private JLabel lblRoom;
        private JTable table;
        
        private Vector<Vector<String>> originalDataBackup;
        private Map<Integer, Vector<String>> pendingEdits = new HashMap<>();
        private Set<Integer> rowsToDelete = new HashSet<>();
        private List<Vector<String>> insertedRows = new ArrayList<>();
        
        // Constructor: builds the editing UI, loads the table data, and prepares editing features
        public editingPage(DefaultTableModel scheduleModel, UpdatedProjectFrame parent) {
        	// Full edit screen with buttons for save, close, insert, delete, filter, etc.
            this.parentFrame = parent;
            this.term = parent.getTerm();
            this.building = parent.getBuilding();
            this.room = parent.getRoom();

            setTitle("Schedule Editing Screen");
            setSize(1000, 850);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            getContentPane().setLayout(null);
            
            Color lrRed = new Color(132, 0, 20);
            Color lrGold = new Color(255, 204, 0);
            Color darkGrey = new Color(50, 50, 50);
            Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
            Font titleFont = new Font("Segoe UI", Font.BOLD, 26);
            Font buttonFont = new Font("Segoe UI", Font.BOLD, 12);

            tableModel = new DefaultTableModel(scheduleModel.getDataVector(), getColumnNames());
            
            this.originalDataBackup = deepCopy(scheduleModel.getDataVector());
            
            table = new JTable(tableModel);
            table.setRowHeight(20);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setFont(new Font("Arial", Font.BOLD, 12));
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            table.setDragEnabled(true);
            table.setDropMode(DropMode.INSERT_ROWS);
            table.setTransferHandler(new TableRowTransferHandler(table));

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(100, 135, 800, 300);
            getContentPane().add(scrollPane);

            lblTerm = new JLabel(term, SwingConstants.CENTER);
            lblTerm.setFont(new Font("Arial", Font.BOLD, 18));
            lblTerm.setBounds(250, 10, 500, 30);
            getContentPane().add(lblTerm);
            lblTerm.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        new EditPropertiesDialog(editingPage.this);
                    }
                }
            });

            lblBuilding = new JLabel(building, SwingConstants.CENTER);
            lblBuilding.setFont(new Font("Arial", Font.BOLD, 18));
            lblBuilding.setBounds(250, 30, 500, 30);
            getContentPane().add(lblBuilding);
            lblBuilding.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        new EditPropertiesDialog(editingPage.this);
                    }
                }
            });

            lblRoom = new JLabel("Room " + room, SwingConstants.CENTER);
            lblRoom.setFont(new Font("Arial", Font.BOLD, 18));
            lblRoom.setBounds(250, 50, 500, 30);
            getContentPane().add(lblRoom);
            lblRoom.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        new EditPropertiesDialog(editingPage.this);
                    }
                }
            });

            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/logo.png"));
            JLabel lblLogo = new JLabel(logoIcon);
            lblLogo.setBounds(330, 440, 300, 300);
            getContentPane().add(lblLogo);

            sorter = new TableRowSorter<>(table.getModel());
            table.setRowSorter(sorter);

            JButton btnFilterSort = new JButton("Filter / Sort");
            btnFilterSort.setBounds(770, 460, 120, 30);
            btnFilterSort.setBackground(Color.BLACK);
            btnFilterSort.setForeground(Color.WHITE);
            btnFilterSort.setFont(buttonFont);
            btnFilterSort.addActionListener(e -> new FilterSortDialog(this, table, sorter));
            getContentPane().add(btnFilterSort);

            JButton btnResetFilter = new JButton("Reset Filters");
            btnResetFilter.setBounds(770, 500, 120, 30);
            btnResetFilter.setBackground(Color.LIGHT_GRAY);
            btnResetFilter.setFont(buttonFont);
            btnResetFilter.addActionListener(e -> {
                sorter.setRowFilter(null);
                sorter.setSortKeys(null);
            });
            getContentPane().add(btnResetFilter);
            
            JButton saveButton = new JButton("Save Edits");
            saveButton.setBounds(190, 740, 100, 25);
            saveButton.setBackground(new Color(0, 128, 255));
            saveButton.setForeground(Color.WHITE);
            saveButton.setFont(buttonFont);
            saveButton.addActionListener(e -> {
            	List<Integer> sortedDeletions = new ArrayList<>(rowsToDelete);
            	Collections.sort(sortedDeletions, Collections.reverseOrder());
            	for (int rowIndex : sortedDeletions) {
            		if (rowIndex < tableModel.getRowCount()) {
            			tableModel.removeRow(rowIndex);
            		}
            	}
            	
            	for (Map.Entry<Integer, Vector<String>> entry : pendingEdits.entrySet()) {
            		int rowIndex = entry.getKey();
            		Vector<String> newRow = entry.getValue();
            		if (rowIndex < tableModel.getRowCount()) {
            			tableModel.removeRow(rowIndex);
            			tableModel.insertRow(rowIndex, newRow);
            		}
            	}
            	
            	insertedRows.clear();
            	
                Vector<Vector<String>> updatedData = getTableData();
                UpdatedProjectFrame updatedFrame = new UpdatedProjectFrame(
                        updatedData,
                        term,
                        building,
                        room
                );
                
                pendingEdits.clear();
                rowsToDelete.clear();
                dispose();
            });
            getContentPane().add(saveButton);

            JButton insertRowButton = new JButton("Insert Row");
            insertRowButton.setBounds(315, 740, 100, 25);
            insertRowButton.setBackground(Color.LIGHT_GRAY);
            insertRowButton.setForeground(Color.BLACK);
            insertRowButton.setFont(buttonFont);
            insertRowButton.addActionListener(e -> {
                new InsertRow1(tableModel, this);
                dispose();
            });
            getContentPane().add(insertRowButton);

            JButton deleteRowButton = new JButton("Delete Row");
            deleteRowButton.setBounds(440, 740, 100, 25);
            deleteRowButton.setBackground(new Color(220, 20, 60));
            deleteRowButton.setForeground(Color.WHITE);
            deleteRowButton.setFont(buttonFont);
            deleteRowButton.addActionListener(e -> deleteSelectedRow());
            getContentPane().add(deleteRowButton);

            JButton editRowButton = new JButton("Edit Row");
            editRowButton.setBounds(560, 740, 100, 25);
            editRowButton.setBackground(new Color(75, 0, 130));
            editRowButton.setForeground(Color.WHITE);
            editRowButton.setFont(buttonFont);
            editRowButton.addActionListener(e -> editSelectedRow());
            getContentPane().add(editRowButton);

            JButton closeButton = new JButton("Close");
            closeButton.setBounds(690, 740, 100, 25);
            closeButton.setBackground(lrRed);
            closeButton.setForeground(Color.WHITE);
            closeButton.setFont(buttonFont);
            closeButton.addActionListener(e -> {
            	int confirm = JOptionPane.showConfirmDialog(this, 
            		"Unsaved edits will be discarded. Are you sure?",
            		"Discard Changes", JOptionPane.YES_NO_OPTION);
            	
            	if (confirm == JOptionPane.YES_OPTION) {
            		 new UpdatedProjectFrame(originalDataBackup, term, building, room);
                     dispose();
            	}   
            });
            getContentPane().add(closeButton);

            setVisible(true);
        }
        
        // Helper: makes a deep copy of table data to allow reverting if edits are canceled
        /**
         * Makes deep copy of table data to allow reverting if edits made
         * and copies nested vectors into new structure
         * @param original - copies nested vectors into new structure and contains
         * deep copy of table data
         * @return
         */
        private Vector<Vector<String>> deepCopy(Vector<?> original) {
        	// Copies nested vectors into new structure
        	Vector<Vector<String>> copy = new Vector<>();
        	for (Object rowObj : original) {
        		Vector<?> row = (Vector<?>) rowObj;
        		Vector<String> newRow = new Vector<>();
        		for (Object cell : row) {
        			newRow.add(cell.toString());
        		}
        		copy.add(newRow);
        	}
        	return copy;
        }
        
        // Helper: gets table model for internal use
        /**
         * gets table model for internal use
         * @return
         */
        private DefaultTableModel getTableModel() {
        	return tableModel;
        }
        
        // Helper: retrieves table data from current edits
        /**
         * retrieves table data from current edits and pulls data 
         * directly from table model
         * @return
         */
        private Vector<Vector<String>> getTableData() {
        	// Pulls data directly from the tableModel
            Vector<Vector<String>> data = new Vector<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Vector<String> row = new Vector<>();
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    row.add((String) tableModel.getValueAt(i, j));
                }
                data.add(row);
            }
            return data;
        }

        // Deletes the currently selected row (with confirmation)
        private void deleteSelectedRow() {
        	// Prompts user and removes row if confirmed
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this row?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                	rowsToDelete.add(selectedRow);
                    tableModel.removeRow(selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
            }
        }
        
        // Opens the editCourse screen to modify the selected row
        private void editSelectedRow() {
        	// Loads selected row into editable fields
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                Vector<String> rowData = new Vector<>();
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    rowData.add((String) tableModel.getValueAt(selectedRow, col));
                }
                new editCourse(this, selectedRow, rowData);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to edit", "No Row Selected", JOptionPane.WARNING_MESSAGE);
            }
        }

        // Returns the list of column headers
        private Vector<String> getColumnNames() {
        	// Returns course table structure
            Vector<String> columnNames = new Vector<>();
            columnNames.add("Course");
            columnNames.add("§");
            columnNames.add("Course Name");
            columnNames.add("Day");
            columnNames.add("Time");
            columnNames.add("Professor");
            return columnNames;
        }

        // Queues an edit to a row, replacing its data upon save
        /**
         * queues an edit to a row, replacing its data upon clicking save
         * @param rowIndex - searches row index of newRow vector to save updated
         * row for later use
         * @param newRow - edited row is queued and added to a veector of strings called newRow
         */
        public void queueEdit(int rowIndex, Vector<String> newRow) {
        	// Saves the updated row in a map for later use
            pendingEdits.put(rowIndex, newRow);
        	
        	for (int col = 0; col < newRow.size(); col++) {
                tableModel.setValueAt(newRow.get(col), rowIndex, col);
            }
        }
        
        // Queues a new row for insertion
        /**
         * queues a new row for insertion
         * @param newRow - inserted row is queued and added to a vector of strings
         * called newRow
         */
        public void queueInsert(Vector<String> newRow) {
        	// Adds the new row to a list for later insertion
        	insertedRows.add(newRow);
        }
        
        // Setters and getters for schedule metadata (term, building, room)
        public void setTerm(String newTerm) {
            this.term = newTerm;
        }

        public void setBuilding(String newBuilding) {
            this.building = newBuilding;
        }

        public void setRoom(String newRoom) {
            this.room = newRoom;
        }

        public String getTerm() {
            return term;
        }

        public String getBuilding() {
            return building;
        }

        public String getRoom() {
            return room;
        }

        // Updates the term/building/room labels visually
        public void refreshPropertyLabels() {
        	// Sets label text with updated values
            lblTerm.setText(term);
            lblBuilding.setText(building);
            lblRoom.setText("Room " + room);
        }
    }

    // ======================= EditPropertiesDialog =======================
    // Dialog used to change schedule properties like term/building/room
    public static class EditPropertiesDialog extends JFrame {
    	// Constructor: builds the GUI for selecting term, building, room
		private static final long serialVersionUID = 1L;
		private HashMap<String, String[]> dataMap = new HashMap<>();
    	private JComboBox<String> cmbRoom;
    	
        public EditPropertiesDialog(editingPage editorPage) {
        	// Loads available room/building combos and confirms update
            setTitle("Edit Properties");
            setSize(350, 250);
            setLayout(null);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            Color lrRed = new Color(132, 0, 20);
            Font labelFont = new Font("Segoe UI", Font.PLAIN, 12);
            Font buttonFont = new Font("Segoe UI", Font.BOLD, 12);
            
            dataMap.put("Minges Science", new String[]{"101", "102", "104", "105", "106", "107", "109", "111", "118", "214", "222", "300", "301", "302", "316"});
            dataMap.put("George Hall", new String[]{"122", "126", "128", "129", "132", "137", "139", "315", "319", "324", "325", "330"});
            dataMap.put("Rudisill Library", new String[]{"100", "101", "201", "210"});
            dataMap.put("Admin Building", new String[]{"100", "101", "200", "201"});

            JLabel lblTerm = new JLabel("Term:");
            lblTerm.setFont(labelFont);
            lblTerm.setBounds(30, 20, 100, 25);
            add(lblTerm);

            JComboBox<String> cmbTermSeason = new JComboBox<>(new String[]{"Spring", "Summer", "Fall"});
            cmbTermSeason.setBounds(130, 20, 90, 25);
            cmbTermSeason.setBackground(Color.LIGHT_GRAY);
            add(cmbTermSeason);

            JTextField txtYear = new JTextField();
            txtYear.setBounds(230, 20, 50, 25);
            add(txtYear);

            // Pre-fill based on editorPage.getTerm()
            String fullTerm = editorPage.getTerm(); // e.g., "Spring 2025"
            if (fullTerm != null && fullTerm.matches("(Spring|Summer|Fall) \\d{4}")) {
                String[] parts = fullTerm.split(" ");
                cmbTermSeason.setSelectedItem(parts[0]);
                txtYear.setText(parts[1]);
            }

            JLabel lblBuilding = new JLabel("Building:");
            lblBuilding.setBounds(30, 60, 100, 25);
            lblBuilding.setFont(labelFont);
            add(lblBuilding);

            JComboBox<String> cmbBuilding = new JComboBox<>(dataMap.keySet().toArray(new String[0]));
            cmbBuilding.setBounds(130, 60, 150, 25);
            cmbBuilding.setBackground(Color.LIGHT_GRAY);
            add(cmbBuilding);
            
            cmbBuilding.setSelectedItem(editorPage.getBuilding());

            JLabel lblRoom = new JLabel("Room Number:");
            lblRoom.setBounds(30, 100, 100, 25);
            lblRoom.setFont(labelFont);
            add(lblRoom);
            
            cmbRoom = new JComboBox<>();
            cmbRoom.setBounds(130, 100, 150, 25);
            cmbRoom.setBackground(Color.LIGHT_GRAY);
            add(cmbRoom);
            
            String selectedBuilding = (String) cmbBuilding.getSelectedItem();
            String[] initialRooms = dataMap.getOrDefault(selectedBuilding, new String[]{});
            for (String room : initialRooms) {
            	cmbRoom.addItem(room);
            }
            cmbRoom.setSelectedItem(editorPage.getRoom());
            
            cmbBuilding.addActionListener(e -> {
            	cmbRoom.removeAllItems();
            	String selected = (String) cmbBuilding.getSelectedItem();
            	String[] rooms = dataMap.getOrDefault(selected, new String[]{});
            	for (String room : rooms) {
            		cmbRoom.addItem(room);
            	}
            });

            JButton btnUpdate = new JButton("Edit Properties");
            btnUpdate.setBounds(40, 150, 130, 35);
            btnUpdate.setBackground(new Color(0, 128, 255));
            btnUpdate.setForeground(Color.BLACK);
            add(btnUpdate);
            
            JButton btnCancel = new JButton("Cancel");
            btnCancel.setBounds(220, 150, 80, 35);
            btnCancel.setBackground(lrRed);
            btnCancel.setFont(buttonFont);
            btnCancel.setForeground(Color.WHITE);
            add(btnCancel);
            
            btnUpdate.addActionListener(e -> {
            	String selectedSeason = (String) cmbTermSeason.getSelectedItem();
            	String yearInput = txtYear.getText().trim();

            	if (!yearInput.matches("\\d{4}")) {
            	    JOptionPane.showMessageDialog(this, "Please enter a valid 4-digit year.");
            	    return;
            	}

            	int enteredYear = Integer.parseInt(yearInput);
            	int currentYear = LocalDate.now().getYear();

            	if (enteredYear < currentYear) {
            	    JOptionPane.showMessageDialog(this, "Year must be the current year or later (≥ " + currentYear + ").");
            	    return;
            	}

            	String finalTerm = selectedSeason + " " + enteredYear;
            	editorPage.setTerm(finalTerm);
            	editorPage.setBuilding((String) cmbBuilding.getSelectedItem());
            	editorPage.setRoom((String) cmbRoom.getSelectedItem());
            	editorPage.refreshPropertyLabels();
            	dispose();
            });
            
            btnCancel.addActionListener(e -> dispose());

            setVisible(true);
        }
    }

    // ======================= editCourse =======================
    // Class that handles inline row editing via a popup form with validation
    public static class editCourse {
    	// Constructor: displays course data in editable form fields and saves edits
        private JFrame editFrame;
        private editingPage parent;
        private int rowIndex;

        public editCourse(editingPage parent, int rowIndex, Vector<String> rowData) {
        	// Validates input, enforces format, saves if valid
            this.parent = parent;
            this.rowIndex = rowIndex;

            editFrame = new JFrame();
            editFrame.setLayout(null);
            editFrame.setSize(600, 400);
            editFrame.setTitle("Course Editor");
            editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            editFrame.setLocationRelativeTo(null);
            
            Font labelFont = new Font("Segoe UI", Font.PLAIN, 12);
            Font dropdownFont = new Font("Segoe UI", Font.PLAIN, 12);
            Font buttonFont = new Font("Segoe UI", Font.BOLD, 12);
            Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
            Color lrRed = new Color(132, 0, 20);

            JLabel lblTitle = new JLabel("Editing Mode");
            lblTitle.setFont(titleFont);
            lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
            lblTitle.setBounds(60, 10, 500, 30);
            editFrame.add(lblTitle);

            JLabel lblCourse = new JLabel("* Course Code:");
            lblCourse.setBounds(110, 50, 100, 25);
            lblCourse.setFont(labelFont);
            editFrame.add(lblCourse);
            JTextField txtCourse = new JTextField(rowData.get(0));
            txtCourse.setBounds(210, 50, 200, 25);
            editFrame.add(txtCourse);

            JLabel lblSection = new JLabel("* Course Section:");
            lblSection.setBounds(100, 80, 100, 25);
            lblSection.setFont(labelFont);
            editFrame.add(lblSection);
            JComboBox<String> cmbSection = new JComboBox<>();
            cmbSection.setBounds(210, 80, 200, 25);
            cmbSection.setBackground(Color.LIGHT_GRAY);
            loadSectionsFromDatabase(cmbSection);
            cmbSection.setSelectedItem(rowData.get(1));
            editFrame.add(cmbSection);

            JLabel lblName = new JLabel("* Course Name:");
            lblName.setBounds(110, 110, 100, 25);
            lblName.setFont(labelFont);
            editFrame.add(lblName);
            JTextField txtName = new JTextField(rowData.get(2));
            txtName.setBounds(210, 110, 200, 25);
            editFrame.add(txtName);
            JLabel lblDays = new JLabel("* Instruction Day(s):");
            lblDays.setBounds(88, 140, 115, 25);
            lblDays.setFont(labelFont);
            editFrame.add(lblDays);

            JCheckBox cbM = new JCheckBox("M");
            JCheckBox cbT = new JCheckBox("T");
            JCheckBox cbW = new JCheckBox("W");
            JCheckBox cbR = new JCheckBox("R");
            JCheckBox cbF = new JCheckBox("F");
            cbM.setBounds(215, 140, 40, 25);
            cbT.setBounds(255, 140, 40, 25);
            cbW.setBounds(295, 140, 40, 25);
            cbR.setBounds(335, 140, 40, 25);
            cbF.setBounds(375, 140, 40, 25);
            editFrame.add(cbM); editFrame.add(cbT); editFrame.add(cbW); editFrame.add(cbR); editFrame.add(cbF);

            String days = rowData.get(3);
            if (days.contains("M")) cbM.setSelected(true);
            if (days.contains("T")) cbT.setSelected(true);
            if (days.contains("W")) cbW.setSelected(true);
            if (days.contains("R")) cbR.setSelected(true);
            if (days.contains("F")) cbF.setSelected(true);

            JLabel lblTimes = new JLabel("* Instruction Time(s):");
            lblTimes.setBounds(82, 170, 115, 25);
            lblTimes.setFont(labelFont);
            editFrame.add(lblTimes);

            String[] times = rowData.get(4).split(" - ");
            String[] startParts = times[0].split(" ");
            String[] endParts = times[1].split(" ");

            JTextField txtStart = new JTextField(startParts[0]);
            txtStart.setBounds(210, 170, 40, 25);
            JComboBox<String> cmbStartAMPM = new JComboBox<>(new String[]{"AM", "PM"});
            cmbStartAMPM.setBounds(255, 170, 50, 25);
            cmbStartAMPM.setSelectedItem(startParts[1]);
            cmbStartAMPM.setBackground(Color.LIGHT_GRAY);
            editFrame.add(txtStart); editFrame.add(cmbStartAMPM);

            JLabel dash = new JLabel("-");
            dash.setBounds(310, 170, 10, 25);
            editFrame.add(dash);

            JTextField txtEnd = new JTextField(endParts[0]);
            txtEnd.setBounds(320, 170, 40, 25);
            JComboBox<String> cmbEndAMPM = new JComboBox<>(new String[]{"AM", "PM"});
            cmbEndAMPM.setBounds(365, 170, 50, 25);
            cmbEndAMPM.setSelectedItem(endParts[1]);
            cmbEndAMPM.setBackground(Color.LIGHT_GRAY);
            editFrame.add(txtEnd); editFrame.add(cmbEndAMPM);

            JLabel lblProf = new JLabel("* Professor Last Name:");
            lblProf.setBounds(70, 200, 140, 25);
            lblProf.setFont(labelFont);
            editFrame.add(lblProf);
            JTextField txtProf = new JTextField(rowData.get(5));
            txtProf.setBounds(210, 200, 200, 25);
            editFrame.add(txtProf);

            JButton save = new JButton("Save Edits");
            save.setBounds(320, 250, 100, 25);
            save.setBackground(new Color(0, 128, 255));
            save.setFont(buttonFont);
            save.setForeground(Color.BLACK);
            save.addActionListener(e -> {
            	String courseCode = txtCourse.getText().trim();
            	String section = cmbSection.getSelectedItem().toString().trim();
            	String courseName = txtName.getText().trim();
            	String profLastName = txtProf.getText().trim();
            	String start = txtStart.getText().trim();
            	String end = txtEnd.getText().trim();
                String startAMPM = cmbStartAMPM.getSelectedItem().toString();
                String endAMPM = cmbEndAMPM.getSelectedItem().toString();
            	
                StringBuilder updatedDays = new StringBuilder();
                if (cbM.isSelected()) updatedDays.append("M");
                if (cbT.isSelected()) updatedDays.append("T");
                if (cbW.isSelected()) updatedDays.append("W");
                if (cbR.isSelected()) updatedDays.append("R");
                if (cbF.isSelected()) updatedDays.append("F");
                
                boolean startEmpty = start.isEmpty();
            	boolean endEmpty = end.isEmpty();
        		
        		if (courseCode.isEmpty() || courseName.isEmpty() || profLastName.isEmpty()) {
        			JOptionPane.showMessageDialog(editFrame, "Please fill in all required fields");
        			return;
        		}
        		
        		if (!courseCode.matches("[A-Z]{3} \\d{3}")) {
        			JOptionPane.showMessageDialog(editFrame, "Course code must be in format: AAA 123");
        			return;
        		}
        		
        		if (!courseName.matches("[a-zA-Z\\s\\-:',]+")) {
        			JOptionPane.showMessageDialog(editFrame, "Course name cannot contain numbers");
        			return;
        		}
        		
        		if (!profLastName.matches("[a-zA-Z\\s]+")) {
        			JOptionPane.showMessageDialog(editFrame, "Professor last name cannot contain numbers");
        			return;
        		}
        		
        		if ((startEmpty && !endEmpty) || (!startEmpty && endEmpty)) {
        			JOptionPane.showMessageDialog(editFrame, "Please enter both start and end times");
        			return;
        		}
        		
        		if (startEmpty && endEmpty && updatedDays.length() == 0) {
        			start = end = "12:00";
        			startAMPM = endAMPM = "AM";
        			updatedDays.append("ONLINE");
        		} else {
        			if (!start.matches("\\d{1,2}:\\d{2}") || !end.matches("\\d{1,2}:\\d{2}")) {
        				JOptionPane.showMessageDialog(editFrame, "Time format must be HH:MM");
        				return;
        			}
           			
        			try {
        				String fullStart = start + " " + startAMPM;
        				String fullEnd = end + " " + endAMPM;
        				
        				SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        				
        				Date startTime = sdf.parse(fullStart);
        				Date endTime = sdf.parse(fullEnd);
        				
        				long diff = endTime.getTime() - startTime.getTime();
        				if (diff < 30 * 60 * 1000) {
        					JOptionPane.showMessageDialog(editFrame, "Start and end times must be at least 30 minutes apart.");
        					return;
        				}
        			} catch (Exception ex) {
        				JOptionPane.showMessageDialog(editFrame, "Invalid time input.");
        				ErrorLogger.log("Time parsing error: " + ex.getMessage());
        				ex.printStackTrace();
        				return;
        			}
        		}

                Vector<String> newRow = new Vector<>();
                newRow.add(courseCode);
                newRow.add(section);
                newRow.add(courseName);
                newRow.add(updatedDays.toString());
                newRow.add(start + " " + startAMPM + " - " + end + " " + endAMPM);
                newRow.add(profLastName);
                
                boolean changed = !newRow.equals(parent.getTableModel().getDataVector().get(rowIndex));
                if (!changed) {
                	JOptionPane.showMessageDialog(editFrame, "No changes detected. Modify a field or press Cancel");
                	return;
                }
                
                for (int i = 0; i < parent.getTableModel().getRowCount(); i++) {
                	if (i == rowIndex) continue;
                	
                	boolean isDuplicate = true;
                	for (int j = 0; j < newRow.size(); j++) {
                		String existingValue = String.valueOf(parent.getTableModel().getValueAt(i, j)).trim();
                		String newValue = newRow.get(j).trim();
                		if (!existingValue.equalsIgnoreCase(newValue)) {
                			isDuplicate = false;
                			break;
                		}
                	}
                	
                	if (isDuplicate) {
                		JOptionPane.showMessageDialog(editFrame, "Duplicate entry. This course already exists in the table.");
                		return;
                	}
                }

                parent.queueEdit(rowIndex, newRow);
                editFrame.dispose();
        	});
            editFrame.add(save);

            JButton cancel = new JButton("Cancel");
            cancel.setBounds(200, 250, 100, 25);
            cancel.setBackground(lrRed);
            cancel.setForeground(Color.WHITE);
            cancel.setFont(buttonFont);
            cancel.addActionListener(e -> editFrame.dispose());
            editFrame.add(cancel);

            editFrame.setVisible(true);
        }
    }
    
    // Loads section numbers from a MySQL table for the section dropdown in editCourse
    private static void loadSectionsFromDatabase(JComboBox<String> comboBox) {
    	// Connects to database, runs query, fills combo box
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
            JOptionPane.showMessageDialog(null, "Failed to load sections from database.");
            ErrorLogger.log("Section load erro/r: " + e.getMessage());
        }
    }

    // ======================= Main Method =======================
    // Main method: starts the application by opening a blank UpdatedProjectFrame
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	// Launch GUI with empty schedule
            Vector<Vector<String>> scheduleData = new Vector<>();
            String defaultTerm = "";
            String defaultBuilding = "";
            String defaultRoom = "";
            new UpdatedProjectFrame(scheduleData, defaultTerm, defaultBuilding, defaultRoom);
        });
    }

    // ======================= TableRowTransferHandler =======================
    // Enables drag-and-drop reordering of rows in JTable
    static class TableRowTransferHandler extends TransferHandler {
    	// Constructor assigns table reference
        private final JTable table;

        public TableRowTransferHandler(JTable table) {
            this.table = table;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return new StringSelection("");
        }

        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        public boolean canImport(TransferSupport support) {
            return support.isDrop() && support.getComponent() instanceof JTable;
        }

        @Override
        public boolean importData(TransferSupport support) {
        	// Handles moving selected rows to new drop location
        	if (!canImport(support)) return false;
        	
            JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();
            int dropRow = dl.getRow();
            if (dropRow < 0) return false;
            
            JTable table = (JTable) support.getComponent();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            int[] rows = table.getSelectedRows();
            
            List<Vector<?>> rowsToMove = new ArrayList<>();
            for (int row : rows) {
                Vector<?> rowData = (Vector<?>) model.getDataVector().get(row);
                rowsToMove.add(new Vector<>(rowData));
            }
            for (int i = rows.length - 1; i >= 0; i--) {
                model.removeRow(rows[i]);
                if (rows[i] < dropRow) {
                	dropRow--;
                }
            }
            
            dropRow = Math.min(dropRow, model.getRowCount());
            
            for (Vector<?> rowData : rowsToMove) {
            	model.insertRow(dropRow++, rowData);
            }
            
            return true;
        }
    }

    // ======================= FilterSortDialog =======================
    // Modal window for filtering and sorting the table data
    static class FilterSortDialog extends JDialog {
        public FilterSortDialog(JFrame parent, JTable table, TableRowSorter<TableModel> sorter) {
        	// Adds fields and combo boxes to filter by course, professor, day; and sort by column/order
            super(parent, "Filter and Sort", true);
            setSize(400, 350);
            setLayout(null);
            setLocationRelativeTo(parent);
            
            Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
            Font dropdownFont = new Font("Segoe UI", Font.PLAIN, 12);
            Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

            JLabel lblCourse = new JLabel("Filter by Course:");
            lblCourse.setBounds(30, 20, 150, 25);
            lblCourse.setFont(labelFont);
            add(lblCourse);

            JTextField txtCourse = new JTextField();
            txtCourse.setBounds(170, 20, 180, 25);
            add(txtCourse);

            JLabel lblProfessor = new JLabel("Filter by Professor:");
            lblProfessor.setBounds(30, 60, 150, 25);
            lblProfessor.setFont(labelFont);
            add(lblProfessor);

            JTextField txtProfessor = new JTextField();
            txtProfessor.setBounds(170, 60, 180, 25);
            add(txtProfessor);

            JLabel lblDay = new JLabel("Filter by Day:");
            lblDay.setBounds(30, 100, 150, 25);
            lblDay.setFont(labelFont);
            add(lblDay);

            JComboBox<String> cmbDay = new JComboBox<>(new String[]{"All", "M", "T", "W", "R", "F"});
            cmbDay.setBounds(170, 100, 180, 25);
            cmbDay.setFont(dropdownFont);
            cmbDay.setBackground(Color.LIGHT_GRAY);
            cmbDay.setForeground(Color.BLACK);
            add(cmbDay);

            JLabel lblSortBy = new JLabel("Sort By:");
            lblSortBy.setBounds(30, 140, 150, 25);
            lblSortBy.setFont(labelFont);
            add(lblSortBy);

            JComboBox<String> cmbSortColumn = new JComboBox<>(new String[]{"Course", "Day", "Time", "Professor"});
            cmbSortColumn.setBounds(170, 140, 180, 25);
            cmbSortColumn.setBackground(Color.LIGHT_GRAY);
            cmbSortColumn.setForeground(Color.BLACK);
            cmbSortColumn.setFont(dropdownFont);
            add(cmbSortColumn);

            JLabel lblOrder = new JLabel("Order:");
            lblOrder.setBounds(30, 180, 150, 25);
            lblOrder.setFont(labelFont);
            add(lblOrder);

            JComboBox<String> cmbOrder = new JComboBox<>(new String[]{"Ascending", "Descending"});
            cmbOrder.setBounds(170, 180, 180, 25);
            cmbOrder.setBackground(Color.LIGHT_GRAY);
            cmbOrder.setForeground(Color.BLACK);
            cmbOrder.setFont(dropdownFont);
            add(cmbOrder);

            JButton btnApply = new JButton("Apply");
            btnApply.setBounds(150, 240, 100, 30);
            btnApply.setBackground(new Color(0, 128, 255));
            btnApply.setFont(buttonFont);
            add(btnApply);

            btnApply.addActionListener(e -> {
                String course = txtCourse.getText().trim().toLowerCase();
                String professor = txtProfessor.getText().trim().toLowerCase();
                String day = (String) cmbDay.getSelectedItem();

                sorter.setRowFilter(new RowFilter<Object, Object>() {
                    public boolean include(RowFilter.Entry<?, ?> entry) {
                        String courseVal = entry.getStringValue(0).toLowerCase();
                        String dayVal = entry.getStringValue(3);
                        String profVal = entry.getStringValue(5).toLowerCase();
                        return (course.isEmpty() || courseVal.contains(course)) &&
                               (professor.isEmpty() || profVal.contains(professor)) &&
                               (day.equals("All") || dayVal.contains(day));
                    }
                });

                int sortColumn = cmbSortColumn.getSelectedIndex();
                SortOrder order = cmbOrder.getSelectedItem().equals("Ascending") ? SortOrder.ASCENDING : SortOrder.DESCENDING;
                sorter.setSortKeys(List.of(new RowSorter.SortKey(sortColumn, order)));
                dispose();
            });

            setVisible(true);
        }
    }
    
    }