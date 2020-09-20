package quinzical.impl.models;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import quinzical.impl.constants.GameScene;
import quinzical.impl.models.structures.GameQuestion;
import quinzical.interfaces.models.GameModel;
import quinzical.interfaces.models.SceneHandler;
import quinzical.interfaces.strategies.questiongenerator.QuestionGeneratorStrategyFactory;

import java.util.List;
import java.util.Map;

@Singleton
public class GameModelImpl implements GameModel {

    private GameQuestion activeQuestion = null;

    @Inject
    private QuestionGeneratorStrategyFactory questionGeneratorStrategyFactory;

    @Inject
    private SceneHandler sceneHandler;

    private Map<String, List<GameQuestion>> boardQuestions;

    /**
     * Returns map containing the questions for the current game.
     */
    @Override
    public Map<String, List<GameQuestion>> getBoardQuestions() {
        return boardQuestions;
    }

    /**
     * Generates a new set of questions.
     */
    @Override
    public void generateNewGameQuestionSet() {
        this.boardQuestions = questionGeneratorStrategyFactory.createGameQuestionStratgey().generateQuestions();
    }

    /**
     * Sets the active question in the game.
     */
    @Override
    public void activateQuestion(String category, int questionIdx) {
        List<GameQuestion> gameQuestions = boardQuestions.get(category);
        GameQuestion question = gameQuestions.get(questionIdx);

        this.activeQuestion = question;
        sceneHandler.setActiveScene(GameScene.GAME_QUESTION);
    }

}
