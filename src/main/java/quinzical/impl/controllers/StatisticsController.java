package quinzical.impl.controllers;

import com.google.inject.Inject;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.Glow;
import javafx.util.Duration;
import javafx.util.Pair;
import quinzical.Entry;
import quinzical.impl.constants.GameScene;
import quinzical.interfaces.models.GameModel;
import quinzical.interfaces.models.SceneHandler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static javafx.collections.FXCollections.observableArrayList;

public class StatisticsController extends AbstractSceneController {

    @Inject
    GameModel gameModel;

    @Inject
    SceneHandler sceneHandler;

    @FXML
    private PieChart pieRatio;

    @FXML
    private TableView<NameValuePair> tableMostAnswered;

    @FXML
    private TableColumn<NameValuePair, String> colMostAnsweredName;

    @FXML
    private TableColumn<NameValuePair, String> colMostAnsweredNumber;

    @FXML
    private TableView<NameValuePair> tableMostChallenging;

    @FXML
    private TableColumn<NameValuePair, String> colMostChallengingName;

    @FXML
    private TableColumn<NameValuePair, String> colMostChallengingNumber;

    @FXML
    private Button btnChartReset;

    @Override
    protected void onLoad() {
        Platform.runLater(() -> {
            createChart();
            createMostAnsweredTable();
            createMostChallengingTable();
        });
    }

    private ObservableList<PieChart.Data> createAnswerData() {
        int correct = gameModel.getUserData().getCorrect();
        int incorrect = gameModel.getUserData().getIncorrect();

        PieChart.Data correctData = new PieChart.Data("Correct (" + correct + ")", correct);
        PieChart.Data incorrectData = new PieChart.Data("Incorrect (" + incorrect + ")", incorrect);

        return observableArrayList(correctData, incorrectData);
    }

    private void createMostAnsweredTable() {
        ObservableList<String> categories =
            observableArrayList(gameModel.getUserData().getAnalytics().getMostAnsweredCategories());
        ObservableList<String> answered =
            observableArrayList(gameModel.getUserData().getAnalytics().getQuestionsAnsweredByCategory(categories));

        createNameValueTable(tableMostAnswered, colMostAnsweredName, colMostAnsweredNumber, categories,
            answered);
    }

    private void createMostChallengingTable() {
        ObservableList<String> categories =
            observableArrayList(gameModel.getUserData().getAnalytics().getMostChallengingCategories());
        ObservableList<String> answered =
            observableArrayList(gameModel.getUserData().getAnalytics().getCorrectRatiosOfCategories(categories));

        createNameValueTable(tableMostChallenging, colMostChallengingName, colMostChallengingNumber, categories,
            answered);
    }

    private void createNameValueTable(TableView<NameValuePair> table, TableColumn<NameValuePair, String> names,
                                      TableColumn<NameValuePair, String> values, ObservableList<String> nameList,
                                      ObservableList<String> valueList) {
        names.setCellValueFactory(v -> v.getValue().nameProperty());
        values.setCellValueFactory(v -> v.getValue().valueProperty());
        table.setItems(observableArrayList(
            IntStream
                .range(0, nameList.size())
                .mapToObj(v -> new NameValuePair(nameList.get(v), valueList.get(v)))
                .collect(Collectors.toList())
        ));
    }

    private ScaleTransition createScaleAnimation(Node node) {
        final ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setDuration(Duration.seconds(0.7));
        scaleTransition.setNode(node);
        scaleTransition.setFromX(0.75);
        scaleTransition.setFromY(0.75);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);
        scaleTransition.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                return -1.76 * (Math.pow(t, 3)) + 0.931 * (Math.pow(t, 2)) + 1.785 * t;
            }
        });
        return scaleTransition;
    }

    private void createChart() {
        pieRatio.setData(createAnswerData());
        ScaleTransition scaleTransition = createScaleAnimation(pieRatio);
        pieRatio.getStylesheets().add(Objects.requireNonNull(Entry.class.getClassLoader().getResource("css/statistics" +
            ".css")).toExternalForm());
        pieRatio.applyCss();
        pieRatio.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.4, 3, 3);");
        pieRatio.setLegendVisible(false);
        pieRatio.setTitle("Correct Answer Ratio");
        pieRatio.setLabelLineLength(15);
        scaleTransition.play();
        setChartStateOne(false);
    }

    private void setChartStateOne(boolean reloadData) {
        if (reloadData)
            pieRatio.setData(createAnswerData());
        Node correct = pieRatio.getData().get(0).getNode();
        Node wrong = pieRatio.getData().get(1).getNode();
        correct.setOnMouseClicked(a -> setChartStateCorrect());
        wrong.setOnMouseClicked(a -> setChartStateIncorrect());

        pieRatio.getData().forEach(data -> {
            Node node = data.getNode();
            node.setCursor(Cursor.HAND);
            node.setOnMouseEntered(a -> {
                node.setEffect(new Glow());
                final ScaleTransition innerST = new ScaleTransition();
                innerST.setDuration(Duration.seconds(0.4));
                innerST.setNode(node);
                innerST.setFromX(node.getScaleX());
                innerST.setFromY(node.getScaleY());
                innerST.setToX(1.1);
                innerST.setToY(1.1);
                innerST.playFromStart();
            });
            node.setOnMouseExited(a -> {
                node.setEffect(null);
                final ScaleTransition innerST = new ScaleTransition();
                innerST.setDuration(Duration.seconds(0.4));
                innerST.setNode(node);
                innerST.setFromX(node.getScaleX());
                innerST.setFromY(node.getScaleY());
                innerST.setToX(1);
                innerST.setToY(1);
                innerST.playFromStart();
            });
        });
    }

    private void setChartStateOne() {
        setChartStateOne(true);
    }

    private void setChartStateCorrect() {
        btnChartReset.setDisable(false);
        pieRatio.setData(rightChartData());
    }

    private void setChartStateIncorrect() {
        btnChartReset.setDisable(false);
        pieRatio.setData(wrongChartData());
    }

    @FXML
    void resetChartClick() {
        btnChartReset.setDisable(true);
        setChartStateOne();
    }

    private ObservableList<PieChart.Data> wrongChartData() {
        List<Pair<String, Integer>> data = gameModel.getUserData().getAnalytics().getPairsForIncorrectAnswers(5);
        return observableArrayList(
            data.stream()
                .map(d -> new PieChart.Data(d.getKey() + " (" + d.getValue() + ")", d.getValue()))
                .collect(Collectors.toList())
        );
    }

    private ObservableList<PieChart.Data> rightChartData() {
        List<Pair<String, Integer>> data = gameModel.getUserData().getAnalytics().getPairsForCorrectAnswers(5);
        return observableArrayList(
            data.stream()
                .map(d -> new PieChart.Data(d.getKey() + " (" + d.getValue() + ")", d.getValue()))
                .collect(Collectors.toList())
        );
    }


    @FXML
    void btnBackPress() {
        sceneHandler.setActiveScene(GameScene.INTRO);
    }

    public static class NameValuePair {
        private final SimpleStringProperty name;
        private final SimpleStringProperty value;

        public NameValuePair(String name, double value) {
            this.name = new SimpleStringProperty(name);
            this.value = new SimpleStringProperty(value + "");
        }

        public NameValuePair(String name, String value) {
            this.name = new SimpleStringProperty(name);
            this.value = new SimpleStringProperty(value);
        }

        public String getName() {
            return name.get();
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public String getValue() {
            return value.get();
        }

        public void setValue(int value) {
            this.value.set(value + "");
        }

        public SimpleStringProperty valueProperty() {
            return value;
        }
    }
}
