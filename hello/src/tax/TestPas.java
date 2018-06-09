package tax;

import java.io.IOException;

public class TestPas {
    public TestPas() {
    }

    public static void main(String[] args) throws IOException {
        TestDyd.dyd();
        WordAnalyse ana = new WordAnalyse("/Users/zhangxuwei/Documents/hello/src/tax/test1.pas");
        ana.readFile();
        ana.Analyse();
    }
}
