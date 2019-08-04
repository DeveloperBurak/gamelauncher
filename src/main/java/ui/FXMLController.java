package ui;

import helper.FileHelper;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Category;
import main.FileController;
import main.Steam;
import main.WindowsActivities;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
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
    static boolean steamInfoFetched = false;
    static boolean firstSteamUser = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            scene.getStylesheets().add(getClass().getClassLoader().getResource("css/style.css").toExternalForm());
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        expandButton.setPrefWidth(main.returnSceneWidth() - 5);
        expandButton.setPrefHeight(main.returnSceneHeight() - 5);
        setStyles();
        expandedScene.setVisible(false);
        categories.add(uncategorized);
        addToListFromRoot();
        generateButton(categories);
        Thread steamThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Runnable updater = new Runnable() {
                    @Override
                    public void run() {
                        checkAndGetSteamUser();
                    }
                };
                while (!steamInfoFetched) {
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                // UI update is run on the Application thread
                Platform.runLater(updater);
            }
        });
        // don't let thread prevent JVM shutdown
        steamThread.setDaemon(true);
        steamThread.start();

        expandButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<>() {
            @Override
            public void handle(MouseEvent e) {
                showExpandedScene();
            }
        });
        expandedScene.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                collapseScreen();
            }
        });
    }

    static void openSteamUser(){
        if(firstSteamUser){
            Alert userIsWantSteamDialog = new Alert(Alert.AlertType.CONFIRMATION);
            userIsWantSteamDialog.setContentText("Test?");
            userIsWantSteamDialog.setTitle("Game Launcher");
            userIsWantSteamDialog.setHeaderText(null);
            userIsWantSteamDialog.setContentText("Steam User Not Found. Do you Want add?");
            userIsWantSteamDialog.showAndWait();
            Optional<ButtonType> result = userIsWantSteamDialog.showAndWait();
            if(result.get() == ButtonType.OK){
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Game Launcher");
                dialog.setHeaderText("Enter your Steam URL name");
                dialog.setContentText("Name:");
                Optional<String> userInput = dialog.showAndWait();

                result.ifPresent(name -> {
                    try{
                        if(userInput.get().length() > 0){
                            Steam.initUser(userInput.get());
                            System.out.println(userInput.get());
                        }else{
                            System.out.println("not entered");
                        }
                    }catch (NoSuchElementException e){
                        System.out.println(e.getMessage());
                    }
                });
            }
        }

    }

    private void checkAndGetSteamUser(){
        if(steamInfoFetched){
            Label labelSteamName = new Label("Hoşgeldiniz " + ui.Main.userInfo.getRealName(),null);
            gamesList.getChildren().add(0,labelSteamName);
            labelSteamName.setStyle("-fx-font-weight: bold;");
        }
    }

    private void setStyles(){
        centerItems(expandButton);
    }


    private void collapseScreen() {
        System.out.println("Collapsing...");
        categories.clear();
        for (Category category : categories) {
            category.removeGames();
        }
        uncategorized.removeGames();
        gameImageViewer.setImage(null);
        expandedScene.setVisible(false);
        expandButton.setVisible(true);
        stage.setWidth(expandButton.getWidth());
        stage.setHeight(expandButton.getHeight());

//        FadeTransition ft = new FadeTransition();
//        ft.setDuration(Duration.seconds(1));
//        ft.setNode(gamesList);
//        ft.setFromValue(1);
//        ft.setToValue(0);
//        ft.play();
    }

    private void showExpandedScene() {
        expandButton.setVisible(false);
        expandedScene.setVisible(true);
        expandedScene.setMinSize(0, 0);
        stage.setWidth(listWidth);
        stage.setHeight(height);
        gamesList.setPadding(new Insets(labelGameList.getHeight() + 20, 15, 0, 10));
        gamesList.setStyle("-fx-background-color: linear-gradient(to right, rgba(200,200,200,1) 0%, rgba(200,200,200,0.80) 30%,rgba(255,255,255,0.20) 80%, rgba(255,255,255,0.0) 100%)");
        gamesList.setMaxWidth(listWidth);
        gameImageViewer.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<>() {
            @Override
            public void handle(MouseEvent e) {
                collapseScreen();
            }
        });


        TranslateTransition trans = new TranslateTransition(Duration.seconds(1), expandedScene);
        trans.setFromX(-150);
        trans.setToX(0);
        trans.play();
        trans.setOnFinished(new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Stage Coordinates : \n X: " + stage.getX() + " \n Y: " + stage.getY());
                System.out.println("Stage Sizes : \n X: " + stage.getWidth() + " \n Y: " + stage.getHeight());
            }
        });
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
                        stage.setWidth(width + 18);
                        gameImageViewer.setImage(null);
                        javafx.scene.image.Image picture = new Image(game.getGameImage().toURI().toString(), expandedScene.getWidth(), expandedScene.getHeight(), false, false);
                        gameImageViewer.setImage(picture);
                        FadeTransition ft = new FadeTransition(Duration.millis(1000), gameImageViewer);
                        ft.setFromValue(0.1);
                        ft.setToValue(1);
                        ft.play();
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
                        String op = WindowsActivities.getOperatingSystem();
                        if (op.equals("Windows 10") || op.equals("Windows 7")) {
                            try {
                                if (game.checkGameExist()) {
                                    ProcessBuilder pb = new ProcessBuilder("cmd", "/c", game.getGameExe().getAbsolutePath());
                                    System.out.println("Program starting");
                                    Process proc = pb.start();
//                                    System.out.println(pb.command());
                                } else{
                                    game.getGameExe().delete();
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Game Launcher");
                                    alert.setHeaderText(null);
                                    alert.setContentText("Program Not Found: " + game.getGameText());
                                    alert.showAndWait();
                                }
                                collapseScreen();
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

    private static void addToListFromRoot() {
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
                        String gameText = FileHelper.stripExtension(fileEntry.getName());
                        File fileImage = new File(folder + File.separator + "images" + File.separator + gameText + ".jpg");
                        uncategorized.addGame(fileEntry, gameText, fileImage);
                    }
                }
            }
        }
    }

    private void centerItems(Region node) {
        double width = 0;
        double height = 0;
        try {
            width = scene.getWidth();
            height = scene.getHeight();
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        node.setLayoutX(width / 2 - node.getWidth() / 2);
        node.setLayoutY(height / 2 - node.getHeight() / 2);
    }
}