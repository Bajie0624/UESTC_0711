package lex;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        analyse ana = new analyse();
        ana.readFile();
        ana.main_procedure();
        System.out.println("变量表：");
        for (int i = 0; i < ana.varList.size(); i++) {
            System.out.println(ana.varList.get(i) + " " + i);
        }
        System.out.println("过程表");
        for (int i = 0; i < ana.proList.size(); i++) {
            System.out.println(ana.proList.get(i) + " " + i);
        }
        ana.Write_file();
    }
}
