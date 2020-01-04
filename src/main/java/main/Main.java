package main;

public class Main {
    public static void main(String[] args) {
        if (!FileController.checkExistsFolders()) {
            System.out.println("Folders are writing...");
            FileController.createPredefinedFolders();
        }
        setScreen(args);
    }

    private static void setScreen(String[] args) {
        ui.Main.main(args);
    }
}
