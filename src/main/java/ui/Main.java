package ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.OsActivities;
import main.Steam;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;


public class Main extends Application {
    private static Stage stage;
    //    private final helper.Monitor screen = new Monitor();
    private static ui.Monitor monitor;

    static {
        try {
            monitor = new Monitor();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("monitor setting file couldnt get properly.");
            System.exit(1);
        }
    }

    private static final double SCREEN_WIDTH = monitor.getWidth();
    private static final double SCREEN_HEIGHT = monitor.getHeight();
    public static OsActivities activity = new OsActivities();
    static Steam.SteamUser userInfo;
    static boolean firstSteamUser = false;
    static boolean steamGameDetected;

    public static void main(String[] args) {
        launch(args);
    }

    static Stage getStage() {
        return stage;
    }

    static double getScreenWidth() {
        return SCREEN_WIDTH;
    }

    static double getScreenHeight() {
        return SCREEN_HEIGHT;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        double sceneWidth = SCREEN_WIDTH / 100;
        double sceneHeight = SCREEN_HEIGHT / 55;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/scene.fxml"));
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("Game Launcher");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setX(0);
        stage.setY(0);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.jpg")));
        stage.setIconified(false);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
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
        stage.show();

        // longrunning operation runs on different thread
        Thread activityThread = new Thread(() -> {
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
        });
        // don't let thread prevent JVM shutdown
        activityThread.setDaemon(true);
        activityThread.start();

        Thread steamThread = new Thread(() -> {
            Runnable updater = () -> {
                boolean isSteamUserExists = Steam.isExistsUserProperties();
                System.out.println("status: " + isSteamUserExists);
                if (!isSteamUserExists) {
                    firstSteamUser = true;
                    FXMLController.openSteamUserDialog();
                } else {
                    try {
                        userInfo = Steam.readUserInfoFromFile();
                        firstSteamUser = false;
                        System.out.println(userInfo.getPersonaName());
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            };
            Platform.runLater(updater);
            while (userInfo == null && !firstSteamUser) {
                try {
                    System.out.println("user fetching...");
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        // don't let thread prevent JVM shutdown
        steamThread.setDaemon(true);
        steamThread.start();
    }

}
