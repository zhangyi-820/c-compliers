package utils;

import java.util.Objects;

/**
 * @author :zhangyi
 * @description:
 * @date :2020/12/30 14:51
 */
public class Variable {
    public static final int INT=0,CHAR=1,FLOAT=2,BOOL=3;
    public String name;
    public Register register;
    public int type;
    public Object value;

    public Variable(String name,int type){
        this.name=name;
        this.type=type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Variable variable = (Variable) o;
        return Objects.equals(name, variable.name) &&
                Objects.equals(register, variable.register) &&
                Objects.equals(value, variable.value);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        String[] strings={"int","char","float","bool"};
       return "\t"+name+"\t"+strings[type];
    }
}
