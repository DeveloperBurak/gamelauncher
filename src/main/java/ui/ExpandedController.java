package ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ExpandedController implements Initializable {
    @FXML
    private Button expandButton;
    @FXML
    private Label labelGameList;
    @FXML
    private AnchorPane expandedScene;
    @FXML
    private VBox gamesList;
    private Main main = new Main();
    private boolean isClicked = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        double width = main.returnScreenWidth();
        double height = main.returnScreenHeight();
        expandedScene.setPrefWidth(width / 10);
        expandedScene.setPrefHeight(height);
    }

    public boolean setIsClicked() {
        if (this.isClicked) {
            this.isClicked = false;
        } else {
            this.isClicked = true;
        }
        return this.isClicked;
    }

    public void collapseScreen() {
        if (!this.isClicked) {
            try {
                main.changeSceneWithButton(expandedScene, "fxml/scene.fxml");
            } catch (Exception e) {
                System.out.println("asas " + e.getMessage() + " | " + e.getClass().getCanonicalName());
            }
        }
    }
}