package utils;

/**
 * @author :zhangyi
 * @description:表
 */
public class Table {
    //N:S,B,A,C,,X,R,Z,Z’,U,U’,E,E’,H,H’,G,M,D,L,L’,T,T’,F,O,P,Q
    /**
     * 非终结符：N:S,B,A,C,,X,R,Z,Z’,U,U’,E,E’,H,H’,G,M,D,L,L’,T,T’,F,O,P,Q
     */
    public final static int B = 0;
    public final static int S = 1;
    public final static int C = 2;
    public final static int A = 3;
    public final static int D = 4;
    public final static int J = 5;
    public final static int R = 6;
    public final static int T = 7;
    public final static int N = 8;
    public final static int U = 9;
    public final static int L = 10;
    public final static int E = 11;
    public final static int H = 12;
    public final static int P = 13;
    public final static int G = 14;
    public final static int I = 15;
    public final static int F = 16;
    public final static int K = 17;
    public final static int Y = 18;
    public final static int M = 19;
    public final static int X = 20;
    public final static int W = 21;
    /**
     * 终结符：main	printf	scanf	void	int 	char	bool	id(自定义变量)	num（int常量）	ch(char常量)
     * if	else	while	for	;	,(	)	{    }	=	== !=	>	<	+	-	*	/	&&	||	!	++	--	#
     */
    public final static int Add_Sub = 100;
    public final static int Add = 101;
    public final static int Sub = 102;
    public final static int Div_Mul = 103;
    public final static int Div = 104;
    public final static int Mul = 105;
    public final static int AsS_R = 106;
//    public final static int AsS_Q = 107;
    public final static int AsS_F = 108;
    public final static int AsS_U = 109;
    public final static int Tran_LF = 110;
    public final static int EQ = 111;
    public final static int EQ_U = 112;
    public final static int Compare = 113;
    public final static int Comapre_OP = 114;
    public final static int If_FJ = 115;
    public final static int If_Backpatch_FJ = 116;
    public final static int If_RJ = 117;
    public final static int If_Backpatch_RJ = 118;
    public final static int While_FJ = 119;
    public final static int While_Backpatch_FJ = 120;
    public final static int For_FJ = 121;
    public final static int For_RJ = 122;
    public final static int For_Backpatch_FJ = 123;
    public final static int For = 124;
    public final static int Do = 125;
    public final static int Do_Backpatch_RJ = 126;
//    public final static int MUL = 127;
//    public final static int AND = 128;
//    public final static int OR = 129;
//    public final static int NON = 130;
//    public final static int DADD = 31;
//    public final static int DSUB = 32;
//    public final static int END = 33;

    /**
     * 产生式PRO:S,B,A,C,,X,R,Z,Z’,U,U’,E,E’,H,H’,G,M,D,L,L’,T,T’,F,O,P,Q
     */
}
