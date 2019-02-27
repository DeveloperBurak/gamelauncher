package ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ExpandedController implements Initializable {
    @FXML
    private Label labelGameList;
    @FXML
    private AnchorPane expandedScene;
    @FXML
    private VBox gamesList;
    private Main main = new Main();
    private static boolean isClicked = false;
    static String temp = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        double width = main.returnScreenWidth();
        double height = main.returnScreenHeight();
        expandedScene.setPrefWidth(width / 10);
        expandedScene.setPrefHeight(height);
        File folder = new File("C:/Users/Developer/Documents/Game Launcher");
        String temp = "";
        ArrayList<String> games = listFilesForFolder(folder);
        generateButton(games.size(),games);
    }

    public boolean setIsClicked() {
        if (isClicked) {
            isClicked = false;
        } else {
            isClicked = true;
        }
        return isClicked;
    }

    public void collapseScreen() {
        if (!isClicked) {
            try {
                main.changeSceneWithButton("fxml/scene.fxml");
            } catch (Exception e) {
                System.out.println("asas " + e.getMessage() + " | " + e.getClass().getCanonicalName());
            }
        }
    }

    public void generateButton(int num,ArrayList<String> games) {
        for (int i = 0; i < num; i++) {
            Button b = new Button();
            b.setText(games.get(i));
            gamesList.getChildren().add(b);
        }
    }



    private static ArrayList<String> listFilesForFolder(final File folder) {
        ArrayList<String> games = new ArrayList<>();

        ArrayList<String> gamesShortcuts = new ArrayList<>();
        File folderShortcut = new File(folder.getPath()+"/shortcuts");
        for (final File fileEntry : folderShortcut.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("Reading files under the folder "+folderShortcut.getAbsolutePath());
                listFilesForFolder(fileEntry);
            } else {
                if (fileEntry.isFile()) {
                    temp = fileEntry.getName();
                    String tempName = stripExtension(temp);
                    String tempExtension = temp.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase();
                    if ((tempExtension).equals("lnk")){
                        gamesShortcuts.add(tempName);
                    }
                }

            }
        }

        System.out.println(folder.getAbsoluteFile());
        File folderImg = new File(folder.getPath()+"/images");
        ArrayList<String> gamesImages = new ArrayList<>();

        for (final File fileEntry : folderImg.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("Reading files under the folder "+folderImg.getAbsolutePath());
                listFilesForFolder(fileEntry);
            } else {
                if (fileEntry.isFile()) {
                    temp = fileEntry.getName();
                    String tempName = stripExtension(temp);
                    String tempExtension = temp.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase();
                    if ((tempExtension).equals("jpg")){
                        gamesImages.add(tempName);
                    }
                }
            }
        }

        // your code
        for (String game: gamesShortcuts) {
            if(gamesImages.contains(game)){
                System.out.println(game);
                games.add(game);
            }
        }
        return games;
    }
    private static String stripExtension (String str) {
        // Handle null case specially.

        if (str == null) return null;

        // Get position of last '.'.

        int pos = str.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.

        if (pos == -1) return str;

        // Otherwise return the string, up to the dot.

        return str.substring(0, pos);
    }
}