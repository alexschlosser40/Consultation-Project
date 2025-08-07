package capstoneProject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/* File Name: ErrorLogger
 * Purpose: Implements the backend utility that writes error messages or logs to an external file for debugging.
 * Date Completed: 4/16/2025
 */


public class ErrorLogger {
	private static final String LOG_FILE = "error_log.txt";

    public static void log(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.write("[" + timestamp + "] " + message);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to log error: " + e.getMessage());
        }
    }

    public static String readLog() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            content.append("No logs found or failed to read log.");
        }
        return content.toString();
    }

    public static void clearLog() {
        try (PrintWriter writer = new PrintWriter(LOG_FILE)) {
            writer.print("");
        } catch (IOException e) {
            System.err.println("Failed to clear log: " + e.getMessage());
        }
    }
}