package main;

public class Main {
    public static void main(String[] args) {
        setScreen(args);
    }

    private static void setScreen(String[] args) {
        ui.Main scene = new ui.Main();
        scene.main(args);
    }
}
