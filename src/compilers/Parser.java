package compilers;

import utils.*;

import java.util.*;

public class Parser {
    LexAnalyse lexAnalyse;//词法分析器

    int errorCount = 0;//错误计数器
    boolean grammarErrorFlag = false;//语法分析错误标志
    int tempCount = 0;//临时变量计数器
    int fourElemCount = 0;//四元式数量计数器

    List<Word> wordList;//单词列表
    public List<FourElement> fourElementList = new ArrayList<>();//四元式列表
    List<CustomizeError> errorList = new ArrayList<>();//错误列表

    Stack<AnalyseNode> analyseNodeStack = new Stack<>();//语法分析结点栈
    Stack<String> semanticStack = new Stack<>();//语义栈

    AnalyseNode top = null;//当前栈顶元素
    Word firstWord = null;//当前分析词

    //四元式元素
    String arg1, arg2, res;
    String op;


    Stack<Integer> ifFJ = new Stack<>();
    Stack<Integer> ifRJ = new Stack<>();
    Stack<Integer> whileFJ = new Stack<>();
    Stack<Integer> forFJ = new Stack<>();
    Stack<Integer> forRJ = new Stack<>();
    Stack<Integer> forOP = new Stack<>();
    Stack<Integer> doOP = new Stack<>();

    HashMap<String, Variable> variables = new HashMap<>();//变量表

    public Parser(LexAnalyse lexAnalyse) {
        this.lexAnalyse = lexAnalyse;
        this.wordList = lexAnalyse.wordList;
    }

    public void grammarAnalyse() {
        analyseNodeStack.add(0, B);
        analyseNodeStack.add(1, end);
        semanticStack.add("#");

        boolean ifComapreCheck = false;
        while (!analyseNodeStack.empty() && !wordList.isEmpty()) {
            top = analyseNodeStack.get(0);
            firstWord = wordList.get(0);
            //正常结束
            if (firstWord.value.equals("#") && top.name.equals("#")) {
                analyseNodeStack.remove(0);
                wordList.remove(0);
            }
            //非正常结束
            else if (top.name.equals("#")) {
                analyseNodeStack.remove(0);
                grammarErrorFlag = true;
            }
            //if 没有对应点的else语句
            else if (!ifComapreCheck && top.name.equals("@If_Backpatch_FJ")
                    && !firstWord.value.equals("else")) {
                analyseNodeStack.remove(0);
                analyseNodeStack.add(0, If_Backpatch_NoElse);
                for (int i = 0; i < 6; i++) {//删除else结点
                    analyseNodeStack.remove(1);
                }
                ifComapreCheck = true;
            } else if (AnalyseNode.isTerm(top)) {
                processTerm(top.name);    //终结符处理
            } else if (AnalyseNode.isNonterm(top)) {
                processNonTerm(top.name); //非终结符处理
            } else if (top.type == AnalyseNode.ACTIONSIGN) {
                processActionSignOP(); //栈顶是动作符
            } else {
                System.out.println("语法分析未通过");
                return;
            }
        }
    }

    //处理终结符
    public void processTerm(String term) {
        //若当前分析的单词是整型、字符型、浮点型、bool型常量或者当前是标识符
        if (firstWord.type == Word.INT_CONST
                || firstWord.type == Word.CHAR_CONST
                || firstWord.type == Word.BOOL_CONST
                || firstWord.type == Word.FLOAT_CONST
                || term.equals(firstWord.value)
                || term.equals("id")
                || firstWord.type == Word.IDENTIFIER
        ) {
            analyseNodeStack.remove(0);
            wordList.remove(0);
        } else {
            errorCount++;
            errorList.add(new CustomizeError(errorCount, "语法错误", firstWord.line, firstWord));
            grammarErrorFlag = true;
            analyseNodeStack.remove(0);
            wordList.remove(0);
        }
    }

    //处理非终结符
    public void processNonTerm(String term) {
        char ch = term.charAt(0);
        analyseNodeStack.remove(0);
        String firstWordValue = firstWord.value;
        switch (ch) {
            case 'B':// B->{S}  first(B)={'{'}
                if (firstWord.value.equals("void")) {
                    analyseNodeStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "void", null));
                    analyseNodeStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "main", null));
                    analyseNodeStack.add(2, leftParenthese);
                    analyseNodeStack.add(3, rightParenthese);
                    analyseNodeStack.add(4, leftBrace);
                    analyseNodeStack.add(5, new AnalyseNode(AnalyseNode.NONTERMINAL, "S", null));
                    analyseNodeStack.add(6, rightBrace);
                } else {
                    errorCount++;
                    grammarErrorFlag = true;
                    errorList.add(new CustomizeError(errorCount, "错误的开始", firstWord.line, firstWord));
                }
                break;
            case 'S'://S->CA C->D|J|R       first(S)={'int','char','bool','float','if','while','for',id}
                if (firstWordValue.equals("int") || firstWordValue.equals("char") || firstWordValue.equals("bool") || firstWordValue.equals("float")
                        || firstWordValue.equals("if") || firstWordValue.equals("while") || firstWordValue.equals("for") || firstWordValue.equals("do")
                        || firstWord.type == Word.IDENTIFIER) {
                    analyseNodeStack.add(0, C);
                    analyseNodeStack.add(1, S);
                }
                break;
            case 'C'://C->D|J|R
                analyseNodeStack.add(0, D);
                analyseNodeStack.add(1, J);
                analyseNodeStack.add(2, R);
                break;
            case 'D'://D->TN;
                if (firstWordValue.equals("int") || firstWordValue.equals("char")
                        || firstWordValue.equals("bool") || firstWordValue.equals("float")) {
                    analyseNodeStack.add(0, T);
                    analyseNodeStack.add(1, N);
                    analyseNodeStack.add(2, semicolon);
                }
                break;
            case 'T'://T->int|char|bool|float
                if (firstWordValue.equals("int") || firstWordValue.equals("char")
                        || firstWordValue.equals("bool") || firstWordValue.equals("float")) {
                    T.value = firstWordValue;
                    analyseNodeStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, firstWordValue, null));
                } else {
                    errorCount++;
                    errorList.add(new CustomizeError(errorCount, "非法数据类型", firstWord.line, firstWord));
                    grammarErrorFlag = true;
                }
                break;
            case 'N'://N->Z A
                if (firstWord.type == Word.IDENTIFIER) {
                    analyseNodeStack.add(0, Z);
                    analyseNodeStack.add(1, A);
                }
                break;
            case 'Z'://Z->id U
                if (firstWord.type == Word.IDENTIFIER) {
                    analyseNodeStack.add(0, AsS_Z);
                    analyseNodeStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "id", null));
                    analyseNodeStack.add(2, U);
//                    analyseNodeStack.add(3,EQ_U);
                }
                break;
            case 'A'://A->,Z
                if (firstWordValue.equals(",")) {
                    analyseNodeStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, ",", null));
                    analyseNodeStack.add(1, Z);
                }
                break;
            case 'U'://U->=L
                if (firstWordValue.equals("=")) {
                    analyseNodeStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "=", null));
                    analyseNodeStack.add(1, L);
                    analyseNodeStack.add(2, EQ_U);
                }
                break;
            case 'L'://L->XM
                if (firstWordValue.equals("(") || firstWord.type == Word.IDENTIFIER
                        || firstWord.type == Word.INT_CONST || firstWord.type == Word.FLOAT_CONST
                        || firstWord.type == Word.BOOL_CONST) {
                    analyseNodeStack.add(0, X);
                    analyseNodeStack.add(1, M);
                    analyseNodeStack.add(2, Add_Sub);
                }
                break;
            case 'M'://M->+L|-L
                if (firstWordValue.equals("+") || firstWordValue.equals("-")) {
                    analyseNodeStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, firstWordValue, null));
                    analyseNodeStack.add(1, L);
                    analyseNodeStack.add(2, firstWordValue.equals("+") ? Add : Sub);
                }
                break;
            case 'X'://X->FY
                if (firstWord.type == Word.IDENTIFIER || firstWord.type == Word.INT_CONST
                        || firstWord.type == Word.FLOAT_CONST || firstWord.type == Word.BOOL_CONST
                        || firstWordValue.equals("(")) {
                    analyseNodeStack.add(0, F);
                    analyseNodeStack.add(1, Y);
                    analyseNodeStack.add(2, Div_Mul);
                } else {
                    errorCount++;
                    wordList.remove(0);
                    errorList.add(new CustomizeError(errorCount, "不能进行算术运算的数据类型", firstWord.line, firstWord));
                    grammarErrorFlag = true;
                }
                break;
            case 'Y'://Y->*X | /X
                if (firstWordValue.equals("*") || firstWordValue.equals("/")) {
                    analyseNodeStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, firstWordValue, null));
                    analyseNodeStack.add(1, X);
                    analyseNodeStack.add(2, firstWordValue.equals("*") ? Mul : Div);
                }
                break;
            case 'F'://F->(L)|id|num|float|bool
                if (firstWord.type == Word.IDENTIFIER) {
                    if (variables.get(firstWordValue) == null) {
                        errorCount++;
                        errorList.add(new CustomizeError(errorCount, "未声明的变量", firstWord.line, firstWord));
                        grammarErrorFlag = true;
                    } else {
                        analyseNodeStack.add(0, AsS_F);
                        analyseNodeStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "id", null));
                    }
                } else if (firstWord.type == Word.INT_CONST) {
                    analyseNodeStack.add(0, AsS_F);
                    analyseNodeStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "num", null));
                } else if (firstWord.type == Word.FLOAT_CONST) {
                    analyseNodeStack.add(0, AsS_F);
                    analyseNodeStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "float", null));
                } else if (firstWord.type == Word.BOOL_CONST) {
                    analyseNodeStack.add(0, AsS_F);
                    analyseNodeStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "bool", null));
                } else {
                    analyseNodeStack.add(0, leftParenthese);
                    analyseNodeStack.add(1, L);
                    analyseNodeStack.add(2, rightParenthese);
                    analyseNodeStack.add(3, Tran_LF);
                }
                break;
            case 'E'://E->HP
                if (firstWord.type == Word.INT_CONST || firstWord.type == Word.FLOAT_CONST
                        || firstWord.type == Word.IDENTIFIER || firstWordValue.equals("(") || firstWord.type == Word.BOOL_CONST || firstWord.equals("!")) {
                    analyseNodeStack.add(0, H);
                    analyseNodeStack.add(1, P);
                } else {
                    errorCount++;
                    errorList.add(new CustomizeError(errorCount, "不能进行算术运算的数据类型", firstWord.line, firstWord));
                    grammarErrorFlag = true;
                }
                break;
            case 'H'://H->GI
                if (firstWord.type == Word.INT_CONST || firstWord.type == Word.FLOAT_CONST
                        || firstWord.type == Word.IDENTIFIER || firstWordValue.equals("(") || firstWord.type == Word.BOOL_CONST) {
                    analyseNodeStack.add(0, G);
                    analyseNodeStack.add(1, I);
                } else {
                    errorCount++;
                    errorList.add(new CustomizeError(errorCount, "不能进行算术运算的数据类型", firstWord.line, firstWord));
                    grammarErrorFlag = true;
                }
                break;
            case 'P'://P->&&E
                if (firstWordValue.equals("&&")) {
                    analyseNodeStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "&&", null));
                    analyseNodeStack.add(1, E);
                    analyseNodeStack.add(2, And);
                }
                break;
            case 'G'://G->FKF | (E) | !E|true|false
                if (firstWord.type == Word.BOOL_CONST) {
                    if (firstWordValue.equals("true")) {
                        analyseNodeStack.add(0, RJ);
                    } else {
                        analyseNodeStack.add(0, FJ);
                    }
                } else if (firstWord.type == Word.IDENTIFIER || firstWord.type == Word.INT_CONST || firstWord.type == Word.FLOAT_CONST) {
                    analyseNodeStack.add(0, F);
                    analyseNodeStack.add(1, K);
                    analyseNodeStack.add(2, F);
                    analyseNodeStack.add(3, Compare);
                } else if (firstWordValue.equals("(")) {
                    analyseNodeStack.add(0, leftParenthese);
                    analyseNodeStack.add(1, E);
                    analyseNodeStack.add(2, rightParenthese);
                } else if (firstWordValue.equals("!")) {
                    analyseNodeStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "!", null));
                    analyseNodeStack.add(1, E);
//                    analyseNodeStack.add(2,)
                } else {
                    errorCount++;
                    errorList.add(new CustomizeError(errorCount, "不能进行算术运算的数据类型", firstWord.line, firstWord));
                    grammarErrorFlag = true;
                }
                break;
            case 'I'://I->||E
                if (firstWordValue.equals("||")) {
                    analyseNodeStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "||", null));
                    analyseNodeStack.add(1, E);
                    analyseNodeStack.add(2, Or);
                }
                break;
            case 'K'://K->==|!=|>|<
                if (firstWordValue.equals("==") || firstWordValue.equals("!=")
                        || firstWordValue.equals(">") || firstWordValue.equals("<")
                        || firstWordValue.equals("<=") || firstWordValue.equals(">=")) {
                    analyseNodeStack.add(0, Compare_OP);
                    analyseNodeStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, firstWordValue, null));
                } else {
                    errorCount++;
                    errorList.add(new CustomizeError(errorCount, "非法运算符", firstWord.line, firstWord));
                    grammarErrorFlag = true;
                }
                break;
            case 'J':
                if (firstWordValue.equals("if")) {
                    analyseNodeStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "if", null));
                    analyseNodeStack.add(1, leftParenthese);
                    analyseNodeStack.add(2, E);
                    analyseNodeStack.add(3, rightParenthese);
                    analyseNodeStack.add(4, If_FJ);
                    analyseNodeStack.add(5, leftBrace);
                    analyseNodeStack.add(6, S);
                    analyseNodeStack.add(7, rightBrace);
                    analyseNodeStack.add(8, If_Backpatch_FJ);
                    analyseNodeStack.add(9, If_RJ);
                    analyseNodeStack.add(10, new AnalyseNode(AnalyseNode.TERMINAL, "else", null));
                    analyseNodeStack.add(11, leftBrace);
                    analyseNodeStack.add(12, S);
                    analyseNodeStack.add(13, rightBrace);
                    analyseNodeStack.add(14, If_Backpatch_RJ);
                } else if (firstWordValue.equals("while")) {
                    analyseNodeStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "while", null));
                    analyseNodeStack.add(1, leftParenthese);
                    analyseNodeStack.add(2, E);
                    analyseNodeStack.add(3, rightParenthese);
                    analyseNodeStack.add(4, While_FJ);
                    analyseNodeStack.add(5, leftBrace);
                    analyseNodeStack.add(6, S);
                    analyseNodeStack.add(7, rightBrace);
                    analyseNodeStack.add(8, While_RJ);
                    analyseNodeStack.add(9, While_Backpatch_FJ);
                } else if (firstWordValue.equals("for")) {
                    analyseNodeStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "for", null));
                    analyseNodeStack.add(1, leftParenthese);
                    analyseNodeStack.add(2, T);
                    analyseNodeStack.add(3, N);
                    analyseNodeStack.add(4, semicolon);
                    analyseNodeStack.add(5, E);
                    analyseNodeStack.add(6, For_FJ);
                    analyseNodeStack.add(7, semicolon);
                    analyseNodeStack.add(8, W);
                    analyseNodeStack.add(9, For);
                    analyseNodeStack.add(10, rightParenthese);
                    analyseNodeStack.add(11, leftBrace);
                    analyseNodeStack.add(12, S);
                    analyseNodeStack.add(13, rightBrace);
                    analyseNodeStack.add(14, For_RJ);
                    analyseNodeStack.add(15, For_Backpatch_FJ);
                } else if (firstWordValue.equals("do")) {
                    analyseNodeStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "do", null));
                    analyseNodeStack.add(1, leftBrace);
                    analyseNodeStack.add(2, Do);
                    analyseNodeStack.add(3, S);
                    analyseNodeStack.add(4, rightBrace);
                    analyseNodeStack.add(5, new AnalyseNode(AnalyseNode.TERMINAL, "while", null));
                    analyseNodeStack.add(6, leftParenthese);
                    analyseNodeStack.add(7, E);
                    analyseNodeStack.add(8, Do_Backpatch_RJ);
                    analyseNodeStack.add(9, rightParenthese);
                    analyseNodeStack.add(10, semicolon);
                }
                break;
            case 'R':
                if (firstWord.type == Word.IDENTIFIER) {
                    analyseNodeStack.add(0, AsS_R);
                    analyseNodeStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "id", null));
                    analyseNodeStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL, "=", null));
                    analyseNodeStack.add(3, L);
                    analyseNodeStack.add(4, EQ);
                    analyseNodeStack.add(5, new AnalyseNode(AnalyseNode.TERMINAL, ";", null));
                }
                break;
            case 'W':
                if (firstWord.type == Word.IDENTIFIER) {
                    analyseNodeStack.add(0, AsS_R);
                    analyseNodeStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "id", null));
                    analyseNodeStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL, "=", null));
                    analyseNodeStack.add(3, L);
                    analyseNodeStack.add(4, EQ);
                }
                break;
        }
    }

    //处理动作符
    public void processActionSignOP() {
        String topName = top.name;
        switch (topName) {
            case "@Add_Sub":
                if (op != null && (op.equals("+") || op.equals("-"))) {
                    arg2 = semanticStack.pop();
                    arg1 = semanticStack.pop();
                    res = newTemp();

                    fourElemCount++;
                    fourElementList.add(new FourElement(fourElemCount, op, arg1, arg2, res));

                    L.value = res;
                    semanticStack.push(L.value);
                    op = null;
                }
                break;
            case "@Add":
                op = "+";
                break;
            case "@Sub":
                op = "-";
                break;
            case "@Div_Mul":
                if (op != null && (op.equals("*") || op.equals("/"))) {
                    arg2 = semanticStack.pop();
                    arg1 = semanticStack.pop();
                    res = newTemp();

                    fourElemCount++;
                    fourElementList.add(new FourElement(fourElemCount, op, arg1, arg2, res));

                    X.value = res;
                    semanticStack.push(X.value);
                    op = null;
                }
                break;
            case "@Div":
                op = "/";
                break;
            case "@Mul":
                op = "*";
                break;
            case "@Tran_LF":
                F.value = L.value;
                break;
            case "@AsS_F":
                F.value = firstWord.value;
                semanticStack.push(F.value);
                break;
            case "@AsS_R":
                R.value = firstWord.value;
                semanticStack.push(R.value);
                break;
            case "@AsS_Z":
                if (variables.get(firstWord.value) != null) {
                    errorCount++;
                    errorList.add(new CustomizeError(errorCount, "变量已声明", firstWord.line, firstWord));
                    grammarErrorFlag = true;
                }
                switch (T.value) {
                    case "int":
                        variables.put(firstWord.value, new Variable(firstWord.value, Variable.INT));
                        break;
                    case "float":
                        variables.put(firstWord.value, new Variable(firstWord.value, Variable.FLOAT));
                        break;
                    case "bool":
                        variables.put(firstWord.value, new Variable(firstWord.value, Variable.BOOL));
                        break;
                    case "char":
                        variables.put(firstWord.value, new Variable(firstWord.value, Variable.CHAR));
                        break;
                }
                Z.value = firstWord.value;
                semanticStack.push(Z.value);

                break;
            case "@EQ":
            case "@EQ_U":
                op = "=";
                arg1 = semanticStack.pop();
                res = semanticStack.pop();

                fourElemCount++;
                fourElementList.add(new FourElement(fourElemCount, op, arg1, "/", res));
                op = null;
                break;
            case "@Compare":
                arg2 = semanticStack.pop();
                op = semanticStack.pop();
                arg1 = semanticStack.pop();
                res = newTemp();

                fourElemCount++;
                fourElementList.add(new FourElement(fourElemCount, op, arg1, arg2, res));
                G.value = res;
                semanticStack.push(G.value);
                op = null;
                break;
            case "@Compare_OP":
                K.value = firstWord.value;
                semanticStack.push(K.value);
                break;
            case "@If_FJ":
                op = "FJ";
                arg1 = semanticStack.pop();
                fourElemCount++;
                ifFJ.push(fourElemCount);
                fourElementList.add(new FourElement(fourElemCount, op, arg1, "/", res));
                op = null;
                break;
            case "@If_Backpatch_FJ":
                backPatch(ifFJ.pop(), fourElemCount + 2);
                break;
            case "@If_Backpatch_NoElse":
                backPatch(ifFJ.pop(), fourElemCount + 1);
                break;
            case "@If_RJ":
                op = "RJ";
                fourElemCount++;
                ifRJ.push(fourElemCount);
                fourElementList.add(new FourElement(fourElemCount, op, "/", "/", "/"));
                op = null;
                break;
            case "@If_Backpatch_RJ":
                backPatch(ifRJ.pop(), fourElemCount + 1);
                break;
            case "@While_RJ":
                op = "RJ";
                res = String.valueOf(whileFJ.peek() - 1);
                fourElemCount++;
                forRJ.push(fourElemCount);
                fourElementList.add(new FourElement(fourElemCount, op, "/", "/", res));
                op = null;
                break;
            case "@While_FJ":
                op = "FJ";
                arg1 = semanticStack.pop();
                fourElemCount++;
                whileFJ.push(fourElemCount);
                fourElementList.add(new FourElement(fourElemCount, op, arg1, "/", res));
                op = null;
                break;
            case "@While_Backpatch_FJ":
                backPatch(whileFJ.pop(), fourElemCount + 1);
                break;
            case "@For":
                forOP.push(fourElemCount);
                break;
            case "@For_RJ":
                forBackPatch(forOP.pop(), forOP.pop(), fourElemCount);
                op = "RJ";
                res = String.valueOf(forFJ.peek() - 1);
                fourElemCount++;
                fourElementList.add(new FourElement(fourElemCount, op, "/", "/", res));
                forRJ.push(fourElemCount);
                op = null;
                break;
            case "@For_FJ":
                op = "FJ";
                arg1 = semanticStack.pop();
                fourElemCount++;
                fourElementList.add(new FourElement(fourElemCount, op, arg1, "/", "/"));
                forFJ.push(fourElemCount);
                forOP.push(fourElemCount);//将for循环中的赋值语句存入栈中
                op = null;
                break;
            case "@For_Backpatch_FJ":
                backPatch(forFJ.pop(), fourElemCount + 1);
                break;
            case "@Do":
                doOP.push(fourElemCount);
                break;
            case "@Do_Backpatch_RJ":
                op = "RJ";
                res = String.valueOf(doOP.pop());
                arg1 = semanticStack.pop();
                fourElemCount++;
                fourElementList.add(new FourElement(fourElemCount, op, arg1, "/", res));
                op = null;
                break;
            case "@Or":
                op = "||";
                res = newTemp();
                arg1 = semanticStack.pop();
                arg2 = semanticStack.pop();

                fourElemCount++;
                fourElementList.add(new FourElement(fourElemCount, op, arg1, arg2, res));
                semanticStack.push(res);
                op = null;
                break;
            case "@And":
                op = "&&";
                res = newTemp();
                arg1 = semanticStack.pop();
                arg2 = semanticStack.pop();

                fourElemCount++;
                fourElementList.add(new FourElement(fourElemCount, op, arg1, arg2, res));
                semanticStack.push(res);
                op = null;
                break;
            case "@True":
                op = "=";
                arg1 = "true";
                res = newTemp();

                fourElemCount++;
                fourElementList.add(new FourElement(fourElemCount, op, arg1, "/", res));
                G.value = res;
                semanticStack.push(G.value);
                wordList.remove(0);
                op = null;
                break;
            case "@False":
                op = "=";
                arg1 = "flase";
                res = newTemp();

                fourElemCount++;
                fourElementList.add(new FourElement(fourElemCount, op, arg1, "/", res));
                G.value = res;
                semanticStack.push(G.value);
                wordList.remove(0);
                op = null;
                break;
        }
        analyseNodeStack.remove(0);
    }

    //生成新的临时变量
    public String newTemp() {
        tempCount++;
        return "T" + tempCount;
    }

    //地址回填
    private void backPatch(int i, int res) {
        if (res != -1) {
            FourElement temp = fourElementList.get(i - 1);
            temp.result = res + "";
            fourElementList.set(i - 1, temp);
        } else {
            FourElement temp = fourElementList.get(i - 1);
            temp.op = "nop";
            fourElementList.set(i - 1, temp);
        }
    }

    //for循环的地址回填
    private void forBackPatch(int mid, int begin, int end) {
        List<FourElement> fourElementsTemp = new ArrayList<>();
        for (int i = begin; i < mid; i++) {
            fourElementsTemp.add(fourElementList.get(i));
        }
        int j = begin;
        for (int k = mid; k < end; k++) {
            fourElementList.set(j, fourElementList.get(k));
            j++;
        }
        for (int k = j; k < end; k++) {
            fourElementList.set(k, fourElementsTemp.get(k - j));
        }
    }

    //输出错误
    public void outputError() {
        for (CustomizeError error : errorList) {
            System.out.println(error.toString());
        }
    }

    //输出四元式
    public void outputFourElement() {
        int i = 1;
        for (FourElement element : this.fourElementList) {
            System.out.println(i + ":" + element.toString());
            i++;
        }
    }

    //输入变量表
    public void outputVariables() {
        int i = 1;
        Iterator<Variable> variableIterator = variables.values().iterator();
        while (variableIterator.hasNext()) {
            System.out.println(i + ":" + variableIterator.next());
            i++;
        }
    }

    //非终结语法结点
    AnalyseNode B = new AnalyseNode(AnalyseNode.NONTERMINAL, "B", null),
            S = new AnalyseNode(AnalyseNode.NONTERMINAL, "S", null),
            C = new AnalyseNode(AnalyseNode.NONTERMINAL, "C", null),
            D = new AnalyseNode(AnalyseNode.NONTERMINAL, "D", null),
            J = new AnalyseNode(AnalyseNode.NONTERMINAL, "J", null),
            R = new AnalyseNode(AnalyseNode.NONTERMINAL, "R", null),
            T = new AnalyseNode(AnalyseNode.NONTERMINAL, "T", null),
            N = new AnalyseNode(AnalyseNode.NONTERMINAL, "N", null),
            U = new AnalyseNode(AnalyseNode.NONTERMINAL, "U", null),
            L = new AnalyseNode(AnalyseNode.NONTERMINAL, "L", null),
            E = new AnalyseNode(AnalyseNode.NONTERMINAL, "E", null),
            H = new AnalyseNode(AnalyseNode.NONTERMINAL, "H", null),
            P = new AnalyseNode(AnalyseNode.NONTERMINAL, "P", null),
            G = new AnalyseNode(AnalyseNode.NONTERMINAL, "G", null),
            I = new AnalyseNode(AnalyseNode.NONTERMINAL, "I", null),
            F = new AnalyseNode(AnalyseNode.NONTERMINAL, "F", null),
            K = new AnalyseNode(AnalyseNode.NONTERMINAL, "K", null),
            M = new AnalyseNode(AnalyseNode.NONTERMINAL, "M", null),
            X = new AnalyseNode(AnalyseNode.NONTERMINAL, "X", null),
            W = new AnalyseNode(AnalyseNode.NONTERMINAL, "W", null),
            Y = new AnalyseNode(AnalyseNode.NONTERMINAL, "Y", null),
            Z = new AnalyseNode(AnalyseNode.NONTERMINAL, "Z", null),
            A = new AnalyseNode(AnalyseNode.NONTERMINAL, "A", null),
            end = new AnalyseNode(AnalyseNode.END, "#", null),
            leftParenthese = new AnalyseNode(AnalyseNode.TERMINAL, "(", null),//左括号
            rightParenthese = new AnalyseNode(AnalyseNode.TERMINAL, ")", null),//右括号
            leftBrace = new AnalyseNode(AnalyseNode.TERMINAL, "{", null),//左大括号
            rightBrace = new AnalyseNode(AnalyseNode.TERMINAL, "}", null),//右大括号
            semicolon = new AnalyseNode(AnalyseNode.TERMINAL, ";", null);//分号

    //终结语法结点
    AnalyseNode Add_Sub = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@Add_Sub", null),//生成四元式（op不为空，Res=newTemp(),L.val=Res,[OP,X.val,M.val,Res]）
            Add = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@Add", null),//op=+,arg2=L.val
            Sub = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@Sub", null),//op=-,arg2=L.val
            Div_Mul = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@Div_Mul", null),//if op!=null,res=newTemp,X.val=Res,[OP,F.val,arg2,Res]
            Div = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@Div", null),//op=*,arg2=X.val
            Mul = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@Mul", null),//op=/,arg2=X.val
            AsS_F = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@AsS_F", null),//F.val=num|id
            AsS_R = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@AsS_R", null),//R.val=id,压入语义栈
            AsS_Z = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@AsS_Z", null),//将值归约到N
            Tran_LF = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@TRAN_LF", null),
            EQ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@EQ", null),
            EQ_U = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@EQ_U", null),
            Compare = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@Compare", null),//op=D.val,arg1=F(1),arg2=F(2);Res=newTemp(),[op,F.val,arg2,Res];G.val=Res并进语义栈
            Compare_OP = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@Compare_OP", null),//运算符加入语义栈
            If_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@If_FJ", null),
            If_RJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@If_RJ", null),
            If_Backpatch_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@If_Backpatch_FJ", null),
            If_Backpatch_RJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@If_Backpatch_RJ", null),
            If_Backpatch_NoElse = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@If_Backpatch_NoElse", null),
            While_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@While_FJ", null),
            While_RJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@While_RJ", null),
            While_Backpatch_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@While_Backpatch_FJ", null),
            For_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@For_FJ", null),
            For_RJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@For_RJ", null),
            For_Backpatch_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@For_Backpatch_FJ", null),
            For = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@For", null),
            Do = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@Do", null),
            Do_Backpatch_RJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@Do_Backpatch_RJ", null),
            Or = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@Or", null),
            And = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@And", null),
            RJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@True", null),
            FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@False", null);
}