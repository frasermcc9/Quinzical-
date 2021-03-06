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

package quinzical.impl.controllers;

import com.google.inject.Inject;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import quinzical.interfaces.models.SceneHandler;

/**
 * An abstract class that is used by all of the other scenes
 */
public abstract class AbstractSceneController {

    @FXML
    protected AnchorPane background;

    @Inject
    protected SceneHandler sceneHandler;

    private String passedInfo;

    /**
     * Creates an animation that changes the size of a given node
     * to a new size over a given time
     * 
     * @param node The node that the animation will be of
     * @param ms The time for the animation to play over
     * @param toSize The final size of the node after the animation
     */
    protected static void createScaleAnimation(final Node node, final int ms, final double toSize) {
        final ScaleTransition st = new ScaleTransition(Duration.millis(ms));
        st.setNode(node);
        st.setFromX(node.getScaleX());
        st.setFromY(node.getScaleY());
        st.setToX(toSize);
        st.setToY(toSize);
        st.playFromStart();
    }

    /**
     * Adjusts the brightness of a given node over a certain time
     * and to a certain level of brightness
     * 
     * @param node The node that will chang in brightness
     * @param ms The time that the brightness change will occur over
     * @param toLevel The final brightness of the node at the end of the time
     */
    protected static void adjustBrightness(final Node node, final int ms, final double toLevel) {
        final ColorAdjust ca = new ColorAdjust();
        node.setEffect(ca);
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(ca.brightnessProperty(), ca.getBrightness())
            ),
            new KeyFrame(
                Duration.millis(ms),
                new KeyValue(ca.brightnessProperty(), toLevel)
            )
        ).playFromStart();
    }

    protected static void createAnimation(final int ms, final double toLevel, final Property<Number> property) {
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(property, property.getValue())
            ),
            new KeyFrame(
                Duration.millis(ms),
                new KeyValue(property, toLevel)
            )
        ).playFromStart();
    }

    /**
     * Called when the scene is initiated. Calls template methods for onLoad() and refresh().
     */
    @FXML
    public final void initialize() {
        onLoad();
        refresh();
        this.background.getStyleClass().clear();
        this.background.getStyleClass().add(sceneHandler.getActiveTheme().name());
    }

    protected void onLoad() {
    }

    protected void refresh() {
    }

    /**
     * Repeatedly requests focus on a component, since sometimes JavaFX is buggy and doesn't like to request focus with
     * just one try :)
     *
     * @param node    The node to request focus on
     * @param retries Maximum number of times to try request focus before stopping
     */
    protected final void focusRequester(final Node node, final int retries) {
        if (retries == 0) return;
        Platform.runLater(() -> {
            if (!node.isFocused()) {
                node.requestFocus();
                focusRequester(node, retries - 1);
            }
        });
    }

    protected void usePassedData(final String passedInfo) {
    }

    public final void passData(final String data) {
        this.passedInfo = data;
        usePassedData(passedInfo);
    }
}
