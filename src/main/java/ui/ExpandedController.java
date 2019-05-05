package ui;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.FileController;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static java.awt.Cursor.HAND_CURSOR;

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
    private static ArrayList<Category> categories = new ArrayList<>();
    private static Category uncategorized = new Category("Uncategorized");

    private final double width = main.returnScreenWidth();
    private final double height = main.returnScreenHeight();
    private Stage stage = main.getStage();
    private final double listWidth = width / 10;
    private final double listHeight = height;
    private static final File folder = FileController.getFolder();
    private static final File folderImage = FileController.getFolderImage();
    private static final File folderShortcut = FileController.getFolderShortcut();

    private static final ArrayList<String> fileExe = new ArrayList<>();
    private static ArrayList<String> shortcuts = new ArrayList<>();
    private static ArrayList<String> images = new ArrayList<>();
    private static ImageView gameImageViewer = new ImageView();
    private static Label label = new Label();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        expandedScene.getStylesheets().add(getClass().getClassLoader().getResource("css/style.css").toExternalForm());
        stage.setWidth(listWidth);
        stage.setHeight(height);
        expandedScene.setMinSize(0, 0);
        expandedScene.setPrefSize(expandedContainer.getPrefWidth(), expandedContainer.getPrefHeight()); //didn't work
        stage.setMaxWidth(width);
        gamesList.setPadding(new Insets(labelGameList.getHeight() + 20, 15, 0, 10));
        gamesList.setStyle("-fx-background-color: linear-gradient(to right, rgba(200,200,200,1) 0%, rgba(200,200,200,0.80) 30%,rgba(255,255,255,0.20) 80%, rgba(255,255,255,0.0) 100%)");
        gameImageViewer.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<>() {
            @Override
            public void handle(MouseEvent e) {
                collapseScreen();
            }
        });
        expandedScene.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<>() {
            @Override
            public void handle(MouseEvent e) {
                setIsClicked(true);
            }
        });

        categories.add(uncategorized);
        addToListFrom();
        generateButton(categories);

        // Set up a Translate Transition for the Text object
        TranslateTransition trans = new TranslateTransition(Duration.seconds(1), gamesList);
        trans.setFromX(-150);
        trans.setToX(0);
        // Let the animation run forever
        trans.setCycleCount(1);
        // Play the Animation
        trans.play();
    }

    private void setIsClicked(boolean status) {
        isClicked = status;
    }

    public void collapseScreen() {
        categories.clear();
        for (Category category : categories) {
            category.removeGames();
        }
        uncategorized.removeGames(); //it should be there, can't delete automatically in for loop
        TranslateTransition trans = new TranslateTransition(Duration.seconds(1), gamesList);
        trans.setFromX(0);
        trans.setToX(-150);
        // Play the Animation
        trans.play();
        trans.setOnFinished(new EventHandler<>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    main.changeSceneWithButton("fxml/scene.fxml");
                    stage.setWidth(main.returnSceneWidth());
                    stage.setHeight(main.returnSceneHeight());
                } catch (Exception e) {
                    System.out.println("Collapsed screen couldnt load: " + e.getMessage() + " | " + e.getClass().getCanonicalName());
                }
            }
        });


    }

    private void generateButton(ArrayList<Category> categories) {
        for (Category category : categories) {
//            Button buttonCategory = new Button();
            //using a two-parameter constructor
            TitledPane tp = new TitledPane(category.category_name, null);
//applying methods
//            tp.setContent(new Button("Button"));
            tp.setText(category.category_name);
            tp.setStyle(
                    "-fx-background-color: rgba(255,255,255,0);");
            tp.setExpanded(false);
            gamesList.getChildren().add(tp);
            final VBox vbox = new VBox(0);


            ArrayList<Category.Game> games = category.getGames();
            for (Category.Game game : games) {
                Button gameButton = new Button();
                gameButton.setText(game.gameText);
                gameButton.setStyle(
                        "-fx-background-color: rgba(255, 255, 255, 0);");

                gameButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<>() {
                    @Override
                    public void handle(MouseEvent e) {
                        Image picture = new Image(game.gameImage.toURI().toString(), expandedScene.getWidth(), expandedScene.getHeight(), false, false);
                        gameImageViewer.setImage(picture);
                        FadeTransition ft = new FadeTransition(Duration.millis(1000), gameImageViewer);
                        ft.setFromValue(0.5);
                        ft.setToValue(1);
                        ft.play();
                        stage.setWidth(width + 18);
                        expandedScene.setAlignment(Pos.TOP_LEFT);
                        gameButton.setCursor(javafx.scene.Cursor.HAND);
                        expandedScene.getChildren().removeAll(gameImageViewer);
                        expandedScene.getChildren().add(gameImageViewer);
                        gamesList.toFront();
                        gameImageViewer.toBack();
                    }
                });
                gameButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<>() {
                    @Override
                    public void handle(MouseEvent e) {
                        setIsClicked(false);
                        stage.setAlwaysOnTop(false);
                        collapseScreen();

                        String op = main.getOperatingSystem();
                        System.out.println("Operating System = " + op);
                        System.out.println("mouse clicked");

                        if (op.equals("Windows 10") || op.equals("Windows 7")) {

                            Runtime rt = Runtime.getRuntime();
                            String cmd = "cmd /c start cmd.exe /K \"" + game.gameExe + "\"";
                            try {
                                Process proc = rt.exec(cmd);
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
                vbox.getChildren().add(gameButton);
                tp.setContent(vbox);
            }
        }
    }

    private static void addToListFrom() {
        File[] files = folderShortcut.listFiles();
        if (files == null) {
            System.out.println("Folder is empty");
        } else {
            try {
                for (final File fileEntry : files) {
                    if (fileEntry.isDirectory()) {
                        Category category = new Category(fileEntry.getName());
                        categories.add(category);
                        addToCategory(fileEntry.getAbsoluteFile(), category);
                    } else {
                        if (fileEntry.isFile()) {
                            String gameText = stripExtension(fileEntry.getName());
                            File fileImage = new File(folder + File.separator + "images" + File.separator + gameText + ".jpg");
                            uncategorized.addGame(fileEntry, gameText, fileImage);
                        }
                    }
                }
            } catch (NullPointerException e) {
                System.out.println("File is empty.");
            }
        }
    }

    private static void addToCategory(File folderParam, Category category) {
        for (final File fileEntry : folderParam.listFiles()) {
            if (fileEntry.isFile()) {
                String gameText = stripExtension(fileEntry.getName());
                File fileImage = new File(fileEntry.getParentFile().getParentFile().getParentFile().getPath() + File.separator + "images" + File.separator + gameText + ".jpg");
                category.addGame(fileEntry, gameText, fileImage);
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

    private static class Category {

        String category_name;
        ArrayList<Game> games = new ArrayList<>();

        Category(String category_name) {
            this.category_name = category_name;
        }

        private ArrayList<Game> getGames() {
            return this.games;
        }

        private void addGame(File gameExe, String gameText, File gameImage) {
            Game game = new Game(gameExe, gameText, gameImage);
            this.games.add(game);
        }

        private void removeGames() {
            this.games.clear();
        }

        static class Game {
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
}
