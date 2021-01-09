package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :zhangyi
 * @description:字
 */
public class Word {
    public final static int KEY = 1;//关键字
    public final static int OPERATOR = 2;//操作符
    public final static int INT_CONST = 3;//整形常量
    public final static int CHAR_CONST = 4;//字符常量
    public final static int BOOL_CONST =5; //布尔常量
    public final static int IDENTIFIER = 6;//标识符
    public final static int BOUNDARYSIGN =7; //界符;
    public final static int END =8; //结束符
    public final static int UNIDEF =9;// 未知类型
    public final static int FLOAT_CONST=10;//浮点型常量;
    public static List<String> key = new ArrayList<>();// 关键字集合
    public static List<String> boundarySign = new ArrayList<>();// 界符集合
    public static List<String> operator = new ArrayList<>();// 运算符集合

    public int id;// 单词序号
    public String value;// 单词的值
    public int type;// 单词类型
    public int line;// 单词所在行
    public boolean flag = true;//单词是否合法

    public Word() {

    }

    public Word(int id, String value, int type, int line) {
        this.id = id;
        this.value = value;
        this.type = type;
        this.line = line;
    }
    public static boolean isKey(String word) {
        return key.contains(word);
    }

    public static boolean isOperator(String word) {
        return operator.contains(word);
    }

    public static boolean isBoundarySign(String word) {
        return boundarySign.contains(word);
    }

    public String toString(){
        return this.id+"\t"+this.value+"\t"+this.type+"\t"+this.line+"\t"+this.flag;
    }

    static {
        Word.operator.add("+");
        Word.operator.add("-");
        Word.operator.add("++");
        Word.operator.add("--");
        Word.operator.add("*");
        Word.operator.add("/");
        Word.operator.add(">");
        Word.operator.add("<");
        Word.operator.add(">=");
        Word.operator.add("<=");
        Word.operator.add("==");
        Word.operator.add("!=");
        Word.operator.add("=");
        Word.operator.add("&&");
        Word.operator.add("||");
        Word.operator.add("!");
        Word.operator.add(".");
        Word.operator.add("?");
        Word.operator.add("|");
        Word.operator.add("&");
        Word.boundarySign.add("(");
        Word.boundarySign.add(")");
        Word.boundarySign.add("{");
        Word.boundarySign.add("}");
        Word.boundarySign.add(";");
        Word.boundarySign.add(",");
        
        Word.key.add("void");
        Word.key.add("main");
        Word.key.add("int");
        Word.key.add("char");
        Word.key.add("float");
        Word.key.add("if");
        Word.key.add("else");
        Word.key.add("while");
        Word.key.add("for");
        Word.key.add("do");
    }
}
