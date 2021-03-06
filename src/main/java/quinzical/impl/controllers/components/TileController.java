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

package quinzical.impl.controllers.components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class TileController {
    @FXML
    private StackPane parent;

    @FXML
    private VBox container;

    @FXML
    private StackPane header;

    @FXML
    private StackPane content;

    @FXML
    private Label contentLabel;

    @FXML
    private Label lblMarker;

    private static String colourFromInt(final int i) {
        return Integer.toHexString(i).substring(0, 6);
    }

    private void setHeaderColourFromString(final String anything) {
        final String hex = colourFromInt(anything.hashCode());
        final Color base = Color.web(hex);
        final double hue = base.getHue();
        final double sat = 0.8;
        final double val = 0.9;
        final Color enhanced = Color.hsb(hue, sat, val);

        final String newHex = String.format("#%02X%02X%02X",
            (int) (enhanced.getRed() * 255),
            (int) (enhanced.getGreen() * 255),
            (int) (enhanced.getBlue() * 255));

        header.setStyle("-fx-background-color: " + newHex);
    }

    public final TileController setContent(final String content, final String ratio) {
        contentLabel.setText(content);
        setHeaderColourFromString(content);

        this.lblMarker.setText(ratio);
        return this;
    }

    public final String getHeader() {
        return this.contentLabel.getText();
    }
}
