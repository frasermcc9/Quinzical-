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

import com.google.inject.Singleton;
import javafx.application.Platform;

/**
 * An extension of SpeakerManager that is used when the OS is windows, meaning that festival cannot be used
 */
@Singleton
public class WindowsSpeakerManager extends SpeakerManager {

    /**
     * "Speaks" the inputted text (prints it out)
     *
     * @param text the text to speak
     */
    @Override
    public final void speak(final String text, final Runnable callback) {
        execute(text, callback);
    }

    @Override
    public final void speak(final String text) {
        speak(text, () -> {
        });
    }

    private void execute(final String text, final Runnable runnable) {
        new Thread(() -> {
            System.out.println("Operating System is windows. Printing TTS: " + text);
            try {
                Thread.sleep(2000);
                Platform.runLater(runnable);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Prints out the inputted pitch value, ensuring that this function is called when it should be
     *
     * @param pitch - The pitch that the speaker will talk in (how high or low the voice sounds)
     */
    @Override
    public final void setPitch(final int pitch) {
        super.setPitch(pitch);
        System.out.println("Operating System is windows. Printing new pitch: " + pitch);
    }

    /**
     * Prints out the inputted amplitude, ensuring that this function is called when it should be
     *
     * @param amplitude - The amplitude to be set (how loud the voice is)
     */
    @Override
    public final void setAmplitude(final int amplitude) {
        super.setAmplitude(amplitude);
        System.out.println("Operating System is windows. Printing new amplitude: " + amplitude);
    }

    /**
     * Prints out the inputted reading speed, ensuring that this function is called when it should be
     *
     * @param speed - Reading speed (recommended range between 80 ~ 500)
     */
    @Override
    public final void setSpeed(final int speed) {
        super.setSpeed(speed);
        System.out.println("Operating System is windows. Printing new speed: " + speed);
    }

    /**
     * Prints out the inputted gap, ensuring that this function is called when it should be
     *
     * @param gap - How long to wait between each word spoken
     */
    @Override
    public final void setGap(final int gap) {
        super.setGap(gap);
        System.out.println("Operating System is windows. Printing new gap: " + gap);
    }
}
