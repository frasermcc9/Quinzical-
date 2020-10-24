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

package quinzical.impl.models.structures;

import com.google.inject.Inject;
import quinzical.impl.constants.Theme;
import quinzical.interfaces.models.GameModel;
import quinzical.interfaces.models.SceneHandler;
import quinzical.interfaces.models.structures.PersistentSettings;
import quinzical.interfaces.models.structures.SpeakerMutator;
import quinzical.interfaces.strategies.objectreader.ObjectReaderStrategy;
import quinzical.interfaces.strategies.objectreader.ObjectReaderStrategyFactory;

import java.io.Serializable;

public class PersistentSettingsImpl implements Serializable, PersistentSettings {

    @Inject
    private transient ObjectReaderStrategyFactory objectReaderStrategyFactory;
    @Inject
    private transient SpeakerMutator speakerMutator;
    @Inject
    private transient SceneHandler sceneHandler;
    @Inject
    private transient GameModel gameModel;

    private Theme theme = Theme.FIELDS;

    private int gap = 0;
    private int speed = 175;
    private int amp = 100;
    private int pitch = 50;

    private double timer = 25;

    public PersistentSettings loadSettingsFromDisk() {
        ObjectReaderStrategy<PersistentSettings> strategy = objectReaderStrategyFactory.createObjectReader();

        try {
            PersistentSettings settings = strategy.readObject(System.getProperty("user.dir") + "/data/preferences.qdb");

            theme = settings.getTheme();
            gap = settings.getGap();
            speed = settings.getSpeed();
            amp = settings.getAmp();
            pitch = settings.getPitch();
            timer = settings.getTimer();
        } catch (Exception e) {
            //dont really care if this happens. It will just use default settings.
        }

        return this;
    }

    public void applySettings() {
        this.gameModel.setTimerValue(timer);

        this.speakerMutator.setGap(gap);
        this.speakerMutator.setAmplitude(amp);
        this.speakerMutator.setPitch(pitch);
        this.speakerMutator.setSpeed(speed);

        this.sceneHandler.fireBackgroundChange(theme);
    }

    public PersistentSettings loadSettingsFromGame() {
        timer = gameModel.getTimerValue();

        gap = speakerMutator.getGap();
        amp = speakerMutator.getAmplitude();
        pitch = speakerMutator.getPitch();
        speed = speakerMutator.getSpeed();

        theme = sceneHandler.getActiveTheme();

        return this;
    }

    @Override
    public Theme getTheme() {
        return theme;
    }

    @Override
    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    @Override
    public int getGap() {
        return gap;
    }

    @Override
    public void setGap(int gap) {
        this.gap = gap;
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public int getAmp() {
        return amp;
    }

    @Override
    public void setAmp(int amp) {
        this.amp = amp;
    }

    @Override
    public int getPitch() {
        return pitch;
    }

    @Override
    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    @Override
    public double getTimer() {
        return timer;
    }

    @Override
    public void setTimer(double timer) {
        this.timer = timer;
    }
}
