package capstoneProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

/* File Name: FilePathConfig
 * Purpose: Stores constants or settings related to default file paths used for file I/O operations in the application.
 * Date Completed: 4/16/2025
 */


public class FilePathConfig {
    private static final String CONFIG_FILE = "default_paths.properties";
    private static Properties config = new Properties();

    static {
        load();
    }

    private static void load() {
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            config.load(in);
        } catch (IOException e) {
            ErrorLogger.log("Config file missing or unreadable: " + e.getMessage());
        }
    }

    public static String getImportPath() {
        return config.getProperty("import_path", "");
    }

    public static String getExportPath() {
        return config.getProperty("export_path", "");
    }

    public static boolean isExportPathSet() {
        return !getExportPath().isEmpty();
    }

    public static boolean isImportPathSet() {
        return !getImportPath().isEmpty();
    }
    
    public static void setExportPath(String path) {
    	try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
    		config.setProperty("export_path", path);
    		config.store(out, "Updated Export Path");
    	} catch (IOException e) {
    		ErrorLogger.log("Failed to set export path: " + e.getMessage());
    	}
    }
    
    public static String getLastCSVPath() {
    	return config.getProperty("last_csv", "");
    }
    
    public static void setLastCSVPath(String path) {
    	try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
    		config.setProperty("last_csv", path);
    		config.store(out, "Updated Last CSV Path");
    	} catch (IOException e) {
    		ErrorLogger.log("Failed to save last CSV path: " + e.getMessage());
    	}
    }
}