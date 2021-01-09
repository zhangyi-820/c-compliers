package compilers;

import utils.CustomizeError;
import utils.Word;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author :zhangyi
 * @description:词法分析器
 */
public class LexAnalyse {
    List<Word> wordList=new ArrayList<>();
    List<CustomizeError> customizeErrorList =new ArrayList<>();

    int wordCount=0;
    int errorCount=0;
    boolean noteTag=false;
    public boolean lexErrorTag=false;

    public LexAnalyse(){

    }

    //读取文件
    public List<String> readFile() {
        try {
            BufferedReader reader=new BufferedReader(new FileReader("test-files\\test.c"));
            List<String> ans=new ArrayList<>();
            String temp=reader.readLine();
            while (temp!=null){
                System.out.println(temp);
                ans.add(temp);
                temp=reader.readLine();
            }
            return ans;
        }catch (IOException ex){
            ex.printStackTrace();
            return null;
        }
    }

    private static boolean isID(String word) {
        boolean flag = false;
        int i = 0;
        if (Word.isKey(word))
            return flag;
        char temp = word.charAt(i);
        if (Character.isLetter(temp) || temp == '_') {
            for (i = 1; i < word.length(); i++) {
                temp = word.charAt(i);
                if (Character.isLetter(temp) || temp == '_' || Character.isDigit(temp))
                    continue;
                else
                    break;
            }
            if (i >= word.length())
                flag = true;
        } else
            return flag;

        return flag;
    }

    //分行读取输入，生成单词
    //当存在多行注释符号时返回true
    public boolean analyse(String str, int line){
        int index=0;
        int length=str.length();
        while (index<length){
            char ch=str.charAt(index);
            //是空字符时跳过
            if(ch==' '||ch=='\t'||ch=='\r'){
                index++;
                continue;
            }
            //出现单行注释
            if(ch=='/'&&index+1<length&&str.charAt(index+1)=='/'){
                return false;
            }
            if(ch=='/'&&index+1<length&&str.charAt(index+1)=='*')
                return true;
            Word word;
            boolean tag=false;
            //运算符
            switch (ch){
                case '+':
                    tag=true;
                    if(index+1<length&&str.charAt(index+1)=='+'){
                        wordList.add(new Word(++wordCount,"=",Word.OPERATOR,line));
                        wordList.add(new Word(++wordCount,wordList.get(wordCount-3).value,Word.IDENTIFIER,line));
                        wordList.add(new Word(++wordCount,"+",Word.OPERATOR,line));
                        wordList.add(new Word(++wordCount,"1",Word.INT_CONST,line));
                        index=index+2;
                    }else {
                        wordList.add(new Word(++wordCount,str.substring(index,index+1),Word.OPERATOR,line));
                        index++;
                    }
                    break;
                case '-':
                    tag=true;
                    if(index+1<length&&str.charAt(index+1)=='-'){
                        wordList.add(new Word(++wordCount,"=",Word.OPERATOR,line));
                        wordList.add(new Word(++wordCount,wordList.get(wordCount-3).value,Word.IDENTIFIER,line));
                        wordList.add(new Word(++wordCount,"-",Word.OPERATOR,line));
                        wordList.add(new Word(++wordCount,"1",Word.INT_CONST,line));
                        index=index+2;
                    }else {
                        wordCount++;
                        word=new Word(wordCount,str.substring(index,index+1),Word.OPERATOR,line);
                        index++;
                        wordList.add(word);
                    }
                    break;
                case '*':
                case '/':
                    tag=true;
                    wordCount++;
                    word=new Word(wordCount,str.substring(index,index+1),Word.OPERATOR,line);
                    this.wordList.add(word);
                    index++;
                    break;
                case '&':
                    tag=true;
                    wordCount++;
                    if(index+1<length&&str.charAt(index+1)=='&'){
                        word=new Word(wordCount,str.substring(index,index+2),Word.OPERATOR,line);
                        index=index+2;
                    }else{
                        word=new Word(wordCount,str.substring(index,index+1),Word.OPERATOR,line);
                        index++;
                    }
                    wordList.add(word);
                    break;
                case '|':
                    tag=true;
                    wordCount++;
                    if(index+1<length&&str.charAt(index+1)=='|'){
                        word=new Word(wordCount,str.substring(index,index+2),Word.OPERATOR,line);
                        index=index+2;
                    }else{
                        word=new Word(wordCount,str.substring(index,index+1),Word.OPERATOR,line);
                        index++;
                    }
                    wordList.add(word);
                    break;
                case '=':
                case '<':
                case '>':
                case '!':
                    tag=true;
                    wordCount++;
                    if(index+1<length&&str.charAt(index+1)=='='){
                        word=new Word(wordCount,str.substring(index,index+2),Word.OPERATOR,line);
                        index=index+2;
                    }else{
                        word=new Word(wordCount,str.substring(index,index+1),Word.OPERATOR,line);
                        index++;
                    }
                    wordList.add(word);
                    break;
            }
            if(tag)
                continue;
            //数字
            if(Character.isDigit(ch)){
                int indexTemp=index;
                while (indexTemp<length&&(Character.isDigit(str.charAt(indexTemp)))){
                    indexTemp++;
                }
                if(indexTemp==length||str.charAt(indexTemp)!='.'){//输入的为整数类型
                    wordCount++;
                    word=new Word(wordCount,str.substring(index,indexTemp),Word.INT_CONST,line);
                }
                else{//输入的为浮点数
                    indexTemp++;
                    while (indexTemp<length&&(Character.isDigit(str.charAt(indexTemp)))){
                        indexTemp++;
                    }
                    wordCount++;
                    word=new Word(wordCount,str.substring(index,indexTemp),Word.FLOAT_CONST,line);
                }
                index=indexTemp;
                wordList.add(word);
            }
            //字母
            else if(Character.isLetter(ch)){
                int indexTemp = index;
                indexTemp++;
                while ((indexTemp < length)
                        && (!Word.isBoundarySign(String.valueOf(str.charAt(indexTemp))))
                        && (!Word.isOperator(String.valueOf(str.charAt(indexTemp))))
                        && (str.charAt(indexTemp) != ' ')
                        && (str.charAt(indexTemp) != '\t')
                        && (str.charAt(indexTemp) != '\r')
                        && (str.charAt(indexTemp) != '\n')) {
                    indexTemp++;
                }
                wordCount++;
                String temp=str.substring(index,indexTemp);
                if(temp.equals("true")){
                    word=new Word(wordCount,"true",Word.BOOL_CONST,line);
                }else if(temp.equals("false")){
                    word=new Word(wordCount,"false",Word.BOOL_CONST,line);
                }else {
                    word = new Word(wordCount, str.substring(index, indexTemp), 0, line);
                    if (Word.isKey(word.value)) {
                        word.type = Word.KEY;
                    } else if (isID(word.value)) {
                        word.type = Word.IDENTIFIER;
                    } else {
                        word.type = Word.UNIDEF;
                        word.flag = false;
                        errorCount++;
                        customizeErrorList.add(new CustomizeError(errorCount, "非法标识符", word.line, word));
                        lexErrorTag = true;
                    }
                }
                index = indexTemp;
                wordList.add(word);
            }
            // 不是标识符、数字常量、字符串常量
            else {
                ch=str.charAt(index);
                switch (ch) {
                    case '[':
                    case ']':
                    case '(':
                    case ')':
                    case '{':
                    case '}':
                    case ',':
                    case '"':
                    case '.':
                    case ';':
                    case '%':
                    case '>':
                    case '<':
                    case '?':
                    case '#':
                        wordCount++;
                        word = new Word(wordCount,String.valueOf(ch),0,line);
                        if (Word.isOperator(word.value))
                            word.type = Word.OPERATOR;
                        else if (Word.isBoundarySign(word.value))
                            word.type = Word.BOUNDARYSIGN;
                        else
                            word.type = Word.END;
                        break;
                    default:
                        wordCount++;
                        word = new Word(wordCount,String.valueOf(ch),Word.UNIDEF,line);
                        word.flag = false;
                        errorCount++;
                        customizeErrorList.add(new CustomizeError(errorCount, "非法标识符", word.line, word));
                        lexErrorTag = true;
                }
                index++;
                wordList.add(word);
            }
        }
        return false;
    }

    //词法分析
    public void analyseWords(List<String> strings){
        int i=0;
        for(;i<strings.size();i++){
            if(analyse(strings.get(i),i+1)){
                i++;
                //删除多行注释
                String temp=deleteMultiLineComment(strings.get(i));
                while (temp==null){
                    i++;
                    temp=deleteMultiLineComment(strings.get(i));
                }
                analyse(temp,i+1);
            }
        }

        //增加结束符号
        if (wordList.get(wordList.size() - 1).type!=Word.END) {
            wordCount++;
            wordList.add(new Word(wordCount, "#", Word.END, i+2));
        }
    }

    //删除多行注释符号
    public String deleteMultiLineComment(String str){
        int length=str.length();
        for(int i=0;i<length-1;i++) {
            if(str.charAt(i)=='*'&&str.charAt(i+1)=='/'){
                return str.substring(i+2);
            }
        }
        return null;
    }

    //输入单词列表
    public void generateWordList(){
        List<String> strings=this.readFile();
        this.analyseWords(strings);
    }

    //输出单词列表
    public void outputWordsList(){
        for(Word word:this.wordList){
            System.out.println(word.toString());
        }
    }


    public static void main(String[] args){
        LexAnalyse lex=new LexAnalyse();
        List<String> strings=lex.readFile();
        lex.analyseWords(strings);
        for(Word word:lex.wordList){
            System.out.println(word.toString());
        }

        for(CustomizeError customizeError :lex.customizeErrorList){
            System.out.println(customizeError.toString());
        }
    }
}
