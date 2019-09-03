package ui;

import helper.FileHelper;
import javafx.animation.*;
import javafx.application.Platform;
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
    private VBox container;
    //    @FXML
//    private Button refreshButton;
    private static ArrayList<Category> categories = new ArrayList<>();
    private static Category uncategorized = new Category("Uncategorized");
    private static Main main = new Main();
    private Stage stage = ui.Main.getStage();
    private final double width = main.returnScreenWidth();
    private final double height = main.returnScreenHeight();
    private double minListWidth; // default width.
    private static final File folder = FileController.getFolder();
    private static final File folderShortcut = FileController.getFolderShortcut();
    private static ImageView gameImageViewer = new ImageView();
    static boolean steamInfoFetched = false;
    static boolean firstSteamUser = false;
    private VBox gamesList = new VBox();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (width < 1400) {
            minListWidth = width / 7; // default width.
        } else if (width > 1401 && width < 2048) {
            minListWidth = width / 9;
        } else {
            minListWidth = width / 11;
        }
//        System.out.println("width: "+minListWidth);

        try {
            scene.getStylesheets().add(getClass().getClassLoader().getResource("css/style.css").toExternalForm());
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        expandedScene.setAlignment(Pos.TOP_LEFT);

//        expandButton.setPrefWidth(main.returnSceneWidth() - 5);
//        expandButton.setPrefHeight(main.returnSceneHeight() - 5);
        setStyles();
        expandedScene.setVisible(false);
        container.getChildren().add(gamesList);
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

        expandButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<>() {
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
        gameImageViewer.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                collapseScreen();
            }
        });

        Button refreshButton = new Button();
        try {
            refreshButton.setGraphic(new ImageView(new Image(this.getClass().getResourceAsStream("/images/icons/sync.jpg"), 30, 30, false, false)));
            refreshButton.setStyle("-fx-background-color: transparent;");
            container.getChildren().add(0, refreshButton);
            RotateTransition rt = new RotateTransition(Duration.millis(2000), refreshButton);

            refreshButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<>() {
                @Override
                public void handle(MouseEvent e) {
                    rt.setFromAngle(0);
                    rt.setToAngle(180);
                    rt.setInterpolator(Interpolator.LINEAR);
                    rt.setCycleCount(Timeline.INDEFINITE);
                    rt.play();
                }
            });

            refreshButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<>() {
                @Override
                public void handle(MouseEvent e) {
                    rt.setToAngle(rt.getByAngle());
//                    System.out.println(rt.getFromAngle());
//                    rt.setToAngle();
                    rt.stop();
                }
            });

            refreshButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<>() {
                @Override
                public void handle(MouseEvent e) {
                    RotateTransition rt = new RotateTransition(Duration.millis(3000), refreshButton);
                    rt.setByAngle(360);
                    rt.setCycleCount(1);
                    rt.play();
                    gamesList.getChildren().clear();
                    categories.add(uncategorized);
                    addToListFromRoot();
                    generateButton(categories);
                }
            });
        } catch (NullPointerException e) {
            System.out.println("SVG Image couldnt load: " + e.getMessage());
        }
        System.out.println("Activated FXML init");
        System.out.println("X:" + stage.getX() + " Y: " +stage.getY()+" Width: "+stage.getWidth() + " Height: " + stage.getHeight());
    }

    static void openSteamUserDialog() {
        if (firstSteamUser) {
            Alert userIsWantSteamDialog = new Alert(Alert.AlertType.CONFIRMATION);
            userIsWantSteamDialog.setTitle("Game Launcher");
            userIsWantSteamDialog.setHeaderText(null);
            userIsWantSteamDialog.setContentText("Steam User Not Found. Do you Want add?");
            userIsWantSteamDialog.showAndWait();
            Optional<ButtonType> result = userIsWantSteamDialog.showAndWait();
            if (result.get() == ButtonType.OK) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Game Launcher");
                dialog.setHeaderText("Enter your Steam URL name or Your Steam ID");
                dialog.setContentText("Name or ID:");
                Optional<String> userInput = dialog.showAndWait();

                result.ifPresent(name -> {
                    try {
                        if (userInput.get().length() > 0) {
                            Steam.initUser(userInput.get());
                            System.out.println(userInput.get());
                        } else {
                            System.out.println("not entered");
                        }
                    } catch (NoSuchElementException e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
        }
    }

    private void checkAndGetSteamUser() {
        if (steamInfoFetched) {
            Label labelSteamName = new Label("Hoşgeldiniz " + ui.Main.userInfo.getPersonaName(), null);
            container.getChildren().add(1, labelSteamName);
            labelSteamName.setStyle("-fx-font-weight: bold;");
        }
    }

    private void setStyles() {
        centerItems(expandButton);
    }

    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent) node, nodes);
        }
    }

    private void collapseScreen() {
        System.out.println("Button width: " +expandButton.getWidth());
        gameImageViewer.setImage(null);
        expandedScene.setVisible(false);
        expandButton.setVisible(true);
        stage.setWidth(expandButton.getWidth());
        stage.setHeight(expandButton.getHeight());
//        System.out.println("Button width: " +expandButton.getWidth() + "2");
        System.out.println("Collapsing... " + " X:" + stage.getX() + " Y: " +stage.getY()+" Width: "+stage.getWidth() + " Height: " + stage.getHeight());
    }

    private void showExpandedScene() {
        expandButton.setVisible(false);
        expandedScene.setVisible(true);
//        expandedScene.setMinSize(0, 0);
        stage.setWidth(minListWidth);
        stage.setHeight(height);
        ArrayList<Node> nodes = getAllNodes(gamesList);
        double highest = 0;
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) instanceof VBox) {
                for (int j = 0; j < ((VBox) nodes.get(i)).getChildren().size(); j++) {
                    if (((VBox) nodes.get(i)).getChildren().get(j) instanceof Button) {
                        Button btn = ((Button) ((VBox) nodes.get(i)).getChildren().get(j));
                        double width = btn.getWidth();
                        if (width > highest) {
                            highest = width;
                        }
                    }
                }
            }
        }
        container.setPadding(new Insets(labelGameList.getHeight() + 20, 15, 0, 10));
        container.setStyle("-fx-background-color: linear-gradient(to right, rgba(200,200,200,1) 0%, rgba(200,200,200,0.80) 30%,rgba(255,255,255,0.20) 80%, rgba(255,255,255,0.0) 100%)");
        container.setMaxWidth(minListWidth + highest);
        TranslateTransition trans = new TranslateTransition(Duration.seconds(1), expandedScene);
        trans.setFromX(-150);
        trans.setToX(0);
        trans.play();
        System.out.println("Expanding... : "+ " X: " + stage.getX() + " Y: " +stage.getY()+" Width: "+stage.getWidth() + " Height: " + stage.getHeight());
        /*trans.setOnFinished(new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });*/
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
                gameButton.setAlignment(Pos.BASELINE_LEFT);
                gameButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<>() {
                    @Override
                    public void handle(MouseEvent e) {
                        stage.setWidth(width + 18);
                        gameImageViewer.setImage(null);
                        gameImageViewer.setImage(new Image(game.getGameImage().toURI().toString(), expandedScene.getWidth(), expandedScene.getHeight(), false, false));
                        System.gc(); // fresh the ram, useful for new Image
                        FadeTransition ft = new FadeTransition(Duration.millis(1000), gameImageViewer);
                        ft.setFromValue(0.1);
                        ft.setToValue(1);
                        ft.play();
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
                                    Main.steamGameDetected = game.getIsSteamGame();
                                    System.out.println("Program starting");
                                    Process proc = pb.start();
//                                    System.out.println(pb.command());
                                } else {
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
        categories.clear();
        for (Category category : categories) {
            category.removeGames();
        }
        uncategorized.removeGames();
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