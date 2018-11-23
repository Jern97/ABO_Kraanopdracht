package be.kul.gantry.domain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class Main {
    static String INPUT_FILE;
    static String OUTPUT_FILE;

    public static void main(String [ ] args){
        INPUT_FILE = args[0];
        OUTPUT_FILE = args[1];
        try{
            long startTime = System.currentTimeMillis();
            Problem problem;
            if(INPUT_FILE.contains("TRUE")){
                problem = Problem.fromJsonStaggered(new File(INPUT_FILE));
            }
            else{
                problem = Problem.fromJsonNotStaggered(new File(INPUT_FILE));
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE));
            writer.write("\"gID\";\"T\";\"x\";\"y\";\"itemsInCraneID\"");

            List<Move> moves = problem.solve();

            for(Move m: moves){
                writer.write("\n");
                writer.write(m.toString());
            }
            writer.close();
            //System.out.println(System.currentTimeMillis() - startTime);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
