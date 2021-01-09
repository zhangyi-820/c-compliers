package utils;

/**
 * @author :zhangyi
 * @description:错误
 */
public class CustomizeError {
    int id;//错误序号；
    String info;//错误信息；
    int line;//错误所在行
    Word word;//错误的单词

    public CustomizeError() {
    }

    public CustomizeError(int id, String info, int line, Word word) {
        this.id = id;
        this.info = info;
        this.line = line;
        this.word = word;
    }
    public String toString(){
        return this.id+"\t"+this.info+"\t"+this.line+"\t"+this.word.value;
    }

}
