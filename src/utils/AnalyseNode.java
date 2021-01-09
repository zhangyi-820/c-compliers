package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :zhangyi
 * @description:语义分析节点
 */
public class AnalyseNode {
    public final static int NONTERMINAL = 1;//非终结符
    public final static int TERMINAL = 2;//终结符
    public final static int ACTIONSIGN = 3;//动作符
    public final static int END = 4;//结束符
    static List<String> nonterminal = new ArrayList<>();//非终结符集合
    static List<String> actionSign = new ArrayList<>();//动作符集合

    public int type;//节点类型
    public String name;//节点名
    public String value;//节点值

    public AnalyseNode() {
    }

    public AnalyseNode(int type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public static boolean isNonterm(AnalyseNode node) {
        return nonterminal.contains(node.name);
    }

    public static boolean isTerm(AnalyseNode node) {
        return Word.isKey(node.name)
                || Word.isOperator(node.name)
                || Word.isBoundarySign(node.name)
                || node.name.equals("id")
                || node.name.equals("num")
                || node.name.equals("char")
                ||node.name.equals("bool");
    }

    public static boolean isActionSign(AnalyseNode node) {
        return actionSign.contains(node.name);
    }


    static {
        //N:S,B,A,C,,X,R,Z,Z’,U,U’,E,E’,H,H’,G,M,D,L,L’,T,T’,F,O,P,Q
        nonterminal.add("B");//开始标志符 0
        nonterminal.add("S");//complex statement  1
        nonterminal.add("C");// 2
        nonterminal.add("A");// 3
        nonterminal.add("D");//declaration 4
        nonterminal.add("J");//jump 5
        nonterminal.add("R");//Assignment 6

        //declaration
        nonterminal.add("T"); //7
        nonterminal.add("N"); //8
        nonterminal.add("U"); //9
        nonterminal.add("L"); //10
        nonterminal.add("Z");
        nonterminal.add("A");

        //bool
        nonterminal.add("E"); //11
        nonterminal.add("H"); //12
        nonterminal.add("P");//E' 13
        nonterminal.add("G");// 14
        nonterminal.add("I");//H' 15

        //compare
        nonterminal.add("F"); //16
        nonterminal.add("K");//Comparator 17
        nonterminal.add("O");//19

        //Self-increasing and decreasing
//        nonterminal.add("Q");//18


        // arithmetic computing
        nonterminal.add("Y");//18
        nonterminal.add("M");//L'//19
        nonterminal.add("X");//T//20
        nonterminal.add("W");//21 不带分号的赋值语句


        actionSign.add("@Add_Sub");//101
        actionSign.add("@Add");//102
        actionSign.add("@Sub");//103
        actionSign.add("@Div_Mul");//104
        actionSign.add("@Div");//105
        actionSign.add("@Mul");//106


        actionSign.add("@AsS_R");//107
//        actionSign.add("@AsS_Q");//108
        actionSign.add("@AsS_F");//109
        actionSign.add("@AsS_U");//110
        actionSign.add("@Tran_LF");//111
        actionSign.add("@EQ");//112
        actionSign.add("@EQ_U'");//113
        actionSign.add("@Compare");//114
        actionSign.add("@Compare_OP");//115
        actionSign.add("@If_FJ");//116
        actionSign.add("@If_Backpatch_FJ");//117
        actionSign.add("@If_RJ");//118
//        actionSign.add("@If_RJ");//119
        actionSign.add("@If_Backpatch_RJ");//120
        actionSign.add("@While_FJ");//121
        actionSign.add("@While_Backpatch_FJ");//122
        actionSign.add("@For_FJ");//123
        actionSign.add("@For_RJ");//124
        actionSign.add("@For_Backpatch_FJ");//125
        actionSign.add("@For");
        actionSign.add("@Do");
        actionSign.add("@Do_Backpatch_RJ");
        actionSign.add("@RJ");
        actionSign.add("@FJ");
    }
}
