package utils;

/**
 * @author :zhangyi
 * @description:四元式
 */
public class FourElement {
    public int id;//四元式编号
    public String op;//操作符
    public String arg1;//第一个操作数
    public String arg2;//第二个操作数
    public Object result;//结果

    public FourElement(){

    }

    public FourElement(int id,String op,String arg1,String arg2,Object result){
        this.id=id;
        this.op=op;
        this.arg1=arg1;
        this.arg2=arg2;
        this.result=result;
    }

    public String toString(){
        return "("+this.op+","+this.arg1+","+this.arg2+","+this.result+")";
    }
}
