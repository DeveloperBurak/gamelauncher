package ui;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ExpandedController implements Initializable {
    @FXML
    private Label labelGameList;
    @FXML
    private AnchorPane expandedContainer;
    @FXML
    private StackPane expandedScene;
    @FXML
    private VBox gamesList;
    private Main main = new Main();
    private static boolean isClicked = false;
    static String temp = "";
    private static ArrayList<Game> gamesWithImage = new ArrayList<>();
    private final double width = main.returnScreenWidth();
    private final double height = main.returnScreenHeight();
    private Stage stage = main.getStage();
    private final double listWidth = width / 10;
    private final double listHeight = height;
    private static final File folder = new File("C:/Users/Developer/Documents/Game Launcher");
    private static final File folderImage = new File(folder.getPath() + "/images/");
    private static final File folderShortcut = new File(folder.getPath() + "/shortcuts/");
    private static ArrayList<String> shortcuts = new ArrayList<>();
    private static ArrayList<String> images = new ArrayList<>();
    private static ImageView gameImageViewer = new ImageView();
    private static Label label = new Label();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*expandedContainer.setPrefWidth(listWidth);
        expandedContainer.setPrefHeight(height);*/
        stage.setWidth(listWidth);
        stage.setHeight(height);
        expandedScene.setMinSize(0, 0);
        expandedScene.setPrefSize(expandedContainer.getPrefWidth(), expandedContainer.getPrefHeight()); //didn't work

        stage.setMaxWidth(width);
        gamesList.setPrefWidth(stage.getWidth() - 1500);
        gamesList.setPadding(new Insets(labelGameList.getHeight() + 20, 15, 0, 10));
        gamesList.setStyle("-fx-background-color: linear-gradient(to right, rgba(100,100,100,0.80) 0%, rgba(150,150,150,0.0) 100%)");
        gameImageViewer.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                collapseScreen();
            }
        });
        expandedScene.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                setIsClicked(true);
            }
        });

        addToListFrom(folderImage, images);
        addToListFrom(folderShortcut, shortcuts);
        generateGameWithImage(shortcuts, images);

        for (Game game : gamesWithImage) {
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

    private void generateGameWithImage(ArrayList<String> shortcuts, ArrayList<String> images) {
        for (String game : shortcuts) {
            String gameName = stripExtension(game); // get base name
            String gameImage = gameName + ".jpg"; // set image file.
            if (images.contains(gameImage)) {
                String gameImagePath = folderImage.getAbsolutePath() + "/" + images.get(images.indexOf(gameImage));
                File fileImagePath = new File(gameImagePath);
                String gameShortcut = folderShortcut.getAbsolutePath() + "/" + game;
                File fileShortcut = new File(gameShortcut);
                Game gameElement = new Game(fileShortcut, gameName, fileImagePath);
                gamesWithImage.add(gameElement);
                System.out.println("dasda");
            }
        }
    }

    private void setIsClicked(boolean status) {
        isClicked = status;
    }

    public void collapseScreen() {
        if (!isClicked) {
            try {
                gamesWithImage.clear();
                main.changeSceneWithButton("fxml/scene.fxml");
                System.out.println(stage.getWidth());
                stage.setWidth(main.returnSceneWidth());
                stage.setHeight(main.returnSceneHeight());
                System.out.println("Screen width: " + main.returnScreenHeight());
                System.out.println("Screen height: " + main.returnScreenWidth());
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
        b.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                Image picture = new Image(game.gameImage.toURI().toString(), expandedScene.getWidth(), expandedScene.getHeight(), false, false);
                gameImageViewer.setImage(picture);
                FadeTransition ft = new FadeTransition(Duration.millis(1000), gameImageViewer);
                ft.setFromValue(0.5);
                ft.setToValue(1);
                ft.play();
                stage.setWidth(width + 18);
                gamesList.setMaxWidth(stage.getWidth() - 1500);
                expandedScene.setAlignment(Pos.TOP_LEFT);

                expandedScene.getChildren().removeAll(gameImageViewer);
                expandedScene.getChildren().add(gameImageViewer);
                gamesList.toFront();
                gameImageViewer.toBack();
                System.out.println(width);
            }
        });

        b.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                setIsClicked(false);
                stage.setAlwaysOnTop(false);

                String op = main.getOperatingSystem();
                System.out.println("Operating System = " + op);
                System.out.println("mouse clicked");

                if (op.equals("Windows 10") || op.equals("Windows 7")) {

                    Runtime rt = Runtime.getRuntime();
                    String cmd = "cmd /c start cmd.exe /K \"" + game.gameExe + "\"";
                    try {
                        Process proc = rt.exec(cmd);
//                        proc.destroy();
                        try {
                            Thread.sleep(4000);
                            Runtime.getRuntime().exec("taskkill /f /im cmd.exe");

                        } catch (InterruptedException exc) {
                            System.out.println(exc.getMessage());
                        }

                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                    System.out.println("Executing command: " + cmd);
                }

            }
        });
        gamesList.getChildren().add(b);
    }

    private static String getExtensionOf(String str) {
        String tempName = str.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase();
        return tempName;
    }

    private static void addToListFrom(File folderParam, ArrayList<String> assetArray) {
        try {
            for (final File fileEntry : folderParam.listFiles()) {
                if (fileEntry.isDirectory()) {
                    System.out.println("Reading files under the folder " + folderParam.getAbsolutePath());
                    addToListFrom(fileEntry, assetArray);
                } else {
                    if (fileEntry.isFile()) {
                        temp = fileEntry.getName();
                        if (!assetArray.contains(temp)) {
                            assetArray.add(temp);
                        } else {
                            System.out.println("Duplicated : " + temp);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            System.out.println("File is empty.");
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

        Game(File gameExe, String gameText, File gameImage) {
            this.gameImage = gameImage;
            this.gameText = gameText;
            this.gameExe = gameExe;
        }
    }
}
