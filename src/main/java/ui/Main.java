package ui;

import helper.Monitor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.FileController;
import main.Steam;
import main.OsActivities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class Main extends Application {
    private static Stage stage;
    private final helper.Monitor screen = new Monitor();
    private final double SCENE_WIDTH = screen.getScreenWidth() / 100;
    private final double SCENE_HEIGHT = screen.getScreenHeight() / 55;
    private static final File monitorSettingFile = new File(FileController.getUserDirectory().getAbsolutePath() + File.separator + "monitor.json");
    public static OsActivities activity = new OsActivities();
    static Steam.SteamUser userInfo;

    static boolean steamGameDetected;

    public static void main(String[] args) {
        launch(args);
    }

    static Stage getStage() {
        return stage;
    }

    Monitor getScreen() {
        return screen;
    }

    double getSceneWidth() {
        return SCENE_WIDTH;
    }

    double getSceneHeight() {
        return SCENE_HEIGHT;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        if (!monitorSettingFile.exists()) {
            FXMLController.openMonitorSelectorDialog();
        } else {
            System.out.println("monitor settings getting...");
        }

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/scene.fxml"));
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("Game Launcher");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setX(0);
        stage.setY(0);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.jpg")));
        stage.setIconified(false);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        System.out.println("starting... coords: " + stage.getX());

        String op = OsActivities.getOperatingSystem();

        if (op.equals("Windows 10") || op.equals("Windows 7")) {

            final SystemTray tray = SystemTray.getSystemTray();
            final TrayIcon trayIcon = new TrayIcon(ImageIO.read(getClass().getResource("/images/icon.jpg"))
                    .getScaledInstance(16, 16, 2), "Game Launcher");
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.out.println("TrayIcon could not be added.");
            }
        }

        // longrunning operation runs on different thread
        Thread activityThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean[] willContinue = {true};
                Runnable updater = new Runnable() {
                    String prevExe = OsActivities.getOpenedProgram();

                    @Override
                    public void run() {
                        String exe = OsActivities.getOpenedProgram();
                        try {
                            if (exe == null) {
                                willContinue[0] = false;
                                Thread.currentThread().interrupt();
                                System.out.println("Activity wont tracking now.");
                            } else {
                                if (!exe.equals(prevExe)) { // check the opened program every 800ms. anchor: $1
                                    prevExe = exe;
                                    System.out.println("Steam Game Detected: " + steamGameDetected);
//                                    if steam game detected, always on top should be false
                                    stage.setAlwaysOnTop(OsActivities.isLegalProgram(prevExe) || !steamGameDetected);
                                }
                            }

                        } catch (NullPointerException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                };
                Platform.runLater(updater);
                while (willContinue[0]) {
                    try {
                        Thread.sleep(800); // $1
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                    // UI update is run on the Application thread
                }
            }
        });
        // don't let thread prevent JVM shutdown
        activityThread.setDaemon(true);
        activityThread.start();


        Thread steamThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Runnable updater = new Runnable() {
                    @Override
                    public void run() {
                        boolean isSteamUserExists = Steam.isExistsUserProperties();
                        if (!isSteamUserExists) {
                            FXMLController.firstSteamUser = true;
                            FXMLController.openSteamUserDialog();
                        } else {
                            try {
                                Steam.readUserInfoFromFile();
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        if (Steam.getUser() != null) {
                            userInfo = Steam.getUser();
                            FXMLController.steamInfoFetched = true;
                        }
                    }
                };
                Platform.runLater(updater);

                while (!FXMLController.steamInfoFetched && !FXMLController.firstSteamUser) {
                    try {
                        System.out.println("user fething...");
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        });
        // don't let thread prevent JVM shutdown
        steamThread.setDaemon(true);
        steamThread.start();


        stage.show();
    }

    static File getMonitorSettingsFile() {
        return monitorSettingFile;
    }
}
