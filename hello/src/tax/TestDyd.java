package tax;

import java.io.FileOutputStream;
import java.io.IOException;

public class TestDyd {
    public TestDyd() {
    }

    static void dyd() throws IOException {
        FileOutputStream test = new FileOutputStream("/Users/zhangxuwei/Desktop/test1.dyd");
        test.write((new String("")).getBytes());
        test.close();
        new FileOutputStream("/Users/zhangxuwei/Desktop/wrong.dyd");
        test.write((new String("")).getBytes());
        test.close();
    }
}