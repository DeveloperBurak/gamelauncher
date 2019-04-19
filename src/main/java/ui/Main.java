package ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.WindowsActivities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;


public class Main extends Application {
    private static Stage stage;
    private final Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    private final String OperatingSystem = System.getProperty("os.name");

    public static void main(String[] args) {
        launch(args);
    }

    static Stage getStage() {
        return stage;
    }

    String getOperatingSystem() {
        return OperatingSystem;
    }

    static void setStage(Stage stage) {
        Main.stage = stage;
    }

    double returnScreenWidth() {
        return screenSize.getWidth();
    }

    double returnScreenHeight() {
        return screenSize.getHeight();
    }

    final double returnSceneHeight() {
        return screenSize.getHeight() / 35;
    }

    final double returnSceneWidth() {
        return screenSize.getWidth() / 45;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        final double sceneWidth = this.returnSceneWidth();
        final double sceneHeight = this.returnSceneHeight();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/scene.fxml"));
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        stage.setTitle("Game Launcher");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setX(0);
        stage.setY(0);
        stage.setHeight(sceneHeight);
        stage.setWidth(sceneWidth);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.jpg")));
        stage.setIconified(false);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.show();
        String op = this.getOperatingSystem();

        final SystemTray tray = SystemTray.getSystemTray();
        final TrayIcon trayIcon = new TrayIcon(ImageIO.read(getClass().getResource("/images/icon.jpg"))
                .getScaledInstance(16, 16, 2), "Game Launcher");
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }

      if (op.equals("Windows 10") || op.equals("Windows 7")) {

            final SystemTray tray = SystemTray.getSystemTray();
            final TrayIcon trayIcon = new TrayIcon(ImageIO.read(getClass().getResource("/images/instagramicon.jpg"))
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
                        String prevExe = WindowsActivities.checkOpen();

                        @Override
                        public void run() {
                            String exe = WindowsActivities.checkOpen(); //start the check open
                            try {
                                if (!exe.equals(prevExe)) {
                                    prevExe = exe;
                                    boolean status = WindowsActivities.getIsLegal();
                                    stage.setAlwaysOnTop(status);
                                    System.out.println(exe);
                                }
                            } catch (NullPointerException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    };

                    while (true) {
                        try {
                            Thread.sleep(200);
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
//       task.run();
        }


    }

    void changeSceneWithButton(String fxml) throws Exception {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource(fxml));
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        if (root != null) {
            if (stage == null) {
                System.out.println("belirtilen yol yok.");
                System.out.println("stage boş");
            } else {
                stage.setScene(new Scene(root));
            }
        }
    }
}
