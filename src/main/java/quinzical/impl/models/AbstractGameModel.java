// Copyright 2020 Fraser McCallum and Braden Palmer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//  
//     http://www.apache.org/licenses/LICENSE-2.0
//  
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package quinzical.impl.models;

import com.google.inject.Inject;
import quinzical.impl.models.structures.GameQuestion;
import quinzical.interfaces.events.ActiveQuestionObserver;
import quinzical.interfaces.models.QuinzicalModel;
import quinzical.interfaces.strategies.questiongenerator.QuestionGeneratorStrategyFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGameModel implements QuinzicalModel {

    /**
     * List of functions (observers) that are executed when the game board updates.  Adding (subscribing) to the list is
     * done with {@link this#onActiveQuestionUpdate}, which will add the given ActiveQuestionObserver to the list. When
     * {@link this#fireActiveQuestionUpdate} )} is called, all functions will be executed.
     */
    protected final List<ActiveQuestionObserver> activeObservers = new ArrayList<>();

    /**
     * Factory for creating strategies for generating the questions for the board.
     */
    @Inject
    protected QuestionGeneratorStrategyFactory questionGeneratorStrategyFactory;

    /**
     * Most recent question that was active, or null if no question was active. Potential bug: is null when loading game
     * from save.
     */
    protected GameQuestion activeQuestion = null;

    /**
     * Gets the currently active question
     *
     * @return current active question, or null if there isn't one.
     */
    @Override
    public final GameQuestion getActiveQuestion() {
        return this.activeQuestion;
    }

    /**
     * Sets the active question in the game.
     */
    @Override
    public final void activateQuestion(final GameQuestion question) {
        this.activeQuestion = question;
        fireActiveQuestionUpdate();
    }

    @Override
    public final void onActiveQuestionUpdate(final ActiveQuestionObserver fn) {
        activeObservers.add(fn);
    }

    protected final void fireActiveQuestionUpdate() {
        this.activeObservers.forEach(ActiveQuestionObserver::fireActiveQuestion);
    }

    /**
     * Gets the list of categories that are available in the game currently.
     *
     * @return - The list of all categories, expressed as strings that are the names of each category.
     */
    public final List<String> getCategories() {
        return new ArrayList<>(questionGeneratorStrategyFactory.createPracticeQuestionStrategy().generateQuestions().keySet());
    }
}
