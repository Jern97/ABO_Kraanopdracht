package be.kul.gantry.domain;

import be.kul.gantry.domain.GUI.MoveListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import static java.lang.Thread.sleep;

public class Main {
    static String INPUT_FILE;
    static String OUTPUT_FILE;

    public static void main(String[] args) {
        INPUT_FILE = args[0];
        OUTPUT_FILE = args[1];
        MoveListener test= MoveListener.getInstance();
        try {
            sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
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

                    if (problem.getGantries().size() == 2) problem.solveTwoGantries();
                    else problem.solveOneGantry();

                    System.out.println("########## GANTRY0");
                    for (Move m : MoveGenerator.getInstance().gantry0Moves) {
                        System.out.println(m);
                    }
                    System.out.println("########### GANTRY1");
                    for (Move m : MoveGenerator.getInstance().gantry1Moves) {
                        System.out.println(m);
                    }

                    for (Move m : MoveGenerator.getInstance().gantry0Moves) {
                        writer.write("\n");
                        writer.write(m.toString());
                    }

                    for (Move m : MoveGenerator.getInstance().gantry1Moves) {
                        writer.write("\n");
                        writer.write(m.toString());
                    }

                    writer.close();
                    //System.out.println(System.currentTimeMillis() - startTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();







    }


}
