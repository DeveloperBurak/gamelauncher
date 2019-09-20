package main;

import helper.Monitors;

public class Main {
    public static Steam steam = new Steam();

    public static void main(String[] args) {
        if(!FileController.checkExistsFolders()){
            System.out.println("Folders are writing...");
            FileController.writePredefinedFolders();
        }
        setScreen(args);
    }

    private static void setScreen(String[] args) {
        ui.Main.main(args);
    }
}
