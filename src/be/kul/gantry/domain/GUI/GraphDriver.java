package be.kul.gantry.domain.GUI;

import be.kul.gantry.domain.Move;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GraphDriver extends Thread {

    private GraphController gc;

    public GraphDriver(){
        gc= loadAndSetGui();
    }



    private GraphController loadAndSetGui() {
        com.sun.javafx.application.PlatformImpl.startup(()->{});
        Parent root=null;
        GraphController controller = null;
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("graphView.fxml"));
            root = fxmlLoader.load();
            controller = fxmlLoader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent finalRoot = root;
        Platform.runLater(
                () -> {
                    Stage graphStage= new Stage();
                    graphStage.setTitle("Graph");
                    //width en height van de scene bepalen
                    //dit moet hier geset worden, jammergenoeg, we kunnen dit niet later aanpassen
                    Scene startScene= new Scene(finalRoot); //misschien nog wijzigen
                    graphStage.setScene(startScene);
                    graphStage.setResizable(true);
                    graphStage.show();

                }
        );

        return controller;
    }



    public void addMove(Move m) {
        gc.addMove(m);
    }
}
