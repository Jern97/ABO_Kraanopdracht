package be.kul.gantry.domain;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String [ ] args){
        try{
            Problem problem = Problem.fromJson(new File("1_10_100_4_FALSE_65_50_50.json"));
            problem.writeJsonFile(new File("test.json"));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
