package ui;

import com.google.gson.Gson;
import helper.Monitors;
import javafx.collections.ObservableList;
import javafx.stage.Screen;
import main.FileController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class Monitor {
    private final double width;
    private final double height;
    private final int position;
    private static final File MONITOR_SETTINGS_FILE = new File(FileController.getUserDirectory().getAbsolutePath() + File.separator + "monitor.json");

    Monitor() {
        if (!MONITOR_SETTINGS_FILE.exists()) {
            FXMLController.openMonitorSelectorDialog();
        } else {
            System.out.println("monitor settings getting...");
        }
        String fileReader = null;
        try {
            fileReader = new String(Files.readAllBytes(Paths.get(MONITOR_SETTINGS_FILE.getAbsolutePath())));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        monitorJSON monitorJSON = new Gson().fromJson(fileReader, monitorJSON.class);
        this.width = monitorJSON.getWidth();
        this.height = monitorJSON.getHeight();
        this.position = monitorJSON.getPosition();
    }

    double getWidth() {
        return width;
    }

    double getHeight() {
        return height;
    }

    private static class monitorJSON {
        double width;
        double height;
        int monitorPosition;

        double getWidth() {
            return width;
        }

        double getHeight() {
            return height;
        }

        int getPosition() {
            return monitorPosition;
        }
    }

    public double getMonitorCoordinate(String coord) {
        double coordinate = 0;
        ObservableList<Screen> screens = Monitors.getAvailableMonitors();
        switch (coord) {
            case "X":
                return screens.get(position - 1).getBounds().getMinX();
            case "Y":
                return screens.get(position - 1).getBounds().getMinY();
        }
        return coordinate;
    }

    static File getMonitorSettingsFile() {
        return MONITOR_SETTINGS_FILE;
    }
}
