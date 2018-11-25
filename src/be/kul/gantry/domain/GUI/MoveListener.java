package be.kul.gantry.domain.GUI;

import be.kul.gantry.domain.Move;
import javafx.application.Platform;

public class MoveListener {
    private static MoveListener instance = new MoveListener();
    private GraphController gc;

    private MoveListener() {

    }

    public static MoveListener getInstance(){
        return instance;
    }

    public void reportNewMove(Move m){
        //Platform.runLater(() -> gc.addMove(m));
        gc.addMove(m);
    }

    public void setGraphController(GraphController gc){
        this.gc = gc;
    }
}
