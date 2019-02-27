package ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.WindowsActivities;

import javax.imageio.ImageIO;
import java.awt.*;


public class Main extends Application{
    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    static Stage getStage() {
        return stage;
    }

    static void setStage(Stage stage) {
        Main.stage = stage;
    }

    double returnScreenWidth() {
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        return screenSize.getWidth();
    }

    double returnScreenHeight() {
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        return screenSize.getHeight();
    }

    double returnSceneHeight() {
        return this.returnScreenHeight() / 25;
    }

    double returnSceneWidth() {
        return this.returnScreenWidth() / 100;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        double width = this.returnSceneWidth();
        double height = this.returnSceneHeight();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/scene.fxml"));
        Scene scene = new Scene(root, width, height);
        stage.setTitle("Game Launcher");
        stage.setX(-8);
        stage.setY(-30);
        stage.initStyle(StageStyle.UTILITY);
//        stage.setAlwaysOnTop(true);

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/instagramicon.jpg")));
        stage.setResizable(false);
        stage.setIconified(false);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.setOpacity(1);

        stage.show();
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
                                System.out.println("isLegal : " + status);
                                stage.setAlwaysOnTop(status);
                                if(status){
                                    stage.setOpacity(1);
                                }else{
                                    stage.setOpacity(0);
                                }

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

    void changeSceneWithButton(String fxml) throws Exception {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource(fxml));
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        if (root == null) {
            System.out.println("belirtilen yol yok.");
        } else {
            if (stage == null) {
                System.out.println("stage boş");
            } else {
                stage.setScene(new Scene(root));
            }
        }
    }
}
