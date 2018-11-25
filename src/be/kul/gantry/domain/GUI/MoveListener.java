package be.kul.gantry.domain.GUI;

import be.kul.gantry.domain.Move;
import javafx.application.Application;
import javafx.application.Platform;

public class MoveListener {
    private static MoveListener instance = new MoveListener();
    private GraphDriver gd;

    private MoveListener() {
        gd= new GraphDriver();
        gd.start();
    }

    public static MoveListener getInstance(){
        return instance;
    }

    public void reportNewMove(Move m){
        Platform.runLater(() -> gd.addMove(m));
        //gc.addMove(m);
    }

}
