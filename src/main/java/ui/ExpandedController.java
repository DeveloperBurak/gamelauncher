package ui;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ExpandedController implements Initializable {
    @FXML
    private Label labelGameList;
    @FXML
    private StackPane expandedScene;
    @FXML
    private VBox gamesList;
    @FXML
    private HBox gamesListContainer;
    private Main main = new Main();
    private static boolean isClicked = false;
    static String temp = "";
    private static ArrayList<Game> gamesWithImage = new ArrayList<>();
    private final double width = main.returnScreenWidth();
    private final double height = main.returnScreenHeight();
    private final double listWidth = width/10;
    private final double listHeight = height;
    private static final File folder = new File("C:/Users/Developer/Documents/Game Launcher");
    private static final File folderImage = new File(folder.getPath()+"/images/");
    private static final File folderShortcut = new File(folder.getPath() + "/shortcuts/");
    private static ImageView gameImageViewer = new ImageView();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        expandedScene.setMinWidth(listWidth);
        expandedScene.setMaxWidth(width);
        expandedScene.setPrefWidth(width);
        expandedScene.setPrefHeight(height);
        gamesListContainer.setPrefHeight(height);
        gamesListContainer.setPrefWidth(width);
        gamesList.setPrefHeight(listHeight);
        gamesList.setPrefWidth(listWidth);
        gamesList.setPadding(new Insets(labelGameList.getHeight()+20,15,0,10));
        gamesList.setStyle("-fx-background-color: linear-gradient(to right, rgba(100,100,100,0.80) 0%, rgba(150,150,150,0.10) 100%)");
        gameImageViewer.addEventHandler(MouseEvent.MOUSE_ENTERED,new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                collapseScreen();
            }
        });
        listFilesForFolder(folder);
        for(Game game : gamesWithImage){
            generateButton(game);
        }
        // Set up a Translate Transition for the Text object
        TranslateTransition trans = new TranslateTransition(Duration.seconds(1), gamesList);
        trans.setFromX(-150);
        trans.setToX(0);
        // Let the animation run forever
        trans.setCycleCount(1);
        // Reverse direction on alternating cycles
        trans.setAutoReverse(true);
        // Play the Animation
        trans.play();
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
                gamesWithImage.clear();
                main.changeSceneWithButton("fxml/scene.fxml");
            } catch (Exception e) {
                System.out.println("Collapsed screen couldnt load: " + e.getMessage() + " | " + e.getClass().getCanonicalName());
            }
        }
    }

    private void generateButton(Game game) {
            Button b = new Button();
            b.setText(game.gameText);
            b.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0);");
            b.addEventHandler(MouseEvent.MOUSE_ENTERED,new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    gameImageViewer.setX(listWidth+5);
                    Image picture = new Image(game.gameImage.toURI().toString(),gamesListContainer.getWidth()-gamesList.getWidth(),gamesListContainer.getHeight(),false,false);
                    gameImageViewer.setImage(picture);
                    FadeTransition ft = new FadeTransition(Duration.millis(1000), gameImageViewer);
                    ft.setFromValue(0.5);
                    ft.setToValue(1);
                    ft.setCycleCount(1);
                    ft.setAutoReverse(true);
                    ft.play();
                    gamesListContainer.getChildren().removeAll(gameImageViewer);
                    gamesListContainer.getChildren().add(gameImageViewer);

                }
            });

            b.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    System.out.println("Mouse Clicked !");
                }
            });
            gamesList.getChildren().add(b);
    }


    private static void listFilesForFolder(final File folder) {
        ArrayList<String> games = new ArrayList<>();

        ArrayList<String> gamesShortcuts = new ArrayList<>();
        for (final File fileEntry : folderShortcut.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("Reading files under the folder " + folderShortcut.getAbsolutePath());
                listFilesForFolder(fileEntry);
            } else {
                if (fileEntry.isFile()) {
                    temp = fileEntry.getName();
                    String tempName = stripExtension(temp);
                    String tempExtension = temp.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase();
                    if ((tempExtension).equals("lnk") || (tempExtension).equals("url")) {
                        gamesShortcuts.add(tempName);

                    }
                }

            }
        }

        System.out.println(folder.getAbsoluteFile());
        ArrayList<String> gamesImages = new ArrayList<>();

        for (final File fileEntry : folderImage.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("Reading files under the folder " + folderImage.getAbsolutePath());
                listFilesForFolder(fileEntry);
            } else {
                if (fileEntry.isFile()) {
                    temp = fileEntry.getName();
                    String tempName = stripExtension(temp);
                    String tempExtension = temp.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase();
                    if ((tempExtension).equals("jpg")) {
                        gamesImages.add(tempName);
                    }
                }
            }
        }
        // your code
        for (String gameText : gamesShortcuts) {
            if (gamesImages.contains(gameText)) {
                games.add(gameText);
                String gameImagePath = folderImage.getAbsolutePath()+"/"+gamesImages.get(gamesImages.indexOf(gameText)) + ".jpg";
                File fileImagePath = new File(gameImagePath);
                String gameShortcut = folderShortcut.getAbsolutePath()+"/"+gamesImages.get(gamesImages.indexOf(gameText)) + ".jpg";
                File fileShortcut = new File(gameShortcut);
                Game gameElement = new Game(fileShortcut,gameText,fileImagePath);
                gamesWithImage.add(gameElement);
            }
        }
    }

    private static String stripExtension(String str) {
        // Handle null case specially.

        if (str == null) return null;

        // Get position of last '.'.

        int pos = str.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.

        if (pos == -1) return str;

        // Otherwise return the string, up to the dot.

        return str.substring(0, pos);
    }

    private static class Game {
        private String gameText;
        private File gameExe;
        private File gameImage;

        Game(File gameExe,String gameText, File gameImage) {
            this.gameImage = gameImage;
            this.gameText = gameText;
            this.gameExe = gameExe;
        }
    }
}
