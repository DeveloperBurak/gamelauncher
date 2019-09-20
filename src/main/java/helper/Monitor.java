package helper;

import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Monitor {
    public final Rectangle2D screenSize = getMonitorSizes();

    public double getScreenWidth() {
        return screenSize.getWidth();
    }

    public double getScreenHeight() {
        return screenSize.getHeight();
    }

    public ObservableList<Screen> getAvailableMonitors(){
        return Screen.getScreens();
    }

    public Rectangle2D getMonitorSizes() {
        return getAvailableMonitors().get(getAvailableMonitors().size() - 1).getBounds();
    }
}
