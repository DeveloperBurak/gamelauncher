package ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Steam;
import main.WindowsActivities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;


public class Main extends Application {
    private static Stage stage;
    private final Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    public static WindowsActivities activity = new WindowsActivities();
    static Steam.SteamUser userInfo;
    static boolean steamGameDetected;

    public static void main(String[] args) {
        launch(args);
    }

    static Stage getStage() {
        return stage;
    }

    double returnScreenWidth() {
        return screenSize.getWidth();
    }

    double returnScreenHeight() {
        return screenSize.getHeight();
    }

    final double returnSceneHeight() {
        return screenSize.getHeight() / 55;
    }

    final double returnSceneWidth() {
        return screenSize.getWidth() / 100;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        final double sceneWidth = this.returnSceneWidth();
        final double sceneHeight = this.returnSceneHeight();
//        System.out.println("width:" + sceneWidth + "\n" + "height"+sceneHeight);

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/scene.fxml"));
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("Game Launcher");
        stage.initStyle(StageStyle.TRANSPARENT);
//        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();

        stage.setX(0);
        stage.setY(0);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.jpg")));
        stage.setIconified(false);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        String op = WindowsActivities.getOperatingSystem();

        if (op.equals("Windows 10") || op.equals("Windows 7")) {

            final SystemTray tray = SystemTray.getSystemTray();
            final TrayIcon trayIcon = new TrayIcon(ImageIO.read(getClass().getResource("/images/icon.jpg"))
                    .getScaledInstance(16, 16, 2), "Game Launcher");
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.out.println("TrayIcon could not be added.");
            }

            // longrunning operation runs on different thread
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Runnable updater = new Runnable() {
                        String prevExe = WindowsActivities.getOpenedProgram();

                        @Override
                        public void run() {
                            String exe = WindowsActivities.getOpenedProgram();
                            try {
                                if (!exe.equals(prevExe)) { // check the opened program every 800ms. anchor: $1
                                    prevExe = exe;
                                    System.out.println("Steam Game Detected: "+steamGameDetected);
                                    System.out.println("Exe: "+exe);
//                                    if steam game detected, always on top should be false
                                    stage.setAlwaysOnTop(WindowsActivities.isLegalProgram(prevExe) || !steamGameDetected);
                                }
                            } catch (NullPointerException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    };

                    while (true) {
                        try {
                            Thread.sleep(800); // $1
                        } catch (InterruptedException ex) {
                            System.out.println(ex.getMessage());
                        }
                        // UI update is run on the Application thread
                        Platform.runLater(updater);
                    }
                }
            });
            // don't let thread prevent JVM shutdown
            thread.setDaemon(true);
            thread.start();
        }


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
                        }else{
                            try{
                                Steam.readUserInfoFromFile();
                            }catch (IOException e){
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
        System.out.println("Activated ui Main init");
        stage.show();
    }
}
