package be.kul.gantry.domain.GUI;

import be.kul.gantry.domain.Move;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import static be.kul.gantry.domain.Problem.gantries;
import static be.kul.gantry.domain.Problem.safetyDistance;


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
        xaxis.setForceZeroInRange(false);
        xaxis.setUpperBound(1000);
        xaxis.setAutoRanging(false);
        yaxis.setAutoRanging(true);

        gantry0.setName("Gantry 0");
        gantry1.setName("Gantry 1");

        linechart.getData().add(gantry0);
        linechart.getData().add(gantry1);
    }

    public void addMove(Move m) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (m.getGantry().getId() == 0) {
                    gantry0.getData().add(new XYChart.Data(m.getTime(), m.getX()));

                }
                if (m.getGantry().getId() == 1) {
                    gantry1.getData().add(new XYChart.Data(m.getTime(), m.getX()));
                }
                if(Math.abs(gantries.get(0).getX()-gantries.get(1).getX())<safetyDistance){
                    System.out.println("OWOWOW kik ier minder dan de safetyDistance e broer");
                }
                System.out.println(Math.abs(gantries.get(0).getX()-gantries.get(1).getX()));
                double minDiff= Math.abs(xaxis.getLowerBound()- m.getTime());
                double maxDiff= Math.abs(xaxis.getUpperBound()-m.getTime());
                if(m.getTime()<xaxis.getLowerBound()+200){
                    xaxis.setLowerBound(m.getTime()-200);
                    xaxis.setUpperBound(xaxis.getLowerBound()+1000);
                }
                else if(m.getTime()>xaxis.getUpperBound()+200){
                    xaxis.setLowerBound(m.getTime()-200);
                    xaxis.setUpperBound(xaxis.getLowerBound()+1000);
                }
                else if(maxDiff<100){
                    xaxis.setLowerBound(xaxis.getLowerBound()+600);
                    xaxis.setUpperBound(xaxis.getUpperBound()+600);
                }
            }
        });

    }

    public void updateBounds() {
        xaxis.setAutoRanging(false);
        xaxis.setLowerBound(Integer.parseInt(lower.getText()));
        xaxis.setUpperBound(Integer.parseInt(upper.getText()));
    }

}
