package helper;

import javafx.collections.ObservableList;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Monitors {

    public static ObservableList<Screen> getAvailableMonitors(){
        return Screen.getScreens();
    }

//    public static List<Map<Map<String,Integer>, Map<String,Integer>>> getMonitorsWithSize(){
//        List<Map<Map<String,Integer>, Map<String,Integer>>> monitorsInfo = new ArrayList<>();
//        Integer count = 1;
//        for (Screen monitor : getAvailableMonitors()) {
//            Map<Map, Map> monitorSpecifics = new HashMap<>();
//            Map<Map<String,Integer>, Map<String,Integer>>  sizes = new HashMap<Map<String,Integer>, Map<String,Integer>>();
//            Map<String,Integer> width = new HashMap<String,Integer>();
//            Map<String,Integer> height = new HashMap<String,Integer>();
//            int widthSize = (int) monitor.getBounds().getWidth();
//            int heightSize = (int) monitor.getBounds().getHeight();
//            width.put("width",widthSize);
//            height.put("height",heightSize);
//
//            sizes.put(width, height);
//            monitorsInfo.add(sizes);
//            count++;
//        }
//        System.out.println(monitorsInfo);
////        return  monitorsInfo;
//        return monitorsInfo;
//    }
public static List<Monitor> getMonitorsWithSize(){
        List<Monitor> monitorsInfo = new ArrayList<>();
        for (Screen monitor : getAvailableMonitors()) {
            double widthSize = monitor.getBounds().getWidth();
            double heightSize = monitor.getBounds().getHeight();
            Map<String,Integer> width = new HashMap<String, Integer>();
//            width.put("width",widthSize);
            Map<String,Integer> height = new HashMap<String, Integer>();
//            height.put("height",heightSize);
            Monitor monitorSizes = new Monitor(widthSize,heightSize);
            monitorsInfo.add(monitorSizes);
        }
        return monitorsInfo;
    }
    public static class Monitor{
//        private Map<String,Integer> width,height;
        private double width;
        private double height;
        private int monitorPosition;
        public Monitor(int monitorPosition,double width,double height){
            this.width = width;
            this.height = height;
            this.monitorPosition = monitorPosition;
        }

        public Monitor(double width,double height){
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
