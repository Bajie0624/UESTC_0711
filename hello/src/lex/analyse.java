package lex;

import javax.lang.model.util.ElementScanner6;
import java.io.*;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Parameter;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public class analyse {
    static StringBuffer buffer = new StringBuffer();
  //  按照ppt上给的单词符号与种别对照表进行赋值，有些变量名取名，仿照陈文宇老师上课ppt上给出的方式给出。例如sym等
    private String begin_code = "01";
    private String end_code = "02";
    private String  integer_code = "03";
    private String  if_code = "04";
    private String  then_code = "05";
    private String  else_code = "06";
    private String  function_code = "07";
    private String  read_code = "08";
    private String  write_code = "09";
    private String identifier_code = "10";
    private String constant_code = "11";
    private String minus_code = "18";
    private String multiple_code = "19";
    private String assign_code = "20";
    private String left_bracket = "21";
    private String right_bracket = "22";
    private String semi_code = "23";
    private String EOLN = "24";
    private String EOF = "25";
    //其实我觉得这里有点画蛇添足的味道，搞得自己都不是很清楚上面各个code的含义，写错了一个位置，检查了好久

    public int p = 0;//当前位置
    private int nowLine = 1;//当前行数
    private int countLine = 0;
    private String nowPro = "main";
    private String lastPro;
    private String str;
    public int nowLevel = 0;
    public List<String> list = new ArrayList<>();//将文件输出到list
    public List<String> wordSymbol = new ArrayList<>();//单词符号表
    public List<String> varList = new ArrayList<>();//变量表
    public List<String> proList = new ArrayList<>();//过程
    public StringBuffer varTable = new StringBuffer();
    public List<VarTable> var_List = new ArrayList<>();//上面有些存的还是不够，需要弄个新的变量表存
    public StringBuffer proTable = new StringBuffer();
    String sym;
    public void readFile() throws IOException {
       // BufferedWriter buff3 = new BufferedWriter(new FileReader(file));
        File file = new File("/Users/zhangxuwei/Documents/hello/src/lex/test2.dyd");
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while ((str = bufferedReader.readLine()) != null) {
            list.add(str.substring(17, 19));//取假如02这种值
            wordSymbol.add(str.substring(0,16).trim());//取符号，去掉空格
       }
        list.remove(list.size() - 1);//去掉一位以后，就要向前进一位，因为去掉了
        wordSymbol.remove(wordSymbol.size() -1);
        fileReader.close();
        bufferedReader.close();
        sym = list.get(p);
    }

    public void advance(){
        System.out.println("匹配成功" + "当前行号:" + nowLine);
        p++;
        countLine++;
        if (p <= list.size() - 1){
            //判断下是否到了结尾
            if (list.get(p).equals(EOLN)){
                p++;
                nowLine++;
                countLine++;
                sym = list.get(p);
            }else {
                sym = list.get(p);
            }
        }
    }
    //错误表
    public void error(String err)throws IOException {
        System.out.println(err + "当前行号：" + nowLine);
        BufferedWriter buff = new BufferedWriter(new FileWriter("/Users/zhangxuwei/Documents/hello/src/lex/wrong2.dyd",true));
        buff.write(err + "当前行号:" + nowLine);
        buff.newLine();
        buff.flush();
        buff.close();
    }
    /*这里还是要感谢万哥教我一手
    当出错时，如果按照以前的写法，这里出错就会结束程序，所以每次都需要advance一下，不管对错
    if(){
    }else{
    error()
    }
    advance();
    这种模式虽然看起来很奇怪，但是少写了一次advance();不知道好坏，但是觉得还是不错

     */
    public void main_procedure()throws IOException{
        //<程序>→<分程序>
        branch_procedure();
    }
    public void branch_procedure()throws IOException{
        //#<分程序>→begin <说明语句表>；<执行语句表> end
        if ((sym.equals(begin_code))){
        }else {
            error("分程序出错，缺少begin");
        }
        advance();
        declare_statemnet_table();
        exec_statement_table();
////        if (sym.equals(semi_code)) {//分号为23
////        }else {
////            error("分程序错误，是否缺少 ;");
////        }
//        advance();
        if (sym.equals(end_code)) {
        } else {
            error("分程序错误，缺少end");
        }
        advance();
    }
    public void declare_statemnet_table()throws IOException{
        /*
        <说明语句表>→<说明语句>│<说明语句表> ；<说明语句>
         需要改写为: <说明语句表>→<说明语句><说明语句表A>
        <说明语句表A>→;<说明语句><说明语句表A>│空
        */
        declare_statement();
        declare_statemnet_tableA();
    }
    public void declare_statemnet_tableA()throws IOException{
        //<说明语句表A>→;<说明语句><说明语句表A>│空
        if (sym.equals(semi_code)){
            advance();
            declare_statement();
            declare_statemnet_tableA();
        }

    }

    public void declare_statement()throws IOException{
        // <说明语句>→<变量说明>│<函数说明>
        if (sym.equals(integer_code)){
            //只有判断成功，才能进入下一步.
            String stri = list.get(p+1);
            if (stri.equals(function_code)){//
                func_declare();
            }else if (stri.equals(identifier_code)){
                var_declare();
            }else {
            error("说明语句出错 变量名起错!!");//形如 integer *　的错误
            }
        } else{
            if (sym.equals(read_code) || sym.equals(write_code) || sym.equals(identifier_code) || sym.equals(if_code)) {
             } else {
            error("说明语句出错?");
            }
        }

    }

    //判断当前变量是否在变量定义表中
    public boolean var_table(){
        if (varList.contains(wordSymbol.get(countLine))) {
            return true;
        }else {
            return false;
        }
    }
    public void var_declare()throws IOException{
        //<变量说明>→integer <变量>
        if (sym.equals(integer_code)){
        }else {
            error("变量说明出错，缺少integer");
        }
        advance();
        if (sym.equals(identifier_code)){
            if (!varList.contains(wordSymbol.get(countLine))) {//这里就是worldSymbol的作用，相当于一个缓冲区
                varList.add(wordSymbol.get(countLine));
                //传值进varTable
                varTable.append(wordSymbol.get(countLine) + " " + nowPro
                        + " " + "0" + " " + "integer" + " " + nowLevel + " " + (varList.size() - 1) + "\r\n");
                //过程表的最后两行
                VarTable var = new VarTable();
                var.name = wordSymbol.get(countLine);
                var.varLevel = nowLevel;
                var.varCount = varList.size() - 2;
                var_List.add(var);
            }
        }
        var();
    }
    public void var()throws IOException{//标识符即为终结符进行处理
         // <变量>→<标识符>
        if (sym.equals(identifier_code)){
            if (var_table()){
                advance();
            }else {
                error("变量" + wordSymbol.get(countLine) + "未定义");
                advance();
            }
        }else {
            error("变量说明出错");
            advance();
        }
    }
    public void func_declare()throws IOException {
        //<函数说明>→integer function <标识符>（<参数>）；<函数体>
        if (sym.equals(integer_code)) {
        } else {
            error("函数说明出错，缺少integer");
        }
        advance();
        if (sym.equals(function_code)) {
        } else {
            error("函数说明出错，缺少function");
        }
        advance();
        if (sym.equals(identifier_code)) {
            if (!proList.contains(wordSymbol.get(countLine))) {
                proList.add(wordSymbol.get(countLine));
            }
            if (!varList.contains(wordSymbol.get(countLine))) {
                varList.add(wordSymbol.get(countLine));
            }
        }
            int proce_Table = countLine;//过程表
            if (sym.equals(identifier_code)) {
            } else {
                error("函数声明出错");
            }
            nowLevel++;
            lastPro = nowPro;
            nowPro = wordSymbol.get(countLine);
            advance();
            if (sym.equals(left_bracket)) {
            } else {
                error("函数说明出错，缺少 (");
            }
            advance();
            if (sym.equals(identifier_code)) {
                if (!varList.contains(wordSymbol.get(countLine))) {
                    varList.add(wordSymbol.get(countLine));
                    varTable.append(wordSymbol.get(countLine) + " " + nowPro
                            + " " + "1" + " " + "integer" + " " + nowLevel + " " + (varList.size() - 2) + "\r\n");
                    VarTable var = new VarTable();
                    var.name = wordSymbol.get(countLine);
                    var.varLevel = nowLevel;
                    var.varCount = varList.size() - 2;
                    var_List.add(var);
                }
            }
            parameter();
            if (sym.equals(right_bracket)) {
            } else {
                error("函数声明出错 缺少)");
            }
            advance();
            if (sym.equals(semi_code)) {
            } else {
                error("函数声明出错 缺少;");
            }
            advance();
            func_body();
            nowPro = lastPro;

            //填写过程表
            int firstVarLovcation = -1;
            for (int i = 0; i < var_List.size(); i++) {
                if (var_List.get(i).varLevel == nowLevel) {
                    firstVarLovcation = var_List.get(i).varCount;
                    break;
                }
            }
            int lastVarLovcation = -1;
            for (int i = 0; i < var_List.size(); i++) {
                if (var_List.get(i).varLevel == nowLevel) {
                    lastVarLovcation = var_List.get(i).varCount;
                }
            }
            proTable.append(wordSymbol.get(proce_Table) + " " + "integer"
                    + " " + nowLevel + " " + firstVarLovcation + " " + lastVarLovcation + "\r\n");

    }
    public void parameter()throws IOException{
        //<参数>→<变量>
        var();
    }
    public void func_body()throws IOException{
        //<函数体>→begin <说明语句表>；<执行语句表> end
        if (sym.equals(begin_code)) {
        }else {
            error("函数说明出错，缺少begin");
        }
        advance();
        declare_statemnet_table();
        exec_statement_table();
        if (sym.equals(end_code)){
        }else {
            error("函数说明出错，缺少end");
        }
        advance();
    }
    public void exec_statement_table()throws IOException{
        /*
        左递归：<执行语句表>→<执行语句>│<执行语句表>；<执行语句>
         需要改写为
         <执行语句表>→<执行语句><执行语句表A>
         <执行语句表A>→;<执行语句><执行语句表A>│空
         */
        exec_statement();
        exec_statement_tableA();
    }
    public void exec_statement_tableA()throws IOException{
        //<执行语句表A>→;<执行语句><执行语句表A>│空
        if (sym.equals(semi_code)){
            advance();
            exec_statement();
            exec_statement_tableA();
        }
    }
    public void exec_statement()throws IOException{
        //<执行语句>→<读语句>│<写语句>│<赋值语句>│<条件语句>
        //千万千万小心
        if (sym.equals(read_code)){
            read_statement();;
        }else if (sym.equals(write_code)){
            write_statement();
        }else if (sym.equals(identifier_code)){
            assign_statement();
        }else if (sym.equals(if_code)){
            condition_statement();
        }else if (sym.equals(end_code)){//这里是为了解决分号问题
            return;
        } else {
            error("执行语句出错");
        }
    }
    public void read_statement()throws IOException{
        //<读语句>→read(<变量>)
        if (sym.equals(read_code)) {
        }else {
            error("语句出错");
        }
        advance();
        if (sym.equals(left_bracket)) {
        }else {
            error("读语句出错");
        }
        advance();
        var();
        if (sym.equals(right_bracket)){
        }else {
            error("读语句出错");
        }
        advance();

    }
    public void write_statement()throws IOException{
        //<写语句>→write(<变量>)
        if (sym.equals(write_code)) {
        }else {
            error("写语句出错");
        }
        advance();
        if (sym.equals(left_bracket)){ }else {
                error("写语句出错");
        }
        advance();
        var();
        if (sym.equals(right_bracket)){
        }else {
            error("写语句出错");
        }
        advance();
    }
    public void assign_statement()throws IOException{
        //<赋值语句>→<变量>:=<算术表达式>
        var();
        if (sym.equals(assign_code)){
        }else {
            error("赋值语句出错");
        }
        advance();
        math_expression();
    }
    public void math_expression()throws IOException{
       /*
       左递归：<算术表达式>→<算术表达式>-<项>│<项>
          改写：<算术表达式>→<项><算术表达式A>
          <算术表达式A>→-<项><算术表达式A>|空
          */
       item();
       math_expressionA();
    }
    public void math_expressionA()throws IOException{
        //<算术表达式A>→-<项><算术表达式A>|空
        if (sym.equals(minus_code)){
            advance();
            item();
            math_expressionA();
        }
    }
    public void item()throws IOException{
        /*左递归：<项>→<项>*<因子>│<因子>
          改写<项>→<因子><项A>
          <项A>→*<因子><项A>│空*/
        factor();
        itemA();
    }
    public void itemA()throws IOException{
        //<项A>→*<因子><项A>│空
        if (sym.equals(multiple_code)){
            advance();
            factor();
            itemA();
        }

    }
    public void factor()throws IOException{
        //<因子>→<变量>│<常数>│<函数调用>
        //这里感觉有点技巧，要从常数开始判断，如果写if循环从变量开始，不好判断
        if (sym.equals(constant_code)){
            constant();//常数
        }else if (sym.equals(identifier_code)){
            if (list.get(p + 1).equals(left_bracket)){
                func_call();//函数调用
            } else {
                var();
            }
        }else {
            error("因子出错");
        }
    }
    public void func_call()throws IOException{
        //<函数调用>→<标识符>(<算数表达式>)
        if (sym.equals(identifier_code)) {}
        else {
            error("函数调用出错，没有标识符");
        }
        advance();
        if (sym.equals(left_bracket)) {
        }else {
            error("函数调用出错，缺少(");
        }
        advance();
        math_expression();
        if (sym.equals(right_bracket)) {}
        else {
            error("函数调用出错，缺少)");
        }
        advance();

    }
    public void constant()throws IOException{
        //<常数>→<无符号整数>
        unsigned_integer();
    }
    public void unsigned_integer()throws IOException{
        if (sym.equals(constant_code)){}
        else {
            error("常数调用出错");
        }
        advance();
    }
    public void condition_statement()throws IOException {
        //<条件语句>→if<条件表达式>then<执行语句>else <执行语句>
        if (sym.equals(if_code)) {
        } else {
            error("缺少if");
        }
        advance();
        condition_expression();
        if (sym.equals(then_code)) {
        } else {
            error("缺少then");
        }
        advance();
        exec_statement();
        if (sym.equals(else_code)) {
        } else {
            error("缺少end");
        }
        advance();
        exec_statement();

    }
    public void condition_expression()throws IOException{
        //<条件表达式>→<算术表达式><关系运算符><算术表达式>
        math_expression();
        relation_operator();
        math_expression();
    }
    public void relation_operator()throws IOException{
        //<关系运算符> →<│<=│>│>=│=│<>
        if (sym.equals("15")){
            advance();
        }else if (sym.equals("14")){
            advance();
        }else if (sym.equals("17")){
            advance();
        }else if (sym.equals("16")){
            advance();
        }else if (sym.equals("12")){
            advance();
        }else if (sym.equals("13")){
            advance();
        }else {
            error("关系运算符出错");
            advance();
        }

    }
    public void Write_file()throws IOException{
        BufferedWriter buff1 = new BufferedWriter(new FileWriter("/Users/zhangxuwei/Documents/hello/src/lex/test2.var",false));//清除作用，同词法分析一致
        BufferedWriter buff2 = new BufferedWriter(new FileWriter("/Users/zhangxuwei/Documents/hello/src/lex/test2.pro",false));
        buff1.write(varTable.toString());
        buff2.write(varTable.toString());
        buff1.close();
        buff2.close();
    }
}

