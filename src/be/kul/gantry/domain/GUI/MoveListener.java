package be.kul.gantry.domain.GUI;

import be.kul.gantry.domain.Move;
import javafx.application.Application;
import javafx.application.Platform;

public class MoveListener {
    private static MoveListener instance;
    GraphDriver gd;

    private MoveListener() {
        gd= new GraphDriver();
        gd.start();
    }

    public static MoveListener getInstance() {
        if (instance == null){
            instance = new MoveListener();
        }

        return instance;
    }

    public GraphDriver getGraphDriver(){
        return gd;
    }

}
