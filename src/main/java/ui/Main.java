package ui;

import activities.ProgramHandler;
import activities.OS;
import activities.SteamGameHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.SteamAPI;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.prefs.BackingStoreException;


public class Main extends Application {
    private static Stage stage;
    private static ui.Monitor monitor;
    private static double SCREEN_WIDTH, SCREEN_HEIGHT; // these sizes of monitor
    private static double firstSceneWidth, firstSceneHeight; // these size of
    public static ProgramHandler activity = new ProgramHandler();
    static SteamAPI.SteamUser userInfo;
    static boolean firstSteamUser = false;
//    static boolean steamGameDetected;

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

    static double getFirstSceneWidth() {
        return firstSceneWidth;
    }

    static double getFirstSceneHeight() {
        return firstSceneHeight;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        monitor = new Monitor();
        SCREEN_HEIGHT = monitor.getHeight();
        SCREEN_WIDTH = monitor.getWidth();
        firstSceneWidth = SCREEN_WIDTH / 50;
        firstSceneHeight = SCREEN_HEIGHT / 25;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/scene.fxml"));
        Scene scene = new Scene(root, firstSceneWidth, firstSceneHeight);
        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("Game Launcher");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setX(0);
        stage.setY(0);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.jpg")));
        stage.setIconified(false);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.setWidth(getFirstSceneWidth());
        stage.setHeight(getFirstSceneHeight());
        String op = OS.getOperatingSystem();
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

        // this tracks which program is active
        Thread activityThread = new Thread(() -> {
            final boolean[] willContinue = {true};
            Runnable updater = new Runnable() {
                String prevExe = ProgramHandler.getActiveProgram();

                @Override
                public void run() {
                    String exe = ProgramHandler.getActiveProgram();

                    try {
//                        System.out.println("test");
                        if (exe == null) {
                            willContinue[0] = false;
                            Thread.currentThread().interrupt();
                            System.out.println("Activity wont tracking now.");
                        } else {
                            if (!exe.equals(prevExe)) { // check the opened program every 800ms. anchor: $1
                                prevExe = exe;
//                                    if steam game detected, always on top should be false
                                // TODO make this adjustable by user preferences

                            }
                            try {
                                stage.setAlwaysOnTop(ProgramHandler.isLegalProgram(prevExe) && !SteamGameHandler.isStillRunningSteamGame());
                            } catch (BackingStoreException | IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (NullPointerException e) {
                        System.out.println(e.getMessage());
                    }
                }
            };
//            Platform.runLater(updater);

            System.out.println(willContinue[0]);
            while (willContinue[0]) {
                try {
                    Thread.sleep(10000); // $1
                    Platform.runLater(updater);

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
                boolean isSteamUserExists = SteamAPI.isExistsUserProperties();
                System.out.println("status: " + isSteamUserExists);
                if (!isSteamUserExists) {
                    firstSteamUser = true;
                    FXMLController.openSteamUserDialog();
                } else {
                    try {
                        userInfo = SteamAPI.readUserInfoFromFile();
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
