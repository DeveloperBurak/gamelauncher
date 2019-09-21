package ui;

import com.google.gson.Gson;
import helper.FileHelper;
import helper.Monitor;
import helper.Monitors;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Category;
import main.FileController;
import main.Steam;
import main.OsActivities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;

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
    private static Category unCategorized = new Category("Uncategorized");
    private static Main main = new Main();
    private Stage stage = ui.Main.getStage();
    //    private static UIScreen screen = main.getScreen();
    private final double SCREEN_WIDTH = main.getScreen().getScreenWidth();
    private final double SCREEN_HEIGHT = main.getScreen().getScreenHeight();
    private double minListWidth; // default width.
    private static final File folder = FileController.getFolder();
    private static final File folderShortcut = FileController.getFolderShortcut();
    private static ImageView gameImageViewer = new ImageView();
    static boolean steamInfoFetched = false;
    static boolean firstSteamUser = false;
    private VBox gamesList = new VBox();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (SCREEN_WIDTH < 1400) {
            minListWidth = SCREEN_WIDTH / 7; // default width.
        } else if (SCREEN_WIDTH > 1401 && SCREEN_WIDTH < 2048) {
            minListWidth = SCREEN_WIDTH / 9;
        } else {
            minListWidth = SCREEN_WIDTH / 11;
        }
        try {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/style.css")).toExternalForm());
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        expandButton.setPrefWidth(main.getSceneWidth() - 5);
        expandButton.setPrefHeight(main.getSceneHeight() - 5);
        setStyles();
        expandedScene.setVisible(false);
        container.getChildren().add(gamesList);
        categories.add(unCategorized);
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
                Platform.runLater(updater);
                while (!steamInfoFetched) {
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                // UI update is run on the Application thread
            }
        });
        // don't let thread prevent JVM shutdown
        steamThread.setDaemon(true);
        steamThread.start();

        expandButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> showExpandedScene());
//        expandedScene.addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> collapseScreen());
        gameImageViewer.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEvent -> collapseScreen());

        Button refreshButton = new Button();
        try {
            refreshButton.setGraphic(new ImageView(new Image(this.getClass().getResourceAsStream("/images/icons/sync.jpg"), 30, 30, false, false)));
            refreshButton.setStyle("-fx-background-color: transparent;");
            container.getChildren().add(0, refreshButton);
            RotateTransition rt = new RotateTransition(Duration.millis(2000), refreshButton);

            refreshButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                rt.setFromAngle(0);
                rt.setToAngle(180);
                rt.setInterpolator(Interpolator.LINEAR);
                rt.setCycleCount(Timeline.INDEFINITE);
                rt.play();
            });

            refreshButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
                rt.setToAngle(rt.getByAngle());
                System.out.println(rt.getFromAngle());
//                    rt.setToAngle();
                rt.stop();
            });

            refreshButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                RotateTransition rt1 = new RotateTransition(Duration.millis(3000), refreshButton);
                rt1.setByAngle(360);
                rt1.setCycleCount(1);
                rt1.play();
                gamesList.getChildren().clear();
                categories.add(unCategorized);
                addToListFromRoot();
                generateButton(categories);
            });
        } catch (NullPointerException e) {
            System.out.println("SVG Image couldnt load: " + e.getMessage());
        }
    }

    static void openMonitorSelectorDialog() {
        final List<Monitors.Monitor> monitors = Monitors.getMonitorsWithSize();
        System.out.println(monitors);
        if (monitors.size() == 1) {
            System.out.println(monitors.get(0).getHeight());
        } else if (monitors.size() > 1) {
            List<String> monitorList = new ArrayList<>();
            int count = 0;
            for (Monitors.Monitor monitor : monitors) {
                monitorList.add((count + 1) + " : " + monitors.get(count).getWidth() + " x " + monitors.get(count).getHeight());
                count++;
            }
            ChoiceDialog dialogMonitorScreen = new ChoiceDialog(monitorList.get(0), monitorList);
            dialogMonitorScreen.setTitle("Monitors");
            dialogMonitorScreen.setHeaderText("Select your choice");

            Optional<String> result = dialogMonitorScreen.showAndWait();
            String selected;

            if (result.isPresent()) {
                selected = result.get();
                int selected_monitor = Character.getNumericValue(selected.charAt(0));
                selected_monitor = selected_monitor - 1; // we set +1 for beautiful read to numbers. so we must minus -1 for get real array index.
                Gson gson = new Gson();
                Monitors.Monitor monitor = new Monitors.Monitor(selected_monitor + 1, monitors.get(selected_monitor).getWidth(), monitors.get(selected_monitor).getHeight());
                try (FileWriter writer = new FileWriter(Main.getMonitorSettingsFile())) {
                    gson.toJson(monitor, writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Monitor couldnt find.");
        }
    }

    static void openSteamUserDialog() {
        if (firstSteamUser) {
            System.out.println("Dialog pane is opening");
            Alert userIsWantSteamDialog = new Alert(Alert.AlertType.CONFIRMATION);
            userIsWantSteamDialog.setTitle("Game Launcher");
            userIsWantSteamDialog.setHeaderText(null);
            userIsWantSteamDialog.setContentText("Steam User Not Found. Do you Want add?");
            Optional<ButtonType> result = userIsWantSteamDialog.showAndWait();
            result.ifPresent(choice -> {
                if (choice == ButtonType.OK) {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Game Launcher");
                    dialog.setHeaderText("Enter your Steam URL name or Your Steam ID");
                    dialog.setContentText("Name or ID:");
                    Optional<String> userInput = dialog.showAndWait();
                    userInput.ifPresent(Steam::initUser);
                }
            });
        }
    }

    private void checkAndGetSteamUser() {
        if (steamInfoFetched) {
            Label labelSteamName = new Label("Welcome " + ui.Main.userInfo.getPersonaName(), null);
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
        System.out.println("Collapsing...");
        gameImageViewer.setImage(null);
        expandedScene.setVisible(false);
        expandButton.setVisible(true);
        stage.setWidth(expandButton.getWidth());
        stage.setHeight(expandButton.getHeight());
        System.out.println("Coords - X: " + stage.getX() + " Y: " + stage.getY());
    }

    private void showExpandedScene() {
        System.out.println("Expanding...");
        expandButton.setVisible(false);
        expandedScene.setVisible(true);
        expandedScene.setMinSize(0, 0);
        stage.setWidth(minListWidth);
        stage.setHeight(SCREEN_HEIGHT);
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
        System.out.println(highest);
        container.setPadding(new Insets(labelGameList.getHeight() + 20, 15, 0, 10));
        container.setStyle("-fx-background-color: linear-gradient(to right, rgba(200,200,200,1) 0%, rgba(200,200,200,0.80) 30%,rgba(255,255,255,0.20) 80%, rgba(255,255,255,0.0) 100%)");
        container.setMaxWidth(minListWidth + highest);
        TranslateTransition trans = new TranslateTransition(Duration.seconds(1), expandedScene);
        trans.setFromX(-150);
        trans.setToX(0);
        trans.play();
        System.out.println("Coords - X: " + stage.getX() + " Y: " + stage.getY());
//        System.out.println("Coords - X: "+ stage.getX()+" Y: " + stage.getY());
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
            if (!unCategorized.getCategoryName().equals(tp.getText())) {
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
                        stage.setWidth(SCREEN_WIDTH + 18);
                        gameImageViewer.setImage(null);
                        gameImageViewer.setImage(new Image(game.getGameImage().toURI().toString(), expandedScene.getWidth(), expandedScene.getHeight(), false, false));
                        System.gc(); // fresh the ram, useful for new Image
                        FadeTransition ft = new FadeTransition(Duration.millis(1000), gameImageViewer);
                        ft.setFromValue(0.1);
                        ft.setToValue(1);
                        ft.play();
                        expandedScene.setAlignment(Pos.TOP_LEFT);
                        gameButton.setCursor(javafx.scene.Cursor.HAND);
                        expandedScene.getChildren().removeAll(gameImageViewer);
                        expandedScene.getChildren().add(gameImageViewer);
//                        container.setPrefWidth(minListWidth);
                        gamesList.toFront();
                        gameImageViewer.toBack();
                    }
                });
                gameButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<>() {
                    @Override
                    public void handle(MouseEvent e) {
                        stage.setAlwaysOnTop(false);
                        String op = OsActivities.getOperatingSystem();
                        if (game.checkGameExist()) {
                            if (op.equals("Windows 10") || op.equals("Windows 7")) {
                                try {
                                    ProcessBuilder pb = new ProcessBuilder("cmd", "/c", game.getGameExe().getAbsolutePath());
                                    Main.steamGameDetected = game.getIsSteamGame();
                                    System.out.println("Program starting");
                                    Process proc = pb.start();
//                                    System.out.println(pb.command());

                                    collapseScreen();
                                } catch (IOException ex) {
                                    System.out.println(ex.getMessage());
                                }
                            } else if (op.equals("Linux")) {
                                try {
                                    String shortcut = FileController.getFolderShortcut().toString();
                                    shortcut = shortcut.replace("Game Launcher", "'Game Launcher'");
                                    ProcessBuilder pb = new ProcessBuilder("bash", "-c", "cd " + shortcut + " && ./'" + game.getGameExe().getName() + "'");
                                    Process proc = pb.start();
                                    System.out.println(pb.command());
                                } catch (IOException IOex) {
                                    System.out.println(IOex.getMessage());
                                }
                            } else {
                                System.out.println("Unsupported OS : " + op);
                            }
                        } else {
//                            if (!game.getGameExe().delete()) {
//                                System.out.println("Shortcut couldn't deleted");
//                            }
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Game Launcher");
                            alert.setHeaderText(null);
                            alert.setContentText("Program Not Found: " + game.getGameText());
                            alert.showAndWait();
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
        unCategorized.removeGames();
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
                        unCategorized.addGame(fileEntry, gameText, fileImage);
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