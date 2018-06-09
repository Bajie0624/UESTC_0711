package tax;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

    public class WordAnalyse {
        private String[] keyWord = new String[]{"begin", "end", "integer", "if", "then", "else", "function", "read", "write", "标识符", "常量", "=", "<>", "<=", "<", ">=", ">", "-", "*", ":=", "(", ")", ";", "EOLN"};
        static StringBuffer buffer = new StringBuffer();
        private char cha;
        private int p = 0;
        private String token;
        private String fileName;
        static int lines = 1;

        public WordAnalyse(String fileName) {
            this.fileName = fileName;
        }

        public void getchar() {
            this.cha = buffer.charAt(this.p);
            ++this.p;
        }

        public void getnbc() {
            while(Character.isSpaceChar(this.cha)) {
                this.getchar();
            }

        }

        public void concat() {
            this.token = this.token + this.cha;
        }

        boolean  letter() {
            return ('a' <= this.cha && this.cha <= 'z')||('A' <= this.cha && this.cha <= 'Z');

        }

        boolean digit() {
            return Character.isDigit(this.cha);
        }

        public void retract() {
            --this.p;
            this.cha = ' ';
        }

        public int reserve() {
            for(int i = 0; i < this.keyWord.length; ++i) {
                if (this.token.equals(this.keyWord[i])) {
                    return i + 1;
                }
            }

            return 0;
        }
        //补充成16位
        public static StringBuffer wordSymbol(String str) throws IOException {
            StringBuffer strBr = new StringBuffer();
            if (str.length() > 16) {
                writeBackWrongs(str);
            }

            int len = 16 - str.length();

            for(int j = 0; j < len; ++j) {
                strBr.append(" ");
            }

            strBr.append(str);
            return strBr;
        }
        //补充成2位
        public static StringBuffer addInt(int q) throws IOException {
            String strBr = String.valueOf(q);
            StringBuffer str1 = new StringBuffer();
            int longs = 2 - strBr.length();

            for(int i = 0; i < longs; ++i) {
                str1.append(0);
            }

            str1.append(strBr);
            return str1;
        }
        //处理行号
        public String arryTwo() throws IOException {
            String wSymbol = new String(wordSymbol(this.token));
            String kinds = new String(addInt(this.reserve()));
            String str = wSymbol + " " + kinds;
            return str;
        }
        //处理标识符
        public String dealSymbol(int bar) throws IOException {
            String reStr = new String(wordSymbol(this.token));
            String str = reStr + " " + bar;
            return str;
        }

        public boolean wrongs() throws IOException {
            if (this.token.length() > 16) {
                String str1 = "***LINE" + lines + "\n";
                String str = "***LINe" + str1 + " 超过十六位的最大长度";
                writeBackWrongs(str);
                return true;
            } else {
                return false;
            }
        }

        public static void writeBackWrongs(String str) throws IOException {
            BufferedWriter backStr = new BufferedWriter(new FileWriter("/Users/zhangxuwei/Documents/hello/src/tax/wrong.dyd", true));
            backStr.write(str);
            backStr.newLine();
            backStr.flush();
            backStr.close();
        }

        public static void analyzBack(String str) throws IOException {
            BufferedWriter backStr = new BufferedWriter(new FileWriter("/Users/zhangxuwei/Documents/hello/src/tax/test1.dyd", true));
            backStr.write(str);
            backStr.newLine();
            backStr.flush();
            backStr.close();
        }

        public void readFile() {
            try {
                File file = new File(this.fileName);
                FileReader reader = new FileReader(file);
                char[] data = new char[1];

                while(reader.read(data) != -1) {
                    buffer.append(data, 0, data.length);
                }

                reader.close();
            } catch (FileNotFoundException var4) {
                System.out.println("文件没有找到");
                var4.printStackTrace();
            } catch (IOException var5) {
                System.out.println("读写文件出现异常");
                var5.printStackTrace();
            }

        }

        public void Analyse() throws IOException {
            while(this.p < buffer.length()) {
                this.token = "";
                this.getchar();
                this.getnbc();
                int temp = 0;
                if (this.letter()) {
                    temp = 1;
                } else if (this.digit()) {
                    temp = 2;
                } else if (this.cha == '=') {
                    temp = 3;
                } else if (this.cha == '-') {
                    temp = 4;
                } else if (this.cha == '*') {
                    temp = 5;
                } else if (this.cha == '(') {
                    temp = 6;
                } else if (this.cha == ')') {
                    temp = 7;
                } else if (this.cha == '<') {
                    temp = 8;
                } else if (this.cha == '>') {
                    temp = 9;
                } else if (this.cha == ':') {
                    temp = 10;
                } else if (this.cha == ';') {
                    temp = 11;
                } else if (this.cha == '\n') {
                    temp = 12;
                } else {
                    temp = 13;
                }

                String str1;
                switch(temp) {
                    case 1:
                        this.concat();
                        this.getchar();

                        while(this.letter() || this.digit()) {
                            this.concat();
                            this.getchar();
                        }

                        this.retract();
                        if (!this.wrongs()) {
                            if (this.reserve() != 0) {
                                analyzBack(this.arryTwo());
                            } else {
                                analyzBack(this.dealSymbol(10));
                            }
                        }
                        break;
                    case 2:
                        this.concat();
                        this.getchar();

                        while(this.digit()) {
                            this.concat();
                            this.getchar();
                        }

                        this.retract();
                        if (!this.wrongs()) {
                            analyzBack(this.dealSymbol(11));
                        }
                        break;
                    case 3:
                        this.concat();
                        analyzBack(this.arryTwo());
                        break;
                    case 4:
                        this.concat();
                        analyzBack(this.arryTwo());
                        break;
                    case 5:
                        this.concat();
                        analyzBack(this.arryTwo());
                        break;
                    case 6:
                        this.concat();
                        analyzBack(this.arryTwo());
                        break;
                    case 7:
                        this.concat();
                        analyzBack(this.arryTwo());
                        break;
                    case 8:
                        this.concat();
                        this.getchar();
                        if (this.cha == '=') {
                            analyzBack(this.arryTwo());
                        } else if (this.cha == '>') {
                            analyzBack(this.arryTwo());
                        } else if (this.cha != '=') {
                            this.retract();
                            analyzBack(this.arryTwo());
                        }
                        break;
                    case 9:
                        this.concat();
                        this.getchar();
                        if (this.cha == '=') {
                            analyzBack(this.arryTwo());
                            break;
                        } else if (this.cha != '=') {
                            this.retract();
                            analyzBack(this.arryTwo());
                            break;
                        }
                    case 10:
                        this.concat();
                        this.getchar();
                        if (this.cha == '=') {
                            this.concat();
                            analyzBack(this.arryTwo());
                            break;
                        }

                        str1 = "***LINE" + lines + "不匹配";
                        writeBackWrongs(str1);
                        break;
                    case 11:
                        this.concat();
                        analyzBack(this.arryTwo());
                        break;
                    case 12:
                        if (this.p == buffer.length()) {
                            analyzBack("             EOF 25");
                            break;
                        }

                        this.token = "EOLN";
                        ++lines;
                        analyzBack(this.arryTwo());
                        break;
                    case 13:
                        this.concat();
                        str1 = "***LINE" + lines + "非法字符";
                        writeBackWrongs(str1);
                }
            }

        }
    }


