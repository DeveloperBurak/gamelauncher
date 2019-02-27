package main;

public class Main {
    public static void main(String[] args) {
        setScreen(args);
    }

    public static void setScreen(String[] args) {
        ui.Main scene = new ui.Main();
        new Thread(() -> {
            try {
                WindowsActivities.checkOpen();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }).start();
        new Thread(() -> {
            scene.main(args);
        }).start();

    }
}
