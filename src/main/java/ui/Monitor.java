package ui;

import com.google.gson.Gson;
import main.FileController;
import main.Steam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class Monitor {
    private final double width;
    private final double height;
    private static final File MONITOR_SETTINGS_FILE = new File(FileController.getUserDirectory().getAbsolutePath() + File.separator + "monitor.json");

    Monitor() throws IOException {
        if (!MONITOR_SETTINGS_FILE.exists()) {
            FXMLController.openMonitorSelectorDialog();
        } else {
            System.out.println("monitor settings getting...");
        }
        String fileReader = new String(Files.readAllBytes(Paths.get(MONITOR_SETTINGS_FILE.getAbsolutePath())));
        monitorJSON monitorJSON = new Gson().fromJson(fileReader,monitorJSON.class);
        this.width = monitorJSON.getWidth();
        this.height = monitorJSON.getHeight();
    }
    double getWidth(){
        return width;
    }
    double getHeight(){
        return height;
    }

    private static class monitorJSON{
        double width;
        double height;

        double getWidth() {
            return width;
        }

        double getHeight() {
            return height;
        }
    }
    static File getMonitorSettingsFile() {
        return MONITOR_SETTINGS_FILE;
    }
}
