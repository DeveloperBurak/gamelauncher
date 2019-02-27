package ui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import main.WindowsActivities;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {
    @FXML
    private Button expandButton;
    @FXML
    private AnchorPane scene;
    @FXML
    private Text reference;
    private Main main = new Main();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        double width = main.returnSceneWidth();
        double height = main.returnSceneHeight();
        scene.setPrefWidth(width);
        scene.setPrefHeight(height);
        expandButton.setPrefWidth(width);
        expandButton.setMaxWidth(width-5);
        expandButton.setPrefHeight(height);
        expandButton.setMaxHeight(height-5);
    }

    public void centered() {
        double width = 0;
        double height = 0;

        try {
            width = scene.getWidth();
            height = scene.getHeight();
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }

        expandButton.setLayoutX(width / 2 - expandButton.getWidth() / 2);
        expandButton.setLayoutY(height / 2 - expandButton.getHeight() / 2);

    }

    public void expandScreen(){
        try{
            main.changeSceneWithButton(scene,"fxml/expanded.fxml");
        }catch (Exception e){
            System.out.println("asas "+e.getMessage() + " | "+ e.getClass().getCanonicalName());
        }
    }
    /*public void hideApp(){
        main.hideApp(scene);
    }*/

}