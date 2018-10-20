package be.kul.gantry.domain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String [ ] args){
        try{
            Problem problem = Problem.fromJson(new File("1_10_100_4_FALSE_65_50_50.json"));

            BufferedWriter writer = new BufferedWriter(new FileWriter("output.csv"));
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
