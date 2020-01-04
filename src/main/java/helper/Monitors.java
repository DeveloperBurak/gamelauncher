package helper;

import javafx.collections.ObservableList;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Monitors {

    public static ObservableList<Screen> getAvailableMonitors() {
        try {
            return Screen.getScreens();
        } catch (ExceptionInInitializerError ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public static List<Monitor> getMonitorsWithSize() {
        List<Monitor> monitorsInfo = new ArrayList<>();
        ObservableList<Screen> monitors = getAvailableMonitors();
        System.out.println(monitors);
        if (monitors != null) {
            for (Screen monitor : monitors) {
                double widthSize = monitor.getBounds().getWidth();
                double heightSize = monitor.getBounds().getHeight();
                Map<String, Integer> width = new HashMap<String, Integer>();
                Map<String, Integer> height = new HashMap<String, Integer>();
                Monitor monitorSizes = new Monitor(widthSize, heightSize);
                monitorsInfo.add(monitorSizes);
            }
        }
        return monitorsInfo;
    }

    public static class Monitor {
        //        private Map<String,Integer> width,height;
        private double width;
        private double height;
        private int monitorPosition;

        public Monitor(int monitorPosition, double width, double height) {
            this.width = width;
            this.height = height;
            this.monitorPosition = monitorPosition;
        }

        public Monitor(double width, double height) {
            this.width = width;
            this.height = height;
            this.monitorPosition = 1;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }
    }


}
