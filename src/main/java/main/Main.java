package main;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        setScreen(args);
    }

    private static void setScreen(String[] args) {
        if(!FileController.checkExistsFolders()){
            System.out.println("Folders are writing...");
            FileController.writeFiles();
        }
        ui.Main.main(args);
    }
}
