package be.kul.gantry.domain.GUI;

import be.kul.gantry.domain.Move;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class GraphController {

    @FXML
    LineChart linechart;

    @FXML
    TextField lower;

    @FXML
    TextField upper;

    @FXML
    Button resetbutton;

    @FXML
    Button setbutton;

    private XYChart.Series gantry0 = new XYChart.Series();
    private XYChart.Series gantry1 = new XYChart.Series();

    @FXML
    NumberAxis xaxis;

    @FXML
    NumberAxis yaxis;

    @FXML
    public void initialize() {
        setbutton.setOnAction(e -> {
            updateBounds();
        });

        resetbutton.setOnAction(e -> {
            xaxis.setAutoRanging(true);
        });

        xaxis.setLabel("time");
        yaxis.setLabel("x");
        xaxis.setAutoRanging(true);
        yaxis.setAutoRanging(true);

        gantry0.setName("Gantry 0");
        gantry1.setName("Gantry 1");

        linechart.getData().add(gantry0);
        linechart.getData().add(gantry1);
    }

    public void addMove(Move m) {
        if (m.getGantry().getId() == 0) {
            gantry0.getData().add(new XYChart.Data(m.getTime(), m.getX()));
        }
        if (m.getGantry().getId() == 1) {
            gantry1.getData().add(new XYChart.Data(m.getTime(), m.getX()));
        }
    }

    public void updateBounds() {
        xaxis.setAutoRanging(false);
        xaxis.setLowerBound(Integer.parseInt(lower.getText()));
        xaxis.setUpperBound(Integer.parseInt(upper.getText()));
    }

}
