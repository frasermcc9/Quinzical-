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

import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import quinzical.Entry;

import java.io.IOException;

public class FxmlInfo<T> {
    private final Parent parent;
    private final T controller;

    public FxmlInfo(final Parent p, final T controller) {
        this.parent = p;
        this.controller = controller;
    }

    public static <T> FxmlInfo<T> loadFXML(final String fxml, final Injector injector) throws IOException {
        final String path = "quinzical/impl/views/" + fxml + ".fxml";
        final FXMLLoader loader = new FXMLLoader(Entry.class.getClassLoader().getResource(path));
        loader.setControllerFactory(injector::getInstance);

        return new FxmlInfo<>(loader.load(), loader.getController());
    }


    public final Parent getParent() {
        return parent;
    }

    public final T getController() {
        return controller;
    }
}
