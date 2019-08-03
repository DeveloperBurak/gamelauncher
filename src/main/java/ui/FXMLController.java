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
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Category;
import main.FileController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/*
 * @todo fix the width problem on width.
 * */

public class FXMLController implements Initializable {
    @FXML
    private Button expandButton;
    @FXML
    private AnchorPane scene;
    @FXML
    private javafx.scene.control.Label labelGameList;
    @FXML
    private AnchorPane expandedContainer;
    @FXML
    private StackPane expandedScene;
    @FXML
    private VBox gamesList;
    private static ArrayList<Category> categories = new ArrayList<>();
    private static Category uncategorized = new Category("Uncategorized");
    private static Main main = new Main();
    private Stage stage = ui.Main.getStage();
    private final double width = main.returnScreenWidth();
    private final double height = main.returnScreenHeight();
    private final double listWidth = width / 10;
    private static final File folder = FileController.getFolder();
    private static final File folderShortcut = FileController.getFolderShortcut();
    private static ImageView gameImageViewer = new ImageView();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            scene.getStylesheets().add(getClass().getClassLoader().getResource("css/style.css").toExternalForm());
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        expandButton.setPrefWidth(main.returnSceneWidth() - 5);
        expandButton.setPrefHeight(main.returnSceneHeight() - 5);
        expandedScene.setVisible(false);
        stage.setX(-8);
        expandButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<>() {
            @Override
            public void handle(MouseEvent e) {
                showExpandedScene();
            }
        });
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
        trans.play();
        gameImageViewer.setImage(null);
        trans.setOnFinished(new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Collapsing stage width: " + stage.getWidth());
                expandedScene.setVisible(false);
                expandButton.setVisible(true);
                gamesList.getChildren().clear();
                stage.setWidth(main.returnSceneWidth());
                stage.setHeight(main.returnSceneHeight());
            }
        });
    }

    private void showExpandedScene() {
        expandedScene.setVisible(true);
        expandButton.setVisible(false);
        expandedScene.setMinSize(0, 0);
        stage.setWidth(listWidth);
        System.out.println("stage current width: " + stage.getWidth());
        stage.setHeight(height);
        gamesList.setPadding(new Insets(labelGameList.getHeight() + 20, 15, 0, 10));
        gamesList.setStyle("-fx-background-color: linear-gradient(to right, rgba(200,200,200,1) 0%, rgba(200,200,200,0.80) 30%,rgba(255,255,255,0.20) 80%, rgba(255,255,255,0.0) 100%)");
        gameImageViewer.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<>() {
            @Override
            public void handle(MouseEvent e) {
                collapseScreen();
            }
        });
        categories.add(uncategorized);
        addToListFrom();
        generateButton(categories);
        TranslateTransition trans = new TranslateTransition(Duration.seconds(1), expandedScene);
        trans.setFromX(-150);
        trans.setToX(0);
        trans.play();
    }

    private void generateButton(ArrayList<Category> categories) {
        for (Category category : categories) {
            TitledPane tp = new TitledPane(category.getCategoryName(), null);
            tp.setText(category.getCategoryName());
            tp.setStyle("-fx-background-color: rgba(255,255,255,0);");
            if (!uncategorized.getCategoryName().equals(tp.getText())) {
                tp.setExpanded(false);
            } else {
                tp.setExpanded(true);
            }
            gamesList.getChildren().add(tp);
            final VBox vbox = new VBox(0);
            ArrayList<Category.Game> games = category.getGames();
            for (Category.Game game : games) {
                Button gameButton = new Button();
                gameButton.setText(game.getGameText());
                gameButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0);");

                gameButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<>() {
                    @Override
                    public void handle(MouseEvent e) {
                        javafx.scene.image.Image picture = new Image(game.getGameImage().toURI().toString(), expandedScene.getWidth(), expandedScene.getHeight(), false, false);
                        gameImageViewer.setImage(picture);
                        FadeTransition ft = new FadeTransition(Duration.millis(1000), gameImageViewer);
                        ft.setFromValue(0.1);
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
                        stage.setAlwaysOnTop(false);
                        collapseScreen();
                        String op = Main.activity.getOperatingSystem();
                        if (op.equals("Windows 10") || op.equals("Windows 7")) {
                            try {
                                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", game.getGameExe().getAbsolutePath());
                                Process proc = pb.start();
                            } catch (IOException ex) {
                                System.out.println(ex.getMessage());
                            }
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
            for (final File fileEntry : files) {
                if (fileEntry.isDirectory()) {
                    Category category = new Category(fileEntry.getName());
                    categories.add(category);
                    category.addGamesFromFolder(fileEntry.getAbsoluteFile());
                } else {
                    if (fileEntry.isFile()) {
                        String gameText = helper.File.stripExtension(fileEntry.getName());
                        File fileImage = new File(folder + File.separator + "images" + File.separator + gameText + ".jpg");
                        uncategorized.addGame(fileEntry, gameText, fileImage);
                    }
                }
            }
        }
    }

    public void centered() {
        double width = 0;
        double height = 0;
        try {
            width = scene.getWidth();
            height = scene.getHeight();
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        expandButton.setLayoutX(width / 2 - expandButton.getWidth() / 2);
        expandButton.setLayoutY(height / 2 - expandButton.getHeight() / 2);
    }
}