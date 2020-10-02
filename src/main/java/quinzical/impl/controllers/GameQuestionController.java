package quinzical.impl.controllers;

import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import quinzical.impl.constants.GameScene;
import quinzical.impl.models.structures.GameQuestion;
import quinzical.impl.util.questionparser.Solution;
import quinzical.impl.util.strategies.questionverifier.VerifierType;
import quinzical.interfaces.models.GameModel;
import quinzical.interfaces.models.QuinzicalModel;

import java.util.List;

public class GameQuestionController extends AbstractQuestionController {

    //#region Injected classes

    @Inject
    private GameModel gameModel;


    //#endregion

    //#region Injected FXML components

    @FXML
    private Label lblPrompt;

    @FXML
    private Button btnPass;

    //#endregion


    //#region Initialisation at FXML load


    @FXML
    void initialize() {
        listen();
        initMacronButtons();
    }


    //#endregion

    //#region Injected handlers

    @FXML
    void onReplyClick() {
        String s = gameModel.getActiveQuestion().getHint();
        speaker.speak(s);
    }

    @FXML
    void onPassClicked() {
        onSubmitClicked();
    }

    @Override
    protected QuinzicalModel getGameModel() {
        return this.gameModel;
    }

    @Override
    protected void setPrompts(String hint, String prompt) {
        this.lblPrompt.setText(prompt);
    }

    @FXML
    void onSubmitClicked() {
        GameQuestion question = gameModel.getActiveQuestion();

        List<Solution> solutions = question.getSolutionsCopy();

        textAreas.forEach(textArea -> textArea.setEditable(false));

        List<Boolean> corrects =
            questionVerifierFactory.getQuestionVerifier(VerifierType.FILL_SOLUTION).verifySolutions(solutions,
                textAreas);

        gameModel.answerActive(corrects.stream().allMatch(e -> e));


        btnSubmit.setText("Categories");
        btnSubmit.setOnAction(this::handleReturnToCategories);
        btnPass.setText("Next Question");
        btnPass.setOnAction(e -> handleNextQuestion(question));

        btnPass.requestFocus();
    }

    //#endregion


    @Override
    protected void adjustQuestionOnLoad() {
        gameModel.answerActive(false);
    }

    private void handleReturnToCategories(ActionEvent e) {
        refreshSubmissionButtonsState();

        if (gameModel.numberOfQuestionsRemaining() == 0) {
            handleCompletion();
            return;
        }

        sceneHandler.setActiveScene(GameScene.GAME);
    }

    private void handleNextQuestion(GameQuestion question) {
        refreshSubmissionButtonsState();

        if (gameModel.numberOfQuestionsRemaining() == 0) {
            handleCompletion();
            return;
        }

        GameQuestion next = gameModel.getNextActiveQuestion(question);
        if (next == null) {
            sceneHandler.setActiveScene(GameScene.GAME);
        } else {
            gameModel.activateQuestion(next);
        }

    }

    private void refreshSubmissionButtonsState() {
        btnSubmit.setOnAction(e -> onSubmitClicked());
        btnPass.setOnAction(e -> onSubmitClicked());
        btnPass.setText("Pass");
        btnSubmit.setText("Submit");
    }

    private void onTextAreaKeyPress(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            if (e.getSource() instanceof TextArea) {
                TextArea ta = (TextArea) e.getSource();
                ta.setText(ta.getText().trim());
                int idx = textAreas.indexOf(ta);
                if (idx + 1 == textAreas.size()) {
                    btnSubmit.fire();
                    btnPass.requestFocus();
                } else {
                    textAreas.get(idx + 1).requestFocus();
                }
            }

        }
    }

    private void handleCompletion() {
        sceneHandler.setActiveScene(GameScene.END);
    }

}
