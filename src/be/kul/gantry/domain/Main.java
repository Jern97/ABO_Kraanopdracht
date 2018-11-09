package be.kul.gantry.domain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Main {
    static String INPUT_FILE;
    static String OUTPUT_FILE;

    public static void main(String [ ] args){
        INPUT_FILE = args[0]+".json";
        OUTPUT_FILE = args[0]+"_out.csv";
        try{
            Problem problem;
            if(INPUT_FILE.contains("TRUE")){
                problem = Problem.fromJsonStaggered(new File(INPUT_FILE));
            }
            else{
                problem = Problem.fromJsonNotStaggered(new File(INPUT_FILE));
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE));
            writer.write("\"gID\";\"T\";\"x\";\"y\";\"itemsInCraneID\"");

            for(Move m: problem.solve()){
                writer.write("\n");
                writer.write(m.toString());
            }
            writer.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
