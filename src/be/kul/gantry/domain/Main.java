package be.kul.gantry.domain;

import be.kul.gantry.domain.GUI.GraphController;
import be.kul.gantry.domain.GUI.MoveListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class Main extends Application {
    static String INPUT_FILE;
    static String OUTPUT_FILE;

    public static void main(String[] args) {
        INPUT_FILE = args[0];
        OUTPUT_FILE = args[1];

        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI/graphView.fxml"));
        Parent root = loader.load();
        GraphController gc = loader.getController();
        MoveListener.getInstance().setGraphController(gc);
        primaryStage.setTitle("Plot");
        Scene scene= new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

        try {
            long startTime = System.currentTimeMillis();
            Problem problem;
            if (INPUT_FILE.contains("TRUE")) {
                problem = Problem.fromJsonStaggered(new File(INPUT_FILE));
            } else {
                problem = Problem.fromJsonNotStaggered(new File(INPUT_FILE));
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE));
            writer.write("\"gID\";\"T\";\"x\";\"y\";\"itemsInCraneID\"");

            List<Move> moves = problem.solve();

            System.out.println("########## GANTRY0");
            for (Move m : MoveGenerator.getInstance().gantry0Moves) {
                System.out.println(m);
            }
            System.out.println("########### GANTRY1");
            for (Move m : MoveGenerator.getInstance().gantry1Moves) {
                System.out.println(m);
            }

            for (Move m : moves) {
                writer.write("\n");
                writer.write(m.toString());
            }
            writer.close();
            //System.out.println(System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
