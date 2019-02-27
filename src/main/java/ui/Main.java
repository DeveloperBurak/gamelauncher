package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.awt.*;
import java.io.File;
import java.net.URL;
import javafx.scene.image.Image; //you should import manually
import javax.imageio.ImageIO;
import java.awt.SystemTray;
import java.awt.TrayIcon;


public class Main extends Application {
    private Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    public double returnScreenWidth() {
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        return screenSize.getWidth();
    }

    public double returnScreenHeight() {
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        return screenSize.getHeight();
    }

    public double returnSceneHeight(){
        return this.returnScreenHeight()/25;
    }
    public double returnSceneWidth(){
        return this.returnScreenWidth()/30;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        double width = this.returnSceneWidth();
        double height = this.returnSceneHeight();

       Parent root = FXMLLoader.load(getClass().getResource("/fxml/scene.fxml"));

        Scene scene = new Scene(root, width, height);
        stage.setTitle("Game Launcher");
        stage.setX(-8);
        stage.setY(-30);
        stage.initStyle(StageStyle.UTILITY);
        stage.setAlwaysOnTop(true);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/instagramicon.jpg")));
        stage.setResizable(false);
        stage.setIconified(false);
        stage.setScene(scene);
        stage.show();
        final SystemTray tray = SystemTray.getSystemTray();
        final TrayIcon trayIcon = new TrayIcon(ImageIO.read(getClass().getResource("/images/instagramicon.jpg"))
                .getScaledInstance(16, 16,2), "Game Launcher");
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    }

    public void changeSceneWithButton(AnchorPane anchorPane, String fxml) throws Exception{
        Parent root = null;
        try{
            root = FXMLLoader.load(getClass().getClassLoader().getResource(fxml));
        }catch (NullPointerException e){
            System.out.println(e.getMessage());
        }
        if(root == null){
            System.out.println("belirtilen yol yok.");
        }else{
            Stage stage = (Stage)anchorPane.getScene().getWindow();
            if(stage == null){
                System.out.println("stage boş");
            }else{
                stage.setScene(new Scene(root));
            }
        }
    }
    /*protected void hideApp(AnchorPane anchorPane){
        Stage stage = (Stage)anchorPane.getScene().getWindow();
        stage.setAlwaysOnTop(false);
    }*/
}
