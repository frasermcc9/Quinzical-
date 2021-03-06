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

import com.google.inject.Inject;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.json.JSONArray;
import org.json.JSONException;
import quinzical.impl.constants.GameScene;
import quinzical.impl.controllers.AbstractSceneController;
import quinzical.impl.multiplayer.models.MultiplayerGame;
import quinzical.interfaces.models.SceneHandler;
import quinzical.interfaces.multiplayer.SocketModel;

public class MenuController extends AbstractSceneController {

    @Inject
    SceneHandler sceneHandler;
    @Inject
    private SocketModel socketModel;
    @FXML
    private TextField txtCode;
    @FXML
    private Label lblName;

    @Override
    protected final void onLoad() {
        lblName.setText(socketModel.getName());
    }

    @FXML
    final void btnBrowse(final ActionEvent event) {
        sceneHandler.setActiveScene(GameScene.MULTI_BROWSE);
    }

    @FXML
    final void btnHost(final ActionEvent event) {
        sceneHandler.setActiveScene(GameScene.MULTI_HOST);
    }

    @FXML
    final void btnJoin() {
        final Socket socket = socketModel.getSocket();
        final String name = socketModel.getName();
        final String code = txtCode.getText().toUpperCase();
        socket.emit("joinGameRequest", name, code);
        socket.once("joinGameNotification", (arg) -> {
            if (arg[0].equals(false)) {
                txtCode.setPromptText(arg[1] + "");
                txtCode.setText("");
            } else {
                final JSONArray jsonArray = (JSONArray) arg[1];
                final MultiplayerGame mg = MultiplayerGame.getInstance();
                mg.setCode(code);
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        mg.addPlayer((String) jsonArray.get(i));
                    } catch (final JSONException e) {
                        e.printStackTrace();
                    }
                }
                sceneHandler.setActiveScene(GameScene.MULTI_PLAYER_WAIT);
            }
        });
    }

    @FXML
    final void btnProfile(final ActionEvent event) {
        new Thread(() -> sceneHandler.setActiveScene(GameScene.MULTI_PROFILE)).start();
    }
    
    @FXML
    final void btnLeaderboard(final ActionEvent event) {
        new Thread(() -> sceneHandler.setActiveScene(GameScene.MULTI_LEADERBOARD)).start();
    }


    @FXML
    final void btnQuit(final ActionEvent event) {
        Platform.runLater(() -> socketModel.destroy());
        sceneHandler.setActiveScene(GameScene.MULTI_INTRO);
    }

}
