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

package quinzical.impl.multiplayer;

import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import quinzical.impl.controllers.StandardSceneController;
import quinzical.impl.multiplayer.models.ActiveGame;
import quinzical.impl.multiplayer.models.Player;
import quinzical.impl.multiplayer.models.SocketModel;


public class RoundEndController extends StandardSceneController {

    private final Socket socket = SocketModel.getInstance().getSocket();
    @FXML
    private Label lblServerAns;
    @FXML
    private Label lblUserAns;
    @FXML
    private TableView<Player> tablePlayers;
    @FXML
    private TableColumn<Player, String> columnName;
    @FXML
    private TableColumn<Player, String> columnPoints;
    @FXML
    private Label lblPoints;

    @Override
    protected void onLoad() {
        columnName.setCellValueFactory(v -> v.getValue().nameProperty());
        columnPoints.setCellValueFactory(v -> v.getValue().scoreProperty());
        tablePlayers.setItems(ActiveGame.getInstance().getPlayers());

        String userAns = ActiveGame.getInstance().getGivenSolution();
        lblUserAns.setText("You answered: " + userAns);


        String newUserAns = ActiveGame.getInstance().getGivenSolution();
        String actualAns = ActiveGame.getInstance().getTrueSolution();
        int points = ActiveGame.getInstance().getPoints();

        Platform.runLater(() -> {
            lblServerAns.setText("The correct answer was: " + actualAns);
            lblUserAns.setText("You answered: " + newUserAns);
            lblPoints.setText("You Have: " + points + " Points");
        });
    }

}

