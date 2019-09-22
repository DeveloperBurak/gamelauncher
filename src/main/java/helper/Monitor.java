package helper;

import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class Monitor {
    public static double getScreenWidth() {
        return getMonitorSizes().getWidth();
    }

    public static double getScreenHeight() {
        return getMonitorSizes().getHeight();
    }

    public static ObservableList<Screen> getAvailableMonitors(){
        return Screen.getScreens();
    }

    public static Rectangle2D getMonitorSizes() {
        return getAvailableMonitors().get(getAvailableMonitors().size() - 1).getBounds();
    }
}
