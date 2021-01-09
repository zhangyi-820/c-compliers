//package compilers;
//
//import utils.FourElement;
//import utils.Register;
//import utils.Variable;
//
//import java.util.*;
//
///**
// * @author :zhangyi
// * @description:汇编代码生成器（四元式->汇编代码）
// */
//public class Assembler {
//    List<FourElement> fourElementList;
//    List<String> assemblyCode;
//    Deque<Register> freeRegisters;
//    Deque<Register> busyRegisters;
//    String[] name={"rbx","rcx","rdx","rsi","rdi","rbp","r8","r9","r10","r11","r12","r13","r14","r15"};
//    Hashtable<String,Variable> variables;
//
//    public Assembler(List<FourElement> fourElementList){
//        this.fourElementList = fourElementList;
//        this.assemblyCode=new ArrayList<>();
//        this.busyRegisters=new ArrayDeque<>();
//        this.freeRegisters=new ArrayDeque<>();
//        variables=new Hashtable<>();
//
//        for(int i=1;i<=14;i++){
//            freeRegisters.addLast(new Register(name[i-1]));
//        }
//
//    }
//
//    public void generateAssemblyCode(){
//        int line=1;
//        for(FourElement fourElement: fourElementList){
//            this.assemblyCode.add(this.generateAssemblyCode(fourElement,line));
//            line++;
//        }
//
//        //将寄存器中的数据放回变量中
//        for(Register register:this.busyRegisters){
//            String str=storeRegister(register);
//            if(str==null)
//                continue;
//            this.assemblyCode.add(str);
//        }
//    }
//
//    public String generateAssemblyCode(FourElement element,int line){
//        StringBuffer buffer=new StringBuffer();
//        buffer.append("\t"+line+":");
//
//        //给每一个四元式分配寄存器
//        String temp=allocateRegister(element);
//        buffer.append(temp);
//
//        String reg1Name=element.arg1,reg2Name=element.arg2,tarName=element.result.toString();
//
//        if(variables.containsKey(element.arg1))
//            reg1Name=variables.get(element.arg1).register.name;
//        if(variables.containsKey(element.arg2))
//            reg2Name=variables.get(element.arg2).register.name;
//        if(variables.containsKey(element.result.toString()))
//            tarName=variables.get(element.result.toString()).register.name;
//
//
//        switch (element.op){
//            case "++":
//                buffer.append("\tinc "+tarName);
//                break;
//            case "==":
//                buffer.append("\tcmpq "+reg1Name+","+reg2Name);
//                break;
//            case "=":
//                buffer.append("\tmov "+reg1Name+","+element.arg1);
////                buffer.append("\tmov "+tarName+","+reg1Name);
//                break;
//            case "+":
//                buffer.append("\tmov "+tarName+","+reg1Name+"\n");
//                buffer.append("\tadd "+tarName+","+reg2Name);
//                break;
//            case "-":
//                buffer.append("\tmov "+tarName+","+reg1Name+"\n");
//                buffer.append("\tsub "+tarName+","+reg2Name);
//                break;
//            case "*":
//                buffer.append("\tmov "+tarName+","+reg1Name+"\n");
//                buffer.append("\tmul "+tarName+","+reg2Name);
//                break;
//            case "/":
//                buffer.append("\tmov "+tarName+","+reg1Name+"\n");
//                buffer.append("\tdiv "+tarName+","+reg2Name);
//                break;
//            case "RJ":
//                if(reg1Name.equals("/")&&reg2Name.equals("/")){
//                    buffer.append("\tjmp "+tarName);
//                }else{
//                    buffer.append("\tje "+tarName);
//                }
//                break;
//            case "FJ":
//                if(reg1Name.equals("/")&&reg2Name.equals("/")) {
//                    buffer.append("\tjmp "+tarName);
//                }else {
//                    buffer.append("\tjne " + tarName);
//                }
//                break;
//            case ">":
//                buffer.append("\tcmpq "+reg1Name+","+reg2Name+"\n");
//                buffer.append("\tsetg "+tarName);
//                break;
//            case "<":
//                buffer.append("\tcmpq "+reg1Name+","+reg2Name+"\n");
//                buffer.append("\tsetl "+tarName);
//                break;
//        }
//        return buffer.toString();
//    }
//
//    //暂时不考虑寄存器溢出
//    public String allocateRegister(FourElement element){
//        StringBuffer buffer=new StringBuffer();
//
//        //分配寄存器
//        String arg=element.arg1;
//        if(!arg.equals("/")&&!isNumber(arg)&&!variables.containsKey(arg)){
//            Variable variable1=new Variable(arg);
//            if(freeRegisters.size()==0){
//                Register temp=busyRegisters.pollFirst();
//                storeRegister(temp);
//            }
//            Register register=freeRegisters.pollFirst();
//            variable1.register=register;
//            variables.put(arg,variable1);
//            register.variable=variable1;
//            this.busyRegisters.addLast(register);
//        }
//
//        arg=element.arg2;
//        if(!arg.equals("/")&&!isNumber(arg)&&!variables.containsKey(arg)){
//            Variable variable1=new Variable(arg);
//            Register register=freeRegisters.pollFirst();
//            variable1.register=register;
//            register.variable=variable1;
//            variables.put(arg,variable1);
//            this.busyRegisters.addLast(register);
//        }
//
//        arg=element.result.toString();
//        if(!arg.equals("/")&&!isNumber(arg)&&!variables.containsKey(arg)){
//            Variable variable1=new Variable(arg);
//            Register register=freeRegisters.pollFirst();
//            variable1.register=register;
//            register.variable=variable1;
//            variables.put(arg,variable1);
//            this.busyRegisters.addLast(register);
//        }
//        return buffer.toString();
//    }
//
//    public boolean isNumber(String str){
//        int len=str.length();
//        for(int i=0;i<len;i++){
//            char c=str.charAt(i);
//            if(c<48||c>57){
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public String storeRegister(Register register){
//        String ans;
//        if (register.variable.name.contains("T"))
//            ans=null;
//        else
//            ans="\tsw "+register.name+","+register.variable.name;
//        freeRegisters.addLast(register);
//        register.variable.register=null;
//        register.variable=null;
//        busyRegisters.remove(register);
//        return ans;
//    }
//
//    public void outputAssemblyCode(){
//        for(String str:this.assemblyCode){
//            System.out.println(str);
//        }
//    }
//
//}
