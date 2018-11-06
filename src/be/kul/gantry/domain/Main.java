package be.kul.gantry.domain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {
    static String INPUT_FILE = "1_10_100_4_FALSE_65_50_50.json";
    static String OUTPUT_FILE = "output.csv";

    public static void main(String [ ] args){
        try{
            Problem problem = Problem.fromJson(new File(INPUT_FILE));

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
