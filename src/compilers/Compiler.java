package compilers;

/**
 * @author :zhangyi
 * @description:编译器主体
 */
public class Compiler {
    public static void main(String[] args){
        LexAnalyse lex=new LexAnalyse();
        lex.generateWordList();
        if(lex.lexErrorTag){
            System.out.println("语法分析未通过");
            return;
        }
        lex.outputWordsList();


        Parser parser=new Parser(lex);
        parser.grammarAnalyse();
        if(parser.grammarErrorFlag){
            System.out.println("语义分析未通过");
            parser.outputError();
            return;
        }

        parser.outputFourElement();

        System.out.println();
        parser.outputVariables();


    }
}
