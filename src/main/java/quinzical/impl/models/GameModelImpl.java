package quinzical.impl.models;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import quinzical.impl.models.structures.GameQuestion;
import quinzical.impl.models.structures.SaveData;
import quinzical.interfaces.events.ActiveQuestionObserver;
import quinzical.interfaces.events.QuestionObserver;
import quinzical.interfaces.events.ValueChangeObserver;
import quinzical.interfaces.models.GameModel;
import quinzical.interfaces.models.GameModelSaver;
import quinzical.interfaces.strategies.questiongenerator.QuestionGeneratorStrategyFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
public class GameModelImpl implements GameModel, GameModelSaver {


    private final List<QuestionObserver> questionObservers = new ArrayList<>();

    private final List<ActiveQuestionObserver> activeObservers = new ArrayList<>();

    private final List<ValueChangeObserver> valueChangeObservers = new ArrayList<>();

    @Inject
    private QuestionGeneratorStrategyFactory questionGeneratorStrategyFactory;

    private Map<String, List<GameQuestion>> boardQuestions;

    private GameQuestion activeQuestion = null;

    private int value = 0;

    @Override
    public int getValue() {
        return value;
    }

    public void increaseValueBy(int number) {
        this.value += number;
        fireValueChange();
    }

    @Override
    public Map<String, List<GameQuestion>> getQuestionsForPracticeMode() {
        return questionGeneratorStrategyFactory.createPracticeQuestionStrategy().generateQuestions();
    }

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
        fireQuestionsUpdate();
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        this.boardQuestions = saveData.getQuestionData();
        this.value = saveData.getValue();

        fireQuestionsUpdate();
        fireValueChange();
    }

    @Override
    public void saveQuestionsToDisk() throws IOException {
        FileOutputStream fileOut = new FileOutputStream(System.getProperty("user.dir") + "/data/save.qdb");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);

        SaveData sd = new SaveData().setQuestionData(this.boardQuestions).setValue(this.value);
        out.writeObject(sd);

        out.close();
        fileOut.close();
    }

    /**
     * Give a game question, returns the next in the category, or null if it is the final question
     *
     * @param question the current game question
     * @return the next game question in category, or null.
     */
    @Override
    public GameQuestion getNextActiveQuestion(GameQuestion question) {
        String category = question.getCategory();
        int index = this.boardQuestions.get(category).indexOf(question);
        if (index == 4) {
            return null;
        } else {
            return this.boardQuestions.get(category).get(index + 1);
        }

    }

    /**
     * Gets the currently active question
     *
     * @return current active question, or null if there isn't one.
     */
    @Override
    public GameQuestion getActiveQuestion() {
        return this.activeQuestion;
    }

    /**
     * Binds a function to the question update event. This event is fired when the question board is updated.
     *
     * @param fn the function to call when the event is fired.
     */
    @Override
    public void onQuestionsUpdate(QuestionObserver fn) {
        questionObservers.add(fn);
    }

    /**
     * Binds a function to the event that is fired when there is a new active question.
     *
     * @param fn the function to call when the event is fired.
     */
    @Override
    public void onActiveQuestionUpdate(ActiveQuestionObserver fn) {
        activeObservers.add(fn);
    }

    /**
     * Binds a function to the event that is fired when there is a new active question.
     *
     * @param fn the function to call when the event is fired.
     */
    @Override
    public void onValueChange(ValueChangeObserver fn) {
        valueChangeObservers.add(fn);
    }

    /**
     * Fire the questions update event.
     */
    @Override
    public void fireQuestionsUpdate() {
        questionObservers.forEach(QuestionObserver::updateQuestionDisplay);
    }

    /**
     * Fire the questions update event.
     */
    @Override
    public void fireValueChange() {
        valueChangeObservers.forEach(ValueChangeObserver::updateValue);
    }

    /**
     * Sets the active question in the game.
     */
    @Override
    public void activateQuestion(GameQuestion question) {
        this.activeQuestion = question;
        activeObservers.forEach(o -> o.fireActiveQuestion(question));
    }

    /**
     * Answers whatever the active question is.
     * <p>
     * Removes the active question, sets it as answered and no longer answerable, and sets the next question in the
     * category as answerable.
     */
    @Override
    public void answerActive(boolean correct) {
        GameQuestion question = this.activeQuestion;
        this.activeQuestion.answer(correct);
        //this.activeQuestion = null;

        GameQuestion next = getNextActiveQuestion(question);
        if (next != null) {
            next.setAnswerable(true);
        }

        if (correct) {
            increaseValueBy(question.getValue());
        }

        fireQuestionsUpdate();

    }


}
