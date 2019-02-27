package ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ExpandedController implements Initializable{
    @FXML
    private Button expandButton;
    @FXML
    private Label labelGameList;
    @FXML
    private AnchorPane expandedScene;
    private Main main = new Main();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        double width = main.returnSceneWidth();
        double height = main.returnScreenHeight();
        expandedScene.setPrefWidth(width*3);
        expandedScene.setPrefHeight(height);
    }
    public void collapseScreen(){
        try{
            main.changeSceneWithButton(expandedScene,"fxml/scene.fxml");
        }catch (Exception e){
            System.out.println("asas "+e.getMessage() + " | "+ e.getClass().getCanonicalName());
        }
    }
}