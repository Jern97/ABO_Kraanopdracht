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

    private XYChart.Series gantry0;
    private XYChart.Series gantry1;

    NumberAxis xAxis;
    NumberAxis yAxis;

    @FXML
    public void initialize(){
        xAxis = (NumberAxis) linechart.getXAxis();
        yAxis = (NumberAxis) linechart.getYAxis();

        setbutton.setOnAction(e -> {
            updateBounds();
        });

        resetbutton.setOnAction(e -> {
            xAxis.setAutoRanging(true);
        });

        xAxis.setLabel("time");
        yAxis.setLabel("x");
        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);

        gantry0 = new XYChart.Series();
        gantry0.setName("Gantry 0");
        gantry1 = new XYChart.Series();
        gantry1.setName("Gantry 1");

        linechart.getData().add(gantry0);
        linechart.getData().add(gantry1);
    }

    public void addMove(Move m){
        if(m.getGantry().getId() == 0){
            gantry0.getData().add(new XYChart.Data(m.getTime(), m.getX()));
        }
        if(m.getGantry().getId() == 1){
            gantry1.getData().add(new XYChart.Data(m.getTime(), m.getX()));
        }
    }

    public void updateBounds(){
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(Integer.parseInt(lower.getText()));
        xAxis.setUpperBound(Integer.parseInt(upper.getText()));
    }

}
