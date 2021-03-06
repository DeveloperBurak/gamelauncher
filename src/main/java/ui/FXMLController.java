package ui;

import apps.Category;
import apps.Game;
import com.google.gson.Gson;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import helper.FileHelper;
import helper.Monitors;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.FileController;
import main.SteamAPI;
import system.SteamGameHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static ui.Main.monitor;

public class FXMLController implements Initializable {
    @FXML
    private Button expandButton;
    @FXML
    private AnchorPane scene;
    @FXML
    private StackPane expandedScene;
    @FXML
    private VBox container;
    private static final String WHITE_TEXT_CLASS = "white-text";
    private static final String TRANSPARENT_CLASS = "transparent";
    private static final String LIST_PADDING_CLASS = "list-padding";
    /**
     * this will be clean after buttons generated.
     */
    private static ArrayList<Category> categories = new ArrayList<>();
    private static Category unCategorized = new Category("Uncategorized");
    private Stage stage = ui.Main.getStage();
    private static final double SCREEN_WIDTH = Main.getScreenWidth();
    private static final double SCREEN_HEIGHT = Main.getScreenHeight();
    private double minListWidth; // default width.
    private static final File folder = FileController.getFolder();
    private static final File folderShortcut = FileController.getFolderShortcut();
    private static ImageView gameImageViewer = new ImageView();
    private VBox gamesList = new VBox();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (SCREEN_WIDTH < 1400) {
            minListWidth = SCREEN_WIDTH / 7;
        } else if (SCREEN_WIDTH > 1401 && SCREEN_WIDTH < 2048) {
            minListWidth = SCREEN_WIDTH / 9;
        } else {
            minListWidth = SCREEN_WIDTH / 11;
        }
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/style.css")).toExternalForm());
        expandButton.minWidthProperty().bind(stage.widthProperty().multiply(0.4));
        expandButton.prefWidthProperty().bind(stage.widthProperty().multiply(0.2));
        expandButton.prefHeightProperty().bind(stage.heightProperty().multiply(0.2));
        expandButton.setBackground(new Background(new BackgroundFill(Color.GRAY.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        expandButton.setAlignment(Pos.CENTER);
        FontAwesomeIconView barsIcon = new FontAwesomeIconView();
        barsIcon.setStyleClass("bars");
        expandButton.setGraphic(barsIcon);
        setStyles();
        expandedScene.setVisible(false);
        container.getChildren().add(gamesList);
        categories.add(unCategorized);
        addToListFromRoot();
        generateButton(categories);
        Thread labelSetThread = new Thread(() -> {
            Runnable updater = () -> {
                if (Main.userInfo != null) {
                    setSteamUserLabel();
                }
            };
            while (Main.userInfo == null) {
                try {
                    System.out.println("listening for steam name label...");
                    Thread.sleep(800);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            Platform.runLater(updater);// UI update is run on the Application thread
        });

        labelSetThread.setDaemon(true); // don't let thread prevent JVM shutdown
        labelSetThread.start();

        expandButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> showExpandedScene());
        gameImageViewer.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEvent -> collapseScreen());

        this.setRefreshButton();

    }

    private void setRefreshButton() {
        Button refreshButton = new Button();
        FontAwesomeIconView refreshIcon = new FontAwesomeIconView();
        refreshIcon.setGlyphSize(24);
        refreshIcon.setStyleClass("refresh");
        refreshButton.setGraphic(refreshIcon);
        refreshButton.getStyleClass().add(TRANSPARENT_CLASS);
        container.getChildren().add(0, refreshButton);
        RotateTransition rt = new RotateTransition(Duration.millis(2000), refreshIcon);

        refreshButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            rt.setFromAngle(0);
            rt.setToAngle(180);
//            rt.setInterpolator(Interpolator.LINEAR);
            rt.setCycleCount(Timeline.INDEFINITE);
            rt.play();
        });

        refreshButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            rt.setToAngle(rt.getByAngle());
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
            ChoiceDialog<String> dialogMonitorScreen = new ChoiceDialog<>(monitorList.get(0), monitorList);
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
                try (FileWriter writer = new FileWriter(ui.Monitor.getMonitorSettingsFile())) {
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
        if (Main.firstSteamUser) {
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
                    userInput.ifPresent(SteamAPI::initUser);
                }
            });
        }
    }

    private void setSteamUserLabel() {
        try {
            Label labelSteamName = new Label("Welcome " + Main.userInfo.getPersonaName(), null);
            container.getChildren().add(1, labelSteamName);
            labelSteamName.setStyle("-fx-font-weight: bold;");
            labelSteamName.getStyleClass().add(WHITE_TEXT_CLASS);
            labelSteamName.getStyleClass().add(LIST_PADDING_CLASS);
        } catch (NullPointerException e) {
            System.out.println("user info is null");
        }

    }

    private void setStyles() {
        centerItems(expandButton);
    }

    private static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) { // these searches deeply all
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
        stage.setWidth(Main.getFirstSceneWidth());
        stage.setHeight(Main.getFirstSceneHeight());
        expandButton.minWidthProperty().bind(stage.widthProperty().multiply(0.4));
        expandButton.prefWidthProperty().bind(stage.widthProperty().multiply(0.2));
        expandButton.prefHeightProperty().bind(stage.heightProperty().multiply(0.2));
        expandButton.setVisible(true);
    }

    private void showExpandedScene() {
        expandButton.minWidthProperty().unbind();
        expandButton.prefWidthProperty().unbind();
        expandButton.prefHeightProperty().unbind();
        expandedScene.setVisible(true);
        expandedScene.setMinSize(0, 0);
        stage.setWidth(SCREEN_WIDTH + 18);
        stage.setHeight(SCREEN_HEIGHT);
        ArrayList<Node> nodes = getAllNodes(gamesList);
        double highest = 0;
        for (Node node : nodes) {
            if (node instanceof VBox) {
                for (int j = 0; j < ((VBox) node).getChildren().size(); j++) {
                    if (((VBox) node).getChildren().get(j) instanceof Button) {
                        Button btn = ((Button) ((VBox) node).getChildren().get(j));
                        double width = btn.getWidth();
                        if (width > highest) {
                            highest = width;
                        }
                    }
                }
            }
        }
//        container.setPadding(new Insets(labelGameList.getHeight() + 20, 15, 0, 10));
        container.setStyle("-fx-background-color: linear-gradient(to right, rgba(33,33,33,1) 0%, rgba(33,33,33,0.70) 30%,rgba(66,66,66,0.20) 80%, rgba(66,66,66,0.0) 100%)");
        container.prefWidthProperty().bind(stage.widthProperty().multiply(0.2));
        container.maxWidthProperty().bind(stage.widthProperty().multiply(0.2));

        //Instantiating FadeTransition class
        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(1000));
        fade.setFromValue(10);
        fade.setToValue(0.1);
        fade.setCycleCount(1);
        fade.setNode(expandButton);
        fade.play();
        fade.onFinishedProperty().set(event -> {
            expandButton.setVisible(false);
            expandButton.setOpacity(1);
        });
        TranslateTransition trans = new TranslateTransition(Duration.seconds(1), expandedScene);
        trans.setFromX(stage.getWidth() / 2 * -1);
        trans.setToX(0);
        trans.play();
    }

    private void generateButton(ArrayList<Category> categories) {
        for (Category category : categories) {
            TitledPane tp = new TitledPane(category.getCategoryName(), null);
            tp.setText(category.getCategoryName());
            tp.getStyleClass().add(TRANSPARENT_CLASS);
            if (!unCategorized.getCategoryName().equals(tp.getText())) {
                tp.setExpanded(false);
            } else {
                tp.setExpanded(true);
            }
            gamesList.getChildren().add(tp);
            final VBox vbox = new VBox(0);
            ArrayList<Game> games = category.getGames();
            for (Game game : games) {
                Button gameButton = new Button();
                gameButton.getStyleClass().add("game-button");
                gameButton.setText(game.getGameText());
                gameButton.getStyleClass().add(TRANSPARENT_CLASS);
                gameButton.setAlignment(Pos.BASELINE_LEFT);
                gameButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                    stage.setWidth(SCREEN_WIDTH + 18);
                    gameImageViewer.setImage(null);
                    gameImageViewer.setImage(new Image(game.getGameImage().toURI().toString(), SCREEN_WIDTH, SCREEN_HEIGHT, false, false));
                    System.gc(); // fresh the ram, useful for new Image
                    FadeTransition ft = new FadeTransition(Duration.millis(1000), gameImageViewer);
                    ft.setFromValue(0.1);
                    ft.setToValue(1);
                    ft.play();
                    container.getStyleClass().add(TRANSPARENT_CLASS);
                    expandedScene.setAlignment(Pos.TOP_LEFT);
                    gameButton.setCursor(javafx.scene.Cursor.HAND);
                    expandedScene.getChildren().removeAll(gameImageViewer);
                    expandedScene.getChildren().add(gameImageViewer);
                    gamesList.toFront();
                    gameImageViewer.toBack();
                });
                gameButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    collapseScreen();
                    if (game.checkGameExist()) {
                        if (game.run()) {
                            if (game.isSteamGame()) {
                                SteamGameHandler.addRunningSteamGame(game.getSteamID());
                            }
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Game Launcher");
                            alert.setHeaderText(null);
                            alert.setContentText("Program Couldnt start");
                            alert.showAndWait();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Game Launcher");
                        alert.setHeaderText(null);
                        alert.setContentText("Program Not Found: " + game.getGameText());
                        alert.showAndWait();
                    }

                });
                vbox.getChildren().add(gameButton);
                tp.setContent(vbox);
            }
        }
        categories.clear();
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