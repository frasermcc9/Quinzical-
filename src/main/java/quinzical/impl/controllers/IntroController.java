package quinzical.impl.controllers;

import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import quinzical.impl.constants.GameScene;
import quinzical.interfaces.models.SceneHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class IntroController extends PrimaryScene {


    @Inject
    private SceneHandler sceneHandler;

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private ImageView imgTitle;
    @FXML
    private Button btnPractice;
    @FXML
    private Button btnPlay;
    @FXML
    private AnchorPane background;
    @FXML
    private ImageView btnSun;
    @FXML
    private ImageView btnNight;

    @FXML
    void btnPlayClick(ActionEvent event) {
        sceneHandler.setActiveScene(GameScene.GAME);
    }

    @FXML
    void btnPracticeClick(ActionEvent event) {
        sceneHandler.setActiveScene(GameScene.PRACTICE);
    }

    @FXML
    void btnMoonClicked(MouseEvent event) {
        setTheme(Theme.DARK);
    }

    @FXML
    void btnSunClicked(MouseEvent event) {
        setTheme(Theme.LIGHT);
    }

    @FXML
    void initialize() {
        assert imgTitle != null : "fx:id=\"imgTitle\" was not injected: check your FXML file 'intro.fxml'.";
        assert btnPractice != null : "fx:id=\"btnPractice\" was not injected: check your FXML file 'intro.fxml'.";
        assert btnPlay != null : "fx:id=\"btnPlay\" was not injected: check your FXML file 'intro.fxml'.";
    }


    @Override
    protected AnchorPane getBackground() {
        return this.background;
    }
}
